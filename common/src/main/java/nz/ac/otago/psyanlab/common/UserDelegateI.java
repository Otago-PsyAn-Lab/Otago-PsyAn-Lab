
/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

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
    Uri addExperiment(Experiment experiment) throws IOException;

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
