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

import nz.ac.otago.psyanlab.common.util.Args;
import nz.ac.otago.psyanlab.multi.AuthDialogFragment.AuthListener;
import nz.ac.otago.psyanlab.multi.admin.AdminActivity;
import nz.ac.otago.psyanlab.multi.dbmodel.UserModel;
import nz.ac.otago.psyanlab.multi.util.DataProvider;
import nz.ac.otago.psyanlab.multi.util.UserDelegate;

import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SelectUserActivity extends FragmentActivity implements
        UserListFragment.Callbacks {
    public static final int LOADER_USERS = 0x01;

    private AuthDialogFragment.AuthListener manageUsersAuthListener = new AuthDialogFragment.AuthListener() {
        @Override
        public void onAuthCancel() {
        }

        @Override
        public void onAuthSuccess() {
            // Start user management activity.
            Intent manageUsersIntent = new Intent(SelectUserActivity.this,
                    AdminActivity.class);
            startActivity(manageUsersIntent);
        }
    };

    public SimpleCursorAdapter mUsersAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        UserGridFragment ulf = ((UserGridFragment)getSupportFragmentManager()
                .findFragmentById(R.id.user_list));
        ulf.setActivateOnItemClick(false);
        ulf.setColumnWidth(getResources().getDimension(R.dimen.select_user_column_width));

        // TODO: Remove this before release!
        // Export the database file for debug purposes.
        File dbfile = getDatabasePath("runner");
        File dest = new File(
                Environment.getExternalStoragePublicDirectory("PsyAn Lab"),
                "runner.db");
        try {
            InputStream in = new FileInputStream(dbfile);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[8192];
            int len;
            while (-1 != (len = in.read(buffer))) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(this, new String[] {
            dest.getPath()
        }, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_profile_selection, menu);
        return true;
    };

    @Override
    public void onUserSelected(final UserModel user) {
        if (user.pwhash == null) {
            loginUser(user);
            return;
        }

        // Clear any existing dialog.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show authentication dialog.
        DialogFragment authDialog = AuthDialogFragment.newDialog(getResources()
                .getString(R.string.title_authenticate), user.id,
                new AuthListener() {
                    @Override
                    public void onAuthCancel() {
                    }

                    @Override
                    public void onAuthSuccess() {
                        loginUser(user);
                    }

                });
        authDialog.show(ft, "dialog");
    }

    private void loginUser(final UserModel user) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(new ComponentName("nz.ac.otago.psyanlab.multi",
                "nz.ac.otago.psyanlab.common.PaleActivity"));
        intent.putExtra(Args.USER_DELEGATE,
                new UserDelegate(user.id, user.name));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (item.getItemId()) {
            case R.id.menu_manage:
                // Clear any existing dialog.
                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment prev = fragmentManager.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show authentication dialog.
                DialogFragment authDialog = AuthDialogFragment.newDialog(
                        getResources().getString(R.string.title_authenticate),
                        manageUsersAuthListener);
                authDialog.show(ft, "dialog");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public SimpleCursorAdapter getUsersAdapter(int layout, String[] fields,
            int[] ids) {
        mUsersAdapter = new SimpleCursorAdapter(this, layout, null, fields,
                ids, 0);

        getSupportLoaderManager().initLoader(LOADER_USERS, null,
                new UsersLoaderCallbacks());
        return mUsersAdapter;
    }

    private final class UsersLoaderCallbacks implements LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getApplicationContext(),
                    DataProvider.URI_USERS, null, null, null,
                    UserModel.KEY_NAME + " ASC");
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Cursor oldCursor = mUsersAdapter.swapCursor(null);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
            Cursor oldCursor = mUsersAdapter.swapCursor(newCursor);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }
    }
}
