
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
