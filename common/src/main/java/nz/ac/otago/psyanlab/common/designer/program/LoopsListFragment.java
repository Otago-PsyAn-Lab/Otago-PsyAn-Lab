
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

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.model.Loop;
import nz.ac.otago.psyanlab.common.util.TonicFragment;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class LoopsListFragment extends BaseProgramFragment {
    public static TonicFragment newInstance() {
        TonicFragment f = new LoopsListFragment();
        return f;
    }

    public ProgramComponentAdapter<?> mLoopsAdapter;

    public OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mViews.loops.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
                mViews.loops.setItemChecked(position, true);

            } else if (mViews.loops.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
                setNextFragment(LoopFragment.newInstance(id));

                View handle = mViews.loops.getChildAt(
                        position - mViews.loops.getFirstVisiblePosition()
                                + mViews.loops.getHeaderViewsCount()).findViewById(R.id.handle);
                if (handle != null) {
                    handle.setEnabled(true);
                    handle.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private OnClickListener mNewLoopClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onNewLoop();
        }
    };

    protected ActionMode mActionMode;

    protected OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
            if (mActionMode != null) {
                return false;
            }
            mViews.loops.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mViews.loops.setItemChecked(position, true);
            mLoopsAdapter.fixItemBackground(R.drawable.loop_activated_background);
            setNextFragment(null);

            for (int i = 0; i < mViews.loops.getChildCount(); i++) {
                View handle = mViews.loops.getChildAt(i).findViewById(R.id.handle);
                if (handle != null) {
                    handle.setEnabled(false);
                    handle.setVisibility(View.GONE);
                }
            }

            mViews.loops.setDragEnabled(false);

            return true;
        }
    };

    protected MultiChoiceModeListener mMultiChoiceModeCallbacks = new MultiChoiceModeListener() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_delete) {
                long[] checkedItemIds = mViews.loops.getCheckedItemIds();
                for (int i = 0; i < checkedItemIds.length; i++) {
                    mCallbacks.deleteLoop(checkedItemIds[i]);
                }
            } else {
            }
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.context_program_component, menu);
            mode.setTitle(R.string.title_select_loops);

            mActionMode = mode;

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mViews.loops.post(new Runnable() {
                @Override
                public void run() {
                    mLoopsAdapter.fixItemBackground(R.drawable.loops_activated_background_arrow);
                    mViews.loops.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                    mViews.loops.setDragEnabled(true);
                }
            });
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                boolean checked) {
            final int checkedCount = mViews.loops.getCheckedItemCount();
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                case 1:
                    mode.setSubtitle(R.string.subtitle_one_item_selected);
                    break;
                default:
                    mode.setSubtitle(String.format(
                            getResources().getString(R.string.subtitle_fmt_num_items_selected),
                            checkedCount));
                    break;
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }
    };

    protected ViewHolder mViews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_program_loops, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoopsAdapter = mCallbacks.getLoopAdapter();

        mViews = new ViewHolder(view);
        mViews.initViews();

    }

    @Override
    protected int getFavouredBackground() {
        return R.drawable.background_designer_program_loops;
    }

    @Override
    protected ViewHolder getViewHolder() {
        return mViews;
    }

    protected void onNewLoop() {
        Loop loop = new Loop();
        final long newLoopId = mCallbacks.addLoop(loop);
        loop.name = "Loop " + (newLoopId + 1);
        setNextFragment(LoopFragment.newInstance(newLoopId));

        if (mActionMode != null) {
            mActionMode.finish();
            mViews.loops.post(new Runnable() {
                @Override
                public void run() {
                    selectItemInList(newLoopId, mViews.loops);
                }
            });
        } else {
            selectItemInList(newLoopId, mViews.loops);
        }
    }

    protected class ViewHolder extends BaseProgramFragment.ViewHolder<Object> {
        public DragSortListView loops;

        private View mEmpty;

        private View mNew;

        public ViewHolder(View view) {
            super(view);
            loops = (DragSortListView)view.findViewById(android.R.id.list);
            mEmpty = view.findViewById(android.R.id.empty);
            mNew = view.findViewById(R.id.new_loop);
        }

        @Override
        public void initViews() {
            loops.setMultiChoiceModeListener(mMultiChoiceModeCallbacks);
            loops.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            loops.setOnItemClickListener(mOnItemClickListener);
            loops.setOnItemLongClickListener(mItemLongClickListener);

            ProgramComponentDragSortController controller = new ProgramComponentDragSortController(
                    loops, R.id.handle, DragSortController.ON_DRAG, 0, 0, 0);
            loops.setFloatViewManager(controller);
            loops.setOnTouchListener(controller);
            loops.setAdapter(mLoopsAdapter);
            loops.setEmptyView(mEmpty);

            mNew.setOnClickListener(mNewLoopClickListener);
        }

        @Override
        public void setViewValues(Object object) {
        }
    }
}
