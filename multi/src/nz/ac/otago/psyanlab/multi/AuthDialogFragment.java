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

package nz.ac.otago.psyanlab.multi;

import nz.ac.otago.psyanlab.multi.R;
import nz.ac.otago.psyanlab.multi.util.AdminVerificationTask;
import nz.ac.otago.psyanlab.multi.util.DatabaseHelper;
import nz.ac.otago.psyanlab.multi.util.UserVerificationTask;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Fragment that implements getting authorisation from the device administrator
 * by way of password verification. Can be used as a password entry box inside
 * an activity from xml or in code. Can also be used as a dialog by using
 * newDialog().
 */
public class AuthDialogFragment extends DialogFragment implements
        UserVerificationTask.AuthListener {
    private static final int ID_ADMIN = -1;
    private AuthListener authListener;
    private boolean asDialog = false;
    private long userId;
    private String title;
    private TextView mCrouton;
    private TextView mPassword;

    /**
     * Create a new instance of AuthFragment in its dialog form for the express
     * purpose of authenticating the administrator.
     * 
     * @param title Dialog window title.
     * @param al Listener for dialog authentication call-backs.
     * @return Dialog for authenticating the administrator.
     */
    public static AuthDialogFragment newDialog(String title, AuthListener al) {
        return newDialog(title, ID_ADMIN, al);
    }

    /**
     * Create a new instance of AuthFragment in its dialog form.
     * 
     * @param title Title text for dialog window.
     * @param userId Id of user to authenticate. If userId is -1 then
     *            authorisation is for the administrator.
     * @param al Listener for dialog authentication call-backs.
     * @return Dialog for authenticating user with given id.
     */
    public static AuthDialogFragment newDialog(String title, long userId,
            AuthListener al) {
        AuthDialogFragment f = new AuthDialogFragment();
        f.title = title;
        f.authListener = al;
        f.asDialog = true;
        f.userId = userId;
        return f;
    }

    /**
     * Initiate authorisation attempt with entered text in box.
     */
    public void doAuthorisation(long userId) {
        String pw = ((EditText)getView().findViewById(R.id.password)).getText()
                .toString();

        // Verify password in background thread. Result comes via callback
        // through the AuthListener interface.
        DatabaseHelper db = new DatabaseHelper(getActivity());
        if (userId == ID_ADMIN) {
            new AdminVerificationTask(db.getReadableDatabase(), this)
                    .execute(pw);
        } else {
            new UserVerificationTask(db.getReadableDatabase(), userId, this)
                    .execute(pw);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v;
        if (asDialog) {
            v = dialogView(inflater);
            InputMethodManager imm = (InputMethodManager)getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            getDialog().setTitle(title);

            // getDialog().setCanceledOnTouchOutside(false);
        } else {
            v = inflater.inflate(R.layout.auth_box, null);
            mPassword = (TextView)v.findViewById(R.id.password);
            mPassword.setText(null);
            mCrouton = (TextView)v.findViewById(R.id.crouton);
        }
        return v;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        super.onCancel(dialog);
    }

    /**
     * Initialise the view for the dialog form of the fragment.
     * 
     * @param inflater Layout inflater.
     * @return View intended for dialog use.
     */
    private View dialogView(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.auth_dialog, null);
        mPassword = (TextView)v.findViewById(R.id.password);
        mCrouton = (TextView)v.findViewById(R.id.crouton);
        mPassword.setText(null);
        mPassword
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId,
                            KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_GO
                                || actionId == EditorInfo.IME_ACTION_DONE) {
                            doAuthoriseClick();
                            return true;
                        }
                        return false;
                    }
                });
        View posButton = v.findViewById(android.R.id.button1);
        View negButton = v.findViewById(android.R.id.button2);
        ((Button)posButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAuthoriseClick();
            }
        });
        ((Button)negButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCancelClick();
            }
        });
        return v;
    }

    /**
     * Handle cancel click.
     */
    protected void doCancelClick() {
        authListener.onAuthCancel();
        dismiss();
    }

    /**
     * Handle authorise click.
     */
    protected void doAuthoriseClick() {
        doAuthorisation(userId);
    }

    @Override
    public void onAuthTaskComplete(boolean authSuccess, Bundle args) {
        if (authSuccess) {
            authListener.onAuthSuccess();
            dismiss();
        } else {
            mCrouton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void dismiss() {
        InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        super.dismiss();
    }

    /**
     * Callback interface for authorisation result.
     */
    public interface AuthListener {
        /**
         * Called on authorisation success.
         */
        public void onAuthSuccess();

        /**
         * Called when authorisation cancelled by user.
         */
        public void onAuthCancel();
    }
}
