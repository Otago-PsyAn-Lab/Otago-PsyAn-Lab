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

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.UserDelegateI;
import nz.ac.otago.psyanlab.common.UserExperimentDelegateI;
import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter.ViewBinder;
import nz.ac.otago.psyanlab.common.designer.assets.AssetTabFragmentsCallbacks;
import nz.ac.otago.psyanlab.common.designer.assets.AssetsFragment;
import nz.ac.otago.psyanlab.common.designer.assets.ImportAssetActivity;
import nz.ac.otago.psyanlab.common.designer.meta.MetaFragment;
import nz.ac.otago.psyanlab.common.designer.meta.MetaFragment.Details;
import nz.ac.otago.psyanlab.common.designer.program.ProgramCallbacks;
import nz.ac.otago.psyanlab.common.designer.program.ProgramFragment;
import nz.ac.otago.psyanlab.common.designer.subject.SubjectFragment;
import nz.ac.otago.psyanlab.common.model.Action;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.Experiment;
import nz.ac.otago.psyanlab.common.model.Generator;
import nz.ac.otago.psyanlab.common.model.LandingPage;
import nz.ac.otago.psyanlab.common.model.Loop;
import nz.ac.otago.psyanlab.common.model.Rule;
import nz.ac.otago.psyanlab.common.model.Scene;
import nz.ac.otago.psyanlab.common.util.Args;
import nz.ac.otago.psyanlab.common.util.ConfirmDialogFragment;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Provides an interface to the fragments implementing the UI whereby they can
 * manipulate the experiment data.
 */
