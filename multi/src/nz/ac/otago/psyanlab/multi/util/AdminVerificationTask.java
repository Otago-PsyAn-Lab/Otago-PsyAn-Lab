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

package nz.ac.otago.psyanlab.multi.util;

import nz.ac.otago.psyanlab.multi.dbmodel.AdminModel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Background task to verify a password matches that stored for the
 * administrator.
 */
public class AdminVerificationTask extends AuthVerificationTask {
    private static final String TAG = "psyanlab.runner.util.adminauth";

    private static final String FMT_ERROR_INCORRECT_ARGS = "Incorrect number of arguments. Got %d, expected 1.";
    private static final String ERROR_MISSING_RECORD = "Admin record missing.";

    /**
     * A task to verify a password for the administrator.
     * 
     * @param database Database where user record is held.
     * @param al Listener for the verification result.
     */
    public AdminVerificationTask(SQLiteDatabase database, AuthListener al) {
        super(database, al);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if (params.length != 1) {
            throw new RuntimeException(String.format(FMT_ERROR_INCORRECT_ARGS,
                    params.length));
        }
        String password = params[0];

        Cursor c = db.query(AdminModel.TABLE, null, null, null, null, null,
                null);
        if (!c.moveToFirst()) {
            Log.e(TAG, ERROR_MISSING_RECORD);
            return false;
        }

        String pwhash = c.getString(c
                .getColumnIndex(AdminModel.KEY_PASSWORDHASH));
        String salt = c.getString(c.getColumnIndex(AdminModel.KEY_UPSALT));

        if (PWUtils.verify(password, pwhash, salt)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        authListener.onAuthTaskComplete(result, null);
    }
}
