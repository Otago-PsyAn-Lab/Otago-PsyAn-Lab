
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

import nz.ac.otago.psyanlab.common.expression.parselets.BinaryOperatorParselet;
import nz.ac.otago.psyanlab.common.expression.parselets.ComparisonParselet;
import nz.ac.otago.psyanlab.common.expression.parselets.ConditionalParselet;
import nz.ac.otago.psyanlab.common.expression.parselets.FloatParselet;
import nz.ac.otago.psyanlab.common.expression.parselets.GroupParselet;
import nz.ac.otago.psyanlab.common.expression.parselets.IntegerParselet;
import nz.ac.otago.psyanlab.common.expression.parselets.NameParselet;
import nz.ac.otago.psyanlab.common.expression.parselets.PostfixOperatorParselet;
import nz.ac.otago.psyanlab.common.expression.parselets.PrefixOperatorParselet;
import nz.ac.otago.psyanlab.common.expression.parselets.StringParselet;
import nz.ac.otago.psyanlab.common.expression.parselets.SubstringParselet;

/**
 * Extends the generic Parser class with support for parsing the actual Bantam
 * grammar.
 */
public class OpalExpressionParser extends Parser {
    public OpalExpressionParser(Lexer lexer) {
        super(lexer);

        // Register all of the parselets for the grammar.

        // Register the ones that need special parselets.
        register(TokenType.NAME, new NameParselet());
        register(TokenType.NOT_EQUALS, new ComparisonParselet());
        register(TokenType.EQUALS, new ComparisonParselet());
        register(TokenType.MORE_THAN, new ComparisonParselet());
        register(TokenType.MORE_THAN_OR_EQUAL_TO, new ComparisonParselet());
        register(TokenType.LESS_THAN, new ComparisonParselet());
        register(TokenType.LESS_THAN_OR_EQUAL_TO, new ComparisonParselet());
        register(TokenType.QUESTION, new ConditionalParselet());
        register(TokenType.LEFT_BRACKET, new SubstringParselet());
        register(TokenType.INTEGER, new IntegerParselet());
        register(TokenType.FLOAT, new FloatParselet());
        register(TokenType.STRING, new StringParselet());
        register(TokenType.LEFT_PAREN, new GroupParselet());

        // Register the simple operator parselets.
        prefix(TokenType.PLUS, Precedence.PREFIX);
        prefix(TokenType.MINUS, Precedence.PREFIX);
        prefix(TokenType.BANG, Precedence.PREFIX);

        infixLeft(TokenType.AND, Precedence.AND);
        infixLeft(TokenType.XOR, Precedence.XOR);
        infixLeft(TokenType.OR, Precedence.OR);
        infixLeft(TokenType.PLUS, Precedence.SUM);
        infixLeft(TokenType.MINUS, Precedence.SUM);
        infixLeft(TokenType.PERCENT, Precedence.PRODUCT);
        infixLeft(TokenType.ASTERISK, Precedence.PRODUCT);
        infixLeft(TokenType.SLASH, Precedence.PRODUCT);
        infixRight(TokenType.CARET, Precedence.EXPONENT);
    }

    /**
     * Registers a postfix unary operator parselet for the given token and
     * precedence.
     */
    public void postfix(TokenType token, int precedence) {
        register(token, new PostfixOperatorParselet(precedence));
    }

    /**
     * Registers a prefix unary operator parselet for the given token and
     * precedence.
     */
    public void prefix(TokenType token, int precedence) {
        register(token, new PrefixOperatorParselet(precedence));
    }

    /**
     * Registers a left-associative binary operator parselet for the given token
     * and precedence.
     */
    public void infixLeft(TokenType token, int precedence) {
        register(token, new BinaryOperatorParselet(precedence, false));
    }

    /**
     * Registers a right-associative binary operator parselet for the given
     * token and precedence.
     */
    public void infixRight(TokenType token, int precedence) {
        register(token, new BinaryOperatorParselet(precedence, true));
    }
}
