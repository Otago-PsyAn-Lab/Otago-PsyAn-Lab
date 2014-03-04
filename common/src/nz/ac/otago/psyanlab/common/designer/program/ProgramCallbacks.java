
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.ActionDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.GeneratorDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.LoopDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.RuleDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.SceneDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.designer.util.ArrayFragmentMapAdapter.Factory;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar;
import nz.ac.otago.psyanlab.common.designer.util.ExperimentObjectAdapter;
import nz.ac.otago.psyanlab.common.model.Action;
import nz.ac.otago.psyanlab.common.model.ExperimentObject;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.Generator;
import nz.ac.otago.psyanlab.common.model.Loop;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.Rule;
import nz.ac.otago.psyanlab.common.model.Scene;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;

public interface ProgramCallbacks extends DialogueResultListenerRegistrar {
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

    ProgramComponentAdapter<Action> getActionAdapter(long ruleId);

    SpinnerAdapter getEventsAdapter(Class<?> clazz);

    ExperimentObject getExperimentObject(ExperimentObjectReference object);

    Generator getGenerator(long id);

    ProgramComponentAdapter<Generator> getGeneratorAdapter(long loopId);

    Loop getLoop(long loopId);

    ProgramComponentAdapter<Loop> getLoopAdapter();

    SpinnerAdapter getMethodsAdapter(Class<?> clazz, Class<?> returnType);

    ExperimentObjectAdapter getObjectSectionListAdapter(long sceneId, int section, int filter);

    FragmentPagerAdapter getObjectsPagerAdapter(FragmentManager fm, long sceneId, Factory factory);

    ArrayList<Prop> getPropsArray(long stageId);

    Rule getRule(long ruleId);

    ProgramComponentAdapter<Rule> getRuleAdapter(long sceneId);

    Scene getScene(long sceneId);

    ProgramComponentAdapter<Scene> getScenesAdapter(long loopId);

    /**
     * Open UI to pick an Experiment Object. Result is notified through listener
     * interface set via
     * {@link #registerDialogueResultListener(int, nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar.DialogueResultListener)
     * registerDialogueResultListener} .
     * 
     * @param sceneId Id of the scene in which the object will be found.
     * @param filter See {@link ExperimentObjectReference
     *            ExperimentObjectReference} for types of object that can be
     *            filtered on.
     * @param requestCode Request to track which listeners are called when the
     *            object has been picked.
     */
    void pickExperimentObject(long sceneId, int filter, int requestCode);

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
