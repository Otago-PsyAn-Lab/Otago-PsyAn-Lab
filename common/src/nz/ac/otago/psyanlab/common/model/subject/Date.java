
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Subject;

public class Date extends Subject {
    public static final int ID = 0x00;

    public Date() {
        mTypeId = ID;
        mTypeLabelResId = R.string.label_subject_detail_date;
    }
}
