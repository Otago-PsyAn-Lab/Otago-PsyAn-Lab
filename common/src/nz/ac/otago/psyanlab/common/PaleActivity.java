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
import nz.ac.otago.psyanlab.common.model.Experiment;
import nz.ac.otago.psyanlab.common.util.Args;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

public class PaleActivity extends FragmentActivity implements PaleListFragment.Callbacks,
        PaleDetailFragment.Callbacks {
    private static final int REQUEST_IMPORT = 0x02;

    private static final int REQUEST_NEW = 0x03;

    private UserExperimentDelegateI mCurrentExperimentDelegate;

    private UserDelegateI mUserDelegate;

    protected PaleDetailFragment mPaleDetailFragment;

    protected PaleListFragment mPaleListFragment;

    protected SlidingPaneLayout mSlidingContainer;

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
                    case RESULT_OK: {
                        // Nothing let the list fragment know that we have a new
                        // experiment and to choose it for the user.
                        long experimentId = data.getLongExtra(Args.EXPERIMENT_ID, -1);
                        mPaleListFragment.onExperimentInsert(experimentId);
                        onItemSelected(experimentId);
                        break;
                    }
                    default: {
                        // We have to delete the experiment because it was
                        // discarded by the user.
                        long experimentId = data.getLongExtra(Args.EXPERIMENT_ID, -1);
                        if (experimentId != -1) {
                            try {
                                mUserDelegate.getUserExperimentDelegate(experimentId)
                                        .deleteExperiment();
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        } else {
                            throw new RuntimeException("Invalid experiment id.");
                        }
                        break;
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!mSlidingContainer.isOpen()) {
            mSlidingContainer.openPane();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pale);

        // Get and initialise user delegate.
        if (getIntent().hasExtra(Args.USER_DELEGATE)) {
            mUserDelegate = getIntent().getParcelableExtra(Args.USER_DELEGATE);
            mUserDelegate.init(this);
        } else {
            throw new IllegalStateException("User delegate must be provided.");
        }

        // Initialise action bar.
        ActionBar actionBar = getActionBar();
        String title = mUserDelegate.getUserName();
        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.app_name);
            actionBar.setDisplayHomeAsUpEnabled(false);
        } else {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle(title);

        // Initialise fragments.
        FragmentManager fm = getSupportFragmentManager();

        // List fragment.
        mPaleListFragment = (PaleListFragment)fm.findFragmentById(R.id.pale_list_fragment);
        mPaleListFragment.setUserDelegate(mUserDelegate);
        mPaleListFragment.setActivateOnItemClick(true);
        // if (savedInstanceState == null) {
        // mPaleListFragment.setActivatedPosition(0);
        // }

        // Detail fragment.
        mPaleDetailFragment = (PaleDetailFragment)fm.findFragmentById(R.id.pale_detail_fragment);
        if (savedInstanceState != null) {
            updateExperimentDelegate(savedInstanceState
                    .<UserExperimentDelegateI> getParcelable(Args.USER_EXPERIMENT_DELEGATE));
        }

        // Initialise sliding container.
        mSlidingContainer = (SlidingPaneLayout)findViewById(R.id.sliding_container);
        mSlidingContainer.setParallaxDistance((int)getResources().getDimension(
                R.dimen.sliding_container_parallax));
        mSlidingContainer.setShadowResource(R.drawable.opal_list_background);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_pales, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onExperimentDeleted() {
        mCurrentExperimentDelegate = null;
    }

    @Override
    public void onItemSelected(final long experimentId) {
        UserExperimentDelegateI experimentDelegate = mUserDelegate
                .getUserExperimentDelegate(experimentId);

        updateExperimentDelegate(experimentDelegate);

        mSlidingContainer.closePane();
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

    /**
     * Perform import action. Starts the import activity for the user.
     */
    private void doImportExperiment() {
        Intent i = new Intent(this, ImportPaleActivity.class);
        i.putExtra(Args.USER_DELEGATE, mUserDelegate);
        startActivityForResult(i, REQUEST_IMPORT);
    }

    /**
     * Perform new experiment action. Creates a new experiment and sends an
     * intent to edit it. If the user discards the new experiment without
     * 'saving' it, we'll have to delete the new experiment when the edit
     * activity returns.
     */
    private void doNewExperiment() {
        Experiment experiment = new Experiment();
        experiment.authors = mUserDelegate.getUserName();
        experiment.dateCreated = System.currentTimeMillis();
        experiment.lastModified = System.currentTimeMillis();
        experiment.name = getString(R.string.default_name_new_experiment);
        long experimentId;
        try {
            Uri uri = mUserDelegate.addExperiment(experiment);
            experimentId = Long.parseLong(uri.getLastPathSegment());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Intent i = new Intent(this, ExperimentDesignerActivity.class);
        i.putExtra(Args.USER_EXPERIMENT_DELEGATE,
                mUserDelegate.getUserExperimentDelegate(experimentId));
        startActivityForResult(i, REQUEST_NEW);
    }

    /**
     * Handle IDs for experiments the user just chose to import.
     * 
     * @param ids IDs of new experiments.
     */
    private void handleImportedIds(final long[] ids) {
        if (ids.length == 1) {
            // Use a handler to post a message back to self after we have been
            // recreated.
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mPaleListFragment.onExperimentInsert(ids[0]);
                    onItemSelected(ids[0]);
                }
            });
        }
    }

    private void updateExperimentDelegate(UserExperimentDelegateI experimentDelegate) {
        mCurrentExperimentDelegate = experimentDelegate;
        mPaleDetailFragment.setExperimentDelegate(experimentDelegate);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(Args.USER_EXPERIMENT_DELEGATE, mCurrentExperimentDelegate);
    }
}
