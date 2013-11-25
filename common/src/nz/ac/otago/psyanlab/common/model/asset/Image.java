
package nz.ac.otago.psyanlab.common.model.asset;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

public class Image extends Asset {
    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public Image() {
        mTypeId = 0x02;
        mHeaderResId = R.string.header_images;
    }

    protected static class MethodNameFactory extends Asset.MethodNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                default:
                    return super.getResId(lookup);
            }
        }
    }
}
