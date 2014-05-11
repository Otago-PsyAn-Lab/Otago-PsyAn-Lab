
package nz.ac.otago.psyanlab.common.model.prop;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.PALEPropProperty;
import nz.ac.otago.psyanlab.common.model.util.ParameterId;

import android.content.Context;
import android.os.Parcel;

public class Text extends Prop {
    private static final int METHOD_GET_TEXT = 0x04;

    private static final int METHOD_GET_TEXT2 = 0x05;

    private static final int METHOD_SET_TEXT = 0x01;

    private static final int METHOD_SET_TEXT2 = 0x02;

    private static final int METHOD_SET_TEXT3 = 0x03;

    private static final int PARAM_TEST_FLOAT = 0x03;

    private static final int PARAM_TEST_INT = 0x02;

    private static final int PARAM_TEXT = 0x01;

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public static NameResolverFactory getParameterNameFactory() {
        return new ParameterNameFactory();
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

    @MethodId(METHOD_GET_TEXT)
    public String getText() {
        return text;
    }

    @MethodId(METHOD_GET_TEXT2)
    public String getText(@ParameterId(PARAM_TEST_INT)
    int a) {
        return text;
    }

    @MethodId(METHOD_SET_TEXT)
    public void setText(@ParameterId(PARAM_TEXT)
    String text) {
        this.text = text;
    }

    @MethodId(METHOD_SET_TEXT2)
    public void setText2(@ParameterId(PARAM_TEST_INT)
    int a, @ParameterId(PARAM_TEXT)
    String text) {
        this.text = text;
    }

    @MethodId(METHOD_SET_TEXT3)
    public void setText3(@ParameterId(PARAM_TEST_FLOAT)
    float ab, @ParameterId(PARAM_TEST_INT)
    int a, @ParameterId(PARAM_TEXT)
    String text) {
        this.text = text;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(text);
        dest.writeInt(fontSize);
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
                case METHOD_GET_TEXT:
                    return R.string.test_method_get_text;
                case METHOD_GET_TEXT2:
                    return R.string.method_get_text_2;
                default:
                    return super.getResId(lookup);
            }
        }
    }

    protected static class ParameterNameFactory extends Prop.ParameterNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case PARAM_TEXT:
                    return R.string.parameter_text;
                case PARAM_TEST_FLOAT:
                    return R.string.parameter_test_float;
                case PARAM_TEST_INT:
                    return R.string.parameter_test_integer;
                default:
                    return super.getResId(lookup);
            }
        }
    }
}
