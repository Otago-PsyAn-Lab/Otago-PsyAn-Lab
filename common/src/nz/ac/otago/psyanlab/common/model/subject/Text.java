
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Subject;

public class Text extends Subject {
    public static final int ID = 0x06;

    public Text() {
        mTypeId = ID;
        mTypeLabelResId = R.string.label_subject_detail_text;
    }
}
