
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.model.ExpressionNode.ParseException;

import java.util.HashMap;

public class ExpressionCompiler {

    private OperandCallbacks mCallbacks;

    private ExpressionLexer mLexer;

    private ParseException mError;

    public ExpressionCompiler(OperandCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    public boolean compile(String expression, HashMap<String, Long> operandIds) {
        ExpressionParser parser = new ExpressionParser(mCallbacks, operandIds);
        mLexer = new ExpressionLexer(parser);
        try {
            mLexer.lex(expression);
        } catch (ParseException e) {
            mError = e;
            return false;
        }
        return true;
    }

    public String formatExpression() {
        return mLexer.formatExpression();
    }

    public TokenError getError() {
        return mLexer.getError();
    }

    public static class TokenError {
        public String errorString;

        public int tokenIndex;

        public TokenError(int i, String error) {
            tokenIndex = i;
            errorString = error;
        }
    }
}
