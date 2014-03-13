
package nz.ac.otago.psyanlab.common.designer.program.util;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.ActionDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.model.Action;

public interface ActionCallbacks {
    void addActionDataChangeListener(ActionDataChangeListener listener);

    long createAction(Action action);

    void deleteAction(long id);

    Action getAction(long id);

    ProgramComponentAdapter<Action> getActionAdapter(long ruleId);

    void removeActionDataChangeListener(ActionDataChangeListener listener);

    void updateAction(long id, Action action);
}
