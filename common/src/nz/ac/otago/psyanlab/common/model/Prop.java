
package nz.ac.otago.psyanlab.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.InvocationTargetException;

public abstract class Prop implements Parcelable {
    public static final String NAMESPACE = "nz.ac.otago.psyanlab.common.model.prop.";

    public static final Parcelable.Creator<Prop> CREATOR = new Parcelable.Creator<Prop>() {
        public Prop createFromParcel(Parcel in) {
            String propKind = in.readString();

            try {
                return (Prop)getClass().getClassLoader().loadClass(NAMESPACE + propKind)
                        .getConstructor(Parcelable.class).newInstance(in);

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

        public Prop[] newArray(int size) {
            return new Prop[size];
        }
    };

    public String name;

    public Prop() {
    }

    public Prop(Parcel in) {
        name = in.readString();
    }

    public Prop(Prop prop) {
        if (prop == null) {
            return;
        }

        name = prop.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getClass().getSimpleName());
        dest.writeString(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
