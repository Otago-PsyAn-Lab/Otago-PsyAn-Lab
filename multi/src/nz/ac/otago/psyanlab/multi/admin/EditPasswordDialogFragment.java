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
import nz.ac.otago.psyanlab.multi.R.string;
import nz.ac.otago.psyanlab.multi.dbmodel.AdminModel;
import nz.ac.otago.psyanlab.multi.dbmodel.UserModel;
import nz.ac.otago.psyanlab.multi.util.Args;
import nz.ac.otago.psyanlab.multi.util.DatabaseHelper;
import nz.ac.otago.psyanlab.multi.util.PWUtils;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EditPasswordDialogFragment extends DialogFragment {
    public static final int MODE_USER = 0x00;
    public static final int MODE_ADMIN = 0x01;

    protected UserModel user;
    protected Callbacks callbacks = new DummyCallbacks();
    protected int mode;

    public static interface Callbacks {
        public void onPasswordSet(UserModel user);
    }

    private View.OnClickListener onCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private TextView mCrouton;
    private TextView mPassword;
    private TextView mPassword2;

    private View.OnClickListener onDoneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCrouton.setVisibility(View.GONE);

            String password = mPassword.getText().toString();

            if (mode == MODE_ADMIN) {
                String password2 = mPassword2.getText().toString();
                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {
                    if (TextUtils.isEmpty(password)) {
                    }
                    if (TextUtils.isEmpty(password2)) {
                    }
                    onMissingPassword(mCrouton);
                    return;
                }
                if (!TextUtils.equals(password, password2)) {
                    onPasswordMismatch(mCrouton);
                    return;
                }

                SQLiteDatabase db = new DatabaseHelper(getActivity())
                        .getWritableDatabase();
                AdminModel.setPassword(db, password);

                db.close();
                dismiss();
                return;
            }

            user.salt = PWUtils.generateSalt();
            user.pwhash = PWUtils.generateHash(password, user.salt);
            if (TextUtils.isEmpty(password)) {
                onMissingPassword(mCrouton);
                return;
            }
            
            callbacks.onPasswordSet(user);
            dismiss();
        }

    };

    private void onPasswordMismatch(TextView crouton) {
        crouton.setVisibility(View.VISIBLE);
        crouton.setText(R.string.warning_password_mismatch);
    }

    private void onMissingPassword(TextView crouton) {
        crouton.setVisibility(View.VISIBLE);
        crouton.setText(R.string.warning_missing_password);
    }

    public static DialogFragment newInstance(int mode) {
        EditPasswordDialogFragment f = new EditPasswordDialogFragment();
        Bundle args = new Bundle();
        args.putInt(Args.MODE, mode);
        f.setArguments(args);
        return f;
    }

    /**
     * Create a new instance of dialog to edit user data.
     * 
     * @param user User to edit.
     * @return New dialog.
     */
    static EditPasswordDialogFragment newInstance(UserModel user) {
        EditPasswordDialogFragment fragment = new EditPasswordDialogFragment();

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
            if (args.containsKey(Args.MODE)) {
                mode = args.getInt(Args.MODE);
            } else {
                user = args.getParcelable(Args.USER);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callbacks) {
            callbacks = (Callbacks)activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_password_edit,
                container, false);
        mCrouton = (TextView)rootView.findViewById(R.id.crouton);
        mPassword = (TextView)rootView.findViewById(R.id.user_password);
        mPassword2 = (TextView)rootView.findViewById(R.id.user_password2);

        if (mode == MODE_ADMIN) {
            mPassword2.setVisibility(View.VISIBLE);
            getDialog().setTitle(R.string.title_admin_password);
        } else {
            getDialog().setTitle(R.string.title_set_password);
        }

        View posButton = rootView.findViewById(android.R.id.button1);
        View negButton = rootView.findViewById(android.R.id.button2);
        ((Button)posButton).setOnClickListener(onDoneListener);
        ((Button)negButton).setOnClickListener(onCancelListener);

        return rootView;
    }

    static class DummyCallbacks implements Callbacks {
        @Override
        public void onPasswordSet(UserModel user) {
        }
    }
}
