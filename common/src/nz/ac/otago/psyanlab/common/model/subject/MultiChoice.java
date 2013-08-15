
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;

import java.util.ArrayList;

public class MultiChoice extends SubjectDetailWithOptions {
    public static final int ID = 0x03;

    public MultiChoice() {
        this(null);
    }

    public MultiChoice(ArrayList<String> options) {
        super(options);
        mTypeId = ID;
        mTypeLabelResId = R.string.label_subject_detail_multiple_choice;
    }
}
