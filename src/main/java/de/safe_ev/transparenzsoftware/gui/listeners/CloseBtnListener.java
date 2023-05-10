package de.safe_ev.transparenzsoftware.gui.listeners;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Closes the given jFrame
 */
public class CloseBtnListener implements ActionListener {
    private JFrame frameToClose;


    public CloseBtnListener(JFrame frameToClose) {
        this.frameToClose = frameToClose;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        this.frameToClose.dispose();
    }

}
