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

public class AppXMLTest extends AbstractAppTest {

	@Test
    public void test_input_invalid_xml() throws Exception {
    	chooseFile(testDirXML,"test_input_invalid_xml.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	assertEquals("", pubKey);
    	getWindow().label("lbl.elog").requireText("Die Eingabedaten sind kein gültiges XML-Format");
    	delay();
    }

	@Test
    public void test_mennekes_full() throws Exception {
    	chooseFile(testDirXML,"test_mennekes_full.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	assertEquals("6DACB9C5466A25B3EB9F6466B53457C84A27448B01A64A278C0A28DAC95F2B45DF39B79918A9A4D2E3551F3FE925D09D", pubKey);
    	delayForVerify();
    	getWindow().label("lbl.elog").requireText("Unbekanntes Format");
    	delay();
    }


}
