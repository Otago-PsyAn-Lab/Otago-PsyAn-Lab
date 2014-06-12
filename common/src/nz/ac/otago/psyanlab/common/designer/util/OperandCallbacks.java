
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.OperandDataChangeListener;
import nz.ac.otago.psyanlab.common.model.ExperimentObject;
import nz.ac.otago.psyanlab.common.model.Operand;

import android.widget.SpinnerAdapter;

import java.util.HashMap;

public interface OperandCallbacks {
    /**
     * Create the given operand within the experiment.
     * 
     * @param operand Operand to be created.
     * @return Id of the newly created operand.
     */
    long addOperand(Operand operand);

    /**
     * Add a listener to be notified whenever the set of operands is modified.
     * 
     * @param listener Listener that will be notified.
     */
    void addOperandDataChangeListener(OperandDataChangeListener listener);

    /**
     * Clean up an operand adapter that is no longer needed.
     * 
     * @param adapter Adapter to clean up.
     */
    void discardOperandAdapter(ProgramComponentAdapter<Operand> adapter);

    /**
     * Delete an operand if it exists.
     * 
     * @param id Id of operand to delete.
     */
    void deleteOperand(long id);

    /**
     * Get an operand specified by the id.
     * 
     * @param id Id of desired operand.
     * @return Operand request.
     */
    Operand getOperand(long id);

    /**
     * Get an adapter for the operands nested within the operand indicated
     * 
     * @param parentId Id of parent operand.
     * @return Adapter mediating access to the child operands of the specified
     *         parent.
     */
    ProgramComponentAdapter<Operand> getOperandAdapter(long parentId);

    /**
     * Remove an operand data change listener.
     * 
     * @param listener Listener to remove.
     */
    void removeOperandDataChangeListener(OperandDataChangeListener listener);

    /**
     * Replace an existing operand with a new one.
     * 
     * @param id Id of operand to replace.
     * @param operand New operand to store.
     */
    void putOperand(long id, Operand operand);

    /**
     * Get the entire map of operands in the experiment.
     * 
     * @return Map containing the entire set of operands in the experiment.
     */
    HashMap<Long, Operand> getOperands();

    /**
     * Get a methods adapter for the given type. NOTE: This is a little strange
     * here but it is convenient. The methods the adapter accesses are virtual,
     * that is they are either stubs actually defined on the class or are
     * constructed from user data. This adapter is intended only for use in
     * displaying a programming API to the user, not for the navigation of one
     * and actually perform said calls within this program.
     * 
     * @param clazz Class to look into for methods.
     * @param returnTypes Desired set of return types to match methods onto.
     * @return An adapter that mediates access to the desired set of methods
     *         defined on the given class.
     */
    SpinnerAdapter getMethodsAdapter(ExperimentObject object, int returnTypes);
}
