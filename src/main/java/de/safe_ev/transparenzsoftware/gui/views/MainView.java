package de.safe_ev.transparenzsoftware.gui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.safe_ev.transparenzsoftware.Constants;
import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.gui.listeners.GotoBtnListener;
import de.safe_ev.transparenzsoftware.gui.views.customelements.MainViewBottomPanel;
import de.safe_ev.transparenzsoftware.gui.views.customelements.MainViewCenterPanel;
import de.safe_ev.transparenzsoftware.gui.views.customelements.MainViewMenu;
import de.safe_ev.transparenzsoftware.gui.views.customelements.MainViewNorthPanel;
import de.safe_ev.transparenzsoftware.gui.views.customelements.MainViewWestPanel;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.ContainedPublicKeyParser;
import de.safe_ev.transparenzsoftware.verification.DecodingException;
import de.safe_ev.transparenzsoftware.verification.EncodingType;
import de.safe_ev.transparenzsoftware.verification.TransactionValidationException;
import de.safe_ev.transparenzsoftware.verification.ValidationException;
import de.safe_ev.transparenzsoftware.verification.VerificationParser;
import de.safe_ev.transparenzsoftware.verification.VerificationParserFactory;
import de.safe_ev.transparenzsoftware.verification.VerificationType;
import de.safe_ev.transparenzsoftware.verification.VerificationTypeNotImplementedException;
import de.safe_ev.transparenzsoftware.verification.Verifier;
import de.safe_ev.transparenzsoftware.verification.format.pcdf.PcdfReader;
import de.safe_ev.transparenzsoftware.verification.format.pcdf.PcdfVerificationParser;
import de.safe_ev.transparenzsoftware.verification.input.InputReader;
import de.safe_ev.transparenzsoftware.verification.input.InvalidInputException;
import de.safe_ev.transparenzsoftware.verification.result.Error;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;
import de.safe_ev.transparenzsoftware.verification.xml.EncodedData;
import de.safe_ev.transparenzsoftware.verification.xml.PublicKey;
import de.safe_ev.transparenzsoftware.verification.xml.SignedData;
import de.safe_ev.transparenzsoftware.verification.xml.Value;
import de.safe_ev.transparenzsoftware.verification.xml.Values;

public class MainView extends JFrame {

	private final static long serialVersionUID = 1L;
	private final static String TEXT_WINDOW_TITEL = "app.title";
	private final static Logger LOGGER = LogManager.getLogger(MainView.class);
	private static final int VERIFY_DELAY = 500;
	private static MainView instance;

	private final VerificationParserFactory factory;
	private final Verifier verifier;

	private JPanel northPanel = null;
	private MainViewCenterPanel centerPanel = null;
	private MainViewBottomPanel southPanel = null;
	private MainViewWestPanel westPanel = null;
	private HelpView helpView;
	private AboutView aboutView;
	private MainViewMenu menuBar;

	private Values values;
	private int currentValuePos;
	private final Timer delayVerifyTimer;
	private boolean verifyMode;
	private boolean publicKeyIsIndeterminate;

	public MainView(VerificationParserFactory factory) {
		this.factory = factory;
		verifier = new Verifier(factory);
		currentValuePos = 0;
		initPage();
		delayVerifyTimer = new Timer(VERIFY_DELAY, e -> verify());
		delayVerifyTimer.setRepeats(false);
	}

	private void verify() {
		try {
			verify_();
		} finally {
			centerPanel.setEnabledFields(true);
		}
	}

	public static MainView init(VerificationParserFactory factory) {
		instance = new MainView(factory);
		return instance;
	}

	private void initPage() {
		this.setLayout(new BorderLayout(10, 10));
		this.setTitle(String.format("%s - Version: %s", Translator.get(TEXT_WINDOW_TITEL), Constants.VERSION));

		// now create the panels
		northPanel = new MainViewNorthPanel(this);
		centerPanel = new MainViewCenterPanel(this);
		southPanel = new MainViewBottomPanel(this);
		westPanel = new MainViewWestPanel(this);

		// set top menu
		menuBar = new MainViewMenu(this);
		this.setJMenuBar(menuBar);
		this.add(northPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);
		this.add(westPanel, BorderLayout.WEST);

		pack();
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().requestFocusInWindow();
		// fix to resize left panel
		westPanel.initView();
	}

	public void showFirstPane() {
		centerPanel.showFirstPane();
	}

	/**
	 * Clears the error messages of the view
	 */
	public void clearErrorMessages() {
		centerPanel.clearErrorMessage();
	}

