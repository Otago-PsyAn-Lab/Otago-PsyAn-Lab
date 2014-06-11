
package nz.ac.otago.psyanlab.common.model.asset;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.typestub.VideoStub;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

public class Video extends Asset {
    protected static final int METHOD_GET_VIDEO = 0x01;

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public Video() {
        mTypeId = 0x04;
        mHeaderResId = R.string.header_videos;
    }

    @MethodId(METHOD_GET_VIDEO)
    public VideoStub stubGetVideo() {
        return null;
    }

    protected static class MethodNameFactory extends Asset.MethodNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case METHOD_GET_VIDEO:
                    return R.string.method_get_video;
                default:
                    return super.getResId(lookup);
            }
        }
    }
}
