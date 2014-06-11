
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

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.label_touch_event);
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
        public int getResId(int lookup) {
            switch (lookup) {
                case METHOD_GET_FINGER:
                    return R.string.method_get_finger;
                case METHOD_GET_X:
                    return R.string.method_get_x_position;
                case METHOD_GET_Y:
                    return R.string.method_get_y_position;
                case METHOD_GET_TIMESTAMP:
                    return R.string.method_get_timestamp;
                default:
                    return super.getResId(lookup);
            }
        }
    }

}
