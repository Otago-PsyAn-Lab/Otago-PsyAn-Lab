
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;

public class SingleChoice extends Question {
    public SingleChoice() {
        super();
    }

    public SingleChoice(Question q) {
        super(q);
    }

    @Override
    public int getKind() {
        return Question.KIND_SINGLE_CHOICE;
    }

    @Override
    public int getKindResId() {
        return R.string.label_subject_detail_single_choice;
    }
}
