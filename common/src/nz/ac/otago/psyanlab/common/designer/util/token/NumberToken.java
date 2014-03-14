
package nz.ac.otago.psyanlab.common.designer.util.token;

public class NumberToken extends LiteralToken {
    private static final String sValidCharacters = "0123456789.";

    public static boolean validates(int codepoint) {
        return Character.isDigit(codepoint) && isValidCharacter(codepoint);
    }

    private static boolean isValidCharacter(int codepoint) {
        return sValidCharacters.contains(new String(Character.toChars(codepoint)));
    }

    public NumberToken(int codepoint) {
        super(codepoint);
    }

    @Override
    public int eat(int codepoint) {
        int r = 0;

        if (isValidCharacter(codepoint)) {
            mStringRepresentation.appendCodePoint(codepoint);
            r |= RESULT_CHAR_CONSUMED;
        } else {
            r |= RESULT_TERMINAL_REACHED;
        }

        return r;
    }

    @Override
    public int getType() {
        return super.getType() | TOKEN_NUMBER;
    }
}
