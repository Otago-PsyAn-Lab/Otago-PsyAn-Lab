
package nz.ac.otago.psyanlab.single;

import nz.ac.otago.psyanlab.common.PaleRow;
import nz.ac.otago.psyanlab.common.UserExperimentDelegateI;
import nz.ac.otago.psyanlab.common.model.Experiment;
import nz.ac.otago.psyanlab.single.model.ExperimentModel;
import nz.ac.otago.psyanlab.single.model.RecordModel;

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
import android.widget.ListAdapter;

import java.io.IOException;
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

    private long mExperimentId;

    private PaleRow mExperimentRow;

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
    public PaleRow getExperimentDetails() {
        if (mExperimentRow == null) {
            Cursor c = mActivity.getContentResolver().query(mUri, sProjection, null, null, null);
            if (!c.moveToFirst()) {
                c.close();
                return null;
            }

            mExperimentRow = new PaleRow();
            new ExperimentModel(c).toDetails(mExperimentRow);
            c.close();
        }
        return mExperimentRow;
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
        // TODO Auto-generated method stub
        return false;
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

    @Override
    public Experiment getExperiment() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
}
