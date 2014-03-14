package nz.ac.otago.psyanlab.common.designer.util.token;

import nz.ac.otago.psyanlab.common.designer.util.Token;

import android.text.TextUtils;

public class OperatorToken extends Token {
    private static final String[] sMultiCharOperators = new String[] {
            "<=", ">=", "<>",
    };

    private static final String sValidCharacters = "^*/%+-!=<>()[,]";

    public static boolean validates(int codepoint) {
        return sValidCharacters.contains(new String(Character.toChars(codepoint)));
    }

    public OperatorToken(int codepoint) {
        super(codepoint);
    }

    @Override
    public int eat(int codepoint) {
        int r = RESULT_TERMINAL_REACHED;
        StringBuilder builder = new StringBuilder(mStringRepresentation);
        String op = builder.appendCodePoint(codepoint).toString();
        for (int i = 0; i < sMultiCharOperators.length; i++) {
            if (TextUtils.equals(op, sMultiCharOperators[i])) {
                mStringRepresentation = builder;
                r |= RESULT_CHAR_CONSUMED;
            }
        }
        return r;
    }

    @Override
    public int getType() {
        return TOKEN_OPERATOR;
    }
}