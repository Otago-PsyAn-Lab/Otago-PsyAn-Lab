
package nz.ac.otago.psyanlab.single;

import nz.ac.otago.psyanlab.common.UserDelegateI;
import nz.ac.otago.psyanlab.common.model.Experiment;
import nz.ac.otago.psyanlab.single.model.ExperimentModel;

import org.json.JSONException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class UserDelegate implements UserDelegateI {
    /**
     * CREATOR for Parcelable.
     */
    public static final Creator<UserDelegate> CREATOR = new Creator<UserDelegate>() {
        @Override
        public UserDelegate createFromParcel(Parcel in) {
            return new UserDelegate(in);
        }

        @Override
        public UserDelegate[] newArray(int size) {
            return new UserDelegate[size];
        }
    };

    private static final int LOADER_EXPERIMENTS = 0x01;

    private static final String PATH_INTERNAL_TEMP_DIR = "temp";

    private static final String[] sExperimentsCols = new String[] {
            ExperimentModel.namespaced(ExperimentModel.KEY_ID), ExperimentModel.KEY_NAME
    };

    private FragmentActivity mActivity;

    private SimpleCursorAdapter mAdapter;

    public UserDelegate() {
    }

    public UserDelegate(Parcel in) {
    }

    @Override
    public Uri addExperiment(Experiment experiment) throws IOException {
        // Compress experiment and store it in a temporary file. This file is
        // then copied and named as appropriate as part of the data provider
        // insert operation.
        File tempFile = new File(mActivity.getExternalCacheDir(),
                FileUtils.generateTimestampFilename());
        tempFile.createNewFile();
        String path = tempFile.getCanonicalPath();

        Uri uri = null;
        try {
            FileUtils.compress(tempFile, experiment);
            File paleFile = FileUtils.copyToInternalStorage(mActivity, path,
                    FileUtils.generateNewFileName(path));

            // Store experiment.
            ContentValues values = new ExperimentModel(experiment, paleFile).toValues();
            ContentResolver contentResolver = mActivity.getContentResolver();
            uri = contentResolver.insert(DataProvider.URI_EXPERIMENTS, values);
        } finally {
            FileUtils.clearCache(mActivity);
        }
        return uri;
    }

    @Override
    public Uri addExperiment(String path) throws JSONException, IOException {
        File paleFile = FileUtils.copyToInternalStorage(mActivity, path,
                FileUtils.generateNewFileName(path));
        ContentValues values = new ExperimentModel(FileUtils.loadExperimentDefinition(paleFile),
                paleFile).toValues();

        ContentResolver contentResolver = mActivity.getContentResolver();
        return contentResolver.insert(DataProvider.URI_EXPERIMENTS, values);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public ListAdapter getExperimentsAdapter(int layout, int[] fields, int[] to) {
        String[] from = convertFields(fields);
        mAdapter = new SimpleCursorAdapter(mActivity, layout, null, from, to, 0);
        mActivity.getSupportLoaderManager().initLoader(LOADER_EXPERIMENTS, null,
                new ExperimentsLoaderCallbacks());

        return mAdapter;
    }

    @Override
    public ExperimentDelegate getUserExperimentDelegate(long experimentId) {
        ExperimentDelegate userExperimentDelegate = new ExperimentDelegate(experimentId);
        userExperimentDelegate.init(mActivity);
        return userExperimentDelegate;
    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public void init(FragmentActivity activity) {
        mActivity = activity;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    private String[] convertFields(int[] fields) {
        String[] cols = new String[fields.length];
        int numUnderstood = 0;
        for (int i = 0; i < fields.length; i++) {
            String col;
            switch (fields[i]) {
                case EXPERIMENT_NAME:
                    col = ExperimentModel.KEY_NAME;
                    break;
                case EXPERIMENT_DATE_CREATED:
                    col = ExperimentModel.KEY_DATE_CREATED;
                    break;
                case EXPERIMENT_DESCRIPTION:
                    col = ExperimentModel.KEY_DESCRIPTION;
                    break;
                case EXPERIMENT_FILE_SIZE:
                    col = ExperimentModel.KEY_FILE_SIZE;
                    break;
                case EXPERIMENT_VERSION:
                    col = ExperimentModel.KEY_VERSION;
                    break;
                case EXPERIMENT_LAST_RUN:
                    col = ExperimentModel.KEY_LAST_RUN;
                    break;

                default:
                    continue;
            }
            numUnderstood++;
            cols[i] = col;
        }
        return Arrays.copyOfRange(cols, 0, numUnderstood);
    }

    private final class ExperimentsLoaderCallbacks implements LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int newLoaderId, final Bundle args) {
            return new CursorLoader(mActivity, DataProvider.URI_EXPERIMENTS, sExperimentsCols,
                    null, null, ExperimentModel.KEY_NAME + " ASC");
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