	/**
	 * Enables the verify button
	 *
	 * @param enable if true will be enable otherwise disabled
	 */
	public void setEnableVerifyButton(boolean enable) {
		centerPanel.setEnabledFields(enable);
	}

	public void setEnableVerifyMode(boolean single) {
		verifyMode = single;
		centerPanel.setEnabledFields(single);
	}

	/**
	 * Loads the content of the fields and tries to verify it
	 */
	private void verify_() {
		setEnableVerifyButton(false);
		// only use he first value
		VerificationParser parser;
		clearErrorMessages();

		String publicKeyContent = centerPanel.getPublicKeyContent().trim();
		if (publicKeyContent.isEmpty() && !centerPanel.getVerificationType().isPublicKeyAware()) {
			if (publicKeyIsIndeterminate) {
				LOGGER.error("Public key is inderminate");
				setErrorMessage(Translator.get("error.values.ind.publickey"));
			} else {
				LOGGER.error("Empty public key field");
				setErrorMessage(Translator.get("error.values.no.publickey"));
			}
			return;
		}

		final String rawDataContent = centerPanel.getRawDataContent().trim();
		if (rawDataContent.isEmpty()) {
			LOGGER.error("Empty data field");
			setErrorMessage(Translator.get("error.values.no.signeddata"));
			return;
		}
		try {
			parser = factory.getParser(centerPanel.getVerificationType());
		} catch (final VerificationTypeNotImplementedException e) {
			setErrorMessage(Translator.get("error.format.unknown"));
			// we have not found a parser skip the rest
			return;
		}
		if (!verifyMode) {
			return;
		}

		VerificationResult verificationResult = null;
		try {
			if (westPanel.getCurrentTransactionid() == null) {
				if (publicKeyContent.trim().isEmpty()) {
					verificationResult = verifier.verifyUnknown(parser, rawDataContent, null);
				} else {
					publicKeyContent = publicKeyContent.replaceAll(" ", "");
					verificationResult = verifier.verifyUnknown(parser, rawDataContent, publicKeyContent);
				}
			} else {
				final List<Value> values = westPanel.getValues(westPanel.getCurrentTransactionid());
				verificationResult = verifier.verifyTransaction(parser, values, publicKeyContent);
			}
		} catch (final TransactionValidationException e) {
			LOGGER.error(String.format("TransactionValidation exception happened: %s", e.getMessage()));
			setErrorMessage(e.getLocalizedMessage());
			setEnableVerifyButton(true);
			return;
		} catch (final Exception e) {
			// we do not want our application to crash
			setEnableVerifyButton(true);
			setErrorMessage(Translator.get("app.view.error.generic"));
			LOGGER.error("Unhandled error", e);
			return;
		}
		setEnableVerifyButton(true);
		// make sure we have no nullpointer if so something was fishy here, lets show an
		// error because
		// we cant get forward
		if (verificationResult == null) {
			setErrorMessage(Translator.get("app.view.error.generic"));
			return;
		}

		if (verificationResult.containsErrorOfType(Error.Type.VALIDATION)
				|| verificationResult.containsErrorOfType(Error.Type.INPUT)) {
			final String errmsg = verificationResult.getErrorMessages().size() > 0
					? verificationResult.getErrorMessages().get(0).getLocalizedMessageCode()
					: "";
			LOGGER.error(String.format("Validation error %s", errmsg));
			final List<String> errorMessages = new ArrayList<>();
			for (final Error errorMessage : verificationResult.getErrorMessages()) {
				if (!errorMessage.getType().equals(Error.Type.VERIFICATION)) {
					errorMessages.add(errorMessage.getLocalizedMessage());
					break;
				}
			}
			if (!errorMessages.isEmpty()) {
				final StringBuilder messageBuilder = new StringBuilder();
				if (errorMessages.size() == 1) {
					messageBuilder.append("<html><body><p>").append(errorMessages.get(0)).append("</p></body></html>");
				} else {
					// show a list if more than 1 error message
					messageBuilder.append("<html><body><ul>");
					for (final String errorMessage : errorMessages) {
						messageBuilder.append("<li>").append(errorMessage).append("</li>");
					}
					messageBuilder.append("</ul></body></html>");
				}
				setErrorMessage(messageBuilder.toString());
			}
		} else {
			centerPanel.setVerificationContent(verificationResult);
		}
	}

