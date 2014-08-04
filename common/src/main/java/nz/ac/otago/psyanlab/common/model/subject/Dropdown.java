
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;

public class Dropdown extends Question {
    public Dropdown() {
        super();
    }

    public Dropdown(Question q) {
        super(q);
    }

    @Override
    public int getKind() {
        return Question.KIND_DROPDOWN;
    }

    @Override
    public int getKindResId() {
        return R.string.label_subject_detail_dropdown;
    }
}
