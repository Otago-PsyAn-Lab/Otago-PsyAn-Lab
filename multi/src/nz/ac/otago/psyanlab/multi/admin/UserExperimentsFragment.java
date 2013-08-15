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

package nz.ac.otago.psyanlab.multi.admin;


import nz.ac.otago.psyanlab.multi.R;
import nz.ac.otago.psyanlab.multi.R.id;
import nz.ac.otago.psyanlab.multi.R.layout;
import nz.ac.otago.psyanlab.multi.R.menu;
import nz.ac.otago.psyanlab.multi.dbmodel.UserModel;
import nz.ac.otago.psyanlab.multi.util.FileUtils;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

public class UserExperimentsFragment extends ListFragment {
    public static final String TAG = UserExperimentsFragment.class
            .getSimpleName();

    protected static final int LOADER_USER_EXPERIMENTS = 0x02;

    private Callbacks mCallbacks;

    protected ViewBinder experimentViewBinder = new ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            switch (view.getId()) {
                case R.id.experiment_version:
                    ((TextView)view).setText("v"
                            + cursor.getString(columnIndex));
                    break;
                case R.id.experiment_file_size:
                    long fileSize = cursor.getLong(columnIndex);
                    ((TextView)view).setText(FileUtils.formatBytes(fileSize));
                    break;
                case R.id.experiment_last_run:
                    ((TextView)view).setText(DateUtils.formatDateTime(
                            getActivity(), cursor.getLong(columnIndex), 0));
                    break;

                default:
                    return false;
            }
            return true;
        }
    };
    protected SimpleCursorAdapter mAdapter;
    protected UserModel mUser;

    public UserExperimentsFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void resetAdapter() {
        setListAdapter(mAdapter);
    }

    public void setUser(UserModel user) {
        mUser = user;
        mAdapter = mCallbacks.getExperimentsAdapter(user.id,
                R.layout.list_user_experiment, new String[] {
                        "name", "description", "version", "file_size",
                        "last_run"
                }, new int[] {
                        R.id.experiment_name, R.id.experiment_description,
                        R.id.experiment_version, R.id.experiment_file_size,
                        R.id.experiment_last_run
                });
        mAdapter.setViewBinder(experimentViewBinder);
        setListAdapter(mAdapter);
    }

    public static interface Callbacks {
        SimpleCursorAdapter getExperimentsAdapter(long userId, int layout,
                String[] fields, int[] ids);
    }
}
