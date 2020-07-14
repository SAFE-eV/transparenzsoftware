package com.hastobe.transparenzsoftware.gui.views.customelements;

import com.hastobe.transparenzsoftware.gui.listeners.VerifyBtnListener;
import com.hastobe.transparenzsoftware.gui.views.MainView;
import com.hastobe.transparenzsoftware.i18n.Translator;

public class VerifyButton extends StyledButton {

    public final static String TEXT_VERIFY_BUTTON = "app.view.verify";
    public final static String TEXT_VERIFY_TRANSACTION_BUTTON = "app.view.verify.transaction";
    private boolean verifySingle;

    public VerifyButton(MainView mainView) {
        super(Translator.get(TEXT_VERIFY_BUTTON));
        this.addActionListener(new VerifyBtnListener(mainView));
        verifySingle = true;
    }

    public void setVerifySingle(boolean single) {
        verifySingle = true;
        String text = Translator.get(VerifyButton.TEXT_VERIFY_BUTTON);
        if(!single){
            text = Translator.get(VerifyButton.TEXT_VERIFY_TRANSACTION_BUTTON);
        }
        this.setText(text);
        this.verifySingle = single;
    }

    public boolean isVerifySingle() {
        return verifySingle;
    }
}
