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
import nz.ac.otago.psyanlab.multi.UserListFragment;
import nz.ac.otago.psyanlab.multi.R.id;
import nz.ac.otago.psyanlab.multi.R.layout;
import nz.ac.otago.psyanlab.multi.R.menu;
import nz.ac.otago.psyanlab.multi.R.string;
import nz.ac.otago.psyanlab.multi.UserListFragment.Callbacks;
import nz.ac.otago.psyanlab.multi.dbmodel.ExperimentModel;
import nz.ac.otago.psyanlab.multi.dbmodel.UserModel;
import nz.ac.otago.psyanlab.multi.util.Args;
import nz.ac.otago.psyanlab.multi.util.DataProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;

public class AdminActivity extends FragmentActivity implements
        UserListFragment.Callbacks, LoaderCallbacks<Cursor>,
        EditPasswordDialogFragment.Callbacks, UserDetailFragment.Callbacks,
        UserExperimentsFragment.Callbacks {
    private static final int LOADER_USER = 0x01;
    private static final int LOADER_USER_EXPERIMENTS = 0x02;
    private static final int LOADER_USERS = 0x03;

    private static final int PICK_CONTACT = 0x01;
    private static final String KEY_USER_ID = "user_id";

    private boolean mTwoPane;

    private SimpleCursorAdapter mExperimentsAdapter;
    private SimpleCursorAdapter mUsersAdapter;
    private UserDetailContainerFragment mDetailContainerFragment;
    private UserListFragment mUserListFragment;

    @Override
    public SimpleCursorAdapter getExperimentsAdapter(long userId, int layout,
            String[] fields, int[] ids) {
        mExperimentsAdapter = new SimpleCursorAdapter(this, layout, null,
                fields, ids, 0);
        Bundle args = new Bundle();
        args.putLong(KEY_USER_ID, userId);
        getSupportLoaderManager().restartLoader(LOADER_USER_EXPERIMENTS, args,
                this);
        return mExperimentsAdapter;
    }

    @Override
    public SimpleCursorAdapter getUsersAdapter(int layout, String[] fields,
            int[] ids) {
        mUsersAdapter = new SimpleCursorAdapter(this, layout, null, fields,
                ids, 0);

        getSupportLoaderManager().initLoader(LOADER_USERS, null, this);
        return mUsersAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        init(savedInstanceState);

    }

    private void init(Bundle savedInstanceState) {
        FragmentManager fm = getSupportFragmentManager();
        mUserListFragment = (UserListFragment)fm
                .findFragmentById(R.id.user_list_fragment);

        if (findViewById(R.id.user_detail_container) != null) {
            mTwoPane = true;
            mDetailContainerFragment = (UserDetailContainerFragment)fm
                    .findFragmentById(R.id.user_detail_container);

            mUserListFragment.setActivateOnItemClick(true);
            if (savedInstanceState == null) {
                mUserListFragment.setActivatedPosition(0);
            }
        }
    }

    private static String sExperimentSelection = ExperimentModel
            .namespaced(ExperimentModel.KEY_USER_ID) + "=?";

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_USER:
                return new CursorLoader(getApplicationContext(),
                        Uri.withAppendedPath(DataProvider.URI_USERS,
                                args.getString(Args.USER_ID)), null, null,
                        null, UserModel.KEY_NAME + " ASC");

            case LOADER_USER_EXPERIMENTS:
                String userId = String.valueOf(args.getLong(Args.USER_ID));
                String[] selectionArgs = new String[] {
                    userId
                };
                return new CursorLoader(getApplicationContext(),
                        DataProvider.URI_EXPERIMENTS, null,
                        sExperimentSelection, selectionArgs, null);

            case LOADER_USERS:
                return new CursorLoader(getApplicationContext(),
                        DataProvider.URI_USERS, null, null, null,
                        UserModel.KEY_NAME + " ASC");
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_list, menu);
        return true;
    }

    public void onDeleteUser(final UserModel user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.text_delete_user_warning)
                .setTitle("Delete?")
                .setCancelable(true)
                .setNeutralButton("Export Data",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method stub
                            }
                        })
                .setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Cursor oldCursor = null;
        switch (loader.getId()) {
            case LOADER_USER:
                return;

            case LOADER_USER_EXPERIMENTS:
                oldCursor = mExperimentsAdapter.swapCursor(null);
                break;

            case LOADER_USERS:
                oldCursor = mUsersAdapter.swapCursor(null);
                break;
        }
        if (oldCursor != null) {
            oldCursor.close();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        Cursor oldCursor = null;
        switch (loader.getId()) {
            case LOADER_USER:
                return;

            case LOADER_USER_EXPERIMENTS:
                oldCursor = mExperimentsAdapter.swapCursor(newCursor);
                break;
            case LOADER_USERS:
                oldCursor = mUsersAdapter.swapCursor(newCursor);
                break;

        }
        if (oldCursor != null) {
            oldCursor.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction fragTransaction;
        Fragment prevDialog;
        DialogFragment newDialog;

        switch (item.getItemId()) {
            case R.id.menu_admin_password:
                // Clear any existing dialog.
                fragTransaction = getSupportFragmentManager()
                        .beginTransaction();
                prevDialog = getSupportFragmentManager().findFragmentByTag(
                        "dialog");
                if (prevDialog != null) {
                    fragTransaction.remove(prevDialog);
                }
                fragTransaction.addToBackStack(null);

                // Create and show authentication dialog.
                newDialog = EditPasswordDialogFragment
                        .newInstance(EditPasswordDialogFragment.MODE_ADMIN);
                newDialog.show(fragTransaction, "dialog");
                break;

            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_new_user:
                Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                intent.putExtra("finishActivityOnSaveCompleted", true);
                startActivityForResult(intent, PICK_CONTACT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserSelected(UserModel user) {
        if (mTwoPane) {
            // Bundle arguments = new Bundle();
            // arguments.putLong(Args.ITEM_ID, id);
            // UserDetailFragment fragment = new UserDetailFragment();
            // fragment.setArguments(arguments);
            // getSupportFragmentManager().beginTransaction()
            // .replace(R.id.user_detail_container, fragment).commit();
            mDetailContainerFragment.setUser(user);

        } else {
            Intent detailIntent = new Intent(this, AdminDetailActivity.class);
            detailIntent.putExtra(Args.ITEM_ID, user.id);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_CONTACT:
                handleContactPicked(data);
                break;
        }
    }

    private void handleContactPicked(Intent data) {
        Uri pickedUri = data.getData();

        Cursor rawContact = getContentResolver().query(pickedUri, null, null,
                null, null);
        rawContact.moveToFirst();
        DatabaseUtils.dumpCursor(rawContact);

        UserModel user = new UserModel();

        Time now = new Time();
        now.setToNow();
        user.dateCreated = now.toMillis(false);
        user.name = rawContact.getString(rawContact
                .getColumnIndex(Contacts.DISPLAY_NAME));
        user.lookupKey = rawContact.getString(rawContact
                .getColumnIndex(Contacts.LOOKUP_KEY));

        // Store new user
        ContentValues values = new ContentValues();
        values.put(UserModel.KEY_NAME, user.name);
        values.put(UserModel.KEY_LOOKUP_KEY, user.lookupKey);
        values.put(UserModel.KEY_DATE_CREATED, user.dateCreated);

        Uri userUri = getContentResolver().insert(DataProvider.URI_USERS,
                values);

        if (mTwoPane) {
            mDetailContainerFragment.setUser(user);
        } else {
            Intent intent = new Intent(this, AdminDetailActivity.class);
            intent.putExtra(Args.USER_URI, userUri);
            startActivity(intent);
        }
    }

    @Override
    public void onChangePassword(UserModel user) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment editDialog = EditPasswordDialogFragment
                .newInstance(user);
        editDialog.show(ft, "dialog");
    }

    @Override
    public void onPasswordSet(UserModel user) {
        ContentValues values = new ContentValues();
        values.put(UserModel.KEY_SALT, user.salt);
        values.put(UserModel.KEY_PWHASH, user.pwhash);
        getContentResolver().update(
                Uri.withAppendedPath(DataProvider.URI_USERS,
                        String.valueOf(user.id)), values, null, null);
    }
}
