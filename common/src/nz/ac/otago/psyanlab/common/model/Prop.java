
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.InvocationTargetException;

public abstract class Prop implements Parcelable, ExperimentObject {
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

    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.format_prop_class_name, name);
    }

    @Override
    public int kind() {
        return ExperimentObjectReference.KIND_PROP;
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
            return R.string.event_missing_string;
        }
    }

    protected static class MethodNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            return R.string.method_missing_string;
        }
    }

    protected static class ParameterNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            return R.string.parameter_missing_string;
        }
    }
}
