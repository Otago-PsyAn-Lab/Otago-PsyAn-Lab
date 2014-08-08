/*
 * Copyright (C) 2014 Tonic Artos <tonic.artos@gmail.com>
 *
 * Otago PsyAn Lab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * In accordance with Section 7(b) of the GNU General Public License version 3,
 * all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.common.designer.variable;

import android.support.v4.widget.DrawerLayout;
import android.widget.ListAdapter;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity;
import nz.ac.otago.psyanlab.common.model.Variable;

public interface VariableCallbacks {

    /**
     * Add a listener that will be called when the experiment asset data set is modified. The
     * listener must be removed before the fragment or activity is finished or there will be crashes
     * from calls to dangling references.
     *
     * @param listener Listener for data changes in the asset data set.
     */
    void addVariableDataChangeListener(
            ExperimentDesignerActivity.VariableDataChangeListener listener);

    void addDrawerListener(DrawerLayout.DrawerListener listener);

    /**
     * Add new variable.
     *
     * @param variable New variable.
     */
    long addVariable(Variable variable);

    /**
     * Delete indicated variable.
     *
     * @param id Id of variable to be deleted.
     */
    void deleteVariable(long id);

    /**
     * Get the indicated variable.
     *
     * @param id Id of variable to get.
     * @return Variable desired.
     */
    Variable getVariable(long id);

    /**
     * Get an adapter for the entire set of variables in the experiment.
     *
     * @return Variables adapter.
     */
    ListAdapter getVariablesAdapter();

    /**
     * Replace an existing variable with a new one.
     *
     * @param id       Id of variable to be replaced.
     * @param variable New variable to be stored.
     */
    void putVariable(long id, Variable variable);

    /**
     * Remove a listener from the set of variable data change listeners. This must be called before
     * an activity or fragment finishes to clean up dangling references.
     */
    void removeVariableDataChangeListener(
            ExperimentDesignerActivity.VariableDataChangeListener listener);

    void removeDrawerListener(DrawerLayout.DrawerListener drawerListener);

}
