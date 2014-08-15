
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

package nz.ac.otago.psyanlab.common.expression.parselets;

import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.Precedence;
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.TokenType;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.InfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.LinkExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.OperatorExpression;

/**
 * Parselet for the condition or "ternary" operator, like "a ? b : c".
 */
public class ComparisonParselet implements InfixParselet {
    @Override
    public Expression parse(Parser parser, Expression left, Token token) {
        Expression right = parser.parseExpression(Precedence.COMPARISON);

        if (left.getPrecedence() == Precedence.COMPARISON) {
            // 'and' the two comparisons and link the right side into our left
            // side.
            Expression link = new LinkExpression(((InfixExpression)left).getRight());
            Expression comparison = new InfixExpression(link, token.getType(), right,
                    Precedence.COMPARISON, OperatorExpression.ASSOCIATIVE_LEFT);
            return new InfixExpression(left, TokenType.AND, comparison, Precedence.AND,
                    OperatorExpression.ASSOCIATIVE_LEFT, true);
        }

        return new InfixExpression(left, token.getType(), right, Precedence.COMPARISON,
                OperatorExpression.ASSOCIATIVE_LEFT);
    }

    @Override
    public int getPrecedence() {
        return Precedence.COMPARISON;
    }
}
