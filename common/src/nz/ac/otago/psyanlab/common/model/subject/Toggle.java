
package nz.ac.otago.psyanlab.common.model.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;

import java.util.ArrayList;

public class Toggle extends Question {
    public Toggle() {
        super();
        options.add("Yes");
        options.add("No");
    }

    public Toggle(Question q) {
        super(q);
        if (options.size() > 2) {
            options = new ArrayList<String>(options.subList(0, 2));
        }
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
