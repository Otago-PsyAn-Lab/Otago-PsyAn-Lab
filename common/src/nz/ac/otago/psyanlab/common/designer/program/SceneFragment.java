
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.SceneDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Rule;
import nz.ac.otago.psyanlab.common.model.Scene;

import android.content.res.Resources;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class SceneFragment extends BaseProgramFragment implements SceneDataChangeListener {
    public static BaseProgramFragment newInstance(long id) {
        return init(new SceneFragment(), id);
    }

    public OnClickListener mEditStageClickListener = new OnClickListener() {
        public void onClick(View v) {
            mCallbacks.editStage(mObjectId);
        }
    };

    public OnItemClickListener mOnRuleItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mViews.rulesList.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
                mViews.rulesList.setItemChecked(position, true);
            } else if (mViews.rulesList.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
                onRuleClick(id);
            }
        }
    };

    private TextWatcher mNameWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String newName = s.toString();
            if (!TextUtils.equals(mScene.name, newName)) {
                mScene.name = newName;
                mCallbacks.updateScene(mObjectId, mScene);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    private OnClickListener mNewRuleClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onNewRule();
        }
    };

    private ListAdapter mRulesAdapter;

    private Scene mScene;

    private ViewHolder mViews;

    protected ActionMode mActionMode;

    protected OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
            if (mActionMode != null) {
                return false;
            }
            mViews.rulesList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mViews.rulesList.setItemChecked(position, true);
            setNextFragment(null);
            return true;
        }
    };

    protected MultiChoiceModeListener mMultiChoiceModeCallbacks = new MultiChoiceModeListener() {
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
                mCallbacks.updateScene(mObjectId, mScene);
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
                    mViews.rulesList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                    // FIXME: Marked checked the current selected loop.
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

    protected OnItemSelectedListener mOnOrientationSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mScene.orientation = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_program_scene, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks.removeSceneDataChangeListener(this);
        saveChanges();
    }

    @Override
    public void onSceneDataChange() {
        Scene old = mScene;
        mScene = mCallbacks.getScene(mObjectId);
        if (mScene == null) {
            removeSelf();
        }

        mViews.updateViews(mScene, old);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mScene = mCallbacks.getScene(mObjectId);
        mCallbacks.addSceneDataChangeListener(this);

        mRulesAdapter = mCallbacks.getRuleAdapter(mObjectId);

        mViews = new ViewHolder(getResources(), view);
        mViews.setViewValues(mScene);
        mViews.initViews();
    }

    private void saveChanges() {
        mCallbacks.updateScene(mObjectId, mScene);
    }

    protected void onNewRule() {
        Rule rule = new Rule();
        final long newRuleId = mCallbacks.createRule(rule);
        rule.name = "Rule " + (newRuleId + 1);
        mScene.rules.add(newRuleId);
        mCallbacks.updateScene(mObjectId, mScene);
        setNextFragment(RuleFragment.newInstance(newRuleId));

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
        setNextFragment(RuleFragment.newInstance(id));
    }

    private class ViewHolder {
        public Button editStage;

        public EditText name;

        public Button newRule;

        public ListView rulesList;

        public Spinner stageOrientation;

        public ViewHolder(Resources resources, View view) {
            editStage = (Button)view.findViewById(R.id.stage);
            name = (EditText)view.findViewById(R.id.name);
            newRule = (Button)view.findViewById(R.id.new_rule);
            rulesList = (ListView)view.findViewById(R.id.rules);
            stageOrientation = (Spinner)view.findViewById(R.id.stage_orientation);
        }

        public void initViews() {
            name.addTextChangedListener(mNameWatcher);

            editStage.setOnClickListener(mEditStageClickListener);

            newRule.setOnClickListener(mNewRuleClickListener);

            rulesList.setAdapter(mRulesAdapter);
            rulesList.setMultiChoiceModeListener(mMultiChoiceModeCallbacks);
            rulesList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            rulesList.setOnItemClickListener(mOnRuleItemClickListener);
            rulesList.setOnItemLongClickListener(mItemLongClickListener);

            stageOrientation.setOnItemSelectedListener(mOnOrientationSelectedListener);
        }

        public void setViewValues(Scene scene) {
            name.setText(scene.name);
            stageOrientation.setSelection(mScene.orientation);
        }

        public void updateViews(Scene newScene, Scene oldScene) {
            if (!TextUtils.equals(newScene.name, oldScene.name)) {
                name.setText(newScene.name);
            }
        }
    }
}
