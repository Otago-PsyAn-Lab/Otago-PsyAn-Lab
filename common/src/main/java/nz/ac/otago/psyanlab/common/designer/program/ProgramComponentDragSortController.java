/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.common.designer.program;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

class ProgramComponentDragSortController extends DragSortController {
    private DragSortListView mDslv;

    public ProgramComponentDragSortController(DragSortListView dslv, int dragHandleId, int dragInitMode,
            int removeMode, int clickRemoveId, int flingHandleId) {
        super(dslv, dragHandleId, dragInitMode, removeMode, clickRemoveId, flingHandleId);
        mDslv = dslv;

    }

    @Override
    public boolean startDrag(int position, int deltaX, int deltaY) {
        if (mDslv.getCheckedItemPosition() == position) {
            return super.startDrag(position, deltaX, deltaY);
        }

        return false;
    }

}