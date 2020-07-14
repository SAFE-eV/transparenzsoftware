package com.hastobe.transparenzsoftware.gui.listeners;

import com.hastobe.transparenzsoftware.gui.views.MainView;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Proxy action to capture paste events on text areas
 * https://stackoverflow.com/questions/25276020/listen-to-the-paste-events-jtextarea
 */
public class ProxyAction extends AbstractAction {

    private final Action action;
    private final MainView mainView;

    public ProxyAction(Action action, MainView mainView) {
        this.action = action;
        this.mainView = mainView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        action.actionPerformed(e);
        mainView.onPaste();
    }

}
