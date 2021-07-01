package com.hastobe.transparenzsoftware;

import static org.junit.Assert.assertEquals;

import java.awt.Component;
import java.awt.Container;
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

public class AppTest {

	private FrameFixture window;
	static File testDirXML = new File("src/test/resources/xml").getAbsoluteFile();
	static File testDirPCDF = new File("src/test/resources/pcdf").getAbsoluteFile();
	
	@Before
	public void setUp() {
        VerificationParserFactory factory = new VerificationParserFactory();
		MainView app = GuiActionRunner.execute( () -> new MainView(factory));
		window = new FrameFixture(app);
		window.show();
	}
	
	@After
	public void tearDown() {
		window.cleanUp();
	}
	
	static Timeout longTimeout = Timeout.timeout(2000);
	Pattern Daten_wurden_verifiziert = Pattern.compile(".*Ihre Daten wurden verifiziert.*");

	//@Test
    public void test_ocmf_keba_kcp30() throws Exception {
    	chooseFile(testDirXML,"test_ocmf_keba_kcp30.xml");
    	String pubKey = window.textBox("text.pubkey").text();
    	assertEquals("3059301306072A8648CE3D020106082A8648CE3D030107034200043AEEB45C392357820A58FDFB0857BD77ADA31585C61C430531DFA53B440AFBFDD95AC887C658EA55260F808F55CA948DF235C2108A0D6DC7D4AB1A5E1A7955BE", pubKey);
    	window.button("btn.verify").click();
    	delay();
    	FrameFixture view = WindowFinder.findFrame("wnd.verifier0").using(window.robot());
    	view.label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	view.label("lbl.meter").requireText(Pattern.compile(".*0,2597 kWh.*"));
    	view.button("btn.verifyOK").click();
    	delay();
    }

	//@Test
    public void test_input_invalid_xml() throws Exception {
    	chooseFile(testDirXML,"test_input_invalid_xml.xml");
    	String pubKey = window.textBox("text.pubkey").text();
    	assertEquals("", pubKey);
    	window.button("btn.verify").requireDisabled();
    	window.label("lbl.elog").requireText("Die Eingabedaten sind kein gültiges XML-Format");
    	delay();
    }

	//@Test
    public void test_mennekes_full() throws Exception {
    	chooseFile(testDirXML,"test_mennekes_full.xml");
    	String pubKey = window.textBox("text.pubkey").text();
    	assertEquals("6DACB9C5466A25B3EB9F6466B53457C84A27448B01A64A278C0A28DAC95F2B45DF39B79918A9A4D2E3551F3FE925D09D", pubKey);
    	window.button("btn.verify").click();
    	delay();
    	window.label("lbl.elog").requireText("<html><body><p>Ungültige Daten im OCMF-Format</p></body></html>");
    	delay();
    }

	// /transparenzsoftware/src/test/resources/pcdf/TSW0000_01-ok.pcdf
	//@Test
    public void test_TSW0000_01_ok() throws Exception {
    	chooseFile(testDirPCDF,"TSW0000_01-ok.pcdf");
    	String pubKey = window.textBox("text.pubkey").text();
    	assertEquals("049a3ac42ff9aa0ddfe9b627f1f3b5c1ff39f4ff26fa4f495ca86490d625a13bf3d9aa3d70989746fc5322f79f05c85dac5d23caf14f2e60221d52a8c746eaba37", pubKey);
    	window.button("btn.verify").click();
    	delay();
    	FrameFixture view = WindowFinder.findFrame("wnd.verifier0").using(window.robot());
    	view.label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	view.label("lbl.meter").requireText(Pattern.compile(".*3,2760 kWh.*"));
    	view.button("btn.verifyOK").click();
    	delay();
    }

	// /transparenzsoftware/src/test/resources/pcdf/TSW0026_01-sign-fail.pcdf
	//@Test
    public void test_TSW0026_01_sign_fail() throws Exception {
    	chooseFile(testDirPCDF,"TSW0026_01-sign-fail.pcdf");
    	String pubKey = window.textBox("text.pubkey").text();
    	assertEquals("049a3ac42ff9aa0ddfe9b627f1f3b5c1ff39f4ff26fa4f495ca86490d625a13bf3d9aa3d70989746fc5322f79f05c85dac5d23caf14f2e60221d52a8c746eaba37", pubKey);
    	window.button("btn.verify").click();
    	delay();
    	window.label("lbl.elog").requireText("<html><body><p>PCDF Signatur ungültig</p></body></html>");
    	delay();
    }

    private void chooseFile(File subdir, String file) {
    	OpenFileBtnListener.ignoreXML = subdir == testDirPCDF;
		window.menuItem("menu.file").click();
    	JFileChooserFixture chooser = window.fileChooser("chooser",longTimeout);
    	chooser.target().setAcceptAllFileFilterUsed(true);
    	chooser.setCurrentDirectory(subdir);
    	JScrollPane scoll = findComponent(chooser.target(), JScrollPane.class);
    	Container c = (Container)scoll.getComponent(0);
    	JTable jt = (JTable)c.getComponent(0);
    	delay();
    	jt.grabFocus();
    	for (int i = 0; i < jt.getRowCount(); i++) {
    		jt.setRowSelectionInterval(i, i);
        	delay();
        	String path = chooser.target().getSelectedFile().getAbsolutePath();
    		System.out.println(""+i+": "+path);
    		if (path.indexOf(file) > 0) break;
    	}
    	chooser.approve();
    	delay();
	}

    private <T> T findComponent(Component target, Class<T> class1) {
		if (class1.isAssignableFrom(target.getClass())) return (T)target;
		if (target instanceof Container) {
			Container cont = (Container)target;
			for (int i = 0; i < cont.getComponentCount(); i++) {
	    		Component c = cont.getComponent(i);
	    		T t = findComponent(c,class1);
	    		if (t != null) return t;
			}
    	}
		return null;
	}
	private void delay() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
