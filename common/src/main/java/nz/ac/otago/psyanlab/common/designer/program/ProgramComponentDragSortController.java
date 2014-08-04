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