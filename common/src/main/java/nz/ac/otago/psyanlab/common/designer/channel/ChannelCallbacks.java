
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
