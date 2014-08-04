
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;

public class Toggle extends Question {
    public Toggle() {
        super();
        options.add("Yes");
        options.add("No");
    }

    public Toggle(Question q) {
        super(q);
    }

    @Override
    public int getKind() {
        return Question.KIND_TOGGLE;
    }

    @Override
    public int getKindResId() {
        return R.string.label_subject_detail_toggle;
    }
}
