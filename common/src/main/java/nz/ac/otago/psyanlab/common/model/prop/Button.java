package nz.ac.otago.psyanlab.common.model.prop;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.ParameterId;

import android.content.Context;
import android.os.Parcel;

public class Button extends Text {
    protected static final int METHOD_SET_COLOUR_TINT = 0x01 + Prop.BUTTON_NS_OFFSET;

    protected static final int METHOD_SET_COLOUR_TINT_HTML = 0x02 + Prop.BUTTON_NS_OFFSET;

    protected static final int METHOD_SET_COLOUR_TINT_RGB = 0x03 + Prop.BUTTON_NS_OFFSET;

    public Button(Context context, Prop prop) {
        super(context, prop);
    }

    public Button(Parcel in) {
        super(in);
    }

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Override
    public NameResolverFactory getParameterNameFactory() {
        return new ParameterNameFactory();
    }

    @MethodId(METHOD_SET_COLOUR_TINT)
    public void stubSetColourTint(@ParameterId(PARAM_COLOUR) int colour) {
    }

    @MethodId(METHOD_SET_COLOUR_TINT_HTML)
    public void stubSetColourTintHTML(@ParameterId(PARAM_COLOUR_HTML) String htmlColourCode) {
    }

    @MethodId(METHOD_SET_COLOUR_TINT_RGB)
    public void stubSetColourTintRGB(@ParameterId(PARAM_RED) int red,
                                     @ParameterId(PARAM_GREEN) int green,
                                     @ParameterId(PARAM_BLUE) int blue) {
    }

    protected static class MethodNameFactory extends Text.MethodNameFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case METHOD_SET_COLOUR_TINT_HTML:
                    return context.getString(R.string.method_set_colour_tint_html);
                case METHOD_SET_COLOUR_TINT_RGB:
                    return context.getString(R.string.method_set_colour_tint_rgb);
                case METHOD_SET_COLOUR_TINT:
                    return context.getString(R.string.method_set_colour_tint);

                default:
                    return super.getName(context, lookup);
            }
        }
    }

    protected static class ParameterNameFactory extends Text.ParameterNameFactory {

        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                default:
                    return super.getName(context, lookup);
            }
        }
    }
}
