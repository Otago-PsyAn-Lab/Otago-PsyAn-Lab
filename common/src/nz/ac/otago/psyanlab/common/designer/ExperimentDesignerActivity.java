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
import nz.ac.otago.psyanlab.common.UserExperimentDelegateI;
import nz.ac.otago.psyanlab.common.designer.EditorSectionManager.EditorSectionItem;
import nz.ac.otago.psyanlab.common.designer.assets.AssetCallbacks;
import nz.ac.otago.psyanlab.common.designer.assets.AssetsFragment;
import nz.ac.otago.psyanlab.common.designer.assets.ImportAssetActivity;
import nz.ac.otago.psyanlab.common.designer.channels.ChannelCallbacks;
import nz.ac.otago.psyanlab.common.designer.channels.ChannelsFragment;
import nz.ac.otago.psyanlab.common.designer.meta.MetaFragment;
import nz.ac.otago.psyanlab.common.designer.program.ProgramFragment;
import nz.ac.otago.psyanlab.common.designer.program.object.PickObjectDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.program.stage.StageActivity;
import nz.ac.otago.psyanlab.common.designer.program.util.ProgramCallbacks;
import nz.ac.otago.psyanlab.common.designer.subject.SubjectFragment;
import nz.ac.otago.psyanlab.common.designer.util.ActionListItemViewBinder;
import nz.ac.otago.psyanlab.common.designer.util.ArrayFragmentMapAdapter;
import nz.ac.otago.psyanlab.common.designer.util.ArrayFragmentMapAdapter.PageData;
import nz.ac.otago.psyanlab.common.designer.util.DetailsCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.EventAdapter;
import nz.ac.otago.psyanlab.common.designer.util.ExperimentObjectAdapter;
import nz.ac.otago.psyanlab.common.designer.util.GeneratorListItemViewBinder;
import nz.ac.otago.psyanlab.common.designer.util.LoopListItemViewBinder;
import nz.ac.otago.psyanlab.common.designer.util.MethodAdapter;
import nz.ac.otago.psyanlab.common.designer.util.MethodAdapter.MethodData;
import nz.ac.otago.psyanlab.common.designer.util.OperandListItemViewBinder;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.designer.util.RuleListItemViewBinder;
import nz.ac.otago.psyanlab.common.designer.util.SceneListItemViewBinder;
import nz.ac.otago.psyanlab.common.model.Action;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.DataChannel;
import nz.ac.otago.psyanlab.common.model.Experiment;
import nz.ac.otago.psyanlab.common.model.ExperimentObject;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.Generator;
import nz.ac.otago.psyanlab.common.model.LandingPage;
import nz.ac.otago.psyanlab.common.model.Loop;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.Rule;
import nz.ac.otago.psyanlab.common.model.Scene;
import nz.ac.otago.psyanlab.common.model.TouchEvent;
import nz.ac.otago.psyanlab.common.model.TouchMotionEvent;
import nz.ac.otago.psyanlab.common.model.channel.Field;
import nz.ac.otago.psyanlab.common.model.operand.CallValue;
import nz.ac.otago.psyanlab.common.model.operand.StubOperand;
import nz.ac.otago.psyanlab.common.model.util.EventData;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.ModelUtils;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.OperandHolder;
import nz.ac.otago.psyanlab.common.model.util.Type;
import nz.ac.otago.psyanlab.common.util.Args;
import nz.ac.otago.psyanlab.common.util.ConfirmDialogFragment;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.format.Time;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Provides an interface to the fragments implementing the UI whereby they can
 * manipulate the experiment data.
 */
