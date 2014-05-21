
package nz.ac.otago.psyanlab.common.designer.assets;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.AssetDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar;
import nz.ac.otago.psyanlab.common.model.Asset;

public interface AssetTabFragmentsCallbacks extends DialogueResultListenerRegistrar {
    public void displayAsset(long id);

    public void doImportAsset();

    void addAssetDataChangeListener(AssetDataChangeListener listener);

    void deleteAsset(long id);

    Asset getAsset(long id);

    StickyGridHeadersSimpleAdapter getAssetsAdapter();

    void removeAssetDataChangeListener(AssetDataChangeListener listener);

    void updateAsset(long id, Asset asset);
}
