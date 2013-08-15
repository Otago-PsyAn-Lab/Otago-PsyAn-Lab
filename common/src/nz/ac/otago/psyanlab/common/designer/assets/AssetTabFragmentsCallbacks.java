
package nz.ac.otago.psyanlab.common.designer.assets;

import nz.ac.otago.psyanlab.common.model.Asset;

import android.widget.ListAdapter;

import java.io.File;

public interface AssetTabFragmentsCallbacks {
    public void displayAsset(long id);

    public void doImportAsset();

    void deleteAsset(long id);

    Asset getAsset(long id);

    ListAdapter getAssetsAdapter();

    void updateAsset(long id, Asset asset);

    public File getWorkingDirectory();
}
