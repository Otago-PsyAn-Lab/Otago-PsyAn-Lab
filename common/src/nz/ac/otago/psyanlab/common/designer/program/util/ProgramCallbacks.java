
package nz.ac.otago.psyanlab.common.designer.program.util;

import nz.ac.otago.psyanlab.common.designer.util.ActionCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.ArrayFragmentMapAdapter.Factory;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar;
import nz.ac.otago.psyanlab.common.designer.util.ExperimentObjectAdapter;
import nz.ac.otago.psyanlab.common.designer.util.GeneratorCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.LoopCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.OperandCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.RuleCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.SceneCallbacks;
import nz.ac.otago.psyanlab.common.model.ExperimentObject;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.Prop;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;

/**
 * Collection of all the call-backs used toimplement the program editor
 * interface.
 */
public interface ProgramCallbacks extends DialogueResultListenerRegistrar, OperandCallbacks,
        ActionCallbacks, GeneratorCallbacks, LoopCallbacks, RuleCallbacks, SceneCallbacks {
    void editStage(long id);

    SpinnerAdapter getEventsAdapter(Class<?> clazz);

    ExperimentObject getExperimentObject(ExperimentObjectReference object);

    SpinnerAdapter getMethodsAdapter(Class<?> clazz, int returnTypes);

    ExperimentObjectAdapter getObjectSectionListAdapter(long sceneId, int section, int filter);

    FragmentPagerAdapter getObjectsPagerAdapter(FragmentManager fm, long sceneId, Factory factory);

    Prop getProp(long id);

    ArrayList<Prop> getPropsArray(long stageId);

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
}
