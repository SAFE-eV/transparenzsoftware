package de.safe_ev.transparenzsoftware.gui.views;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.Constants;
import de.safe_ev.transparenzsoftware.i18n.Translator;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
    	this.setName("dialog.input");
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
        taData.setName("paste.data");
        taData.setLineWrap(true);
        inner.add(taData);
        inner.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel lblKey = new JLabel(Translator.get("app.view.datain.key"));
        inner.add(lblKey);

        inner.add(Box.createRigidArea(new Dimension(0, 2)));
        
        JTextArea taKey = new JTextArea(5,80);
        taKey.setLineWrap(true);
        taKey.setName("paste.key");
        inner.add(taKey);
        inner.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonBox = new JPanel();
        
        JButton close = new JButton(Translator.get("app.view.verify"));
        close.setName("paste.close");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mainView.onPaste(taData.getText(),taKey.getText());
                dialogClose();
            }
        });
        close.setDefaultCapable(true);
        close.setAlignmentX(0.5f);

        JButton abort = new JButton(Translator.get("app.view.verify.abort"));
        abort.setName("paste.abort");
        abort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dialogClose();
            }
        });
        abort.setAlignmentX(0.5f);

        buttonBox.add(abort);
        buttonBox.add(Box.createRigidArea(new Dimension(0, 40)));
        buttonBox.add(close);
        
        buttonBox.setAlignmentX(0.5f);
        inner.add(buttonBox);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            }
        });
        taData.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dialogClose();
				}
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
