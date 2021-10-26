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

	@Test
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


}
