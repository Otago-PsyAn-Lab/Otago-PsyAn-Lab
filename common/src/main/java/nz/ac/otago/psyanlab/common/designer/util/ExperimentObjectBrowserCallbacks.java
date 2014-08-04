
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.model.ExperimentObject;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public interface ExperimentObjectBrowserCallbacks {
    ExperimentObject getExperimentObject(ExperimentObjectReference object);

    /**
     * Get a list adapter for objects as selected by the combination of caller
     * kind, relative scope level, and a filter value.
     * 
     * @param callerKind The kind of the caller.
     * @param callerId The caller's id.
     * @param scope The relative scope level. 0 is the same scope as the caller,
     *            1 is one level up the scope hierarchy.
     * @param filter A filter value applied to objects in scope.
     * @return The adapter.
     */
    ExperimentObjectReferenceAdapter getObjectSectionListAdapter(int callerKind, long callerId, int scope,
            int filter);

    /**
     * Get an adapter that provides pages of objects to browse as implemented
     * through a caller provided factory.
     * 
     * @param fm Fragment manager for building and adding fragment pages.
     * @param callerKind The kind of the caller.
     * @param callerId The caller's id.
     * @param filter A filter value which describes the desired set composition.
     * @param fragmentFactory A factory which generates the fragments that will
     *            represent the pages.
     * @return The adapter.
     */
    FragmentPagerAdapter getObjectBrowserPagerAdapter(FragmentManager fm, int callerKind,
            long callerId, int filter, ArrayFragmentMapAdapter.FragmentFactory fragmentFactory);

    /**
     * Open UI to pick an Experiment Object. Result is notified through listener
     * interface set via
     * {@link #registerDialogueResultListener(int, nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar.DialogueResultListener)
     * registerDialogueResultListener}.
     * 
     * @param callerKind The kind of the caller.
     * @param callerId The id of the caller.
     * @param filter See {@link ExperimentObjectReference
     *            ExperimentObjectReference} for types of object that can be
     *            filtered on.
     * @param requestCode Request to track which listeners are called when the
     *            object has been picked.
     */
    void pickExperimentObject(int callerKind, long callerId, int filter, int requestCode);
}
