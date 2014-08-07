/*
 Copyright (C) 2014 Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.common.designer.source;

import com.tonicartos.component.FilePickerFragment;
import com.tonicartos.component.FilePickerFragment.Callbacks;
import com.tonicartos.component.FilePickerFragment.HeaderMapper;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Source;
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
public class ImportSourceActivity extends FragmentActivity implements Callbacks {
    private FilePickerFragment mFilePickerFragment;

    @Override
    public FilenameFilter getfilter() {
        return new FilenameFilter() {
            private Pattern mAllowedFilesPattern = Pattern.compile(Source.FILE_ENDINGS,
                    Pattern.CASE_INSENSITIVE);

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
            private Pattern mDocumentPattern = Pattern.compile("text/.*|application/.*");

            @Override
            public String getHeaderFor(String mimeType, File file) {
                Matcher matcher = mDocumentPattern.matcher(mimeType);
                if (matcher.matches() || mimeType.endsWith("Unknown")) {
                    return getResources().getString(R.string.header_csv_data);
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
        data.putExtra(Args.PICKED_PATHS, paths);
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
