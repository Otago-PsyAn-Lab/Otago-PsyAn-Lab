
package nz.ac.otago.psyanlab.common.model.asset;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.typestub.FontStub;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

public class Font extends Asset {
    protected static final int METHOD_GET_FONT = 0x01;

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public Font() {
        mTypeId = 0x05;
        mHeaderResId = R.string.header_fonts;
    }

    @MethodId(METHOD_GET_FONT)
    public FontStub stubGetFont() {
        return null;
    }

    protected static class MethodNameFactory extends Asset.MethodNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case METHOD_GET_FONT:
                    return R.string.method_get_font;
                default:
                    return super.getResId(lookup);
            }
        }
    }
}
