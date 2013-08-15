
package nz.ac.otago.psyanlab.multi.util;

import nz.ac.otago.psyanlab.multi.dbmodel.BaseExperimentModel;
import nz.ac.otago.psyanlab.multi.dbmodel.DbModel;
import nz.ac.otago.psyanlab.multi.dbmodel.ExperimentModel;
import nz.ac.otago.psyanlab.multi.dbmodel.RecordModel;
import nz.ac.otago.psyanlab.multi.dbmodel.UserModel;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class DataProvider extends ContentProvider {
    public static final String AUTHORITY = "nz.ac.otago.psyanlab.multi.userprovider";
    public static final String EXPERIMENT_PATH = "experiments";
    public static final String RECORD_PATH = "records";
    public static final String USER_PATH = "users";
    public static final String KEY_CONTACT_TEMP_URI = "contact_temp_uri";

    public static final Uri URI_EXPERIMENTS = Uri.parse("content://"
            + AUTHORITY + "/" + EXPERIMENT_PATH);
    public static final Uri URI_RECORDS = Uri.parse("content://" + AUTHORITY
            + "/" + RECORD_PATH);
    public static final Uri URI_USERS = Uri.parse("content://" + AUTHORITY
            + "/" + USER_PATH);

    private static final int EXPERIMENT_ID = 0x20;
    private static final int EXPERIMENTS = 0x02;

    private static final int RECORD_ID = 0x30;
    private static final int RECORDS = 0x03;

    private static final int USER_ID = 0x10;
    private static final int USERS = 0x01;

    /**
     * Count, ID, File.
     */
    private static final String[] sExperimentsToCheckProjection = new String[] {
            "COUNT(*) as count", ExperimentModel.KEY_EXP_ID,
            BaseExperimentModel.KEY_FILE
    };

    /**
     * ID, File.
     */
    private static final String[] sRecordsToRemoveProjection = new String[] {
            RecordModel.TABLE + "." + RecordModel.KEY_ID, RecordModel.KEY_FILE
    };

    private static final UriMatcher sUriMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, USER_PATH, USERS);
        sUriMatcher.addURI(AUTHORITY, USER_PATH + "/#", USER_ID);

        sUriMatcher.addURI(AUTHORITY, EXPERIMENT_PATH, EXPERIMENTS);
        sUriMatcher.addURI(AUTHORITY, EXPERIMENT_PATH + "/#", EXPERIMENT_ID);

        sUriMatcher.addURI(AUTHORITY, RECORD_PATH, RECORDS);
        sUriMatcher.addURI(AUTHORITY, RECORD_PATH + "/#", RECORD_ID);
    }

    private DatabaseHelper mDatabaseHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        int nr;
        switch (uriType) {
            case USER_ID:
                if (TextUtils.isEmpty(selection)) {
                    selection = UserModel.KEY_ID + "="
                            + uri.getLastPathSegment();
                } else {
                    selection = UserModel.KEY_ID + "="
                            + uri.getLastPathSegment() + " and " + selection;
                }
            case USERS:
                nr = deleteUsers(selection, selectionArgs);
                break;

            case EXPERIMENT_ID:
                if (TextUtils.isEmpty(selection)) {
                    selection = ExperimentModel.TABLE + "."
                            + ExperimentModel.KEY_ID + "="
                            + uri.getLastPathSegment();
                } else {
                    selection = ExperimentModel.TABLE + "."
                            + ExperimentModel.KEY_ID + "="
                            + uri.getLastPathSegment() + " and " + selection;
                }
            case EXPERIMENTS:
                nr = deleteExperiments(uri, selection, selectionArgs);
                break;

            case RECORD_ID:
                if (TextUtils.isEmpty(selection)) {
                    selection = RecordModel.KEY_ID + "="
                            + uri.getLastPathSegment();
                } else {
                    selection = RecordModel.KEY_ID + "="
                            + uri.getLastPathSegment() + " and " + selection;
                }
            case RECORDS:
                nr = deleteRecords(selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return nr;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sUriMatcher.match(uri);
        long id = DbModel.INVALID_ID;
        switch (uriType) {
            case USERS:
                id = insertUser(values);
                break;

            case EXPERIMENTS:
                try {
                    id = insertExperiment(values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case RECORDS:
                id = insertRecord(values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (id == DbModel.INVALID_ID) {
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(uri.getPath() + "/" + id);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        Cursor cursor;
        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case USER_ID:
                queryBuilder.appendWhere(UserModel.KEY_ID + "="
                        + uri.getLastPathSegment());
            case USERS:
                queryBuilder.setTables(UserModel.TABLE);
                break;

            case EXPERIMENT_ID:
                queryBuilder.appendWhere(ExperimentModel
                        .namespaced(ExperimentModel.KEY_ID)
                        + "="
                        + uri.getLastPathSegment());
            case EXPERIMENTS:
                queryBuilder.setTables(ExperimentModel.TABLE
                        + " LEFT JOIN "
                        + BaseExperimentModel.TABLE
                        + " ON "
                        + BaseExperimentModel
                                .namespaced(BaseExperimentModel.KEY_ID) + "="
                        + ExperimentModel.KEY_EXP_ID);
                break;

            case RECORD_ID:
                queryBuilder.appendWhere(RecordModel.KEY_ID + "="
                        + uri.getLastPathSegment());
            case RECORDS:
                queryBuilder.setTables(RecordModel.TABLE);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        cursor = queryBuilder.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor queryUsers(String[] projection, String selection,
            String[] selectionArgs, String sortOrder,
            SQLiteQueryBuilder queryBuilder) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        Cursor users = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // Todo link contacts info
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        int nr;
        switch (uriType) {
            case USER_ID:
                if (TextUtils.isEmpty(selection)) {
                    selection = ExperimentModel.KEY_ID + "="
                            + uri.getLastPathSegment();
                } else {
                    selection = ExperimentModel.KEY_ID + "="
                            + uri.getLastPathSegment() + " and " + selection;
                }
            case USERS:
                nr = updateUsers(values, selection, selectionArgs);
                break;

            case EXPERIMENT_ID:
                if (TextUtils.isEmpty(selection)) {
                    selection = ExperimentModel.KEY_ID + "="
                            + uri.getLastPathSegment();
                } else {
                    selection = ExperimentModel.KEY_ID + "="
                            + uri.getLastPathSegment() + " and " + selection;
                }
                nr = updateExperiments(values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unkown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return nr;
    }

    private void cleanUpExperiments(SQLiteDatabase db, Cursor c) {
        if (!c.moveToFirst()) {
            return;
        }

        // Check for potential experiments.
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        do {
            queryBuilder.appendWhere(ExperimentModel.KEY_EXP_ID + "="
                    + c.getLong(1));
        } while (c.moveToNext());
        queryBuilder.setTables(ExperimentModel.TABLE + " LEFT JOIN "
                + BaseExperimentModel.TABLE + " ON "
                + ExperimentModel.KEY_EXP_ID + "=" + BaseExperimentModel.TABLE
                + "." + BaseExperimentModel.KEY_ID);
        Cursor potentials = queryBuilder.query(db,
                sExperimentsToCheckProjection, null, null,
                ExperimentModel.KEY_EXP_ID, null, null);

        if (!potentials.moveToFirst()) {
            return;
        }

        do {
            if (potentials.getInt(0) == 1) {
                // Clean up experiment because it now has, or will soon have, no
                // references.
                db.delete(BaseExperimentModel.TABLE, BaseExperimentModel.KEY_ID
                        + "=" + potentials.getLong(1), null);
                File paleFile = new File(potentials.getString(2));
                paleFile.delete();
            }
        } while (potentials.moveToNext());
        potentials.close();
    }

    private void cleanUpRecords(SQLiteDatabase db, Cursor c) {
        if (!c.moveToFirst()) {
            return;
        }

        do {
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.appendWhere(RecordModel.KEY_USER_EXP_ID + "="
                    + c.getLong(0));
            queryBuilder.setTables(RecordModel.TABLE);
            Cursor files = queryBuilder.query(db, sRecordsToRemoveProjection,
                    null, null, null, null, null);

            if (!files.moveToFirst()) {
                continue;
            }

            do {
                new File(c.getString(1)).delete();
            } while (files.moveToNext());
            files.close();

            db.delete(RecordModel.TABLE,
                    RecordModel.KEY_USER_EXP_ID + "=" + c.getLong(0), null);
        } while (c.moveToNext());

    }

    private int deleteExperiments(Uri uri, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        Cursor experiments = query(uri, new String[] {
                ExperimentModel.namespaced(ExperimentModel.KEY_ID),
                ExperimentModel.namespaced(ExperimentModel.KEY_EXP_ID)
        }, selection, selectionArgs, null);
        experiments.moveToFirst();

        cleanUpRecords(db, experiments);
        cleanUpExperiments(db, experiments);

        int nr = db.delete(ExperimentModel.TABLE, selection, selectionArgs);
        if (nr == 0) {
            return 0;
        }

        return nr;
    }

    private int deleteRecords(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        String[] projection = new String[] {
            RecordModel.KEY_FILE
        };
        Cursor c = db.query(RecordModel.TABLE, projection, selection,
                selectionArgs, null, null, null);
        if (!c.moveToFirst()) {
            return 0;
        }

        do {
            new File(c.getString(0)).delete();
        } while (c.moveToNext());
        c.close();

        return db.delete(RecordModel.TABLE, selection, selectionArgs);
    }

    private int deleteUsers(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String[] projection = new String[] {
            UserModel.KEY_ID
        };
        Cursor e = db.query(UserModel.TABLE, projection, selection,
                selectionArgs, null, null, null);
        if (!e.moveToFirst()) {
            return 0;
        }
        ArrayList<String> experimentSelection = new ArrayList<String>();
        do {
            experimentSelection.add(ExperimentModel.KEY_USER_ID + "="
                    + e.getLong(0));
        } while (e.moveToNext());

        deleteExperiments(URI_EXPERIMENTS,
                TextUtils.join(" or ", experimentSelection), null);

        return db.delete(UserModel.TABLE, selection, selectionArgs);
    }

    /**
     * Adds pale to users list of experiments. Updates database and copies pale
     * file to internal storage if necessary.
     * 
     * @param values
     * @return Id of user experiment.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private long insertExperiment(ContentValues values) throws IOException,
            NoSuchAlgorithmException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String dest, source;
        long expId;

        // Setup filenames.
        if (!values.containsKey(Args.FILE_PATH)) {
            throw new IllegalStateException("Key file_path required.");
        }
        source = values.getAsString(Args.FILE_PATH);
        long userId = values.getAsLong(ExperimentModel.KEY_USER_ID);
        values.remove(Args.FILE_PATH);
        values.remove(ExperimentModel.KEY_USER_ID);
        dest = FileUtils.generateNewFileName(source);

        // Check PALE file not already in storage.
        Cursor c = db.query(BaseExperimentModel.TABLE, new String[] {
            BaseExperimentModel.KEY_ID
        }, BaseExperimentModel.KEY_FILE + "=?", new String[] {
            dest
        }, null, null, null);

        boolean createUserExperimentEntry = true;
        if (!c.moveToFirst() || c.getCount() == 0) {
            // Experiment is not in storage yet so add it.
            File paleFile = FileUtils.copyToInternalStorage(getContext(),
                    source, dest);

            ContentValues experimentValues = new ContentValues();
            experimentValues.put(BaseExperimentModel.KEY_FILE, dest);
            experimentValues.put(BaseExperimentModel.KEY_FILE_SIZE,
                    paleFile.length());
            experimentValues.put(BaseExperimentModel.KEY_AUTHORS,
                    values.getAsString(BaseExperimentModel.KEY_AUTHORS));
            experimentValues.put(BaseExperimentModel.KEY_DATE_CREATED,
                    values.getAsLong(BaseExperimentModel.KEY_DATE_CREATED));
            experimentValues.put(BaseExperimentModel.KEY_DESCRIPTION,
                    values.getAsString(BaseExperimentModel.KEY_DESCRIPTION));
            experimentValues.put(BaseExperimentModel.KEY_NAME,
                    values.getAsString(BaseExperimentModel.KEY_NAME));

            expId = db
                    .insert(BaseExperimentModel.TABLE, null, experimentValues);
        } else {
            // Get stored experiment id.
            expId = c.getLong(c.getColumnIndex(BaseExperimentModel.KEY_ID));
            c = db.query(ExperimentModel.TABLE, new String[] {
                ExperimentModel.KEY_ID
            }, ExperimentModel.KEY_EXP_ID + "=" + expId, null, null, null, null);

            if (c.moveToFirst() || c.getCount() > 0) {
                createUserExperimentEntry = false;
            }
        }

        if (createUserExperimentEntry) {
            // Insert experiment record for the user.
            ContentValues ueValues = new ContentValues();
            ueValues.put(ExperimentModel.KEY_EXP_ID, expId);
            ueValues.put(ExperimentModel.KEY_USER_ID, userId);
            ueValues.put(ExperimentModel.KEY_LAST_RUN, -1);

            return db.insert(ExperimentModel.TABLE, null, ueValues);
        }
        long id = c.getLong(0);
        c.close();
        return id;
    }

    private long insertRecord(ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        return db.insert(UserModel.TABLE, null, values);
    }

    private long insertUser(ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        return db.insert(UserModel.TABLE, null, values);
    }

    private int updateExperiments(ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        return db.update(ExperimentModel.TABLE, values, selection,
                selectionArgs);
    }

    private int updateUsers(ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        return db.update(UserModel.TABLE, values, selection, selectionArgs);
    }
}
