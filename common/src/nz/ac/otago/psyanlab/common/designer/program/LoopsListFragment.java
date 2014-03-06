
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter;
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

    public OnItemClickListener mOnLoopItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mViews.listview.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
                mViews.listview.setItemChecked(position, true);

            } else if (mViews.listview.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
                setNextFragment(LoopFragment.newInstance(id));
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
            mViews.listview.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mViews.listview.setItemChecked(position, true);
            mLoopsAdapter.fixItemBackground(R.drawable.loop_activated_background);
            setNextFragment(null);
            return true;
        }
    };

    protected MultiChoiceModeListener mMultiChoiceModeCallbacks = new MultiChoiceModeListener() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_delete) {
                long[] checkedItemIds = mViews.listview.getCheckedItemIds();
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
            mViews.listview.post(new Runnable() {
                @Override
                public void run() {
                    mLoopsAdapter.fixItemBackground(R.drawable.loop_activated_background_arrow);
                    mViews.listview.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                }
            });
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                boolean checked) {
            final int checkedCount = mViews.listview.getCheckedItemCount();
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
        return R.drawable.loops_background_flat;
    }

    @Override
    protected ViewHolder getViewHolder() {
        return mViews;
    }

    protected void onNewLoop() {

        Loop loop = new Loop();
        final long newLoopId = mCallbacks.createLoop(loop);
        loop.name = "Loop " + (newLoopId + 1);
        setNextFragment(LoopFragment.newInstance(newLoopId));

        if (mActionMode != null) {
            mActionMode.finish();
            mViews.listview.post(new Runnable() {
                @Override
                public void run() {
                    selectItemInList(newLoopId, mViews.listview);
                }
            });
        } else {
            selectItemInList(newLoopId, mViews.listview);
        }
    }

    protected class ViewHolder extends BaseProgramFragment.ViewHolder<Object> {
        public ListView listview;

        public View newLoop;

        public ViewHolder(View view) {
            super(view);
            listview = (ListView)view.findViewById(android.R.id.list);
            newLoop = view.findViewById(R.id.new_loop);
        }

        public void initViews() {
            listview.setMultiChoiceModeListener(mMultiChoiceModeCallbacks);
            listview.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listview.setOnItemClickListener(mOnLoopItemClickListener);
            listview.setOnItemLongClickListener(mItemLongClickListener);
            listview.setAdapter(mLoopsAdapter);
            listview.setDivider(null);

            newLoop.setOnClickListener(mNewLoopClickListener);
        }

        @Override
        public void setViewValues(Object object) {
        }
    }
}
