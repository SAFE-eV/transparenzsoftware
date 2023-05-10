package de.safe_ev.transparenzsoftware.gui.views.customelements;

import javax.swing.*;

import de.safe_ev.transparenzsoftware.gui.Colors;
import de.safe_ev.transparenzsoftware.gui.views.ViewUtils;

import java.awt.*;

public class StyledButton extends JButton {

    public StyledButton(String s) {
        super(s);
        this.setForeground(Color.WHITE);
        this.setBackground(Colors.VERIFY_BUTTON);
        ViewUtils.makeButtonLookLikeLabel(this);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (evt.getComponent().isEnabled()) {
                    evt.getComponent().setBackground(Colors.VERIFY_BUTTON);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!evt.getComponent().isEnabled()) {
                    evt.getComponent().setBackground(Colors.VERIFY_BUTTON_DISABLED);
                } else {
                    evt.getComponent().setBackground(Color.BLACK);
                }
            }
        });
    }
}
