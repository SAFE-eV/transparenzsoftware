package de.safe_ev.transparenzsoftware.gui.views.customelements;

import javax.swing.*;
import java.awt.*;

/**
 * Panel holding the logic around rendering an error
 */
public class MainViewErrorPanel extends JPanel {

    private final ErrorLog errorLog;

    public MainViewErrorPanel(ErrorLog errorLog) {
        this.setLayout(new GridLayout(0, 1));
        this.add(Box.createVerticalStrut(this.getWidth() / 3));
        this.errorLog = errorLog;
        this.add(errorLog);
    }

    /**
     * Sets the error text in the error box
     *
     * @param errorText text to display
     * @param warning
     */
    public void setErrorText(String errorText, boolean warning) {
        errorLog.setText(errorText, warning);
    }

    /**
     * Indicates if a error message is set
     * @return
     */
    public boolean isErrorMessageSet() {
        return !this.errorLog.getText().isEmpty();
    }
}
