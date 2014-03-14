
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.util.token.IdentityToken;
import nz.ac.otago.psyanlab.common.designer.util.token.NumberToken;
import nz.ac.otago.psyanlab.common.designer.util.token.OperatorToken;
import nz.ac.otago.psyanlab.common.designer.util.token.StringToken;
import nz.ac.otago.psyanlab.common.designer.util.token.UnknownToken;

public abstract class Token {
    public static final int RESULT_CHAR_CONSUMED = 0x01;

    public static final int RESULT_TERMINAL_REACHED = 0x02;

    public static final int TOKEN_DECIMAL = 0x01;

    public static final int TOKEN_IDENTITY = 0x02;

    public static final int TOKEN_INTEGER = 0x04;

    public static final int TOKEN_LITERAL = 0x08;

    public static final int TOKEN_NUMBER = 0x10;

    public static final int TOKEN_OPERATOR = 0x20;

    public static final int TOKEN_STRING = 0x40;

    public static final int TOKEN_UNKNOWN = 0x100;

    static public Token newToken(int codepoint) {
        Token token = null;
        if (!Character.isWhitespace(codepoint)) {
            if (StringToken.validates(codepoint)) {
                token = new StringToken();
            } else if (NumberToken.validates(codepoint)) {
                token = new NumberToken(codepoint);
            } else if (IdentityToken.validates(codepoint)) {
                token = new IdentityToken(codepoint);
            } else if (OperatorToken.validates(codepoint)) {
                token = new OperatorToken(codepoint);
            } else {
                token = new UnknownToken(codepoint);
            }
        }
        return token;
    }

    private boolean mError;

    private String mErrorString;

    protected StringBuilder mStringRepresentation = new StringBuilder();

    public Token(int codepoint) {
        mStringRepresentation.appendCodePoint(codepoint);
    }

    public Token() {
    }

    public abstract int eat(int codepoint);

    public String getError() {
        return mErrorString;
    }

    public String getString() {
        return mStringRepresentation.toString();
    }

    public boolean hasError() {
        return mError;
    }

    public void markError(String error) {
        mError = true;
        mErrorString = error;
    }

    @Override
    public String toString() {
        return mStringRepresentation.toString();
    }

    public abstract int getType();
}
