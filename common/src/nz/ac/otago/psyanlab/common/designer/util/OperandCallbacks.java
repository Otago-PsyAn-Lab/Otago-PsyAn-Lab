
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.OperandDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Operand;

import android.support.v4.util.LongSparseArray;

public interface OperandCallbacks {
    long createOperand(Operand operand);

    void addOperandDataChangeListener(OperandDataChangeListener listener);

    /**
     * Clean up an operand adapter that is no longer needed.
     * 
     * @param adapter Adapter to clean up.
     */
    void discardOperandAdapter(ProgramComponentAdapter<Operand> adapter);

    void deleteOperand(long id);

    Operand getOperand(long id);

    ProgramComponentAdapter<Operand> getOperandAdapter(long scopeId);

    void removeOperandDataChangeListener(OperandDataChangeListener listener);

    void updateOperand(long id, Operand operand);

    LongSparseArray<Operand> getOperands();
}
