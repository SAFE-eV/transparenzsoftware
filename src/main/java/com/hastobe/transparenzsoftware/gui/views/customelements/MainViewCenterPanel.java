package com.hastobe.transparenzsoftware.gui.views.customelements;

import com.hastobe.transparenzsoftware.gui.Colors;
import com.hastobe.transparenzsoftware.gui.listeners.ProxyAction;
import com.hastobe.transparenzsoftware.gui.listeners.SelectBoxChangedListener;
import com.hastobe.transparenzsoftware.gui.views.DetailDataView;
import com.hastobe.transparenzsoftware.gui.views.MainView;
import com.hastobe.transparenzsoftware.gui.views.VerifyDataView;
import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.VerificationType;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class MainViewCenterPanel extends JPanel {

    private final static String TEXT_USER_DATA = "app.view.userdata";
    private final static String TEXT_RAW_DATA = "app.view.dataset";
    private final static String TEXT_DETAIL_DATA = "app.view.datadetails";
    public static final String PASTE_FROM_CLIPBOARD_ACTION = "paste-from-clipboard";

    private final MainViewErrorPanel errorPanel;
    private Border tfDefaultBorder;

    private final JTabbedPane tabPane;
    private final JPanel lowerPanel;
    
    private final RawDataPanel raw;
	private final VerifyDataView verifyDataView;
	private final DetailDataView detailDataView;
    
    public MainViewCenterPanel(MainView mainView) {
    	tabPane = new JTabbedPane();
    	
    	this.verifyDataView = new VerifyDataView(mainView);
    	tabPane.addTab(Translator.get(TEXT_USER_DATA),verifyDataView);
    	
    	this.detailDataView = new DetailDataView(mainView);
    	tabPane.addTab(Translator.get(TEXT_DETAIL_DATA),detailDataView);
    	
    	this.raw = new RawDataPanel(mainView);
    	tabPane.addTab(Translator.get(TEXT_RAW_DATA),raw);

    	lowerPanel = new JPanel();
    	
        this.setLayout(new BorderLayout());

        ErrorLog eLog = new ErrorLog();
        eLog.setName("lbl.elog");
        errorPanel = new MainViewErrorPanel(eLog);
        
    	this.add(tabPane,BorderLayout.CENTER);
    	this.add(lowerPanel,BorderLayout.SOUTH);
        
    }

    public void setErrorMessage(String message) {
        lowerPanel.add(errorPanel);
        errorPanel.setErrorText(message, false);
    }

    public void setWarningMessage(String message) {
        this.add(errorPanel);
        errorPanel.setErrorText(message, true);
    }

    public void clearErrorMessage() {
        this.remove(errorPanel);
        errorPanel.setErrorText("", true);
        verifyDataView.clearErrorMessage();
        detailDataView.clearErrorMessage();
        this.repaint();
        this.revalidate();
        this.updateUI();
    }

    public void clearInputs() {
    	raw.clearInputs();
    	verifyDataView.clearInputs();
    }

    public String getRawDataContent() {
        return raw.getRawDataContent();
    }

    public void cleanUpNoiseInRawData(){
    	raw.cleanUpNoiseInRawData();
    }

    public String getPublicKeyContent() {
        return verifyDataView.getPublicKeyField().getText();
    }

    public void fillUpContent(String rawDataContent, String publicKeyContent, EncodingType encoding, VerificationType type) {
        raw.fillUpContent(rawDataContent, encoding, type);
        verifyDataView.fillUpContent(publicKeyContent);
    }

    public VerificationType getVerificationType() {
        return raw.getVerificationType();
    }

    public EncodingType getEncoding() {
        return raw.getEncoding();
    }

    public void setEncoding(EncodingType encodingType) {
        raw.setEncoding(encodingType);
    }

    public void setVerificationType(VerificationType verificationType) {
        raw.setVerificationType(verificationType);
    }

    public void setPublicKey(String parsePublicKey) {
    	verifyDataView.setPublicKey(parsePublicKey);
    }

    public void setPublicKeyWarning(boolean warn) {
    	verifyDataView.setPublicKeyWarning(warn);
    }

    public void setEnabledFields(boolean b) {
    	raw.setEnabled(b);
    	verifyDataView.setEnabled(b);
    	
    }

    public boolean isErrorMessageSet() {
        return errorPanel.isErrorMessageSet();
    }

	public void setVerificationContent(VerificationResult verificationResult) {
		verifyDataView.setState(verificationResult);
		detailDataView.setAdditionalData(verificationResult.getAdditionalVerificationData());
	}
}
