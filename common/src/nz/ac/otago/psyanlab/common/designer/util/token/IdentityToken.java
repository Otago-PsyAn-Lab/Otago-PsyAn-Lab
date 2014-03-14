
package nz.ac.otago.psyanlab.common.designer.util.token;

import nz.ac.otago.psyanlab.common.designer.util.Token;

public class IdentityToken extends Token {
    public static boolean validates(int codepoint) {
        return Character.isLetter(codepoint);
    }

    public IdentityToken(int codepoint) {
        super(codepoint);
    }

    @Override
    public int eat(int codepoint) {
        int r = 0;
        if (Character.isLetterOrDigit(codepoint)) {
            mStringRepresentation.appendCodePoint(codepoint);
            r |= RESULT_CHAR_CONSUMED;
        } else {
            r |= RESULT_TERMINAL_REACHED;

        }
        return r;
    }

    @Override
    public int getType() {
        return TOKEN_IDENTITY;
    }
}
