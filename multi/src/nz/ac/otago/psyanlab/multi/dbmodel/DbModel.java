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

package nz.ac.otago.psyanlab.multi.dbmodel;

public abstract class DbModel {
    public static final long INVALID_COL = -1;
    public static final long INVALID_ID = -1;
    public static final String KEY_ID = "_id";

    public interface DeleteSuccessListener {
        public void onDeleteFailure(long id);

        public void onDeleteSuccess();
    }

    public interface InsertSuccessListener {
        public void onInsertFailure();

        public void onInsertSuccess(long id);
    }

    public interface UpdateSuccessListener {
        public void onUpdateFailure(long id);

        public void onUpdateSuccess(long id);
    }
}
