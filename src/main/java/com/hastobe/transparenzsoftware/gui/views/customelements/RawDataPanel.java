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

/**
 * The raw data panel
 *
 */
public class RawDataPanel extends JPanel {

	private final static String TEXT_RAW_DATA = "app.view.dataset";
	private final static String TEXT_PUBLIC_KEY = "app.public.key";
	public static final String PASTE_FROM_CLIPBOARD_ACTION = "paste-from-clipboard";

	private final VerifyTextArea rawDataField;
	private final MainViewErrorPanel errorPanel;
	private final EncodingTypePanel encodingTypePanel;

	public RawDataPanel(MainView mainView) {

		this.setLayout(new BorderLayout(20, 20));

		this.rawDataField = new VerifyTextArea(mainView);
		JScrollPane scrollRawData = new JScrollPane(getRawDataField());
		this.add(scrollRawData, BorderLayout.CENTER);
		// catch the copy event
		Action action = getRawDataField().getActionMap().get(PASTE_FROM_CLIPBOARD_ACTION);
		getRawDataField().getActionMap().put(PASTE_FROM_CLIPBOARD_ACTION, new ProxyAction(action, mainView));
		getRawDataField().setName("text.rawdata");

		ErrorLog eLog = new ErrorLog();
		eLog.setName("lbl.elog");
		errorPanel = new MainViewErrorPanel(eLog);

		this.encodingTypePanel = new EncodingTypePanel(new SelectBoxChangedListener(mainView));
		this.add(encodingTypePanel, BorderLayout.SOUTH);

	}

	public void setErrorMessage(String message) {
		this.add(errorPanel,  BorderLayout.SOUTH);
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
		getRawDataField().setText("");
		getRawDataField().setEnabled(false);
	}

	public String getRawDataContent() {
		return getRawDataField().getText();
	}

	public void cleanUpNoiseInRawData() {
		String data = getRawDataField().getText();
		data = data.replaceAll("\n", " ");
		data = data.replaceAll("\t", " ");
		data = data.trim();
		getRawDataField().setText(data);
	}

	public void fillUpContent(String rawDataContent, EncodingType encoding,
			VerificationType type) {
		if (rawDataContent == null) {
			rawDataContent = "";
		}
		if (encoding == null) {
			encoding = EncodingType.PLAIN;
		}
		if (type == null) {
			type = VerificationType.EDL_40_P;
		}
		encodingTypePanel.setVerificationType(type);
		encodingTypePanel.setEncoding(encoding);
		getRawDataField().setText(rawDataContent);
	}

	public void setEnabledFields(boolean b) {
		this.getRawDataField().setEnabled(b);
	}

	public boolean isErrorMessageSet() {
		return errorPanel.isErrorMessageSet();
	}

	public VerifyTextArea getRawDataField() {
		return rawDataField;
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
}
