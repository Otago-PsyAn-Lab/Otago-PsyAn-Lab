
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Loop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.NumberPicker;

public class EditConditionDialogFragment extends DialogFragment {
    private static final String ARG_ID = "arg_id";

    private static final long INVALID_ID = -1;

    /**
     * Create a new dialogue to edit the number of iterations a loop undergoes.
     */
    public static EditConditionDialogFragment newDialog(long id) {
        EditConditionDialogFragment f = new EditConditionDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        f.setArguments(args);
        return f;
    }

    private ProgramCallbacks mCallbacks;

    private long mId;

    private Loop mLoop;

    private ViewHolder mViews;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProgramCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (ProgramCallbacks)activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Bundle args = getArguments();
        if (args != null) {
            mId = args.getLong(ARG_ID, INVALID_ID);
        }

        if (mId == INVALID_ID) {
            throw new RuntimeException("Invalid loop id given.");
        }

        mLoop = mCallbacks.getLoop(mId);

        View view = inflater.inflate(R.layout.dialogue_designer_program_iteration, null);
        mViews = new ViewHolder(view);
        mViews.initViews();
        mViews.setViewValues(mLoop);

        // Build dialogue.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_edit_iterations).setView(view)
                .setPositiveButton(R.string.action_create, new OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        mLoop.iterations = mViews.iterations.getValue();
                        mCallbacks.updateLoop(mId, mLoop);
                    }

                }).setNegativeButton(R.string.action_discard, new OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }

                });

        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return dialog;
    }

    public class ViewHolder {
        public NumberPicker iterations;

        public ViewHolder(View view) {
            iterations = (NumberPicker)view.findViewById(R.id.iterations);
        }

        public void initViews() {
            iterations.setMinValue(1);
            iterations.setMaxValue(Integer.MAX_VALUE);
            iterations.setWrapSelectorWheel(false);
        }

        public void setViewValues(Loop loop) {
            iterations.setValue(loop.iterations);
        }
    }
}
