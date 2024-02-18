package de.safe_ev.transparenzsoftware.gui.views;

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

}
