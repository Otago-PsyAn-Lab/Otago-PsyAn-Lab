
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.RuleDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.designer.program.object.PickObjectDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.program.operand.EditOperandDialogFragment;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar.DialogueResultListener;
import nz.ac.otago.psyanlab.common.designer.util.RequestCodes;
import nz.ac.otago.psyanlab.common.model.Action;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.Operand;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class RuleFragment extends BaseProgramFragment implements RuleDataChangeListener {
    private static final String ARG_SCENE_ID = "arg_scene_id";

    public static BaseProgramFragment newInstance(long id, long sceneId) {
        RuleFragment fragment = init(new RuleFragment(), id);
        Bundle args = fragment.getArguments();
        args.putLong(ARG_SCENE_ID, sceneId);
        fragment.setArguments(args);
        return fragment;
    }

    protected final OnItemClickListener mActionItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mViews.actionsList.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
                mViews.actionsList.setItemChecked(position, true);
            } else if (mViews.actionsList.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
                onActionClick(id);
            }
        }
    };

    protected final OnItemLongClickListener mActionItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
            if (mActionMode != null) {
                return false;
            }
            mViews.actionsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mViews.actionsList.setItemChecked(position, true);
            mActionsAdapter.fixItemBackground(R.drawable.rule_activated_background);
            setNextFragment(null);
            return true;
        }
    };

    protected ActionMode mActionMode;

    protected ProgramComponentAdapter<Action> mActionsAdapter;

    protected final OnClickListener mConditionClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showEditOperandDialogue();
        }
    };

    protected final DialogueResultListener mDialogueResultListener = new DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            long objectId = data.getLong(PickObjectDialogueFragment.RESULT_OBJECT_ID);
            int objectKind = data.getInt(PickObjectDialogueFragment.RESULT_OBJECT_KIND);

            mRule.triggerObject = new ExperimentObjectReference(objectKind, objectId);

            mCallbacks.updateRule(mObjectId, mRule);
        }
    };

    protected final MultiChoiceModeListener mMultiChoiceModeCallbacks = new MultiChoiceModeListener() {
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
                    mActionsAdapter.fixItemBackground(R.drawable.rule_activated_background_arrow);
                    mViews.actionsList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
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

    protected final OnClickListener mNewActionClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onNewAction();
        }
    };

    protected Rule mRule;

    protected long mSceneId;

    protected final OnItemSelectedListener mTriggerEventItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mRule.triggerEvent != (int)id) {
                mRule.triggerEvent = (int)id;
                mCallbacks.updateRule(mObjectId, mRule);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    protected final OnClickListener mTriggerObjectClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Once the user has picked an experiment object we will be notified
            // through our listener, mDialogueResultListener.
            mCallbacks.pickExperimentObject(mSceneId, ExperimentObjectReference.EMITS_EVENTS,
                    RequestCodes.RULE_TRIGGER_OBJECT);
        }
    };

    protected ViewHolder mViews;

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

        mSceneId = getArguments().getLong(ARG_SCENE_ID);

        mRule = mCallbacks.getRule(mObjectId);
        mCallbacks.addRuleDataChangeListener(this);

        mActionsAdapter = mCallbacks.getActionAdapter(mObjectId);

        mViews = new ViewHolder(view);
        mViews.setViewValues(mRule);
        mViews.initViews();

        // Register a listener for when a rule trigger object has been set.
        mCallbacks.registerDialogueResultListener(RequestCodes.RULE_TRIGGER_OBJECT,
                mDialogueResultListener);
    }

    @Override
    protected int getFavouredBackground() {
        return R.drawable.rule_background_flat;
    }

    @Override
    protected ViewHolder getViewHolder() {
        return mViews;
    }

    protected void onActionClick(long id) {
        setNextFragment(ActionFragment.newInstance(id, mSceneId));
    }

    protected void onNewAction() {
        Action action = new Action();
        final long newActionId = mCallbacks.createAction(action);
        action.name = "Action " + (newActionId + 1);
        mRule.actions.add(newActionId);
        mCallbacks.updateRule(mObjectId, mRule);
        setNextFragment(ActionFragment.newInstance(newActionId, mSceneId));

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

    protected void showEditOperandDialogue() {
        if (mActionMode != null) {
            mActionMode.finish();
        }

        EditOperandDialogFragment dialog = EditOperandDialogFragment.newDialog(mRule.conditionId,
                Operand.TYPE_BOOLEAN);
        dialog.show(getChildFragmentManager(), "dialog_edit_iteration");
    }

    private void saveChanges() {
        mCallbacks.updateRule(mObjectId, mRule);
    }

    private class ViewHolder extends BaseProgramFragment.ViewHolder<Rule> {
        public View condition;

        public TextView name;

        public Spinner triggerEvent;

        public Button triggerObject;

        private ListView actionsList;

        private View newAction;

        public ViewHolder(View view) {
            super(view);
            name = (EditText)view.findViewById(R.id.name);
            triggerObject = (Button)view.findViewById(R.id.trigger_object);
            triggerEvent = (Spinner)view.findViewById(R.id.trigger_event);
            condition = view.findViewById(R.id.edit_condition);
            actionsList = (ListView)view.findViewById(R.id.actions);
            newAction = view.findViewById(R.id.new_action);
        }

        @Override
        public void initViews() {
            condition.setOnClickListener(mConditionClickListener);

            newAction.setOnClickListener(mNewActionClickListener);

            actionsList.setAdapter(mActionsAdapter);
            actionsList.setMultiChoiceModeListener(mMultiChoiceModeCallbacks);
            actionsList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            actionsList.setOnItemClickListener(mActionItemClickListener);
            actionsList.setOnItemLongClickListener(mActionItemLongClickListener);
            actionsList.setDivider(null);

            triggerObject.setOnClickListener(mTriggerObjectClickListener);
            triggerEvent.setOnItemSelectedListener(mTriggerEventItemSelectedListener);
        }

        @Override
        public void setViewValues(Rule rule) {
            name.setText(rule.name);
            if (rule.triggerObject != null) {
                setTrigger(rule);
            } else {
                unsetTrigger();
            }
        }

        public void updateViews(Rule newRule, Rule oldRule) {
            if (!TextUtils.equals(newRule.name, oldRule.name)) {
                name.setText(newRule.name);
            }
            if (newRule.triggerObject != null) {
                setTrigger(newRule);
            } else {
                unsetTrigger();
            }
        }

        private void setTrigger(Rule rule) {
            triggerObject.setText(mCallbacks.getExperimentObject(rule.triggerObject).getPrettyName(
                    getActivity()));
            triggerEvent.setEnabled(true);
            SpinnerAdapter eventsAdapter = mCallbacks.getEventsAdapter(mCallbacks
                    .getExperimentObject(rule.triggerObject).getClass());
            triggerEvent.setAdapter(eventsAdapter);
            for (int i = 0; i < eventsAdapter.getCount(); i++) {
                if ((int)eventsAdapter.getItemId(i) == mRule.triggerEvent) {
                    triggerEvent.setSelection(i);
                    break;
                }
            }
        }

        private void unsetTrigger() {
            triggerObject.setText(null);
            triggerEvent.setEnabled(false);
            triggerEvent.setAdapter(null);
        }
    }
}