	/**
	 * Triggered on file open tries to verify the file and set the input fields
	 *
	 * @param filename - path of the file which was choosen
	 */
	public void onFileOpen(String filename) {
		// cleanup state
		LOGGER.info(String.format("Try to open file %s", filename));
		clearState();
		showFirstPane();

		if (filename.indexOf(".pcdf") != -1) {
			// this is a Porsche Charging Data File, parse in a different way
			final PcdfReader pcdfReader = new PcdfReader();
			try {
				values = pcdfReader.readPcdfFile(filename);
			} catch (final ValidationException e) {
				LOGGER.error("Validation error in file", e);
				final String localizedMessage = e.getLocalizedMessage();
				setErrorMessage(localizedMessage);
				return;
			} catch (final InvalidInputException e) {
				LOGGER.error("Error on reading file", e);
				final String localizedMessage = e.getLocalizedMessage();
				setErrorMessage(localizedMessage);
				return;
			}
		} else {
			final File xmlFile = new File(filename);
			try {
				final InputReader inputReader = new InputReader();
				values = inputReader.readFile(xmlFile);
			} catch (final InvalidInputException exception) {
				LOGGER.error("Error on reading file", exception);
				final String localizedMessage = exception.getLocalizedMessage();
				setErrorMessage(localizedMessage);
				return;
			}
		}
		onValuesRead(values);
		centerPanel.setEnabledFields(true);
	}

	public void stepToValueWithKeyCheck(int index) {
		// Check, if all public keys are the same:
		publicKeyIsIndeterminate = false;
		if (values.getValues().size() > 1) {
			final HashSet<String> keys = new HashSet<>();
			for (final Value v : values.getValues()) {
				keys.add(v.getPublicKey().getValue());
			}
			publicKeyIsIndeterminate = keys.size() > 1;
		}
		stepToValue_(index);
	}

	public void stepToValue(int index) {
		publicKeyIsIndeterminate = false;
		stepToValue_(index);
	}

	/**
	 * Changes to the view to the according element of index of the values
	 *
	 * @param index page nr
	 */
	private void stepToValue_(int index) {
		currentValuePos = index;
		setEnableVerifyButton(false);

		// validate will throw an error so the first one will have an error anyway
		final Value firstValue = values.getValues().get(currentValuePos);
		// set the view fields
		final SignedData signedData = firstValue.getSignedData();
		final PublicKey publicKey = firstValue.getPublicKey();
		try {
			String publicKeyContent;
			if (publicKey != null) {
				publicKeyContent = publicKey.getValue();
			} else {
				publicKeyContent = tryFetchingEmbeddedKey(signedData, signedData.getFormatAsVerificationType());
			}

			final List<EncodingType> encodingTypes = EncodingType.guessType(publicKeyContent);
			if (encodingTypes.size() == 1) {
				final EncodingType type = encodingTypes.get(0);
				switch (type) {
				case BASE64:
					publicKeyContent = Utils.toFormattedHex(EncodingType.base64Decode(publicKeyContent));
					break;
				case HEX:
					publicKeyContent = Utils.splitStringToGroups(publicKeyContent, 2);
					break;
				}
			}
			if (publicKeyIsIndeterminate) {
				publicKeyContent = "";
			}
			if (signedData == null) {
				final EncodedData encodedData = firstValue.getEncodedData();
				centerPanel.fillUpContent(encodedData.getValue(), publicKeyContent, publicKeyIsIndeterminate,
						encodedData.getEncodingType(), encodedData.getFormatAsVerificationType());
			} else {
				centerPanel.fillUpContent(signedData.getValue(), publicKeyContent, publicKeyIsIndeterminate,
						signedData.getEncodingType(), signedData.getFormatAsVerificationType());
			}
			// auto
			delayedAutoVerify();

		} catch (final DecodingException exception) {
			LOGGER.error("Error on reading file", exception);
			final String localizedMessage = exception.getLocalizedMessage();
			setErrorMessage(localizedMessage);
		}
		// enable for verify
		setEnableVerifyButton(true);
		if (firstValue.getSignedData() != null) {
			if (firstValue.getSignedData().getEncoding() == null) {
				final List<EncodingType> types = EncodingType.guessType(firstValue.getSignedData().getValue(), true);
				if (!types.isEmpty()) {
					centerPanel.setEncoding(types.get(0));
				}
			}
			if (firstValue.getSignedData().getFormat() == null) {
				try {
					final List<VerificationParser> verificationParserList = factory
							.getParserWithData(firstValue.getSignedData().getValue());
					final VerificationParser verificationParser = verificationParserList.get(0);
					centerPanel.setVerificationType(verificationParser.getVerificationType());
				} catch (final VerificationTypeNotImplementedException e) {
					LOGGER.info("No verification type found for data");
				}
			}
		}
		// check if public key might be in the payload data
		checkPublicKeyPayloadData(firstValue);

		if (index + 1 >= values.getValues().size()) {
			menuBar.setGotoNextItemEnabled(false);
		} else if (values.getValues().size() > 1) {
			menuBar.setGotoNextItemEnabled(true);
		}
		if (index <= 0) {
			menuBar.setGotoPreviousItemEnabled(false);
		} else if (values.getValues().size() > 1) {
			menuBar.setGotoPreviousItemEnabled(true);
		}
		southPanel.setPagingCount(index + 1, values.getValues().size());
	}

