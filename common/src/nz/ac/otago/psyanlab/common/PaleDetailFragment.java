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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;

public class PaleDetailFragment extends Fragment {
    public static final String TAG = "pale_detail_fragment";

    private Callbacks mCallbacks;

    private TextView mDescription;

    private TextView mCreated;

    private TextView mName;

    private View mRootView;

    private TextView mAuthors;

    private UserExperimentDelegateI mDelegate;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_pale_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_pale_detail, container, false);
        mName = (TextView)mRootView.findViewById(R.id.name);
        mDescription = (TextView)mRootView.findViewById(R.id.description);
        mCreated = (TextView)mRootView.findViewById(R.id.created);
        mAuthors = (TextView)mRootView.findViewById(R.id.authors);

        updateDisplay();

        return mRootView;
    }

    public void setExperimentDelegate(UserExperimentDelegateI userExperimentDelegate) {
        mDelegate = userExperimentDelegate;
        if (mDelegate != null) {
            mDelegate.init(getActivity());
        }
        updateDisplay();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_run) {
            doRunExperiment();
            return true;
        } else if (itemId == R.id.menu_delete) {
            doDeleteExperiment();
            return true;
        } else if (itemId == R.id.menu_edit) {
            doEditExperiment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDisplay() {
        if (mDelegate == null) {
            setHasOptionsMenu(false);

            mName.setVisibility(View.GONE);
            mDescription.setVisibility(View.GONE);
            mAuthors.setVisibility(View.GONE);
            mCreated.setVisibility(View.GONE);
        } else {
            setHasOptionsMenu(true);

            mName.setVisibility(View.VISIBLE);
            mDescription.setVisibility(View.VISIBLE);
            mAuthors.setVisibility(View.VISIBLE);
            mCreated.setVisibility(View.VISIBLE);

            PaleRow experimentDetails = mDelegate.getExperimentDetails();

            mName.setText(experimentDetails.name);
            mDescription.setText(experimentDetails.description);
            mAuthors.setText(experimentDetails.authors);

            if (experimentDetails.lastRun > experimentDetails.dateCreated) {
                mCreated.setText(DateUtils.formatDateTime(getActivity(),
                        experimentDetails.dateCreated, 0));
            } else {
                mCreated.setText("Never");
            }
        }
    }

    private void doDeleteExperiment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Experiment and all associated data will be deleted permanently.")
                .setTitle("Delete?").setCancelable(true)
                .setNeutralButton("Export Data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            mDelegate.deleteExperiment();
                        } catch (IOException e) {
                            // FIXME: Handle error deleting experiment.
                            e.printStackTrace();
                        }
                        mCallbacks.onExperimentDeleted();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void doEditExperiment() {
        Intent i = new Intent(getActivity(), ExperimentDesignerActivity.class).putExtra(
                Args.USER_EXPERIMENT_DELEGATE, mDelegate);
        startActivity(i);
    }

    private void doRunExperiment() {
        // Intent i = new Intent(getActivity(), ExperimentRuntimeActivity.class)
        // .putExtra(Args.USER_EXPERIMENT_DELEGATE, mDelegate);
        // startActivity(i);
    }

    public interface Callbacks {
        void onExperimentDeleted();
    }
}
