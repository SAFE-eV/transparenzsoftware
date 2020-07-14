package com.hastobe.transparenzsoftware.gui.views.customelements;

import com.hastobe.transparenzsoftware.gui.listeners.TextareaChangedListeners;
import com.hastobe.transparenzsoftware.gui.views.MainView;

import javax.swing.*;

public class VerifyTextArea extends JTextArea {

    public VerifyTextArea(MainView mainView) {
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
        this.getDocument().addDocumentListener(new TextareaChangedListeners(mainView));
    }
}
