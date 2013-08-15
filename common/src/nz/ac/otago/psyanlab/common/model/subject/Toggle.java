
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;

import java.util.ArrayList;

public class Toggle extends SubjectDetailWithOptions {
    public static final int ID = 0x08;

    public Toggle() {
        this(null);
    }

    public Toggle(ArrayList<String> options) {
        super(options);
        mTypeId = ID;
        mTypeLabelResId = R.string.label_subject_detail_toggle;
    }
}
