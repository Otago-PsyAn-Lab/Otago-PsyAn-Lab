
/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.asset.Image;
import nz.ac.otago.psyanlab.common.model.asset.Sound;
import nz.ac.otago.psyanlab.common.model.asset.Video;

import android.content.Context;

import java.io.File;
import java.util.regex.Pattern;

public abstract class Asset extends ExperimentObject implements Comparable<Asset> {
    public static final String FILE_ENDINGS_IMAGES = ".*\\.jpg|.*\\.jpeg|.*\\.png|.*\\.bmp|.*\\.webp";

    public static final String FILE_ENDINGS_SOUNDS = ".*\\.m4a|.*\\.aac|.*\\.flac|.*\\.mp3|.*\\.ogg|.*\\.ogm|.*\\.wav";

    public static final String FILE_ENDINGS_VIDEOS = ".*\\.mp4|.*\\.webm|.*\\.mkv";

    private static AssetFactory mFactory = new AssetFactory();

    public static AssetFactory getFactory() {
        return mFactory;
    }

    public static final class AssetFactory {
        public Asset newAsset(String filePath) {
            File file = new File(filePath);
            String filename = file.getName();

            Pattern imagesPattern = Pattern.compile(FILE_ENDINGS_IMAGES, Pattern.CASE_INSENSITIVE);
            Pattern soundsPattern = Pattern.compile(FILE_ENDINGS_SOUNDS, Pattern.CASE_INSENSITIVE);
            Pattern videosPattern = Pattern.compile(FILE_ENDINGS_VIDEOS, Pattern.CASE_INSENSITIVE);

            Asset asset;
            if (imagesPattern.matcher(filename).matches()) {
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

    @Expose
    public String filename;

    @Expose
    public long filesize;

    @Expose
    public String name;

    @Expose
    public String path;

    protected int mHeaderResId;

    protected long mTypeId = 0x00;

    private boolean mIsExternal = false;

    @Override
    public int compareTo(Asset another) {
        int cmpr = getClass().getName().compareToIgnoreCase(another.getClass().getName());
        if (cmpr == 0 && name != null && another != null) {
            return name.compareToIgnoreCase(another.name);
        }
        return cmpr;
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.format_asset_class_name, name);
    }

    public int getHeaderResId() {
        return mHeaderResId;
    }

    @Override
    public int getKindResId() {
        return R.string.label_asset;
    }

    public long getTypeId() {
        return mTypeId;
    }

    public boolean isExternal() {
        return mIsExternal;
    }

    @Override
    public int kind() {
        return ExperimentObject.KIND_ASSET;
    }

    public void setExternalFile(File file) {
        filesize = file.length();
        path = file.getPath();
        filename = file.getName();
        name = file.getName();
        mIsExternal = true;
    }
}
