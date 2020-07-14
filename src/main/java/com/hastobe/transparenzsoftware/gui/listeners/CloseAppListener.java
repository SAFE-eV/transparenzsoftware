package com.hastobe.transparenzsoftware.gui.listeners;

import com.hastobe.transparenzsoftware.gui.views.MainView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener to close the whole gui and app.
 */
public class CloseAppListener implements ActionListener {

    private MainView mainView;

    public CloseAppListener(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        mainView.onClose();
    }
}
