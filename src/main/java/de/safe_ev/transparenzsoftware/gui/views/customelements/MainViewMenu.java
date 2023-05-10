package de.safe_ev.transparenzsoftware.gui.views.customelements;

import javax.swing.*;

import de.safe_ev.transparenzsoftware.gui.listeners.*;
import de.safe_ev.transparenzsoftware.gui.views.MainView;
import de.safe_ev.transparenzsoftware.i18n.Translator;

import java.awt.event.KeyEvent;

public class MainViewMenu extends JMenuBar {


    private final JMenu fileMenu;

    private final JMenu gotoMenu;
    private final JMenuItem gotoNextItem;
    private final JMenuItem gotoPreviousItem;

    private final JMenu infoMenu;

    public MainViewMenu(MainView mainView) {
        fileMenu = new JMenu(Translator.get("app.view.file"));
        fileMenu.setName("menu.top");

        JMenuItem fileItem = new JMenuItem(Translator.get("app.view.openfile"));
        fileItem.getAccessibleContext().setAccessibleDescription(
                Translator.get("app.view.openfile.description"));
        fileItem.addActionListener(new OpenFileBtnListener(mainView));
        fileItem.setName("menu.file");
        KeyStroke keyO = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
        fileItem.setAccelerator(keyO);
        fileMenu.add(fileItem);

        JMenuItem pasteItem = new JMenuItem(Translator.get("app.view.pastefile"));
        pasteItem.getAccessibleContext().setAccessibleDescription(
                Translator.get("app.view.pastefile.description"));
        pasteItem.addActionListener(new ManualInputBtnListener(mainView));
        pasteItem.setName("menu.paste");
        KeyStroke key1 = KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK);
        pasteItem.setAccelerator(key1);
        fileMenu.add(pasteItem);

        JMenuItem closeItem = new JMenuItem(Translator.get("app.view.exit"));
        closeItem.getAccessibleContext().setAccessibleDescription(
                Translator.get("app.view.exit.description")
        );
        closeItem.addActionListener(new CloseAppListener(mainView));
        KeyStroke keyQ = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK);
        closeItem.setAccelerator(keyQ);
        closeItem.setName("menu.close");
        fileMenu.add(closeItem);

        this.add(fileMenu);

        gotoMenu = new JMenu(Translator.get("app.view.goto"));

        gotoPreviousItem = new JMenuItem(Translator.get("app.view.previous"));
        gotoPreviousItem.addActionListener(new GotoBtnListener(mainView, GotoBtnListener.Direction.PREVIOUS));
        setGotoPreviousItemEnabled(false);
        KeyStroke keyP = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK);
        gotoPreviousItem.setAccelerator(keyP);
        gotoPreviousItem.setName("menu.prev");
        gotoMenu.add(gotoPreviousItem);
        
        gotoNextItem = new JMenuItem(Translator.get("app.view.next"));
        gotoNextItem.addActionListener(new GotoBtnListener(mainView, GotoBtnListener.Direction.NEXT));
        KeyStroke keyN = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);
        gotoNextItem.setAccelerator(keyN);
        gotoNextItem.setName("menu.next");

        setGotoNextItemEnabled(false);
        gotoMenu.add(gotoNextItem);
        this.add(gotoMenu);

        infoMenu = new JMenu(Translator.get("app.view.help"));
        JMenuItem helpItem = new JMenuItem(Translator.get("app.view.help"));
        helpItem.addActionListener(new HelpBtnListener(mainView));
        helpItem.getAccessibleContext().setAccessibleDescription(
                Translator.get("app.view.help.description"));
        helpItem.setName("menu.help");
        infoMenu.add(helpItem);

        JMenuItem aboutItem = new JMenuItem(Translator.get("app.view.about"));
        aboutItem.getAccessibleContext().setAccessibleDescription("app.view.about.description");
        aboutItem.addActionListener(new AboutBtnListener(mainView));
        aboutItem.setName("menu.about");
        infoMenu.add(aboutItem);

        this.add(infoMenu);
    }

    public void setGotoNextItemEnabled(boolean enabled) {
        this.gotoNextItem.setEnabled(enabled);
    }

    public void setGotoPreviousItemEnabled(boolean enabled) {
        this.gotoPreviousItem.setEnabled(enabled);
    }

}
