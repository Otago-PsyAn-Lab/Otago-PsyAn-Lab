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

package nz.ac.otago.psyanlab.single.model;

import nz.ac.otago.psyanlab.common.PaleRow;
import nz.ac.otago.psyanlab.common.model.Experiment;
import nz.ac.otago.psyanlab.single.DbModel;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExperimentModel extends DbModel {
    public static final String CREATE_TABLE = "create table experiment (_id integer primary key autoincrement, last_run number, name text not null, description text, version number, date_created number not null, file text unique not null, file_size number not null, authors text); ";

    public static final String KEY_AUTHORS = "authors";

    public static final String KEY_DATE_CREATED = "date_created";

    public static final String KEY_DESCRIPTION = "description";

    public static final String KEY_FILE = "file";

    public static final String KEY_FILE_SIZE = "file_size";

    public static final String KEY_LAST_RUN = "last_run";

    public static final String KEY_NAME = "name";

    public static final String KEY_VERSION = "version";

    public static final String TABLE = "experiment";

    public static void create(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void delete(SQLiteDatabase db, long id) {
        db.delete(TABLE, KEY_ID + "=" + id, null);
    }

    public static long insert(SQLiteDatabase db, ContentValues values) {
        if (values.size() == 0) {
            return INVALID_ID;
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

    public List<Long> additional = new ArrayList<Long>();

    public String authors;

    public long dateCreated;

    public String description;

    public String file;

    public long fileSize;

    public long id = INVALID_ID;

    public long lastRun;

    public String name;

    public int version;

    public ExperimentModel() {
    }

    public ExperimentModel(Bundle b) {
        if (b.containsKey(KEY_ID)) {
            id = b.getLong(KEY_ID);
        }
        if (b.containsKey(KEY_NAME)) {
            name = b.getString(KEY_NAME);
        }
        if (b.containsKey(KEY_AUTHORS)) {
            authors = b.getString(KEY_AUTHORS);
        }
        if (b.containsKey(KEY_DESCRIPTION)) {
            description = b.getString(KEY_DESCRIPTION);
        }
        if (b.containsKey(KEY_VERSION)) {
            version = b.getInt(KEY_VERSION);
        }
        if (b.containsKey(KEY_DATE_CREATED)) {
            dateCreated = b.getLong(KEY_DATE_CREATED);
        }
        if (b.containsKey(KEY_FILE)) {
            file = b.getString(KEY_FILE);
        }
        if (b.containsKey(KEY_FILE_SIZE)) {
            fileSize = b.getLong(KEY_FILE_SIZE);
        }
    }

    public ExperimentModel(Cursor c) {
        if (c.getColumnIndex(KEY_ID) != -1) {
            id = c.getLong(c.getColumnIndex(KEY_ID));
        } else if (c.getColumnIndex(namespaced(KEY_ID)) != -1) {
            id = c.getLong(c.getColumnIndex(namespaced(KEY_ID)));
        }
        if (c.getColumnIndex(KEY_NAME) != INVALID_COL) {
            name = c.getString(c.getColumnIndex(KEY_NAME));
        } else if (c.getColumnIndex(namespaced(KEY_NAME)) != INVALID_COL) {
            name = c.getString(c.getColumnIndex(namespaced(KEY_NAME)));
        }
        if (c.getColumnIndex(KEY_AUTHORS) != INVALID_COL) {
            authors = c.getString(c.getColumnIndex(KEY_AUTHORS));
        } else if (c.getColumnIndex(namespaced(KEY_AUTHORS)) != INVALID_COL) {
            authors = c.getString(c.getColumnIndex(namespaced(KEY_AUTHORS)));
        }
        if (c.getColumnIndex(KEY_DESCRIPTION) != INVALID_COL) {
            description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
        } else if (c.getColumnIndex(namespaced(KEY_DESCRIPTION)) != INVALID_COL) {
            description = c.getString(c.getColumnIndex(namespaced(KEY_DESCRIPTION)));
        }
        if (c.getColumnIndex(KEY_VERSION) != INVALID_COL) {
            version = c.getInt(c.getColumnIndex(KEY_VERSION));
        } else if (c.getColumnIndex(namespaced(KEY_VERSION)) != INVALID_COL) {
            version = c.getInt(c.getColumnIndex(namespaced(KEY_VERSION)));
        }
        if (c.getColumnIndex(KEY_DATE_CREATED) != INVALID_ID) {
            dateCreated = c.getLong(c.getColumnIndex(KEY_DATE_CREATED));
        } else if (c.getColumnIndex(namespaced(KEY_DATE_CREATED)) != INVALID_ID) {
            dateCreated = c.getLong(c.getColumnIndex(namespaced(KEY_DATE_CREATED)));
        }
        if (c.getColumnIndex(KEY_FILE) != INVALID_COL) {
            file = c.getString(c.getColumnIndex(KEY_FILE));
        } else if (c.getColumnIndex(namespaced(KEY_FILE)) != INVALID_COL) {
            file = c.getString(c.getColumnIndex(namespaced(KEY_FILE)));
        }
        if (c.getColumnIndex(KEY_FILE_SIZE) != INVALID_ID) {
            fileSize = c.getLong(c.getColumnIndex(KEY_FILE_SIZE));
        } else if (c.getColumnIndex(namespaced(KEY_FILE_SIZE)) != INVALID_ID) {
            fileSize = c.getLong(c.getColumnIndex(namespaced(KEY_FILE_SIZE)));
        }
    }

    public ExperimentModel(Cursor c, String prefix) {
        if (c.getColumnIndex(prefix + KEY_ID) != -1) {
            id = c.getLong(c.getColumnIndex(prefix + KEY_ID));
        }
        if (c.getColumnIndex(prefix + KEY_NAME) != INVALID_COL) {
            name = c.getString(c.getColumnIndex(prefix + KEY_NAME));
        }
        if (c.getColumnIndex(prefix + KEY_AUTHORS) != INVALID_COL) {
            authors = c.getString(c.getColumnIndex(prefix + KEY_AUTHORS));
        }
        if (c.getColumnIndex(prefix + KEY_DESCRIPTION) != INVALID_COL) {
            description = c.getString(c.getColumnIndex(prefix + KEY_DESCRIPTION));
        }
        if (c.getColumnIndex(prefix + KEY_VERSION) != INVALID_COL) {
            version = c.getInt(c.getColumnIndex(prefix + KEY_VERSION));
        }
        if (c.getColumnIndex(prefix + KEY_DATE_CREATED) != INVALID_ID) {
            dateCreated = c.getLong(c.getColumnIndex(prefix + KEY_DATE_CREATED));
        }
        if (c.getColumnIndex(prefix + KEY_FILE) != INVALID_COL) {
            file = c.getString(c.getColumnIndex(prefix + KEY_FILE));
        }
        if (c.getColumnIndex(prefix + KEY_FILE_SIZE) != INVALID_ID) {
            fileSize = c.getLong(c.getColumnIndex(prefix + KEY_FILE_SIZE));
        }
    }

    public ExperimentModel(Experiment experimentDef, File paleFile) {
        name = experimentDef.name;
        authors = experimentDef.authors;
        description = experimentDef.description;
        version = experimentDef.version;
        dateCreated = experimentDef.dateCreated;
        file = paleFile.getName();
        fileSize = paleFile.length();
    }

    public Bundle bundle() {
        Bundle b = new Bundle();
        b.putLong(KEY_ID, id);
        b.putString(KEY_NAME, name);
        b.putString(KEY_AUTHORS, authors);
        b.putString(KEY_DESCRIPTION, description);
        b.putInt(KEY_VERSION, version);
        b.putLong(KEY_DATE_CREATED, dateCreated);
        b.putString(KEY_FILE, file);
        b.putLong(KEY_FILE_SIZE, fileSize);
        return b;
    }

    /**
     * Compare an experiment model to another.
     * 
     * @param other The other experiment model in the comparison.
     * @return 0 if the same. -1 if this experiment model is lesser in order of
     *         name, date created, version, description, file size. +1 if more.
     */
    public int compareTo(ExperimentModel other) {
        int nameCompare = name.compareTo(other.name);
        if (nameCompare != 0) {
            return nameCompare;
        }

        if (dateCreated != other.dateCreated) {
            return (dateCreated < other.dateCreated) ? -1 : 1;
        }

        if (version != other.version) {
            return version > other.version ? 1 : -1;
        }

        int descriptionCompare = description.compareTo(other.description);
        if (descriptionCompare != 0) {
            return descriptionCompare;
        }

        if (fileSize != other.fileSize) {
            return (fileSize < other.fileSize) ? -1 : 1;
        }

        return 0;
    }

    /**
     * Fully prepares the experiment for execution.
     * 
     * @param context The application context.
     * @return The prepared experiment ready for execution.
     */
    // public Experiment getPreparedExperiment(Context context) throws
    // IOException {
    // File workingDir = FileUtils.decompress(
    // new File(context.getDir(FileUtils.PATH_INTERNAL_EXPERIMENTS_DIR,
    // Context.MODE_PRIVATE), filePath), context.getDir(FileUtils.PATH_TEMP,
    // Context.MODE_PRIVATE));
    // File experimentDefinition = new File(workingDir, "experiment.json");
    // Experiment experiment = ModelUtils.getDataReaderWriter().fromJson(
    // new JsonReader(new FileReader(experimentDefinition)), Experiment.class);
    // TODO: load data model into runtime model.
    // return experiment;
    // }

    public PaleRow toDetails(PaleRow row) {
        row.dateCreated = dateCreated;
        row.description = description;
        row.authors = authors;
        row.name = name;
        row.fileSize = fileSize;
        return row;
    }

    public ContentValues toValues() {
        ContentValues v = new ContentValues();
        v.put(KEY_AUTHORS, authors);
        v.put(KEY_DATE_CREATED, dateCreated);
        v.put(KEY_DESCRIPTION, description);
        v.put(KEY_FILE, file);
        v.put(KEY_FILE_SIZE, fileSize);
        v.put(KEY_NAME, name);
        v.put(KEY_VERSION, version);
        return v;
    }
}
