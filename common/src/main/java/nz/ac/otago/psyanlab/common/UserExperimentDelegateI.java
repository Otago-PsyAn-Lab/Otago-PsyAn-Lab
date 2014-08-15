
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

import android.annotation.SuppressLint;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.widget.ListAdapter;

import java.io.File;
import java.io.IOException;

/**
 * Interface for classes that enable operations upon an experiment belonging to
 * some user.
 */
@SuppressLint("ParcelCreator")
public interface UserExperimentDelegateI extends Parcelable {
    static final int RECORD_DATE = 0x03;

    static final int RECORD_FILE_SIZE = 0x01;

    static final int RECORD_ID = 0x04;

    static final int RECORD_NOTE = 0x02;

    /**
     * Cleanup anything left behind while the experiment was opened.
     */
    void closeExperiment();

    /**
     * Store
     * 
     * @param records
     * @throws IOException
     */
    // int addRecords(SessionData records) throws IOException;

    boolean deleteExperiment() throws IOException;

    /**
     * Get a file from storage.
     * 
     * @param path Path may be internal or external.
     * @return File from storage.
     */
    File getFile(String path);

    /**
     * Get the details about the experiment.
     * 
     * @return Experiment details.
     */
    PaleRow getExperimentDetails();

    /**
     * Get the experiment id.
     * 
     * @return Experiment id.
     */
    long getId();

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

    /**
     * Unpack the experiment this delegate represents.
     * 
     * @return Experiment Memory model of experiment.
     * @throws IOException on error when reading experiment.
     */
    Experiment openExperiment() throws IOException;

    int removeRecords(long[] ids) throws IOException;

    /**
     * Replace the current experiment on the disk. Use for in-place editing of
     * experiments.
     * 
     * @param experiment Experiment to replace the old version on the disk.
     * @throws IOException
     */
    boolean replace(Experiment experiment) throws IOException;
}
