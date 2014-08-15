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
