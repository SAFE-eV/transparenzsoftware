package com.hastobe.transparenzsoftware.gui.views;

import com.hastobe.transparenzsoftware.Constants;
import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.gui.listeners.GotoBtnListener;
import com.hastobe.transparenzsoftware.gui.views.customelements.*;
import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.*;
import com.hastobe.transparenzsoftware.verification.format.pcdf.PcdfReader;
import com.hastobe.transparenzsoftware.verification.input.InputReader;
import com.hastobe.transparenzsoftware.verification.input.InvalidInputException;
import com.hastobe.transparenzsoftware.verification.result.Error;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import com.hastobe.transparenzsoftware.verification.xml.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainView extends JFrame {

    private final static long serialVersionUID = 1L;
    private final static String TEXT_WINDOW_TITEL = "app.title";
    private final static Logger LOGGER = LogManager.getLogger(MainView.class);
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


    private MainView(VerificationParserFactory factory) {
        this.factory = factory;
        verifier = new Verifier(factory);
        currentValuePos = 0;
        initPage();
    }

    public static MainView init(VerificationParserFactory factory) {
        instance = new MainView(factory);
        return instance;
    }


    private void initPage() {
        this.setLayout(new BorderLayout());
        this.setTitle(String.format("%s - Version: %s", Translator.get(TEXT_WINDOW_TITEL), Constants.VERSION));

        //now create the panels
        northPanel = new MainViewNorthPanel(this);
        centerPanel = new MainViewCenterPanel(this);
        southPanel = new MainViewBottomPanel(this);
        westPanel = new MainViewWestPanel(this);

        //set top menu
        menuBar = new MainViewMenu(this);
        this.setJMenuBar(menuBar);
        this.add(northPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
        this.add(westPanel, BorderLayout.WEST);
        this.add(Box.createHorizontalStrut(getPreferredSize().width / 3), BorderLayout.EAST);

        pack();
        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().requestFocusInWindow();
        //fix to resize left panel
        westPanel.initView();
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
        southPanel.setEnableVerifyButton(enable);
    }

    public void setEnableVerifyMode(boolean single){
        southPanel.setEnableVerifyMode(single);
        centerPanel.setEnabledFields(single);
    }
    /**
     * Loads the content of the fields and tries to verify it
     * If it was successfully a new window will be opened
     */
    public void verify() {
        setEnableVerifyButton(false);
        //only use he first value
        VerificationParser parser;
        clearErrorMessages();

        String publicKeyContent = centerPanel.getPublicKeyContent().trim();
        if (publicKeyContent.isEmpty() && !centerPanel.getVerificationType().isPublicKeyAware()) {
            LOGGER.error("Empty public key field");
            setErrorMessage(Translator.get("error.values.no.publickey"));
            return;
        }

        String rawDataContent = centerPanel.getRawDataContent().trim();
        if (rawDataContent.isEmpty()) {
            LOGGER.error("Empty data field");
            setErrorMessage(Translator.get("error.values.no.signeddata"));
            return;
        }
        try {
            parser = factory.getParser(centerPanel.getVerificationType());
        } catch (VerificationTypeNotImplementedException e) {
            setErrorMessage(Translator.get("error.format.unknown"));
            // we have not found a parser skip the rest
            return;
        }


        VerificationResult verificationResult = null;
        try {
            if (westPanel.getCurrentTransactionid() == null) {
                if (publicKeyContent.trim().isEmpty()) {
                    verificationResult = verifier.verify(parser, rawDataContent, null);
                } else {
                    publicKeyContent = publicKeyContent.replaceAll(" ", "");
                    verificationResult = verifier.verify(parser, rawDataContent, publicKeyContent);
                }
            } else {
                List<Value> values = westPanel.getValues(westPanel.getCurrentTransactionid());
                verificationResult = verifier.verifyTransaction(parser, values, publicKeyContent);
            }
        } catch (TransactionValidationException e) {
            LOGGER.error(String.format("TransactionValidation exception happened: %s", e.getMessage()));
            setErrorMessage(e.getLocalizedMessage());
            setEnableVerifyButton(true);
            return;
        } catch (Exception e) {
            //we do not want our application to crash
            setEnableVerifyButton(true);
            setErrorMessage(Translator.get("app.view.error.generic"));
            LOGGER.error("Unhandled error", e);
            return;
        }
        setEnableVerifyButton(true);
        //make sure we have no nullpointer if so something was fishy here, lets show an error because
        //we cant get forward
        if (verificationResult == null) {
            setErrorMessage(Translator.get("app.view.error.generic"));
            return;
        }

        if (verificationResult.containsErrorOfType(Error.Type.VALIDATION) || verificationResult.containsErrorOfType(Error.Type.INPUT)) {
            String errmsg = verificationResult.getErrorMessages().size() > 0 ? verificationResult.getErrorMessages().get(0).getLocalizedMessageCode() : "";
            LOGGER.error(String.format("Validation error %s", errmsg));
            List<String> errorMessages = new ArrayList<>();
            for (Error errorMessage : verificationResult.getErrorMessages()) {
                if (!errorMessage.getType().equals(Error.Type.VERIFICATION)) {
                    errorMessages.add(errorMessage.getLocalizedMessage());
                    break;
                }
            }
            if (!errorMessages.isEmpty()) {
                StringBuilder messageBuilder = new StringBuilder();
                if (errorMessages.size() == 1) {
                    messageBuilder
                            .append("<html><body><p>")
                            .append(errorMessages.get(0))
                            .append("</p></body></html>");
                } else {
                    //show a list if more than 1 error message
                    messageBuilder.append("<html><body><ul>");
                    for (String errorMessage : errorMessages) {
                        messageBuilder.append("<li>")
                                .append(errorMessage)
                                .append("</li>");
                    }
                    messageBuilder.append("</ul></body></html>");
                }
                setErrorMessage(messageBuilder.toString());
            }
        } else {
            ViewUtils.spawnVerificationWindow(verificationResult);
        }
    }

    /**
     * Triggered on file open tries to verify the file
     * and set the input fields
     *
     * @param filename - path of the file which was choosen
     */
    public void onFileOpen(String filename) {
        //cleanup state
        LOGGER.info(String.format("Try to open file %s", filename));
        clearState();
        
        if (filename.indexOf(".pcdf") != -1)
        {
        	//this is a Porsche Charging Data File, parse in a different way
        	PcdfReader pcdfReader = new PcdfReader();
        	try {
				values = pcdfReader.readPcdfFile(filename);
			} catch (ValidationException e) {
				LOGGER.error("Validation error in file", exception);
	            String localizedMessage = e.getLocalizedMessage();
	            setErrorMessage(localizedMessage);
	            return;
			} catch (InvalidInputException e) {
				LOGGER.error("Error on reading file", exception);
	            String localizedMessage = e.getLocalizedMessage();
	            setErrorMessage(localizedMessage);
	            return;
			}
        }
        else
        {
        	File xmlFile = new File(filename);
	        try {
	            InputReader inputReader = new InputReader();
	            values = inputReader.readFile(xmlFile);
	        } catch (InvalidInputException exception) {
	            LOGGER.error("Error on reading file", exception);
	            String localizedMessage = exception.getLocalizedMessage();
	            setErrorMessage(localizedMessage);
	            return;
	        }
        }
        onValuesRead(values);
    }

    /**
     * Changes to the view to the according element of index of the values
     *
     * @param index page nr
     */
    public void stepToValue(int index) {
        currentValuePos = index;
        //validate will throw an error so the first one will have an error anyway
        Value firstValue = values.getValues().get(currentValuePos);
        //set the view fields
        SignedData signedData = firstValue.getSignedData();
        PublicKey publicKey = firstValue.getPublicKey();
        try {
            String publicKeyContent;
            if (publicKey != null) {
                publicKeyContent = publicKey.getValue();
            } else {
                publicKeyContent = tryFetchingEmbeddedKey(signedData, signedData.getFormatAsVerificationType());
            }

            List<EncodingType> encodingTypes = EncodingType.guessType(publicKeyContent);
            if (encodingTypes.size() == 1) {
                EncodingType type = encodingTypes.get(0);
                switch (type) {
                    case BASE64:
                        publicKeyContent = Utils.toFormattedHex(EncodingType.base64Decode(publicKeyContent));
                        break;
                    case HEX:
                        publicKeyContent = Utils.splitStringToGroups(publicKeyContent, 2);
                        break;
                }
            }
            centerPanel.fillUpContent(signedData.getValue(), publicKeyContent, signedData.getEncodingType(), signedData.getFormatAsVerificationType());
        } catch (DecodingException exception) {
            LOGGER.error("Error on reading file", exception);
            String localizedMessage = exception.getLocalizedMessage();
            setErrorMessage(localizedMessage);
        }
        //enable for verify
        setEnableVerifyButton(true);
        if (firstValue.getSignedData().getEncoding() == null) {
            List<EncodingType> types = EncodingType.guessType(firstValue.getSignedData().getValue(), true);
            if (!types.isEmpty()) {
                centerPanel.setEncoding(types.get(0));
            }
        }
        if (firstValue.getSignedData().getFormat() == null) {
            try {
                List<VerificationParser> verificationParserList = factory.getParserWithData(firstValue.getSignedData().getValue());
                VerificationParser verificationParser = verificationParserList.get(0);
                centerPanel.setVerificationType(verificationParser.getVerificationType());
            } catch (VerificationTypeNotImplementedException e) {
                LOGGER.info("No verification type found for data");
            }
        }

        //check if public key might be in the payload data
        checkPublicKeyPayloadData(signedData);


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
        this.southPanel.setPagingCount(index + 1, values.getValues().size());
    }

    /**
     * Checks if the payload data contains a public key and if
     * it matches to the entered public key
     *
     * @param signedData
     */
    private void checkPublicKeyPayloadData(SignedData signedData) {
        VerificationParser verificationParser = null;
        try {
            verificationParser = factory.getParser(centerPanel.getVerificationType());
            String publicKeyContent = centerPanel.getPublicKeyContent().trim().isEmpty() ? null : centerPanel.getPublicKeyContent();
            String loaded = verifier.checkForEmbeddedPublicKey(verificationParser, publicKeyContent, signedData.getValue());
            if (loaded != null && !loaded.trim().isEmpty()) {
                centerPanel.setPublicKey(loaded);
            }
        } catch (VerificationTypeNotImplementedException e) {
            // no op
        } catch (InvalidInputException e) {
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
        } catch (VerificationTypeNotImplementedException e) {
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
     * Clears the input fields and error messages.
     * Sets the verify button to disabled
     */
    private void clearState() {
        centerPanel.clearInputs();
        setEnableVerifyButton(false);
        clearErrorMessages();
        menuBar.setGotoNextItemEnabled(false);
        menuBar.setGotoPreviousItemEnabled(false);
        centerPanel.setPublicKeyWarning(false);
        southPanel.setEnableVerifyMode(true);
        centerPanel.setEnabledFields(true);
        currentValuePos = 0;
        westPanel.initView();
    }

    private void setErrorMessage(String message) {
        centerPanel.setErrorMessage(message);
    }

    private boolean isErrorMessageSet(){
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
            HelpView helpView = new HelpView(this);
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
        if (this.helpView != null) {
            helpView.dispose();
        }
        this.helpView = null;
    }

    /**
     * About window opened or bring to front if open
     */
    public void onAboutOpen() {
        if (aboutView == null) {
            AboutView aboutView = new AboutView(this);
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
        if (this.aboutView != null) {
            aboutView.dispose();
        }
        this.aboutView = null;
    }

    /**
     * Tries to load content of a transparenzsoftware xml files with values input into the view
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
            this.southPanel.showPaginationCount();
        } catch (InvalidInputException e) {
            String localizedMessage = e.getLocalizedMessage();
            setErrorMessage(localizedMessage);
        }
    }

    /**
     * Listener for a goto page action
     *
     * @param direction direction where to go to
     */
    public void onGoto(GotoBtnListener.Direction direction) {
        int nrValues = values.getValues() != null ? values.getValues().size() : 0;
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
        Dimension size = getSize();
        size.setSize(size.getWidth() - 40, size.getHeight() - 40);
        frame.setSize(size);
        frame.setVisible(true);
    }

    /**
     * This action is triggered after the user has pasted content into the input field
     */
    public void onPaste() {
        centerPanel.cleanUpNoiseInRawData();
        String rawDataContent = centerPanel.getRawDataContent();
        InputReader reader = new InputReader();
        Values values;
        try {
            values = reader.readString(rawDataContent);
            onValuesRead(values);
            return;
        } catch (InvalidInputException e) {
        	try
        	{
        		PcdfReader pcdfRead = new PcdfReader();
        		pcdfRead.readPCDFString(rawDataContent);
        	}
        	catch (ValidationException er)
        	{
        		LOGGER.debug("No values pasted");
        	}
        }

        //we could not read values so it can be a single value
        //flag is necessary to remove the values in the ui which are there at the moment
        boolean singleValueFound = false;
        try {

            List<VerificationParser> parserWithData = factory.getParserWithData(rawDataContent);
            if (!parserWithData.isEmpty()) {
                VerificationParser parser = parserWithData.get(0);
                singleValueFound = true;
                centerPanel.setVerificationType(parser.getVerificationType());
                //try to load public key if its there
                if(centerPanel.getPublicKeyContent().trim().isEmpty() && parser instanceof ContainedPublicKeyParser) {
                    String parsePublicKey = ((ContainedPublicKeyParser) parser).createFormattedKey(rawDataContent);
                    centerPanel.setPublicKey(parsePublicKey);
                }
            }
            List<EncodingType> encodingTypes = EncodingType.guessType(rawDataContent, true);
            if (!parserWithData.isEmpty()) {
                centerPanel.setEncoding(encodingTypes.get(0));
            }


        } catch (VerificationTypeNotImplementedException e) {
            LOGGER.debug("Guessing parser failed", e);
        }
        if(singleValueFound){
            try {
                westPanel.updateTree(null);
                this.values = null;
            } catch (InvalidInputException e) {
                LOGGER.error("Error on cleaning tree");
            }
        }
    }
}
