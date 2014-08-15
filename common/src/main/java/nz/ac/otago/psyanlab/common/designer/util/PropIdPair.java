
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
