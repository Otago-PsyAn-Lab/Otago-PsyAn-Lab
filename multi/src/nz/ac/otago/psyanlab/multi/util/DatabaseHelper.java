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

package nz.ac.otago.psyanlab.multi.util;

import nz.ac.otago.psyanlab.multi.dbmodel.AdminModel;
import nz.ac.otago.psyanlab.multi.dbmodel.BaseExperimentModel;
import nz.ac.otago.psyanlab.multi.dbmodel.ExperimentModel;
import nz.ac.otago.psyanlab.multi.dbmodel.RecordModel;
import nz.ac.otago.psyanlab.multi.dbmodel.UserModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper to handle opening and upgrading the database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "runner";

    public DatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        deleteAll(db);
        createAll(db);

        onUpgrade(db, 1, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // FIXME: Remove clutch upgrade method and properly handle database
        // upgrades.
        deleteAll(db);
        createAll(db);
    }

    /**
     * Create all tables.
     * 
     * @param db Database to create tables in.
     */
    private void createAll(SQLiteDatabase db) {
        UserModel.create(db);
        AdminModel.create(db);
        BaseExperimentModel.create(db);
        RecordModel.create(db);
        ExperimentModel.create(db);
    }

    /**
     * Delete all tables.
     * 
     * @param db Database to delete tables from.
     */
    private void deleteAll(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + UserModel.TABLE);
        db.execSQL("drop table if exists " + AdminModel.TABLE);
        db.execSQL("drop table if exists " + BaseExperimentModel.TABLE);
        db.execSQL("drop table if exists " + RecordModel.TABLE);
        db.execSQL("drop table if exists " + ExperimentModel.TABLE);
    }
}
