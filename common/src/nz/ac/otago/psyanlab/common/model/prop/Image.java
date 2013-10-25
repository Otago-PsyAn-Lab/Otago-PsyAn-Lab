
package nz.ac.otago.psyanlab.common.model.prop;

import nz.ac.otago.psyanlab.common.model.Prop;

import android.content.Context;
import android.os.Parcel;

public class Image extends Prop {
    public Image(Parcel in) {
        super(in);
    }
    
    public Image(Context context, Prop prop, int defaultSuffix) {
        super(context, prop, defaultSuffix);

        // if (TextUtils.isEmpty(name)) {
        // name = context.getString(R.string.default_image_prop_name);
        // }
    }
}
