
package nz.ac.otago.psyanlab.common.designer.util.token;

public class StringToken extends LiteralToken {
    public static boolean validates(int codepoint) {
        return codepoint == (int)'\"';
    }

    private boolean mEscaping;

    public StringToken() {
        super();
    }

    @Override
    public int eat(int codepoint) {
        int r = 0;
        r |= RESULT_CHAR_CONSUMED;
        if (!mEscaping && codepoint == (int)'\"') {
            r |= RESULT_TERMINAL_REACHED;
        } else if (!mEscaping && codepoint == (int)'\\') {
            mEscaping = true;
        } else {
            mEscaping = false;
            mStringRepresentation.appendCodePoint(codepoint);
        }
        return r;
    }

    @Override
    public int getType() {
        return super.getType() | TOKEN_STRING;
    }
}
