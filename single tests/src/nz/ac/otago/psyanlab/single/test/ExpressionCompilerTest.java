
package nz.ac.otago.psyanlab.single.test;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.OperandDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler;
import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler.TokenError;
import nz.ac.otago.psyanlab.common.designer.util.OperandCallbacks;
import nz.ac.otago.psyanlab.common.model.Operand;

import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;

public class ExpressionCompilerTest extends TestCase {
    private final class OperandCallbacksImplementation implements OperandCallbacks {
        @Override
        public void updateOperand(long id, Operand operand) {
            mOperands.put(id, operand);
        }

        @Override
        public void removeOperandDataChangeListener(OperandDataChangeListener listener) {
        }

        @Override
        public ProgramComponentAdapter<Operand> getOperandAdapter(long scopeId, int scope) {
            return null;
        }

        @Override
        public Operand getOperand(long id) {
            return mOperands.get(id);
        }

        @Override
        public void discardOperandAdapter(ProgramComponentAdapter<Operand> adapter) {
        }

        @Override
        public void deleteOperand(long id) {
            mOperands.remove(id);
        }

        @Override
        public long createOperand(Operand operand) {
            long id = findUnusedKey(mOperands);
            mOperands.put(id, operand);
            return id;
        }

        @Override
        public void addOperandDataChangeListener(OperandDataChangeListener listener) {
        }
    }

    protected LongSparseArray<Operand> mOperands;

    private OperandCallbacks mOperandCallbacks;

    protected Long findUnusedKey(LongSparseArray<?> map) {
        Long currKey = 0l;
        while (true) {
            if (map.indexOfKey(currKey) < 0) {
                break;
            }
            currKey++;
        }
        return currKey;
    }

    public ExpressionCompilerTest(String name) {
        super(name);

        mOperandCallbacks = new OperandCallbacksImplementation();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testExpression() {
        mOperands = new LongSparseArray<Operand>();
        ExpressionCompiler compiler = new ExpressionCompiler(mOperandCallbacks);
        HashMap<String, Long> operandIds = new HashMap<String, Long>();
        compiler.compile("a  +\"a string\"=b<=a and 3 = myInteger", operandIds);

        TokenError error = compiler.getError();

        Log.d("DEBUG PRETTY PRINT", compiler.formatExpression());

        if (error != null) {
            Log.d("DEBUG USER ERROR", error.tokenIndex + " " + error.errorString);
        }

        for (int i = 0; i < mOperands.size(); i++) {
            Log.d("DEBUG OPERAND TYPE", typeToString(mOperands.get(mOperands.keyAt(i)).type));
        }
    }

    private String typeToString(int type) {
        ArrayList<String> types = new ArrayList<String>();
        if ((type & Operand.TYPE_BOOLEAN) != 0) {
            types.add("boolean");
        }
        if ((type & Operand.TYPE_FLOAT) != 0) {
            types.add("float");
        }
        if ((type & Operand.TYPE_IMAGE) != 0) {
            types.add("image");
        }
        if ((type & Operand.TYPE_INTEGER) != 0) {
            types.add("integer");
        }
        if ((type & Operand.TYPE_SOUND) != 0) {
            types.add("sound");
        }
        if ((type & Operand.TYPE_STRING) != 0) {
            types.add("string");
        }
        if ((type & Operand.TYPE_VIDEO) != 0) {
            types.add("video asset");
        }

        return TextUtils.join(", ", types);
    }
}