
package nz.ac.otago.psyanlab.common;

import nz.ac.otago.psyanlab.common.model.Experiment;

import android.annotation.SuppressLint;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.widget.ListAdapter;

import java.io.IOException;

/**
 * Interface for classes that enable operations upon an experiment belonging to
 * some user.
 */
@SuppressLint("ParcelCreator")
public interface UserExperimentDelegateI extends Parcelable {
    static final int RECORD_FILE_SIZE = 0x01;

    static final int RECORD_NOTE = 0x02;

    static final int RECORD_DATE = 0x03;

    static final int RECORD_ID = 0x04;

    /**
     * Store
     * 
     * @param records
     * @throws IOException
     */
    // int addRecords(SessionData records) throws IOException;

    boolean deleteExperiment() throws IOException;

    /**
     * Get the details about the experiment.
     * 
     * @return Experiment details.
     */
    PaleRow getExperimentDetails();

    /**
     * Get the experiment (in its initialised form) that this delegate
     * represents.
     * 
     * @return The experiment.
     * @throws IOException
     */
    Experiment getInitialisedExperiment() throws IOException;

    ListAdapter getRecordsAdapter(int layout, int[] fields, int[] ids);

    void init(FragmentActivity activity);

    int removeRecords(long[] ids) throws IOException;

    /**
     * Replace the current experiment on the disk. Use for in-place editing of
     * experiments.
     * 
     * @param experiment Experiment to replace the old version on the disk.
     * @throws IOException
     */
    boolean replace(Experiment experiment) throws IOException;

    /**
     * Get the experiment this delegate represents.
     * 
     * @return Experiment Memory model of experiment.
     * @throws IOException on error when reading experiment.
     */
    Experiment getExperiment() throws IOException;
}
