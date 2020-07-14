package com.hastobe.transparenzsoftware.gui.listeners;

import com.hastobe.transparenzsoftware.gui.views.MainView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Click listener to trigger the verify view
 */
public class VerifyBtnListener implements ActionListener {

    private static final Logger LOGGER = LogManager.getLogger(VerifyBtnListener.class);
    private final MainView mainView;


    public VerifyBtnListener(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        LOGGER.debug("VerifyBtn listener perfom triggered");
        mainView.verify();

    }

}
