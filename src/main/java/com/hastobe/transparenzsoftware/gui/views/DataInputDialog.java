package com.hastobe.transparenzsoftware.gui.views;

import com.hastobe.transparenzsoftware.Constants;
import com.hastobe.transparenzsoftware.i18n.Translator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

/**
 *  Input dialog for public key and raw data.
 */
public class DataInputDialog extends JDialog {
    private final static Logger LOGGER = LogManager.getLogger(DataInputDialog.class);

    public DataInputDialog(MainView mainView) {
    	//app.view.datainput=Dateneingabe
    	//app.view.datain.txt=Rohdaten
    	//		app.view.datain.key=Ãffentlicher SchlÃ¼ssel
    	setSize(mainView.getWidth(),mainView.getHeight());
    	JPanel inner = (JPanel)getContentPane();
        setTitle(Translator.get("app.view.datainput"));
        setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
		int innerMargin = 5;
    	inner.setSize(mainView.getWidth(),mainView.getHeight());
		Border b = BorderFactory.createEmptyBorder(innerMargin, innerMargin, innerMargin, innerMargin);

        inner.setBorder(b);
        inner.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblTxt = new JLabel(Translator.get("app.view.datain.txt"));
        inner.add(lblTxt);

        inner.add(Box.createRigidArea(new Dimension(0, 2)));
        
        JTextArea taData = new JTextArea(5,80);
        inner.add(taData);
        inner.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel lblKey = new JLabel(Translator.get("app.view.datain.key"));
        inner.add(lblKey);

        inner.add(Box.createRigidArea(new Dimension(0, 2)));
        
        JTextArea taKey = new JTextArea(5,80);
        inner.add(taKey);
        inner.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton close = new JButton(Translator.get("app.view.verify"));
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mainView.onPaste(taData.getText(),taKey.getText());
                dialogClose();
            }
        });

        close.setAlignmentX(0.5f);
        inner.add(close);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            }
        });
        this.setModal(true);
        this.setVisible(true);
        
    }

    void dialogClose()
    {
    	this.setVisible(false);
    }
}
