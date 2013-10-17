
package nz.ac.otago.psyanlab.common.model.prop;

import nz.ac.otago.psyanlab.common.model.Prop;

import android.content.Context;

public class Button extends Text {
    public Button(Context context, Prop prop, int defaultSuffix) {
        super(context, prop, defaultSuffix);

        // if (TextUtils.isEmpty(name)
        // || TextUtils.equals(name,
        // context.getString(R.string.default_text_prop_name))) {
        // name = context.getString(R.string.default_button_prop_name);
        // }
    }
}
