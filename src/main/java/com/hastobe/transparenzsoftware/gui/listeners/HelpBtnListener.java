package com.hastobe.transparenzsoftware.gui.listeners;

import com.hastobe.transparenzsoftware.gui.views.MainView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Help button listener, which opens the help view
 */
public class HelpBtnListener implements ActionListener {

    private MainView mainView;

    public HelpBtnListener(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        mainView.onHelpOpen();
    }
}
