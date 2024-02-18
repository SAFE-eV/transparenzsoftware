package de.safe_ev.transparenzsoftware.gui.views.customelements;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.safe_ev.transparenzsoftware.gui.Colors;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.result.Error;

public class ErrorLog extends JLabel {

    public ErrorLog() {
	this.setBackground(Colors.ERROR_LOG);
	this.setForeground(Color.WHITE);
	this.setHorizontalAlignment(SwingConstants.LEFT);
	this.setOpaque(true);
    }

    public void setText(java.util.List<Error> errors) {
	final StringBuilder messageBuilder = new StringBuilder();
	if (errors.size() == 1) {
	    String translation = Translator.get(errors.get(0).getLocalizedMessageCode(), null);
	    if (translation == null) {
		translation = errors.get(0).getMessage();
	    }
	    messageBuilder.append("<html><body><p>").append(translation).append("</p></body></html>");
	} else {
	    // show a list if more than 1 error message
	    messageBuilder.append("<html><body><ul>");
	    for (final Error error : errors) {
		String translation = Translator.get(error.getLocalizedMessageCode(), null);
		if (translation == null) {
		    translation = error.getMessage();
		}
		messageBuilder.append("<li>").append(translation).append("</li>");
	    }
	    messageBuilder.append("</ul></body></html>");
	}
	setText(messageBuilder.toString(), true);
    }

    public void setText(String s, boolean warning) {
	if (warning) {
	    this.setBackground(Colors.WARNING_LOG);
	} else {
	    this.setBackground(Colors.ERROR_LOG);
	}
	super.setText(s);
    }
}
