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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class RecordModel extends DbModel {
    public static final String CREATE_TABLE = "create table record (_id integer primary key autoincrement, user_exp_id number not null, file text not null, file_size number not null, date number not null, note text, session_id number not null); ";

    public static final String KEY_DATE = "date";
    public static final String KEY_FILE = "file";
    public static final String KEY_FILE_SIZE = "file_size";
    public static final String KEY_NOTE = "note";
    public static final String KEY_SESSION_ID = "session_id";
    public static final String KEY_USER_EXP_ID = "user_exp_id";
    
    public static final String TABLE = "record";

    public static void create(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void deleteExperimentRecords(SQLiteDatabase db, long id) {
        db.delete(TABLE, KEY_USER_EXP_ID + "=" + id, null);
    }

    public static void deleteExperimentRecordsForUser(SQLiteDatabase db, long id) {
    }

    public static void insert(SQLiteDatabase db, ContentValues values) {
        if (values.size() == 0) {
            return;
        }
        db.insert(TABLE, null, values);
    }

    public static String namespaced(String key) {
        return TABLE + "." + key;
    }

    public static void update(SQLiteDatabase db, long id, ContentValues values) {
        if (values.size() == 0) {
            return;
        }
        db.update(TABLE, values, KEY_ID + "=" + id, null);
    }
    public long date;
    public String file;
    public long fileSize;
    public long id = INVALID_ID;
    public String note;

    public long sessionId;

    public long userExperimentId;

    public RecordModel(Bundle b) {
        if (b.containsKey(KEY_ID)) {
            id = b.getLong(KEY_ID);
        }
        if (b.containsKey(KEY_USER_EXP_ID)) {
            userExperimentId = b.getLong(KEY_USER_EXP_ID);
        }
        if (b.containsKey(KEY_FILE)) {
            file = b.getString(KEY_FILE);
        }
        if (b.containsKey(KEY_FILE_SIZE)) {
            fileSize = b.getLong(KEY_FILE_SIZE);
        }
        if (b.containsKey(KEY_DATE)) {
            date = b.getLong(KEY_DATE);
        }
        if (b.containsKey(KEY_NOTE)) {
            note = b.getString(KEY_NOTE);
        }
        if (b.containsKey(KEY_SESSION_ID)) {
            sessionId = b.getLong(KEY_SESSION_ID);
        }

    }

    public RecordModel(Cursor c) {
        if (c.getColumnIndex(KEY_ID) != -1) {
            id = c.getLong(c.getColumnIndex(KEY_ID));
        }
        if (c.getColumnIndex(KEY_USER_EXP_ID) != INVALID_ID) {
            userExperimentId = c.getLong(c.getColumnIndex(KEY_USER_EXP_ID));
        }
        if (c.getColumnIndex(KEY_FILE) != INVALID_ID) {
            file = c.getString(c.getColumnIndex(KEY_FILE));
        }
        if (c.getColumnIndex(KEY_FILE_SIZE) != INVALID_ID) {
            fileSize = c.getLong(c.getColumnIndex(KEY_FILE_SIZE));
        }
        if (c.getColumnIndex(KEY_DATE) != INVALID_ID) {
            date = c.getLong(c.getColumnIndex(KEY_DATE));
        }
        if (c.getColumnIndex(KEY_NOTE) != INVALID_ID) {
            note = c.getString(c.getColumnIndex(KEY_NOTE));
        }
        if (c.getColumnIndex(KEY_SESSION_ID) != INVALID_ID) {
            sessionId = c.getLong(c.getColumnIndex(KEY_SESSION_ID));
        }
    }

    public Bundle bundle() {
        Bundle b = new Bundle();
        b.putLong(KEY_ID, id);
        b.putLong(KEY_USER_EXP_ID, userExperimentId);
        b.putString(KEY_FILE, file);
        b.putLong(KEY_FILE_SIZE, fileSize);
        b.putLong(KEY_DATE, date);
        b.putString(KEY_NOTE, note);
        b.putLong(KEY_SESSION_ID, sessionId);
        return b;
    }
}
