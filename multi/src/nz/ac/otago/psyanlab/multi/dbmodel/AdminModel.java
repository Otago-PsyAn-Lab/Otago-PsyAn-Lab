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

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class AdminModel extends DbModel {
    public static final String CREATE_TABLE = "create table admin "
            + "(_id integer primary key autoincrement,"
            + "password_hash text not null," + "upsalt text not null" + "); ";

    public static final String KEY_PASSWORDHASH = "password_hash";

    /**
     * Salt to ensuring the password is kept distinct (Unique Password Salt).
     */
    public static final String KEY_UPSALT = "upsalt";

    public static final String TABLE = "admin";

    public static void create(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        createDefaultAdmin(db);
    }

    public static void createDefaultAdmin(SQLiteDatabase db) {
        String salt = PWUtils.generateSalt();
        String hash = PWUtils.generateHash("admin", salt);
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORDHASH, hash);
        values.put(KEY_UPSALT, salt);
        db.insert(TABLE, null, values);
    }

    public static String namespaced(String key) {
        return TABLE + "." + key;
    }

    public static void setPassword(SQLiteDatabase db, String password) {
        String salt = PWUtils.generateSalt();
        String hash = PWUtils.generateHash(password, salt);
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORDHASH, hash);
        values.put(KEY_UPSALT, salt);
        db.update(TABLE, values, KEY_ID + "=1", null);
    }
}
