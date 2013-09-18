
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.stage.StageView.OnStageClickListener;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.util.Args;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Activity containing the stage editor. In this the user lays out props and
 * sets their initial properties.
 */
public class StageActivity extends FragmentActivity {
    private static final int REQUEST_EDIT_PROP = 0x01;

    private PropAdapter mPropAdapter;

    private ArrayList<Prop> mProps;

    private OnItemClickListener mPropClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> stage, View view, int position, long id) {
            onPropClicked(position);
        }
    };

    private OnStageClickListener mAddClickListener = new OnStageClickListener() {
        @Override
        public void onStageClick(StageView stage) {
            onAddClicked();
        }
    };

    private OnStageClickListener mEditClickListener = new OnStageClickListener() {
        @Override
        public void onStageClick(StageView stage) {
            onEditClicked();
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

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
        } else {
            mProps = new ArrayList<Prop>();
        }

        mPropAdapter = new PropAdapter(mProps);

        StageView stage = new StageView(this);
        stage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        stage.setAdapter(mPropAdapter);
        stage.setOnItemClickListener(mPropClickListener);
        stage.setOnStageClickListener(2, mEditClickListener);
        stage.setOnStageClickListener(3, mAddClickListener);
        setContentView(stage);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_EDIT_PROP:
                switch (resultCode) {
                    case RESULT_OK:
                        mProps = data.getParcelableArrayListExtra(Args.EXPERIMENT_PROPS);
                        mPropAdapter.setProps(mProps);
                        break;

                    default:
                        break;
                }

                break;

            default:
                break;
        }
    }

    /**
     * Open a dialogue to add a prop.
     */
    protected void onAddClicked() {
        Log.d("asdfadf", "add clicked");
        Intent intent = new Intent(this, EditPropActivity.class);
        startActivityForResult(intent, REQUEST_EDIT_PROP);
    }

    /**
     * Open a dialogue to edit props.
     */
    protected void onEditClicked() {
        Intent intent = new Intent(this, EditPropActivity.class);
        intent.putExtra(Args.EXPERIMENT_PROPS, mProps);
        startActivityForResult(intent, REQUEST_EDIT_PROP);
    }

    /**
     * Open a dialogue to edit a prop as indicated by position.
     * 
     * @param position Position of prop in the prop data set.
     */
    protected void onPropClicked(int position) {
        Intent intent = new Intent(this, EditPropActivity.class);
        intent.putExtra(Args.PROP_ID, position);
        intent.putExtra(Args.EXPERIMENT_PROPS, mProps);
        startActivityForResult(intent, REQUEST_EDIT_PROP);
    }

    /**
     * Call to handle event when the user wishes to exit the stage editor and
     * store the changes made.
     */
    protected void onConfirm() {
        Intent result = new Intent();
        result.putExtra(Args.EXPERIMENT_PROPS, mProps);
        result.putExtra(Args.SCENE_ID, getIntent().getIntExtra(Args.SCENE_ID, -1));
        setResult(RESULT_OK, result);
    }

    /**
     * Call to handle cancel event when the user wishes to exit the stage editor
     * and not store any changes.
     */
    protected void onCancel() {
        setResult(RESULT_CANCELED);
    }

    private final class PropAdapter extends BaseAdapter implements StageView.PropAdapter {
        private ArrayList<Prop> mProps;

        public void setProps(ArrayList<Prop> props) {
            mProps = props;
            notifyDataSetChanged();
        }

        public PropAdapter(ArrayList<Prop> props) {
            mProps = props;
        }

        @Override
        public int getCount() {
            return mProps.size();
        }

        @Override
        public Object getItem(int position) {
            return mProps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.stage_prop, parent, false);
            }

            ((TextView)convertView).setText(mProps.get(position).name);

            return convertView;
        }
    }
}
