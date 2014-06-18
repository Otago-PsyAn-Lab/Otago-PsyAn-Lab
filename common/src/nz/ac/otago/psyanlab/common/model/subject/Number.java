
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;

public class Number extends Question {
    public Number() {
        super();
    }

    public Number(Question q) {
        super(q);
    }

    @Override
    public void cleanForStorage() {
        options.clear();
    }

    @Override
    public int getKind() {
        return Question.KIND_NUMBER;
    }

    @Override
    public int getKindResId() {
        return R.string.label_subject_detail_number;
    }
}