	/**
	 * Checks if the payload data contains a public key and if it matches to the
	 * entered public key
	 *
	 * @param signedData
	 */
	private void checkPublicKeyPayloadData(Value value) {
		VerificationParser verificationParser = null;
		try {
			verificationParser = factory.getParser(centerPanel.getVerificationType());
			final String publicKeyContent = centerPanel.getPublicKeyContent().trim().isEmpty() ? null
					: centerPanel.getPublicKeyContent();
			final String loaded = verifier.checkForEmbeddedPublicKey(verificationParser, publicKeyContent, value);
			if (loaded != null && !loaded.trim().isEmpty()) {
				centerPanel.setPublicKey(loaded);
			}
		} catch (final VerificationTypeNotImplementedException e) {
			// no op
		} catch (final InvalidInputException e) {
			setWarningMessage(Translator.get("app.view.error.publickeynotmatchdata"));
		}
	}

	/**
	 * Tries to fetch the embedded public key if the parser is aware of
	 *
	 * @param signedData       Data to read out the key
	 * @param verificationType verification type to load the parser
	 * @return public key as string or null
	 */
	public String tryFetchingEmbeddedKey(SignedData signedData, VerificationType verificationType) {
		VerificationParser verificationParser = null;
		String publicKey = null;
		try {
			verificationParser = factory.getParser(verificationType);
			if (verificationParser instanceof ContainedPublicKeyParser) {
				publicKey = ((ContainedPublicKeyParser) verificationParser).parsePublicKey(signedData.getValue());
			}
		} catch (final VerificationTypeNotImplementedException e) {
			// no op
		}
		return publicKey;
	}

	/**
	 * Called when the app is closed via a button
	 */
	public void onClose() {
		this.dispose();
	}

	/**
	 * Clears the input fields and error messages. Sets the verify button to
	 * disabled
	 */
	private void clearState() {
		centerPanel.clearInputs();
		setEnableVerifyButton(false);
		clearErrorMessages();
		menuBar.setGotoNextItemEnabled(false);
		menuBar.setGotoPreviousItemEnabled(false);
		centerPanel.setPublicKeyWarning(false);
		setEnableVerifyMode(true);
		centerPanel.setEnabledFields(true);
		currentValuePos = 0;
		westPanel.initView();
		delayVerifyTimer.stop();
	}

	private void setErrorMessage(String message) {
		centerPanel.setErrorMessage(message);
	}

	private boolean isErrorMessageSet() {
		return centerPanel.isErrorMessageSet();
	}

	private void setWarningMessage(String message) {
		centerPanel.setWarningMessage(message);
		centerPanel.setPublicKeyWarning(true);
	}

	/**
	 * Opens the help window or brings it to the front
	 */
	public void onHelpOpen() {
		if (helpView == null) {
			final HelpView helpView = new HelpView(this);
			configureSupportWindows(helpView);
			this.helpView = helpView;
		} else {
			helpView.toFront();
		}
	}

	/**
	 * called when the help window will be closed
	 */
	public void onHelpClose() {
		if (helpView != null) {
			helpView.dispose();
		}
		helpView = null;
	}

	/**
	 * About window opened or bring to front if open
	 */
	public void onAboutOpen() {
		if (aboutView == null) {
			final AboutView aboutView = new AboutView(this);
			configureSupportWindows(aboutView);
			this.aboutView = aboutView;
		} else {
			aboutView.toFront();
		}
	}

	/**
	 * closes the about window
	 */
	public void onAboutClose() {
		if (aboutView != null) {
			aboutView.dispose();
		}
		aboutView = null;
	}

