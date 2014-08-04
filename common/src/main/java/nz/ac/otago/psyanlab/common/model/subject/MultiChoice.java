
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;

public class MultiChoice extends Question {
    public MultiChoice() {
        super();
    }

    public MultiChoice(Question q) {
        super(q);
    }

    @Override
    public int getKind() {
        return Question.KIND_MULTI_CHOICE;
    }

    @Override
    public int getKindResId() {
        return R.string.label_subject_detail_multiple_choice;
    }
}
