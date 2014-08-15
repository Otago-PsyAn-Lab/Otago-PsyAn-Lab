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

public class TouchEvent extends ExperimentObject {
    private static final int METHOD_GET_FINGER = 0x01;

    private static final int METHOD_GET_TIMESTAMP = 0x04;

    private static final int METHOD_GET_X = 0x02;

    private static final int METHOD_GET_Y = 0x03;

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
        return EventData.EVENT_TOUCH;
    }

    @Override
    public int kind() {
        return ExperimentObject.KIND_EVENT;
    }

    @MethodId(METHOD_GET_FINGER)
    public int stubGetFinger() {
        return 0;
    }

    @MethodId(METHOD_GET_TIMESTAMP)
    public int stubGetTimeStamp() {
        return 0;
    }

    @MethodId(METHOD_GET_X)
    public int stubGetX() {
        return 0;
    }

    @MethodId(METHOD_GET_Y)
    public int stubGetY() {
        return 0;
    }

    protected static class MethodNameFactory extends ExperimentObject.MethodNameFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case METHOD_GET_FINGER:
                    return context.getString(R.string.method_get_finger);
                case METHOD_GET_X:
                    return context.getString(R.string.method_get_x_position);
                case METHOD_GET_Y:
                    return context.getString(R.string.method_get_y_position);
                case METHOD_GET_TIMESTAMP:
                    return context.getString(R.string.method_get_timestamp);
                default:
                    return super.getName(context, lookup);
            }
        }
    }
}
