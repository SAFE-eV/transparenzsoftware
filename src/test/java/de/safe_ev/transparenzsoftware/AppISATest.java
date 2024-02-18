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

public class AppISATest extends AbstractAppTest {

	@Test
    public void test_isa_edl_40p_begin_fail() throws Exception {
    	chooseFile(testDirXML,"isa-edl-40p-begin-fail.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	
    	delayForVerify();
    	assertEquals("319098b3378bd30891f207679be8b0cd78a824d025d68606872e8d13bf4e08e0beeb8d41248eae7a613054e81aa767829b70ff7176f8b0ac81f6559bfd0fcf42", pubKey);
    	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*25695,9316 kWh.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*11.08.2020 07:09:11.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*11.08.2020 07:09:20.*"));
    	delay();
    }

	@Test
    public void test_isa_edl_40p_ok() throws Exception {
    	chooseFile(testDirXML,"isa-edl-40p-ok.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	
    	delayForVerify();
    	assertEquals("319098b3378bd30891f207679be8b0cd78a824d025d68606872e8d13bf4e08e0beeb8d41248eae7a613054e81aa767829b70ff7176f8b0ac81f6559bfd0fcf42", pubKey);
    	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*25695,9316 kWh.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*25718,2610 kWh.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*11.08.2020 07:09:11.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*11.08.2020 07:14:55.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*22,3294 kWh.*"));
    	delay();
    }

    @Test
    public void test_isa_edl_40p_sign_fail() throws Exception {
    	chooseFile(testDirXML,"isa-edl-40p-sign-fail.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	
    	delayForVerify();
    	assertEquals("319098b3378bd30891f207679be8b0cd78a824d025d68606872e8d13bf4e08e0beeb8d41248eae7a613054e81aa767829b70ff7176f8b0ac81f6559bfd0fcf41", pubKey);
    	getWindow().label("lbl.elog").requireText(Ungueltiger_Schluessel);
    	delay();
    }


    @Test
    public void test_isa_edl_40p_data_fail() throws Exception {
    	chooseFile(testDirXML,"isa-edl-40p-data-fail.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	
    	delayForVerify();
    	assertEquals("319098b3378bd30891f207679be8b0cd78a824d025d68606872e8d13bf4e08e0beeb8d41248eae7a613054e81aa767829b70ff7176f8b0ac81f6559bfd0fcf42", pubKey);
    	getWindow().label("lbl.elog").requireText(Pattern.compile(".*Ungültige Daten im SML-Format.*"));
    	delay();
    }

}
