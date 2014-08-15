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

package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

public abstract class Generator extends ExperimentObject {
    private static final int METHOD_GENERATE_NUMBER = 0x01;

    @Expose
    public int end;

    @Expose
    public String name;

    @Expose
    public int start;

    public Generator() {
        name = "New Generator";
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.format_generator_class_name, name);
    }

    @Override
    public int kind() {
        return ExperimentObject.KIND_GENERATOR;
    }

    @MethodId(METHOD_GENERATE_NUMBER)
    public int generateNumber() {
        return 0;
    }

    protected static class MethodNameFactory implements NameResolverFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case METHOD_GENERATE_NUMBER:
                    return context.getString(R.string.method_generator_generate_number);
                default:
                    return context.getString(R.string.method_missing_string);
            }
        }
    }
}
