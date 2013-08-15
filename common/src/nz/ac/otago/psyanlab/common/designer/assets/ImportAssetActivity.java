
package nz.ac.otago.psyanlab.common.designer.assets;

import com.tonicartos.component.FilePickerFragment;
import com.tonicartos.component.FilePickerFragment.Callbacks;
import com.tonicartos.component.FilePickerFragment.HeaderMapper;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.util.Args;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A dialogue themed activity that uses the a FilePicker fragment to choose
 * files to import as assets.
 */
public class ImportAssetActivity extends FragmentActivity implements Callbacks {
    private static final String FILE_ENDINGS_CSV_DATA = ".*\\.csv";

    private static final String FILE_ENDINGS_IMAGES = ".*\\.jpg|.*\\.jpeg|.*\\.png|.*\\.bmp|.*\\.webp";

    @SuppressWarnings("unused")
    private static final String FILE_ENDINGS_SOUNDS = ".*\\.m4a|.*\\.aac|.*\\.flac|.*\\.mp3|.*\\.ogg|.*\\.ogm|.*\\.wav";

    @SuppressWarnings("unused")
    private static final String FILE_ENDINGS_VIDEOS = ".*\\.mp4|.*\\.webm|.*\\.mkv";

    private FilePickerFragment mFilePickerFragment;

    @Override
    public FilenameFilter getfilter() {
        return new FilenameFilter() {
            private Pattern mAllowedFilesPattern = Pattern.compile(FILE_ENDINGS_CSV_DATA + "|"
                    + FILE_ENDINGS_IMAGES /*
                                           * + "|" + FILE_ENDINGS_SOUNDS + "|" +
                                           * FILE_ENDINGS_VIDEOS
                                           */, Pattern.CASE_INSENSITIVE);

            @Override
            public boolean accept(File dir, String filename) {
                File f = new File(dir, filename);
                if (f.isHidden()) {
                    return false;
                }
                if (f.isDirectory()) {
                    return true;
                }
                return mAllowedFilesPattern.matcher(filename).matches();
            }
        };
    }

    @Override
    public HeaderMapper getHeaderMapper() {
        return new HeaderMapper() {
            private Pattern mAudioPattern = Pattern.compile("audio/.*|application/ogg");

            private Pattern mDocumentPattern = Pattern.compile("text/.*|application/.*");

            private Pattern mImagePattern = Pattern.compile("image/.*");

            private Pattern mVideoPattern = Pattern.compile("video/.*");

            @Override
            public String getHeaderFor(String mimeType, File file) {
                Matcher matcher = mAudioPattern.matcher(mimeType);
                if (matcher.matches()) {
                    return getResources().getString(R.string.header_sounds);
                }
                matcher = mDocumentPattern.matcher(mimeType);
                if (matcher.matches() || mimeType.endsWith("Unknown")) {
                    return getResources().getString(R.string.header_csv_data);
                }
                matcher = mVideoPattern.matcher(mimeType);
                if (matcher.matches()) {
                    return getResources().getString(R.string.header_videos);
                }
                matcher = mImagePattern.matcher(mimeType);
                if (matcher.matches()) {
                    return getResources().getString(R.string.header_images);
                }
                return mimeType;
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (!mFilePickerFragment.goBack()) {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.just_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onFilesPicked(File... files) {
        Intent data = new Intent();
        String[] paths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            paths[i] = files[i].getPath();
        }
        data.putExtra(Args.ASSET_PATHS, paths);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_cancel || itemId == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_import);

        Resources resources = getResources();
        mFilePickerFragment = (FilePickerFragment)getSupportFragmentManager().findFragmentById(
                R.id.file_picker);
        if (savedInstanceState == null) {
            mFilePickerFragment.setRootDir(Environment.getExternalStorageDirectory());
            mFilePickerFragment.setColumnWidth(resources
                    .getDimensionPixelSize(R.dimen.grid_column_width));
            mFilePickerFragment.setNumColumns(StickyGridHeadersGridView.AUTO_FIT);
            mFilePickerFragment.setMultiSelectEnabled(true);
        }
        ActionBar abs = getActionBar();
        abs.setDisplayUseLogoEnabled(false);
        abs.setDisplayShowHomeEnabled(false);
        abs.setTitle(R.string.title_import_asset);
    }
}
