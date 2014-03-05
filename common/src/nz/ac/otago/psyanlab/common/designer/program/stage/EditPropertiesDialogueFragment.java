
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Scene;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

public class EditPropertiesDialogueFragment extends DialogFragment {
    private static final String ARG_IS_DIALOGUE = "arg_is_dialogue";

    public static EditPropertiesDialogueFragment newDialogue() {
        EditPropertiesDialogueFragment f = new EditPropertiesDialogueFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_DIALOGUE, true);
        f.setArguments(args);
        return f;
    }

    public static EditPropertiesDialogueFragment newInstance() {
        EditPropertiesDialogueFragment f = new EditPropertiesDialogueFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    public OnClickListener mConfirmListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.setStageOrientation(mViews.orientation.getSelectedItemPosition());
            mCallbacks.refreshStage();
            dismiss();
        }
    };

    public OnClickListener mDiscardListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.refreshStage();
            getDialog().cancel();
        }
    };

    public OnItemSelectedListener mOrientationSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
            spinner.postDelayed(
                    new OrientationChange(
                            position == Scene.ORIENTATION_LANDSCAPE ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                    : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT), 33);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    private StageCallbacks mCallbacks;

    private boolean mIsDialogue;

    private ViewHolder mViews;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof StageCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (StageCallbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogue_edit_stage_properties, null);

        Bundle args = getArguments();
        if (args != null) {
            mIsDialogue = args.getBoolean(ARG_IS_DIALOGUE, false);
        }

        mViews = new ViewHolder(view);
        mViews.setViewValues();
        mViews.initViews();

        if (mIsDialogue) {
            getDialog().setTitle(R.string.title_edit_stage_properties);
        }

        return view;
    }

    public class ViewHolder {
        public View confirm;

        public View delete;

        public TextView dimensions;

        public View discard;

        public Spinner orientation;

        public ViewHolder(View view) {
            orientation = (Spinner)view.findViewById(R.id.orientation);
            dimensions = (TextView)view.findViewById(R.id.dimensions);

            discard = view.findViewById(R.id.discard);
            delete = view.findViewById(R.id.delete);
            confirm = view.findViewById(R.id.confirm);
        }

        public void initViews() {
            orientation.setOnItemSelectedListener(mOrientationSelectedListener);

            delete.setVisibility(View.GONE);

            discard.setOnClickListener(mDiscardListener);
            confirm.setOnClickListener(mConfirmListener);
        }

        public void setDimensions(int stageWidth, int stageHeight) {
            dimensions.setText(getResources().getString(R.string.format_screen_dimensions,
                    stageWidth, stageHeight));
        }

        public void setViewValues() {
            setDimensions(mCallbacks.getStageWidth(), mCallbacks.getStageHeight());
            orientation
                    .setSelection((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? 0
                            : 1);
        }
    }

    class OrientationChange implements Runnable {
        private int mOrientation;

        public OrientationChange(int orientation) {
            mOrientation = orientation;
        }

        @Override
        public void run() {
            getActivity().setRequestedOrientation(mOrientation);

            getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mViews.setDimensions(mCallbacks.getStageWidth(), mCallbacks.getStageHeight());
                }
            }, 150);
        }
    }
}
