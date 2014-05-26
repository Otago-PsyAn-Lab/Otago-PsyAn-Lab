
package nz.ac.otago.psyanlab.single;

import nz.ac.otago.psyanlab.single.model.ExperimentModel;
import nz.ac.otago.psyanlab.single.model.RecordModel;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class DataProvider extends ContentProvider {
    public static final String AUTHORITY = "nz.ac.otago.psyanlab.single.data";

    public static final String EXPERIMENT_PATH = "experiments";

    public static final String RECORD_PATH = "records";

    public static final Uri URI_EXPERIMENTS = Uri.parse("content://" + AUTHORITY + "/"
            + EXPERIMENT_PATH);

    public static final Uri URI_RECORDS = Uri.parse("content://" + AUTHORITY + "/" + RECORD_PATH);

    private static final int EXPERIMENT_ID = 0x20;

    private static final int EXPERIMENTS = 0x02;

    private static final int RECORD_ID = 0x30;

    private static final int RECORDS = 0x03;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
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
            case EXPERIMENT_ID:
                if (TextUtils.isEmpty(selection)) {
                    selection = ExperimentModel.KEY_ID + "=" + uri.getLastPathSegment();
                } else {
                    selection = ExperimentModel.KEY_ID + "=" + uri.getLastPathSegment() + " and "
                            + selection;
                }
            case EXPERIMENTS:
                nr = deleteExperiments(selection, selectionArgs);
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
            case EXPERIMENTS:
                try {
                    id = insertExperiment(values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        Cursor cursor;
        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case EXPERIMENT_ID:
                queryBuilder.appendWhere(ExperimentModel.namespaced(ExperimentModel.KEY_ID) + "="
                        + uri.getLastPathSegment());
            case EXPERIMENTS:
                queryBuilder.setTables(ExperimentModel.TABLE);
                break;

            case RECORD_ID:
                queryBuilder.appendWhere(RecordModel.KEY_ID + "=" + uri.getLastPathSegment());
            case RECORDS:
                queryBuilder.setTables(RecordModel.TABLE);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        cursor = queryBuilder
                .query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        int nr;
        switch (uriType) {
            case EXPERIMENT_ID:
                if (TextUtils.isEmpty(selection)) {
                    selection = ExperimentModel.KEY_ID + "=" + uri.getLastPathSegment();
                } else {
                    selection = ExperimentModel.KEY_ID + "=" + uri.getLastPathSegment() + " and "
                            + selection;
                }
                nr = updateExperiments(values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unkown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return nr;
    }

    private int deleteExperiments(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        return db.delete(ExperimentModel.TABLE, selection, selectionArgs);
    }

    private int deleteRecords(String selection, String[] selectionArgs) {
        return 0;
    }

    private long insertExperiment(ContentValues values) throws IOException,
            NoSuchAlgorithmException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        values.put(ExperimentModel.KEY_LAST_RUN, -1);

        return db.insert(ExperimentModel.TABLE, null, values);
    }

    private long insertRecord(ContentValues values) {
        return 0;
    }

    private int updateExperiments(ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        return db.update(ExperimentModel.TABLE, values, selection, selectionArgs);
    }
}
