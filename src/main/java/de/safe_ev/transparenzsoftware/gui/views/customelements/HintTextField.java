package de.safe_ev.transparenzsoftware.gui.views.customelements;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.safe_ev.transparenzsoftware.gui.listeners.TextareaChangedListeners;
import de.safe_ev.transparenzsoftware.gui.views.MainView;

/**
 * A text field showing a hint inside the textfield.
 *
 */
public class HintTextField extends JTextArea {

	/**
	* 
	*/
	private static final long serialVersionUID = 6239343587111979420L;

	private String hint;

	public HintTextField(MainView mainView, AtomicBoolean eventsEnabled) {
		this.hint = "";
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
        this.getDocument().addDocumentListener(new TextareaChangedListeners(mainView, eventsEnabled));
	}

	public void setHint(String hint) {
		this.hint = hint;
		this.repaint();
	}

	@Override
    public void paint(Graphics g) {
        super.paint(g);
        if (getText().length() == 0) {
            int h = getHeight();
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int c0 = getBackground().getRGB();
            int c1 = getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
            g.setColor(new Color(c2, true));
            g.drawString(hint, ins.left, h / 2 + fm.getAscent() / 2 - 2);
        }
    }

}