
package nz.ac.otago.psyanlab.common.designer.assets;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.AssetDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar;
import nz.ac.otago.psyanlab.common.model.Asset;

import android.support.v4.widget.DrawerLayout.DrawerListener;

import java.io.File;

public interface AssetCallbacks extends DialogueResultListenerRegistrar {
    /**
     * Get an internally cached file. Use this to pull files from a deflated
     * experiment.
     * 
     * @param path Path of the file to get.
     * @return File as indicated.
     */
    public File getCachedFile(String path);

    /**
     * Start the activity or dialogue in which the user will choose an asset to
     * import.
     */
    public void startImportAssetUI();

    /**
     * Add a listener that will be called when the experiment asset data set is
     * modified. The listener must be removed before the fragment or activity is
     * finished or there will be crashes from calls to dangling references.
     * 
     * @param listener Listener for data changes in the asset data set.
     */
    void addAssetDataChangeListener(AssetDataChangeListener listener);

    void addDrawerListener(DrawerListener listener);

    /**
     * Delete indicated asset.
     * 
     * @param id Id of asset to be deleted.
     */
    void deleteAsset(long id);

    /**
     * Get the indicated asset.
     * 
     * @param id Id of asset to get.
     * @return Asset desired.
     */
    Asset getAsset(long id);

    /**
     * Get an adapter for the entire set of assets in the experiment.
     * 
     * @return Assets adapter.
     */
    StickyGridHeadersSimpleAdapter getAssetsAdapter();

    /**
     * Replace an existing asset with a new one.
     * 
     * @param id Id of asset to be replaced.
     * @param asset New asset to be stored.
     */
    void putAsset(long id, Asset asset);

    /**
     * Remove a listener from the set of asset data change listeners. This must
     * be called before an activity or fragment finishes to clean up dangling
     * references.
     * 
     * @param listener
     */
    void removeAssetDataChangeListener(AssetDataChangeListener listener);

    void removeDrawerListener(DrawerListener drawerListener);
}
