
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.RuleDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Rule;

import android.widget.SpinnerAdapter;

public interface RuleCallbacks {

    void addRuleDataChangeListener(RuleDataChangeListener listener);

    long createRule(Rule rule);

    void deleteRule(long id);

    SpinnerAdapter getEventsAdapter(Class<?> clazz);

    SpinnerAdapter getMethodsAdapter(Class<?> clazz, int returnTypes);

    Rule getRule(long ruleId);

    ProgramComponentAdapter<Rule> getRuleAdapter(long sceneId);

    void removeRuleDataChangeListener(RuleDataChangeListener listener);

    void updateRule(long id, Rule rule);
}
