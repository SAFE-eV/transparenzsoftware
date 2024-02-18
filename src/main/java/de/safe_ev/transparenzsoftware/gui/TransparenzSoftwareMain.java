package de.safe_ev.transparenzsoftware.gui;

import javax.swing.JOptionPane;

import de.safe_ev.transparenzsoftware.gui.views.MainView;
import de.safe_ev.transparenzsoftware.verification.VerificationParserFactory;

/**
 * Main entry point of the gui part of the application. Wrapper for starting
 * the gui in an separate thread
 */
public class TransparenzSoftwareMain {

	public static final int MIN_VERSION = 14;
	
    public static void initWithParser(VerificationParserFactory factory, String filePath) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                init(factory, filePath);
            }
        });
    }

    private static void init(VerificationParserFactory factory, String filePath) {
    	try {
    		if (Runtime.version().feature() < MIN_VERSION) {
    			tooOld();
    		}
    	} catch (Throwable t) {
    		tooOld();
    	}
        MainView init = MainView.init(factory);
        if(filePath != null && !filePath.trim().isEmpty()) {
            init.onFileOpen(filePath);
        }
    }

	private static void tooOld() {
		JOptionPane tooold = new JOptionPane();
		tooold.showMessageDialog(null, "Die Java Version muss mindestens "+MIN_VERSION+".x.x sein.", "Falsche Java Version", JOptionPane.OK_OPTION);
		System.exit(1);
	}

}
