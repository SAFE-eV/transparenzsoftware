package com.hastobe.transparenzsoftware.gui.views.customelements;

import com.hastobe.transparenzsoftware.gui.Colors;
import com.hastobe.transparenzsoftware.gui.listeners.ProxyAction;
import com.hastobe.transparenzsoftware.gui.listeners.SelectBoxChangedListener;
import com.hastobe.transparenzsoftware.gui.views.MainView;
import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.VerificationType;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class MainViewCenterPanel extends JPanel {

    private final static String TEXT_RAW_DATA = "app.view.dataset";
    private final static String TEXT_PUBLIC_KEY = "app.public.key";
    public static final String PASTE_FROM_CLIPBOARD_ACTION = "paste-from-clipboard";

    private final VerifyTextArea rawDataField;
    private final EncodingTypePanel encodingTypePanel;
    private final VerifyTextArea publicKeyField;
    private final MainViewErrorPanel errorPanel;
    private final JLabel rawDataLabel;
    private final JLabel publicKeyLabel;
    private Border tfDefaultBorder;

    public MainViewCenterPanel(MainView mainView) {

        this.rawDataLabel = new JLabel(Translator.get(TEXT_RAW_DATA));
        this.setLayout(new GridLayout(0, 1));
        this.add(rawDataLabel);

        this.rawDataField = new VerifyTextArea(mainView);
        JScrollPane scrollRawData = new JScrollPane(rawDataField);
        this.add(scrollRawData);
        //catch the copy event
        Action action = rawDataField.getActionMap().get(PASTE_FROM_CLIPBOARD_ACTION);
        rawDataField.getActionMap().put(PASTE_FROM_CLIPBOARD_ACTION, new ProxyAction(action, mainView));

        this.encodingTypePanel = new EncodingTypePanel(new SelectBoxChangedListener(mainView));
        this.add(encodingTypePanel);

        publicKeyLabel = new JLabel(Translator.get(TEXT_PUBLIC_KEY));
        this.add(publicKeyLabel);

        this.publicKeyField = new VerifyTextArea(mainView);

        JScrollPane scrollPublicKey = new JScrollPane(publicKeyField);
        this.add(scrollPublicKey);
        errorPanel = new MainViewErrorPanel(new ErrorLog());
    }

    public void setErrorMessage(String message) {
        this.add(errorPanel);
        errorPanel.setErrorText(message, false);
    }

    public void setWarningMessage(String message) {
        this.add(errorPanel);
        errorPanel.setErrorText(message, true);
    }

    public void clearErrorMessage() {
        this.remove(errorPanel);
        errorPanel.setErrorText("", true);
        this.repaint();
        this.revalidate();
        this.updateUI();
    }

    public void clearInputs() {
        rawDataField.setText("");
        publicKeyField.setText("");
        publicKeyField.setEnabled(true);
        rawDataField.setEnabled(false);
    }

    public String getRawDataContent() {
        return rawDataField.getText();
    }

    public void cleanUpNoiseInRawData(){
        String data = rawDataField.getText();
        data = data.replaceAll("\n", " ");
        data = data.replaceAll("\t", " ");
        data = data.trim();
        rawDataField.setText(data);
    }

    public String getPublicKeyContent() {
        return publicKeyField.getText();
    }

    public void fillUpContent(String rawDataContent, String publicKeyContent, EncodingType encoding, VerificationType type) {
        if (rawDataContent == null) {
            rawDataContent = "";
        }
        if (publicKeyContent == null) {
            publicKeyContent = "";
        }
        if (encoding == null) {
            encoding = EncodingType.PLAIN;
        }
        if (type == null) {
            type = VerificationType.EDL_40_P;
        }
        rawDataField.setText(rawDataContent);
        publicKeyField.setText(publicKeyContent);
        tfDefaultBorder = publicKeyField.getBorder();
        encodingTypePanel.setVerificationType(type);
        encodingTypePanel.setEncoding(encoding);
    }

    public VerificationType getVerificationType() {
        return encodingTypePanel.getVerificationType();
    }

    public EncodingType getEncoding() {
        return encodingTypePanel.getEncoding();
    }

    public void setEncoding(EncodingType encodingType) {
        encodingTypePanel.setEncoding(encodingType);
    }

    public void setVerificationType(VerificationType verificationType) {
        encodingTypePanel.setVerificationType(verificationType);
    }

    public void setPublicKey(String parsePublicKey) {
        publicKeyField.setText(parsePublicKey);
    }

    public void setPublicKeyWarning(boolean warn) {
        if(warn) {
            publicKeyField.setBorder(BorderFactory.createLineBorder(Colors.WARNING_LOG));
        } else {
            publicKeyField.setBorder(tfDefaultBorder);
        }
    }

    public void setEnabledFields(boolean b) {
        this.publicKeyField.setEnabled(b);
        this.encodingTypePanel.setEnabledFields(b);
        this.rawDataField.setEnabled(b);
    }

    public boolean isErrorMessageSet() {
        return errorPanel.isErrorMessageSet();
    }
}
