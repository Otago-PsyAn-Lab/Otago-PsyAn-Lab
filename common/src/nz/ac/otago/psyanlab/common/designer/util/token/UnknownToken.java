
package nz.ac.otago.psyanlab.common.designer.util.token;

import nz.ac.otago.psyanlab.common.designer.util.Token;

public class UnknownToken extends Token {
    public UnknownToken(int codepoint) {
        super(codepoint);
    }

    @Override
    public int eat(int codepoint) {
        int r = 0;
        if (Character.isWhitespace(codepoint)) {
            r |= RESULT_TERMINAL_REACHED;
        } else {
            mStringRepresentation.appendCodePoint(codepoint);
        }
        return r;
    }

    @Override
    public int getType() {
        return TOKEN_UNKNOWN;
    }
}
