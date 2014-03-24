
package nz.ac.otago.psyanlab.common.model.operand;

import nz.ac.otago.psyanlab.common.model.Operand;

public class StubOperand extends Operand {
    public StubOperand(String name) {
        this.name = name;
        type = TYPE_ANY;
    }

    @Override
    public boolean attemptRestrictType(int type) {
        this.type = type;
        return true;
    }
}
