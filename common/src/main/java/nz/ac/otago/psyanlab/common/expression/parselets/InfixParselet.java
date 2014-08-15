
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
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;

/**
 * One of the two parselet interfaces used by the Pratt parser. An InfixParselet
 * is associated with a token that appears in the middle of the expression it
 * parses. Its parse() method will be called after the left-hand side has been
 * parsed, and it in turn is responsible for parsing everything that comes after
 * the token. This is also used for postfix expressions, in which case it simply
 * doesn't consume any more tokens in its parse() call.
 */
public interface InfixParselet {
    Expression parse(Parser parser, Expression left, Token token);

    int getPrecedence();
}
