package de.safe_ev.transparenzsoftware.verification.format.ocmf;


public class OCMF {

    private OCMFPayloadData data;
    private String rawData;
    private OCMFSignature signature;
    private String publicKey;


    public OCMF(OCMFPayloadData data, String rawData, OCMFSignature ocmfSignature, String publicKey) {
        this.data = data;
        this.rawData = rawData;
        this.signature = ocmfSignature;
        this.publicKey = publicKey;


    }

    public OCMFPayloadData getData() {
        return data;
    }

    public OCMFSignature getSignature() {
        return signature;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getRawData() {
        return rawData;
    }
}
