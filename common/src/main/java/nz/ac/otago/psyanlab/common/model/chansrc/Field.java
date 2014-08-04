package nz.ac.otago.psyanlab.common.model.chansrc;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.util.Type;

public class Field implements Parcelable {
    public static final Parcelable.Creator<Field> CREATOR = new Parcelable.Creator<Field>() {
        public Field createFromParcel(Parcel in) {
            return new Field(in);
        }

        public Field[] newArray(int size) {
            return new Field[size];
        }
    };

    /**
     * Id used to track field changes when updating data channel references.
     */
    @Expose
    public int id;

    /**
     * Field name.
     */
    @Expose
    public String name;

    /**
     * The type of the field recorded as a bit mask.
     */
    @Expose
    public int type;

    public Field() {
    }

    public Field(Parcel in) {
        id = in.readInt();
        type = in.readInt();
        name = in.readString();
    }

    @Override
    public String toString() {
        return "Field : " + id + " :: " + name + " :: " + Type.getTypeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(id);
        out.writeInt(type);
        out.writeString(name);
    }
}
