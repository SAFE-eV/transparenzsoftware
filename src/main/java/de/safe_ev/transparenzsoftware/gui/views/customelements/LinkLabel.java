package de.safe_ev.transparenzsoftware.gui.views.customelements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class LinkLabel extends JLabel {

    private static final long serialVersionUID = 8333875024682878518L;
    private String text;
    private URI uri;

    public LinkLabel(String text, String uri) {
        this(text, URI.create(uri));
    }

    public LinkLabel(String t, URI u) {
        super();
        text = t;
        uri = u;
        setText(text);
        setToolTipText(uri.toString());
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                open(uri);
            }

            public void mouseEntered(MouseEvent e) {
                setText(text, false);
            }

            public void mouseExited(MouseEvent e) {
                setText(text, true);
            }
        });
        Font font = getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        setFont(font.deriveFont(attributes));
        setForeground(Color.BLUE);
    }

    @Override
    public void setText(String text) {
        setText(text, true);
    }

    public void setText(String text, boolean ul) {
        super.setText(text);
        this.text = text;
    }

    private static void open(URI uri) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(uri);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to launch the link, your computer is likely misconfigured.",
                        "Cannot Launch Link", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Java is not able to launch links on your computer.",
                    "Cannot Launch Link", JOptionPane.WARNING_MESSAGE);
        }
    }
}
