/*
 Copyright (C) 2012, 2013 University of Otago, Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.multi.dbmodel;

import nz.ac.otago.psyanlab.multi.util.PWUtils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

public class UserModel extends DbModel implements Parcelable {
    public static final String CREATE_TABLE = "create table user (_id integer primary key autoincrement, "
            + "name text not null, "
            + "lookup number not null, "
            + "password_hash text, "
            + "upsalt text, "
            + "date_created number, "
            + "last_log_in number, "
            + "total_experiments number, "
            + "total_experiments_size number, "
            + "total_records number, " + "total_records_size number); ";

    public static final String KEY_DATE_CREATED = "date_created";
    public static final String KEY_LAST_LOG_IN = "last_log_in";
    public static final String KEY_LOOKUP_KEY = "lookup";
    public static final String KEY_NAME = "name";
    public static final String KEY_PWHASH = "password_hash";
    public static final String KEY_SALT = "upsalt";
    public static final String KEY_TOTAL_EXPERIMENTS = "total_experiments";
    public static final String KEY_TOTAL_EXPERIMENTS_SIZE = "total_experiments_size";
    public static final String KEY_TOTAL_RECORDS = "total_records";
    public static final String KEY_TOTAL_RECORDS_SIZE = "total_records_size";

    public static final String TABLE = "user";

    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public static final void create(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static final String namespaced(String key) {
        return TABLE + "." + key;
    }

    public long id = -1;
    public long dateCreated = -1;
    public long lastLogIn = -1;
    public String lookupKey = "";
    public String name = "";
    public String pwhash = "";
    public String salt = "";
    public int totalExperiments = 0;
    public long totalExperimentsSize = 0;
    public int totalRecords = 0;
    public long totalRecordsSize = 0;
    // Derived fields.
    public String email = "";
    public String phone = "";

    public UserModel() {
    }

    public UserModel(Parcel in) {
        id = in.readLong();
        dateCreated = in.readLong();
        lastLogIn = in.readLong();

        lookupKey = in.readString();
        name = in.readString();
        pwhash = in.readString();
        salt = in.readString();

        totalExperiments = in.readInt();
        totalExperimentsSize = in.readLong();
        totalRecords = in.readInt();
        totalRecordsSize = in.readLong();

        email = in.readString();
        phone = in.readString();
    }

    public UserModel(Cursor c) {
        id = c.getLong(c.getColumnIndex(UserModel.KEY_ID));
        lastLogIn = c.getLong(c.getColumnIndex(UserModel.KEY_LAST_LOG_IN));
        dateCreated = c.getLong(c.getColumnIndex(UserModel.KEY_DATE_CREATED));

        lookupKey = c.getString(c.getColumnIndex(UserModel.KEY_LOOKUP_KEY));
        name = c.getString(c.getColumnIndex(UserModel.KEY_NAME));
        pwhash = c.getString(c.getColumnIndex(UserModel.KEY_PWHASH));
        salt = c.getString(c.getColumnIndex(UserModel.KEY_SALT));

        totalExperiments = c.getInt(c
                .getColumnIndex(UserModel.KEY_TOTAL_EXPERIMENTS));
        totalExperimentsSize = c.getLong(c
                .getColumnIndex(UserModel.KEY_TOTAL_EXPERIMENTS_SIZE));
        totalRecords = c.getInt(c.getColumnIndex(UserModel.KEY_TOTAL_RECORDS));
        totalRecordsSize = c.getLong(c
                .getColumnIndex(UserModel.KEY_TOTAL_RECORDS_SIZE));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setPassword(String password) {
        salt = PWUtils.generateSalt();
        pwhash = PWUtils.generateHash(password, salt);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(lastLogIn);
        dest.writeLong(dateCreated);

        dest.writeString(lookupKey);
        dest.writeString(name);
        dest.writeString(pwhash);
        dest.writeString(salt);

        dest.writeInt(totalExperiments);
        dest.writeLong(totalExperimentsSize);
        dest.writeInt(totalRecords);
        dest.writeLong(totalRecordsSize);

        dest.writeString(email);
        dest.writeString(phone);
    }
}
