package de.safe_ev.transparenzsoftware.gui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.Constants;
import de.safe_ev.transparenzsoftware.i18n.Translator;

/**
 * About view containg used libraries and a copyright text
 */
public class AboutView extends JFrame {
    private final static Logger LOGGER = LogManager.getLogger(AboutView.class);

    public AboutView(MainView mainView) {
	setTitle(Translator.get("app.view.about"));
	setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

	add(Box.createRigidArea(new Dimension(0, 10)));

	final JLabel hastobe = new JLabel("Transparenzssoftware " + String.format("Version: %s", Constants.VERSION));
	hastobe.setAlignmentX(0.5f);
	add(hastobe);
	add(Box.createRigidArea(new Dimension(0, 20)));
	final JLabel creator = new JLabel("Hersteller: © 2023 S.A.F.E. e.V.");
	creator.setAlignmentX(0.5f);
	add(creator);
	if (Constants.BETA) {
	    final JLabel betaText = new JLabel(
		    "Es handelt sich hierbei um eine nicht freigegebene, nicht geprüfte Testversion.");
	    betaText.setAlignmentX(0.5f);
	    add(betaText);
	}

	add(Box.createRigidArea(new Dimension(0, 20)));
	final JLabel checksumText = new JLabel(
		Translator.get("app.view.checksum") + " Transparenzssoftware (SHA-256):");
	checksumText.setAlignmentX(0.5f);
	add(checksumText);
	final JLabel checksum = new JLabel(getCheckSum());
	checksum.setAlignmentX(0.5f);
	add(checksum);

	add(Box.createRigidArea(new Dimension(0, 20)));
	final JLabel text = new JLabel("Powered by:");
	text.setAlignmentX(0.5f);
	add(text);

	add(Box.createRigidArea(new Dimension(0, 10)));
	for (final String openSourceName : openSourceNames()) {
	    final JLabel library = new JLabel(openSourceName);
	    library.setAlignmentX(0.5f);
	    add(library);
	}

	add(Box.createRigidArea(new Dimension(0, 200)));

	final JButton close = new JButton(Translator.get("app.view.close"));
	close.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent event) {
		mainView.onAboutClose();
	    }
	});

	close.setAlignmentX(0.5f);
	add(close);
	setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	setSize(300, 200);

	this.addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
		mainView.onAboutClose();
	    }
	});
    }

    private ArrayList<String> openSourceNames() {
	final ArrayList<String> libraryNames = new ArrayList<>();
	libraryNames.add("Bouncy Castle Version 1.72 (https://www.bouncycastle.org) - MIT License");
	libraryNames.add("jSML Openmuc (https://www.openmuc.org/sml/) - MPL v2.0 License");
	libraryNames.add("Gson (https://github.com/google/gson) - Apache 2.0 License");
	libraryNames.add("Guava (https://github.com/google/guava) - Apache 2.0 License");
	libraryNames.add("Commons-Cli (https://commons.apache.org/proper/commons-cli/) - Apache 2.0 License");
	libraryNames.add("Log4j (https://logging.apache.org/log4j/2.x/) - Apache 2.0 License");
	return libraryNames;
    }

    private static String getCheckSum() {
	final StringBuilder sb = new StringBuilder();
	try {
	    final URL u = AboutView.class.getProtectionDomain().getCodeSource().getLocation();
	    final String path = URLDecoder.decode(u.getPath(), Charset.forName("UTF-8"));
	    final File currentJavaJarFile = new File(path);
	    final String filepath = currentJavaJarFile.getAbsolutePath();

	    final MessageDigest md = MessageDigest.getInstance("SHA-256");// MD5
	    final FileInputStream fis = new FileInputStream(filepath);
	    final byte[] dataBytes = new byte[1024];
	    int nread = 0;

	    while ((nread = fis.read(dataBytes)) != -1) {
		md.update(dataBytes, 0, nread);
	    }

	    final byte[] mdbytes = md.digest();

	    for (int i = 0; i < mdbytes.length; i++) {
		sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	} catch (final Exception e) {
	    LOGGER.error("Could not create checksum for jar file", e);
	}

	return sb.toString();
    }
}
