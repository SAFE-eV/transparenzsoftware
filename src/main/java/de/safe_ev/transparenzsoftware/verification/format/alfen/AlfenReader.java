package de.safe_ev.transparenzsoftware.verification.format.alfen;

import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;

public class AlfenReader {


    public AlfenSignature parseString(String data) throws ValidationException {
        Split split = new Split(data);

        try {
            byte[] dataSet = EncodingType.base32Decode(split.dataset);
            byte[] signature = EncodingType.base32Decode(split.signature);
            AlfenSignature payload = new AlfenSignature(split.identifier, split.type, split.blobVersion, split.publicKey, dataSet, signature);
            return payload;
        } catch (DecodingException e) {
            throw new ValidationException("Could not parse alfen string", "error.alfen.validation");
        }

    }

    private class Split {

        private final static String SEPERATOR = ";";
        private final static int POS_IDENTIFIER = 0;
        private final static int POS_TYPE = 1;
        private final static int POS_BLOB_VERSION = 2;
        private final static int POS_PUBLIC_KEY = 3;
        private final static int POS_DATASET = 4;
        private final static int POS_SIGNATURE = 5;


        private String identifier;
        private String type;
        private String blobVersion;
        private String publicKey;
        private String dataset;
        private String signature;

        private Split(String data) throws ValidationException {
            String[] splitted = data.split(SEPERATOR);
            if (splitted.length != 6) {
                throw new ValidationException("Invalid length of datablocks in dataset", "error.alfen.invaliddatablocklength");
            }
            this.identifier = splitted[POS_IDENTIFIER];
            this.type = splitted[POS_TYPE];
            this.blobVersion = splitted[POS_BLOB_VERSION];
            this.publicKey = splitted[POS_PUBLIC_KEY];
            this.dataset = splitted[POS_DATASET];
            this.signature = splitted[POS_SIGNATURE];
        }
    }
}