public class ExperimentDesignerActivity extends FragmentActivity implements DetailsCallbacks,
        SubjectFragment.Callbacks, AssetCallbacks, ProgramCallbacks, ChannelCallbacks,
        DialogueResultCallbacks {
    private static final String ADAPTER_STATE = "adapter_state";

    private static final int REQUEST_ASSET_IMPORT = 0x02;

    private static final int REQUEST_EDIT_STAGE = 0x03;

    private static final int SCOPE_EXPERIMENT = 0x04;

    private static final int SCOPE_LOOP = 0x03;

    private static final int SCOPE_NONE = 0;

    private static final int SCOPE_RULE = 0x01;

    private static final int SCOPE_SCENE = 0x02;

    private ArrayList<ActionDataChangeListener> mActionDataChangeListeners;

    private ArrayList<AssetDataChangeListener> mAssetDataChangeListeners;

    private AssetsAdapter mAssetsAdapter;

    private Pair<Long, ProgramComponentAdapter<Action>> mCurrentActionAdapter;

    private Pair<Long, ProgramComponentAdapter<Generator>> mCurrentGeneratorAdapter;

    private Pair<Long, ProgramComponentAdapter<Rule>> mCurrentRuleAdapter;

    private Pair<Long, ProgramComponentAdapter<Scene>> mCurrentSceneAdapter;

    private DataChannelsAdapter mDataChannelAdapter;

    private ArrayList<DataChannelDataChangeListener> mDataChannelDataChangeListeners;

    private SparseArray<DialogueResultListener> mDialogueResultListeners;

    private ListView mDrawerList;

    private ArrayList<DrawerListener> mDrawerListeners = new ArrayList<DrawerLayout.DrawerListener>();

    private ActionBarDrawerToggle mDrawerToggle;

    private Experiment mExperiment;

    private UserExperimentDelegateI mExperimentDelegate;

    private ExperimentHolderFragment mExperimentHolderFragment;

    private ArrayList<GeneratorDataChangeListener> mGeneratorDataChangeListeners;

    private ArrayList<LandingPageDataChangeListener> mLandingPageDataChangeListeners;

    private ProgramComponentAdapter<Loop> mLoopAdapter;

    private ArrayList<LoopDataChangeListener> mLoopDataChangeListeners;

    private OnItemClickListener mOnDrawerListItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            doSelectSection(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    };

    private LongSparseArray<ProgramComponentAdapter<Operand>> mOperandAdapters = new LongSparseArray<ProgramComponentAdapter<Operand>>();

    private ArrayList<OperandDataChangeListener> mOperandDataChangeListeners;

    private ArrayList<RuleDataChangeListener> mRuleDataChangeListeners;

    private ArrayList<SceneDataChangeListener> mSceneDataChangeListeners;

    private EditorSectionManager mSectionManager;

    private CharSequence mTitle;

    protected DrawerLayout mDrawerLayout;

    @Override
    public long addAction(Action action) {
        long unusedKey = findUnusedKey(mExperiment.actions);
        mExperiment.actions.put(unusedKey, action);
        notifyActionDataChangeListeners();
        return unusedKey;
    }

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
    public long addDataChannel(DataChannel dataChannel) {
        Long unusedKey = findUnusedKey(mExperiment.dataChannels);
        mExperiment.dataChannels.put(unusedKey, dataChannel);
        notifyDataChannelDataChange();
        return unusedKey;
    }

    @Override
    public void addDataChannelDataChangeListener(DataChannelDataChangeListener listener) {
        if (mDataChannelDataChangeListeners == null) {
            mDataChannelDataChangeListeners = new ArrayList<DataChannelDataChangeListener>();
        }
        mDataChannelDataChangeListeners.add(listener);
    }

    @Override
    public void addDrawerListener(DrawerListener listener) {
        mDrawerListeners.add(listener);
    }

    @Override
    public long addGenerator(Generator generator) {
        Long unusedKey = findUnusedKey(mExperiment.generators);
        mExperiment.generators.put(unusedKey, generator);
        notifyGeneratorDataChangeListeners();
        return unusedKey;
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
    public long addLoop(Loop loop) {
        Long key = findUnusedKey(mExperiment.loops);
        mExperiment.loops.put(key, loop);
        mExperiment.program.loops.add(key);
        notifyLoopAdapter();
        notifyLoopDataChangeListeners();
        return key;
    }

    @Override
    public void addLoopDataChangeListener(LoopDataChangeListener listener) {
        if (mLoopDataChangeListeners == null) {
            mLoopDataChangeListeners = new ArrayList<LoopDataChangeListener>();
        }
        mLoopDataChangeListeners.add(listener);
    }

    @Override
    public long addOperand(Operand operand) {
        Long key = findUnusedKey(mExperiment.operands);
        mExperiment.operands.put(key, operand);
        notifyOperandDataChangeListeners();
        return key;
    }

    @Override
    public void addOperandDataChangeListener(OperandDataChangeListener listener) {
        if (mOperandDataChangeListeners == null) {
            mOperandDataChangeListeners = new ArrayList<OperandDataChangeListener>();
        }
        mOperandDataChangeListeners.add(listener);
    }

    @Override
    public long addRule(Rule rule) {
        Long unusedKey = findUnusedKey(mExperiment.rules);
        mExperiment.rules.put(unusedKey, rule);
        notifyRuleDataChangeListeners();
        return unusedKey;
    }

    @Override
    public void addRuleDataChangeListener(RuleDataChangeListener listener) {
        if (mRuleDataChangeListeners == null) {
            mRuleDataChangeListeners = new ArrayList<RuleDataChangeListener>();
        }
        mRuleDataChangeListeners.add(listener);
    }

    @Override
    public long addScene(Scene scene) {
        Long unusedKey = findUnusedKey(mExperiment.scenes);
        mExperiment.scenes.put(unusedKey, scene);
        notifySceneDataChangeListeners();
        return unusedKey;
    }

    @Override
    public void addSceneDataChangeListener(SceneDataChangeListener listener) {
        if (mSceneDataChangeListeners == null) {
            mSceneDataChangeListeners = new ArrayList<SceneDataChangeListener>();
        }
        mSceneDataChangeListeners.add(listener);
    }

    @Override
    public void clearDialogueResultListener(int requestCode) {
        if (mDialogueResultListeners != null) {
            mDialogueResultListeners.remove(requestCode);
        }
    }

    @Override
    public void deleteAction(long id) {
        mExperiment.actions.remove(id);
        notifyActionDataChangeListeners();
    }

    @Override
    public void deleteAsset(long id) {
        deleteAssetData(id);

        mAssetsAdapter.notifyDataSetChanged();
        notifyAssetDataChangeListeners();
    }

    @Override
    public void deleteDataChannel(long id) {
        DataChannel dataChannel = mExperiment.dataChannels.remove(id);

        deleteDataChannelData(dataChannel);
        removeReferencesTo(ExperimentObject.KIND_DATA_CHANNEL, id);

        notifyDataChannelDataChange();
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
        if (!mExperiment.operands.containsKey(id)) {
            // Nothing to do because we already don't have the indicated
            // operand.
            return;
        }

        deleteOperandData(mExperiment.operands.remove(id));
        ;
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
        for (int j = 0; j < mOperandAdapters.size(); j++) {
            // Check adapters currently existing for operand parents.
            long key = mOperandAdapters.keyAt(j);
            if (mOperandAdapters.get(key) == adapter) {
                mOperandAdapters.remove(key);
                return;
            }
        }
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
                new ActionListItemViewBinder(this, this));
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

    @Override
    public String getAuthors() {
        return mExperiment.authors;
    }

    @Override
    public File getCachedFile(String path) {
        return mExperimentDelegate.getFile(path);
    }

    @Override
    public DataChannel getDataChannel(long id) {
        return mExperiment.dataChannels.get(id);
    }

    @Override
    public ListAdapter getDataChannelsAdapter() {
        if (mDataChannelAdapter == null) {
            mDataChannelAdapter = new DataChannelsAdapter(this, mExperiment.dataChannels);
        }

        return mDataChannelAdapter;
    }

    @Override
    public String getDescription() {
        return mExperiment.description;
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
        SortedSet<EventData> filteredEvents;

        final NameResolverFactory nameFactory = ModelUtils.getEventNameFactory(clazz);

        filteredEvents = new TreeSet<EventData>(new Comparator<EventData>() {
            @Override
            public int compare(EventData lhs, EventData rhs) {
                Collator collator = getCollater();
                return collator.compare(getString(nameFactory.getResId(lhs.id())),
                        getString(nameFactory.getResId(rhs.id())));
            }
        });

        Method[] methods = clazz.getMethods();

        // Filter methods for those which register listeners for events.
        for (int i = 0; i < methods.length; i++) {
            EventData annotation = methods[i].getAnnotation(EventData.class);
            if (annotation != null) {
                filteredEvents.add(annotation);
            }
        }

        return new EventAdapter(this, filteredEvents, nameFactory);
    }

    @Override
    public ExperimentObject getExperimentObject(ExperimentObjectReference object) {
        switch (object.kind) {
            case ExperimentObject.KIND_ASSET:
                return mExperiment.assets.get(object.id);
            case ExperimentObject.KIND_GENERATOR:
                return mExperiment.generators.get(object.id);
            case ExperimentObject.KIND_LOOP:
                return mExperiment.loops.get(object.id);
            case ExperimentObject.KIND_PROP:
                return mExperiment.props.get(object.id);
            case ExperimentObject.KIND_SCENE:
                return mExperiment.scenes.get(object.id);
            case ExperimentObject.KIND_EVENT:
                if (object.id == EventData.EVENT_TOUCH) {
                    return new TouchEvent();
                } else if (object.id == EventData.EVENT_TOUCH_MOTION) {
                    return new TouchMotionEvent();
                }
            case ExperimentObject.KIND_EXPERIMENT:
            default:
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
                new GeneratorListItemViewBinder(this, this));
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
                mExperiment.program.loops, new LoopListItemViewBinder(this, this));

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
    public SpinnerAdapter getMethodsAdapter(Class<?> clazz, int returnTypes) {
        // Obtain the name factory to pull the internationalised event names.
        SortedSet<MethodData> filteredMethods;

        final NameResolverFactory nameFactory = ModelUtils.getMethodNameFactory(clazz);

        filteredMethods = new TreeSet<MethodData>(new Comparator<MethodData>() {
            @Override
            public int compare(MethodData lhs, MethodData rhs) {
                Collator collator = getCollater();
                return collator.compare(getString(nameFactory.getResId(lhs.id)),
                        getString(nameFactory.getResId(rhs.id)));
            }
        });

        Method[] methods = clazz.getMethods();

        // Filter methods for those which register listeners for events.
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            MethodId annotation = method.getAnnotation(MethodId.class);
            if (annotation != null && returnTypeIntersects(method, returnTypes)) {
                MethodData data = new MethodData();
                data.id = annotation.value();
                data.method = method;
                filteredMethods.add(data);
            }
        }

        return new MethodAdapter(this, filteredMethods, nameFactory);
    }

    @Override
    public String getName() {
        return mExperiment.name;
    }

    /**
     * Get an adapter for objects grouped into pages by scope.
     * 
     * @param fm Fragment manager for adapter.
     * @param callerKind The kind of calling component. Use
     *            ExperimentObject.KIND_*.
     * @param componentId Id of the calling component.
     * @param filter Filter to match experiment objects by.
     * @param fragmentFactory Factory to produce page fragments.
     * @return Adapter for pages of objects
     */
    @Override
    public FragmentPagerAdapter getObjectBrowserPagerAdapter(FragmentManager fm, int callerKind,
            long callerId, int filter, ArrayFragmentMapAdapter.FragmentFactory fragmentFactory) {

        // Work out the base scope level from our caller.
        int scopeLevel;
        switch (callerKind) {
            case ExperimentObject.KIND_ACTION:
            case ExperimentObject.KIND_RULE:
                scopeLevel = SCOPE_RULE;
                break;
            case ExperimentObject.KIND_SCENE:
                scopeLevel = SCOPE_SCENE;
                break;
            case ExperimentObject.KIND_LOOP:
                scopeLevel = SCOPE_LOOP;
                break;

            default:
                scopeLevel = SCOPE_EXPERIMENT;
                break;
        }

        // Build a dynamic list of pages corresponding with the scopes in which
        // there are valid matches of objects as per the passed filter and base
        // scope level.
        List<PageData> stubs = new ArrayList<PageData>();
        while (scopeLevel != SCOPE_NONE) {
            switch (scopeLevel) {
                case SCOPE_RULE:
                    if (anyObjectsMatching(scopeLevel, callerKind, callerId, filter)) {
                        Rule rule = mExperiment.rules.get(callerId);
                        stubs.add(new PageData(rule.name, scopeLevel));
                    }
                    scopeLevel = SCOPE_SCENE;
                case SCOPE_SCENE:
                    if (anyObjectsMatching(scopeLevel, callerKind, callerId, filter)) {
                        Scene scene = mExperiment.scenes.get(callerId);
                        stubs.add(new PageData(scene.name, scopeLevel));
                    }
                    scopeLevel = SCOPE_LOOP;
                case SCOPE_LOOP:
                    if (anyObjectsMatching(scopeLevel, callerKind, callerId, filter)) {
                        Loop loop = mExperiment.loops.get(callerId);
                        stubs.add(new PageData(loop.name, scopeLevel));
                    }

                    scopeLevel = SCOPE_EXPERIMENT;
                case SCOPE_EXPERIMENT:
                default:
                    scopeLevel = SCOPE_NONE;
                    break;
            }
        }

        return new ArrayFragmentMapAdapter(fm, fragmentFactory, stubs);
    }

    @Override
    public ExperimentObjectAdapter getObjectSectionListAdapter(int callerKind, long callerId,
            int relativeScope, int filter) {
        // final int effectiveScope = getEffectiveScopeLevel(callerKind,
        // relativeScope);
        List<Pair<ExperimentObject, Long>> objects = getObjectsMatching(relativeScope, callerKind,
                callerId, filter);
        return new ExperimentObjectAdapter(this, objects);
    }

    @Override
    public Operand getOperand(long id) {
        return mExperiment.operands.get(id);
    }

    @Override
    public ProgramComponentAdapter<Operand> getOperandAdapter(long parentId) {
        ProgramComponentAdapter<Operand> adapter;
        if (mOperandAdapters != null) {
            adapter = mOperandAdapters.get(parentId);
            if (adapter != null) {
                return adapter;
            }
        } else {
            mOperandAdapters = new LongSparseArray<ProgramComponentAdapter<Operand>>();
        }

        // Create adapter for our requested parent id.
        OperandHolder operandHolder = (OperandHolder)mExperiment.operands.get(parentId);

        adapter = new ProgramComponentAdapter<Operand>(mExperiment.operands,
                operandHolder.getOperands(), new OperandListItemViewBinder(this, this));
        mOperandAdapters.put(parentId, adapter);

        return adapter;
    }

    @Override
    public HashMap<Long, Operand> getOperands() {
        return mExperiment.operands;
    }

    @Override
    public Prop getProp(long id) {
        return mExperiment.props.get(id);
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

        ProgramComponentAdapter<Rule> adapter = new ProgramComponentAdapter<Rule>(
                mExperiment.rules, mExperiment.scenes.get(sceneId).rules,
                new RuleListItemViewBinder(this, this));

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
                mExperiment.scenes, mExperiment.loops.get(loopId).scenes,
                new SceneListItemViewBinder(this, this));
        mCurrentSceneAdapter = new Pair<Long, ProgramComponentAdapter<Scene>>(loopId, adapter);

        return adapter;
    }

    @Override
    public int getVersion() {
        return mExperiment.version;
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
                        mExperiment.assets.put(findUnusedKey(mExperiment.assets), Asset
                                .getFactory().newAsset(paths[0]));
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
                R.string.message_exit_designer, R.string.action_save_exit, R.string.action_cancel,
                new ConfirmDialogFragment.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        doSaveAction();
                        finish();
                        dialog.dismiss();
                    }
                }, new ConfirmDialogFragment.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
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

        mSectionManager.addSection(R.string.designer_tab_properties, MetaFragment.class, null);
        mSectionManager.addSection(R.string.designer_tab_subject, SubjectFragment.class, null);
        mSectionManager.addSection(R.string.designer_tab_assets, AssetsFragment.class, null);
        mSectionManager.addSection(R.string.designer_tab_sources, AssetsFragment.class, null);
        mSectionManager.addSection(R.string.designer_tab_variables, AssetsFragment.class, null);
        mSectionManager.addSection(R.string.designer_tab_data_channels, ChannelsFragment.class,
                null);
        mSectionManager.addSection(R.string.designer_tab_program, ProgramFragment.class, null);

        ArrayAdapter<EditorSectionItem> adapter = new SectionAdapter(this,
                R.layout.list_item_drawer, mSectionManager.getItems());

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(mOnDrawerListItemClickListener);

        Bundle extras = getIntent().getExtras();

        mExperimentDelegate = extras.getParcelable(Args.USER_EXPERIMENT_DELEGATE);
        mExperimentDelegate.init(this);

        mExperimentHolderFragment = restoreExperimentHolder();
        mExperiment = restoreOrCreateExperiment(mExperimentHolderFragment);

        if (mExperiment == null) {
            throw new RuntimeException("Missing experiment");
        }

        mAssetsAdapter = new AssetsAdapter(this, mExperiment.assets);

        setTitle(mExperiment.name);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new DrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close);

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        if (savedInstanceState == null) {
            selectSection(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.activity_experiment_designer, menu);
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

        long itemId = item.getItemId();
        if (itemId == R.id.menu_discard) {
            return doDiscardAction();
        } else if (itemId == R.id.menu_done) {
            doSaveAction();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // ExperimentUtils.convertToScreen(this, mExperiment);
    }

    @Override
    public void pickExperimentObject(int callerKind, long callerId, int filter, int requestCode) {
        DialogFragment dialog = PickObjectDialogueFragment.newDialog(callerKind, callerId, filter,
                requestCode);
        dialog.show(getSupportFragmentManager(), PickObjectDialogueFragment.TAG);
    };

    @Override
    public void putAction(long id, Action action) {
        mExperiment.actions.put(id, action);

        notifyActionAdapter();

        notifyActionDataChangeListeners();
    }

    @Override
    public void putAsset(long id, Asset asset) {
        mExperiment.assets.put(id, asset);

        updateReferencesTo(ExperimentObject.KIND_ASSET, id);

        mAssetsAdapter.notifyDataSetChanged();
        notifyAssetDataChangeListeners();
    }

    @Override
    public void putDataChannel(long id, DataChannel dataChannel) {
        mExperiment.dataChannels.put(id, dataChannel);
        updateReferencesTo(ExperimentObject.KIND_DATA_CHANNEL, id);
        notifyDataChannelDataChange();
    }

    @Override
    public void putGenerator(long id, Generator generator) {
        mExperiment.generators.put(id, generator);

        updateReferencesTo(ExperimentObject.KIND_GENERATOR, id);

        notifyGeneratorDataChangeListeners();
        notifyLoopDataChangeListeners();
        notifyGeneratorAdapter();
    }

    @Override
    public void putLoop(long id, Loop loop) {
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
    public void putOperand(long id, Operand operand) {
        mExperiment.operands.put(id, operand);

        if (operand instanceof OperandHolder) {
            OperandHolder holder = (OperandHolder)operand;
            updateOperandAdapterIfExists(id, holder);
        }

        notifyOperandAdapters(id);

        notifyOperandDataChangeListeners();
    }

    @Override
    public void putRule(long id, Rule rule) {
        mExperiment.rules.put(id, rule);
        notifyRuleAdapter();
        if (mCurrentActionAdapter != null && mCurrentActionAdapter.first == id) {
            notifyActionAdapter();
        }
        notifyRuleDataChangeListeners();
    }

    @Override
    public void putScene(long id, Scene scene) {
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
    public void removeDataChannelDataChangeListener(DataChannelDataChangeListener listener) {
        mDataChannelDataChangeListeners.remove(listener);
    }

    @Override
    public void removeDrawerListener(DrawerListener drawerListener) {
        mDrawerListeners.remove(drawerListener);
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
    public void startEditStage(long sceneId) {
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
    public void startImportAssetUI() {
        Intent intent = new Intent(this, ImportAssetActivity.class);
        startActivityForResult(intent, REQUEST_ASSET_IMPORT);
    }

    @Override
    public void storeLandingPage(LandingPage landingPage) {
        mExperiment.landingPage = landingPage;
        notifyLandingPageDataChangeListeners();
    }

    @Override
    public void updateAuthors(String authors) {
        mExperiment.authors = authors;
    }

    @Override
    public void updateDescription(String description) {
        mExperiment.description = description;
    }

    @Override
    public void updateName(String name) {
        mExperiment.name = name;
        setTitle(name);
    }

    @Override
    public void updateVersion(int version) {
        mExperiment.version = version;
    }

    /**
     * Look through all the experiment object containers to see if there are any
     * matches.
     * 
     * @param scopeLevel
     * @param callerKind
     * @param callerId
     * @param filter
     * @return
     */
    private boolean anyObjectsMatching(final int scopeLevel, final int callerKind,
            final long callerId, final int filter) {
        // We only need to know if a single object satisfies the filter so we
        // use early returns.
        if (scopeLevel == SCOPE_RULE) {
            final long ruleId = findRuleIdForDescendant(callerKind, callerId);

            // Only trigger events exist in the rule scope level.
            return triggerMatches(mExperiment.rules.get(ruleId), filter);
        } else if (scopeLevel == SCOPE_SCENE) {
            final long sceneId = findSceneIdForDescendant(callerKind, callerId);

            Scene scene = mExperiment.scenes.get(sceneId);

            if (scene.satisfiesFilter(filter)) {
                return true;
            }

            for (Long propId : scene.props) {
                Prop prop = mExperiment.props.get(propId);
                if (prop.satisfiesFilter(filter)) {
                    return true;
                }
            }

            return false;
        } else if (scopeLevel == SCOPE_LOOP) {
            final long loopId = findLoopIdForDescendant(callerKind, callerId);

            Loop loop = mExperiment.loops.get(loopId);
            if (loop.satisfiesFilter(filter)) {
                return true;
            }

            return false;
        } else if (scopeLevel == SCOPE_EXPERIMENT) {
            for (Entry<Long, DataChannel> entry : mExperiment.dataChannels.entrySet()) {
                if (entry.getValue().satisfiesFilter(filter)) {
                    return true;
                }
            }

            for (Entry<Long, Asset> entry : mExperiment.assets.entrySet()) {
                if (entry.getValue().satisfiesFilter(filter)) {
                    return true;
                }
            }

            for (Entry<Long, Generator> entry : mExperiment.generators.entrySet()) {
                if (entry.getValue().satisfiesFilter(filter)) {
                    return true;
                }
            }

            return false;
        } else {
            // No scope, or unknown scope.
            return false;
        }
    }

    private void deleteActionData(Long id) {
        mExperiment.actions.remove(id);
    }

    private void deleteAssetData(long id) {
        mExperiment.assets.remove(id);

        removeReferencesTo(ExperimentObject.KIND_ASSET, id);
    }

    private void deleteDataChannelData(DataChannel dataChannel) {
        // removeReferencesTo(ExperimentObjectReference.KIND_DATA_CHANNEL, id);
    }

    private void deleteGeneratorData(Long id) {
        mExperiment.generators.remove(id);

        removeReferencesTo(ExperimentObject.KIND_GENERATOR, id);
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

    private void deleteOperandData(Operand operand) {
        if (operand == null) {
            return;
        }

        if (operand instanceof OperandHolder) {
            ArrayList<Long> parameterIds = ((OperandHolder)operand).getOperands();
            for (Long parameterId : parameterIds) {
                deleteOperandData(mExperiment.operands.remove(parameterId));
            }
            return;
        }
    }

    private void deletePropData(Long id) {
        Prop prop = mExperiment.props.remove(id);

        if (prop != null) {
            // TODO: Cleanup prop properties.
        }
    }

    private void deleteRuleData(Long id) {
        Rule rule = mExperiment.rules.remove(id);

        if (rule != null) {
            for (Long aid : rule.actions) {
                deleteActionData(aid);
            }
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

    private boolean doDiscardAction() {
        Intent data = new Intent();
        data.putExtra(Args.EXPERIMENT_ID, mExperimentDelegate.getId());

        mExperimentDelegate.closeExperiment();
        setResult(RESULT_CANCELED, data);
        finish();
        return true;
    }

    private void doSaveAction() {
        storeExperiment();

        Intent data = new Intent();
        data.putExtra(Args.EXPERIMENT_ID, mExperimentDelegate.getId());

        mExperimentDelegate.closeExperiment();
        setResult(RESULT_OK, data);
    }

    private void doSelectSection(int position) {
        mSectionManager.selectSection(position);
        mDrawerList.setItemChecked(position, true);
        getActionBar().setSubtitle(mSectionManager.getTitle(position));
    }

    private ArrayList<Long> findAffectedCallIds(int kind, long id) {
        ArrayList<Long> affectedCallIds = new ArrayList<Long>();
        for (Entry<Long, Operand> entry : mExperiment.operands.entrySet()) {
            Operand value = entry.getValue();
            if (value instanceof CallValue) {
                CallValue call = (CallValue)value;
                if (call.getObject() != null && call.getObject().kind == kind
                        && call.getObject().id == id) {
                    affectedCallIds.add(entry.getKey());
                }
            }
        }
        return affectedCallIds;
    }

    private long findLoopIdForDescendant(final int callingComponentKind, final long componentId) {
        final long childId;
        switch (callingComponentKind) {
            case ExperimentObject.KIND_ACTION:
            case ExperimentObject.KIND_RULE:
            case ExperimentObject.KIND_SCENE:
                childId = findSceneIdForDescendant(callingComponentKind, componentId);
                break;

            case ExperimentObject.KIND_LOOP:
                return componentId;

            default:
                throw new RuntimeException(
                        "Trying to call in loop scope from a component that is not in scope.");
        }

        for (Entry<Long, Loop> loopEntry : mExperiment.loops.entrySet()) {
            Loop loop = loopEntry.getValue();
            for (Long id : loop.scenes) {
                if (id == childId) {
                    return loopEntry.getKey();
                }
            }
        }

        throw new RuntimeException("Did not find loop for child. Must have been orphaned.");
    }

    private long findRuleIdForDescendant(final int callerKind, final long callerId) {
        final long childId;
        switch (callerKind) {
            case ExperimentObject.KIND_ACTION:
                childId = callerId;
                break;

            case ExperimentObject.KIND_RULE:
                return callerId;

            default:
                throw new RuntimeException(
                        "Trying to call in rule scope from a component that is not in scope.");
        }

        for (Entry<Long, Rule> ruleEntry : mExperiment.rules.entrySet()) {
            Rule rule = ruleEntry.getValue();
            for (Long id : rule.actions) {
                if (id == childId) {
                    return ruleEntry.getKey();
                }
            }
        }

        throw new RuntimeException("Did not find rule for child. Must have been orphaned.");
    }

    private long findSceneIdForDescendant(final int callingComponentKind, final long componentId) {
        final long childId;
        switch (callingComponentKind) {
            case ExperimentObject.KIND_ACTION:
            case ExperimentObject.KIND_RULE:
                childId = findRuleIdForDescendant(callingComponentKind, componentId);
                break;

            case ExperimentObject.KIND_SCENE:
                return componentId;

            default:
                throw new RuntimeException(
                        "Trying to call in scene scope from a component that is not in scope.");
        }

        for (Entry<Long, Scene> sceneEntry : mExperiment.scenes.entrySet()) {
            Scene scene = sceneEntry.getValue();
            for (Long id : scene.rules) {
                if (id == childId) {
                    return sceneEntry.getKey();
                }
            }
        }

        throw new RuntimeException("Did not find scene for child. Must have been orphaned.");
    }

    private Long findUnusedKey(HashMap<Long, ?> map) {
        Long currKey = 0l;
        while (true) {
            if (!map.keySet().contains(currKey)) {
                break;
            }
            currKey++;
        }
        return currKey;
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

    private int getEffectiveScopeLevel(int callerKind, int relativeScope) {
        // Apply relative scope to implicit scope of the caller.
        int callerScope;
        switch (callerKind) {
            case ExperimentObject.KIND_ACTION:
            case ExperimentObject.KIND_RULE:
                callerScope = 0 + relativeScope;
                break;
            case ExperimentObject.KIND_SCENE:
                callerScope = 1 + relativeScope;
                break;
            case ExperimentObject.KIND_LOOP:
                callerScope = 2 + relativeScope;
                break;
            case ExperimentObject.KIND_EXPERIMENT:
                callerScope = 3 + relativeScope;
                break;

            default:
                throw new RuntimeException("Invalid object kind for object browsing.");

        }

        // Convert the desired caller scope into static value scope level.
        int effectiveScope;
        switch (callerScope) {
            case 0:
                effectiveScope = SCOPE_RULE;
                break;
            case 1:
                effectiveScope = SCOPE_SCENE;
                break;
            case 2:
                effectiveScope = SCOPE_LOOP;
                break;
            case 3:
                effectiveScope = SCOPE_EXPERIMENT;
                break;

            default:
                throw new RuntimeException("Unknown program component scope level requested.");
        }
        return effectiveScope;
    }

    private Experiment getExperimentFromDelegate() {
        try {
            return mExperimentDelegate.openExperiment();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    };

    private List<Pair<ExperimentObject, Long>> getObjectsMatching(int scopeLevel, int callerKind,
            long callerId, int filter) {

        List<Pair<ExperimentObject, Long>> objects = new ArrayList<Pair<ExperimentObject, Long>>();

        if (scopeLevel == SCOPE_RULE) {
            final long ruleId = findRuleIdForDescendant(callerKind, callerId);

            Rule rule = mExperiment.rules.get(ruleId);
            // Presently, there are only trigger events in the rule scope level.
            ExperimentObject eventObject = getTriggerEventDataForRule(rule);
            if (eventObject != null && eventObject.satisfiesFilter(filter)) {
                // Fake an id from the tagged event data kind so we can later
                // rebuild the right kind of event.
                Long fakeId = (long)eventObject.getTag();
                objects.add(new Pair<ExperimentObject, Long>(eventObject, fakeId));
            }
        } else if (scopeLevel == SCOPE_SCENE) {
            final long sceneId = findSceneIdForDescendant(callerKind, callerId);

            Scene scene = mExperiment.scenes.get(sceneId);

            if (scene.satisfiesFilter(filter)) {
                objects.add(new Pair<ExperimentObject, Long>(scene, sceneId));
            }

            for (Long propId : scene.props) {
                Prop prop = mExperiment.props.get(propId);
                if (prop.satisfiesFilter(filter)) {
                    objects.add(new Pair<ExperimentObject, Long>(prop, propId));
                }
            }
        } else if (scopeLevel == SCOPE_LOOP) {
            final long loopId = findLoopIdForDescendant(callerKind, callerId);

            Loop loop = mExperiment.loops.get(loopId);
            if (loop.satisfiesFilter(filter)) {
                objects.add(new Pair<ExperimentObject, Long>(loop, loopId));
            }
        } else if (scopeLevel == SCOPE_EXPERIMENT) {
            for (Entry<Long, DataChannel> entry : mExperiment.dataChannels.entrySet()) {
                DataChannel dataChannel = entry.getValue();
                if (dataChannel.satisfiesFilter(filter)) {
                    objects.add(new Pair<ExperimentObject, Long>(dataChannel, entry.getKey()));
                }
            }

            for (Entry<Long, Asset> entry : mExperiment.assets.entrySet()) {
                Asset asset = entry.getValue();
                if (asset.satisfiesFilter(filter)) {
                    objects.add(new Pair<ExperimentObject, Long>(asset, entry.getKey()));
                }
            }

            for (Entry<Long, Generator> entry : mExperiment.generators.entrySet()) {
                Generator generator = entry.getValue();
                if (generator.satisfiesFilter(filter)) {
                    objects.add(new Pair<ExperimentObject, Long>(generator, entry.getKey()));
                }
            }
        }
        return objects;
    }

    /**
     * Get any event data object associated with the trigger event this rule is
     * registered for.
     * 
     * @param rule Rule to check event data for.
     * @return Any event data object that will be created when the rule is
     *         triggered.
     */
    private ExperimentObject getTriggerEventDataForRule(Rule rule) {
        if (rule.triggerObject == null) {
            return null;
        }

        // Find the referenced trigger event, and pull the object that describes
        // the event, if any.
        ExperimentObject eventObject = null;
        for (Method method : getExperimentObject(rule.triggerObject).getClass().getMethods()) {
            EventData eventData = method.getAnnotation(EventData.class);
            if (eventData != null && eventData.id() == rule.triggerEvent) {
                if (eventData.type() == EventData.EVENT_TOUCH) {
                    eventObject = new TouchEvent();
                } else if (eventData.type() == EventData.EVENT_TOUCH_MOTION) {
                    eventObject = new TouchMotionEvent();
                }
                break;
            }
        }

        return eventObject;
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

    private void notifyDataChannelDataChange() {
        if (mDataChannelAdapter != null) {
            mDataChannelAdapter.notifyDataSetChanged();
        }

        if (mDataChannelDataChangeListeners != null) {
            for (DataChannelDataChangeListener listener : mDataChannelDataChangeListeners) {
                listener.onDataChannelDataChange();
            }
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
        for (int j = 0; j < mOperandAdapters.size(); j++) {
            // Check adapters currently existing for operand parents.
            ProgramComponentAdapter<Operand> adapter = mOperandAdapters.get(mOperandAdapters
                    .keyAt(j));

            for (Long operandId : adapter.getKeys()) {
                if (operandId == id) {
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    private void notifyOperandDataChangeListeners() {
        if (mOperandDataChangeListeners == null) {
            return;
        }

        for (OperandDataChangeListener listener : mOperandDataChangeListeners) {
            listener.onOperandDataChange();
        }
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
     * Look through the experiment program and remove any calls, and the
     * corresponding hierarchy, to the object.
     * 
     * @param kind The kind of the experiment object as indicated by constant
     *            int value from ExperimentObjectReference.KIND_*.
     * @param id The id of the experiment object for which references to are
     *            being removed.
     */
    private void removeReferencesTo(int kind, long id) {
        if (kind == ExperimentObject.KIND_ASSET) {
            // TODO: Something different.
        }

        ArrayList<Long> affectedCallIds = findAffectedCallIds(kind, id);

        for (Long key : affectedCallIds) {
            // There may be attempts to double delete some operands due to the
            // operand graph. However, this is okay because those calls will be
            // ignored.
            CallValue call = (CallValue)mExperiment.operands.get(key);
            StubOperand replacement = call.originalStub;
            if (replacement == null) {
                replacement = new StubOperand(call.name);
                replacement.attemptRestrictType(call.type);
            }
            deleteOperand(key);
            putOperand(key, replacement);
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
            experiment = getExperimentFromDelegate();
            // Store the experiment for persistence.
            mExperimentHolderFragment.setExperiment(experiment);
        }

        return experiment;
    }

    /**
     * Checks to see the given ored set of return types intersects with the
     * given method's return type.
     * 
     * @param method Method to test.
     * @param returnTypes Ored set of return types.
     * @return True if intersection.
     */
    private boolean returnTypeIntersects(Method method, int returnTypes) {
        if ((returnTypes & Type.TYPE_BOOLEAN) != 0 && method.getReturnType().equals(Boolean.TYPE)) {
            return true;
        }
        if ((returnTypes & Type.TYPE_INTEGER) != 0 && method.getReturnType().equals(Integer.TYPE)) {
            return true;
        }
        if ((returnTypes & Type.TYPE_FLOAT) != 0 && method.getReturnType().equals(Float.TYPE)) {
            return true;
        }
        if ((returnTypes & Type.TYPE_STRING) != 0 && method.getReturnType().equals(String.class)) {
            return true;
        }
        if (returnTypes == 0 && method.getReturnType().equals(Void.TYPE)) {
            return true;
        }
        return false;
    }

    private void selectSection(int section) {
        mDrawerList.setSelection(section);
        doSelectSection(section);
    }

    private void storeExperiment() {
        mExperiment.lastModified = System.currentTimeMillis();
        try {
            mExperimentDelegate.replace(mExperiment);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean triggerMatches(Rule rule, int filter) {
        if (rule.triggerObject == null) {
            return false;
        }

        // Find the referenced trigger event, and pull the object that describes
        // the event, if any.
        ExperimentObject eventObject = null;
        ExperimentObject experimentObject = getExperimentObject(rule.triggerObject);
        Method[] methods = experimentObject.getClass().getMethods();
        for (Method method : methods) {
            EventData eventData = method.getAnnotation(EventData.class);
            if (eventData != null && eventData.id() == rule.triggerEvent) {
                if (eventData.type() == EventData.EVENT_TOUCH) {
                    eventObject = new TouchEvent();
                } else if (eventData.type() == EventData.EVENT_TOUCH_MOTION) {
                    eventObject = new TouchMotionEvent();
                }
                break;
            }
        }

        if (eventObject == null) {
            return false;
        }

        return eventObject.satisfiesFilter(filter);
    }

    private void updateOperandAdapterIfExists(long scopeId, OperandHolder holder) {
        ProgramComponentAdapter<Operand> adapter;

        if (mOperandAdapters != null) {
            adapter = mOperandAdapters.get(scopeId);
            if (adapter != null) {
                adapter.setKeys(holder.getOperands());
                return;
            }
        }
    }

    /**
     * Look through the experiment program and make a best attempt to update
     * calls to the experiment object.
     * 
     * @param kind The kind of the experiment object as indicated by constant
     *            int value from ExperimentObjectReference.KIND_*.
     * @param id The id of the experiment object for which references to are
     *            being updated.
     */
    private void updateReferencesTo(int kind, long id) {
        switch (kind) {
            case ExperimentObject.KIND_ASSET:
                updateReferencesToAsset(id);
                return;
            case ExperimentObject.KIND_DATA_CHANNEL:
                updateReferencesToDataChannel(id);
                return;
            case ExperimentObject.KIND_GENERATOR:
                // Nothing to do because generators always have the same type
                // and
                // calling interface.
                return;

            default:
                break;
        }

    }

    private void updateReferencesToAsset(long id) {
        // TODO Auto-generated method stub
    }

    private void updateReferencesToDataChannel(long id) {
        DataChannel dataChannel = mExperiment.dataChannels.get(id);
        ArrayList<Long> calls = findAffectedCallIds(ExperimentObject.KIND_DATA_CHANNEL, id);

        for (Long callId : calls) {
            CallValue call = (CallValue)mExperiment.operands.get(callId);

            // Separate the parameters that will be retained from those that
            // will be discarded.
            LongSparseArray<Long> retainedParameterIds = new LongSparseArray<Long>();
            ArrayList<Long> discardedParameterIds = new ArrayList<Long>();
            for (Long parameterId : call.parameters) {
                Operand parameter = mExperiment.operands.get(parameterId);
                for (Field field : dataChannel.fields) {
                    if (parameter.tag == field.id) {
                        // Store parameters indexed by field id.
                        retainedParameterIds.put(field.id, parameterId);
                    } else {
                        discardedParameterIds.add(parameterId);
                    }
                }
            }

            // Delete discarded parameters.
            for (Long discardedId : discardedParameterIds) {
                deleteOperandData(mExperiment.operands.remove(discardedId));
            }

            // Update parameters and rebuild the order to match that of the
            // fields in the data channel.
            call.parameters = new ArrayList<Long>();
            for (Field field : dataChannel.fields) {
                // Attempt to update type of parameters to match fields,
                // otherwise replace parameters with stubs.
                long parameterId = retainedParameterIds.get(field.id);
                Operand parameter = mExperiment.operands.get(parameterId);
                parameter.name = field.name;
                if (!parameter.attemptRestrictType(field.type)) {
                    deleteOperandData(parameter);
                    StubOperand replacement = new StubOperand(field.name);
                    replacement.attemptRestrictType(field.type);
                    replacement.tag = field.id;
                    putOperand(parameterId, replacement);
                }
                call.parameters.add(parameterId);
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
        HashMap<Long, Asset> filteredAssets = new HashMap<Long, Asset>();
        for (Entry<Long, Asset> entry : filteredAssets.entrySet()) {
            long key = entry.getKey();
            Asset asset = mExperiment.assets.get(key);
            if (asset.satisfiesFilter(filter)) {
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

    public interface ActionDataChangeListener {
        void onActionDataChange();
    }

    public interface AssetDataChangeListener {
        void onAssetDataChange();
    }

    public interface DataChannelDataChangeListener {
        void onDataChannelDataChange();
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
            for (DrawerListener listener : mDrawerListeners) {
                listener.onDrawerClosed(view);
            }

            getActionBar().setSubtitle(
                    mSectionManager.getTitle(mSectionManager.getCurrentPosition()));
            invalidateOptionsMenu(); // creates call to
                                     // onPrepareOptionsMenu()
        }

        /** Called when a drawer has settled in a completely open state. */
        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            for (DrawerListener listener : mDrawerListeners) {
                listener.onDrawerOpened(drawerView);
            }

            getActionBar().setSubtitle(null);
            invalidateOptionsMenu(); // creates call to
                                     // onPrepareOptionsMenu()
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
            for (DrawerListener listener : mDrawerListeners) {
                listener.onDrawerStateChanged(newState);
            }
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
