
package nz.ac.otago.psyanlab.common.model.operand;


import nz.ac.otago.psyanlab.common.model.Operand;

/**
 * A kind of operand that has no value but encodes multiple type potentials.
 */
public class ConstraintOperand extends Operand {
    public ConstraintOperand() {
    }

    public void setTypes(int... types) {
        type = 0;
        for (int i = 0; i < types.length; i++) {
            type = type | types[i];
        }
    }

    @Override
    public int type() {
        return type;
    }
}
