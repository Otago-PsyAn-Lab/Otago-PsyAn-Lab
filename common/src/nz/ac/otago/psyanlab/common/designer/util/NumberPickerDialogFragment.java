
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.R;

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

public class NumberPickerDialogFragment extends DialogFragment {
    private static final String ARG_DEFAULT_VALUE = "arg_default_value";

    private static final String ARG_MAX = "arg_max";

    private static final String ARG_MIN = "arg_min";

    private static final long INVALID_ID = -1;

    /**
     * Create a new dialogue to edit the number of iterations a loop undergoes.
     */
    public static NumberPickerDialogFragment newDialog(int defaultValue, int minValue) {
        NumberPickerDialogFragment f = new NumberPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DEFAULT_VALUE, defaultValue);
        args.putInt(ARG_MIN, minValue);
        f.setArguments(args);
        return f;
    }

    /**
     * Create a new dialogue to edit the number of iterations a loop undergoes.
     */
    public static NumberPickerDialogFragment newDialog(int defaultValue, int minValue, int maxValue) {
        NumberPickerDialogFragment f = new NumberPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DEFAULT_VALUE, defaultValue);
        args.putInt(ARG_MIN, minValue);
        args.putInt(ARG_MAX, maxValue);
        f.setArguments(args);
        return f;
    }

    private OnConfirmedValueListener mOnConfirmedValueListener;

    private ViewHolder mViews;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        int min = 0;
        int defaultValue = 0;
        int max = Integer.MAX_VALUE;

        Bundle args = getArguments();
        if (args != null) {
            defaultValue = args.getInt(ARG_DEFAULT_VALUE, 0);
            min = args.getInt(ARG_MIN, 0);
            max = args.getInt(ARG_MAX, Integer.MAX_VALUE);
        }

        View view = inflater.inflate(R.layout.dialogue_designer_program_iteration, null);
        mViews = new ViewHolder(view);
        mViews.initViews(min, max);
        mViews.setViewValues(defaultValue);

        // Build dialogue.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_edit_iterations).setView(view)
                .setPositiveButton(R.string.action_confirm, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mOnConfirmedValueListener == null) {
                            throw new RuntimeException("Missing confirm listener for dialog");
                        }
                        mOnConfirmedValueListener.onConfirmedValue(mViews.numberPicker.getValue());
                        dismiss();
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

    public void setOnConfirmedValueListener(OnConfirmedValueListener listener) {
        mOnConfirmedValueListener = listener;
    }

    public interface OnConfirmedValueListener {
        void onConfirmedValue(int value);
    }

    public class ViewHolder {
        public NumberPicker numberPicker;

        public ViewHolder(View view) {
            numberPicker = (NumberPicker)view.findViewById(R.id.iterations);
        }

        public void initViews(int min, int max) {
            numberPicker.setMinValue(min);
            numberPicker.setMaxValue(max);
            numberPicker.setWrapSelectorWheel(false);
        }

        public void setViewValues(int defaultValue) {
            numberPicker.setValue(defaultValue);
        }
    }
}
