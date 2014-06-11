
package nz.ac.otago.psyanlab.common.model.asset;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.typestub.ImageStub;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

public class Image extends Asset {
    protected static final int METHOD_GET_IMAGE = 0x01;

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public Image() {
        mTypeId = 0x02;
        mHeaderResId = R.string.header_images;
    }

    @MethodId(METHOD_GET_IMAGE)
    public ImageStub stubGetImage() {
        return null;
    }

    protected static class MethodNameFactory extends Asset.MethodNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case METHOD_GET_IMAGE:
                    return R.string.method_get_image;
                default:
                    return super.getResId(lookup);
            }
        }
    }
}
