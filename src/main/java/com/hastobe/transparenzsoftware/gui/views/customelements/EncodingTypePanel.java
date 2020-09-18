package com.hastobe.transparenzsoftware.gui.views.customelements;

import com.hastobe.transparenzsoftware.gui.Colors;
import com.hastobe.transparenzsoftware.gui.listeners.SelectBoxChangedListener;
import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.VerificationType;

import javax.swing.*;
import java.awt.*;

public class EncodingTypePanel extends JPanel {

    private final static String TEXT_ENCODING = "app.view.encoding";
    private final static String TEXT_TYPE = "app.view.format";

    private final JComboBox chooserEncoding;
    private final JLabel labelEncoding;
    private final JLabel labelType;
    private final JComboBox chooserType;

    public EncodingTypePanel(SelectBoxChangedListener selectBoxChangedListener) {
        super();
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        labelEncoding = new JLabel(Translator.get(TEXT_ENCODING));
        labelEncoding.setForeground(Colors.ENCODING_LABEL);

//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.weightx = 1;
//        this.add(labelEncoding, gbc);

        chooserEncoding = new JComboBox();
//        chooserEncoding.setModel(new DefaultComboBoxModel(EncodingType.values()));
//        chooserEncoding.setSelectedItem(EncodingType.BASE64);
//        chooserEncoding.addActionListener (selectBoxChangedListener);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.ipady = 0;
//        gbc.weighty = 1.0;
//        gbc.gridx = 2;
//        gbc.gridwidth = 1;
//        gbc.gridy = 0;
//        this.add(chooserEncoding, gbc);

        labelType = new JLabel(Translator.get(TEXT_TYPE));
        labelType.setForeground(Colors.TYPE_LABEL);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        this.add(labelType, gbc);

        chooserType = new JComboBox();
        chooserType.setModel(new DefaultComboBoxModel(VerificationType.values()));
        chooserType.setSelectedItem(VerificationType.ISA_EDL_40_P);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 0;
        gbc.weighty = 1.0;
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        this.add(chooserType, gbc);
    }

    public void setEncoding(EncodingType encoding) {
        this.chooserEncoding.setSelectedItem(encoding);
    }

    public void setVerificationType(VerificationType type) {
        this.chooserType.setSelectedItem(type);
    }

    public VerificationType getVerificationType() {
        return (VerificationType) this.chooserType.getSelectedItem();
    }

    public EncodingType getEncoding() {
        return (EncodingType) this.chooserEncoding.getSelectedItem();
    }

    public void setEnabledFields(boolean b) {
        this.chooserEncoding.setEnabled(b);
        this.chooserType.setEnabled(b);
    }
}
