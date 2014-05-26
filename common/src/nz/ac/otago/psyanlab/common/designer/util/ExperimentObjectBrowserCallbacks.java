package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.util.ArrayFragmentMapAdapter.Factory;
import nz.ac.otago.psyanlab.common.model.ExperimentObject;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public interface ExperimentObjectBrowserCallbacks {
    ExperimentObject getExperimentObject(ExperimentObjectReference object);

    ExperimentObjectAdapter getObjectSectionListAdapter(long sceneId, int section, int filter);

    FragmentPagerAdapter getObjectsPagerAdapter(FragmentManager fm, long sceneId, Factory factory);

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
