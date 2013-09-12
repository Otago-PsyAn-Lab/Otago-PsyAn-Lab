
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
    private static final String TAG_CONTENT_FRAGMENT = "tag_content_fragment";

    private static final int INVALID_ID = -1;

    private EditPropDialogueFragment mContentFragment;

    private int mPropId = INVALID_ID;

    private OnNavigationListener mListNavigationListener = new OnNavigationListener() {
        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            onPropSelected(itemPosition, itemId);
            return true;
        }
    };

    private SpinnerAdapter mListNavigationAdapter;

    private ArrayList<Prop> mProps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                if (extras.containsKey(Args.EXPERIMENT_PROPS)) {
                    mProps = extras.getParcelableArrayList(Args.EXPERIMENT_PROPS);
                } else {
                    throw new RuntimeException("Expected props");
                }

                if (extras.containsKey(Args.PROP_ID)) {
                    mPropId = extras.getInt(Args.PROP_ID, INVALID_ID);
                }
            }
        } else {
            mProps = savedInstanceState.getParcelableArrayList(Args.EXPERIMENT_PROPS);
        }

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        params.width = (int)(displaySize.x * 0.8f);
        params.height = (int)(displaySize.y * 0.9f);
        params.dimAmount = 0.5f;
        getWindow().setAttributes(params);

        setContentView(R.layout.activity_edit_prop);

        mListNavigationAdapter = new ArrayAdapter<Prop>(this,
                android.R.layout.simple_spinner_dropdown_item, mProps);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(mListNavigationAdapter, mListNavigationListener);
        actionBar.setDisplayShowHomeEnabled(false);

        mContentFragment = (EditPropDialogueFragment)getSupportFragmentManager().findFragmentByTag(
                TAG_CONTENT_FRAGMENT);

        if (mContentFragment == null) {
            loadContent(mPropId);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(Args.EXPERIMENT_PROPS, mProps);
    }

    private void loadContent(int propId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mContentFragment = EditPropDialogueFragment.newInstance(propId);
        ft.replace(R.id.container, mContentFragment, TAG_CONTENT_FRAGMENT);
        ft.commit();
    }

    protected void onPropSelected(int itemPosition, long itemId) {
        loadContent(itemPosition);
    }

    @Override
    public Prop getProp(int id) {
        return mProps.get(id);
    }

    @Override
    public void saveProp(Prop prop) {
        mProps.add(prop);
    }
}
