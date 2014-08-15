
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
import nz.ac.otago.psyanlab.common.model.chansrc.Field;
import nz.ac.otago.psyanlab.common.model.util.Type;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;

public class EditDataColumnDialogueFragment extends DialogFragment {
    private static final String ARG_FIELD_ID = "arg_field_id";

    private static final String ARG_FIELD_NAME = "arg_field_name";

    private static final String ARG_FIELD_TYPE = "arg_field_type";

    private static final String ARG_POSITION = "arg_position";

    private static final int POSITION_UNASSIGNED = -1;

    private static final String ARG_REQUEST_CODE = "arg_request_code";

    public static final String RESULT_FIELD = "result_field";
    public static final String RESULT_POSITION = "result_position";

    public static EditDataColumnDialogueFragment init(EditDataColumnDialogueFragment fragment,
                                                      int requestCode, Field field) {
        return init(fragment, requestCode, field, POSITION_UNASSIGNED);
    }

    public static EditDataColumnDialogueFragment init(EditDataColumnDialogueFragment fragment,
                                                      int requestCode, Field field, int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putInt(ARG_POSITION, position);
        args.putInt(ARG_FIELD_ID, field.id);
        args.putInt(ARG_FIELD_TYPE, field.type);
        args.putString(ARG_FIELD_NAME, field.name);
        fragment.setArguments(args);
        return fragment;
    }

    private int mPosition;

    private ViewHolder mViews;

    private DialogueResultCallbacks mCallbacks;

    protected int mRequestCode;

    private OnClickListener mNegativeListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mCallbacks.onDialogueResultCancel(mRequestCode);
            dismiss();
        }
    };

    private OnClickListener mDeleteListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Bundle data = new Bundle();
            data.putParcelable(RESULT_FIELD, mField);
            data.putInt(RESULT_POSITION, mPosition);
            mCallbacks.onDialogueResultDelete(mRequestCode, data);
            dismiss();
        }
    };

    protected Field mField;

    private OnClickListener mPositiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mField.type = mViews.getType();
            mField.name = mViews.getName();
            if (mPosition == POSITION_UNASSIGNED) {
                Bundle data = new Bundle();
                data.putParcelable(RESULT_FIELD, mField);
                mCallbacks.onDialogueResult(mRequestCode, data);
            } else {
                Bundle data = new Bundle();
                data.putParcelable(RESULT_FIELD, mField);
                data.putInt(RESULT_POSITION, mPosition);
                mCallbacks.onDialogueResult(mRequestCode, data);
            }
            dismiss();
        }
    };

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
        Bundle args = getArguments();
        mPosition = args.getInt(ARG_POSITION);
        mField = new Field();
        mField.id = args.getInt(ARG_FIELD_ID);
        mField.type = args.getInt(ARG_FIELD_TYPE);
        mField.name = args.getString(ARG_FIELD_NAME);
        mRequestCode = args.getInt(ARG_REQUEST_CODE);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_edit_channel_field, null);

        mViews = new ViewHolder(view);
        mViews.setViewValues(getActivity(), mField);

        // Build dialogue.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_edit_channel_field).setView(view);
        if (mPosition == POSITION_UNASSIGNED) {
            builder.setPositiveButton(R.string.action_create, mPositiveListener).setNegativeButton(
                    R.string.action_discard, mNegativeListener);
        } else {
            builder.setPositiveButton(R.string.action_done, mPositiveListener)
                    .setNegativeButton(R.string.action_cancel, mNegativeListener)
                    .setNeutralButton(R.string.action_delete, mDeleteListener);
        }

        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return dialog;
    }

    private static class ViewHolder {
        private EditText mName;

        private Spinner mType;

        public ViewHolder(View view) {
            mName = (EditText) view.findViewById(R.id.name);
            mType = (Spinner) view.findViewById(R.id.type);
        }

        public String getName() {
            return mName.getText().toString();
        }

        public int getType() {
            return (Integer) mType.getSelectedItem();
        }

        public void setViewValues(Context context, Field field) {
            mName.setText(field.name);
            mType.setSelection(field.type);
            mType.setAdapter(new Type.TypeAdapter(context));
            mType.setSelection(Type.TypeAdapter.positionOf(field.type));
        }
    }
}
