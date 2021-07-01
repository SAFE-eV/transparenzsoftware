package com.hastobe.transparenzsoftware.gui.listeners;

import com.hastobe.transparenzsoftware.gui.views.MainView;
import com.hastobe.transparenzsoftware.i18n.Translator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener which opens a JFileChooser to open an input file
 */
public class OpenFileBtnListener implements ActionListener {

    private static final Logger LOGGER = LogManager.getLogger(OpenFileBtnListener.class);
    private final MainView mainView;

    static String currentDir = "./";
    /**
     * For test purposes only:
     */
    public static boolean ignoreXML = false;
    
    public OpenFileBtnListener(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        JFileChooser jfc = new JFileChooser(currentDir);
        jfc.setName("chooser");
        jfc.setDialogTitle(Translator.get("app.view.select.file"));
        //disable it otherwise it will always be on the first place
        jfc.setAcceptAllFileFilterUsed(false);
        if (!ignoreXML) jfc.addChoosableFileFilter(new FileNameExtensionFilter(Translator.get("app.view.extensions.xml"), "xml"));
        jfc.addChoosableFileFilter(new FileNameExtensionFilter(Translator.get("app.view.extensions.pcdf"), "pcdf"));
        //add all files at thend
        jfc.addChoosableFileFilter(jfc.getAcceptAllFileFilter());
        jfc.grabFocus();
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
        	currentDir = jfc.getCurrentDirectory().getAbsolutePath();
            String path = jfc.getSelectedFile().getPath();
            LOGGER.debug("Read in file " + path);
            mainView.onFileOpen(path);
        }
    }
}
