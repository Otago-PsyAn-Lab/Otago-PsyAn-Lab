
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.stage.StageView.OnStageClickListener;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.Scene;
import nz.ac.otago.psyanlab.common.util.Args;
import nz.ac.otago.psyanlab.common.util.ConfirmDialogFragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Activity containing the stage editor. In this the user lays out props and
 * sets their initial properties.
 */
public class StageActivity extends FragmentActivity implements StageCallbacks {
    private static final int MODE_FOREIGN = 0x01;

    private static final int MODE_NATIVE = 0x02;

    static final String DIALOGUE_ADD = "dialogue_add";

    static final String DIALOGUE_EDIT = "dialogue_edit";

    static final String DIALOGUE_PROPERTIES = "dialogue_properties";

    static final String DIALOGUE_SELECT_EDIT = "dialogue_select_edit";

    private OnStageClickListener mAddClickListener = new OnStageClickListener() {
        @Override
        public void onStageClick(StageView stage) {
            onAddClicked();
        }
    };

    private int mOrientation;

    private PropAdapter mPropAdapter;

    private OnItemClickListener mPropClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> stage, View view, int position, long id) {
            onPropClicked(position);
        }
    };

    private OnStageClickListener mPropertiesClickListener = new OnStageClickListener() {
        @Override
        public void onStageClick(StageView stage) {
            onPropertiesClicked();
        }
    };

    private ArrayAdapter<Prop> mPropListAdapter;

    private ArrayList<Prop> mProps;

    private OnStageClickListener mSelectClickListener = new OnStageClickListener() {
        @Override
        public void onStageClick(StageView stage) {
            onSelectClicked();
        }
    };

    private int mSpecifiedHeight;

    private int mSpecifiedWidth;

    private StageView mStage;

    @Override
    public void deleteProp(int propId) {
        mProps.remove(propId);

        if (mPropAdapter != null) {
            mPropAdapter.notifyDataSetChanged();
        }
        if (mPropListAdapter != null) {
            mPropListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public Prop getProp(int id) {
        return mProps.get(id);
    }

    @Override
    public ArrayAdapter<Prop> getPropAdapter() {
        if (mPropListAdapter == null) {
            mPropListAdapter = new ArrayAdapter<Prop>(this,
                    android.R.layout.simple_list_item_activated_1, mProps);
        }
        return mPropListAdapter;
    }

    @Override
    public int getPropNumber() {
        return findUnusedKey();
    }

    @Override
    public int getStageHeight() {
        return mStage.getNativeHeight();
    }

    @Override
    public int getStageMode() {
        if (mStage.getWidth() != mStage.getNativeWidth()
                || mStage.getHeight() == mStage.getNativeHeight()) {
            return MODE_FOREIGN;
        }
        return MODE_NATIVE;
    }

    @Override
    public int getStageOrientation() {
        return mOrientation;
    }

    @Override
    public int getStageWidth() {
        return mStage.getNativeWidth();
    }

    @Override
    public void onBackPressed() {
        DialogFragment dialog = ConfirmDialogFragment.newInstance(R.string.title_save_changes,
                R.string.action_save, R.string.action_cancel, R.string.action_discard,
                new ConfirmDialogFragment.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        onConfirm();
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
                        dialog.cancel();
                        finish();
                        dialog.dismiss();
                    }
                });
        dialog.show(getSupportFragmentManager(), "ConfirmDeleteDialog");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mStage.setNativeHeight(-1);
        mStage.setNativeWidth(-1);
    }

    @Override
    public void refreshStage() {
        if (mOrientation == Scene.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void saveProp(int propId, Prop prop) {
        mProps.set(propId, prop);

        if (mPropAdapter != null) {
            mPropAdapter.notifyDataSetChanged();
        }
        if (mPropListAdapter != null) {
            mPropListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void saveProp(Prop prop) {
        mProps.add(prop);

        if (mPropAdapter != null) {
            mPropAdapter.notifyDataSetChanged();
        }
        if (mPropListAdapter != null) {
            mPropListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setStageOrientation(int orientation) {
        mOrientation = orientation;
    }

    private int findUnusedKey() {
        int currKey = 1;
        for (Prop prop : mProps) {
            if (TextUtils.equals(
                    prop.name,
                    getString(R.string.format_default_prop_name,
                            getString(R.string.default_prop_name), currKey))) {
                currKey++;
            }
        }
        return currKey;
    }

    /**
     * Open a dialogue to add a prop.
     */
    protected void onAddClicked() {
        EditPropDialogueFragment dialogue = EditPropDialogueFragment.newAddDialogue();
        dialogue.show(getSupportFragmentManager(), DIALOGUE_ADD);
    }

    /**
     * Call to handle cancel event when the user wishes to exit the stage editor
     * and not store any changes.
     */
    protected void onCancel() {
        setResult(RESULT_CANCELED);
    }

    /**
     * Call to handle event when the user wishes to exit the stage editor and
     * store the changes made.
     */
    protected void onConfirm() {
        Intent result = new Intent();
        result.putExtra(Args.EXPERIMENT_PROPS, mProps);
        result.putExtra(Args.SCENE_ID, getIntent().getLongExtra(Args.SCENE_ID, -1));
        result.putExtra(Args.STAGE_WIDTH, mStage.getNativeWidth());
        result.putExtra(Args.STAGE_HEIGHT, mStage.getNativeHeight());
        result.putExtra(Args.STAGE_ORIENTATION, mOrientation);
        setResult(RESULT_OK, result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(Args.EXPERIMENT_PROPS)) {
                mProps = extras.getParcelableArrayList(Args.EXPERIMENT_PROPS);
            }

            mOrientation = extras
                    .getInt(Args.STAGE_ORIENTATION,
                            (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? Scene.ORIENTATION_LANDSCAPE
                                    : Scene.ORIENTATION_PORTRAIT);

            mSpecifiedWidth = extras.getInt(Args.STAGE_WIDTH, -1);
            mSpecifiedHeight = extras.getInt(Args.STAGE_HEIGHT, -1);
        } else {
            mProps = new ArrayList<Prop>();
        }

        refreshStage();

        mPropAdapter = new PropAdapter(this, mProps);

        View view = getLayoutInflater().inflate(R.layout.activity_stage, null);
        mStage = (StageView)view.findViewById(R.id.stage);
        mStage.setNativeWidth(mSpecifiedWidth);
        mStage.setNativeHeight(mSpecifiedHeight);
        mStage.setAdapter(mPropAdapter);
        mStage.setOnItemClickListener(mPropClickListener);
        mStage.setOnStageClickListener(2, mSelectClickListener);
        mStage.setOnStageClickListener(3, mAddClickListener);
        mStage.forceMultiTouchWhenEmpty(3);
        mStage.setOnStageClickListener(4, mPropertiesClickListener);
        mStage.exemptMultiTouchFromEmptyCondition(4);
        setContentView(view);
    }

    /**
     * Open a dialogue to edit a prop as indicated by position.
     * 
     * @param position Position of prop in the prop data set.
     */
    protected void onPropClicked(int position) {
        DialogFragment dialogue = EditPropDialogueFragment.newEditDialogue(position);
        dialogue.show(getSupportFragmentManager(), DIALOGUE_EDIT);
    }

    /**
     * Open a dialogue to edit the stage properties.
     */
    protected void onPropertiesClicked() {
        DialogFragment dialogue = EditPropertiesDialogueFragment.newDialogue();
        dialogue.show(getSupportFragmentManager(), DIALOGUE_PROPERTIES);
    }

    /**
     * Open a dialogue to select props for editing.
     */
    protected void onSelectClicked() {
        DialogFragment dialogue = SelectPropDialogueFragment.newDialogue();
        dialogue.show(getSupportFragmentManager(), DIALOGUE_SELECT_EDIT);
    }
}
