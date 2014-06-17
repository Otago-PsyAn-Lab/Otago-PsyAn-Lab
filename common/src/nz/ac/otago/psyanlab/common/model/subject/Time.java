
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;

public class Time extends Question {
    public Time() {
        super();
    }

    public Time(Question q) {
        super(q);
        options.clear();
    }

    @Override
    public int getKind() {
        return Question.KIND_TIME;
    }

    @Override
    public int getKindResId() {
        return R.string.label_subject_detail_time;
    }
}
