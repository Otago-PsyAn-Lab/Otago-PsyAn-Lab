
package nz.ac.otago.psyanlab.common.model.operand;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.util.Type;

public class StubOperand extends Operand {
    public StubOperand(String name) {
        this.name = name;
        type = Type.TYPE_ANY;
    }

    @Override
    public boolean attemptRestrictType(int type) {
        this.type = type;
        return true;
    }
}
