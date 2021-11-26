package com.hastobe.transparenzsoftware;

import static org.junit.Assert.assertEquals;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JFileChooserFixture;
import org.assertj.swing.timing.Timeout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hastobe.transparenzsoftware.gui.listeners.OpenFileBtnListener;
import com.hastobe.transparenzsoftware.gui.views.MainView;
import com.hastobe.transparenzsoftware.verification.VerificationParserFactory;

public class AppOCMFTest extends AbstractAppTest {

	//@Test
    public void test_ocmf_keba_kcp30() throws Exception {
    	chooseFile(testDirXML,"test_ocmf_keba_kcp30.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	
    	delayForVerify();
    	assertEquals("3059301306072A8648CE3D020106082A8648CE3D030107034200043AEEB45C392357820A58FDFB0857BD77ADA31585C61C430531DFA53B440AFBFDD95AC887C658EA55260F808F55CA948DF235C2108A0D6DC7D4AB1A5E1A7955BE", pubKey);
    	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*0,2597 kWh.*"));
    	delay();
    }

	//@Test
    public void test_ocmf_keba_kcp30_fail() throws Exception {
    	chooseFile(testDirXML,"test_ocmf_keba_kcp30_fail.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	
    	delayForVerify();
    	assertEquals("3059301306072A8648CE3D020106082A8648CE3D030107034200043AEEB45C392357820A58FDFB0857BD77ADA31585C61C430531DFA53B440AFBFDD95AC887C658EA55260F808F55CA948DF235C2108A0D6DC7D4AB1A5E1A7955BE", pubKey);
    	getWindow().label("lbl.icon").requireText(Daten_wurden_nicht_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*0,2597 kWh.*"));
    	delay();
    }

	//@Test
    public void test_ocmf_brainpoolP() throws Exception {
    	chooseFile(testDirXML,"brainpoolP256r1.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	assertEquals("305A301406072A8648CE3D020106092B240303020801010703420004607201339EF7C61EB1270C0BEA675585711CB160835F0F55975A311EA6F14AE98981A9276ABC6C70ADA4F8CE25A5502336C738B9E86FB1ED62150FF1B0FAD5A2", pubKey);
    	delayForVerify();
    	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*1,833 kWh.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*0,000 kWh.*"));
    	delay();
    }

	@Test
    public void test_second_key_fail() throws Exception {
    	chooseFile(testDirXML,"second_key_fail_ocmf.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	assertEquals("30 56 30 10 06 07 2A 86 48 CE 3D 02 01 06 05 2B 81 04 00 0A 03 42 00 04 AC EB B9 E1 48 F9 66 DB 42 35 C4 9E 5E CA AD 19 79 F2 2F 4F AF E4 23 8A 6B A3 23 FA A0 EA 9F FB 68 4B 0B FC 7F CE 48 F7 81 C9 15 23 63 20 1A A0 DC 79 38 46 C3 97 A1 DB D4 F4 6F 87 00 83 09 B1", pubKey);
    	delayForVerify();
    	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*1,833 kWh.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*0,000 kWh.*"));
    	delay();
    }


}
