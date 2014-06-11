
package nz.ac.otago.psyanlab.common.model.asset;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.typestub.SoundStub;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

public class Sound extends Asset {
    protected static final int METHOD_GET_SOUND = 0x01;

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public Sound() {
        mTypeId = 0x03;
        mHeaderResId = R.string.header_sounds;
    }

    @MethodId(METHOD_GET_SOUND)
    public SoundStub stubGetSound() {
        return null;
    }

    protected static class MethodNameFactory extends Asset.MethodNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case METHOD_GET_SOUND:
                    return R.string.method_get_sound;
                default:
                    return super.getResId(lookup);
            }
        }
    }
}
