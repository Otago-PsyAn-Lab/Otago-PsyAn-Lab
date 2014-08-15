
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

import nz.ac.otago.psyanlab.common.expression.expressions.BooleanExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.ConditionalExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.ExpressionVisitor;
import nz.ac.otago.psyanlab.common.expression.expressions.FloatExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.InfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.IntegerExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.LinkExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.NameExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.OperatorExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PostfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PrefixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.StringExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.SubstringExpression;

import android.util.Log;

/**
 * Print an expression tree while maintaining a relative caret position.
 */
public class PrintWithCaretVisitor implements ExpressionVisitor {
    private int mCaretPosition;

    private boolean mFoundCaret;

    private int mOffset;

    private int mOldCaretPosition;

    private String mOriginalString;

    private int mPrecedence;

    protected StringBuilder mBuilder;

    public PrintWithCaretVisitor(int oldCaretPosition, String originalString) {
        mOldCaretPosition = oldCaretPosition;
        mOriginalString = originalString;
        mBuilder = new StringBuilder();
    }

    public int getCaretPosition() {
        if (!mFoundCaret) {
            return mBuilder.length();
        }
        return mCaretPosition;
    }

    @Override
    public String toString() {
        return mBuilder.toString();
    }

    @Override
    public void visit(BooleanExpression expression) {
        addString(expression.getValueString());
    }

    @Override
    public void visit(ConditionalExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            addString("(");
        }

        mPrecedence = precedence;
        expression.getCondition().accept(this);

        addString(" ");
        addString("?");
        addString(" ");

        mPrecedence = precedence;
        expression.getThenArm().accept(this);

        addString(" ");
        addString(":");
        addString(" ");

        mPrecedence = precedence;
        expression.getElseArm().accept(this);

        if (groupedExpression) {
            addString(")");
        }
    }

    @Override
    public void visit(FloatExpression expression) {
        addString(expression.getValueString());
    }

    @Override
    public void visit(InfixExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            addString("(");
        }

        mPrecedence = precedence;
        expression.getLeft().accept(this);

        if (!expression.isVirtual()) {
            addString(" ");
            addString(expression.getOperator().toString());
            addString(" ");
        }

        mPrecedence = precedence;
        if (expression.getAssociativity() == OperatorExpression.ASSOCIATIVE_LEFT) {
            mPrecedence++;
        }
        expression.getRight().accept(this);

        if (groupedExpression) {
            addString(")");
        }
    }

    @Override
    public void visit(IntegerExpression expression) {
        addString(expression.getValueString());
    }

    @Override
    public void visit(LinkExpression expression) {
        // Do nothing because this link will be printed elsewhere.
    }

    @Override
    public void visit(NameExpression expression) {
        addString(expression.getName());
    }

    @Override
    public void visit(PostfixExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            addString("(");
        }

        mPrecedence = precedence;
        expression.getLeft().accept(this);
        addString(expression.getOperator().toString());

        if (groupedExpression) {
            addString(")");
        }
    }

    @Override
    public void visit(PrefixExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            addString("(");
        }

        addString(expression.getOperator().toString());
        mPrecedence = precedence;
        expression.getRight().accept(this);

        if (groupedExpression) {
            addString(")");
        }
    }

    @Override
    public void visit(StringExpression expression) {
        addString(expression.getString());
    }

    @Override
    public void visit(SubstringExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            addString("(");
        }

        mPrecedence = precedence;
        expression.getString().accept(this);

        addString("[");

        mPrecedence = precedence;
        expression.getLow().accept(this);

        addString(",");
        addString(" ");

        mPrecedence = precedence;
        expression.getHigh().accept(this);

        addString("]");

        if (groupedExpression) {
            addString(")");
        }
    }

    private void addString(String s) {
        Log.d("CARET", "OLD CARET: " + mOldCaretPosition);
        Log.d("CARET", "OFFSET: " + mOffset);
        mBuilder.append(s);
        if (!mFoundCaret && !s.equals(" ")) {
            // Look for the next instance of the added string in the original
            // expression.
            mOffset = mOriginalString.indexOf(s, mOffset);
            if (mOldCaretPosition - mOffset < s.length()) {
                // Caret was within the token/expression being formatted.
                mFoundCaret = true;
            }
            mOffset += s.length();
            if (mFoundCaret) {
                // Position the caret within the new string.
                mCaretPosition = mBuilder.length() + mOldCaretPosition - mOffset;
                Log.d("CARET", "CARET: " + mCaretPosition);
            }
        }
    }
}
