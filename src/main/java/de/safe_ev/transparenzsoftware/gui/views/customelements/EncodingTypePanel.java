package de.safe_ev.transparenzsoftware.gui.views.customelements;

import javax.swing.*;

import de.safe_ev.transparenzsoftware.gui.Colors;
import de.safe_ev.transparenzsoftware.gui.listeners.SelectBoxChangedListener;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.VerificationType;

import java.awt.*;
import java.util.Arrays;

public class EncodingTypePanel extends JPanel {

    private final static String TEXT_ENCODING = "app.view.encoding";
    private final static String TEXT_TYPE = "app.view.format";

    private final JComboBox chooserEncoding;
    private final JLabel labelEncoding;
    private final JLabel labelType;
    private final JLabel chooserType;

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

        chooserType = new JLabel();
        setType();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 0;
        gbc.weighty = 1.0;
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        this.add(chooserType, gbc);
    }
    
    VerificationType actualType = VerificationType.UNKNOWN;

    public void setEncoding(EncodingType encoding) {
        this.chooserEncoding.setSelectedItem(encoding);
    }

    public void setVerificationType(VerificationType type) {
        this.actualType = type;
        setType();
    }

    private void setType() {
    	switch (actualType) {
		case ALFEN:
		case EDL_40_MENNEKES:
		case EDL_40_P:
		case EDL_40_SIG:
		case ISA_EDL_40_P:
		case OCMF:
		case PCDF:
			chooserType.setText(actualType.name());
			break;
		default:
		case UNKNOWN:
			chooserType.setText(Translator.get("error.format.unknown"));
			break;
    	
    	}
	}

	public VerificationType getVerificationType() {
        return actualType;
    }

    public EncodingType getEncoding() {
        return (EncodingType) this.chooserEncoding.getSelectedItem();
    }

    public void setEnabledFields(boolean b) {
        this.chooserEncoding.setEnabled(b);
        this.chooserType.setEnabled(b);
    }
}
