
package nz.ac.otago.psyanlab.common.model.subject;

import java.util.ArrayList;
import nz.ac.otago.psyanlab.common.R;

public class Dropdown extends SubjectDetailWithOptions {
    public static final int ID = 0x02;

    public Dropdown() {
        this(null);
    }

    public Dropdown(ArrayList<String> list) {
        super(list);
        mTypeId = ID;
        mTypeLabelResId = R.string.label_subject_detail_dropdown;
    }
}
