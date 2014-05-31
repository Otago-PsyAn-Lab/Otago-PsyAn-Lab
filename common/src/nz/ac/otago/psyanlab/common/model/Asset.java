
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.asset.Csv;
import nz.ac.otago.psyanlab.common.model.asset.Image;
import nz.ac.otago.psyanlab.common.model.asset.Sound;
import nz.ac.otago.psyanlab.common.model.asset.Video;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

import java.io.File;
import java.util.regex.Pattern;

public abstract class Asset implements ExperimentObject {
    private static final String FILE_ENDINGS_CSV_DATA = ".*\\.csv";

    private static final String FILE_ENDINGS_IMAGES = ".*\\.jpg|.*\\.jpeg|.*\\.png|.*\\.bmp|.*\\.webp";

    private static final String FILE_ENDINGS_SOUNDS = ".*\\.m4a|.*\\.aac|.*\\.flac|.*\\.mp3|.*\\.ogg|.*\\.ogm|.*\\.wav";

    private static final String FILE_ENDINGS_VIDEOS = ".*\\.mp4|.*\\.webm|.*\\.mkv";

    private static AssetFactory mFactory = new AssetFactory();

    public static AssetFactory getFactory() {
        return mFactory;
    }

    @Expose
    public String filename;

    @Expose
    public long filesize;

    @Expose
    public String name;

    @Expose
    public String path;

    private boolean mIsExternal = false;

    protected int mHeaderResId;

    protected long mTypeId = 0x00;

    public int getHeaderResId() {
        return mHeaderResId;
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.format_asset_class_name, name);
    }

    public long getTypeId() {
        return mTypeId;
    }

    public boolean isExternal() {
        return mIsExternal;
    }

    @Override
    public int kind() {
        return ExperimentObjectReference.KIND_ASSET;
    }

    public void setExternalFile(File file) {
        filesize = file.length();
        path = file.getPath();
        filename = file.getName();
        name = file.getName();
        mIsExternal = true;
    }

    public static final class AssetFactory {
        public Asset newAsset(String filePath) {
            File file = new File(filePath);
            String filename = file.getName();

            Pattern csvPattern = Pattern.compile(FILE_ENDINGS_CSV_DATA, Pattern.CASE_INSENSITIVE);
            Pattern imagesPattern = Pattern.compile(FILE_ENDINGS_IMAGES, Pattern.CASE_INSENSITIVE);
            Pattern soundsPattern = Pattern.compile(FILE_ENDINGS_SOUNDS, Pattern.CASE_INSENSITIVE);
            Pattern videosPattern = Pattern.compile(FILE_ENDINGS_VIDEOS, Pattern.CASE_INSENSITIVE);

            Asset asset;
            if (csvPattern.matcher(filename).matches()) {
                asset = new Csv();
            } else if (imagesPattern.matcher(filename).matches()) {
                asset = new Image();
            } else if (soundsPattern.matcher(filename).matches()) {
                asset = new Sound();
            } else if (videosPattern.matcher(filename).matches()) {
                asset = new Video();
            } else {
                throw new RuntimeException("Unknown asset file type");
            }
            asset.setExternalFile(file);

            return asset;
        }
    }

    public static class Comparator implements java.util.Comparator<Asset> {
        @Override
        public int compare(Asset lhs, Asset rhs) {
            int cmpr = lhs.getClass().getName().compareToIgnoreCase(rhs.getClass().getName());
            if (cmpr == 0 && lhs.name != null && rhs != null) {
                return lhs.name.compareToIgnoreCase(rhs.name);
            }
            return cmpr;
        }
    }

    protected static class MethodNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                default:
                    return R.string.method_missing_string;
            }
        }
    }
}
