package com.hastobe.transparenzsoftware.gui.views;

import com.hastobe.transparenzsoftware.Constants;
import com.hastobe.transparenzsoftware.i18n.Translator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * About view containg used libraries and a copyright text
 */
public class AboutView extends JFrame {
    private final static Logger LOGGER = LogManager.getLogger(AboutView.class);

    public AboutView(MainView mainView) {
        setTitle(Translator.get("app.view.about"));
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel hastobe = new JLabel(" © 2020 S.A.F.E. e.V. - " + String.format("Version: %s", Constants.VERSION));
        hastobe.setAlignmentX(0.5f);
        add(hastobe);

        if (Constants.BETA) {
            JLabel betaText = new JLabel("Es handelt sich hierbei um eine nicht freigegebene, nicht geprüfte Testversion.");
            betaText.setAlignmentX(0.5f);
            add(betaText);
        }

        add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel checksumText = new JLabel(Translator.get("app.view.checksum")+" (SHA-256):");
        checksumText.setAlignmentX(0.5f);
        add(checksumText);
        JLabel checksum = new JLabel(getCheckSum());
        checksum.setAlignmentX(0.5f);
        add(checksum);

        add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel text = new JLabel("Powered by:");
        text.setAlignmentX(0.5f);
        add(text);

        add(Box.createRigidArea(new Dimension(0, 10)));
        for (String openSourceName : openSourceNames()) {
            JLabel library = new JLabel(openSourceName);
            library.setAlignmentX(0.5f);
            add(library);
        }

        add(Box.createRigidArea(new Dimension(0, 200)));

        JButton close = new JButton(Translator.get("app.view.close"));
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mainView.onAboutClose();
            }
        });

        close.setAlignmentX(0.5f);
        add(close);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(300, 200);


        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mainView.onAboutClose();
            }
        });
    }


    private ArrayList<String> openSourceNames() {
        ArrayList<String> libraryNames = new ArrayList<>();
        libraryNames.add("Bouncy Castle (https://www.bouncycastle.org) - MIT License");
        libraryNames.add("jSML Openmuc (https://www.openmuc.org/sml/) - MPL v2.0 License");
        libraryNames.add("Gson (https://github.com/google/gson) - Apache 2.0 License");
        libraryNames.add("Guava (https://github.com/google/guava) - Apache 2.0 License");
        libraryNames.add("Commons-Cli (https://commons.apache.org/proper/commons-cli/) - Apache 2.0 License");
        libraryNames.add("Log4j (https://logging.apache.org/log4j/2.x/) - Apache 2.0 License");
        return libraryNames;
    }

    private static String getCheckSum() {
        StringBuilder sb = new StringBuilder();
        try {
            File currentJavaJarFile = new File(AboutView.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            String filepath = currentJavaJarFile.getAbsolutePath();

            MessageDigest md = MessageDigest.getInstance("SHA-256");// MD5
            FileInputStream fis = new FileInputStream(filepath);
            byte[] dataBytes = new byte[1024];
            int nread = 0;

            while ((nread = fis.read(dataBytes)) != -1)
                md.update(dataBytes, 0, nread);

            byte[] mdbytes = md.digest();

            for (int i = 0; i < mdbytes.length; i++)
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        } catch (Exception e) {
            LOGGER.error("Could not create checksum for jar file", e);
        }

        return sb.toString();
    }
}
