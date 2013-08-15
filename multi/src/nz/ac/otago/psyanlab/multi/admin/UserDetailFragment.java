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


import nz.ac.otago.psyanlab.common.util.FileUtils;
import nz.ac.otago.psyanlab.multi.R;
import nz.ac.otago.psyanlab.multi.R.id;
import nz.ac.otago.psyanlab.multi.R.layout;
import nz.ac.otago.psyanlab.multi.R.menu;
import nz.ac.otago.psyanlab.multi.R.string;
import nz.ac.otago.psyanlab.multi.dbmodel.UserModel;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class UserDetailFragment extends Fragment {
    public static final String TAG = UserDetailFragment.class.getSimpleName();

    public static interface Callbacks {
        public void onChangePassword(UserModel user);
    }

    private TextView mCreated;
    private TextView mEmail;
    private TextView mFullName;
    private TextView mLastActive;
    private TextView mPhone;
    private TextView mTotalExperiments;
    private TextView mTotalExperimentsSize;
    private TextView mTotalRecords;
    private TextView mTotalRecordsSize;
    protected SimpleCursorAdapter experimentsAdapter;
    protected UserModel mUser;
    private Button mPasswordMask;
    private Callbacks mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_user_detail, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_detail, container,
                false);
        mFullName = (TextView)v.findViewById(R.id.full_name);
        mEmail = (TextView)v.findViewById(R.id.email);
        mPhone = (TextView)v.findViewById(R.id.phone);
        mCreated = (TextView)v.findViewById(R.id.created);
        mLastActive = (TextView)v.findViewById(R.id.last_active);
        mTotalExperiments = (TextView)v.findViewById(R.id.total_experiments);
        mTotalExperimentsSize = (TextView)v
                .findViewById(R.id.total_experiments_size);
        mTotalRecords = (TextView)v.findViewById(R.id.total_records);
        mTotalRecordsSize = (TextView)v.findViewById(R.id.total_records_size);

        mPasswordMask = (Button)v.findViewById(R.id.password_mask);
        mPasswordMask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onChangePassword(mUser);
            }
        });
        return v;
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
    public void onDestroy() {
        super.onDestroy();
    }

    public void hide() {
        getView().findViewById(R.id.details).setVisibility(View.GONE);
    }

    public void setUser(UserModel user) {
        mUser = user;
        mFullName.setText(mUser.name);
        if (TextUtils.isEmpty(mUser.email) && TextUtils.isEmpty(mUser.phone)) {
            getView().findViewById(R.id.contact_section_title).setVisibility(
                    View.GONE);
            getView().findViewById(R.id.contact_section_underline)
                    .setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.contact_section_title).setVisibility(
                    View.VISIBLE);
            getView().findViewById(R.id.contact_section_underline)
                    .setVisibility(View.VISIBLE);

        }
        if (TextUtils.isEmpty(mUser.phone)) {
            mPhone.setVisibility(View.GONE);
        } else {
            mPhone.setText(mUser.phone);
            mPhone.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(mUser.email)) {
            mEmail.setVisibility(View.GONE);
        } else {
            mEmail.setText(mUser.email);
            mEmail.setVisibility(View.VISIBLE);
        }

        if (mUser.lastLogIn <= 0) {
            // Of course people can't log in before OPAL was ever made ^_^.
            mLastActive.setText(R.string.text_date_never);
        } else {
            mLastActive.setText(DateFormat.getLongDateFormat(getActivity())
                    .format(new Date(mUser.lastLogIn)));
        }
        mCreated.setText(DateFormat.getLongDateFormat(getActivity()).format(
                new Date(mUser.dateCreated)));
        mTotalExperiments.setText(String.valueOf(mUser.totalExperiments));
        mTotalExperimentsSize.setText(FileUtils
                .formatBytes(mUser.totalExperimentsSize));
        mTotalRecords.setText(String.valueOf(mUser.totalRecords));
        mTotalRecordsSize
                .setText(FileUtils.formatBytes(mUser.totalRecordsSize));

        getView().findViewById(R.id.details).setVisibility(View.VISIBLE);
    }
}
