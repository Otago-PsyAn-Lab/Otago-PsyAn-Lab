/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.R;

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
import android.widget.TextView;

public class NumberPickerDialogueFragment extends DialogFragment {
    public static final String RESULT_PICKED_NUMBER = "result_number";

    private static final String ARG_DEFAULT_VALUE = "arg_default_value";

    private static final String ARG_MAX = "arg_max";

    private static final String ARG_MIN = "arg_min";

    private static final String ARG_REQUEST_CODE = "arg_request_code";

    private static final String ARG_TITLE = "arg_title";

    /**
     * Create a new dialogue to edit the number of iterations a loop undergoes.
     */
    public static NumberPickerDialogueFragment newDialog(int titleResId, int defaultValue,
                                                         int minValue, int maxValue,
                                                         int requestCode) {
        NumberPickerDialogueFragment f = new NumberPickerDialogueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, titleResId);
        args.putInt(ARG_DEFAULT_VALUE, defaultValue);
        args.putInt(ARG_MIN, minValue);
        args.putInt(ARG_MAX, maxValue);
        args.putInt(ARG_REQUEST_CODE, requestCode);
        f.setArguments(args);
        return f;
    }

    /**
     * Create a new dialogue to edit the number of iterations a loop undergoes.
     */
    public static NumberPickerDialogueFragment newDialogue(int titleResId, int defaultValue,
                                                           int minValue, int requestCode) {
        return newDialog(titleResId, defaultValue, minValue, Integer.MAX_VALUE, requestCode);
    }

    private DialogueResultCallbacks mCallbacks;

    private OnClickListener mNegativeListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            getDialog().cancel();
        }
    };

    private int mRequestCode;

    private OnClickListener mNeutralListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Bundle data = new Bundle();
            data.putInt(RESULT_PICKED_NUMBER, -1);
            mCallbacks.onDialogueResult(mRequestCode, data);
            dismiss();
        }
    };

    private OnClickListener mPositiveListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            Bundle data = new Bundle();
            data.putInt(RESULT_PICKED_NUMBER, mViews.numberPicker.getValue());
            mCallbacks.onDialogueResult(mRequestCode, data);
            dismiss();
        }
    };

    private boolean mShowRange = false;

    private ViewHolder mViews;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof DialogueResultCallbacks)) {
            throw new RuntimeException("Activity must implement dialogue result callbacks.");
        }
        mCallbacks = (DialogueResultCallbacks) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        int min = 0;
        int defaultValue = 0;
        int max = Integer.MAX_VALUE;
        int title = R.string.title_pick_number;

        Bundle args = getArguments();
        if (args != null) {
            defaultValue = args.getInt(ARG_DEFAULT_VALUE, 0);
            min = args.getInt(ARG_MIN, 0);
            max = args.getInt(ARG_MAX, Integer.MAX_VALUE);
            title = args.getInt(ARG_TITLE, R.string.title_pick_number);
            mRequestCode = args.getInt(ARG_REQUEST_CODE);
        }

        View view = inflater.inflate(R.layout.dialogue_number_picker, null);
        mViews = new ViewHolder(view);
        mViews.initViews(min, max);
        mViews.setViewValues(defaultValue);

        // Build dialogue.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setView(view)
               .setPositiveButton(R.string.action_confirm, mPositiveListener)
               .setNeutralButton("Infinite", mNeutralListener)
               .setNegativeButton(R.string.action_discard, mNegativeListener);

        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.getWindow()
              .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return dialog;
    }

    public void showRange(boolean showRange) {
        mShowRange = showRange;
        if (mViews != null) {
            mViews.range.setVisibility(mShowRange ? View.VISIBLE : View.GONE);
        }
    }

    public class ViewHolder {
        public NumberPicker numberPicker;

        public TextView range;

        public ViewHolder(View view) {
            numberPicker = (NumberPicker) view.findViewById(R.id.picker);
            range = (TextView) view.findViewById(R.id.range);
        }

        public void initViews(int min, int max) {
            numberPicker.setMinValue(min);
            numberPicker.setMaxValue(max);
            numberPicker.setWrapSelectorWheel(false);
            range.setText(getString(R.string.format_number_range, min, max));
            range.setVisibility(mShowRange ? View.VISIBLE : View.GONE);
        }

        public void setViewValues(int defaultValue) {
            numberPicker.setValue(defaultValue);
        }
    }
}
