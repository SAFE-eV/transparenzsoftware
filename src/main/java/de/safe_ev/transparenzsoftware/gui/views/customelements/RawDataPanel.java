package de.safe_ev.transparenzsoftware.gui.views.customelements;

import javax.swing.*;
import javax.swing.border.Border;

import de.safe_ev.transparenzsoftware.gui.Colors;
import de.safe_ev.transparenzsoftware.gui.listeners.SelectBoxChangedListener;
import de.safe_ev.transparenzsoftware.gui.views.MainView;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.VerificationType;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
	private AtomicBoolean eventsEnabled = new AtomicBoolean();
	public RawDataPanel(MainView mainView) {

		this.setLayout(new BorderLayout(20, 20));

		this.rawDataField = new VerifyTextArea(mainView,eventsEnabled);
		JScrollPane scrollRawData = new JScrollPane(getRawDataField());
		this.add(scrollRawData, BorderLayout.CENTER);
		getRawDataField().setName("text.rawdata");

		ErrorLog eLog = new ErrorLog();
		eLog.setName("lbl.elog");
		errorPanel = new MainViewErrorPanel(eLog);

		this.encodingTypePanel = new EncodingTypePanel(new SelectBoxChangedListener(mainView));
		this.add(encodingTypePanel, BorderLayout.SOUTH);
		getRawDataField().setEnabled(false);

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
