package de.safe_ev.transparenzsoftware.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import de.safe_ev.transparenzsoftware.Utils;
import de.safe_ev.transparenzsoftware.gui.Colors;
import de.safe_ev.transparenzsoftware.gui.views.customelements.ErrorLog;
import de.safe_ev.transparenzsoftware.gui.views.customelements.HintTextField;
import de.safe_ev.transparenzsoftware.i18n.Translator;
import de.safe_ev.transparenzsoftware.verification.result.VerificationResult;
import de.safe_ev.transparenzsoftware.verification.xml.LocalDateTimeAdapter;
import de.safe_ev.transparenzsoftware.verification.xml.Meter;

public class VerifyDataView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final static int WIDTH = 1024;
    private final static int HEIGHT = 768;
    public static final Dimension SIZE_SCROLLPANE_VISIBLE = new Dimension(800, 640);
    public static final Dimension SIZE_SCROLL_PANE_CLOSED = new Dimension(800, 10);

    private static final String TEXT_NO_DATA_PRESENT = "app.view.nodata";

    private static final String TEXT_PLEASE_OPEN_FILE = "app.view.openfile.help";
    private final static String TEXT_PUBLIC_KEY = "app.public.key";

    private JLabel iconLabel;
    private ImageIcon icon;
    private JLabel imageLabel;
    private JLabel meterLabel;
    private ErrorLog warningLabel;
    private JLabel initialHelpLabel;
    private JLabel meterDescLabel;
    private final HintTextField publicKeyField;
    private final JLabel publicKeyLabel;

    private Border tfDefaultBorder;
    private final AtomicBoolean eventsEnabled = new AtomicBoolean();

    public VerifyDataView(MainView mainView) {
	this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
	this.setName("wnd.verifier");
	final JPanel pane = new JPanel();
	// pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
	pane.setLayout(new BorderLayout(2, 2));
	publicKeyLabel = new JLabel("<html>" + Translator.get(TEXT_PUBLIC_KEY) + "</html>");
	publicKeyField = new HintTextField(mainView, eventsEnabled);
	publicKeyField.setColumns(60);
	publicKeyField.setRows(4);
	publicKeyField.setName("text.pubkey");

	initComponents();

	final JPanel paneTop = new JPanel();
	final BoxLayout layoutManagerTop = new BoxLayout(paneTop, BoxLayout.PAGE_AXIS);
	paneTop.setLayout(layoutManagerTop);
	final int innerMargin = 5;
	final Border debugBorder = BorderFactory.createEmptyBorder(innerMargin, innerMargin, innerMargin, innerMargin);

	paneTop.setBorder(debugBorder);
	// paneTop.add(Box.createVerticalGlue());
	final int westWidth = 180;
	paneTop.add(Box.createRigidArea(new Dimension(westWidth, 10)));
	paneTop.add(imageLabel);
	paneTop.add(iconLabel);
	paneTop.add(Box.createRigidArea(new Dimension(westWidth, 10)));
	pane.add(paneTop, BorderLayout.WEST);

	final JPanel paneMiddle = new JPanel();
	paneMiddle.setLayout(new BoxLayout(paneMiddle, BoxLayout.PAGE_AXIS));

	paneMiddle.add(Box.createVerticalStrut(20));
	paneMiddle.add(meterDescLabel);
	meterDescLabel.setBorder(debugBorder);
	paneMiddle.add(Box.createVerticalStrut(5));
	meterLabel.setBorder(debugBorder);
	paneMiddle.add(meterLabel);
	paneMiddle.add(Box.createVerticalStrut(10));
	warningLabel.setBorder(debugBorder);
	paneMiddle.add(warningLabel);
	paneMiddle.add(initialHelpLabel);
	paneMiddle.add(Box.createVerticalStrut(5));
	paneMiddle.setBorder(debugBorder);
	paneMiddle.add(Box.createVerticalGlue());
	pane.add(paneMiddle, BorderLayout.CENTER);
//        paneTop.setBorder(BorderFactory.createLineBorder(Color.GREEN));

	final JPanel keyPanel = new JPanel();
	final BorderLayout layout = new BorderLayout(50, 50);
	keyPanel.setLayout(layout);
	// publicKeyLabel.setPreferredSize(new Dimension(100, 100));
	keyPanel.add(publicKeyLabel, BorderLayout.WEST);
	final JScrollPane scrollPublicKey = new JScrollPane(publicKeyField);
	keyPanel.add(scrollPublicKey, BorderLayout.CENTER);

	final JScrollPane scrollData = new JScrollPane(pane);
	scrollData.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	scrollData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

	this.setLayout(new BorderLayout(20, 20));
	this.add(scrollData, BorderLayout.CENTER);
	this.add(keyPanel, BorderLayout.SOUTH);

	/// pack();
	clearInputs();
	validate();
	repaint();
	this.setAutoscrolls(true);
    }

    private void initComponents() {
	meterDescLabel = new JLabel(Translator.get("app.view.meter"));
	meterDescLabel.setAlignmentX(CENTER_ALIGNMENT);

	meterLabel = new JLabel("", JLabel.CENTER);
	meterLabel.setAlignmentX(CENTER_ALIGNMENT);
	meterLabel.setName("lbl.meter");

	final HashMap<TextAttribute, Object> textAttrMap = new HashMap<TextAttribute, Object>();
	textAttrMap.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
	textAttrMap.put(TextAttribute.FOREGROUND, Color.BLUE);

	iconLabel = new JLabel();
	iconLabel.setAlignmentX(CENTER_ALIGNMENT);
	iconLabel.setMinimumSize(new Dimension(250, 64));
	iconLabel.setName("lbl.icon");
	imageLabel = new JLabel();
	imageLabel.setAlignmentX(CENTER_ALIGNMENT);
	imageLabel.setName("lbl.image");
	imageLabel.setMinimumSize(new Dimension(64, 64));
	warningLabel = new ErrorLog();
	warningLabel.setAlignmentX(CENTER_ALIGNMENT);
	// warningLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
	warningLabel.setName("lbl.warn");
	initialHelpLabel = new JLabel();
	initialHelpLabel.setAlignmentX(CENTER_ALIGNMENT);
	initialHelpLabel.setName("lbl.openfile");
    }

    /**
     * Controls if a success or failure screen is shown
     *
     * @param verificationResult
     */
    public void setState(VerificationResult verificationResult) {
	if (verificationResult == null) {
	    clearVerificationResult();
	    return;
	}
	initialHelpLabel.setVisible(false);
	setVerifyIconAndLabel(verificationResult.isVerified() ? VerifyResult.VERIFIED_OK : VerifyResult.VERIFIED_BAD);

	if (verificationResult.getErrorMessages().size() > 0) {
	    warningLabel.setText(verificationResult.getErrorMessages());
	    warningLabel.setVisible(true);
	} else {
	    warningLabel.setVisible(false);
	}
	setMeters(verificationResult.getMeters(), verificationResult.isTransactionResult());

	validate();
	repaint();
	doLayout();
	tfDefaultBorder = iconLabel.getBorder();
    }

    private void setVerifyIconAndLabel(VerifyResult vr) {
	String imgResourcePath = "";
	String iconTextLabel = "";
	switch (vr) {
	case NOT_YET_VERIFIED:
	    iconTextLabel = "app.view.verify.notYet";
	    imgResourcePath = "gui/not_yet_verified.png";
	    break;
	case VERIFIED_BAD:
	    iconTextLabel = "app.view.verify.failure";
	    imgResourcePath = "gui/not_verified.png";
	    break;
	case VERIFIED_OK:
	    iconTextLabel = "app.view.verify.success";
	    imgResourcePath = "gui/verified.png";
	    break;
	}
	// is set dynamicaly in this.setState called by manager
	final URL imgStatePath = ClassLoader.getSystemResource(imgResourcePath);
	final String translation = Translator.get(iconTextLabel);
	// HBO setTitle(translation);

	icon = new ImageIcon(imgStatePath);
	final Image image = icon.getImage();
	final Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
	icon = new ImageIcon(scaledImage);

	iconLabel.setText(translation);
	imageLabel.setIcon(icon);
    }

    private void clearVerificationResult() {
	setVerifyIconAndLabel(VerifyResult.NOT_YET_VERIFIED);
	warningLabel.setVisible(false);
	initialHelpLabel.setText(Translator.get(TEXT_PLEASE_OPEN_FILE));
	initialHelpLabel.setVisible(true);
	meterLabel.setVisible(false);
	meterDescLabel.setVisible(false);
    }

    /**
     * called when user changes the public key, before the data is verified again.
     */
    public void clearErrorMessage() {
	setVerifyIconAndLabel(VerifyResult.NOT_YET_VERIFIED);
    }

    private void setMeters(List<Meter> meters, boolean transactionResult) {

	meters = Meter.filterLawRelevant(meters);

	if (meters.size() <= 0) {
	    return;
	}
	boolean addStart = false;
	boolean addStop = false;
	int index = 1;

	final StringBuilder builder = new StringBuilder();
	builder.append("<html><body><ul style=\"list-style-type: none; margin-left: 0px\">");
	String preci = null;
	int digitsScaler = Integer.MAX_VALUE;
	int lastStopIndex = -1;
	for (final Meter meter : meters) {
	    if (meter.getType() == Meter.Type.STOP) {
		lastStopIndex = index;
	    }
	    index++;
	}
	index = 1;
	for (final Meter meter : meters) {
	    if (meter.getScaling() < digitsScaler) {
		preci = meter.getScalingFormat();
		digitsScaler = meter.getScaling();
	    }
	    if (meter.getType() != null) {
		if (meter.getType() == Meter.Type.START && !addStart) {
		    addStart = true;
		    AddOverviewDisplayElements(builder, meter);
		}

		if (meter.getType() == Meter.Type.UPDATE && addStart) {
		    AddOverviewDisplayElements(builder, meter);
		}

		if (meter.getType() == Meter.Type.STOP && !addStop && lastStopIndex == index) {
		    addStop = true;
		    AddOverviewDisplayElements(builder, meter);
		}
	    } else {
		AddOverviewDisplayElements(builder, meter);
	    }
	    index++;
	}

	if (transactionResult) {
	    final Meter.TimeSyncType timeSyncType = Meter.getTimeSyncType(meters);
	    builder.append("<li>&nbsp;</li>");
	    builder.append("<li>");
	    if (timeSyncType == Meter.TimeSyncType.SYNCHRONIZED || timeSyncType == Meter.TimeSyncType.REALTIME) {
		builder.append(String.format("%s ", Translator.get("app.view.time.difference")));
	    } else {
		builder.append(String.format("%s ", Translator.get("app.view.time.difference.informative")));
	    }
	    builder.append("<ul style=\"list-style-type: none;margin-left: 0;\">");
	    final Duration timeDiff = Meter.getTimeDiff(meters);
	    builder.append(String.format("<li>%s</li>", Utils.formatDuration(timeDiff)));
	    builder.append("</ul>");
	    builder.append("</li>");

	    builder.append("<li>&nbsp;</li>");
	    builder.append("<li>");
	    builder.append(String.format("%s ", Translator.get("app.view.energy.difference")));
	    builder.append("<ul style=\"list-style-type: none;margin-left: 0;\">");
	    final long compensated = meters.stream().filter(Meter::isCompensated).count();
	    final long relevant = meters.stream().filter(Meter::isLawRelevant).count();
	    String compensation = "";
	    if (compensated == relevant) {
		compensation = Translator.get("app.verify.compensated");
	    } else if (compensated > 0) {
		compensation = Translator.get("app.verify.compensated.partly");
	    }
	    builder.append(
		    String.format("<li>" + preci + " kWh " + compensation + "</li>", Meter.getDifference(meters)));
	    builder.append("</ul>");
	    builder.append("</li>");
	}

	builder.append("</ul></body></html>");
	meterLabel.setVisible(true);
	meterDescLabel.setVisible(true);
	meterLabel.setText(builder.toString());
	meterLabel.setToolTipText(Translator.get("app.view.datetime.time.station"));
	this.validate();
    }

    private void AddOverviewDisplayElements(StringBuilder builder, Meter meter) {
	if (!meter.isLawRelevant()) {
	    return;
	}
	builder.append("<li>");
	if (meter.getDescriptiveMessageText() != null) {
	    builder.append(meter.getDescriptiveMessageText());
	} else if (meter.getType() != null) {
	    builder.append(Translator.get(meter.getType().message));
	} else {
	    // no type.
	}
	builder.append("</li>");

	builder.append("<li>");
	final String preci = meter.getScalingFormat();
	builder.append(String.format(preci + " kWh", meter.getValue()));
	if (meter.isCompensated()) {
	    builder.append(" (");
	    builder.append(Translator.get("app.verify.compensated"));
	    builder.append(")");
	}
	builder.append("</li><li>");

	final LocalDateTime localDateTime = meter.getTimestamp() != null ? meter.getTimestamp().toLocalDateTime()
		: null;
	builder.append(LocalDateTimeAdapter.formattedDateTime(localDateTime));
	builder.append(" (<span style=\"color: blue\">lokal</span>)");

	if (!meter.getAdditonalText().isEmpty()) {
	    builder.append(String.format(" (%s)", meter.getAdditonalText()));
	}
	builder.append("</li>");
	builder.append("<li>&nbsp;</li>");
    }

    public void clearInputs() {
	clearVerificationResult();
	setEnabled(false);
	publicKeyField.setText("");
    }

    public void fillUpContent(String publicKeyContent, boolean indeterminate) {
	if (publicKeyContent == null) {
	    publicKeyContent = "";
	}
	setEnabled(false);
	publicKeyField.setHint(Translator.get("app.view.pubkey.ok"));
	publicKeyField.setText(publicKeyContent);
    }

    public void setPublicKey(String parsePublicKey) {
	fillUpContent(parsePublicKey, false);
    }

    public void setPublicKeyWarning(boolean warn) {
	if (warn) {
	    publicKeyField.setBorder(BorderFactory.createLineBorder(Colors.WARNING_LOG));
	} else {
	    publicKeyField.setBorder(tfDefaultBorder);
	}
    }

    /**
     * Set at the end of the verification or at the end of data input to let the
     * user enter a public key.
     */
    @Override
    public void setEnabled(boolean b) {
	eventsEnabled.set(b);
	publicKeyField.setEnabled(b);
    }

    public String getPublicKeyContent() {
	return publicKeyField.getText();
    }

}
