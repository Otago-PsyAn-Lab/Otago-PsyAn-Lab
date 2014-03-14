
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler.TokenError;
import nz.ac.otago.psyanlab.common.model.ExpressionNode.ParseException;

import java.util.ArrayList;
import java.util.ListIterator;

class ExpressionLexer {
    private Token mCurrentToken;

    private ExpressionParser mParser;

    private ArrayList<Token> mTokens;

    public ExpressionLexer(ExpressionParser parser) {
        mParser = parser;
    }

    public String formatExpression() {
        return mParser.formatExpression();
        // StringBuilder sb = new StringBuilder();
        // for (Token token : mTokens) {
        // sb.append(token.toString());
        // }
        // return sb.toString();
    }

    public TokenError getError() {
        for (ListIterator<Token> iterator = mTokens.listIterator(); iterator.hasNext();) {
            Token token = iterator.next();
            if (token.hasError()) {
                return new TokenError(iterator.nextIndex() - 1, token.getError());
            }
        }

        return null;
    }

    public ArrayList<Token> getTokens() {
        return mTokens;
    }

    public void lex(String expression) throws ParseException {
        mTokens = new ArrayList<Token>();
        final int length = expression.length();
        for (int offset = 0; offset < length;) {
            final int codepoint = expression.codePointAt(offset);
            processCodepoint(codepoint);
            offset += Character.charCount(codepoint);
        }
        finish();
    }

    private void finish() throws ParseException {
        if (mCurrentToken != null) {
            storeToken();
        }
        mParser.completeTree();
        // Log.d("DEBUG TREE", mParser.printState());
    }

    private void processCodepoint(final int codepoint) throws ParseException {
        // Log.d("DEBUG CODEPOINT", "" + (char)codepoint);
        if (mCurrentToken == null) {
            mCurrentToken = Token.newToken(codepoint);
        } else {
            int result = mCurrentToken.eat(codepoint);
            if ((result & Token.RESULT_TERMINAL_REACHED) != 0) {
                storeToken();
                if ((result & Token.RESULT_CHAR_CONSUMED) == 0) {
                    mCurrentToken = Token.newToken(codepoint);
                }
            }
        }
    }

    private void storeToken() throws ParseException {
        // Log.d("DEBUG TOKEN", mCurrentToken.toString());
        mParser.addToken(mCurrentToken);
        // Log.d("DEBUG TREE", mParser.printState());
        mTokens.add(mCurrentToken);
        mCurrentToken = null;
    }
}
