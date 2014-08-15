
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

package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.CallOperand;

import java.util.ArrayList;

public class CallValue extends Operand implements CallOperand {
    public static final int INVALID_METHOD = -1;

    @Expose
    public int method;

    @Expose
    public ExperimentObjectReference object;

    @Expose
    public ArrayList<Long> parameters = new ArrayList<Long>();

    @Expose
    public StubOperand originalStub;

    public CallValue() {
    }

    public CallValue(Operand operand) {
        super(operand);
        method = INVALID_METHOD;

        if (operand instanceof StubOperand) {
            originalStub = (StubOperand)operand;
        }
    }

    @Override
    public int getMethod() {
        return method;
    }

    @Override
    public ExperimentObjectReference getObject() {
        return object;
    }

    @Override
    public ArrayList<Long> getOperands() {
        return parameters;
    }

    @Override
    public int getType() {
        return type;
    }
}
