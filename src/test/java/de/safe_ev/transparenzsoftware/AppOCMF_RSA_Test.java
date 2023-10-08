package de.safe_ev.transparenzsoftware;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

public class AppOCMF_RSA_Test extends AbstractAppTest {

    @Test
    public void test_ocmf_rsa_01() throws Exception {
	chooseFile(testDirXML, "test_ocmf_rsa_01.xml");
	final String pubKey = getWindow().textBox("text.pubkey").text();

	delayForVerify();
	final int index = pubKey.indexOf(
		"30820122300D06092A864886F70D01010105000382010F003082010A0282010100C01F92BF36ED9D80A82D7245DEAAC49E013B050A4FD0C87D0BBE60A351C4F72B219A6E59CF5D79DD06F47456F1BE36774F518CCB7CEBE989A0544E4F341D2F0DBC9A8E3A6AE64BAF83E9EA7D50DAF64F59A8400F06B9F4FB99C5E568FC597440DB81B768C329C9D62672F59A427AAE3454B45D966D11FC2F56F9803B82FAE30667EFD1277536A5D0D4D4331475C7662CDA11C99D9A65970A1ED5C0A03E85D96CC584CB6B4EBB09E00F860DB24DA02EC634D32957E79790928DE4F45E7B2BA4D9F894AEF0E5C20CAFBC407C7EFAFBC1AC72A2C0E02AE62817E68291B77A93CCCD748BC98FC778B4C89A22D1991D13E21A414C2F16EBD6E49A54FCAD152DDAD12F0203010001");

	assertEquals(0, index);
	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
	getWindow().label("lbl.meter").requireText(Pattern.compile(".*62500,2031 kWh.*"));
	delay();
    }

}
