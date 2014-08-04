
package nz.ac.otago.psyanlab.common;

import com.tonicartos.component.FilePickerFragment;
import com.tonicartos.component.FilePickerFragment.Callbacks;
import com.tonicartos.component.FilePickerFragment.HeaderMapper;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import nz.ac.otago.psyanlab.common.util.Args;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Import a PALE.
 */
public class ImportPaleActivity extends FragmentActivity implements Callbacks {
    public static final String RETURN_IDS = "return_ids";

    private FilePickerFragment mFilePickerFragment;

    private Toast mToast;

    private UserDelegateI mUserDelegate;

    @Override
    public FilenameFilter getfilter() {
        return new FilenameFilter() {
            private Pattern mFileNamePattern = Pattern.compile(".*\\.pale",
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
                return mFileNamePattern.matcher(filename).matches();
            }
        };
    }

    @Override
    public HeaderMapper getHeaderMapper() {
        return new HeaderMapper() {
            @Override
            public String getHeaderFor(String mimeType, File file) {
                return getResources().getString(R.string.file_header_experiments);
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
        new AsyncTask<File, Void, Pair<ArrayList<String>, ArrayList<Long>>>() {
            @Override
            protected Pair<ArrayList<String>, ArrayList<Long>> doInBackground(File... files) {
                ArrayList<Long> ids = new ArrayList<Long>();
                ArrayList<String> errored = new ArrayList<String>();
                for (int i = 0; i < files.length; i++) {
                    Uri uri;
                    try {
                        uri = mUserDelegate.addExperiment(files[i].getPath());
                    } catch (JSONException e) {
                        errored.add(files[i].getName());
                        e.printStackTrace();
                        continue;
                    } catch (IOException e) {
                        errored.add(files[i].getName());
                        e.printStackTrace();
                        continue;
                    }
                    ids.add(Long.parseLong(uri.getLastPathSegment()));
                }
                return new Pair<ArrayList<String>, ArrayList<Long>>(errored, ids);
            }

            @Override
            protected void onPostExecute(Pair<ArrayList<String>, ArrayList<Long>> result) {
                ArrayList<String> errored = result.first;
                ArrayList<Long> ids = result.second;
                if (errored.size() > 0) {
                    toast(getResources().getString(R.string.format_error_importing,
                            TextUtils.join(", ", errored)));
                }

                if (ids.size() > 0) {
                    Intent r = new Intent();
                    long[] lids = new long[ids.size()];
                    for (int i = 0; i < ids.size(); i++) {
                        lids[i] = ids.get(i);
                    }
                    r.putExtra(RETURN_IDS, lids);
                    setResult(RESULT_OK, r);
                    finish();
                }
            };
        }.execute(files);
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

    private void toast(String string) {
        if (mToast == null) {
            mToast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(string);
        }
        mToast.show();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserDelegate = getIntent().getParcelableExtra(Args.USER_DELEGATE);
        mUserDelegate.init(this);

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
        abs.setTitle(R.string.title_import);
        // startActionMode(this);
    }
}
