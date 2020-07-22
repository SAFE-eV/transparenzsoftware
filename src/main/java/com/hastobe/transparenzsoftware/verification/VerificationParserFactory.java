package com.hastobe.transparenzsoftware.verification;

import com.hastobe.transparenzsoftware.verification.format.alfen.AlfenVerificationParser;
import com.hastobe.transparenzsoftware.verification.format.ocmf.OCMFVerificationParser;
import com.hastobe.transparenzsoftware.verification.format.pcdf.PcdfVerificationParser;
import com.hastobe.transparenzsoftware.verification.format.sml.EDL40.EDL40VerificationParser;
import com.hastobe.transparenzsoftware.verification.format.sml.EDLMennekes.EDLMennekesVerificationParser;
import com.hastobe.transparenzsoftware.verification.format.sml.SignatureOnly.SignatureOnlyVerificationParser;

import java.util.ArrayList;
import java.util.List;

public class VerificationParserFactory {

    List<VerificationParser> parser;

    public VerificationParserFactory(List<VerificationParser> parser) {
        this.parser = parser;
    }

    public VerificationParserFactory() {
        this.parser = new ArrayList<>();
        this.parser.add(new EDL40VerificationParser());
        this.parser.add(new SignatureOnlyVerificationParser());
        this.parser.add(new EDLMennekesVerificationParser());
        this.parser.add(new AlfenVerificationParser());
        this.parser.add(new OCMFVerificationParser());
        this.parser.add(new PcdfVerificationParser());
    }

    /**
     * @param type format of data
     * @return a parser to handle the given data
     * @throws VerificationTypeNotImplementedException if no parser is present for that format
     */
    public VerificationParser getParser(VerificationType type) throws VerificationTypeNotImplementedException {
        if (type == null) {
            throw new VerificationTypeNotImplementedException(null);
        }
        for (VerificationParser verificationParser : parser) {
            if (verificationParser.getVerificationType().equals(type)) {
                return verificationParser;
            }
        }
        throw new VerificationTypeNotImplementedException(type);
    }

    /**
     * @return a parser to handle the given data
     * @throws VerificationTypeNotImplementedException if no parser is present for that format
     */
    public List<VerificationParser> getParserWithData(String data) throws VerificationTypeNotImplementedException {
        if (data == null) {
            throw new VerificationTypeNotImplementedException(null);
        }
        List<VerificationParser> possibleParser = new ArrayList<>();
        for (VerificationParser verificationParser : parser) {
            if (verificationParser.canParseData(data)) {
                possibleParser.add(verificationParser);
            }
        }
        if (possibleParser.size() == 0) {
            throw new VerificationTypeNotImplementedException(null);
        }
        return possibleParser;
    }

    /**
     * @return
     */
    public Class[] getVerifiedDataClasses() {
        List<Class> classList = new ArrayList<>();
        for (VerificationParser verificationParser : parser) {
            classList.add(verificationParser.getVerfiedDataClass());
        }
        return classList.toArray(new Class[0]);
    }

}
