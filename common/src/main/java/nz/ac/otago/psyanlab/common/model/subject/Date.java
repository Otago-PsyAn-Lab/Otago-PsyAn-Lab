
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;

public class Date extends Question {
    public Date() {
    }

    public Date(Question q) {
        super(q);
    }

    @Override
    public void cleanForStorage() {
        options.clear();
    }

    @Override
    public int getKind() {
        return Question.KIND_DATE;
    }

    @Override
    public int getKindResId() {
        return R.string.label_subject_detail_date;
    }
}
