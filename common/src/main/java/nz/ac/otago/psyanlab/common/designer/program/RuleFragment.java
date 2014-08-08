package nz.ac.otago.psyanlab.common.designer.program;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.OperandDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.RuleDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.program.object.PickObjectDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.program.operand.EditOperandDialogFragment;
import nz.ac.otago.psyanlab.common.designer.util.DialogueRequestCodes;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar.DialogueResultListener;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.model.Action;
import nz.ac.otago.psyanlab.common.model.ExperimentObject;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.Rule;
import nz.ac.otago.psyanlab.common.model.operand.CallValue;
import nz.ac.otago.psyanlab.common.model.operand.kind.CallOperand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.Type;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class RuleFragment extends BaseProgramFragment
        implements RuleDataChangeListener, OperandDataChangeListener {

    private static final String ARG_SCENE_ID = "arg_scene_id";

    protected final OnItemClickListener mActionItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mViews.actionsList.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
                mViews.actionsList.setItemChecked(position, true);
            } else if (mViews.actionsList.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
                onActionClick(id);

                View handle = mViews.actionsList.getChildAt(
                        position - mViews.actionsList.getFirstVisiblePosition() +
                        mViews.actionsList.getHeaderViewsCount()).findViewById(R.id.handle);
                if (handle != null) {
                    handle.setEnabled(true);
                    handle.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    protected final OnClickListener mConditionClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showEditConditionDialogue();
        }
    };

    protected final OnClickListener mNewActionClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onNewAction();
        }
    };

    protected final OnClickListener mTriggerObjectClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Once the user has picked an experiment object we will be notified
            // through our listener, mDialogueResultListener.
            mCallbacks.pickExperimentObject(ExperimentObject.KIND_RULE, mObjectId,
                                            ExperimentObject.EMITS_EVENTS,
                                            DialogueRequestCodes.RULE_TRIGGER_OBJECT);
        }
    };

    protected ActionMode mActionMode;

    protected final OnItemLongClickListener mActionItemLongClickListener =
            new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View view, int position,
                                               long id) {
                    if (mActionMode != null) {
                        return false;
                    }
                    mViews.actionsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
                    mViews.actionsList.setItemChecked(position, true);
                    mActionsAdapter.fixItemBackground(R.drawable.rule_activated_background);
                    setNextFragment(null);

                    for (int i = 0; i < mViews.actionsList.getChildCount(); i++) {
                        View handle = mViews.actionsList.getChildAt(i).findViewById(R.id.handle);
                        if (handle != null) {
                            handle.setEnabled(false);
                            handle.setVisibility(View.GONE);
                        }
                    }

                    mViews.actionsList.setDragEnabled(false);

                    return true;
                }
            };

    protected ProgramComponentAdapter<Action> mActionsAdapter;

    protected Rule mRule;

    protected final DialogueResultListener mDialogueResultListener = new DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            long objectId = data.getLong(PickObjectDialogueFragment.RESULT_OBJECT_ID);
            int objectKind = data.getInt(PickObjectDialogueFragment.RESULT_OBJECT_KIND);

            mRule.triggerObject = new ExperimentObjectReference(objectKind, objectId);

            mCallbacks.putRule(mObjectId, mRule);
        }

        @Override
        public void onResultDelete(Bundle data) {
        }

        @Override
        public void onResultCancel() {
        }
    };

    protected final MultiChoiceModeListener mMultiChoiceModeCallbacks =
            new MultiChoiceModeListener() {
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
                        mCallbacks.putRule(mObjectId, mRule);
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
                            mActionsAdapter
                                    .fixItemBackground(R.drawable.rule_activated_background_arrow);
                            mViews.actionsList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                            mViews.actionsList.setDragEnabled(true);
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
                            mode.setSubtitle(String.format(getResources().getString(
                                    R.string.subtitle_fmt_num_items_selected), checkedCount));
                            break;
                    }
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return true;
                }
            };

    protected final OnItemSelectedListener mTriggerEventItemSelectedListener =
            new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position,
                                           long id) {
                    if (mRule.triggerEvent != (int) id) {
                        mRule.triggerEvent = (int) id;
                        mCallbacks.putRule(mObjectId, mRule);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    protected long mSceneId;

    protected ViewHolder mViews;

    public static BaseProgramFragment newInstance(long id, long sceneId) {
        RuleFragment fragment = init(new RuleFragment(), id);
        Bundle args = fragment.getArguments();
        args.putLong(ARG_SCENE_ID, sceneId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_program_rule, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks.removeOperandDataChangeListener(this);
        mCallbacks.removeRuleDataChangeListener(this);
        saveChanges();
    }

    @Override
    public void onOperandDataChange() {
        mViews.updateViews(mRule, mRule);
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
        mCallbacks.addOperandDataChangeListener(this);

        mActionsAdapter = mCallbacks.getActionAdapter(mObjectId);

        mViews = new ViewHolder(view);
        mViews.setViewValues(mRule);
        mViews.initViews();

        // Register a listener for when a rule trigger object has been set.
        mCallbacks.registerDialogueResultListener(DialogueRequestCodes.RULE_TRIGGER_OBJECT,
                                                  mDialogueResultListener);
    }

    private void saveChanges() {
        mCallbacks.putRule(mObjectId, mRule);
    }

    @Override
    protected int getFavouredBackground() {
        return R.drawable.background_designer_program_rule;
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
        Operand condition = new CallValue();
        condition.type = ExperimentObject.HAS_SETTERS;
        action.operandId = mCallbacks.addOperand(condition);
        final long newActionId = mCallbacks.addAction(action);
        action.name = "Action " + (newActionId + 1);
        mRule.actions.add(newActionId);
        mCallbacks.putRule(mObjectId, mRule);
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

    protected void showEditConditionDialogue() {
        if (mActionMode != null) {
            mActionMode.finish();
        }

        EditOperandDialogFragment dialog = EditOperandDialogFragment
                .newDialog(ExperimentObject.KIND_RULE, mObjectId, mRule.conditionId,
                           Type.TYPE_BOOLEAN,
                           getActivity().getString(R.string.title_edit_condition));
        dialog.show(getChildFragmentManager(), "dialog_edit_iteration");
    }

    private class ViewHolder extends BaseProgramFragment.ViewHolder<Rule> {

        public TextView conditionDetails;

        public View editCondition;

        public TextView name;

        public Spinner triggerEvent;

        public Button triggerObject;

        private DragSortListView actionsList;

        public TextWatcher mNameWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String newName = s.toString();
                if (!TextUtils.equals(mRule.name, newName)) {
                    mRule.name = newName;
                    mCallbacks.putRule(mObjectId, mRule);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        private View mEmpty;

        private View newAction;

        public ViewHolder(View view) {
            super(view);
            name = (EditText) view.findViewById(R.id.name);
            triggerObject = (Button) view.findViewById(R.id.trigger_object);
            triggerEvent = (Spinner) view.findViewById(R.id.trigger_event);
            editCondition = view.findViewById(R.id.edit_condition);
            conditionDetails = (TextView) view.findViewById(R.id.condition_detail);
            actionsList = (DragSortListView) view.findViewById(R.id.actions);
            mEmpty = view.findViewById(android.R.id.empty);
            newAction = view.findViewById(R.id.new_action);

            @SuppressLint("WrongViewCast")
            final GridLayout gridLayout = (GridLayout) view.findViewById(R.id.background);
            final View actionsListContainer = view.findViewById(R.id.actions_list_container);
            final View column = view.findViewById(R.id.column);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    fixGridLayoutOverflow(gridLayout, actionsListContainer);
                    fixGridLayoutOverflow(gridLayout, column);
                }
            });
        }

        @Override
        public void initViews() {
            editCondition.setOnClickListener(mConditionClickListener);

            name.addTextChangedListener(mNameWatcher);

            newAction.setOnClickListener(mNewActionClickListener);

            ProgramComponentDragSortController controller =
                    new ProgramComponentDragSortController(actionsList, R.id.handle,
                                                           DragSortController.ON_DRAG, 0, 0, 0);
            actionsList.setFloatViewManager(controller);
            actionsList.setOnTouchListener(controller);
            actionsList.setAdapter(mActionsAdapter);
            actionsList.setMultiChoiceModeListener(mMultiChoiceModeCallbacks);
            actionsList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            actionsList.setOnItemClickListener(mActionItemClickListener);
            actionsList.setOnItemLongClickListener(mActionItemLongClickListener);
            actionsList.setEmptyView(mEmpty);

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

            updateCondition(rule);
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

            updateCondition(newRule);
        }

        private void setTrigger(Rule rule) {
            triggerObject.setText(mCallbacks.getExperimentObject(rule.triggerObject)
                                            .getExperimentObjectName(getActivity()));
            triggerEvent.setEnabled(true);
            SpinnerAdapter eventsAdapter = mCallbacks.getEventsAdapter(
                    mCallbacks.getExperimentObject(rule.triggerObject).getClass());
            triggerEvent.setAdapter(eventsAdapter);
            for (int i = 0; i < eventsAdapter.getCount(); i++) {
                if ((int) eventsAdapter.getItemId(i) == mRule.triggerEvent) {
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

        private void updateCondition(Rule rule) {
            Operand condition = mCallbacks.getOperand(rule.conditionId);
            String operandDetail = "";
            if (condition instanceof CallOperand) {
                CallOperand callOperand = (CallOperand) condition;
                ExperimentObject experimentObject =
                        mCallbacks.getExperimentObject(callOperand.getObject());
                final NameResolverFactory nameFactory = experimentObject.getMethodNameFactory();
                operandDetail = getString(R.string.format_call_operand_value,
                                          experimentObject.getExperimentObjectName(getActivity()),
                                          nameFactory
                                                  .getName(getActivity(), callOperand.getMethod()));
            } else if (condition instanceof LiteralOperand) {
                operandDetail = ((LiteralOperand) condition).getValue();
            } else {
                conditionDetails.setText(R.string.default_condition_details);
                return;
            }

            conditionDetails.setText(getString(R.string.format_condition_details, operandDetail));
        }
    }
}
