
package nz.ac.otago.psyanlab.common.model.prop;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.typestub.ImageStub;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.ParameterId;

import android.content.Context;
import android.os.Parcel;

public class Image extends Prop {
    protected static final int METHOD_GET_IMAGE = 0x101;

    protected static final int METHOD_SET_IMAGE = 0x102;

    protected static final int PARAM_IMAGE = 0x101;

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public static NameResolverFactory getParameterNameFactory() {
        return new ParameterNameFactory();
    }

    public Image(Context context, Prop prop, int defaultSuffix) {
        super(context, prop, defaultSuffix);
    }

    public Image(Parcel in) {
        super(in);
    }

    @MethodId(METHOD_GET_IMAGE)
    public ImageStub stubGetImage() {
        return null;
    }

    @MethodId(METHOD_SET_IMAGE)
    public void stubSetImage(@ParameterId(PARAM_IMAGE) ImageStub image) {
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
                case METHOD_GET_IMAGE:
                    return R.string.method_get_image;
                case METHOD_SET_IMAGE:
                    return R.string.method_set_image;
                default:
                    return super.getResId(lookup);
            }
        }
    }

    protected static class ParameterNameFactory extends Prop.ParameterNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case PARAM_IMAGE:
                    return R.string.parameter_image;
                default:
                    return super.getResId(lookup);
            }
        }
    }
}
