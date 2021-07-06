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

public class AppPCDFTest extends AbstractAppTest {

	@Test
    public void test_pcdf_valid_ok() throws Exception {
    	chooseFile(testDirPCDF,"valid.pcdf");
    	String pubKey = getWindow().textBox("text.pubkey").text();

    	delayForVerify();
    	assertEquals("049a3ac42ff9aa0ddfe9b627f1f3b5c1ff39f4ff26fa4f495ca86490d625a13bf3d9aa3d70989746fc5322f79f05c85dac5d23caf14f2e60221d52a8c746eaba37", pubKey);
    	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*01.05.2020 16:20:25.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*3,2760 kWh.*"));
    	delay();
    }

	@Test
    public void test_pcdf_valid_sig_fail() throws Exception {
    	chooseFile(testDirPCDF,"SIGinvalid.pcdf");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	delayForVerify();
    	assertEquals("049a3ac42ff9aa0ddfe9b627f1f3b5c1ff39f4ff26fa4f495ca86490d625a13bf3d9aa3d70989746fc5322f79f05c85dac5d23caf14f2e60221d52a8c746eaba37", pubKey);
    	getWindow().label("lbl.elog").requireText("<html><body><p>PCDF Signatur ungültig</p></body></html>");
    	delay();
    }

	@Test
    public void test_pcdf_SP_invalid() throws Exception {
    	chooseFile(testDirPCDF,"SPinvalid.pcdf");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	delayForVerify();
    	assertEquals("", pubKey);
    	getWindow().label("lbl.elog").requireText("Fehler: Abrechnung nicht erlaubt");
    	delay();
    }

	@Test
    public void test_pcdf_BV_invalid() throws Exception {
    	chooseFile(testDirPCDF,"BVinvalid.pcdf");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	delayForVerify();
    	assertEquals("", pubKey);
    	getWindow().label("lbl.elog").requireText("Fehler: Abrechnung nicht erlaubt");
    	delay();
    }

	@Test
    public void test_pcdf_double_test() throws Exception {
    	chooseFile(testDirPCDF,"valid.pcdf");
    	String pubKey = getWindow().textBox("text.pubkey").text();

    	delayForVerify();
    	assertEquals("049a3ac42ff9aa0ddfe9b627f1f3b5c1ff39f4ff26fa4f495ca86490d625a13bf3d9aa3d70989746fc5322f79f05c85dac5d23caf14f2e60221d52a8c746eaba37", pubKey);
    	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*01.05.2020 16:20:25.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*3,2760 kWh.*"));
    	delay();

    	chooseFile(testDirPCDF,"BVinvalid.pcdf");
    	pubKey = getWindow().textBox("text.pubkey").text();
    	delayForVerify();
    	assertEquals("", pubKey);
    	getWindow().label("lbl.elog").requireText("Fehler: Abrechnung nicht erlaubt");
    	delay();
    	delayForVerify();
    	getWindow().label("lbl.elog").requireText("Fehler: Abrechnung nicht erlaubt");
    }

}
