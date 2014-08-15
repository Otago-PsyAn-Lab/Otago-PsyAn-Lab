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

package nz.ac.otago.psyanlab.common.model.variable;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Variable;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.ParameterId;
import nz.ac.otago.psyanlab.common.model.util.Type;

public class IntegerVariable extends Variable {
    @Expose
    int value;

    public IntegerVariable() {}

    public IntegerVariable(IntegerVariable variable) {
        super(variable);
        value = variable.value;
    }

    public IntegerVariable(Variable variable) {
        super(variable);
    }

    @MethodId(METHOD_SET_AND_USE)
    public int chainSetVariableValue(@ParameterId(PARAM_VALUE) int value) {
        return value;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }

    @Override
    public int getType() {
        return Type.TYPE_INTEGER;
    }

    @MethodId(METHOD_GET)
    public int getVariableValue() {
        return value;
    }

    @MethodId(METHOD_SET)
    public void setVariableValue(@ParameterId(PARAM_VALUE) int value) {}
}
