
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Subject;

public class DateTime extends Subject {
    public static final int ID = 0x01;
    
    public DateTime() {
        mTypeId = ID;
        mTypeLabelResId = R.string.label_subject_detail_date_and_time;
    }
}
