package de.safe_ev.transparenzsoftware.gui.views.customelements;

import javax.swing.*;

import de.safe_ev.transparenzsoftware.gui.views.MainView;
import de.safe_ev.transparenzsoftware.i18n.Translator;

import java.awt.*;

public class MainViewBottomPanel extends JPanel {

    //private final VerifyButton verifyBtn;
    private final JLabel pagingLabel;

    public MainViewBottomPanel(MainView mainView) {
        this.setLayout(new GridLayout(0, 3));
        this.add(Box.createVerticalStrut(this.getWidth() / 3));
        this.add(Box.createVerticalStrut(this.getWidth() / 3));
        this.add(Box.createVerticalStrut(this.getWidth() / 3));
        this.add(Box.createHorizontalStrut(this.getWidth() / 3));
        //this.verifyBtn = new VerifyButton(mainView);
        //this.verifyBtn.setName("btn.verify");
        //this.add(verifyBtn);
        pagingLabel = new JLabel("");
        pagingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        setPagingCount(0, 0);
        this.add(pagingLabel);
        this.add(Box.createHorizontalStrut(this.getWidth() / 3));
        this.add(Box.createHorizontalStrut(this.getWidth() / 3));
        this.add(Box.createVerticalStrut(this.getWidth() / 3));
        this.pagingLabel.setVisible(false);
        this.pagingLabel.setName("label.paging");
    }

    public void setPagingCount(int current, int total){
        this.pagingLabel.setText(String.format(Translator.get("app.view.pageof"), current, total));
    }

    public void showPaginationCount() {
        this.pagingLabel.setVisible(true);
    }
}
