package de.safe_ev.transparenzsoftware.gui.listeners;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.safe_ev.transparenzsoftware.gui.views.MainView;

/**
 * Listener which catches changes on a textarea and will
 * perform a resetting of the application state.
 */
public class TextareaChangedListeners implements DocumentListener {

    private final MainView mainView;
    private final AtomicBoolean eventsEnabled;
    public TextareaChangedListeners(MainView mainView, AtomicBoolean eventsEnabled) {
        this.mainView = mainView;
        this.eventsEnabled = eventsEnabled;
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        changedTriggered();
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        changedTriggered();
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        changedTriggered();
    }

    private void changedTriggered() {
    	if (eventsEnabled.get()) {
    		mainView.clearErrorMessages();
    		mainView.delayedAutoVerify();
    	}
    }
}