	/**
	 * Tries to load content of a transparenzsoftware xml files with values input
	 * into the view
	 *
	 * @param values values parsed (either from file or pasted)
	 */
	private void onValuesRead(Values values) {
		this.values = values;
		try {
			values.validate(false);
			stepToValue(currentValuePos);
			if (values.getValues() != null && values.getValues().size() > 1) {
				westPanel.updateTree(values);
				menuBar.setGotoNextItemEnabled(true);
			}
			southPanel.showPaginationCount();
		} catch (final InvalidInputException e) {
			final String localizedMessage = e.getLocalizedMessage();
			setErrorMessage(localizedMessage);
		}
	}

	/**
	 * Listener for a goto page action
	 *
	 * @param direction direction where to go to
	 */
	public void onGoto(GotoBtnListener.Direction direction) {
		final int nrValues = values.getValues() != null ? values.getValues().size() : 0;
		int newPos = currentValuePos;
		switch (direction) {
		case NEXT:
			if (currentValuePos + 1 < nrValues) {
				newPos += 1;
			}
			break;
		case PREVIOUS:
			if (currentValuePos - 1 >= 0) {
				newPos -= 1;
			}
		}
		if (newPos != currentValuePos) {
			stepToValue(newPos);
			westPanel.setSelection(newPos);
		}
	}

	private void configureSupportWindows(JFrame frame) {
		frame.pack();
		final Dimension size = getSize();
		size.setSize(size.getWidth() - 40, size.getHeight() - 40);
		frame.setSize(size);
		frame.setVisible(true);
	}

	private String trimString(String other) {
		other = other.replace('\n', ' ');
		other = other.replace('\r', ' ');
		other = other.trim();
		return other;
	}

	/**
	 * This action is triggered after
	 */
	public void onPaste(String rawData, String pubKey) {
		clearState();
		showFirstPane();
		final String rawDataContent = rawData;

		final InputReader reader = new InputReader();
		Values values;
		try {
			values = reader.readString(rawDataContent);
			onValuesRead(values);
			return;
		} catch (final InvalidInputException e) {
			LOGGER.debug("No values pasted");
		}
		if (rawDataContent.isEmpty()) {
			setErrorMessage(Translator.get("paste.err.empty"));
			return;
		}
		pubKey = trimString(pubKey);
		centerPanel.fillUpContent(rawDataContent, pubKey, false, EncodingType.PLAIN, VerificationType.UNKNOWN);
		// we could not read values so it can be a single value
		// flag is necessary to remove the values in the ui which are there at the
		// moment
		boolean singleValueFound = false;
		try {

			final List<VerificationParser> parserWithData = factory.getParserWithData(rawDataContent);
			if (!parserWithData.isEmpty()) {
				final VerificationParser parser = parserWithData.get(0);
				singleValueFound = true;
				centerPanel.setVerificationType(parser.getVerificationType());
				if (parser instanceof PcdfVerificationParser) {
					final PcdfReader pr = new PcdfReader();
					try {
						pr.readPCDFString(rawDataContent);
					} catch (final ValidationException e) {
						LOGGER.error("Validation error in parsed text", e);
						final String localizedMessage = e.getLocalizedMessage();
						setErrorMessage(localizedMessage);
						return;
					} catch (final InvalidInputException e) {
						LOGGER.error("Error on parsing text", e);
						final String localizedMessage = e.getLocalizedMessage();
						setErrorMessage(localizedMessage);
						return;
					}
				}
				// try to load public key if its there
				if (centerPanel.getPublicKeyContent().trim().isEmpty() && parser instanceof ContainedPublicKeyParser) {
					final String parsePublicKey = ((ContainedPublicKeyParser) parser)
							.createFormattedKey(rawDataContent);
					centerPanel.setPublicKey(parsePublicKey);
				}
			}
			final List<EncodingType> encodingTypes = EncodingType.guessType(rawDataContent, true);
			if (!parserWithData.isEmpty()) {
				centerPanel.setEncoding(encodingTypes.get(0));
			}

		} catch (final VerificationTypeNotImplementedException e) {
			LOGGER.debug("Guessing parser failed", e);
		}
		if (singleValueFound) {
			try {
				westPanel.updateTree(null);
				this.values = null;
			} catch (final InvalidInputException e) {
				LOGGER.error("Error on cleaning tree");
			}
		}
		delayedAutoVerify();
	}

	public void delayedAutoVerify() {
		centerPanel.setEnabledFields(false);
		delayVerifyTimer.restart();
	}

}
