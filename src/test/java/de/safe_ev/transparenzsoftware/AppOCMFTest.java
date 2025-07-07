package de.safe_ev.transparenzsoftware;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

public class AppOCMFTest extends AbstractAppTest {

    // @Test
    public void test_ocmf_keba_kcp30() throws Exception {
	chooseFile(testDirXML, "test_ocmf_keba_kcp30.xml");
	final String pubKey = getWindow().textBox("text.pubkey").text();

	delayForVerify();
	assertEquals(
		"3059301306072A8648CE3D020106082A8648CE3D030107034200043AEEB45C392357820A58FDFB0857BD77ADA31585C61C430531DFA53B440AFBFDD95AC887C658EA55260F808F55CA948DF235C2108A0D6DC7D4AB1A5E1A7955BE",
		pubKey);
	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
	getWindow().label("lbl.meter").requireText(Pattern.compile(".*0,2597 kWh.*"));
	delay();
    }

    // @Test
    public void test_ocmf_keba_kcp30_fail() throws Exception {
	chooseFile(testDirXML, "test_ocmf_keba_kcp30_fail.xml");
	final String pubKey = getWindow().textBox("text.pubkey").text();

	delayForVerify();
	assertEquals(
		"3059301306072A8648CE3D020106082A8648CE3D030107034200043AEEB45C392357820A58FDFB0857BD77ADA31585C61C430531DFA53B440AFBFDD95AC887C658EA55260F808F55CA948DF235C2108A0D6DC7D4AB1A5E1A7955BE",
		pubKey);
	getWindow().label("lbl.icon").requireText(Daten_wurden_nicht_verifiziert);
	getWindow().label("lbl.meter").requireText(Pattern.compile(".*0,2597 kWh.*"));
	delay();
    }

    // @Test
    public void test_ocmf_brainpoolP() throws Exception {
	chooseFile(testDirXML, "brainpoolP256r1.xml");
	final String pubKey = getWindow().textBox("text.pubkey").text();
	assertEquals(
		"305A301406072A8648CE3D020106092B240303020801010703420004607201339EF7C61EB1270C0BEA675585711CB160835F0F55975A311EA6F14AE98981A9276ABC6C70ADA4F8CE25A5502336C738B9E86FB1ED62150FF1B0FAD5A2",
		pubKey);
	delayForVerify();
	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
	getWindow().label("lbl.meter").requireText(Pattern.compile(".*1,833 kWh.*"));
	getWindow().label("lbl.meter").requireText(Pattern.compile(".*0,000 kWh.*"));
	delay();
    }

    @Test
    public void test_second_key_fail() throws Exception {
	chooseFile(testDirXML, "second_key_fail_ocmf.xml");
	final String pubKey = getWindow().textBox("text.pubkey").text();
	assertEquals("", pubKey);
	delayForVerify();
	getWindow().label("lbl.icon").requireText(".*Keine Daten vorhanden.*");
	delay();
    }

    @Test
    public void test_ocmf_obis_9e() throws Exception {
	chooseFile(testDirXML, "ocmf_obis_9e.xml");
	final String pubKey = getWindow().textBox("text.pubkey").text();
	delayForVerify();
	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
	getWindow().label("lbl.meter").requireText(Pattern.compile(".*9,914 kWh [^k].*"));
	delay();
    }

    @Test
    public void test_ocmf_with_compensation() throws Exception {
	chooseFile(testDirXML, "Ocmf_Example_OBIS_98.8.0_2.8.0.xml");
	delayForVerify();
	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
	getWindow().label("lbl.meter").requireText(Pattern.compile(".*1,0 kWh kompensiert.*"));
	delay();
    }

}
