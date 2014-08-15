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

import java.util.ArrayList;

public class Loop extends ExperimentObject {
    public static final long STEPPING_KIND_RANDOM = 0x01;

    public static final long STEPPING_KIND_SEQUENTIAL = 0x02;

    public static final long STEPPING_KIND_SHUFFLE = 0x03;

    protected static final int METHOD_FINISH_LOOP = 0x101;

    protected static final int METHOD_GET_SELECTED_ROW = 0x104;

    protected static final int METHOD_GET_STEP = 0x102;

    protected static final int METHOD_GET_TOTAL_ITERATIONS = 0x103;

    protected static class MethodNameFactory implements NameResolverFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case METHOD_FINISH_LOOP:
                    return context.getString(R.string.method_finish_loop);
                case METHOD_GET_SELECTED_ROW:
                    return context.getString(R.string.method_get_selected_row);
                case METHOD_GET_STEP:
                    return context.getString(R.string.method_get_step);
                case METHOD_GET_TOTAL_ITERATIONS:
                    return context.getString(R.string.method_get_total_iterations);

                default:
                    return context.getString(R.string.method_missing_string);
            }
        }
    }

    @Expose
    public ArrayList<Long> generators;

    @Expose
    public int iterations = 1;

    @Expose
    public long linkedSource = -1;

    @Expose
    public String name;

    @Expose
    public ArrayList<Long> scenes;

    @Expose
    public long steppingKind;

    public Loop() {
        scenes = new ArrayList<Long>();
        generators = new ArrayList<Long>();
    }

    public boolean contains(long sceneId) {
        for (Long id : scenes) {
            if (id == sceneId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.format_loop_class_name, name);
    }

    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Override
    public int kind() {
        return ExperimentObject.KIND_LOOP;
    }

    @MethodId(METHOD_FINISH_LOOP)
    public void stubFinishLoop() {
    }

    @MethodId(METHOD_GET_SELECTED_ROW)
    public int stubGetSelectedRow() {
        return 0;
    }

    @MethodId(METHOD_GET_STEP)
    public int stubGetStep() {
        return 0;
    }

    @MethodId(METHOD_GET_TOTAL_ITERATIONS)
    public int stubGetTotalIterations() {
        return 0;
    }
}
