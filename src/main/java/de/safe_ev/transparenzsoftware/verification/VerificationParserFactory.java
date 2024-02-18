package de.safe_ev.transparenzsoftware.verification;

import java.util.ArrayList;
import java.util.List;

import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenVerificationParser;
import de.safe_ev.transparenzsoftware.verification.format.ocmf.OCMFVerificationParser;
import de.safe_ev.transparenzsoftware.verification.format.pcdf.PcdfVerificationParser;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDL40.EDL40VerificationParser;
import de.safe_ev.transparenzsoftware.verification.format.sml.EDLMennekes.EDLMennekesVerificationParser;
import de.safe_ev.transparenzsoftware.verification.format.sml.IsaEDL40.IsaEDL40VerificationParser;
import de.safe_ev.transparenzsoftware.verification.format.sml.SignatureOnly.SignatureOnlyVerificationParser;

public class VerificationParserFactory {

    List<VerificationParser> parser;

    public VerificationParserFactory(List<VerificationParser> parser) {
        this.parser = parser;
    }

    public VerificationParserFactory() {
        this.parser = new ArrayList<>();
        this.parser.add(new OCMFVerificationParser());
        this.parser.add(new PcdfVerificationParser());
        this.parser.add(new IsaEDL40VerificationParser());
        this.parser.add(new AlfenVerificationParser());
        this.parser.add(new EDL40VerificationParser());
        this.parser.add(new SignatureOnlyVerificationParser());
        this.parser.add(new EDLMennekesVerificationParser());
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
