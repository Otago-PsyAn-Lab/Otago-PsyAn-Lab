
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.RuleDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Rule;

import android.widget.SpinnerAdapter;

public interface RuleCallbacks {
    void addRuleDataChangeListener(RuleDataChangeListener listener);

    long addRule(Rule rule);

    void deleteRule(long id);

    SpinnerAdapter getEventsAdapter(Class<?> clazz);

    Rule getRule(long ruleId);

    ProgramComponentAdapter<Rule> getRuleAdapter(long sceneId);

    void removeRuleDataChangeListener(RuleDataChangeListener listener);

    void putRule(long id, Rule rule);
}
