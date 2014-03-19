
package nz.ac.otago.psyanlab.common.expression;

import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.parselets.InfixParselet;
import nz.ac.otago.psyanlab.common.expression.parselets.PrefixParselet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Parser {
    private final Map<TokenType, InfixParselet> mInfixParselets = new HashMap<TokenType, InfixParselet>();

    private final Map<TokenType, PrefixParselet> mPrefixParselets = new HashMap<TokenType, PrefixParselet>();

    private final List<Token> mRead = new ArrayList<Token>();

    private final Iterator<Token> mTokens;

    public Parser(Iterator<Token> tokens) {
        mTokens = tokens;
    }

    public Token consume() {
        // Make sure we've read the token.
        lookAhead(0);

        return mRead.remove(0);
    }

    public Token consume(TokenType expected) {
        Token token = lookAhead(0);
        if (token.getType() != expected) {
            throw new RuntimeException("Expected token " + expected + " and found "
                    + token.getType());
        }

        return consume();
    }

    public boolean match(TokenType expected) {
        Token token = lookAhead(0);
        if (token.getType() != expected) {
            return false;
        }

        consume();
        return true;
    }

    public Expression parseExpression() {
        return parseExpression(0);
    }

    public Expression parseExpression(int precedence) {
        Token token = consume();
        PrefixParselet prefix = mPrefixParselets.get(token.getType());

        if (prefix == null) {
            throw new ParseException("Could not parse \"" + token.getText() + "\".");
        }

        Expression left = prefix.parse(this, token);

        while (precedence < getPrecedence()) {
            token = consume();

            InfixParselet infix = mInfixParselets.get(token.getType());
            left = infix.parse(this, left, token);
        }

        return left;
    }

    public void register(TokenType token, InfixParselet parselet) {
        mInfixParselets.put(token, parselet);
    }

    public void register(TokenType token, PrefixParselet parselet) {
        mPrefixParselets.put(token, parselet);
    }

    private int getPrecedence() {
        TokenType type = lookAhead(0).getType();
        InfixParselet parser = mInfixParselets.get(type);
        if (parser != null) {
            return parser.getPrecedence();
        }

        return 0;
    }

    private Token lookAhead(int distance) {
        // Read in as many as needed.
        while (distance >= mRead.size()) {
            mRead.add(mTokens.next());
        }

        // Get the queued token.
        return mRead.get(distance);
    }
}
