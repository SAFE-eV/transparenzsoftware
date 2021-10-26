package com.hastobe.transparenzsoftware.gui.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.gui.Colors;
import com.hastobe.transparenzsoftware.gui.views.customelements.ErrorLog;
import com.hastobe.transparenzsoftware.gui.views.customelements.VerifyTextArea;
import com.hastobe.transparenzsoftware.gui.views.helper.DetailsList;
import com.hastobe.transparenzsoftware.i18n.Translator;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.VerificationType;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import com.hastobe.transparenzsoftware.verification.xml.LocalDateTimeAdapter;
import com.hastobe.transparenzsoftware.verification.xml.Meter;


public class DetailDataView extends JScrollPane {
    private static final long serialVersionUID = 1L;

    private static final String TEXT_NO_DATA_PRESENT = "app.view.nodata";
    private static final String TEXT_NO_DATA_DETAILS = "app.view.nodata.details";
    
	private final JTextPane dataLabel;

    public DetailDataView(MainView mainView) {
        this.setName("wnd.details");
        JPanel pane = new JPanel();

        dataLabel = new JTextPane();
        dataLabel.setContentType("text/html");
        dataLabel.setBackground(null);
        dataLabel.setEditable(false);
        dataLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dataLabel.setName("lbl.data");
        this.setViewportView(dataLabel);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setAlignmentX(CENTER_ALIGNMENT);
        ///pack();
        validate();
        repaint();
        this.setAutoscrolls(true);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    public void setAdditionalData(DetailsList detailsList) {
        if (detailsList.isEmpty()) {
        	dataLabel.setText(Translator.get(TEXT_NO_DATA_DETAILS));
            return;
        }
        StringBuilder bd = new StringBuilder();
        String fontfamily = this.getFont().getFamily();
        bd.append("<html><body width=100% style=\"font-family: ")
                .append(fontfamily)
                .append("\"><table>");
        int count = 0;
        for (String s : detailsList.keySet()) {
            Object value = detailsList.get(s);
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

	public void clearErrorMessage() {
    	dataLabel.setText(Translator.get(TEXT_NO_DATA_DETAILS));
	}
	
}
