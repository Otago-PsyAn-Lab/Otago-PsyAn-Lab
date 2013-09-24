
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.util.Args;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;

/**
 * A dialogue styled activity to edit or add a prop on a stage. The styling is
 * to provide a dialogue style interface with an ActionBar.
 */
public class EditPropActivity extends FragmentActivity implements
        EditPropDialogueFragment.Callbacks {
    private static final int INVALID_ID = -1;

    private static final int MODE_ADD = 0x01;

    private static final int MODE_EDIT = 0x02;

    private static final String TAG_CONTENT_FRAGMENT = "tag_content_fragment";

    private static final int RETURN_ARRAY = 0x01;

    private static final int RETURN_SINGLE = 0x02;

    public OnClickListener mOnConfirmListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onConfirm();
        }
    };

    public OnClickListener mOnDiscardListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onDiscard();
        }
    };

    private EditPropDialogueFragment mContentFragment;

    private ArrayAdapter<Prop> mListNavigationAdapter;

    private OnNavigationListener mListNavigationListener = new OnNavigationListener() {
        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            onPropSelected(itemPosition, itemId);
            return true;
        }
    };

    private int mMode;

    private int mPropId;

    private ArrayList<Prop> mProps;

    private ViewHolder mViews;

    private Prop mProp;

    private int mReturnKind;

    @Override
    public Prop getProp(int id) {
        return mProps.get(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMode == MODE_EDIT) {
            getMenuInflater().inflate(R.menu.activity_edit_prop, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_delete) {
            onDeleteProp();
        }

        return super.onOptionsItemSelected(item);
    }

    private void onDeleteProp() {
        mProps.remove(mPropId);
        if (mProps.size() == 0) {
            Intent data = new Intent();
            data.putExtra(Args.EXPERIMENT_PROPS, mProps);
            setResult(RESULT_OK, data);
            finish();
            return;
        }

        mListNavigationAdapter.notifyDataSetChanged();
        onPropSelected(0, 0);
    }

    @Override
    public void saveProp(Prop prop) {
        if (mMode == MODE_ADD) {
            mProp = prop;
        } else {
            mProps.add(prop);
        }
    }

    @Override
    public void saveProp(int propId, Prop prop) {
        if (mProps.size() <= propId) {
            return;
        }

        mProps.set(propId, prop);
        mListNavigationAdapter.notifyDataSetChanged();
    }

    private Point getDisplaySize() {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        return displaySize;
    }

    private void loadContent(int propId) {
        mPropId = propId;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mContentFragment = EditPropDialogueFragment.newInstance(propId);
        ft.replace(R.id.container, mContentFragment, TAG_CONTENT_FRAGMENT);
        ft.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey(Args.EXPERIMENT_PROPS)) {
                mProps = extras.getParcelableArrayList(Args.EXPERIMENT_PROPS);
                if (mProps.size() == 0) {
                    mMode = MODE_ADD;
                    mReturnKind = RETURN_ARRAY;
                    mPropId = INVALID_ID;
                } else {
                    mMode = MODE_EDIT;
                    mReturnKind = RETURN_ARRAY;
                    if (extras.containsKey(Args.PROP_ID)) {
                        mPropId = extras.getInt(Args.PROP_ID, 0);
                    }
                }
            } else {
                mMode = MODE_ADD;
                mReturnKind = RETURN_SINGLE;
                mPropId = INVALID_ID;
            }

        } else {
            mProps = savedInstanceState.getParcelableArrayList(Args.EXPERIMENT_PROPS);
        }

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = getWindow().getAttributes();

        Point displaySize = getDisplaySize();
        params.width = (int)(displaySize.x * 0.8f);
        params.height = (int)(displaySize.y * 0.9f);
        params.dimAmount = 0.5f;
        getWindow().setAttributes(params);

        setContentView(R.layout.activity_edit_prop);
        mViews = new ViewHolder(this);
        mViews.initViews();

        ActionBar actionBar = getActionBar();

        if (mMode == MODE_ADD) {
            actionBar.setTitle(R.string.title_new_prop);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        } else {
            actionBar.setTitle(R.string.title_edit_prop);
            mListNavigationAdapter = new ArrayAdapter<Prop>(getActionBar().getThemedContext(),
                    android.R.layout.simple_dropdown_item_1line, mProps);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setSelectedNavigationItem(mPropId);
            actionBar.setListNavigationCallbacks(mListNavigationAdapter, mListNavigationListener);
        }
        actionBar.setDisplayShowHomeEnabled(false);

        mContentFragment = (EditPropDialogueFragment)getSupportFragmentManager().findFragmentByTag(
                TAG_CONTENT_FRAGMENT);

        if (mContentFragment == null) {
            loadContent(mPropId);
        }
    }

    protected void onConfirm() {
        Intent data = new Intent();

        if (mContentFragment != null) {
            mContentFragment.doSave();
        }

        if (mMode == MODE_ADD && mReturnKind == RETURN_SINGLE) {
            data.putExtra(Args.EXPERIMENT_PROP, mProp);
        } else {
            if (mMode == MODE_ADD) {
                mProps = new ArrayList<Prop>();
                mProps.add(mProp);
            }
            data.putExtra(Args.EXPERIMENT_PROPS, mProps);
        }
        setResult(RESULT_OK, data);
        finish();
    }

    protected void onDiscard() {
        setResult(RESULT_CANCELED);
        finish();
    }

    protected void onPropSelected(int itemPosition, long itemId) {
        loadContent(itemPosition);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(Args.EXPERIMENT_PROPS, mProps);
    }

    class ViewHolder {
        private Button mConfirm;

        private Button mDiscard;

        public ViewHolder(Activity activity) {
            mConfirm = (Button)activity.findViewById(R.id.confirm);
            mDiscard = (Button)activity.findViewById(R.id.discard);
        }

        public void initViews() {
            mConfirm.setOnClickListener(mOnConfirmListener);
            mDiscard.setOnClickListener(mOnDiscardListener);
        }
    }
}
