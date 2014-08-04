/*
 * Copyright (c) 2012, 2013 University of Otago, Tonic Artos <tonic.artos@gmail.com>
 * 
 * Otago PsyAn Lab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * In accordance with Section 7(b) of the GNU General Public License version 3,
 * all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.common.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class BitmapCache extends LruCache<String, BitmapPack> {
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Sampled image will always be larger than the target area.
            if (width > height) {
                inSampleSize = height / reqHeight;
            } else {
                inSampleSize = width / reqWidth;
            }
        }
        return inSampleSize;
    }

    /**
     * Decode a bitmap from file, sizing appropriately.
     * 
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static BitmapPack decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return new BitmapPack(BitmapFactory.decodeFile(path, options), options.inSampleSize);
    }

    private HashMap<String, BitmapParams> map = new HashMap<String, BitmapCache.BitmapParams>();

    /**
     * Create a new BitmapCache.
     * 
     * @param cacheSize
     *            Cache size in bytes.
     */
    public BitmapCache(int cacheSize) {
        super(cacheSize);
    }

    public void loadBitmap(String path, ImageView imageView) {
        final BitmapPack pack = getBitmapFromMemCache(path);
        if (pack != null) {
            if (pack.sampleSize > 1 && (pack.bitmap.getWidth() < imageView.getWidth() || pack.bitmap.getHeight() < imageView.getHeight())) {
                // The image was sampled before for a smaller ImageView and now
                // needs to be re-sampled for the new ImageView.
                new BitmapWorkerTask(imageView).execute(prepareBitmapParams(path, imageView));
            } else {
                imageView.setImageBitmap(pack.bitmap);
            }
        } else {
            new BitmapWorkerTask(imageView).execute(prepareBitmapParams(path, imageView));
        }
    }

    public void preloadBitmaps() {
        for (BitmapParams params : map.values()) {
            new BitmapWorkerTask(null).execute(params);
        }
    }

    /**
     * Prepares parameters for loading a bitmap into the cache. The parameters
     * are themselves cached so many ImageViews can be run through generating
     * only one set of parameters to fit all ImageViews using the same image.
     * 
     * @param path
     *            Path to bitmap.
     * @param imageView
     *            ImageView to use the bitmap.
     * @return
     */
    public BitmapParams prepareBitmapParams(String path, ImageView imageView) {
        BitmapParams param = map.get(path);
        if (param == null) {
            param = new BitmapParams(path, imageView.getWidth(), imageView.getHeight());
            map.put(path, param);
        } else {
            if (param.height < imageView.getHeight()) {
                param.height = imageView.getHeight();
            }
            if (param.width < imageView.getWidth()) {
                param.width = imageView.getWidth();
            }
        }

        return param;
    }

    private void addBitmapToMemoryCache(String key, BitmapPack pack) {
        if (getBitmapFromMemCache(key) == null) {
            put(key, pack);
        }
    }

    private BitmapPack getBitmapFromMemCache(String key) {
        return get(key);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    protected int sizeOf(String key, BitmapPack pack) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            return pack.bitmap.getByteCount();
        } else {
            return pack.bitmap.getRowBytes() * pack.bitmap.getHeight();
        }
    }

    public class BitmapParams {
        protected int height;
        protected String path;
        protected int width;

        public BitmapParams(String path, int width, int height) {
            this.path = path;
            this.width = width;
            this.height = height;
        }
    }

    private class BitmapWorkerTask extends AsyncTask<BitmapParams, Void, BitmapPack> {
        // Use a WeakReference to ensure the ImageView can be garbage collected.
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected BitmapPack doInBackground(BitmapParams... params) {
            final BitmapParams param = params[0];
            final BitmapPack pack = decodeSampledBitmapFromFile(param.path, param.width, param.height);
            addBitmapToMemoryCache(param.path, pack);
            return pack;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(BitmapPack pack) {
            if (imageViewReference != null && pack != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(pack.bitmap);
                }
            }
        }
    }
}
