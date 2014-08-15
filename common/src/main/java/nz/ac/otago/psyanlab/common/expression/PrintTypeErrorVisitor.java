
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

import nz.ac.otago.psyanlab.common.expression.RefineTypeVisitor.TypeError;
import nz.ac.otago.psyanlab.common.expression.expressions.BooleanExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.ConditionalExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.FloatExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.InfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.IntegerExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.NameExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PostfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PrefixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.StringExpression;

public class PrintTypeErrorVisitor extends PrintVisitor {
    private TypeError mError;

    private int mErrorEnd;

    private int mErrorStart;

    public PrintTypeErrorVisitor(TypeError error) {
        mError = error;
    }

    public int getErrorEnd() {
        return mErrorEnd;
    }

    public String getErrorMessage() {
        if (mError == null) {
            return "";
        }
        return mError.getErrorMessage();
    }

    public int getErrorStart() {
        return mErrorStart;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void visit(BooleanExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(ConditionalExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(FloatExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(InfixExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(IntegerExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(NameExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(PostfixExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(PrefixExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(StringExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }
}
