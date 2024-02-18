package de.safe_ev.transparenzsoftware;

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

import de.safe_ev.transparenzsoftware.gui.listeners.OpenFileBtnListener;
import de.safe_ev.transparenzsoftware.gui.views.MainView;
import de.safe_ev.transparenzsoftware.verification.VerificationParserFactory;

public class AppOCMFebeeTest extends AbstractAppTest {

	@Test
    public void test_ocmf_ebee_01() throws Exception {
    	chooseFile(testDirXML,"test_ocmf_ebee_01.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	
    	delayForVerify();
    	assertEquals("3059 3013 0607 2A86 48CE 3D02 0106 082A 8648 CE3D 0301 0703 4200 04E5 CE28 01D7 16BF 55B7 1477 CA66 6BE6 A8B8 994F EBBF 05B0 4BC0 AE78 79A6 2AE7 7460 A143 35F3 F306 57DD 7ADC 4338 398C 837D D2DE 52D8 FCAA E786 01D1 476F E1C2 1F", pubKey);
    	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*7,753 kWh.*"));
    	delay();
    }

	@Test
    public void test_ocmf_ebee_02() throws Exception {
    	chooseFile(testDirXML,"test_ocmf_ebee_02.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	
    	delayForVerify();
    	assertEquals("3059 3013 0607 2A86 48CE 3D02 0106 082A 8648 CE3D 0301 0703 4200 04E5 CE28 01D7 16BF 55B7 1477 CA66 6BE6 A8B8 994F EBBF 05B0 4BC0 AE78 79A6 2AE7 7460 A143 35F3 F306 57DD 7ADC 4338 398C 837D D2DE 52D8 FCAA E786 01D1 476F E1C2 1F", pubKey);
    	getWindow().label("lbl.icon").requireText(Daten_wurden_nicht_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*7,753 kWh.*"));
    	delay();
    }

}
