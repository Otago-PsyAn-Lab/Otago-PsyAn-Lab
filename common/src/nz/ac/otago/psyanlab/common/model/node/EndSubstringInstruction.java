
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;
import nz.ac.otago.psyanlab.common.model.ExpressionNode;

import android.text.TextUtils;

public class EndSubstringInstruction extends ExpressionNode {
    private static final String SYMBOL = "]";

    static public boolean kindOf(Token token) {
        return TextUtils.equals(SYMBOL, token.getString());
    }

    public EndSubstringInstruction() {
        super(SYMBOL, -1);
    }

    @Override
    public ExpressionNode addNodeFromLeft(ExpressionNode node) throws ParseException {
        return null;
    }

    @Override
    public ExpressionNode addNodeFromRight(ExpressionNode node) throws ParseException {
        return null;
    }

    @Override
    public String printGraph() {
        return SYMBOL;
    }

    @Override
    public String toString() {
        return SYMBOL;
    }

    @Override
    protected Object evaluateImplementation() {
        return null;
    }
}
