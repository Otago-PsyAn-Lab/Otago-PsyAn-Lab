
package nz.ac.otago.psyanlab.common.designer.channel;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.channel.Field;
import nz.ac.otago.psyanlab.common.model.util.Type;

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

public class EditChannelFieldDialogueFragment extends DialogFragment {
    private static final String ARG_FIELD_ID = "arg_field_id";

    private static final String ARG_FIELD_NAME = "arg_field_name";

    private static final String ARG_FIELD_TYPE = "arg_field_type";

    private static final String ARG_POSITION = "arg_position";

    private static final int POSITION_UNASSIGNED = -1;

    private static final Listener sDummyOnDoneListener = new Listener() {
        @Override
        public void onEditFieldCanceled() {
        }

        @Override
        public void onFieldCreated(Field field) {
        }

        @Override
        public void onFieldDelete(int position) {
        }

        @Override
        public void onFieldEdited(int position, Field field) {
        }
    };

    public static EditChannelFieldDialogueFragment newDialog(Field field) {
        return newDialog(field, POSITION_UNASSIGNED);
    }

    public static EditChannelFieldDialogueFragment newDialog(Field field, int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putInt(ARG_FIELD_ID, field.id);
        args.putInt(ARG_FIELD_TYPE, field.type);
        args.putString(ARG_FIELD_NAME, field.name);
        EditChannelFieldDialogueFragment f = new EditChannelFieldDialogueFragment();
        f.setArguments(args);
        return f;
    }

    private OnClickListener mDeleteListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mOnDoneListener.onFieldDelete(mPosition);
            dismiss();
        }
    };

    private OnClickListener mNegativeListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mOnDoneListener.onEditFieldCanceled();
            dismiss();
        }
    };

    private Listener mOnDoneListener = sDummyOnDoneListener;

    private int mPosition;

    private OnClickListener mPositiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mField.type = mViews.getType();
            mField.name = mViews.getName();
            if (mPosition == POSITION_UNASSIGNED) {
                mOnDoneListener.onFieldCreated(mField);
            } else {
                mOnDoneListener.onFieldEdited(mPosition, mField);
            }
            dismiss();
        }
    };

    private ViewHolder mViews;

    protected Field mField;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        mPosition = args.getInt(ARG_POSITION);
        mField = new Field();
        mField.id = args.getInt(ARG_FIELD_ID);
        mField.type = args.getInt(ARG_FIELD_TYPE);
        mField.name = args.getString(ARG_FIELD_NAME);

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

    public void setOnDoneListener(Listener listener) {
        mOnDoneListener = listener;
    }

    public interface Listener {
        void onEditFieldCanceled();

        void onFieldCreated(Field field);

        void onFieldDelete(int position);

        void onFieldEdited(int position, Field field);
    }

    private static class ViewHolder {
        private EditText mName;

        private Spinner mType;

        public ViewHolder(View view) {
            mName = (EditText)view.findViewById(R.id.name);
            mType = (Spinner)view.findViewById(R.id.type);
        }

        public String getName() {
            return mName.getText().toString();
        }

        public int getType() {
            return (Integer)mType.getSelectedItem();
        }

        public void setViewValues(Context context, Field field) {
            mName.setText(field.name);
            mType.setSelection(field.type);
            mType.setAdapter(new Type.TypeAdapter(context));
            mType.setSelection(Type.TypeAdapter.positionOf(field.type));
        }
    }
}
