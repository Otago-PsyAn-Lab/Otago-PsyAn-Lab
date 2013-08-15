
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Loop;

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
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

public class LoopsListFragment extends BaseProgramFragment {
    public static BaseProgramFragment newInstance() {
        BaseProgramFragment f = new LoopsListFragment();
        return f;
    }

    public ListAdapter mLoopsAdapter;

    public OnItemClickListener mOnLoopItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mViews.listview.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
                mViews.listview.setItemChecked(position, true);
                // setNextFragment(null);
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

    private ViewHolder mViews;

    protected ActionMode mActionMode;

    protected OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
            if (mActionMode != null) {
                return false;
            }
            mViews.listview.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mViews.listview.setItemChecked(position, true);
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
                    mViews.listview.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                    // FIXME: Marked checked the current selected loop.
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

    protected class ViewHolder {
        public ListView listview;

        public Button newLoop;

        public ViewHolder(View view) {
            listview = (ListView)view.findViewById(android.R.id.list);
            newLoop = (Button)view.findViewById(R.id.new_loop);
        }

        public void initViews() {
            listview.setMultiChoiceModeListener(mMultiChoiceModeCallbacks);
            listview.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listview.setOnItemClickListener(mOnLoopItemClickListener);
            listview.setOnItemLongClickListener(mItemLongClickListener);
            listview.setAdapter(mLoopsAdapter);

            newLoop.setOnClickListener(mNewLoopClickListener);
        }
    }
}