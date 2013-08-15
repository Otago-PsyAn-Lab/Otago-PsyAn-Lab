
package nz.ac.otago.psyanlab.common.model.asset;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;

public class Image extends Asset {
    public Image() {
        mTypeId = 0x02;
        mHeaderResId = R.string.header_images;
    }
}
