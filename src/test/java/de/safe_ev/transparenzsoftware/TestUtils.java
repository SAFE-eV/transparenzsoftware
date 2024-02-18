package de.safe_ev.transparenzsoftware;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.format.alfen.AlfenSignature;

public class TestUtils {

    public static final String SML_FULL = "GxsbGwEBAQF2BZ3J/WxiAGIAcmMBAXYBBUViZWUFXcdVGgsJAUVNSAAAesZiAQFj7B0AdgWdyf1rYgBiAHJjBwF3AQsJAUVNSAAAesZiB4GAgXED/3JiAWUABTFgdHcHgYKBVAH/AXJiA3NlW4V/AVMAPFMAPAEBDWFiY2RlZmdoLTEyMwF3BwEAAREA/2QBAQhyYgNzZVuFfwFTADxTADxiHlL/VgAAAAtmAXcHgQBgCAABAQEBAXJiAXJiAWUABTE9AXcHgYCBcQH/AQEBAWUAAABBAYMEKfiBzm3mT65YsoQM/354kgWHkia8tSFep6/OQrhl1qXUGrkwOixS3hs1B5b5KYcVAAoBYzQvAHYFncn9bWIAYgByYwIBcQFj8nIAABsbGxsaAZkP";
    public static final String SML_FULL_PUBLICKEY = "3108 fa2a caa4 45ab 6aef 6465 c98d a3e6 c3e7 b8bc b14e 1c5f 6b02 1064 b59c 7f67 2511 f183 ddac 0e8e 405c 3196 3d2c d73e";


    public static String TEST_SIG_ONLY = "&lt;?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "&lt;signedMeterValue>\n" +
            "&lt;publicKey encoding=\"base64\">\n" +
            "XLyYACZ8/kaDQB+WzaTo8xhfl7xmo46vNhk2bPxA8v79MSaMV3z1mmnBCEYaqm2z&lt;/publicKey>\n" +
            "&lt;meterValueSignature encoding=\"base64\">\n" +
            "5YP4W8xZDLC1Cr9uX5BiWYKFeBUP+t4i6fnt58dadMxk20zbgnxZgjrYH8Kl0xzbAAI=&lt;/meterValueSignature>\n" +
            "&lt;signatureMethod>\n" +
            "ECDSA192SHA256&lt;/signatureMethod>\n" +
            "&lt;encodingMethod>\n" +
            "EDL&lt;/encodingMethod>\n" +
            "&lt;encodedMeterValue encoding=\"base64\">\n" +
            "CQFFTUgAAH7gNd8UrVsItpUAAAcAAAABAAERAP8e/4oQAAAAAAAAAAIxYTdkNmE0MwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN4UrVsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=&lt;/encodedMeterValue>\n" +
            "&lt;/signedMeterValue>";

    public static String TEST_SIG_ONLY_2 = "&lt;?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "&lt;signedMeterValue>\n" +
            "&lt;publicKey encoding=\"base64\">\n" +
            "XLyYACZ8/kaDQB+WzaTo8xhfl7xmo46vNhk2bPxA8v79MSaMV3z1mmnBCEYaqm2z&lt;/publicKey>\n" +
            "&lt;meterValueSignature encoding=\"base64\">\n" +
            "ELyvG62DwWTW7QCHvJMxApguRj1DBrfRpXMxp/7AG73uaGsN1ylLFI1HypY3npa0AAI=&lt;/meterValueSignature>\n" +
            "&lt;signatureMethod>\n" +
            "ECDSA192SHA256&lt;/signatureMethod>\n" +
            "&lt;encodingMethod>\n" +
            "EDL&lt;/encodingMethod>\n" +
            "&lt;encodedMeterValue encoding=\"base64\">\n" +
            "CQFFTUgAAH7gNUwVrVuIIpYAAAgAAAABAAERAP8e/4sQAAAAAAAAAAIxYTdkNmE0MwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEsVrVsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=&lt;/encodedMeterValue>\n" +
            "&lt;/signedMeterValue>";

    public static byte[] createTestArray(int length, int offset) {
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            data[i] = (byte) (i + offset);
        }
        return data;
    }

    public static byte[] createTestRevertedArray(int length, int offset) {
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            data[length - i - 1] = (byte) (i + offset);
        }
        return data;
    }

    public static String readFile(File f) {
        try {
            byte[] bytes = Files.readAllBytes(f.toPath());
            return new String(bytes, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static AlfenSignature createAlfenSignatureData(byte[] status, byte[] paging, byte[] value) throws ValidationException, DecodingException {
        byte[] sessionId = {0, 1, 0, 0};
        return createAlfenSignatureData(status, paging, value, sessionId);
    }

    public static AlfenSignature createAlfenSignatureData(byte[] status, byte[] paging, byte[] value, byte[] sessionId) throws DecodingException, ValidationException {

        byte[] adapterId = TestUtils.createTestArray(10, 0);
        byte[] adapterFirmwareVersion = TestUtils.createTestArray(4, 0);
        byte[] firmwareVersionChecksum = TestUtils.createTestArray(2, 0);
        byte[] meterId = TestUtils.createTestArray(10, 0);
        byte[] secondIndex = EncodingType.hexDecode("87 80 26 00");
        byte[] obisId = EncodingType.hexDecode("01 00 01 08 00 FF");
        byte[] timestamp = EncodingType.hexDecode("7D 11 F8 5B"); //71542984061

        byte unit = 16;
        byte scalar = 0;
        byte[] uid = EncodingType.hexDecode("31 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

        AlfenSignature signatureData = new AlfenSignature();
        signatureData.setAdapterId(adapterId);
        signatureData.setAdapterFirmwareVersion(adapterFirmwareVersion);
        signatureData.setAdapterFirmwareChecksum(firmwareVersionChecksum);
        signatureData.setMeterId(meterId);
        signatureData.setStatus(status);
        signatureData.setSecondIndex(secondIndex);
        signatureData.setObisId(obisId);
        signatureData.setUnit(unit);
        signatureData.setScalar(scalar);
        signatureData.setUid(uid);
        signatureData.setSessionId(sessionId);
        signatureData.setPaging(paging);
        signatureData.setSignature(TestUtils.createTestArray(10, 0));
        signatureData.setValue(value);
        signatureData.setTimestamp(timestamp);
        return signatureData;
    }
}
