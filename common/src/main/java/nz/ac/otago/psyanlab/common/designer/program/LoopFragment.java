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

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.LoopDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.program.generator.EditGeneratorDialogFragment;
import nz.ac.otago.psyanlab.common.designer.program.generator.EditGeneratorDialogFragment.OnGeneratorCreatedListener;
import nz.ac.otago.psyanlab.common.designer.program.util.LinkLoopDataSourceDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.util.DialogueRequestCodes;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar.DialogueResultListener;
import nz.ac.otago.psyanlab.common.designer.util.NumberPickerDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.model.Loop;
import nz.ac.otago.psyanlab.common.model.Scene;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

public class LoopFragment extends BaseProgramFragment
        implements LoopDataChangeListener, OnGeneratorCreatedListener {

    public static BaseProgramFragment newInstance(long id) {
        return init(new LoopFragment(), id);
    }

    public static class SteppingKindAdapter extends BaseAdapter
            implements ListAdapter, SpinnerAdapter {
        final static private long[] sTypes =
                new long[]{Loop.STEPPING_KIND_SEQUENTIAL, Loop.STEPPING_KIND_SHUFFLE,
                           Loop.STEPPING_KIND_RANDOM};

        public static int positionOf(int type) {
            for (int i = 0; i < sTypes.length; i++) {
                if (sTypes[i] == type) {
                    return i;
                }
            }
            throw new RuntimeException("Unknown stepping kind " + type);
        }

        private Context mContext;

        private LayoutInflater mInflater;

        public SteppingKindAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return sTypes.length;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder) convertView.getTag();
            }

            CharSequence typeString = getSteppingKindString(mContext, sTypes[position]);

            holder.textViews[0].setText(typeString);

            return convertView;
        }

        @Override
        public Long getItem(int position) {
            return sTypes[position];
        }

        @Override
        public long getItemId(int position) {
            return sTypes[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder) convertView.getTag();
            }

            holder.textViews[0].setText(getSteppingKindString(mContext, sTypes[position]));

            return convertView;
        }

        private String getSteppingKindString(Context context, long steppingKind) {
            if (steppingKind == Loop.STEPPING_KIND_RANDOM) {
                return context.getString(R.string.label_stepping_kind_random);
            } else if (steppingKind == Loop.STEPPING_KIND_SEQUENTIAL) {
                return context.getString(R.string.label_stepping_kind_sequential);
            } else if (steppingKind == Loop.STEPPING_KIND_SHUFFLE) {
                return context.getString(R.string.label_stepping_kind_shuffle);
            } else {
                throw new RuntimeException("Unknown stepping kind " + steppingKind);
            }
        }
    }

    protected ActionMode mActionMode;

    protected OnItemClickListener mGeneratorItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onGeneratorClick(id);
        }
    };

    protected OnClickListener mIterationsClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showEditIterationDialogue();
        }
    };

    protected OnClickListener mLinkSourceClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            showLinkLoopDataSourceDialogue();
            ;
        }
    };

    protected TextWatcher mNameWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String newName = s.toString();
            if (!TextUtils.equals(mLoop.name, newName)) {
                mLoop.name = newName;
                mCallbacks.putLoop(mObjectId, mLoop);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    protected OnClickListener mNewSceneClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onNewScene();
        }
    };

    protected DialogueResultListener mOnIterationPickedListener = new DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            mLoop.iterations = data.getInt(NumberPickerDialogueFragment.RESULT_PICKED_NUMBER);
            mCallbacks.putLoop(mObjectId, mLoop);
        }

        @Override
        public void onResultCancel() {
        }

        @Override
        public void onResultDelete(Bundle data) {
        }
    };

    protected OnItemClickListener mSceneItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mViews.scenesList.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
                mViews.scenesList.setItemChecked(position, true);
            } else if (mViews.scenesList.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
                onSceneClick(id);

                for (int i = 0; i < mViews.scenesList.getChildCount(); i++) {
                    View handle = mViews.scenesList.getChildAt(i).findViewById(R.id.handle);
                    if (handle != null) {
                        handle.setEnabled(false);
                        handle.setVisibility(View.GONE);
                    }
                }

                View handle = mViews.scenesList.getChildAt(
                        position - mViews.scenesList.getFirstVisiblePosition() +
                        mViews.scenesList.getHeaderViewsCount()).findViewById(R.id.handle);
                if (handle != null) {
                    handle.setEnabled(true);
                    handle.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    protected OnItemLongClickListener mSceneItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
            if (mActionMode != null) {
                return false;
            }
            mViews.scenesList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            mViews.scenesList.setItemChecked(position, true);
            mScenesAdapter.fixItemBackground(R.drawable.loop_activated_background);
            setNextFragment(null);

            for (int i = 0; i < mViews.scenesList.getChildCount(); i++) {
                View handle = mViews.scenesList.getChildAt(i).findViewById(R.id.handle);
                if (handle != null) {
                    handle.setEnabled(false);
                    handle.setVisibility(View.GONE);
                }
            }

            mViews.scenesList.setDragEnabled(false);

            return true;
        }
    };

    protected MultiChoiceModeListener mSceneMultiChoiceModeCallbacks =
            new MultiChoiceModeListener() {
                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    int itemId = item.getItemId();
                    boolean loopIsDirty = false;
                    if (itemId == R.id.menu_delete) {
                        long[] checkedItemIds = mViews.scenesList.getCheckedItemIds();
                        for (long checkedItemId : checkedItemIds) {
                            mCallbacks.deleteScene(checkedItemId);
                            mLoop.scenes.remove(checkedItemId);
                            loopIsDirty = true;
                        }
                    }
                    if (loopIsDirty) {
                        mCallbacks.putLoop(mObjectId, mLoop);
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
                            mScenesAdapter
                                    .fixItemBackground(R.drawable.loop_activated_background_arrow);
                            mViews.scenesList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                            mViews.scenesList.setDragEnabled(true);
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

    protected ProgramComponentAdapter<Scene> mScenesAdapter;

    protected SpinnerAdapter mSteppingKindAdapter;

    protected AdapterView.OnItemSelectedListener mSteppingKindSelectedListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mLoop.steppingKind = l;
                    mCallbacks.putLoop(mObjectId, mLoop);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            };

    private Loop mLoop;

    private ViewHolder mViews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_program_loop, container, false);
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
        mCallbacks.putLoop(mObjectId, mLoop);
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
        mCallbacks.registerDialogueResultListener(DialogueRequestCodes.ITERATION_NUMBER,
                                                  mOnIterationPickedListener);

        mScenesAdapter = mCallbacks.getScenesAdapter(mObjectId);
        mSteppingKindAdapter = new SteppingKindAdapter(getActivity());

        mViews = new ViewHolder(view);
        mViews.setViewValues(mLoop);
        mViews.initViews();
    }

    @Override
    protected int getFavouredBackground() {
        return R.drawable.background_designer_program_loop;
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
        final long newSceneId = mCallbacks.addScene(scene);
        scene.name = getActivity().getString(R.string.default_name_new_scene, newSceneId + 1);
        mLoop.scenes.add(newSceneId);
        mCallbacks.putLoop(mObjectId, mLoop);
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

        NumberPickerDialogueFragment dialog = NumberPickerDialogueFragment
                .newDialogue(R.string.title_edit_iterations, mLoop.iterations, 1,
                             DialogueRequestCodes.ITERATION_NUMBER);
        dialog.show(getChildFragmentManager(), "dialog_edit_iteration");
    }

    protected void showLinkLoopDataSourceDialogue() {
        if (mActionMode != null) {
            mActionMode.finish();
        }

        LinkLoopDataSourceDialogueFragment dialogue =
                LinkLoopDataSourceDialogueFragment.newDialogue(mObjectId);
        dialogue.show(getChildFragmentManager(), "dialogue_link_data_source");
    }

    protected void showNewGeneratorDialogue() {
        showEditGeneratorDialogue(-1);
    }

    private void saveChanges() {
        mCallbacks.putLoop(mObjectId, mLoop);
    }

    public class ViewHolder extends BaseProgramFragment.ViewHolder<Loop> {
        public Button iterations;

        public Button linkSource;

        public EditText name;

        public View newScene;

        public DragSortListView scenesList;

        public Spinner steppingKind;

        private View mEmptyScenes;

        public ViewHolder(View view) {
            super(view);
            mEmptyScenes = view.findViewById(android.R.id.empty);

            iterations = (Button) view.findViewById(R.id.iterations);
            name = (EditText) view.findViewById(R.id.name);
            linkSource = (Button) view.findViewById(R.id.link_source);
            steppingKind = (Spinner) view.findViewById(R.id.stepping_kind);
            newScene = view.findViewById(R.id.new_scene);
            scenesList = (DragSortListView) view.findViewById(R.id.scenes);

            @SuppressLint("WrongViewCast")
            final GridLayout gridLayout = (GridLayout) view.findViewById(R.id.background);
            final View loopContentContainer = view.findViewById(R.id.column);
            final View scenesListContainer = view.findViewById(R.id.scenes_list_container);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    fixGridLayoutOverflow(gridLayout, loopContentContainer);
                    fixGridLayoutOverflow(gridLayout, scenesListContainer);
                }
            });
        }

        /**
         * Only init views after setting initial values.
         */
        public void initViews() {
            name.addTextChangedListener(mNameWatcher);

            linkSource.setOnClickListener(mLinkSourceClickListener);
            iterations.setOnClickListener(mIterationsClickListener);
            steppingKind.setOnItemSelectedListener(mSteppingKindSelectedListener);

            newScene.setOnClickListener(mNewSceneClickListener);

            ProgramComponentDragSortController controller =
                    new ProgramComponentDragSortController(scenesList, R.id.handle,
                                                           DragSortController.ON_DRAG, 0, 0, 0);
            scenesList.setFloatViewManager(controller);
            scenesList.setOnTouchListener(controller);
            scenesList.setAdapter(mScenesAdapter);
            scenesList.setMultiChoiceModeListener(mSceneMultiChoiceModeCallbacks);
            scenesList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            scenesList.setOnItemClickListener(mSceneItemClickListener);
            scenesList.setOnItemLongClickListener(mSceneItemLongClickListener);
            scenesList.setEmptyView(mEmptyScenes);
        }

        /**
         * Only set view values before initialising views. After that only use update views.
         */
        @Override
        public void setViewValues(Loop loop) {
            name.setText(loop.name);

            setIterationsText(loop);

            if (loop.linkedSource != -1) {
                linkSource.setText(mCallbacks.getSource(loop.linkedSource).name);
            } else {
                linkSource.setText(getString(R.string.text_no_linked_loop_data_source));
            }

            steppingKind.setAdapter(mSteppingKindAdapter);
            for (int i = 0; i < mSteppingKindAdapter.getCount(); i++) {
                if (mSteppingKindAdapter.getItemId(i) == loop.steppingKind) {
                    steppingKind.setSelection(i);
                    break;
                }
            }
        }

        public void updateViews(Loop newLoop, Loop oldLoop) {
            if (!TextUtils.equals(newLoop.name, oldLoop.name)) {
                name.setText(newLoop.name);
            }

            setIterationsText(newLoop);

            if (newLoop.linkedSource != -1) {
                linkSource.setText(mCallbacks.getSource(newLoop.linkedSource).name);
            } else {
                linkSource.setText(getString(R.string.text_no_linked_loop_data_source));
            }

            if (newLoop.steppingKind != oldLoop.steppingKind) {
                for (int i = 0; i < mSteppingKindAdapter.getCount(); i++) {
                    if (mSteppingKindAdapter.getItemId(i) == newLoop.steppingKind) {
                        steppingKind.setSelection(i);
                        break;
                    }
                }
            }
        }

        private void setIterationsText(Loop loop) {
            if (loop.iterations <= 0) {
                iterations.setText(getString(R.string.text_loop_iterations_infinite));
            } else {
                iterations.setText(String.valueOf(loop.iterations));
            }
        }
    }
}
