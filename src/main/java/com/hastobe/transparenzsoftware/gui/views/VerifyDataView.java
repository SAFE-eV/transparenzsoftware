package com.hastobe.transparenzsoftware.gui.views;

import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.gui.listeners.CloseBtnListener;
import com.hastobe.transparenzsoftware.gui.views.customelements.ErrorLog;
import com.hastobe.transparenzsoftware.gui.views.customelements.StyledButton;
import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import com.hastobe.transparenzsoftware.verification.xml.LocalDateTimeAdapter;
import com.hastobe.transparenzsoftware.verification.xml.Meter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VerifyDataView extends JFrame {
    private static final long serialVersionUID = 1L;

    private final static int WIDTH = 1024;
    private final static int HEIGHT = 768;
    public static final Dimension SIZE_SCROLLPANE_VISIBLE = new Dimension(800, 640);
    public static final Dimension SIZE_SCROLL_PANE_CLOSED = new Dimension(800, 10);

    private JLabel iconLabel;
    private ImageIcon icon;
    private JLabel imageLabel;
    private JButton okBtn;
    private JButton showAdditionalBtn;
    private JLabel meterLabel;
    private ErrorLog warningLabel;
    private JTextPane dataLabel;
    private JLabel meterDescLabel;
    private JScrollPane dataScrollpane;


    public VerifyDataView() {
        this.setMinimumSize(new Dimension(WIDTH, HEIGHT));

        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));


        initComponents();

        JPanel paneTop = new JPanel();
        BoxLayout layoutManagerTop = new BoxLayout(paneTop, BoxLayout.PAGE_AXIS);
        paneTop.setLayout(layoutManagerTop);
        paneTop.add(Box.createVerticalGlue());

        paneTop.add(Box.createRigidArea(new Dimension(0, 10)));
        paneTop.add(imageLabel);
        paneTop.add(iconLabel);

        paneTop.add(Box.createVerticalStrut(20));
        paneTop.add(meterDescLabel);
        paneTop.add(Box.createVerticalStrut(5));
        paneTop.add(meterLabel);
        JPanel paneMiddle = new JPanel();
        pane.add(paneTop);
        paneMiddle.add(Box.createVerticalStrut(20));
        paneMiddle.add(warningLabel);
        paneMiddle.add(Box.createVerticalStrut(5));

        paneMiddle.add(Box.createVerticalGlue());
        pane.add(paneMiddle);
//        paneTop.setBorder(BorderFactory.createLineBorder(Color.GREEN));


        JPanel paneCenter = new JPanel();
        BoxLayout layoutManagerCenter = new BoxLayout(paneCenter, BoxLayout.PAGE_AXIS);
        paneCenter.setLayout(layoutManagerCenter);
        paneCenter.add(Box.createVerticalGlue());

        paneCenter.add(showAdditionalBtn);
        paneCenter.add(Box.createVerticalStrut(5));
        paneCenter.add(dataScrollpane);
        paneCenter.add(dataScrollpane);

        paneCenter.add(Box.createVerticalGlue());
//        paneCenter.setBorder(BorderFactory.createLineBorder(Color.RED));

        pane.add(paneCenter);


        JPanel paneBottom = new JPanel();
        BoxLayout layoutManagerBottom = new BoxLayout(paneBottom, BoxLayout.PAGE_AXIS);
        paneBottom.setLayout(layoutManagerBottom);
        paneBottom.add(Box.createVerticalGlue());

        paneBottom.add(Box.createVerticalStrut(25));
        paneBottom.add(okBtn);
        paneBottom.add(Box.createVerticalStrut(10));
        paneBottom.add(Box.createVerticalGlue());
