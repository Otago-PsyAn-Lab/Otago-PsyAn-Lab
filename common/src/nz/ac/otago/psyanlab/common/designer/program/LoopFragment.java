
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.LoopDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.designer.program.generator.EditGeneratorDialogFragment;
import nz.ac.otago.psyanlab.common.designer.program.generator.EditGeneratorDialogFragment.OnGeneratorCreatedListener;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar.DialogueResultListener;
import nz.ac.otago.psyanlab.common.designer.util.NumberPickerDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.util.RequestCodes;
import nz.ac.otago.psyanlab.common.model.Loop;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class LoopFragment extends BaseProgramFragment implements LoopDataChangeListener,
        OnGeneratorCreatedListener {
    public static BaseProgramFragment newInstance(long id) {
        return init(new LoopFragment(), id);
    }

    public OnItemClickListener mOnGeneratorItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onGeneratorClick(id);
        }
    };

    public OnItemClickListener mOnSceneItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mViews.scenesList.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
                mViews.scenesList.setItemChecked(position, true);
            } else if (mViews.scenesList.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
                onSceneClick(id);
            }
        }
    };

    private ListAdapter mGeneratorAdapter;

    private OnClickListener mIterationsClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showEditIterationDialogue();
        }
    };

    private Loop mLoop;

    private TextWatcher mNameWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String newName = s.toString();
            if (!TextUtils.equals(mLoop.name, newName)) {
                mLoop.name = newName;
                mCallbacks.updateLoop(mObjectId, mLoop);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    private OnClickListener mNewGeneratorClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showNewGeneratorDialogue();
        }
    };

    private OnClickListener mNewSceneClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onNewScene();
        }
    };

    private DialogueResultListener mOnIterationPickedListener = new DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            mLoop.iterations = data.getInt(NumberPickerDialogueFragment.RESULT_PICKED_NUMBER);
            mCallbacks.updateLoop(mObjectId, mLoop);
        }
    };

    private ViewHolder mViews;

    protected ActionMode mActionMode;

    protected MultiChoiceModeListener mGeneratorMultiChoiceModeCallbacks = new MultiChoiceModeListener() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();
            boolean loopIsDirty = false;
            if (itemId == R.id.menu_delete) {
                long[] checkedItemIds = mViews.generatorsList.getCheckedItemIds();
                for (int i = 0; i < checkedItemIds.length; i++) {
                    mCallbacks.deleteGenerator(checkedItemIds[i]);
                    mLoop.generators.remove(checkedItemIds[i]);
                    loopIsDirty = true;
                }
            } else {
            }
            if (loopIsDirty) {
                mCallbacks.updateLoop(mObjectId, mLoop);
            }
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.context_program_component, menu);
            mode.setTitle(R.string.title_select_generators);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                boolean checked) {
            final int checkedCount = mViews.generatorsList.getCheckedItemCount();
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

    protected OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
            if (mActionMode != null) {
                return false;
            }
            mViews.scenesList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mViews.scenesList.setItemChecked(position, true);
            mScenesAdapter.fixItemBackground(R.drawable.loop_activated_background);
            setNextFragment(null);
            return true;
        }
    };

    protected MultiChoiceModeListener mSceneMultiChoiceModeCallbacks = new MultiChoiceModeListener() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();
            boolean loopIsDirty = false;
            if (itemId == R.id.menu_delete) {
                long[] checkedItemIds = mViews.scenesList.getCheckedItemIds();
                for (int i = 0; i < checkedItemIds.length; i++) {
                    mCallbacks.deleteScene(checkedItemIds[i]);
                    mLoop.scenes.remove(checkedItemIds[i]);
                    loopIsDirty = true;
                }
            } else {
            }
            if (loopIsDirty) {
                mCallbacks.updateLoop(mObjectId, mLoop);
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
            mViews.scenesList.post(new Runnable() {
                @Override
                public void run() {
                    mScenesAdapter.fixItemBackground(R.drawable.loop_activated_background_arrow);
                    mViews.scenesList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                }
            });
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                boolean checked) {
            final int checkedCount = mViews.scenesList.getCheckedItemCount();
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

    ProgramComponentAdapter<Scene> mScenesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_designer_program_loop, container, false);
        ListView list = (ListView)v.findViewById(R.id.generators);
        list.addHeaderView(inflater.inflate(R.layout.loop_header_content, list, false));
        list.addFooterView(inflater.inflate(R.layout.action_footer_content, list, false));
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks.removeLoopDataChangeListener(this);
        saveChanges();
    }

    @Override
    public void onGeneratorCreated(long id) {
        mLoop.generators.add(id);
        mCallbacks.updateLoop(mObjectId, mLoop);
    }

    @Override
    public void onLoopDataChange() {
        Loop old = mLoop;
        mLoop = mCallbacks.getLoop(mObjectId);
        if (mLoop == null) {
            removeSelf();
            return;
        }

        mViews.updateViews(mLoop, old);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoop = mCallbacks.getLoop(mObjectId);
        mCallbacks.addLoopDataChangeListener(this);
        mCallbacks.registerDialogueResultListener(RequestCodes.ITERATION_NUMBER,
                mOnIterationPickedListener);

        mScenesAdapter = mCallbacks.getScenesAdapter(mObjectId);
        mGeneratorAdapter = mCallbacks.getGeneratorAdapter(mObjectId);

        mViews = new ViewHolder(getResources(), view);
        mViews.setViewValues(mLoop);
        mViews.initViews();
    }

    @Override
    public void setIsLastInList(boolean isLastInList) {
        if (isLastInList) {
            mBackgroundResource = R.drawable.opal_list_background_flat;
            if (mViews != null) {
                mViews.background.setBackgroundResource(mBackgroundResource);
            }
        } else {
            mBackgroundResource = R.drawable.loop_background_flat;
            if (mViews != null) {
                mViews.background.setBackgroundResource(mBackgroundResource);
            }
        }
    }

    private void saveChanges() {
        mCallbacks.updateLoop(mObjectId, mLoop);
    }

    @Override
    protected int getFavouredBackground() {
        return R.drawable.loop_background_flat;
    }

    @Override
    protected ViewHolder getViewHolder() {
        return mViews;
    }

    protected void onGeneratorClick(long id) {
        showEditGeneratorDialogue(id);
    }

    protected void onNewScene() {
        Scene scene = new Scene();
        final long newSceneId = mCallbacks.createScene(scene);
        scene.name = "Scene " + (newSceneId + 1);
        mLoop.scenes.add(newSceneId);
        mCallbacks.updateLoop(mObjectId, mLoop);
        setNextFragment(SceneFragment.newInstance(newSceneId));

        if (mActionMode != null) {
            mActionMode.finish();
            mViews.scenesList.post(new Runnable() {
                @Override
                public void run() {
                    selectItemInList(newSceneId, mViews.scenesList);
                }
            });
        } else {
            selectItemInList(newSceneId, mViews.scenesList);
        }
    }

    protected void onSceneClick(long id) {
        setNextFragment(SceneFragment.newInstance(id));
        // requestMoveToView(mViews.scenesList);
    }

    protected void showEditGeneratorDialogue(long id) {
        if (mActionMode != null) {
            mActionMode.finish();
        }

        EditGeneratorDialogFragment dialog = EditGeneratorDialogFragment.newDialog(id);
        dialog.setOnGeneratorActionListener(this);

        // Dirty hack to stop horizontal scroller from jumping around too much.
        mViews.name.requestFocus();
        mViews.name.clearFocus();
        dialog.show(getChildFragmentManager(), "dialog_edit_generator");
    }

    protected void showEditIterationDialogue() {
        if (mActionMode != null) {
            mActionMode.finish();
        }

        NumberPickerDialogueFragment dialog = NumberPickerDialogueFragment.newDialog(
                R.string.title_edit_iterations, mLoop.iterations, 0, RequestCodes.ITERATION_NUMBER);
        dialog.show(getChildFragmentManager(), "dialog_edit_iteration");
    }

    protected void showNewGeneratorDialogue() {
        showEditGeneratorDialogue(-1);
    }

    public class ViewHolder extends BaseProgramFragment.ViewHolder<Loop> {
        public ListView generatorsList;

        public Button iterations;

        public Resources mResources;

        public EditText name;

        public View newGenerator;

        public View newScene;

        public ListView scenesList;

        public ViewHolder(Resources resources, View view) {
            super(view);
            mResources = resources;

            generatorsList = (ListView)view.findViewById(R.id.generators);

            iterations = (Button)view.findViewById(R.id.iterations);
            name = (EditText)view.findViewById(R.id.name);
            newGenerator = view.findViewById(R.id.new_generator);
            newScene = view.findViewById(R.id.new_scene);
            scenesList = (ListView)view.findViewById(R.id.scenes);
        }

        public void initViews() {
            iterations.setOnClickListener(mIterationsClickListener);

            name.addTextChangedListener(mNameWatcher);

            newGenerator.setOnClickListener(mNewGeneratorClickListener);

            newScene.setOnClickListener(mNewSceneClickListener);

            scenesList.setAdapter(mScenesAdapter);
            scenesList.setMultiChoiceModeListener(mSceneMultiChoiceModeCallbacks);
            scenesList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            scenesList.setOnItemClickListener(mOnSceneItemClickListener);
            scenesList.setOnItemLongClickListener(mItemLongClickListener);
            scenesList.setDivider(null);

            generatorsList.setAdapter(mGeneratorAdapter);
            generatorsList.setOnItemClickListener(mOnGeneratorItemClickListener);
            generatorsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            generatorsList.setMultiChoiceModeListener(mGeneratorMultiChoiceModeCallbacks);
            generatorsList.setDivider(null);
        }

        public void setViewValues(Loop loop) {
            name.setText(loop.name);

            setIterationsText(loop);
        }

        public void updateViews(Loop newLoop, Loop oldLoop) {
            if (!TextUtils.equals(newLoop.name, oldLoop.name)) {
                name.setText(newLoop.name);
            }
            setIterationsText(newLoop);
        }

        private void setIterationsText(Loop loop) {
            iterations.setText(String.valueOf(loop.iterations));
            // if (loop.iterations == 1) {
            // iterations.setText(R.string.label_iteration);
            // } else {
            // iterations.setText(mResources.getString(R.string.label_format_iterations,
            // loop.iterations));
            // }
        }
    }
}
