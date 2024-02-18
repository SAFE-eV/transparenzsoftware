package de.safe_ev.transparenzsoftware.gui.views.customelements;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.*;

import de.safe_ev.transparenzsoftware.gui.listeners.TextareaChangedListeners;
import de.safe_ev.transparenzsoftware.gui.views.MainView;

public class VerifyTextArea extends JTextArea {

	public VerifyTextArea(MainView mainView, AtomicBoolean eventsEnabled) {
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
        this.getDocument().addDocumentListener(new TextareaChangedListeners(mainView, eventsEnabled));
    }
}
