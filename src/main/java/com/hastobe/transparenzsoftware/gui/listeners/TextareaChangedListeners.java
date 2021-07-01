package com.hastobe.transparenzsoftware.gui.listeners;

import com.hastobe.transparenzsoftware.gui.views.MainView;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Listener which catches changes on a textarea and will
 * perform a resetting of the application state.
 */
public class TextareaChangedListeners implements DocumentListener {

    private final MainView mainView;

    public TextareaChangedListeners(MainView mainView) {
        this.mainView = mainView;
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
        mainView.clearErrorMessages();
        mainView.delayedAutoVerify();
    }
}
