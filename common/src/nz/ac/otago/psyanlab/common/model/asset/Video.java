
package nz.ac.otago.psyanlab.common.model.asset;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;

public class Video extends Asset {
    public Video() {
        mTypeId = 0x04;
        mHeaderResId = R.string.header_videos;
    }
}
