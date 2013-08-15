
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Subject;

public class Number extends Subject {
    public static final int ID = 0x04;

    public Number() {
        mTypeId = ID;
        mTypeLabelResId = R.string.label_subject_detail_number;
    }
}
