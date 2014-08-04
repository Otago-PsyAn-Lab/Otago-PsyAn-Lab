
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
import android.widget.EditText;

public class EditStringDialogueFragment extends DialogFragment {
    public static final String RESULT_STRING = "result_string";

    private static final String ARG_LAYOUT = "arg_layout";

    private static final int NO_HINT = -1;

    protected static final String ARG_DEFAULT_VALUE = "arg_default_value";

    protected static final String ARG_DELETE_ENABLED = "arg_delete_enabled";

    protected static final String ARG_HINT = "arg_hint";

    protected static final String ARG_REQUEST_CODE = "arg_request_code";

    protected static final String ARG_TITLE = "arg_title";

    public static EditStringDialogueFragment init(EditStringDialogueFragment fragment,
            int titleResId, String defaultValue, int hintResId, int requestCode) {
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, titleResId);
        args.putInt(ARG_HINT, hintResId);
        args.putString(ARG_DEFAULT_VALUE, defaultValue);
        args.putInt(ARG_REQUEST_CODE, requestCode);
        fragment.setArguments(args);
        return fragment;
    }

    public static EditStringDialogueFragment init(EditStringDialogueFragment fragment,
            int titleResId, String defaultValue, int hintResId, int requestCode, int layoutResId) {
        init(fragment, titleResId, defaultValue, hintResId, requestCode);
        Bundle args = fragment.getArguments();
        args.putInt(ARG_LAYOUT, layoutResId);
        fragment.setArguments(args);
        return fragment;
    }

    private DialogueResultCallbacks mCallbacks;

    private OnClickListener mDeleteListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            mCallbacks.onDialogueResultDelete(mRequestCode, getResultDelete());
            dismiss();
        }
    };

    private OnClickListener mNegativeListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            mCallbacks.onDialogueResultCancel(mRequestCode);
            getDialog().cancel();
        }
    };

    private OnClickListener mPositiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            Bundle data = getResult();
            mCallbacks.onDialogueResult(mRequestCode, data);
            dismiss();
        }

    };

    private int mRequestCode;

    private ViewHolder mViews;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof DialogueResultCallbacks)) {
            throw new RuntimeException("Activity must implement dialogue result callbacks.");
        }
        mCallbacks = (DialogueResultCallbacks)activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        String defaultValue;
        int hintResId;
        int layoutResId;
        int titleResId;
        boolean deleteEnabled;

        Bundle args = getArguments();
        if (args != null) {
            defaultValue = args.getString(ARG_DEFAULT_VALUE, "");
            hintResId = args.getInt(ARG_HINT, NO_HINT);
            titleResId = args.getInt(ARG_TITLE, R.string.title_edit_string);
            layoutResId = args.getInt(ARG_LAYOUT, R.layout.dialogue_edit_string);
            deleteEnabled = args.getBoolean(ARG_DELETE_ENABLED, false);
            if (args.containsKey(ARG_REQUEST_CODE)) {
                mRequestCode = args.getInt(ARG_REQUEST_CODE);
            } else {
                throw new RuntimeException(
                        "Dialogue not properly initialised. Missing request code.");
            }
        } else {
            throw new RuntimeException("Dialogue not properly initialised.");
        }

        View view = inflater.inflate(layoutResId, null);
        mViews = new ViewHolder(view);
        mViews.initViews(hintResId);
        mViews.setViewValues(defaultValue);

        // Build dialogue.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titleResId).setView(view)
                .setPositiveButton(R.string.action_confirm, mPositiveListener)
                .setNegativeButton(R.string.action_discard, mNegativeListener);

        if (deleteEnabled) {
            builder.setNeutralButton(R.string.action_delete, mDeleteListener);
        }

        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return dialog;
    }

    protected Bundle getResult() {
        Bundle result = new Bundle();
        result.putString(RESULT_STRING, mViews.getUserString());
        return result;
    }

    protected Bundle getResultDelete() {
        return new Bundle();
    }

    public class ViewHolder {
        public EditText text;

        public ViewHolder(View view) {
            text = (EditText)view.findViewById(R.id.text);
        }

        public String getUserString() {
            return text.getText().toString();
        }

        public void initViews(int hintResId) {
            if (hintResId != NO_HINT) {
                text.setHint(hintResId);
            }
        }

        public void setViewValues(String defaultValue) {
            text.setText(defaultValue);
        }
    }
}
