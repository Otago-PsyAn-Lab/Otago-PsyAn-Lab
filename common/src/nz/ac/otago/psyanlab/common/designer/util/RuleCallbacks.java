
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.RuleDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Rule;

public interface RuleCallbacks {

    void addRuleDataChangeListener(RuleDataChangeListener listener);

    void deleteRule(long id);

    long createRule(Rule rule);

    Rule getRule(long ruleId);

    ProgramComponentAdapter<Rule> getRuleAdapter(long sceneId);

    void removeRuleDataChangeListener(RuleDataChangeListener listener);

    void updateRule(long id, Rule rule);

}
