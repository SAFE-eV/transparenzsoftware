package com.hastobe.transparenzsoftware.output;

import com.hastobe.transparenzsoftware.gui.views.helper.ValueIndexHolder;
import com.hastobe.transparenzsoftware.gui.views.helper.ValueMapBuilder;
import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.*;
import com.hastobe.transparenzsoftware.verification.input.InputReader;
import com.hastobe.transparenzsoftware.verification.input.InvalidInputException;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import com.hastobe.transparenzsoftware.verification.xml.Value;
import com.hastobe.transparenzsoftware.verification.xml.Values;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * File processor of an input xml to create the xml result
 * for cli part of the "Transparenzsoftware"
 */
public class ConsoleFileProcessor {

    private final static Logger LOGGER = LogManager.getLogger(ConsoleFileProcessor.class);
    private final Verifier verifier;
    private final VerificationParserFactory factory;

    /**
     * Initiates the console file processor
     *
     * @param factory factory of parser which will be used to create the results
     *                of the parsing
     */
    public ConsoleFileProcessor(VerificationParserFactory factory) {
        this.verifier = new Verifier(factory);
        this.factory = factory;
    }

    public boolean processFile(String path, String outputPath) {
        InputReader inputReader = new InputReader();
        File file = new File(path);
        if (!file.isFile()) {
            System.err.println(Translator.get("error.path.not.a.file"));
        }
        File outputFile = null;
        if (outputPath != null) {
            outputFile = new File(outputPath);
            if (!outputFile.isFile()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    System.err.println(String.format(Translator.get("error.cannot.create.file"), outputPath));
                    return false;
                }
                if (!outputFile.canWrite()) {
                    System.err.println(String.format(Translator.get("error.cannot.write.file"), outputFile.getName()));
                    return false;
                }
            } else {
                System.err.println(Translator.get("error.outputfile.exists.already"));
                return false;
            }
        }
        Values values;
        try {
            values = inputReader.readFile(file);
        } catch (InvalidInputException e) {
            System.err.println(e.getMessage());
            return false;
        }

        Map<BigInteger, List<ValueIndexHolder>> transactionMap;
        try {
            transactionMap = ValueMapBuilder.buildTransactionMap(values);
        } catch (InvalidInputException e) {
            System.err.println(e.getLocalizedMessage());
            return false;
        }
        //remove -1 entries they only have been added for creating the transactionMap
        transactionMap.remove(ValueMapBuilder.NO_TRANSACTION_KEY);
        List<VerificationResult> resultList = processValues(values);
        for (BigInteger transactionId : transactionMap.keySet()) {
            List<ValueIndexHolder> valueIndexHolders = transactionMap.get(transactionId);
            List<Value> transactionValues = new ArrayList<>();
            VerificationType type = null;
            String publicKey = null;
            for (ValueIndexHolder valueIndexHolder : valueIndexHolders) {
                transactionValues.add(valueIndexHolder.getValue());
                type = valueIndexHolder.getValue().getSignedData().getFormatAsVerificationType();
                if(valueIndexHolder.getValue().getPublicKey() != null) {
                    publicKey = valueIndexHolder.getValue().getPublicKey().getValue();
                }
            }

            if (type == null) {
                System.err.println("No type given for the values");
                return false;
            }
            VerificationParser parser;
            try {
                parser = factory.getParser(type);
            } catch (VerificationTypeNotImplementedException e) {
                System.err.println(e.getLocalizedMessage());
                return false;
            }
            if (transactionValues.size() <= 1) {
				VerificationResult result;
				try {
					String val = transactionValues.get(0).getSignedData().getValue();
					result = verifier.verify(parser, val, publicKey);
					resultList.clear();
					resultList.add(result);
				} catch (Exception e) {
					System.err.println(e.getLocalizedMessage());
					return false;
				}
            } else {
	            VerificationResult result;
	            try {
	                result = verifier.verifyTransaction(parser, transactionValues, publicKey);
	            } catch (TransactionValidationException e) {
	                System.err.println(e.getLocalizedMessage());
	                return false;
	            }
	            resultList.add(result);
            }
        }
        try {
            Output output = new Output(factory.getVerifiedDataClasses(), resultList, values);
            printOutXml(output.createXML(), outputFile);
            return true;
        } catch (JAXBException e) {
            LOGGER.error("Could not create xml ", e);
            System.err.println(Translator.get("error.create.xml"));
            return false;
        } catch (FileNotFoundException e) {
            System.err.println(String.format(Translator.get("error.cannot.write.file"), outputFile.getName()));
            return false;
        }
    }

    /**
     * Prints the output to std xml
     *
     * @param output
     * @param outputFile
     */
    protected void printOutXml(String output, File outputFile) throws FileNotFoundException {
        if (outputFile != null) {
            PrintWriter writer = new PrintWriter(outputFile);
            writer.println(output);
            writer.flush();
            writer.close();
            System.out.println(Translator.get("app.output.outpfile.created"));
        } else {
            System.out.println(output);
        }
    }

    /**
     * Process an list of values and adds it to a result list
     *
     * @param values
     * @return
     */
    public List<VerificationResult> processValues(Values values) {
        ArrayList<VerificationResult> resultList = new ArrayList<>();
        for (Value value : values.getValues()) {
            resultList.add(verifier.verify(value));
        }
        return resultList;
    }

}
