/*
 Copyright (C) 2014 Tonic Artos <tonic.artos@gmail.com>

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

import android.content.Context;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

public abstract class Timer extends ExperimentObject {
    private static final int METHOD_START = 0x01;

    private static final int METHOD_STOP = 0x02;

    protected static class MethodNameFactory implements NameResolverFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case METHOD_START:
                    return context.getString(R.string.method_timer_start);
                case METHOD_STOP:
                    return context.getString(R.string.method_timer_stop);
                default:
                    return context.getString(R.string.method_missing_string);
            }
        }
    }

    @Expose
    public String name;

    @Expose
    public long waitValue;

    @Override
    public String getExperimentObjectName(Context context) {
        return name;
    }

    @Override
    public int getKindResId() {
        return R.string.label_timer;
    }

    @Override
    public int kind() {
        return ExperimentObject.KIND_TIMER;
    }

    @MethodId(METHOD_START)
    public void start() {}

    @MethodId(METHOD_STOP)
    public void stop() {}
}

