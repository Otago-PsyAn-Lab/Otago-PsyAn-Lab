
package nz.ac.otago.psyanlab.common.model.prop;

import nz.ac.otago.psyanlab.common.model.Prop;

import android.content.Context;

public class Image extends Prop {
    public Image(Context context, Prop prop, int defaultSuffix) {
        super(context, prop, defaultSuffix);

        // if (TextUtils.isEmpty(name)) {
        // name = context.getString(R.string.default_image_prop_name);
        // }
    }
}
