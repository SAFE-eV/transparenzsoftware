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

public class AppXMLTest extends AbstractAppTest {

	//@Test
    public void test_input_invalid_xml() throws Exception {
    	chooseFile(testDirXML,"test_input_invalid_xml.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	assertEquals("", pubKey);
    	getWindow().label("lbl.elog").requireText("Fehler 1306: Die Eingabedaten sind kein gültiges XML-Format");
    	delay();
    }

	@Test
    public void test_mennekes_full() throws Exception {
 //   	chooseFile(testDirXML,"ACU_korrekt.metra.xml");
		chooseFile(testDirXML,"demeke99001002-2020-04-21-08_53_12-5892888.metra.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
//    	assertEquals("ddb6d736e4664afbf2748436dfbfbee1975803561fa75ba2b770ce93d10a5b3fd61e0017ffad7917d0146c5abe38c1a5", pubKey);
    	assertEquals("ed9cd49c6170f3a9502ecd61d3151be7148515da1ca5c214f77a4c4904a715d4cc97097e44a47b5e1d81706d4ed5b601", pubKey);
    	
    	delayForVerify();
 //HBO   	getWindow().label("lbl.elog").requireText("Fehler 1305: Unbekanntes Datenformat");
    	delay();
    }


}
