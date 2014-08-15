
/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
 */

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

    /**
     * Check if parser completed reading all tokens available in the stream.
     * 
     * @return True if EOF was not reached.
     */
    public boolean areUnparsedTokens() {
        return mRead.get(0).getType() != TokenType.EOF;
    }

    public Token consume() {
        // Make sure we've read the token.
        lookAhead(0);

        return mRead.remove(0);
    }

    public Token consume(TokenType expected) {
        Token token = lookAhead(0);
        if (token.getType() != expected) {
            throw new ParseException("Expected token " + expected + ".");
        }

        return consume();
    }

    /**
     * Get the last token read in from the stream.
     * 
     * @return Last token read.
     */
    public Token getLastUnparsed() {
        if (mRead.size() == 0) {
            return new Token(TokenType.EOF, "");
        }
        return mRead.get(0);
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
            if (token.getText().equals("")) {
                throw new ParseException("Expected identity.");
            }
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
