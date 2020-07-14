package com.hastobe.transparenzsoftware.gui.managers;

import com.hastobe.transparenzsoftware.gui.views.VerifyDataView;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager to manage verify views
 */
public class VerifyViewManager {
    private static List<VerifyDataView> allVerifyDataViews = new ArrayList<VerifyDataView>(); //collection where orginial VerifyDataViews are stored
    private static int idCounter = 1; //manager does the id allocation for its managed VerifyDataViews


    private VerifyViewManager() {
    }

    /**
     * Creates an new instace of a VerifyDataView with an id. The VerifyViewManager also start managing this VerifyDataView.
     *
     * @return The new created VerifyDataView.
     */
    public static VerifyDataView create() {
        //add exceptionHandling
        VerifyDataView newDataView = new VerifyDataView();
        newDataView.pack();
        newDataView.setSize(590, 450);
        newDataView.setVisible(false);

        allVerifyDataViews.add(newDataView);
        idCounter++;
        return newDataView; //return deep copy
    }





    /**
     * Set state of the verify view if it passed or if it failed.
     *
     * @param toEdit  VerifyDataView you want to change.
     * @return The changed VerifyDataView.
     */
    public static VerifyDataView setState(VerifyDataView toEdit, VerificationResult verificationResult) {
        //if copy is handed out get origional
        //add exceptionHandling
        toEdit.setState(verificationResult);
        return toEdit; //ever retrun chaned obj because of copys
    }

}
