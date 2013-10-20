
package nz.ac.otago.psyanlab.common.designer.assets;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.AssetDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Asset;

import java.io.File;

public interface AssetTabFragmentsCallbacks {
    public void displayAsset(long id);

    public void doImportAsset();

    public File getWorkingDirectory();

    void addAssetDataChangeListener(AssetDataChangeListener listener);

    void deleteAsset(long id);

    Asset getAsset(long id);

    StickyGridHeadersSimpleAdapter getAssetsAdapter();

    void removeAssetDataChangeListener(AssetDataChangeListener listener);

    void updateAsset(long id, Asset asset);
}
