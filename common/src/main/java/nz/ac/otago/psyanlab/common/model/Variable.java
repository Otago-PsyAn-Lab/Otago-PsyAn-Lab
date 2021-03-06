/*
 * Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>
 *
 * Otago PsyAn Lab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * In accordance with Section 7(b) of the GNU General Public License version 3,
 * all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;
import android.support.annotation.NonNull;

public abstract class Variable extends ExperimentObject implements Comparable<Variable> {
    protected static final int METHOD_GET = 0x03;

    protected static final int METHOD_SET = 0x02;

    protected static final int METHOD_SET_AND_USE = 0x01;

    protected static final int PARAM_VALUE = 0x01;

    public static class MethodNameFactory extends ExperimentObject.MethodNameFactory {

        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case METHOD_GET:
                    return context.getString(R.string.method_variable_get_value);

                case METHOD_SET:
                    return context.getString(R.string.method_variable_set_value);
                case METHOD_SET_AND_USE:
                    return context.getString(R.string.method_variable_set_and_use_value);
                default:
                    return super.getName(context, lookup);
            }
        }
    }

    public static class ParameterNameFactory extends ExperimentObject.ParameterNameFactory {

        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case PARAM_VALUE:
                    return context.getString(R.string.parameter_variable_value);
                default:
                    return super.getName(context, lookup);
            }
        }
    }

    @Expose
    public String name = "";

    public Variable() {}

    public Variable(Variable variable) {
        name = variable.name;
    }

    @Override
    public int compareTo(@NonNull Variable another) {
        if (name != null) {
            return name.compareToIgnoreCase(another.name);
        }
        return 0;
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return name;
    }

    @Override
    public int getKindResId() {
        return R.string.label_variable;
    }

    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public String getName() {
        return name;
    }

    @Override
    public NameResolverFactory getParameterNameFactory() {
        return new ParameterNameFactory();
    }

    public abstract int getType();

    abstract public String getValue();

    @Override
    public int kind() {
        return ExperimentObject.KIND_VARIABLE;
    }
}
