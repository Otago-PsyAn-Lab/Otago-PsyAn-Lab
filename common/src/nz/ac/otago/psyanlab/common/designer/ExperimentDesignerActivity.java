/*
 Copyright (C) 2012, 2013 University of Otago, Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.common.designer;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.UserDelegateI;
import nz.ac.otago.psyanlab.common.UserExperimentDelegateI;
import nz.ac.otago.psyanlab.common.designer.EditorSectionManager.EditorSectionItem;
import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter.ViewBinder;
import nz.ac.otago.psyanlab.common.designer.assets.AssetTabFragmentsCallbacks;
import nz.ac.otago.psyanlab.common.designer.assets.AssetsFragment;
import nz.ac.otago.psyanlab.common.designer.assets.ImportAssetActivity;
import nz.ac.otago.psyanlab.common.designer.meta.MetaFragment;
import nz.ac.otago.psyanlab.common.designer.meta.MetaFragment.Details;
import nz.ac.otago.psyanlab.common.designer.program.ProgramFragment;
import nz.ac.otago.psyanlab.common.designer.program.object.PickObjectDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.program.stage.StageActivity;
import nz.ac.otago.psyanlab.common.designer.program.util.ProgramCallbacks;
import nz.ac.otago.psyanlab.common.designer.subject.SubjectFragment;
import nz.ac.otago.psyanlab.common.designer.util.ArrayFragmentMapAdapter;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.EventAdapter;
import nz.ac.otago.psyanlab.common.designer.util.ExperimentObjectAdapter;
import nz.ac.otago.psyanlab.common.designer.util.LongSparseArrayAdapter;
import nz.ac.otago.psyanlab.common.designer.util.MethodAdapter;
import nz.ac.otago.psyanlab.common.designer.util.MethodAdapter.MethodData;
import nz.ac.otago.psyanlab.common.model.Action;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.Experiment;
import nz.ac.otago.psyanlab.common.model.ExperimentObject;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference.ExperimentObjectFilter;
import nz.ac.otago.psyanlab.common.model.Generator;
import nz.ac.otago.psyanlab.common.model.LandingPage;
import nz.ac.otago.psyanlab.common.model.Loop;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.Rule;
import nz.ac.otago.psyanlab.common.model.Scene;
import nz.ac.otago.psyanlab.common.model.generator.Random;
import nz.ac.otago.psyanlab.common.model.generator.Shuffle;
import nz.ac.otago.psyanlab.common.model.operand.kind.CallOperand;
import nz.ac.otago.psyanlab.common.model.util.EventId;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.ModelUtils;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.OperandHolder;
import nz.ac.otago.psyanlab.common.util.Args;
import nz.ac.otago.psyanlab.common.util.ConfirmDialogFragment;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.LongSparseArray;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Provides an interface to the fragments implementing the UI whereby they can
 * manipulate the experiment data.
 */
