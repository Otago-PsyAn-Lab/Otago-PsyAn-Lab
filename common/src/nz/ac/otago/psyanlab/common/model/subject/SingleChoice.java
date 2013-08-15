
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;

import java.util.ArrayList;

public class SingleChoice extends SubjectDetailWithOptions {
    public static final int ID = 0x05;

    public SingleChoice() {
        this(null);
    }

    public SingleChoice(ArrayList<String> options) {
        super(options);
        mTypeId = ID;
        mTypeLabelResId = R.string.label_subject_detail_single_choice;
    }
}
