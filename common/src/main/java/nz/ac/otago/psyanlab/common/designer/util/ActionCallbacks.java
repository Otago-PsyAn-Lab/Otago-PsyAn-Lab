
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

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.ActionDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Action;

public interface ActionCallbacks {
    /**
     * Add an action to the experiment.
     * 
     * @param action Action to add.
     * @return New id for the added action.
     */
    long addAction(Action action);

    /**
     * Add a listener for action data changes. The listener must be remove
     * before the fragment or activity finished in order to prevent dangling
     * reference from being called.
     * 
     * @param listener Listener to add.
     */
    void addActionDataChangeListener(ActionDataChangeListener listener);

    /**
     * Delete indicated action from the experiment and clean up any dependent
     * parts of the experiment.
     * 
     * @param id Id of action to delete.
     */
    void deleteAction(long id);

    /**
     * Get an action in the experiment.
     * 
     * @param id Id of action.
     * @return Action desired.
     */
    Action getAction(long id);

    /**
     * Get an adapter for all actions in the indicated rule.
     * 
     * @param ruleId Id of rule to which the actions belong.
     * @return Adapter for actions within the indicated rule.
     */
    ProgramComponentAdapter<Action> getActionAdapter(long ruleId);

    /**
     * Replace an action in the experiment.
     * 
     * @param id Id of action to replace.
     * @param action New action.
     */
    void putAction(long id, Action action);

    /**
     * Remove listener from those called when the action dataset is modified.
     * 
     * @param listener Listener to remove.
     */
    void removeActionDataChangeListener(ActionDataChangeListener listener);
}
