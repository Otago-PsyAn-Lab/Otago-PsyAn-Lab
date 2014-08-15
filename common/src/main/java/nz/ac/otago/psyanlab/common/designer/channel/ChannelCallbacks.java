
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

package nz.ac.otago.psyanlab.common.designer.channel;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity
        .DataChannelDataChangeListener;

import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar
        .DialogueResultListener;
import nz.ac.otago.psyanlab.common.model.DataChannel;

import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.widget.ListAdapter;

public interface ChannelCallbacks {
    /**
     * Create a data channel.
     *
     * @param dataChannel Data channel to create.
     * @return Id of created data channel.
     */
    long addDataChannel(DataChannel dataChannel);

    /**
     * Add a listener for changes to data channels. This listener needs to be
     * removed before the fragment or activity finishes to prevent dangling
     * references from being called.
     *
     * @param listener Listener that will be called when data channels are
     *                 modified.
     */
    void addDataChannelDataChangeListener(DataChannelDataChangeListener listener);

    void addDrawerListener(DrawerListener listener);

    /**
     * Delete indicated data channel from the experiment.
     *
     * @param id Id indicating data channel to delete.
     */
    void deleteDataChannel(long id);

    /**
     * Get a data channel.
     *
     * @param id Id of data channel to get.
     * @return Data channel desired.
     */
    DataChannel getDataChannel(long id);

    /**
     * Get an adapter for all data channels in the experiment.
     *
     * @return Adapter for data channels.
     */
    ListAdapter getDataChannelsAdapter();

    /**
     * Replace an existing data channel with a new one.
     *
     * @param id          Id of data channel to replace.
     * @param dataChannel New data channel.
     */
    void putDataChannel(long id, DataChannel dataChannel);

    /**
     * Remove an existing listener.
     *
     * @param listener Listener to remove.
     */
    void removeDataChannelDataChangeListener(DataChannelDataChangeListener listener);

    void removeDrawerListener(DrawerListener drawerListener);

    void registerDialogueResultListener(int requestCode, DialogueResultListener listener);
}
