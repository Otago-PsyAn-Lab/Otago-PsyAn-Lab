
package nz.ac.otago.psyanlab.common.model.prop;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.util.PALEPropProperty;

import android.content.Context;
import android.os.Parcel;

public class Text extends Prop {
    @Expose
    @PALEPropProperty(value = "String")
    public String text;

    @Expose
    @PALEPropProperty(value = "Font Size")
    public int fontSize = -1;

    public Text(Parcel in) {
        super(in);

        text = in.readString();
        fontSize = in.readInt();
    }

    public Text(Context context, Prop prop, int defaultSuffix) {
        super(context, prop, defaultSuffix);

        // if (TextUtils.isEmpty(name)) {
        // name = context.getString(R.string.default_text_prop_name);
        // }

        fontSize = context.getResources().getDimensionPixelSize(R.dimen.default_text_size);

        if (prop == null) {
            return;
        }

        if (prop instanceof Text) {
            Text old = (Text)prop;
            text = old.text;
            fontSize = old.fontSize;
        }

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(text);
        dest.writeInt(fontSize);
    }
}
