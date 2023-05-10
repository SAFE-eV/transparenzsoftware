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
import org.assertj.swing.core.KeyPressInfo;
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

public class AbstractAppTest {

	private FrameFixture window;
	static File testDirXML = new File("src/test/resources/xml").getAbsoluteFile();
	static File testDirPCDF = new File("src/test/resources/pcdf").getAbsoluteFile();
	static Timeout longTimeout = Timeout.timeout(2000);
	Pattern Daten_wurden_verifiziert = Pattern.compile(".*Ihre Daten wurden verifiziert.*");
	Pattern Daten_wurden_nicht_verifiziert = Pattern.compile(".*Ihre Daten wurden nicht verifiziert.*");
	Pattern Ungueltiger_Schluessel = Pattern.compile(".*Ungültiger öffentlicher Schlüssel.*");

	
	@Before
	public void setUp() {
        VerificationParserFactory factory = new VerificationParserFactory();
		MainView app = GuiActionRunner.execute( () -> new MainView(factory));
		window = new FrameFixture(app);
//		Dimension d = new Dimension(800,600);
//		window.show(d);
		
	}
	
	@After
	public void tearDown() {
		window.cleanUp();
	}
	
	protected void chooseByPaste(String data, String pubkey) {
		delay();
		window.menuItem("menu.paste").click();
		delay();
		DialogFixture d = window.dialog("dialog.input");
		delay();
		d.textBox("paste.data").focus();
		delay();
		d.textBox("paste.data").setText(data);
		delay();
		d.textBox("paste.key").setText(pubkey);
		delay();
		d.button("paste.close").click();
		delay();
	}
	
	protected void chooseFile(File subdir, String file) {
    	file = "/"+file;
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
        	delayShort();
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
    protected void delay() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    protected void delayShort() {
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    protected void delayForVerify() {
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FrameFixture getWindow() {
		return window;
	}

}