public class ExperimentDesignerActivity extends FragmentActivity implements MetaFragment.Callbacks,
        SubjectFragment.Callbacks, AssetTabFragmentsCallbacks, ProgramCallbacks {

    private static final int MODE_EDIT = 0x02;

    private static final int MODE_NEW = 0x01;

    private static final int REQUEST_ASSET_IMPORT = 0x02;

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

    private AssetsAdapter mAssetsAdapter;

    private Pair<Long, ProgramComponentAdapter<Action>> mCurrentActionAdapter;

    private Pair<Long, ProgramComponentAdapter<Generator>> mCurrentGeneratorAdapter;

    private Pair<Long, ProgramComponentAdapter<Rule>> mCurrentRuleAdapter;

    private Pair<Long, ProgramComponentAdapter<Scene>> mCurrentSceneAdapter;

    private Experiment mExperiment;

    private UserExperimentDelegateI mExperimentDelegate;

    private ExperimentHolderFragment mExperimentHolderFragment;

    private ArrayList<GeneratorDataChangeListener> mGeneratorDataChangeListeners;

    private ViewBinder<Generator> mGeneratorListItemViewBinder = new ViewBinder<Generator>() {
        @Override
        public View bind(Generator generator, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_program_component,
                        parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            holder.textViews[0].setText(generator.name);
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

    private TabsAdapter mTabsAdapter;

    private UserDelegateI mUserDelegate;

    private ViewPager mViewPager;

    @Override
    public void addActionDataChangeListener(ActionDataChangeListener listener) {
        if (mActionDataChangeListeners == null) {
            mActionDataChangeListeners = new ArrayList<ActionDataChangeListener>();
        }
        mActionDataChangeListeners.add(listener);
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
        if (mExperiment.assets.containsKey(id)) {
            mExperiment.assets.remove(id);
            mAssetsAdapter.notifyDataSetChanged();
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
    public void displayAsset(long id) {
        // TODO: Detail activity here.
    }

    @Override
    public void doImportAsset() {
        Intent intent = new Intent(this, ImportAssetActivity.class);
        startActivityForResult(intent, REQUEST_ASSET_IMPORT);
    }

    @Override
    public void editStage(long objectId) {
        // TODO: Edit stage activity
    }

    @Override
    public Action getAction(long id) {
        return mExperiment.actions.get(id);
    }

    @Override
    public ListAdapter getActionAdapter(long ruleId) {
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
    public ListAdapter getAssetsAdapter() {
        return mAssetsAdapter;
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
    public Generator getGenerator(long id) {
        return mExperiment.generators.get(id);
    }

    @Override
    public ListAdapter getGeneratorAdapter(long loopId) {
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
    public ListAdapter getLoopAdapter() {
        if (mLoopAdapter != null) {
            return mLoopAdapter;
        }

        mLoopAdapter = new ProgramComponentAdapter<Loop>(mExperiment.loops,
                mExperiment.program.loops, mLoopListItemViewBinder);

        return mLoopAdapter;
    }

    @Override
    public Rule getRule(long id) {
        return mExperiment.rules.get(id);
    }

    @Override
    public ListAdapter getRuleAdapter(long sceneId) {
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
    public ListAdapter getScenesAdapter(long loopId) {
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
            case REQUEST_ASSET_IMPORT:
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
        dialog.show(getSupportFragmentManager(), "ConfirmDeleteDialog");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer);

        ActionBar actionBar = getActionBar();
        initActionBar(actionBar);

        mViewPager = (ViewPager)findViewById(R.id.pager);

        Bundle extras = getIntent().getExtras();
        mUserDelegate = extras.getParcelable(Args.USER_DELEGATE);
        if (extras.containsKey(Args.USER_DELEGATE)) {
            mExperimentDelegate = extras.getParcelable(Args.USER_EXPERIMENT_DELEGATE);
        }

        mExperimentHolderFragment = restoreExperimentHolder();
        mExperiment = restoreOrCreateExperiment(mExperimentHolderFragment);

        initTabs(actionBar);

        mAssetsAdapter = new AssetsAdapter(this, mExperiment.assets);

        convertScreen(mExperiment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.activity_experiment_designer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void removeActionDataChangeListener(ActionDataChangeListener listener) {
        mActionDataChangeListeners.remove(listener);
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
    public void removeRuleDataChangeListener(RuleDataChangeListener listener) {
        mRuleDataChangeListeners.remove(listener);
    }

    @Override
    public void removeSceneDataChangeListener(SceneDataChangeListener listener) {
        mSceneDataChangeListeners.remove(listener);
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
        notifyActionDataChangeListeners();
    }

    @Override
    public void updateAsset(long id, Asset asset) {
        if (mExperiment.assets.containsKey(id)) {
            mExperiment.assets.put(id, asset);
            mAssetsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateGenerator(long id, Generator generator) {
        mExperiment.generators.put(id, generator);
        notifyGeneratorDataChangeListeners();
        notifyLoopDataChangeListeners();
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

    /**
     * Converts the experiment reference screen to the current device. Changes
     * are logged and viewable as experiment meta-data.
     * 
     * @param experiment Experiment to convert.
     */
    private void convertScreen(Experiment experiment) {
        
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

    private Long findUnusedKey(HashMap<Long, ?> map) {
        Long currKey = 0l;
        while (true) {
            if (!map.containsKey(currKey)) {
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

    private void initActionBar(ActionBar actionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setTitle("New Experiment");
    }

    private void initTabs(ActionBar actionBar) {
        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.designer_tab_properties),
                MetaFragment.class, null);
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.designer_tab_subject),
                SubjectFragment.class, null);
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.designer_tab_assets),
                AssetsFragment.class, null);
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.designer_tab_program),
                ProgramFragment.class, null);
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

    public interface ActionDataChangeListener {
        void onActionDataChange();
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

    public interface RuleDataChangeListener {
        void onRuleDataChange();
    }

    public interface SceneDataChangeListener {
        void onSceneDataChange();
    }

    public static class TabsAdapter extends FragmentStatePagerAdapter implements
            ActionBar.TabListener, ViewPager.OnPageChangeListener {
        private final ActionBar mActionBar;

        private final Context mContext;

        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        private final ViewPager mViewPager;

        public TabsAdapter(FragmentActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mActionBar = activity.getActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        static final class TabInfo {
            private final Bundle args;

            private final Class<?> clss;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }
    }
}