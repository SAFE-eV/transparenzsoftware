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

public class AppVW_OCMFTest extends AbstractAppTest {

	@Test
    public void test_vw_ocmf() throws Exception {
    	chooseFile(testDirXML,"VW_OCMF_load.xml");
    	String pubKey = getWindow().textBox("text.pubkey").text();
    	
    	delayForVerify();
    	assertEquals("3059301306072A8648CE3D020106082A8648CE3D03010703420004CE6FD2090A85FDE4A69B58EF00FDE362833AD8C70F8E8027CAAC655C0BB02F6B1A44558C78B386FF0BC3A4BBE6C81A4F8133134DB05DAE91E2AA309C07A5128B", pubKey);
    	getWindow().label("lbl.icon").requireText(Daten_wurden_verifiziert);
    	getWindow().label("lbl.meter").requireText(Pattern.compile(".*1,101 kWh.*"));
    	delay();
    }

}
