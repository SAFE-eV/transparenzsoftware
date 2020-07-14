package com.hastobe.transparenzsoftware.gui.views;

import com.hastobe.transparenzsoftware.gui.managers.VerifyViewManager;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class ViewUtils {

    private static final Logger LOGGER = LogManager.getLogger(ViewUtils.class);

    public static void makeButtonLookLikeLabel(JButton btn) {
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
    }

    /**
     * Creates a new verification window which will show the result of a verification
     *
     * @param verificationResult result returned by the verification classes
     */
    public static void spawnVerificationWindow(VerificationResult verificationResult) {
        VerifyDataView newView = VerifyViewManager.create();
        VerifyViewManager.setState(newView, verificationResult);

        newView.pack();
        newView.setVisible(true);
    }
}
