
package nz.ac.otago.psyanlab.single;

import com.google.gson.JsonSyntaxException;

import nz.ac.otago.psyanlab.common.PaleRow;
import nz.ac.otago.psyanlab.common.UserExperimentDelegateI;
import nz.ac.otago.psyanlab.common.model.Experiment;
import nz.ac.otago.psyanlab.single.model.ExperimentModel;
import nz.ac.otago.psyanlab.single.model.RecordModel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.ListAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ExperimentDelegate implements UserExperimentDelegateI {
    public static final Parcelable.Creator<ExperimentDelegate> CREATOR = new Parcelable.Creator<ExperimentDelegate>() {
        @Override
        public ExperimentDelegate createFromParcel(Parcel in) {
            return new ExperimentDelegate(in);
        }

        @Override
        public ExperimentDelegate[] newArray(int size) {
            return new ExperimentDelegate[size];
        }
    };

    private static final int LOADER_RECORDS = 0x02;

    private static final String[] sProjection = new String[] {
            ExperimentModel.KEY_ID, ExperimentModel.KEY_LAST_RUN, ExperimentModel.KEY_AUTHORS,
            ExperimentModel.KEY_DATE_CREATED, ExperimentModel.KEY_DESCRIPTION,
            ExperimentModel.KEY_FILE, ExperimentModel.KEY_FILE_SIZE, ExperimentModel.KEY_NAME,
            ExperimentModel.KEY_VERSION
    };

    private FragmentActivity mActivity;

    private SimpleCursorAdapter mAdapter;

    private Cursor mExperimentCursor;

    private long mExperimentId;

    private Uri mUri;

    public ExperimentDelegate(long experimentId) {
        mExperimentId = experimentId;
        mUri = Uri.withAppendedPath(DataProvider.URI_EXPERIMENTS, String.valueOf(mExperimentId));
    }

    public ExperimentDelegate(Parcel in) {
        mExperimentId = in.readLong();
        mUri = Uri.withAppendedPath(DataProvider.URI_EXPERIMENTS, String.valueOf(mExperimentId));
    }

    @Override
    public boolean deleteExperiment() throws IOException {
        return mActivity.getContentResolver().delete(mUri, null, null) == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public Experiment getExperiment() throws IOException {
        if (mExperimentCursor != null) {
            mExperimentCursor.close();
            mExperimentCursor = null;
        }

        mExperimentCursor = mActivity.getContentResolver().query(mUri, null, null, null, null);
        mExperimentCursor.moveToFirst();
        ExperimentModel model = new ExperimentModel(mExperimentCursor);
        mExperimentCursor.close();
        File paleFile = new File(mActivity.getDir(FileUtils.PATH_INTERNAL_EXPERIMENTS_DIR,
                Context.MODE_PRIVATE), model.file);
        try {
            File workingDir = FileUtils.decompress(paleFile, mActivity.getExternalCacheDir());
            logExperimentDef(workingDir);
            try {
                return FileUtils.loadExperimentDefinition(new File(workingDir, "experiment.json"));
            } catch (JsonSyntaxException e) {
                logExperimentDef(workingDir);
                throw e;
            }
        } finally {
            FileUtils.clearCache(mActivity);
        }
    }

    @Override
    public PaleRow getExperimentDetails() {
        Cursor c = mActivity.getContentResolver().query(mUri, null, null, null, null);
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        PaleRow experimentRow = new PaleRow();
        new ExperimentModel(c).toDetails(experimentRow);
        c.close();

        return experimentRow;
    }

    @Override
    public long getId() {
        return mExperimentId;
    }

    @Override
    public Experiment getInitialisedExperiment() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListAdapter getRecordsAdapter(int layout, int[] fields, int[] ids) {
        String[] cols = convertFields(fields);

        mAdapter = new SimpleCursorAdapter(mActivity, layout, null, cols, ids, 0);

        mActivity.getSupportLoaderManager().initLoader(LOADER_RECORDS, null,
                new RecordsLoaderCallbacks());
        return mAdapter;
    }

    @Override
    public void init(FragmentActivity activity) {
        mActivity = activity;
    }

    @Override
    public int removeRecords(long[] ids) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean replace(Experiment experiment) throws IOException {
        ContentResolver contentResolver = mActivity.getContentResolver();

        // Need to pull the model data before changing it to so we can later
        // clear the old pale file.
        Cursor c = contentResolver.query(mUri, null, null, null, null);
        c.moveToFirst();
        ExperimentModel model = new ExperimentModel(c);

        File tempFile = new File(mActivity.getExternalCacheDir(),
                FileUtils.generateTimestampFilename());
        tempFile.createNewFile();
        String path = tempFile.getCanonicalPath();

        try {
            FileUtils.compress(tempFile, experiment);
            File paleFile = FileUtils.copyToInternalStorage(mActivity, path,
                    FileUtils.generateNewFileName(path));

            // Store experiment.
            ContentValues values = new ExperimentModel(experiment, paleFile).toValues();
            contentResolver.update(mUri, values, null, null);
        } finally {
            FileUtils.clearCache(mActivity);
        }

        File oldFile = new File(mActivity.getDir(FileUtils.PATH_INTERNAL_EXPERIMENTS_DIR,
                Context.MODE_PRIVATE), model.file);
        oldFile.delete();
        return true;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mExperimentId);
    }

    private String[] convertFields(int[] fields) {
        String[] cols = new String[fields.length];
        int numUnderstood = 0;
        for (int i = 0; i < fields.length; i++) {
            String col;
            switch (fields[i]) {
                case RECORD_ID:
                    col = RecordModel.KEY_SESSION_ID;
                    break;
                case RECORD_FILE_SIZE:
                    col = RecordModel.KEY_FILE_SIZE;
                    break;
                case RECORD_NOTE:
                    col = RecordModel.KEY_NOTE;
                    break;
                case RECORD_DATE:
                    col = RecordModel.KEY_DATE;
                    break;

                default:

                    continue;
            }
            numUnderstood++;
            cols[i] = col;
        }
        return Arrays.copyOfRange(cols, 0, numUnderstood);
    }

    private void logExperimentDef(File workingDir) throws FileNotFoundException, IOException,
            UnsupportedEncodingException {
        InputStream in = new FileInputStream(new File(workingDir, "experiment.json"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            final byte[] buffer = new byte[8192];
            int len = 0;

            while (-1 != (len = in.read(buffer))) {
                out.write(buffer, 0, len);
            }
            Log.d("experiment.json", new String(out.toByteArray(), "UTF-16"));
        } finally {
            in.close();
            out.close();
        }
    }

    private final class ExperimentLoaderCallbacks implements LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int newLoaderId, final Bundle args) {
            return new CursorLoader(mActivity, mUri, null, null, null, null);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (mExperimentCursor != null) {
                mExperimentCursor.close();
            }
            mExperimentCursor = null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor newCursor) {
            if (mExperimentCursor != null) {
                mExperimentCursor.close();
            }
            mExperimentCursor = newCursor;
        }
    }

    private final class RecordsLoaderCallbacks implements LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int newLoaderId, final Bundle args) {
            return new CursorLoader(mActivity, DataProvider.URI_RECORDS, null,
                    RecordModel.KEY_EXP_ID + "=" + mExperimentId, null, null);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Cursor oldCursor = mAdapter.swapCursor(null);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor newCursor) {
            Cursor oldCursor = mAdapter.swapCursor(newCursor);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }
    }
}
