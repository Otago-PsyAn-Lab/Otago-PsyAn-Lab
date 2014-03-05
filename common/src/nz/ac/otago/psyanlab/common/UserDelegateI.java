
package nz.ac.otago.psyanlab.common;

import nz.ac.otago.psyanlab.common.model.Experiment;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.widget.ListAdapter;

import java.io.IOException;

/**
 * Interface for classes that enable operations upon some user.
 */
@SuppressLint("ParcelCreator")
public interface UserDelegateI extends Parcelable {
    int EXPERIMENT_DATE_CREATED = 0x01;

    int EXPERIMENT_DESCRIPTION = 0x02;

    int EXPERIMENT_FILE_SIZE = 0x03;

    int EXPERIMENT_LAST_RUN = 0x04;

    int EXPERIMENT_NAME = 0x05;

    int EXPERIMENT_VERSION = 0x06;

    /**
     * Add an experiment to the set of experiments belonging to the user this
     * delegate represents.
     * 
     * @param experiment Experiment to add for the user.
     * @throws IOException
     */
    void addExperiment(Experiment experiment) throws IOException;

    /**
     * Add an experiment to the set of experiments belonging to the user this
     * delegate represents.
     * 
     * @param path Path to the PALE file that is to be added.
     * @throws IOException
     */
    Uri addExperiment(String path) throws JSONException, IOException;

    ListAdapter getExperimentsAdapter(int layout, int[] fields, int[] ids);

    /**
     * Get a delegate for an experiment belonging to this user.
     * 
     * @param experimentId Id of the experiment to get a delegate for.
     * @return Delegate for he experiment.
     */
    UserExperimentDelegateI getUserExperimentDelegate(long experimentId);

    /**
     * Get the name of the user this delegate represents.
     * 
     * @return Name of user.
     */
    String getUserName();

    void init(FragmentActivity activity);
}
