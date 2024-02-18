package de.safe_ev.transparenzsoftware.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.gui.views.helper.ValueIndexHolder;
import de.safe_ev.transparenzsoftware.gui.views.helper.ValueMapBuilder;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.TransactionValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationParser;
import de.safe_ev.transparenzsoftware.verification.VerificationParserFactory;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.VerificationTypeNotImplementedException;
import de.safe_ev.transparenzsoftware.verification.Verifier;
import de.safe_ev.transparenzsoftware.verification.input.InputReader;
import de.safe_ev.transparenzsoftware.verification.input.InvalidInputException;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;
import de.safe_ev.transparenzsoftware.verification.xml.Value;
import de.safe_ev.transparenzsoftware.verification.xml.Values;

/**
 * File processor of an input xml to create the xml result for cli part of the
 * "Transparenzsoftware"
 */
public class ConsoleFileProcessor {

	private final static Logger LOGGER = LogManager.getLogger(ConsoleFileProcessor.class);
	private final Verifier verifier;
	private final VerificationParserFactory factory;

	/**
	 * Initiates the console file processor
	 *
	 * @param factory factory of parser which will be used to create the results of
	 *                the parsing
	 */
	public ConsoleFileProcessor(VerificationParserFactory factory) {
		verifier = new Verifier(factory);
		this.factory = factory;
	}

	public boolean processFile(String path, String outputPath, boolean overwrite) {
		final InputReader inputReader = new InputReader();
		final File file = new File(path);
		if (!file.isFile()) {
			System.err.println(Translator.get("error.path.not.a.file"));
		}
		File outputFile = null;
		if (outputPath != null) {
			outputFile = new File(outputPath);
			if (!outputFile.isFile() || overwrite) {
				try {
					if (!overwrite) {
						outputFile.createNewFile();
					}
				} catch (final IOException e) {
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
		} catch (final InvalidInputException e) {
			System.err.println(e.getMessage());
			return false;
		}

		Map<BigInteger, List<ValueIndexHolder>> transactionMap;
		try {
			transactionMap = ValueMapBuilder.buildTransactionMap(values);
		} catch (final InvalidInputException e) {
			System.err.println(e.getLocalizedMessage());
			return false;
		}
		// remove -1 entries they only have been added for creating the transactionMap
		transactionMap.remove(ValueMapBuilder.NO_TRANSACTION_KEY);
		final List<VerificationResult> resultList = processValues(values);
		for (final BigInteger transactionId : transactionMap.keySet()) {
			final List<ValueIndexHolder> valueIndexHolders = transactionMap.get(transactionId);
			final List<Value> transactionValues = new ArrayList<>();
			VerificationType type = null;
			String publicKey = null;
			for (final ValueIndexHolder valueIndexHolder : valueIndexHolders) {
				transactionValues.add(valueIndexHolder.getValue());
				type = valueIndexHolder.getValue().getSignedData().getFormatAsVerificationType();
				if (valueIndexHolder.getValue().getPublicKey() != null) {
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
			} catch (final VerificationTypeNotImplementedException e) {
				System.err.println(e.getLocalizedMessage());
				return false;
			}
			if (transactionValues.size() <= 1) {
				VerificationResult result;
				try {
					final Value val = transactionValues.get(0);
					result = verifier.verify(parser, val, publicKey);
					resultList.clear();
					resultList.add(result);
				} catch (final Exception e) {
					System.err.println(e.getLocalizedMessage());
					return false;
				}
			} else {
				VerificationResult result;
				try {
					result = verifier.verifyTransaction(parser, transactionValues, publicKey);
				} catch (final TransactionValidationException e) {
					System.err.println(e.getLocalizedMessage());
					return false;
				}
				resultList.add(result);
			}
		}
		try {
			final Output output = new Output(factory.getVerifiedDataClasses(), resultList, values);
			printOutXml(output.createXML(), outputFile);
			return true;
		} catch (final JAXBException e) {
			LOGGER.error("Could not create xml ", e);
			System.err.println(Translator.get("error.create.xml"));
			return false;
		} catch (final FileNotFoundException e) {
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
			final PrintWriter writer = new PrintWriter(outputFile);
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
		final ArrayList<VerificationResult> resultList = new ArrayList<>();
		for (final Value value : values.getValues()) {
			resultList.add(verifier.verify(value));
		}
		return resultList;
	}

}
