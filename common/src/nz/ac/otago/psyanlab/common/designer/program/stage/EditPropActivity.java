
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.util.Args;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

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

    private EditPropDialogueFragment mContentFragment;

    private SpinnerAdapter mListNavigationAdapter;

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

    @Override
    public Prop getProp(int id) {
        return mProps.get(id);
    }

    @Override
    public void saveProp(Prop prop) {
        if (mMode == MODE_ADD) {
            // TODO:
        } else {
            mProps.add(prop);
        }
    }

    @Override
    public void setProp(int propId, Prop prop) {
        mProps.set(propId, prop);
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
                    mPropId = INVALID_ID;
                } else {
                    mMode = MODE_EDIT;
                    if (extras.containsKey(Args.PROP_ID)) {
                        mPropId = extras.getInt(Args.PROP_ID, 0);
                    }
                }
            } else {
                mMode = MODE_ADD;
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

        ActionBar actionBar = getActionBar();

        if (mMode == MODE_ADD) {
            actionBar.setTitle(R.string.title_new_prop);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        } else {
            mListNavigationAdapter = new ArrayAdapter<Prop>(this,
                    android.R.layout.simple_spinner_dropdown_item, mProps);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setListNavigationCallbacks(mListNavigationAdapter, mListNavigationListener);
        }
        actionBar.setDisplayShowHomeEnabled(false);

        mContentFragment = (EditPropDialogueFragment)getSupportFragmentManager().findFragmentByTag(
                TAG_CONTENT_FRAGMENT);

        if (mContentFragment == null) {
            loadContent(mPropId);
        }
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
            mProps.remove(mPropId);
        }

        return super.onOptionsItemSelected(item);
    }
  
    protected void onPropSelected(int itemPosition, long itemId) {
        loadContent(itemPosition);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(Args.EXPERIMENT_PROPS, mProps);
    }
}
