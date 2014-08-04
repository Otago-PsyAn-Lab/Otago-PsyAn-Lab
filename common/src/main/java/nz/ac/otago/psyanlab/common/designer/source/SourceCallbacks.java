
package nz.ac.otago.psyanlab.common.designer.source;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.SourceDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar;
import nz.ac.otago.psyanlab.common.model.Source;

import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.widget.ListAdapter;

import java.io.File;

public interface SourceCallbacks extends DialogueResultListenerRegistrar {
    /**
     * Get an internally cached file. Use this to pull files from a deflated
     * experiment.
     * 
     * @param path Path of the file to get.
     * @return File as indicated.
     */
    public File getCachedFile(String path);

    /**
     * Start the activity or dialogue in which the user will choose a data
     * source document to import.
     */
    public void startImportSourcesUI();

    /**
     * Add a listener that will be called when the experiment asset data set is
     * modified. The listener must be removed before the fragment or activity is
     * finished or there will be crashes from calls to dangling references.
     * 
     * @param listener Listener for data changes in the asset data set.
     */
    void addSourceDataChangeListener(SourceDataChangeListener listener);

    void addDrawerListener(DrawerListener listener);

    /**
     * Delete indicated source.
     * 
     * @param id Id of source to be deleted.
     */
    void deleteSource(long id);

    /**
     * Get the indicated source.
     * 
     * @param id Id of source to get.
     * @return Source desired.
     */
    Source getSource(long id);

    /**
     * Get an adapter for the entire set of sources in the experiment.
     * 
     * @return Sources adapter.
     */
    ListAdapter getSourcesAdapter();

    /**
     * Replace an existing source with a new one.
     * 
     * @param id Id of source to be replaced.
     * @param source New source to be stored.
     */
    void putSource(long id, Source source);

    /**
     * Remove a listener from the set of source data change listeners. This must
     * be called before an activity or fragment finishes to clean up dangling
     * references.
     * 
     * @param listener
     */
    void removeSourceDataChangeListener(SourceDataChangeListener listener);

    void removeDrawerListener(DrawerListener drawerListener);
}
