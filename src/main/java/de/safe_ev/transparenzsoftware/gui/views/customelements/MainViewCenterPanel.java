package de.safe_ev.transparenzsoftware.gui.views.customelements;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import de.safe_ev.transparenzsoftware.gui.views.DetailDataView;
import de.safe_ev.transparenzsoftware.gui.views.MainView;
import de.safe_ev.transparenzsoftware.gui.views.VerifyDataView;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;

public class MainViewCenterPanel extends JPanel {

    private final static String TEXT_USER_DATA = "app.view.userdata";
    private final static String TEXT_RAW_DATA = "app.view.dataset";
    private final static String TEXT_DETAIL_DATA = "app.view.datadetails";
    public static final String PASTE_FROM_CLIPBOARD_ACTION = "paste-from-clipboard";

    private final MainViewErrorPanel errorPanel;
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
    
    public void showFirstPane()
    {
    	this.tabPane.setSelectedIndex(0);
    }

    public void setErrorMessage(String message) {
    	lowerPanel.removeAll();
        lowerPanel.add(errorPanel);
        errorPanel.setErrorText(message, false);
    }

    public void setWarningMessage(String message) {
    	lowerPanel.removeAll();
        lowerPanel.add(errorPanel);
        errorPanel.setErrorText(message, false);
    }

    public void clearErrorMessage() {
    	lowerPanel.removeAll();
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
        return verifyDataView.getPublicKeyContent();
    }

    public void fillUpContent(String rawDataContent, String publicKeyContent, boolean indeterminate, EncodingType encoding, VerificationType type) {
        raw.fillUpContent(rawDataContent, encoding, type);
        verifyDataView.fillUpContent(publicKeyContent, indeterminate);
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

    /**
     * Called at the end of data input to let the user change the data
     * @param b
     */
    public void setEnabledFields(boolean b) {
    	raw.setEnabled(false);
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
