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

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.EventData;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

public class TouchMotionEvent extends TouchEvent {
    private static final int METHOD_GET_MOTION_TYPE = 0x10;

    protected static class MethodNameFactory extends TouchEvent.MethodNameFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case METHOD_GET_MOTION_TYPE:
                    return context.getString(R.string.method_get_motion_type);
                default:
                    return super.getName(context, lookup);
            }
        }
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.label_touch_event);
    }

    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Override
    public int getTag() {
        return EventData.EVENT_TOUCH_MOTION;
    }

    @Override
    public int kind() {
        return ExperimentObject.KIND_EVENT;
    }

    @MethodId(METHOD_GET_MOTION_TYPE)
    public String stubGetMotionType() {
        return null;
    }
}
