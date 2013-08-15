
package nz.ac.otago.psyanlab.common.model.asset;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;

public class Sound extends Asset {
    public Sound() {
        mTypeId = 0x03;
        mHeaderResId = R.string.header_sounds;
    }
}
