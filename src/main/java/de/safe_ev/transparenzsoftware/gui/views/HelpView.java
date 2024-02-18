package de.safe_ev.transparenzsoftware.gui.views;

import javax.swing.*;

import de.safe_ev.transparenzsoftware.gui.views.customelements.LinkLabel;
import de.safe_ev.transparenzsoftware.i18n.Translator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Helpview which contains a link to the help page
 */
public class HelpView extends JFrame {

    public HelpView(MainView mainView) {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(Box.createRigidArea(new Dimension(0, 10)));


        add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel text = new JLabel(Translator.get("app.view.help.link.at"));
        text.setAlignmentX(0.5f);
        add(text);
        add(Box.createRigidArea(new Dimension(0, 10)));

        LinkLabel helpText = new LinkLabel("https://transparenz.software/", "https://transparenz.software/");
        helpText.setAlignmentX(0.5f);
        add(helpText);
        add(Box.createRigidArea(new Dimension(0, 10)));

        JButton close = new JButton(Translator.get("app.view.close"));
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mainView.onHelpClose();
            }
        });

        close.setAlignmentX(0.5f);
        add(close);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(300, 200);


        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mainView.onHelpClose();
            }
        });
    }


}
