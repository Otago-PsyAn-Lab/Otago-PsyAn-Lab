
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.ActionDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.GeneratorDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.LoopDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.RuleDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.SceneDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Action;
import nz.ac.otago.psyanlab.common.model.Generator;
import nz.ac.otago.psyanlab.common.model.Loop;
import nz.ac.otago.psyanlab.common.model.Rule;
import nz.ac.otago.psyanlab.common.model.Scene;

import android.widget.ListAdapter;

public interface ProgramCallbacks {
    void addActionDataChangeListener(ActionDataChangeListener listener);

    void addGeneratorDataChangeListener(GeneratorDataChangeListener listener);

    void addLoopDataChangeListener(LoopDataChangeListener listener);

    void addRuleDataChangeListener(RuleDataChangeListener listener);

    void addSceneDataChangeListener(SceneDataChangeListener listener);

    long createAction(Action action);

    long createGenerator(Generator generator);

    long createLoop(Loop loop);

    long createRule(Rule rule);

    long createScene(Scene scene);

    void deleteAction(long id);

    void deleteGenerator(long id);

    void deleteLoop(long id);

    void deleteRule(long id);

    void deleteScene(long id);

    void editStage(long objectId);

    Action getAction(long id);

    ListAdapter getActionAdapter(long ruleId);

    Generator getGenerator(long id);

    ListAdapter getGeneratorAdapter(long loopId);

    Loop getLoop(long loopId);

    ListAdapter getLoopAdapter();

    Rule getRule(long ruleId);

    ListAdapter getRuleAdapter(long sceneId);

    Scene getScene(long sceneId);

    ListAdapter getScenesAdapter(long loopId);

    void removeActionDataChangeListener(ActionDataChangeListener listener);

    void removeGeneratorDataChangeListener(GeneratorDataChangeListener listener);

    void removeLoopDataChangeListener(LoopDataChangeListener listener);

    void removeRuleDataChangeListener(RuleDataChangeListener listener);

    void removeSceneDataChangeListener(SceneDataChangeListener listener);

    void updateAction(long id, Action action);

    void updateGenerator(long id, Generator generator);

    void updateLoop(long id, Loop loop);

    void updateRule(long id, Rule rule);

    void updateScene(long id, Scene scene);
}
