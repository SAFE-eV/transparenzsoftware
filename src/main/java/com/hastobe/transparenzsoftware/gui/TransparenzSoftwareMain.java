package com.hastobe.transparenzsoftware.gui;

import com.hastobe.transparenzsoftware.gui.views.MainView;
import com.hastobe.transparenzsoftware.verification.VerificationParserFactory;

/**
 * Main entry point of the gui part of the application. Wrapper for starting
 * the gui in an separate thread
 */
public class TransparenzSoftwareMain {

    public static void initWithParser(VerificationParserFactory factory, String filePath) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                init(factory, filePath);
            }
        });
    }

    private static void init(VerificationParserFactory factory, String filePath) {
        MainView init = MainView.init(factory);
        if(filePath != null && !filePath.trim().isEmpty()) {
            init.onFileOpen(filePath);
        }
    }

}
