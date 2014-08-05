
package nz.ac.otago.psyanlab.common.designer.program;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.SceneDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.program.stage.PropAdapter;
import nz.ac.otago.psyanlab.common.designer.program.stage.StageView;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.model.Rule;
import nz.ac.otago.psyanlab.common.model.Scene;
import nz.ac.otago.psyanlab.common.model.operand.BooleanValue;

import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SceneFragment extends BaseProgramFragment implements SceneDataChangeListener {

    protected final OnClickListener mEditStageClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.startEditStage(mObjectId);
        }
    };

    protected final OnClickListener mNewRuleClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onNewRule();
        }
    };

    protected final OnItemClickListener mRuleItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mViews.rulesList.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
                mViews.rulesList.setItemChecked(position, true);
            } else if (mViews.rulesList.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
                onRuleClick(id);

                View handle = mViews.rulesList.getChildAt(
                        position - mViews.rulesList.getFirstVisiblePosition()
                                + mViews.rulesList.getHeaderViewsCount()).findViewById(R.id.handle);
                if (handle != null) {
                    handle.setEnabled(true);
                    handle.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    protected ActionMode mActionMode;

    protected final OnItemLongClickListener mRuleItemLongClickListener
            = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
            if (mActionMode != null) {
                return false;
            }
            mViews.rulesList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mViews.rulesList.setItemChecked(position, true);
            mRulesAdapter.fixItemBackground(R.drawable.scene_activated_background);
            setNextFragment(null);

            for (int i = 0; i < mViews.rulesList.getChildCount(); i++) {
                View handle = mViews.rulesList.getChildAt(i).findViewById(R.id.handle);
                if (handle != null) {
                    handle.setEnabled(false);
                    handle.setVisibility(View.GONE);
                }
            }

            mViews.rulesList.setDragEnabled(false);

            return true;
        }
    };

    protected PropAdapter mPropAdapter;

    protected ProgramComponentAdapter<Rule> mRulesAdapter;

    protected Scene mScene;

    protected final MultiChoiceModeListener mMultiChoiceModeCallbacks
            = new MultiChoiceModeListener() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();
            boolean sceneIsDirty = false;
            if (itemId == R.id.menu_delete) {
                long[] checkedItemIds = mViews.rulesList.getCheckedItemIds();
                for (int i = 0; i < checkedItemIds.length; i++) {
                    mCallbacks.deleteRule(checkedItemIds[i]);
                    mScene.rules.remove(checkedItemIds[i]);
                    sceneIsDirty = true;
                }
            } else {
            }
            if (sceneIsDirty) {
                mCallbacks.putScene(mObjectId, mScene);
            }
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.context_program_component, menu);
            mode.setTitle(R.string.title_select_scenes);

            mActionMode = mode;

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mViews.rulesList.post(new Runnable() {
                @Override
                public void run() {
                    mRulesAdapter.fixItemBackground(R.drawable.scene_activated_background_arrow);
                    mViews.rulesList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                    mViews.rulesList.setDragEnabled(true);
                }
            });
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                boolean checked) {
            final int checkedCount = mViews.rulesList.getCheckedItemCount();
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

    protected final TextWatcher mNameWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String newName = s.toString();
            if (!TextUtils.equals(mScene.name, newName)) {
                mScene.name = newName;
                mCallbacks.putScene(mObjectId, mScene);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    protected ViewHolder mViews;

    public static BaseProgramFragment newInstance(long id) {
        return init(new SceneFragment(), id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_program_scene, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks.removeSceneDataChangeListener(this);
    }

    @Override
    public void onSceneDataChange() {
        if (isDetached()) {
            return;
        }

        Scene old = mScene;
        mScene = mCallbacks.getScene(mObjectId);
        if (mScene == null) {
            removeSelf();
        }

        mPropAdapter.setProps(mCallbacks.getPropsArray(mObjectId));
        mViews.stageThumb.setAdapter(mPropAdapter);
        mViews.updateViews(mScene, old);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mScene = mCallbacks.getScene(mObjectId);
        mCallbacks.addSceneDataChangeListener(this);

        mRulesAdapter = mCallbacks.getRuleAdapter(mObjectId);
        mPropAdapter = new PropAdapter(getActivity(), mCallbacks.getPropsArray(mObjectId));

        mViews = new ViewHolder(view);
        mViews.setViewValues(mScene);
        mViews.initViews();
    }

    private void saveChanges() {
        mCallbacks.putScene(mObjectId, mScene);
    }

    @Override
    protected int getFavouredBackground() {
        return R.drawable.background_designer_program_scene;
    }

    @Override
    protected ViewHolder getViewHolder() {
        return mViews;
    }

    protected void onNewRule() {
        Rule rule = new Rule();
        final long newRuleId = mCallbacks.addRule(rule);
        rule.name = "Rule " + (newRuleId + 1);

        BooleanValue defaultCondition = new BooleanValue();
        defaultCondition.name = getString(R.string.default_condition_name);
        defaultCondition.value = true;
        rule.conditionId = mCallbacks.addOperand(defaultCondition);

        mScene.rules.add(newRuleId);
        mCallbacks.putScene(mObjectId, mScene);
        setNextFragment(RuleFragment.newInstance(newRuleId, mObjectId));

        if (mActionMode != null) {
            mActionMode.finish();
            mViews.rulesList.post(new Runnable() {
                @Override
                public void run() {
                    selectItemInList(newRuleId, mViews.rulesList);
                }
            });
        } else {
            selectItemInList(newRuleId, mViews.rulesList);
        }
    }

    protected void onRuleClick(long id) {
        setNextFragment(RuleFragment.newInstance(id, mObjectId));
    }

    private class ViewHolder extends BaseProgramFragment.ViewHolder<Scene> {

        public View editStage;

        public TextView editStagePsuedoButton;

        public EditText name;

        public View newRule;

        public DragSortListView rulesList;

        public TextView stageDetail;

        public StageView stageThumb;

        private View mEmpty;

        public ViewHolder(View view) {
            super(view);
            editStage = view.findViewById(R.id.edit_stage);
            name = (EditText) view.findViewById(R.id.name);
            newRule = view.findViewById(R.id.new_rule);
            rulesList = (DragSortListView) view.findViewById(R.id.rules);
            mEmpty = view.findViewById(android.R.id.empty);
            stageThumb = (StageView) view.findViewById(R.id.stage);
            stageDetail = (TextView) view.findViewById(R.id.stage_detail);
            editStagePsuedoButton = (TextView) view.findViewById(R.id.edit_stage_psudeo_button);
        }

        @Override
        public void initViews() {
            name.addTextChangedListener(mNameWatcher);

            editStage.setOnClickListener(mEditStageClickListener);
            stageThumb.setAdapter(mPropAdapter);
            stageThumb.setEnabled(false);

            newRule.setOnClickListener(mNewRuleClickListener);

            ProgramComponentDragSortController controller = new ProgramComponentDragSortController(
                    rulesList, R.id.handle, DragSortController.ON_DRAG, 0, 0, 0);
            rulesList.setFloatViewManager(controller);
            rulesList.setOnTouchListener(controller);
            rulesList.setAdapter(mRulesAdapter);
            rulesList.setMultiChoiceModeListener(mMultiChoiceModeCallbacks);
            rulesList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            rulesList.setOnItemClickListener(mRuleItemClickListener);
            rulesList.setOnItemLongClickListener(mRuleItemLongClickListener);
            rulesList.setEmptyView(mEmpty);
        }

        @Override
        public void setViewValues(Scene scene) {
            name.setText(scene.name);
            if (scene.orientation == -1) {
                editStagePsuedoButton.setText(R.string.action_create);
            }
            updateStageDetail(scene);
        }

        public void updateViews(Scene newScene, Scene oldScene) {
            if (!TextUtils.equals(newScene.name, oldScene.name)) {
                name.setText(newScene.name);
            }

            if (newScene.orientation == -1) {
                mViews.editStagePsuedoButton.setText(R.string.action_create);
            } else {
                mViews.editStagePsuedoButton.setText(R.string.action_edit);
            }
            updateStageDetail(newScene);
        }

        private void updateStageDetail(Scene scene) {
            if (scene.orientation == -1) {
                stageDetail.setText(getString(R.string.text_stage_undefined));
            } else {
                stageDetail
                        .setText(getResources()
                                .getString(
                                        R.string.format_stage_detail,
                                        mScene.stageWidth,
                                        mScene.stageHeight,
                                        getResources().getStringArray(
                                                R.array.orientation_options)[scene.orientation],
                                        mScene.props.size()));
            }
            stageThumb.setNativeWidth(mScene.stageWidth);
            stageThumb.setNativeHeight(mScene.stageHeight);
        }
    }
}
