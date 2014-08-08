package nz.ac.otago.psyanlab.common.model;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.EventData;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

public class TouchMotionEvent extends TouchEvent {
    private static final int METHOD_GET_MOTION_TYPE = 0x10;

    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.label_touch_event);
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
}
