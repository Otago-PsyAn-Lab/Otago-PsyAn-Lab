
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.util.Args;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class PropIdPair implements Parcelable {
    public static final Parcelable.Creator<PropIdPair> CREATOR = new Parcelable.Creator<PropIdPair>() {
        @Override
        public PropIdPair createFromParcel(Parcel in) {
            return new PropIdPair(in);
        }

        @Override
        public PropIdPair[] newArray(int size) {
            return new PropIdPair[size];
        }
    };

    private long mId;

    private Prop mProp;

    public PropIdPair(long id, Prop prop) {
        mId = id;
        mProp = prop;
    }

    public PropIdPair(Parcel in) {
        mId = in.readLong();
        mProp = in.readBundle(Prop.class.getClassLoader()).getParcelable(Args.EXPERIMENT_PROP);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Long getId() {
        return mId;
    }

    public Prop getProp() {
        return mProp;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle data = new Bundle();
        data.putParcelable(Args.EXPERIMENT_PROP, mProp);
        dest.writeLong(mId);
        dest.writeBundle(data);
    }

    @Override
    public String toString() {
        return mProp.toString();
    }
}