//        paneBottom.setBorder(BorderFactory.createLineBorder(Color.YELLOW));

        pane.add(paneBottom);
        JScrollPane scrollPane = new JScrollPane(pane);
        add(scrollPane);

        pack();
        validate();
        repaint();
    }

    private void initComponents() {
        meterDescLabel = new JLabel(Translator.get("app.view.meter"));
        meterDescLabel.setAlignmentX(CENTER_ALIGNMENT);
        meterLabel = new JLabel("", JLabel.CENTER);
        meterLabel.setAlignmentX(CENTER_ALIGNMENT);

        showAdditionalBtn = new JButton(Translator.get("app.view.show.details"));
        showAdditionalBtn.setBackground(this.getContentPane().getBackground());
        showAdditionalBtn.setMargin(new Insets(0, 0, 0, 0));
        HashMap<TextAttribute, Object> textAttrMap = new HashMap<TextAttribute, Object>();
        textAttrMap.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        textAttrMap.put(TextAttribute.FOREGROUND, Color.BLUE);
        showAdditionalBtn.setFont(showAdditionalBtn.getFont().deriveFont(textAttrMap));
        showAdditionalBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        showAdditionalBtn.setBorderPainted(false);
        showAdditionalBtn.setFocusPainted(false);
        showAdditionalBtn.setContentAreaFilled(false);
        showAdditionalBtn.addActionListener(actionEvent -> {
            dataScrollpane.setVisible(!dataScrollpane.isVisible());
            if (dataScrollpane.isVisible()) {
                showAdditionalBtn.setText(Translator.get("app.view.hide.details"));
                dataScrollpane.setMaximumSize(SIZE_SCROLLPANE_VISIBLE);
                dataScrollpane.setPreferredSize(SIZE_SCROLLPANE_VISIBLE);
                dataScrollpane.setMinimumSize(SIZE_SCROLLPANE_VISIBLE);
            } else {
                showAdditionalBtn.setText(Translator.get("app.view.show.details"));
                dataScrollpane.setMaximumSize(SIZE_SCROLL_PANE_CLOSED);
                dataScrollpane.setPreferredSize(SIZE_SCROLL_PANE_CLOSED);
                dataScrollpane.setMinimumSize(SIZE_SCROLL_PANE_CLOSED);
            }
            validate();
            repaint();
            doLayout();
        });
        dataLabel = new JTextPane();
        dataLabel.setContentType("text/html");
        dataLabel.setMaximumSize(SIZE_SCROLLPANE_VISIBLE);
        dataLabel.setPreferredSize(SIZE_SCROLLPANE_VISIBLE);
        dataLabel.setMinimumSize(SIZE_SCROLLPANE_VISIBLE);
        dataLabel.setBackground(null);
        dataLabel.setEditable(false);
        dataLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dataScrollpane = new JScrollPane(dataLabel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dataScrollpane.setSize(SIZE_SCROLL_PANE_CLOSED);
        dataScrollpane.setAlignmentX(CENTER_ALIGNMENT);
        dataScrollpane.setVisible(false);

        okBtn = new StyledButton(Translator.get("app.view.close"));
        okBtn.setAlignmentX(CENTER_ALIGNMENT);
        Dimension btnSize = new Dimension(100, 40);
        okBtn.setMinimumSize(btnSize);
        okBtn.setSize(btnSize);
        okBtn.setPreferredSize(btnSize);
        okBtn.setMargin(new Insets(20, 20, 20, 20));

        okBtn.addActionListener(new CloseBtnListener(this));

        iconLabel = new JLabel();
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);
        imageLabel = new JLabel();
        imageLabel.setAlignmentX(CENTER_ALIGNMENT);
        warningLabel = new ErrorLog();
        warningLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
    }


    /**
     * Controls if a success or failure screen is shown
     *
     * @param verificationResult
     */
    public void setState(VerificationResult verificationResult) {
        String imgResourcePath;
        //is set dynamicaly in this.setState called by manager
        String iconTextLabel = "";
        if (verificationResult.isVerified()) {
            iconTextLabel = "app.view.verify.success";
            imgResourcePath = "gui/verified.png";
        } else {
            iconTextLabel = "app.view.verify.failure";
            imgResourcePath = "gui/not_verified.png";
        }
        //is set dynamicaly in this.setState called by manager
        URL imgStatePath = ClassLoader.getSystemResource(imgResourcePath);
        String translation = Translator.get(iconTextLabel);
        setTitle(translation);

        icon = new ImageIcon(imgStatePath);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);

        iconLabel.setText(translation);
        imageLabel.setIcon(icon);

        if (verificationResult.getErrorMessages().size() > 0) {
            warningLabel.setText(verificationResult.getErrorMessages());
        } else {
            warningLabel.setVisible(false);
        }
        setMeters(verificationResult.getMeters(), verificationResult.isTransactionResult());
        setAdditionalData(verificationResult.getAdditionalVerificationData());

        validate();
        repaint();
        doLayout();
    }


    private void setAdditionalData(Map<String, Object> additionalVerificationData) {
        if (additionalVerificationData.isEmpty()) {
            dataScrollpane.setVisible(false);
            return;
        }
        StringBuilder bd = new StringBuilder();
        String fontfamily = this.getFont().getFamily();
        bd.append("<html><body width=100% style=\"font-family: ")
                .append(fontfamily)
                .append("\"><table>");
        int count = 0;
        for (String s : additionalVerificationData.keySet()) {
            Object value = additionalVerificationData.get(s);
            if (!(value instanceof Map)) {
                String styleBg = "border-bottom: 1px dotted black;";
                if (count % 2 != 0) {
                    styleBg += "background-color: dark-grey;";
                }
                String addText = Utils.splitStringToGroups(value != null ? value.toString() : "", 70, "<br/>");
                bd.append(
                        String.format(
                                "<tr style=\"%s\"><td style=\"width: 180px;\">%s</td><td><p>%s</p></td></tr>",
                                styleBg,
                                s,
                                addText)
                );
                count++;
            }
        }
        bd.append("</table></body></html>");
        dataLabel.setText(bd.toString());
        this.doLayout();
        this.repaint();
        this.revalidate();
    }

    private void setMeters(List<Meter> meters, boolean transactionResult) {
        if (meters.size() <= 0) {
            return;
        }
        boolean addStart = false;
        boolean addStop = false;
        int numElements = meters.size();
        int index=1;

        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><ul style=\"list-style-type: none;\">");
        for (Meter meter : meters) {
            if(meter.getType() != null){
                if(meter.getType() == Meter.Type.START && !addStart)
                {
                    addStart = true;
                    AddOverviewDisplayElements(builder, meter);
                }

                if(meter.getType() == Meter.Type.UPDATE && addStart)
                {
                    AddOverviewDisplayElements(builder, meter);
                }

                if(meter.getType() == Meter.Type.STOP && !addStop && numElements==index)
                {
                    addStop = true;
                    AddOverviewDisplayElements(builder, meter);
                }
            }
            index++;
        }

        if (transactionResult) {
            Meter.TimeSyncType timeSyncType = Meter.getTimeSyncType(meters);
            builder.append("<li>&nbsp;</li>");
            builder.append("<li>");
            if (timeSyncType == Meter.TimeSyncType.SYNCHRONIZED || timeSyncType == Meter.TimeSyncType.REALTIME) {
                builder.append(String.format("%s ", Translator.get("app.view.time.difference")));
            } else {
                builder.append(String.format("%s ", Translator.get("app.view.time.difference.informative")));
            }
            builder.append("<ul style=\"list-style-type: none;margin-left: 0;\">");
            Duration timeDiff = Meter.getTimeDiff(meters);
            builder.append(String.format("<li>%s</li>", Utils.formatDuration(timeDiff)));
            builder.append("</ul>");
            builder.append("</li>");

            builder.append("<li>&nbsp;</li>");
            builder.append("<li>");
            builder.append(String.format("%s ", Translator.get("app.view.energy.difference")));
            builder.append("<ul style=\"list-style-type: none;margin-left: 0;\">");
            builder.append(String.format("<li>%.4f kWh</li>", Meter.getDifference(meters)));
            builder.append("</ul>");
            builder.append("</li>");
        }

        builder.append("</ul></body></html>");

        meterLabel.setText(builder.toString());
        meterLabel.setToolTipText(Translator.get("app.view.datetime.time.station"));
    }

    private void AddOverviewDisplayElements(StringBuilder builder, Meter meter) {
        builder.append("<li>");
        builder.append(meter.getDescriptiveMessageText() == null ? Translator.get(meter.getType().message) : meter.getDescriptiveMessageText());
        builder.append("</li>");

        builder.append("<li>");
        builder.append(String.format("%.4f kWh", meter.getValue()));
        builder.append("</li><li>");

        LocalDateTime localDateTime = meter.getTimestamp() != null ? meter.getTimestamp().toLocalDateTime() : null;
        builder.append(LocalDateTimeAdapter.formattedDateTime(localDateTime));
        builder.append(" (<span style=\"color: blue\">lokal</span>)");

        if (!meter.getAdditonalText().isEmpty()) {
            builder.append(String.format(" (%s)", meter.getAdditonalText()));
        }
        builder.append("</li>");
        builder.append("<li>&nbsp;</li>");
    }

}
