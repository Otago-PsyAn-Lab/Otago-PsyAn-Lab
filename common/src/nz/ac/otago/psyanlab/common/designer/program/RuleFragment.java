
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.RuleDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Action;
import nz.ac.otago.psyanlab.common.model.Rule;

import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class RuleFragment extends BaseProgramFragment implements RuleDataChangeListener {
    public static BaseProgramFragment newInstance(long id) {
        return init(new RuleFragment(), id);
    }

    public OnItemLongClickListener mActionItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
            if (mActionMode != null) {
                return false;
            }
            mViews.actionsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mViews.actionsList.setItemChecked(position, true);
            setNextFragment(null);
            return true;
        }
    };

    public ListAdapter mActionsAdapter;

    public OnClickListener mConditionClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
        }
    };

    public MultiChoiceModeListener mMultiChoiceModeCallbacks = new MultiChoiceModeListener() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();
            boolean sceneIsDirty = false;
            if (itemId == R.id.menu_delete) {
                long[] checkedItemIds = mViews.actionsList.getCheckedItemIds();
                for (int i = 0; i < checkedItemIds.length; i++) {
                    mCallbacks.deleteAction(checkedItemIds[i]);
                    mRule.actions.remove(checkedItemIds[i]);
                    sceneIsDirty = true;
                }
            } else {
            }
            if (sceneIsDirty) {
                mCallbacks.updateRule(mObjectId, mRule);
            }
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.context_program_component, menu);
            mode.setTitle(R.string.title_select_actions);

            mActionMode = mode;

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mViews.actionsList.post(new Runnable() {
                @Override
                public void run() {
                    mViews.actionsList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                    // FIXME: Marked checked the current selected loop.
                }
            });
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                boolean checked) {
            final int checkedCount = mViews.actionsList.getCheckedItemCount();
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

    public OnClickListener mNewActionClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onNewAction();
        }
    };

    public OnItemClickListener mOnActionItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mViews.actionsList.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
                mViews.actionsList.setItemChecked(position, true);
            } else if (mViews.actionsList.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
                onActionClick(id);
            }
        }
    };

    private ActionMode mActionMode;

    private Rule mRule;

    private ViewHolder mViews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_program_rule, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks.removeRuleDataChangeListener(this);
        saveChanges();
    }

    @Override
    public void onRuleDataChange() {
        Rule old = mRule;
        mRule = mCallbacks.getRule(mObjectId);
        if (mRule == null) {
            removeSelf();
        }

        mViews.updateViews(mRule, old);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRule = mCallbacks.getRule(mObjectId);
        mCallbacks.addRuleDataChangeListener(this);

        mActionsAdapter = mCallbacks.getActionAdapter(mObjectId);

        mViews = new ViewHolder(view);
        mViews.setViewValues(mRule);
        mViews.initViews();
    }

    private void saveChanges() {
        mCallbacks.updateRule(mObjectId, mRule);
    }

    protected void onActionClick(long id) {
        setNextFragment(ActionFragment.newInstance(id));
    }

    protected void onNewAction() {
        Action action = new Action();
        final long newActionId = mCallbacks.createAction(action);
        action.name = "Action " + (newActionId + 1);
        mRule.actions.add(newActionId);
        mCallbacks.updateRule(mObjectId, mRule);
        setNextFragment(ActionFragment.newInstance(newActionId));

        if (mActionMode != null) {
            mActionMode.finish();
            mViews.actionsList.post(new Runnable() {
                @Override
                public void run() {
                    selectItemInList(newActionId, mViews.actionsList);
                }
            });
        } else {
            selectItemInList(newActionId, mViews.actionsList);
        }
    }

    private class ViewHolder {
        public Button condition;

        public TextView name;

        public ListView operands;

        public Spinner triggerEvent;

        public Spinner triggerObject;

        private ListView actionsList;

        private Button newAction;

        public ViewHolder(View view) {
            name = (EditText)view.findViewById(R.id.name);
            triggerObject = (Spinner)view.findViewById(R.id.trigger_object);
            triggerEvent = (Spinner)view.findViewById(R.id.trigger_event);
            condition = (Button)view.findViewById(R.id.condition);
            actionsList = (ListView)view.findViewById(R.id.actions);
            newAction = (Button)view.findViewById(R.id.new_action);
        }

        public void initViews() {
            condition.setOnClickListener(mConditionClickListener);

            newAction.setOnClickListener(mNewActionClickListener);

            actionsList.setAdapter(mActionsAdapter);
            actionsList.setMultiChoiceModeListener(mMultiChoiceModeCallbacks);
            actionsList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            actionsList.setOnItemClickListener(mOnActionItemClickListener);
            actionsList.setOnItemLongClickListener(mActionItemLongClickListener);
        }

        public void setViewValues(Rule rule) {
            name.setText(rule.name);
        }

        public void updateViews(Rule newRule, Rule oldRule) {
            if (!TextUtils.equals(newRule.name, oldRule.name)) {
                name.setText(newRule.name);
            }
        }
    }
}
