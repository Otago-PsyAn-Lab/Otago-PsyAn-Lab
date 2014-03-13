
package nz.ac.otago.psyanlab.single.test;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.OperandDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler;
import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler.TokenError;
import nz.ac.otago.psyanlab.common.designer.util.OperandCallbacks;
import nz.ac.otago.psyanlab.common.model.Operand;

import android.support.v4.util.LongSparseArray;
import android.util.Log;

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
        compiler.compile("a  ++(+b)=3", operandIds);

        TokenError error = compiler.getError();

        Log.d("DEBUG", compiler.formatExpression());

        if (error != null) {
            Log.d("DEBUG", error.tokenIndex + " " + error.errorString);
        }

        assertEquals(mOperands.size(), 2);

    }
}
