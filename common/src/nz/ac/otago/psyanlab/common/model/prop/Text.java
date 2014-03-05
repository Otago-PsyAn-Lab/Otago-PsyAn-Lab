
package nz.ac.otago.psyanlab.common.model.prop;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.PALEPropProperty;

import android.content.Context;
import android.os.Parcel;

public class Text extends Prop {
    private static final int METHOD_SET_TEXT = 0x01;

    private static final int METHOD_SET_TEXT2 = 0x02;

    private static final int METHOD_SET_TEXT3 = 0x03;

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Expose
    @PALEPropProperty(value = "Font Size")
    public int fontSize = -1;

    @Expose
    @PALEPropProperty(value = "String")
    public String text;

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

    public Text(Parcel in) {
        super(in);

        text = in.readString();
        fontSize = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(text);
        dest.writeInt(fontSize);
    }

    @MethodId(METHOD_SET_TEXT)
    public void setText(String text) {
        this.text = text;
    }

    @MethodId(METHOD_SET_TEXT2)
    public void setText2(int a, String text) {
        this.text = text;
    }

    @MethodId(METHOD_SET_TEXT3)
    public void setText3(float ab, int a, String text) {
        this.text = text;
    }

    protected static class EventNameFactory extends Prop.EventNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                default:
                    return super.getResId(lookup);
            }
        }
    }

    protected static class MethodNameFactory extends Prop.MethodNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case METHOD_SET_TEXT:
                    return R.string.method_set_string;
                case METHOD_SET_TEXT2:
                    return R.string.test_method_2;
                case METHOD_SET_TEXT3:
                    return R.string.test_method_3;
                default:
                    return super.getResId(lookup);
            }
        }
    }
}
