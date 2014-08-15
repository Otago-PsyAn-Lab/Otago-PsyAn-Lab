
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

public class PrintVisitor implements ExpressionVisitor {
    private int mPrecedence;

    protected StringBuilder mBuilder;

    public PrintVisitor() {
        mBuilder = new StringBuilder();
    }

    @Override
    public String toString() {
        return mBuilder.toString();
    }

    @Override
    public void visit(BooleanExpression expression) {
        mBuilder.append(expression.getValueString());
    }

    @Override
    public void visit(ConditionalExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            mBuilder.append("(");
        }

        mPrecedence = precedence;
        expression.getCondition().accept(this);

        mBuilder.append(" ? ");

        mPrecedence = precedence;
        expression.getThenArm().accept(this);

        mBuilder.append(" : ");

        mPrecedence = precedence;
        expression.getElseArm().accept(this);

        if (groupedExpression) {
            mBuilder.append(")");
        }
    }

    @Override
    public void visit(FloatExpression expression) {
        mBuilder.append(expression.getValueString());
    }

    @Override
    public void visit(InfixExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            mBuilder.append("(");
        }

        mPrecedence = precedence;
        expression.getLeft().accept(this);

        if (!expression.isVirtual()) {
            mBuilder.append(" " + expression.getOperator() + " ");
        }

        mPrecedence = precedence;
        if (expression.getAssociativity() == OperatorExpression.ASSOCIATIVE_LEFT) {
            mPrecedence++;
        }
        expression.getRight().accept(this);

        if (groupedExpression) {
            mBuilder.append(")");
        }
    }

    @Override
    public void visit(IntegerExpression expression) {
        mBuilder.append(expression.getValueString());
    }

    @Override
    public void visit(LinkExpression expression) {
        // Do nothing because this link will be printed elsewhere.
    }

    @Override
    public void visit(NameExpression expression) {
        mBuilder.append(expression.getName());
    }

    @Override
    public void visit(PostfixExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            mBuilder.append("(");
        }

        mPrecedence = precedence;
        expression.getLeft().accept(this);
        mBuilder.append(expression.getOperator());

        if (groupedExpression) {
            mBuilder.append(")");
        }
    }

    @Override
    public void visit(PrefixExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            mBuilder.append("(");
        }

        mBuilder.append(expression.getOperator());
        mPrecedence = precedence;
        expression.getRight().accept(this);

        if (groupedExpression) {
            mBuilder.append(")");
        }
    }

    @Override
    public void visit(StringExpression expression) {
        mBuilder.append(expression.getString());
    }

    @Override
    public void visit(SubstringExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            mBuilder.append("(");
        }

        mPrecedence = precedence;
        expression.getString().accept(this);

        mBuilder.append("[");

        mPrecedence = precedence;
        expression.getLow().accept(this);

        mBuilder.append(", ");

        mPrecedence = precedence;
        expression.getHigh().accept(this);

        mBuilder.append("]");

        if (groupedExpression) {
            mBuilder.append(")");
        }
    }
}
