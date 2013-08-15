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

import nz.ac.otago.psyanlab.common.PaleRow;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class ExperimentModel extends DbModel {
    public static final String CREATE_TABLE = "create table experiment (_id integer primary key autoincrement, user_id number not null, experiment_id number unique not null, last_run number not null); ";
    
    public static final String KEY_EXP_ID = "experiment_id";
    public static final String KEY_LAST_RUN = "last_run";
    public static final String KEY_USER_ID = "user_id";
    
    public static final String TABLE = "experiment";

    public static void create(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);        
    }

    public static int delete(SQLiteDatabase db, long id) {
        return db.delete(TABLE, KEY_ID + "=" + id, null);
    }

    public static void deleteUserData(SQLiteDatabase db, long id) {
        db.delete(TABLE, KEY_USER_ID + "=" + id, null);
        RecordModel.deleteExperimentRecordsForUser(db, id);
    }

    public static long getNumExperimentRefs(SQLiteDatabase db, long experimentId) {
        Cursor c = db.query(ExperimentModel.TABLE, new String[] {
            "Count(*)"
        }, ExperimentModel.KEY_EXP_ID + "=" + experimentId, null, null, null,
                null);

        if (c.moveToFirst()) {
            return c.getLong(0);
        }
        return 0;
    }

    public static long insert(SQLiteDatabase db, ContentValues values) {
        if (values.size() == 0) {
            return DbModel.INVALID_ID;
        }
        return db.insert(TABLE, null, values);
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
    public long experimentId;
    public long id = INVALID_ID;

    /**
     * The date the experiment was last run on.
     * <p>
     * If lastRun is less then or equal to the experiment's creation date then
     * the experiment must not have been run by the user at all.
     * </p>
     */
    public long lastRun;

    public long userId;

    public ExperimentModel(Bundle b) {
        if (b.containsKey(KEY_ID)) {
            id = b.getLong(KEY_ID);
        }
        if (b.containsKey(KEY_USER_ID)) {
            userId = b.getLong(KEY_USER_ID);
        }
        if (b.containsKey(KEY_EXP_ID)) {
            experimentId = b.getLong(KEY_EXP_ID);
        }
        if (b.containsKey(KEY_LAST_RUN)) {
            lastRun = b.getLong(KEY_LAST_RUN);
        }
    }

    public ExperimentModel(Cursor c) {
        if (c.getColumnIndex(KEY_ID) != -1) {
            id = c.getLong(c.getColumnIndex(KEY_ID));
        }
        if (c.getColumnIndex(TABLE + KEY_ID) != -1) {
            id = c.getLong(c.getColumnIndex(TABLE + KEY_ID));
        }
        if (c.getColumnIndex(KEY_USER_ID) != INVALID_COL) {
            userId = c.getLong(c.getColumnIndex(KEY_USER_ID));
        }
        if (c.getColumnIndex(KEY_EXP_ID) != INVALID_ID) {
            experimentId = c.getLong(c.getColumnIndex(KEY_EXP_ID));
        }
        if (c.getColumnIndex(KEY_LAST_RUN) != INVALID_ID) {
            lastRun = c.getLong(c.getColumnIndex(KEY_LAST_RUN));
        }
    }

    public ExperimentModel(Cursor c, String prefix) {
        if (c.getColumnIndex(prefix + KEY_ID) != -1) {
            id = c.getLong(c.getColumnIndex(prefix + KEY_ID));
        }
        if (c.getColumnIndex(prefix + KEY_USER_ID) != INVALID_COL) {
            userId = c.getLong(c.getColumnIndex(prefix + KEY_USER_ID));
        }
        if (c.getColumnIndex(prefix + KEY_EXP_ID) != INVALID_ID) {
            experimentId = c.getLong(c.getColumnIndex(prefix + KEY_EXP_ID));
        }
        if (c.getColumnIndex(prefix + KEY_LAST_RUN) != INVALID_ID) {
            lastRun = c.getLong(c.getColumnIndex(prefix + KEY_LAST_RUN));
        }
    }

    public Bundle bundle() {
        Bundle b = new Bundle();
        b.putLong(KEY_ID, id);
        b.putLong(KEY_USER_ID, userId);
        b.putLong(KEY_EXP_ID, experimentId);
        b.putLong(KEY_LAST_RUN, lastRun);
        return b;
    }

    public PaleRow toDetails(PaleRow row) {
        row.lastRun = lastRun;
        row.id = id;
        return row;
    }
}
