package nz.ac.otago.psyanlab.common.model.asset;

import android.content.Context;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.typestub.VideoStub;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

public class Video extends Asset {
    protected static final int METHOD_GET_VIDEO = 0x01;

    public Video() {
        mTypeId = 0x04;
        mHeaderResId = R.string.header_videos;
    }

    @Override
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @MethodId(METHOD_GET_VIDEO)
    public VideoStub stubGetVideo() {
        return null;
    }

    protected static class MethodNameFactory extends Asset.MethodNameFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case METHOD_GET_VIDEO:
                    return context.getString(R.string.method_get_video);
                default:
                    return super.getName(context, lookup);
            }
        }
    }
}