public class ExperimentDesignerActivity extends FragmentActivity implements MetaFragment.Callbacks,
        SubjectFragment.Callbacks, AssetTabFragmentsCallbacks, ProgramCallbacks,
        DialogueResultCallbacks {

    public static final int OPERAND_ACCESS_SCOPE_ACTION = 0x01;

    public static final int OPERAND_ACCESS_SCOPE_EXPRESSION = 0x03;

    public static final int OPERAND_ACCESS_SCOPE_OPERAND = 0x02;

    private static final String ADAPTER_STATE = "adapter_state";

    private static final int MODE_EDIT = 0x02;

    private static final int MODE_NEW = 0x01;

    private static final int REQUEST_ASSET_IMPORT = 0x02;

    private static final int REQUEST_EDIT_STAGE = 0x03;

    private ArrayList<ActionDataChangeListener> mActionDataChangeListeners;

    private ViewBinder<Action> mActionListItemViewBinder = new ViewBinder<Action>() {
        @Override
        public View bind(Action action, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_action, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            holder.textViews[0].setText(action.name);
            return convertView;
        }
    };

    private ArrayList<AssetDataChangeListener> mAssetDataChangeListeners;

    private AssetsAdapter mAssetsAdapter;

    private Pair<Long, ProgramComponentAdapter<Action>> mCurrentActionAdapter;

    private Pair<Long, ProgramComponentAdapter<Generator>> mCurrentGeneratorAdapter;

    private Pair<Long, ProgramComponentAdapter<Operand>> mCurrentParameterAdapter;

    private Pair<Long, ProgramComponentAdapter<Rule>> mCurrentRuleAdapter;

    private Pair<Long, ProgramComponentAdapter<Scene>> mCurrentSceneAdapter;

    private SparseArray<DialogueResultListener> mDialogueResultListeners;

    private ListView mDrawerList;

    private CharSequence mDrawerTitle;

    private ActionBarDrawerToggle mDrawerToggle;

    private Experiment mExperiment;

    private UserExperimentDelegateI mExperimentDelegate;

    private ExperimentHolderFragment mExperimentHolderFragment;

    private ArrayList<GeneratorDataChangeListener> mGeneratorDataChangeListeners;

    private ViewBinder<Generator> mGeneratorListItemViewBinder = new ViewBinder<Generator>() {
        @Override
        public View bind(Generator generator, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_generator, parent,
                        false);
                holder = new TextViewHolder(3);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                holder.textViews[1] = (TextView)convertView.findViewById(android.R.id.text2);
                holder.textViews[2] = (TextView)convertView.findViewById(R.id.type);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            holder.textViews[0].setText(generator.name);
            holder.textViews[1].setText(generator.start + " – " + generator.end);

            if (generator instanceof Random) {
                holder.textViews[2].setText(R.string.generator_kind_random);
            } else if (generator instanceof Shuffle) {
                holder.textViews[2].setText(R.string.generator_kind_shuffle);
            }
            return convertView;
        }

    };

    private ArrayList<LandingPageDataChangeListener> mLandingPageDataChangeListeners;

    private ProgramComponentAdapter<Loop> mLoopAdapter;

    private ArrayList<LoopDataChangeListener> mLoopDataChangeListeners;

    private ViewBinder<Loop> mLoopListItemViewBinder = new ViewBinder<Loop>() {
        @Override
        public View bind(Loop loop, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_loop, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            holder.textViews[0].setText(loop.name);

            return convertView;
        }
    };

    private int mMode;

    private OnItemClickListener mOnDrawerListItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            doSelectSection(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    };

    private LongSparseArray<LongSparseArray<ProgramComponentAdapter<Operand>>> mOperandAdapters = new LongSparseArray<LongSparseArray<ProgramComponentAdapter<Operand>>>();

    private ArrayList<OperandDataChangeListener> mOperandDataChangeListeners;

    private ViewBinder<Operand> mOperandListItemViewBinder = new ViewBinder<Operand>() {
        @Override
        public View bind(Operand operand, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater()
                        .inflate(R.layout.list_item_operand, parent, false);
                holder = new TextViewHolder(3);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                holder.textViews[1] = (TextView)convertView.findViewById(android.R.id.text2);
                holder.textViews[2] = (TextView)convertView.findViewById(R.id.type);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            if (operand instanceof CallOperand) {
                CallOperand callOperand = (CallOperand)operand;
                ExperimentObject experimentObject = getExperimentObject(callOperand
                        .getActionObject());
                final NameResolverFactory nameFactory = ModelUtils
                        .getMethodNameFactory(experimentObject.getClass());

                holder.textViews[1].setVisibility(View.VISIBLE);
                holder.textViews[1].setText(experimentObject
                        .getPrettyName(ExperimentDesignerActivity.this));
                holder.textViews[0].setText(nameFactory.getResId(callOperand.getActionMethod()));
            } else {
                holder.textViews[0].setText(operand.getName());
                holder.textViews[1].setVisibility(View.GONE);
            }
            holder.textViews[2].setText(TextUtils.join("\n",
                    Operand.typeToStringArray(ExperimentDesignerActivity.this, operand.getType())));
            return convertView;
        }
    };

    private ArrayList<RuleDataChangeListener> mRuleDataChangeListeners;

    private ViewBinder<Rule> mRuleListItemViewBinder = new ViewBinder<Rule>() {
        @Override
        public View bind(Rule rule, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_rule, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            holder.textViews[0].setText(rule.name);
            return convertView;
        }
    };

    private ArrayList<SceneDataChangeListener> mSceneDataChangeListeners;

    private ViewBinder<Scene> mSceneListItemViewBinder = new ViewBinder<Scene>() {
        @Override
        public View bind(Scene scene, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_scene, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            holder.textViews[0].setText(scene.name);
            return convertView;
        }
    };

    private EditorSectionManager mSectionManager;

    private CharSequence mTitle;

    private UserDelegateI mUserDelegate;

    protected DrawerLayout mDrawerLayout;

    @Override
    public void addActionDataChangeListener(ActionDataChangeListener listener) {
        if (mActionDataChangeListeners == null) {
            mActionDataChangeListeners = new ArrayList<ActionDataChangeListener>();
        }
        mActionDataChangeListeners.add(listener);
    }

    @Override
    public void addAssetDataChangeListener(AssetDataChangeListener listener) {
        if (mAssetDataChangeListeners == null) {
            mAssetDataChangeListeners = new ArrayList<AssetDataChangeListener>();
        }
        mAssetDataChangeListeners.add(listener);
    }

    @Override
    public void addGeneratorDataChangeListener(GeneratorDataChangeListener listener) {
        if (mGeneratorDataChangeListeners == null) {
            mGeneratorDataChangeListeners = new ArrayList<GeneratorDataChangeListener>();
        }
        mGeneratorDataChangeListeners.add(listener);
    }

    @Override
    public void addLandingPageDataChangeListener(LandingPageDataChangeListener listener) {
        if (mLandingPageDataChangeListeners == null) {
            mLandingPageDataChangeListeners = new ArrayList<LandingPageDataChangeListener>();
        }
        mLandingPageDataChangeListeners.add(listener);
    }

    @Override
    public void addLoopDataChangeListener(LoopDataChangeListener listener) {
        if (mLoopDataChangeListeners == null) {
            mLoopDataChangeListeners = new ArrayList<LoopDataChangeListener>();
        }
        mLoopDataChangeListeners.add(listener);
    }

    @Override
    public void addOperandDataChangeListener(OperandDataChangeListener listener) {
        if (mOperandDataChangeListeners == null) {
            mOperandDataChangeListeners = new ArrayList<OperandDataChangeListener>();
        }
        mOperandDataChangeListeners.add(listener);
    }

    @Override
    public void addRuleDataChangeListener(RuleDataChangeListener listener) {
        if (mRuleDataChangeListeners == null) {
            mRuleDataChangeListeners = new ArrayList<RuleDataChangeListener>();
        }
        mRuleDataChangeListeners.add(listener);
    }

    @Override
    public void addSceneDataChangeListener(SceneDataChangeListener listener) {
        if (mSceneDataChangeListeners == null) {
            mSceneDataChangeListeners = new ArrayList<SceneDataChangeListener>();
        }
        mSceneDataChangeListeners.add(listener);
    }

    @Override
    public long createAction(Action action) {
        long unusedKey = findUnusedKey(mExperiment.actions);
        mExperiment.actions.put(unusedKey, action);
        notifyActionDataChangeListeners();
        return unusedKey;
    }

    @Override
    public long createGenerator(Generator generator) {
        Long unusedKey = findUnusedKey(mExperiment.generators);
        mExperiment.generators.put(unusedKey, generator);
        notifyGeneratorDataChangeListeners();
        return unusedKey;
    }

    @Override
    public long createLoop(Loop loop) {
        Long key = findUnusedKey(mExperiment.loops);
        mExperiment.loops.put(key, loop);
        mExperiment.program.loops.add(key);
        notifyLoopAdapter();
        notifyLoopDataChangeListeners();
        return key;
    }

    @Override
    public long createOperand(Operand operand) {
        Long key = findUnusedKey(mExperiment.operands);
        mExperiment.operands.put(key, operand);
        notifyOperandDataChangeListeners();
        return key;
    }

    @Override
    public long createRule(Rule rule) {
        Long unusedKey = findUnusedKey(mExperiment.rules);
        mExperiment.rules.put(unusedKey, rule);
        notifyRuleDataChangeListeners();
        return unusedKey;
    }

    @Override
    public long createScene(Scene scene) {
        Long unusedKey = findUnusedKey(mExperiment.scenes);
        mExperiment.scenes.put(unusedKey, scene);
        notifySceneDataChangeListeners();
        return unusedKey;
    }

    @Override
    public void deleteAction(long id) {
        mExperiment.actions.remove(id);
        notifyActionDataChangeListeners();
    }

    @Override
    public void deleteAsset(long id) {
        if (mExperiment.assets.indexOfKey(id) >= 0) {
            mExperiment.assets.remove(id);
            mAssetsAdapter.notifyDataSetChanged();
            notifyAssetDataChangeListeners();
        }
    }

    @Override
    public void deleteGenerator(long id) {
        deleteGeneratorData(id);
        notifyGeneratorDataChangeListeners();
    }

    @Override
    public void deleteLoop(long id) {
        deleteLoopData(id);
        notifyLoopAdapter();
        notifyLoopDataChangeListeners();
    }

    @Override
    public void deleteOperand(long id) {
        deleteOperandData(id);
        notifyOperandDataChangeListeners();
    }

    @Override
    public void deleteRule(long id) {
        deleteRuleData(id);
        notifyRuleDataChangeListeners();
    }

    @Override
    public void deleteScene(long id) {
        deleteSceneData(id);
        notifySceneDataChangeListeners();
    }

    @Override
    public void discardOperandAdapter(ProgramComponentAdapter<Operand> adapter) {
        for (int i = 0; i < mOperandAdapters.size(); i++) {
            // Check scope level.
            long scopeKey = mOperandAdapters.keyAt(i);
            LongSparseArray<ProgramComponentAdapter<Operand>> operandAdaptersInScope = mOperandAdapters
                    .get(scopeKey);

            for (int j = 0; j < operandAdaptersInScope.size(); j++) {
                // Check adapters currently existing for operand parents.
                long key = operandAdaptersInScope.keyAt(j);
                if (operandAdaptersInScope.get(key) == adapter) {
                    operandAdaptersInScope.remove(key);
                    if (operandAdaptersInScope.size() == 0) {
                        mOperandAdapters.remove(scopeKey);
                    }
                    return;
                }
            }

        }
    }

    @Override
    public void displayAsset(long id) {
        // TODO: Detail activity here.
    }

    @Override
    public void doImportAsset() {
        Intent intent = new Intent(this, ImportAssetActivity.class);
        startActivityForResult(intent, REQUEST_ASSET_IMPORT);
    }

    @Override
    public void editStage(long sceneId) {
        Scene scene = mExperiment.scenes.get(sceneId);
        Intent intent = new Intent(this, StageActivity.class);

        intent.putExtra(Args.EXPERIMENT_PROPS, getPropsArray(sceneId));
        intent.putExtra(Args.SCENE_ID, sceneId);
        intent.putExtra(Args.STAGE_WIDTH, scene.stageWidth);
        intent.putExtra(Args.STAGE_HEIGHT, scene.stageHeight);

        if (scene.orientation != -1) {
            intent.putExtra(Args.STAGE_ORIENTATION, scene.orientation);
        }

        startActivityForResult(intent, REQUEST_EDIT_STAGE);
    }

    @Override
    public Action getAction(long id) {
        return mExperiment.actions.get(id);
    }

    @Override
    public ProgramComponentAdapter<Action> getActionAdapter(long ruleId) {
        if (mCurrentActionAdapter != null && mCurrentActionAdapter.first == ruleId) {
            return mCurrentActionAdapter.second;
        }

        ProgramComponentAdapter<Action> adapter = new ProgramComponentAdapter<Action>(
                mExperiment.actions, mExperiment.rules.get(ruleId).actions,
                mActionListItemViewBinder);
        mCurrentActionAdapter = new Pair<Long, ProgramComponentAdapter<Action>>(ruleId, adapter);

        return adapter;
    }

    @Override
    public Asset getAsset(long id) {
        return mExperiment.assets.get(id);
    }

    @Override
    public StickyGridHeadersSimpleAdapter getAssetsAdapter() {
        return mAssetsAdapter;
    }

    /**
     * Get an adapter containing all methods which register listeners for events
     * emitted by the given class.
     * 
     * @param clazz Class with the events that are emitted.
     * @return Events ListAdapter.
     */
    @Override
    public SpinnerAdapter getEventsAdapter(final Class<?> clazz) {
        // Obtain the name factory to pull the internationalised event names.
        SortedSet<EventId> filteredEvents;

        final NameResolverFactory nameFactory = ModelUtils.getEventNameFactory(clazz);

        filteredEvents = new TreeSet<EventId>(new Comparator<EventId>() {
            @Override
            public int compare(EventId lhs, EventId rhs) {
                Collator collator = getCollater();
                return collator.compare(getString(nameFactory.getResId(lhs.value())),
                        getString(nameFactory.getResId(rhs.value())));
            }
        });

        Method[] methods = clazz.getMethods();

        // Filter methods for those which register listeners for events.
        for (int i = 0; i < methods.length; i++) {
            EventId annotation = methods[i].getAnnotation(EventId.class);
            if (annotation != null) {
                filteredEvents.add(annotation);
            }
        }

        return new EventAdapter(this, filteredEvents, nameFactory);
    }

    @Override
    public Details getExperimentDetails() {
        Details ed = new Details();
        ed.name = mExperiment.name;
        ed.authors = mExperiment.authors;
        ed.description = mExperiment.description;
        ed.version = String.valueOf(mExperiment.version);
        return ed;
    }

    @Override
    public ExperimentObject getExperimentObject(ExperimentObjectReference object) {
        switch (object.kind) {
            case ExperimentObjectReference.KIND_ASSET:
                return mExperiment.assets.get(object.id);
            case ExperimentObjectReference.KIND_GENERATOR:
                return mExperiment.generators.get(object.id);
            case ExperimentObjectReference.KIND_LOOP:
                return mExperiment.loops.get(object.id);
            case ExperimentObjectReference.KIND_PROP:
                return mExperiment.props.get(object.id);
            case ExperimentObjectReference.KIND_SCENE:
                return mExperiment.scenes.get(object.id);
            default:
            case ExperimentObjectReference.KIND_EXPERIMENT:
                return null;
        }
    }

    @Override
    public Generator getGenerator(long id) {
        return mExperiment.generators.get(id);
    }

    @Override
    public ProgramComponentAdapter<Generator> getGeneratorAdapter(long loopId) {
        if (mCurrentGeneratorAdapter != null && mCurrentGeneratorAdapter.first == loopId) {
            return mCurrentGeneratorAdapter.second;
        }

        ProgramComponentAdapter<Generator> adapter = new ProgramComponentAdapter<Generator>(
                mExperiment.generators, mExperiment.loops.get(loopId).generators,
                mGeneratorListItemViewBinder);
        mCurrentGeneratorAdapter = new Pair<Long, ProgramComponentAdapter<Generator>>(loopId,
                adapter);

        return adapter;
    }

    @Override
    public LandingPage getLandingPage() {
        return mExperiment.landingPage;
    }

    @Override
    public Loop getLoop(long id) {
        return mExperiment.loops.get(id);
    }

    @Override
    public ProgramComponentAdapter<Loop> getLoopAdapter() {
        if (mLoopAdapter != null) {
            return mLoopAdapter;
        }

        mLoopAdapter = new ProgramComponentAdapter<Loop>(mExperiment.loops,
                mExperiment.program.loops, mLoopListItemViewBinder);

        return mLoopAdapter;
    }

    /**
     * Get an adapter for all methods in given class hierarchy which return a
     * given type.
     * 
     * @param clazz Class to fetch methods from.
     * @param returnType Type to select methods by.
     * @return
     */
    @Override
    public SpinnerAdapter getMethodsAdapter(Class<?> clazz, Class<?> returnType) {
        // SortedSet<Method> filteredMethods = new TreeSet<Method>(new
        // Comparator<Method>() {
        // @Override
        // public int compare(Method lhs, Method rhs) {
        // Collator collator = getCollater();
        // return 0;
        // // return
        // // collator.compare(getString(lhs.getAnnotation(Eve.class).value()),
        // // getString(rhs.getAnnotation(I18nName.class).value()));
        // }
        // });
        //
        // Method[] methods = clazz.getMethods();
        // for (int i = 0; i < methods.length; i++) {
        // if (methods[i].getReturnType().equals(returnType)) {
        // filteredMethods.add(methods[i]);
        // }
        // }
        // return null;

        // Obtain the name factory to pull the internationalised event names.
        SortedSet<MethodData> filteredMethods;

        final NameResolverFactory nameFactory = ModelUtils.getMethodNameFactory(clazz);

        filteredMethods = new TreeSet<MethodData>(new Comparator<MethodData>() {
            @Override
            public int compare(MethodData lhs, MethodData rhs) {
                Collator collator = getCollater();
                return collator.compare(getString(nameFactory.getResId(lhs.id.value())),
                        getString(nameFactory.getResId(rhs.id.value())));
            }
        });

        Method[] methods = clazz.getMethods();

        // Filter methods for those which register listeners for events.
        for (int i = 0; i < methods.length; i++) {
            MethodId annotation = methods[i].getAnnotation(MethodId.class);
            if (annotation != null) {
                MethodData data = new MethodData();
                data.id = annotation;
                data.method = methods[i];
                filteredMethods.add(data);
            }
        }

        return new MethodAdapter(this, filteredMethods, nameFactory);
    }

    @Override
    public ExperimentObjectAdapter getObjectSectionListAdapter(long sceneId, int section, int filter) {
        switch (section) {
            case 0:
                return new ExperimentObjectAdapter.Wrapper(ExperimentObjectReference.KIND_PROP,
                        getPropsAdapter(sceneId, filter));
            case 1:
                return getExperimentControlsAdapter(sceneId, filter);
            case 2:
                return new ExperimentObjectAdapter.Wrapper(ExperimentObjectReference.KIND_ASSET,
                        getAssetsAdapter(filter));

            default:
                return null;
        }
    }

    /**
     * Get an array of adapters for all objects accessible from the given scene.
     * 
     * @param sceneId
     * @return Array of ListAdapters
     */
    @Override
    public FragmentPagerAdapter getObjectsPagerAdapter(FragmentManager fm, long sceneId,
            ArrayFragmentMapAdapter.Factory factory) {
        List<String> stubs = new ArrayList<String>();
        stubs.add(getString(R.string.title_props));
        stubs.add(getString(R.string.title_experiment));
        stubs.add(getString(R.string.title_assets));
        return new ArrayFragmentMapAdapter(fm, factory, stubs);
    }

    @Override
    public Operand getOperand(long id) {
        return mExperiment.operands.get(id);
    }

    @Override
    /**
     * Fetch adapter per scope and parent id. If not, create any missing data structure.
     */
    public ProgramComponentAdapter<Operand> getOperandAdapter(long parentId, int scope) {
        ProgramComponentAdapter<Operand> adapter;
        LongSparseArray<ProgramComponentAdapter<Operand>> operandAdaptersInScope;

        operandAdaptersInScope = mOperandAdapters.get(scope);
        if (operandAdaptersInScope != null) {
            adapter = operandAdaptersInScope.get(parentId);
            if (adapter != null) {
                return adapter;
            }
        } else {
            operandAdaptersInScope = new LongSparseArray<ProgramComponentAdapter<Operand>>();
            mOperandAdapters.put(scope, operandAdaptersInScope);
        }

        // Create adapter for our requested scope and parent id.

        OperandHolder operandHolder;
        switch (scope) {
            case OPERAND_ACCESS_SCOPE_ACTION:
                operandHolder = mExperiment.actions.get(parentId);
                break;
            case OPERAND_ACCESS_SCOPE_OPERAND:
                operandHolder = (OperandHolder)mExperiment.operands.get(parentId);
                break;

            default:
                throw new RuntimeException("Unknown operand scope " + scope);
        }

        adapter = new ProgramComponentAdapter<Operand>(mExperiment.operands,
                operandHolder.getOperands(), mOperandListItemViewBinder);
        operandAdaptersInScope.put(parentId, adapter);

        return adapter;
    }

    @Override
    public ArrayList<Prop> getPropsArray(long stageId) {
        ArrayList<Prop> props = new ArrayList<Prop>();
        for (Long propId : mExperiment.scenes.get(stageId).props) {
            props.add(mExperiment.props.get(propId));
        }
        return props;
    }

    @Override
    public Rule getRule(long id) {
        return mExperiment.rules.get(id);
    }

    @Override
    public ProgramComponentAdapter<Rule> getRuleAdapter(long sceneId) {
        if (mCurrentRuleAdapter != null && mCurrentRuleAdapter.first == sceneId) {
            return mCurrentRuleAdapter.second;
        }

        // FIXME: NPE
        ProgramComponentAdapter<Rule> adapter = new ProgramComponentAdapter<Rule>(
                mExperiment.rules, mExperiment.scenes.get(sceneId).rules, mRuleListItemViewBinder);

        mCurrentRuleAdapter = new Pair<Long, ProgramComponentAdapter<Rule>>(sceneId, adapter);

        return adapter;
    }

    @Override
    public Scene getScene(long id) {
        return mExperiment.scenes.get(id);
    }

    @Override
    public ProgramComponentAdapter<Scene> getScenesAdapter(long loopId) {
        if (mCurrentSceneAdapter != null && mCurrentSceneAdapter.first == loopId) {
            return mCurrentSceneAdapter.second;
        }

        ProgramComponentAdapter<Scene> adapter = new ProgramComponentAdapter<Scene>(
                mExperiment.scenes, mExperiment.loops.get(loopId).scenes, mSceneListItemViewBinder);
        mCurrentSceneAdapter = new Pair<Long, ProgramComponentAdapter<Scene>>(loopId, adapter);

        return adapter;
    }

    @Override
    public File getWorkingDirectory() {
        return mExperiment.getWorkingDirectory();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_EDIT_STAGE: {
                switch (resultCode) {
                    case RESULT_OK:
                        ArrayList<Prop> props = data
                                .getParcelableArrayListExtra(Args.EXPERIMENT_PROPS);
                        long sceneId = data.getLongExtra(Args.SCENE_ID, -1);

                        int height = data.getIntExtra(Args.STAGE_HEIGHT, -1);
                        int width = data.getIntExtra(Args.STAGE_WIDTH, -1);
                        int orientation = data.getIntExtra(Args.STAGE_ORIENTATION,
                                Scene.ORIENTATION_LANDSCAPE);

                        updateStageInScene(sceneId, props, orientation, width, height);
                        break;

                    default:
                        break;
                }
                break;
            }
            case REQUEST_ASSET_IMPORT: {
                switch (resultCode) {
                    case RESULT_OK:
                        String[] paths = data.getStringArrayExtra(Args.ASSET_PATHS);
                        Time t = new Time();
                        t.setToNow();
                        mExperiment.assets.put(t.toMillis(false),
                                Asset.getFactory().newAsset(paths[0]));
                        mAssetsAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DialogFragment dialog = ConfirmDialogFragment.newInstance(R.string.title_exit_designer,
                R.string.action_save_exit, R.string.action_cancel, R.string.action_discard,
                new ConfirmDialogFragment.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        storeExperiment();
                        finish();
                        dialog.dismiss();
                    }
                }, new ConfirmDialogFragment.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                }, new ConfirmDialogFragment.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        finish();
                        dialog.dismiss();
                    }
                });
        dialog.show(getSupportFragmentManager(), ConfirmDialogFragment.TAG);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);

        mSectionManager = new EditorSectionManager(this, R.id.content_frame,
                getSupportFragmentManager());

        Log.d("asdfads", "on create");

        mSectionManager.addSection(R.string.designer_tab_properties, MetaFragment.class, null);
        mSectionManager.addSection(R.string.designer_tab_subject, SubjectFragment.class, null);
        mSectionManager.addSection(R.string.designer_tab_assets, AssetsFragment.class, null);
        mSectionManager.addSection(R.string.designer_tab_program, ProgramFragment.class, null);

        ArrayAdapter<EditorSectionItem> adapter = new SectionAdapter(this,
                R.layout.list_item_drawer, mSectionManager.getItems());

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(mOnDrawerListItemClickListener);

        Bundle extras = getIntent().getExtras();
        mUserDelegate = extras.getParcelable(Args.USER_DELEGATE);
        if (extras.containsKey(Args.USER_DELEGATE)) {
            mExperimentDelegate = extras.getParcelable(Args.USER_EXPERIMENT_DELEGATE);
        }

        mExperimentHolderFragment = restoreExperimentHolder();
        mExperiment = restoreOrCreateExperiment(mExperimentHolderFragment);

        if (mExperiment == null) {
            throw new RuntimeException("Missing experiment");
        }

        mAssetsAdapter = new AssetsAdapter(this, mExperiment.assets);

        setTitle(R.string.title_new_experiment);
        mDrawerTitle = mTitle;

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new DrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close);

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            selectSection(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // MenuInflater mi = getMenuInflater();
        // mi.inflate(R.menu.activity_experiment_designer, menu);
        return true;
    }

    @Override
    public void onDialogueResult(int requestCode, Bundle data) {
        mDialogueResultListeners.get(requestCode).onResult(data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // ExperimentUtils.convertToScreen(this, mExperiment);
    };

    @Override
    public void pickExperimentObject(long sceneId, int filter, int requestCode) {
        DialogFragment dialog = PickObjectDialogueFragment.newDialog(sceneId, filter, requestCode);
        dialog.show(getSupportFragmentManager(), PickObjectDialogueFragment.TAG);
    }

    @Override
    public void registerDialogueResultListener(int requestCode, DialogueResultListener listener) {
        if (mDialogueResultListeners == null) {
            mDialogueResultListeners = new SparseArray<DialogueResultListener>();
        }
        mDialogueResultListeners.put(requestCode, listener);
    }

    @Override
    public void removeActionDataChangeListener(ActionDataChangeListener listener) {
        mActionDataChangeListeners.remove(listener);
    }

    @Override
    public void removeAssetDataChangeListener(AssetDataChangeListener listener) {
        mAssetDataChangeListeners.remove(listener);
    }

    @Override
    public void removeGeneratorDataChangeListener(GeneratorDataChangeListener listener) {
        mGeneratorDataChangeListeners.remove(listener);
    }

    @Override
    public void removeLoopDataChangeListener(LoopDataChangeListener listener) {
        mLoopDataChangeListeners.remove(listener);
    }

    @Override
    public void removeOperandDataChangeListener(OperandDataChangeListener listener) {
        mOperandDataChangeListeners.remove(listener);
    }

    @Override
    public void removeRuleDataChangeListener(RuleDataChangeListener listener) {
        mRuleDataChangeListeners.remove(listener);
    }

    @Override
    public void removeSceneDataChangeListener(SceneDataChangeListener listener) {
        mSceneDataChangeListeners.remove(listener);
    }

    @Override
    public void setTitle(int title) {
        mTitle = getText(title);
        getActionBar().setTitle(mTitle);
    }

    @Override
    public void storeDetails(Details details) {
        mExperiment.authors = details.authors;
        mExperiment.description = details.description;
        mExperiment.name = details.name;
        try {
            mExperiment.version = Integer.parseInt(details.version);
        } catch (NumberFormatException e) {
            // Ignore invalid version input.
        }
    }

    @Override
    public void storeLandingPage(LandingPage landingPage) {
        mExperiment.landingPage = landingPage;
        notifyLandingPageDataChangeListeners();
    }

    @Override
    public void updateAction(long id, Action action) {
        mExperiment.actions.put(id, action);

        if (mCurrentParameterAdapter != null && mCurrentGeneratorAdapter.first == id) {
            notifyParameterAdapter();
        }

        updateOperandAdapterIfExists(id, OPERAND_ACCESS_SCOPE_ACTION, action);

        notifyActionDataChangeListeners();
    }

    @Override
    public void updateAsset(long id, Asset asset) {
        if (mExperiment.assets.indexOfKey(id) >= 0) {
            mExperiment.assets.put(id, asset);
            mAssetsAdapter.notifyDataSetChanged();
            notifyAssetDataChangeListeners();
        }
    }

    @Override
    public void updateGenerator(long id, Generator generator) {
        mExperiment.generators.put(id, generator);
        notifyGeneratorDataChangeListeners();
        notifyLoopDataChangeListeners();
        notifyGeneratorAdapter();
    }

    @Override
    public void updateLoop(long id, Loop loop) {
        mExperiment.loops.put(id, loop);
        notifyLoopAdapter();
        if (mCurrentSceneAdapter != null && mCurrentSceneAdapter.first == id) {
            notifySceneAdapter();
        }
        if (mCurrentGeneratorAdapter != null && mCurrentGeneratorAdapter.first == id) {
            notifyGeneratorAdapter();
        }
        notifyLoopDataChangeListeners();
    }

    @Override
    public void updateOperand(long id, Operand operand) {
        mExperiment.operands.put(id, operand);

        if (operand instanceof OperandHolder) {
            OperandHolder holder = (OperandHolder)operand;
            updateOperandAdapterIfExists(id, OPERAND_ACCESS_SCOPE_OPERAND, holder);
        }

        notifyOperandAdapters(id);

        notifyOperandDataChangeListeners();
    }

    @Override
    public void updateRule(long id, Rule rule) {
        mExperiment.rules.put(id, rule);
        notifyRuleAdapter();
        if (mCurrentActionAdapter != null && mCurrentActionAdapter.first == id) {
            notifyActionAdapter();
        }
        notifyRuleDataChangeListeners();
    }

    @Override
    public void updateScene(long id, Scene scene) {
        mExperiment.scenes.put(id, scene);
        notifySceneAdapter();
        if (mCurrentGeneratorAdapter != null && mCurrentGeneratorAdapter.first == id) {
            notifyGeneratorAdapter();
        }
        if (mCurrentRuleAdapter != null && mCurrentRuleAdapter.first == id) {
            notifyRuleAdapter();
        }
        notifySceneDataChangeListeners();
    }

    private void deleteActionData(Long id) {
        mExperiment.actions.remove(id);
    }

    private void deleteGeneratorData(Long id) {
        mExperiment.generators.remove(id);
    }

    private void deleteLoopData(long id) {
        Loop loop = mExperiment.loops.get(id);
        mExperiment.loops.remove(id);
        mExperiment.program.loops.remove(id);

        for (Long gId : loop.generators) {
            deleteGeneratorData(gId);
        }

        for (Long sId : loop.scenes) {
            deleteSceneData(sId);
        }

        if (mCurrentGeneratorAdapter != null && mCurrentGeneratorAdapter.first == id) {
            mCurrentGeneratorAdapter = null;
        }

        if (mCurrentSceneAdapter != null && mCurrentSceneAdapter.first == id) {
            mCurrentSceneAdapter = null;
        }
    }

    private void deleteOperandData(long id) {
        Operand operand = mExperiment.operands.get(id);

        if (!(operand instanceof CallOperand)) {
            mExperiment.operands.remove(id);
            return;
        }

        ArrayList<Long> operandIds = ((CallOperand)operand).getOperands();
        for (Long operandId : operandIds) {
            deleteOperandData(operandId);
        }
    }

    private void deletePropData(Long pId) {
        // TODO Auto-generated method stub
    }

    private void deleteRuleData(Long id) {
        Rule rule = mExperiment.rules.get(id);
        mExperiment.rules.remove(id);

        for (Long aid : rule.actions) {
            deleteActionData(aid);
        }

        if (mCurrentActionAdapter != null && mCurrentActionAdapter.first == id) {
            mCurrentActionAdapter = null;
        }
    }

    private void deleteSceneData(long id) {
        Scene scene = mExperiment.scenes.get(id);
        mExperiment.scenes.remove(id);
        for (Long rId : scene.rules) {
            deleteRuleData(rId);
        }
        for (Long pId : scene.props) {
            deletePropData(pId);
        }

        if (mCurrentRuleAdapter != null && mCurrentRuleAdapter.first == id) {
            mCurrentRuleAdapter = null;
        }
    }

    private void doSelectSection(int position) {
        mSectionManager.selectSection(position);
        mDrawerList.setItemChecked(position, true);
        setTitle(mSectionManager.getTitle(position));
    }

    private Long findUnusedKey(LongSparseArray<?> map) {
        Long currKey = 0l;
        while (true) {
            if (map.indexOfKey(currKey) < 0) {
                break;
            }
            currKey++;
        }
        return currKey;
    }

    private Experiment getExperimentFromDelegate() {
        Experiment experiment = null;
        try {
            experiment = mExperimentDelegate.getExperiment();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return experiment;
    }

    private void notifyActionAdapter() {
        if (mCurrentActionAdapter == null) {
            return;
        }

        mCurrentActionAdapter.second.notifyDataSetChanged();
    }

    private void notifyActionDataChangeListeners() {
        if (mActionDataChangeListeners == null) {
            return;
        }

        for (ActionDataChangeListener l : mActionDataChangeListeners) {
            l.onActionDataChange();
        }
    }

    private void notifyAssetDataChangeListeners() {
        if (mAssetDataChangeListeners == null) {
            return;
        }

        for (AssetDataChangeListener l : mAssetDataChangeListeners) {
            l.onAssetDataChange();
        }
    }

    private void notifyGeneratorAdapter() {
        if (mCurrentGeneratorAdapter == null) {
            return;
        }

        mCurrentGeneratorAdapter.second.notifyDataSetChanged();
    }

    private void notifyGeneratorDataChangeListeners() {
        if (mGeneratorDataChangeListeners == null) {
            return;
        }

        for (GeneratorDataChangeListener l : mGeneratorDataChangeListeners) {
            l.onGeneratorDataChange();
        }
    }

    private void notifyLandingPageDataChangeListeners() {
        if (mLandingPageDataChangeListeners == null) {
            return;
        }

        for (LandingPageDataChangeListener l : mLandingPageDataChangeListeners) {
            l.onLandingPageDataChange();
        }
    }

    private void notifyLoopAdapter() {
        if (mLoopAdapter == null) {
            return;
        }

        mLoopAdapter.notifyDataSetChanged();
    }

    private void notifyLoopDataChangeListeners() {
        if (mLoopDataChangeListeners == null) {
            return;
        }

        for (LoopDataChangeListener l : mLoopDataChangeListeners) {
            l.onLoopDataChange();
        }
    }

    private void notifyOperandAdapters(long id) {
        // Check all adapters to see if it references this operand we have
        // updated so we can notify a data set change.
        for (int i = 0; i < mOperandAdapters.size(); i++) {
            // Check scope level.
            LongSparseArray<ProgramComponentAdapter<Operand>> operandAdaptersInScope = mOperandAdapters
                    .get(mOperandAdapters.keyAt(i));

            for (int j = 0; j < operandAdaptersInScope.size(); j++) {
                // Check adapters currently existing for operand parents.
                ProgramComponentAdapter<Operand> adapter = operandAdaptersInScope
                        .get(operandAdaptersInScope.keyAt(j));

                for (Long operandId : adapter.getKeys()) {
                    if (operandId == id) {
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }

    private void notifyOperandDataChangeListeners() {
        if (mOperandDataChangeListeners == null) {
            return;
        }

        for (OperandDataChangeListener listener : mOperandDataChangeListeners) {
            listener.notify();
        }
    }

    private void notifyParameterAdapter() {
        if (mCurrentParameterAdapter == null) {
            return;
        }

        mCurrentParameterAdapter.second.notifyDataSetChanged();
    }

    private void notifyRuleAdapter() {
        if (mCurrentRuleAdapter == null) {
            return;
        }

        mCurrentRuleAdapter.second.notifyDataSetChanged();
    }

    private void notifyRuleDataChangeListeners() {
        if (mRuleDataChangeListeners == null) {
            return;
        }

        for (RuleDataChangeListener l : mRuleDataChangeListeners) {
            l.onRuleDataChange();
        }
    }

    private void notifySceneAdapter() {
        if (mCurrentSceneAdapter == null) {
            return;
        }

        mCurrentSceneAdapter.second.notifyDataSetChanged();
    }

    private void notifySceneDataChangeListeners() {
        if (mSceneDataChangeListeners == null) {
            return;
        }

        for (SceneDataChangeListener l : mSceneDataChangeListeners) {
            l.onSceneDataChange();
        }
    }

    /**
     * Restore or create the fragment used to keep the experiment in memory
     * across configuration changes.
     * 
     * @return Experiment holder.
     */
    private ExperimentHolderFragment restoreExperimentHolder() {
        FragmentManager fm = getSupportFragmentManager();
        ExperimentHolderFragment holder = (ExperimentHolderFragment)fm
                .findFragmentByTag("experiment_holder");

        // Create the experiment holder if it wasn't already persisted.
        if (holder == null) {
            holder = new ExperimentHolderFragment();
            fm.beginTransaction().add(holder, "experiment_holder").commit();
        }
        return holder;
    }

    /**
     * Restore experiment from persisted state or create it if necessary.
     * 
     * @return Restored, or newly created, experiment.
     */
    private Experiment restoreOrCreateExperiment(ExperimentHolderFragment experimentHolderFragment) {
        Experiment experiment = experimentHolderFragment.getExperiment();
        if (experiment == null) {
            // We are therefore entering the activity and either creating a new
            // experiment or editing an existing one.
            if (mExperimentDelegate == null) {
                // No experiment delegate so we are creating a new experiment.
                experiment = new Experiment();
                experiment.setWorkingDirectory(Environment.getExternalStorageDirectory());
                Time time = new Time();
                time.setToNow();
                experiment.dateCreated = time.toMillis(false);
                mMode = MODE_NEW;
            } else {
                // Load experiment from disk using the delegate.
                experiment = getExperimentFromDelegate();
                mMode = MODE_EDIT;
            }
            // Store the experiment for persistence.
            mExperimentHolderFragment.setExperiment(experiment);
        }

        return experiment;
    };

    private void selectSection(int section) {
        mDrawerList.setSelection(section);
        doSelectSection(section);
    }

    private void storeExperiment() {
        if (mMode == MODE_NEW) {
            try {
                mUserDelegate.addExperiment(mExperiment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mExperimentDelegate.replace(mExperiment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateOperandAdapterIfExists(long scopeId, int scope, OperandHolder holder) {
        ProgramComponentAdapter<Operand> adapter;
        LongSparseArray<ProgramComponentAdapter<Operand>> operandAdaptersInScope;

        operandAdaptersInScope = mOperandAdapters.get(scope);
        if (operandAdaptersInScope != null) {
            adapter = operandAdaptersInScope.get(scopeId);
            if (adapter != null) {
                adapter.setKeys(holder.getOperands());
                return;
            }
        }
    }

    private void updateStageInScene(long sceneId, ArrayList<Prop> props, int orientation,
            int width, int height) {
        /*
         * Refresh all props by first removing the old versions from the prop
         * map and then adding the new ones. The new versions may actually be
         * completely unchanged but there is currently no mechanism to check
         * this.
         */
        Scene scene = mExperiment.scenes.get(sceneId);

        scene.orientation = orientation;
        scene.stageWidth = width;
        scene.stageHeight = height;

        for (Long propId : scene.props) {
            mExperiment.props.remove(propId);
        }
        scene.props = new ArrayList<Long>();

        for (Prop prop : props) {
            Long key = findUnusedKey(mExperiment.props);
            mExperiment.props.put(key, prop);
            scene.props.add(key);
        }

        notifySceneDataChangeListeners();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mSectionManager.restoreState(savedInstanceState.getParcelable(ADAPTER_STATE),
                getClassLoader());

        selectSection(mSectionManager.getCurrentPosition());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(ADAPTER_STATE, mSectionManager.saveState());
    }

    StickyGridHeadersSimpleAdapter getAssetsAdapter(int filter) {
        ExperimentObjectFilter objectFilter = ExperimentObjectReference.getFilter(filter);

        LongSparseArray<Asset> filteredAssets = new LongSparseArray<Asset>();
        for (int i = 0; i < mExperiment.assets.size(); i++) {
            long key = mExperiment.assets.keyAt(i);
            Asset asset = mExperiment.assets.get(key);
            if (objectFilter.filter(asset)) {
                filteredAssets.put(key, asset);
            }
        }

        return new AssetsAdapter(this, filteredAssets);
    }

    Collator getCollater() {
        Locale locale = Locale.getDefault();
        Collator collator = Collator.getInstance(locale);
        collator.setStrength(Collator.SECONDARY);
        return collator;
    }

    /**
     * Get an adapter containing all objects related to the experiment program
     * flow. The objects are grouped by kind in the adapter.
     * 
     * @param sceneId Scene defining scope of objects to gather into the
     *            adapter.
     * @return Program flow objects ListAdapter.
     */
    ExperimentObjectAdapter getExperimentControlsAdapter(long sceneId, int filter) {
        SortedSet<Pair<ExperimentObject, Long>> experimentControls = new TreeSet<Pair<ExperimentObject, Long>>(
                new Comparator<Pair<ExperimentObject, Long>>() {
                    @Override
                    public int compare(Pair<ExperimentObject, Long> lhs,
                            Pair<ExperimentObject, Long> rhs) {
                        Collator collator = getCollater();
                        return collator.compare(
                                lhs.first.getPrettyName(ExperimentDesignerActivity.this),
                                rhs.first.getPrettyName(ExperimentDesignerActivity.this));
                    }
                });

        ExperimentObjectFilter objectFilter = ExperimentObjectReference.getFilter(filter);

        Scene scene = mExperiment.scenes.get(sceneId);
        if (objectFilter.filter(scene)) {
            experimentControls.add(new Pair<ExperimentObject, Long>(scene, sceneId));
        }

        for (int i = 0; i < mExperiment.loops.size(); i++) {
            Loop loop = mExperiment.loops.valueAt(i);
            if (loop.contains(sceneId)) {
                if (objectFilter.filter(loop)) {
                    experimentControls.add(new Pair<ExperimentObject, Long>(loop, (long)i));
                }
                break;
            }
        }

        // TODO: Records and Stores.
        // experimentControls.add(mExperiment.stores);
        // experimentControls.add(mExperiment.records);

        for (int i = 0; i < mExperiment.generators.size(); i++) {
            Generator generator = mExperiment.generators.valueAt(i);
            if (objectFilter.filter(generator)) {
                experimentControls.add(new Pair<ExperimentObject, Long>(generator, (long)i));
            }
        }

        return new ExperimentControlAdapter(this, new ArrayList<Pair<ExperimentObject, Long>>(
                experimentControls));
    }

    /**
     * Get an adapter for all props in the given scene.
     * 
     * @param sceneId Id of the scene to gather props for.
     * @return ListAdapter of all props in the given scene.
     */
    ListAdapter getPropsAdapter(long sceneId) {
        LongSparseArray<Prop> props = new LongSparseArray<Prop>();

        for (Long propId : mExperiment.scenes.get(sceneId).props) {
            props.put(propId, mExperiment.props.get(propId));
        }

        return new LongSparseArrayAdapter<Prop>(this, android.R.layout.simple_list_item_1, props);
    }

    /**
     * Get an adapter for all props in the given scene.
     * 
     * @param sceneId Id of the scene to gather props for.
     * @return ListAdapter of all props in the given scene.
     */
    ListAdapter getPropsAdapter(long sceneId, int filter) {
        LongSparseArray<Prop> props = new LongSparseArray<Prop>();

        for (Long propId : mExperiment.scenes.get(sceneId).props) {
            Prop prop = mExperiment.props.get(propId);
            ExperimentObjectFilter objectFilter = ExperimentObjectReference.getFilter(filter);
            if (objectFilter.filter(prop)) {
                props.put(propId, prop);
            }
        }

        return new LongSparseArrayAdapter<Prop>(this, android.R.layout.simple_list_item_1, props);
    }

    public interface ActionDataChangeListener {
        void onActionDataChange();
    }

    public interface AssetDataChangeListener {
        void onAssetDataChange();
    }

    public interface GeneratorDataChangeListener {
        void onGeneratorDataChange();
    }

    public static interface LandingPageDataChangeListener {
        void onLandingPageDataChange();
    }

    public interface LoopDataChangeListener {
        void onLoopDataChange();
    }

    public interface OperandDataChangeListener {
        void onOperandDataChange();
    }

    public interface RuleDataChangeListener {
        void onRuleDataChange();
    }

    public interface SceneDataChangeListener {
        void onSceneDataChange();
    }

    final class DrawerToggle extends ActionBarDrawerToggle {
        private DrawerToggle(Activity activity, DrawerLayout drawerLayout, int drawerImageRes,
                int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes,
                    closeDrawerContentDescRes);
        }

        /** Called when a drawer has settled in a completely closed state. */
        @Override
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            getActionBar().setTitle(mTitle);
            invalidateOptionsMenu(); // creates call to
                                     // onPrepareOptionsMenu()
        }

        /** Called when a drawer has settled in a completely open state. */
        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            getActionBar().setTitle(mDrawerTitle);
            invalidateOptionsMenu(); // creates call to
                                     // onPrepareOptionsMenu()
        }
    }

    class ExperimentControlAdapter extends BaseAdapter implements ExperimentObjectAdapter {
        private Context mContext;

        private LayoutInflater mInflater;

        private List<Pair<ExperimentObject, Long>> mItems;

        public ExperimentControlAdapter(Context context, List<Pair<ExperimentObject, Long>> items) {
            mContext = context;
            mItems = items;
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position).first;
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).second;
        }

        @Override
        public int getObjectKind(int position) {
            return mItems.get(position).first.kind();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            holder.textViews[0].setText(mItems.get(position).first.getPrettyName(mContext));

            return convertView;
        }

    }

    class SectionAdapter extends ArrayAdapter<EditorSectionItem> {
        private LayoutInflater mInflater;

        private int mResourceId;

        public SectionAdapter(Context context, int resourceId, List<EditorSectionItem> objects) {
            super(context, resourceId, objects);
            mResourceId = resourceId;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(mResourceId, parent, false);
                holder = new TextViewHolder(2);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                holder.textViews[1] = (TextView)convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            holder.textViews[0].setText(getItem(position).titleId);
            holder.textViews[1].setText(getItem(position).titleId);

            return convertView;
        }
    }
}
