
package nz.ac.otago.psyanlab.common.designer.util.token;

import nz.ac.otago.psyanlab.common.designer.util.Token;

public abstract class LiteralToken extends Token {
    public LiteralToken() {
        super();
    }

    public LiteralToken(int codepoint) {
        super(codepoint);
    }

    @Override
    public int getType() {
        return TOKEN_LITERAL;
    }
}
