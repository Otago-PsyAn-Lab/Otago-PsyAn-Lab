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
import nz.ac.otago.psyanlab.multi.dbmodel.UserModel;
import nz.ac.otago.psyanlab.multi.util.Args;
import nz.ac.otago.psyanlab.multi.util.PWUtils;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EditUserDialogFragment extends DialogFragment {
    protected UserModel mUser;

    private View.OnClickListener onCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private TextView mCrouton;
    private TextView mPassword;
    private Button mLinkContactButton;

    private View.OnClickListener onDoneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCrouton.setVisibility(View.GONE);

            String password = mPassword.getText().toString();
            if (!TextUtils.isEmpty(password)) {
                mUser.salt = PWUtils.generateSalt();
                mUser.pwhash = PWUtils.generateHash(password, mUser.salt);
                dismiss();
                return;
            }

            dismiss();
        }

    };

    static EditUserDialogFragment newInstance(UserModel user) {
        EditUserDialogFragment fragment = new EditUserDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable(Args.USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mUser = args.getParcelable(Args.USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_edit,
                container, false);
        mCrouton = (TextView)rootView.findViewById(R.id.crouton);
        mPassword = (TextView)rootView.findViewById(R.id.user_password);
        mLinkContactButton = (Button)rootView.findViewById(R.id.linked_contact);

        getDialog().setTitle(mUser.name);

        Button posButton = (Button)rootView.findViewById(android.R.id.button1);
        Button negButton = (Button)rootView.findViewById(android.R.id.button2);
        posButton.setOnClickListener(onDoneListener);
        negButton.setOnClickListener(onCancelListener);

        return rootView;
    }
}
