
/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
 */

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
