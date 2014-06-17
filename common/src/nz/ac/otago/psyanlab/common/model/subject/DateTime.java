
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;

public class DateTime extends Question {
    public DateTime() {
    }

    public DateTime(Question q) {
        super(q);
        options.clear();
    }

    @Override
    public int getKind() {
        return Question.KIND_DATE_TIME;
    }

    @Override
    public int getKindResId() {
        return R.string.label_subject_detail_date_and_time;
    }
}
