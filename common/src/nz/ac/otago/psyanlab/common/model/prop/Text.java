
package nz.ac.otago.psyanlab.common.model.prop;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.util.PALEPropProperty;

import android.os.Parcel;

public class Text extends Prop {
    @Expose
    @PALEPropProperty(value="Text")
    public String text;

    @Expose
    @PALEPropProperty("Font Size")
    public int fontSize;

    public Text(Parcel in) {
        super(in);

        text = in.readString();
        fontSize = in.readInt();
    }

    public Text(Prop prop) {
        super(prop);

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
