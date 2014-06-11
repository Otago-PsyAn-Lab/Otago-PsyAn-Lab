
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.EventData;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.ParameterId;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.InvocationTargetException;

public abstract class Prop extends ExperimentObject implements Parcelable {
    public static final Parcelable.Creator<Prop> CREATOR = new Parcelable.Creator<Prop>() {
        @Override
        public Prop createFromParcel(Parcel in) {
            String propKind = in.readString();

            try {
                return (Prop)getClass().getClassLoader().loadClass(NAMESPACE + propKind)
                        .getConstructor(Parcel.class).newInstance(in);

            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unknown prop type: " + propKind, e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Error recreating prop: " + propKind, e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Error recreating prop: " + propKind, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error recreating prop: " + propKind, e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Error recreating prop: " + propKind, e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Error recreating prop: " + propKind, e);
            }
        }

        @Override
        public Prop[] newArray(int size) {
            return new Prop[size];
        }
    };

    public static final String NAMESPACE = "nz.ac.otago.psyanlab.common.model.prop.";

    protected static final int EVENT_CLICKED = 0x01;

    protected static final int EVENT_FINGER_DOWN = 0x03;

    protected static final int EVENT_FINGER_DRAG_RELEASED = 0x05;

    protected static final int EVENT_FINGER_DRAG_STARTED = 0x06;

    protected static final int EVENT_FINGER_MOTION = 0x07;

    protected static final int EVENT_FINGER_UP = 0x04;

    protected static final int EVENT_LONG_CLICKED = 0x02;

    protected static final int METHOD_GET_HEIGHT = 0x04;

    protected static final int METHOD_GET_OPACITY = 0x0a;

    protected static final int METHOD_GET_WIDTH = 0x03;

    protected static final int METHOD_GET_X_POSITION = 0x05;

    protected static final int METHOD_GET_Y_POSITION = 0x06;

    protected static final int METHOD_HIDE = 0x01;

    protected static final int METHOD_SET_HEIGHT = 0x08;

    protected static final int METHOD_SET_OPACITY = 0x0b;

    protected static final int METHOD_SET_POSITION = 0x09;

    protected static final int METHOD_SET_WIDTH = 0x07;

    protected static final int METHOD_SHOW = 0x02;

    protected static final int PARAM_HEIGHT = 0x04;

    protected static final int PARAM_OPACITY = 0x03;

    protected static final int PARAM_WIDTH = 0x05;

    protected static final int PARAM_X_COORDINATE = 0x02;

    protected static final int PARAM_Y_COORDINATE = 0x01;

    @Expose
    public int height = 0;

    @Expose
    public String name;

    @Expose
    public int width = 0;

    @Expose
    public int xPos = 0;

    @Expose
    public int yPos = 0;

    public Prop() {
    }

    public Prop(Context context, Prop prop, int defaultSuffix) {
        name = context.getString(R.string.format_default_prop_name,
                context.getString(R.string.default_prop_name), defaultSuffix);

        if (prop == null) {
            return;
        }

        name = prop.name;
        xPos = prop.xPos;
        yPos = prop.yPos;
        width = prop.width;
        height = prop.height;
    }

    public Prop(Parcel in) {
        name = in.readString();
        xPos = in.readInt();
        yPos = in.readInt();
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @EventData(id = EVENT_CLICKED, type = EventData.EVENT_TOUCH)
    public void eventClicked() {
    }

    @EventData(id = EVENT_FINGER_DOWN, type = EventData.EVENT_TOUCH)
    public void eventFingerDown() {
    }

    @EventData(id = EVENT_FINGER_DRAG_RELEASED, type = EventData.EVENT_TOUCH)
    public void eventFingerDragReleased() {
    }

    @EventData(id = EVENT_FINGER_DRAG_STARTED, type = EventData.EVENT_TOUCH)
    public void eventFingerDragStarted() {
    }

    @EventData(id = EVENT_FINGER_MOTION, type = EventData.EVENT_TOUCH_MOTION)
    public void eventFingerMotion() {
    }

    @EventData(id = EVENT_FINGER_UP, type = EventData.EVENT_TOUCH)
    public void eventFingerUp() {
    }

    @EventData(id = EVENT_LONG_CLICKED, type = EventData.EVENT_TOUCH)
    public void eventLongClicked() {
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.format_prop_class_name, name);
    }

    @Override
    public int kind() {
        return ExperimentObject.KIND_PROP;
    }

    @MethodId(METHOD_GET_HEIGHT)
    public int stubGetHeight() {
        return 0;
    }

    @MethodId(METHOD_GET_OPACITY)
    public int stubGetOpacity() {
        return 0;
    }

    @MethodId(METHOD_GET_WIDTH)
    public int stubGetWidth() {
        return 0;
    }

    @MethodId(METHOD_GET_X_POSITION)
    public int stubGetXPosition() {
        return 0;
    }

    @MethodId(METHOD_HIDE)
    public void stubHide() {
    }

    @MethodId(METHOD_SET_HEIGHT)
    public void stubSetHeight(@ParameterId(PARAM_HEIGHT) int height) {
    }

    @MethodId(METHOD_SET_OPACITY)
    public void stubSetOpacity(@ParameterId(PARAM_OPACITY) int opacity) {
    }

    @MethodId(METHOD_SET_POSITION)
    public void stubSetPosition(@ParameterId(PARAM_X_COORDINATE) int x,
            @ParameterId(PARAM_Y_COORDINATE) int y) {
    }

    @MethodId(METHOD_SET_WIDTH)
    public void stubSetWidth(@ParameterId(PARAM_WIDTH) int width) {
    }

    @MethodId(METHOD_SHOW)
    public void stubShow() {
    }

    @MethodId(METHOD_GET_Y_POSITION)
    public int stubShowYPosition() {
        return 0;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getClass().getSimpleName());
        dest.writeString(name);
        dest.writeInt(xPos);
        dest.writeInt(yPos);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    protected static class EventNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case EVENT_CLICKED:
                    return R.string.event_clicked;
                case EVENT_FINGER_DOWN:
                    return R.string.event_finger_down;
                case EVENT_FINGER_DRAG_RELEASED:
                    return R.string.event_finger_drag_released;
                case EVENT_FINGER_DRAG_STARTED:
                    return R.string.event_finger_drag_started;
                case EVENT_FINGER_MOTION:
                    return R.string.event_finger_motion;
                case EVENT_FINGER_UP:
                    return R.string.event_finger_up;
                case EVENT_LONG_CLICKED:
                    return R.string.event_long_clicked;

                default:
                    return R.string.event_missing_string;
            }
        }
    }

    protected static class MethodNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case METHOD_GET_HEIGHT:
                    return R.string.method_get_height;
                case METHOD_GET_OPACITY:
                    return R.string.method_get_opacity;
                case METHOD_GET_WIDTH:
                    return R.string.method_get_width;
                case METHOD_GET_X_POSITION:
                    return R.string.method_get_x_position;
                case METHOD_GET_Y_POSITION:
                    return R.string.method_get_y_position;
                case METHOD_HIDE:
                    return R.string.method_hide;
                case METHOD_SET_HEIGHT:
                    return R.string.method_set_height;
                case METHOD_SET_OPACITY:
                    return R.string.method_set_opacity;
                case METHOD_SET_POSITION:
                    return R.string.method_set_position;
                case METHOD_SET_WIDTH:
                    return R.string.method_set_width;
                case METHOD_SHOW:
                    return R.string.method_show;

                default:
                    return R.string.method_missing_string;
            }
        }
    }

    protected static class ParameterNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case PARAM_HEIGHT:
                    return R.string.parameter_height;
                case PARAM_OPACITY:
                    return R.string.parameter_opacity;
                case PARAM_WIDTH:
                    return R.string.parameter_width;
                case PARAM_X_COORDINATE:
                    return R.string.parameter_x;
                case PARAM_Y_COORDINATE:
                    return R.string.parameter_y;

                default:
                    return R.string.parameter_missing_string;
            }
        }
    }
}
