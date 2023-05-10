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

public class AppPCDFTest extends AbstractAppTest {

	public static final String pubkey = "049a3ac42ff9aa0ddfe9b627f1f3b5c1ff39f4ff26fa4f495ca86490d625a13bf3d9aa3d70989746fc5322f79f05c85dac5d23caf14f2e60221d52a8c746eaba37";
	public static final String pubkeyWrong = "04e4c95c1ca877c9b8237ccc9bed242f1ff6b87e1988bbe5dcd76de4ee6aa4eac3d7a120708d04857d63ae75eba8e2d5c99512d322ae409f8f77387835234e4c96";
	@Test
    public void test_pcdf_paste_valid_ok() throws Exception {
    	chooseByPaste("128.8.0(ST:200501162017)(CT:200501162025)(CD:000217)(TV:1)(BV:1)(CSC:1)(SP:1)(RV:0003.276*kWh)(SI:55*1*959bff1e-7164-4132-b783-4a9feed1bfee)(CS:513f627b)(HW:12345678901)(DT:0)(PK:049a3ac42ff9aa0ddfe9b627f1f3b5c1ff39f4ff26fa4f495ca86490d625a13bf3d9aa3d70989746fc5322f79f05c85dac5d23caf14f2e60221d52a8c746eaba37)(SG:304502210087b8137ecdc0340319474452b36592ca66aadb3b141295921eb9b08f08336a1902207eec4c574812afc55b42b84708367daf63dd29713e9321823497f051976c3920)"
    			 , pubkey);
    	delayForVerify();
    	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*01.05.2020 16:20:25.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*3,276 kWh.*"));
    }
 
	@Test
    public void test_pcdf_paste_valid_fail() throws Exception {
    	chooseByPaste("128.8.0(ST:200501162017)(CT:200501162025)(CD:000217)(TV:1)(BV:1)(CSC:1)(SP:1)(RV:0003.276*kWh)(SI:55*1*959bff1e-7164-4132-b783-4a9feed1bfee)(CS:513f627b)(HW:12345678901)(DT:0)(PK:049a3ac42ff9aa0ddfe9b627f1f3b5c1ff39f4ff26fa4f495ca86490d625a13bf3d9aa3d70989746fc5322f79f05c85dac5d23caf14f2e60221d52a8c746eaba37)(SG:304502210087b8137ecdc0340319474452b36592ca66aadb3b141295921eb9b08f08336a1902207eec4c574812afc55b42b84708367daf63dd29713e9321823497f051976c3920)"
    			 , pubkeyWrong);
    	delayForVerify();
    	getWindow().label("lbl.icon").requireText(Daten_wurden_nicht_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*01.05.2020 16:20:25.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*3,276 kWh.*"));
    }
 
	@Test
    public void test_pcdf_paste_SPInvalid() throws Exception {
    	chooseByPaste("128.8.0(ST:200501162017)(CT:200501162025)(CD:000217)(TV:1)(BV:1)(CSC:1)(SP:3)(RV:0003.276*kWh)(SI:55*1*959bff1e-7164-4132-b783-4a9feed1bfee)(CS:513f627b)(HW:12345678901)(DT:0)(PK:049a3ac42ff9aa0ddfe9b627f1f3b5c1ff39f4ff26fa4f495ca86490d625a13bf3d9aa3d70989746fc5322f79f05c85dac5d23caf14f2e60221d52a8c746eaba37)(SG:3045022100a9fd6f419bc0de53f6f1475d9c48947c2bd076cac65177e3d3cd4df88e44497502201778374cf8b78d9d70021762bcc573a68d0b4a9d8a8616a33fc776a8bea59a12)"
    			 , pubkey);
    	delayForVerify();
    	getWindow().label("lbl.icon").requireText("Keine Daten vorhanden");
    	getWindow().label("lbl.elog").requireText("Fehler 1517: Abrechnung nicht erlaubt");
    }

	@Test
    public void test_pcdf_paste_BVInvalid() throws Exception {
    	chooseByPaste("128.8.0(ST:200501162017)(CT:200501162025)(CD:000217)(TV:1)(BV:0)(CSC:1)(SP:1)(RV:0003.276*kWh)(SI:55*1*959bff1e-7164-4132-b783-4a9feed1bfee)(CS:513f627b)(HW:12345678901)(DT:0)(PK:049a3ac42ff9aa0ddfe9b627f1f3b5c1ff39f4ff26fa4f495ca86490d625a13bf3d9aa3d70989746fc5322f79f05c85dac5d23caf14f2e60221d52a8c746eaba37)(SG:30440220555132f28c39d971bc88c9574b13389381ece9e893abf1f0bf8c5a98c93768bb02203562ef58e0da96448b27851b3704f0c748a623c6b4b259c9810eb029a15ac5e1)"
    			 , pubkey);
    	delayForVerify();
    	getWindow().label("lbl.icon").requireText("Keine Daten vorhanden");
    	getWindow().label("lbl.elog").requireText("Fehler 1503: Abrechnung nicht erlaubt");
    }

	@Test
    public void test_pcdf_valid_ok() throws Exception {
    	chooseFile(testDirPCDF,"valid.pcdf");
    	String pubKey = getWindow().textBox("text.pubkey").text();

    	delayForVerify();
    	assertEquals(pubkey, pubKey);
    	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*01.05.2020 16:20:25.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*3,276 kWh.*"));
    	delay();
    }

	@Test
    public void test_pcdf_valid_sig_fail() throws Exception {
    	chooseFile(testDirPCDF,"SIGinvalid.pcdf");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	delayForVerify();
    	assertEquals(pubkey, pubKey);
    	getWindow().label("lbl.elog").requireText("<html><body><p>Fehler 1522: PCDF Signatur ungültig</p></body></html>");
    	delay();
    }

	@Test
    public void test_pcdf_SP_invalid() throws Exception {
    	chooseFile(testDirPCDF,"SPinvalid.pcdf");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	delayForVerify();
    	assertEquals("", pubKey);
    	getWindow().label("lbl.elog").requireText("Fehler 1517: Abrechnung nicht erlaubt");
    	delay();
    }

	@Test
    public void test_pcdf_BV_invalid() throws Exception {
    	chooseFile(testDirPCDF,"BVinvalid.pcdf");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	delayForVerify();
    	assertEquals("", pubKey);
    	getWindow().label("lbl.elog").requireText("Fehler 1503: Abrechnung nicht erlaubt");
    	delay();
    }

	@Test
    public void test_pcdf_double_test() throws Exception {
    	chooseFile(testDirPCDF,"valid.pcdf");
    	String pubKey = getWindow().textBox("text.pubkey").text();

    	delayForVerify();
    	assertEquals(pubkey, pubKey);
    	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*01.05.2020 16:20:25.*"));
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*3,276 kWh.*"));
    	delay();

    	chooseFile(testDirPCDF,"BVinvalid.pcdf");
    	pubKey = getWindow().textBox("text.pubkey").text();
    	delayForVerify();
    	assertEquals("", pubKey);
    	getWindow().label("lbl.elog").requireText("Fehler 1503: Abrechnung nicht erlaubt");
    	delay();
    	delayForVerify();
    	getWindow().label("lbl.elog").requireText("Fehler 1503: Abrechnung nicht erlaubt");
    }

}
