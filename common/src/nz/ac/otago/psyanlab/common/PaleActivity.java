/*
 Copyright (C) 2012, 2013 University of Otago, Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.common;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity;
import nz.ac.otago.psyanlab.common.util.Args;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

public class PaleActivity extends FragmentActivity implements PaleListFragment.Callbacks,
        PaleDetailFragment.Callbacks {
    private static final int REQUEST_IMPORT = 0x02;

    private static final int REQUEST_NEW = 0x03;

    private boolean mTwoPane;

    private PaleDetailContainerFragment mDetailContainerFragment;

    private PaleListFragment mPaleListFragment;

    private UserDelegateI mUserDelegate;

    private UserExperimentDelegateI mCurrentExperimentDelegate;

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        switch (requestCode) {
            case REQUEST_IMPORT:
                switch (resultCode) {
                    case RESULT_OK:
                        handleImportedIds(data.getLongArrayExtra(ImportPaleActivity.RETURN_IDS));
                        break;
                    default:
                        break;
                }
                break;
            case REQUEST_NEW:
                switch (resultCode) {
                    case RESULT_OK:
                        break;

                    default:
                        break;
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pale);

        if (getIntent().hasExtra(Args.USER_DELEGATE)) {
            mUserDelegate = getIntent().getParcelableExtra(Args.USER_DELEGATE);
            mUserDelegate.init(this);
        } else {
            throw new IllegalStateException("User delegate must be provided.");
        }

        ActionBar actionBar = getActionBar();
        String title = mUserDelegate.getUserName();
        if (TextUtils.isEmpty(title)) {
            title = "Otago PsyAn Lab";
        } else {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle(title);

        init(savedInstanceState);

        if (savedInstanceState != null) {
            updateExperimentDelegate(savedInstanceState
                    .<UserExperimentDelegateI> getParcelable(Args.USER_EXPERIMENT_DELEGATE));
        }
    }

    private void init(Bundle savedInstanceState) {
        FragmentManager fm = getSupportFragmentManager();
        mPaleListFragment = (PaleListFragment)fm.findFragmentById(R.id.user_list_fragment);
        mPaleListFragment.init(mUserDelegate);
        mPaleListFragment.setActivateOnItemClick(true);

        if (findViewById(R.id.user_detail_container) != null) {
            mTwoPane = true;
            mDetailContainerFragment = (PaleDetailContainerFragment)fm
                    .findFragmentById(R.id.user_detail_container);

            mPaleListFragment.setActivateOnItemClick(true);
            if (savedInstanceState == null) {
                mPaleListFragment.setActivatedPosition(0);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(Args.USER_EXPERIMENT_DELEGATE, mCurrentExperimentDelegate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_pales, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemSelected(final long experimentId) {
        UserExperimentDelegateI experimentDelegate = mUserDelegate
                .getUserExperimentDelegate(experimentId);

        if (mTwoPane) {
            updateExperimentDelegate(experimentDelegate);
        } else {
            Intent experimentDetails = new Intent(this, PaleDetailActivity.class).putExtra(
                    Args.USER_EXPERIMENT_DELEGATE, experimentDelegate);
            startActivity(experimentDetails);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_import) {
            doImportExperiment();
            return true;
        } else if (item.getItemId() == R.id.menu_new) {
            doNewExperiment();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateExperimentDelegate(UserExperimentDelegateI experimentDelegate) {
        mCurrentExperimentDelegate = experimentDelegate;
        if (mDetailContainerFragment != null) {
            mDetailContainerFragment.setExperimentDelegate(mCurrentExperimentDelegate);
        }
    }

    private void doImportExperiment() {
        Intent i = new Intent(this, ImportPaleActivity.class);
        i.putExtra(Args.USER_DELEGATE, mUserDelegate);
        startActivityForResult(i, REQUEST_IMPORT);
    }

    private void doNewExperiment() {
        Intent i = new Intent(this, ExperimentDesignerActivity.class);
        i.putExtra(Args.USER_DELEGATE, mUserDelegate);
        startActivityForResult(i, REQUEST_NEW);
    }

    private void handleImportedIds(final long[] ids) {
        if (ids.length == 1) {
            new Handler().post(new Runnable() {
                // Use a handler to post a message back to self
                // after we have been recreated.
                @Override
                public void run() {
                    mPaleListFragment.onExperimentInsert(ids[0]);
                    onItemSelected(ids[0]);
                }
            });
        }
    }

    @Override
    public void onExperimentDeleted() {
        if (mTwoPane) {
            updateExperimentDelegate(null);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mPaleListFragment.onExperimentDelete();
                }
            });
        }
    }
}
