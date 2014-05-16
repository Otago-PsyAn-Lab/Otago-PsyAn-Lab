
package nz.ac.otago.psyanlab.common.designer.program.operand;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.util.OperandCallbacks;
import nz.ac.otago.psyanlab.common.model.Operand;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager;

public class ClearOperandDialogueFragment extends DialogFragment {
    private static final String ARG_OPERAND_ID = "arg_operand_id";

    private static final String ARG_TITLE = "arg_title";

    private static final OnClearListener sDummyListener = new OnClearListener() {
        @Override
        public void OnClearOperand() {
        }

        @Override
        public Operand initReplacement(Operand oldOperand) {
            return oldOperand;
        }
    };

    /**
     * Create a new dialogue to clear operand, or not.
     */
    public static ClearOperandDialogueFragment newDialog(int titleResId, long operandId) {
        ClearOperandDialogueFragment f = new ClearOperandDialogueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, titleResId);
        args.putLong(ARG_OPERAND_ID, operandId);
        f.setArguments(args);
        return f;
    }

    private OperandCallbacks mCallbacks;

    private OnClickListener mNegativeListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            getDialog().cancel();
        }
    };

    private OnClearListener mOnClearListener = sDummyListener;

    private Operand mOperand;

    private long mOperandId;

    private OnClickListener mPositiveListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            // Do this to clear up any potential hierarchy.
            mCallbacks.deleteOperand(mOperandId);
            // Put the replacement into the known position (id).
            mCallbacks.updateOperand(mOperandId, mOnClearListener.initReplacement(mOperand));
            mOnClearListener.OnClearOperand();
            dismiss();
        }
    };

    private int mTitleResId;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OperandCallbacks)) {
            throw new RuntimeException("Activity must implement operand callbacks.");
        }
        mCallbacks = (OperandCallbacks)activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mTitleResId = args.getInt(ARG_TITLE, R.string.title_pick_number);
            mOperandId = args.getLong(ARG_OPERAND_ID, -1);
        }

        if (mOperandId == -1) {
            throw new RuntimeException("Invalid operand id.");
        }

        mOperand = mCallbacks.getOperand(mOperandId);

        // Build dialogue.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(mTitleResId, mOperand.getName()))
                .setMessage(getString(R.string.message_clear_operand, mOperand.getName()))
                .setPositiveButton(R.string.action_clear, mPositiveListener)
                .setNegativeButton(R.string.action_cancel, mNegativeListener);

        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return dialog;
    }

    public void setOnClearListener(OnClearListener listener) {
        mOnClearListener = listener;
    }

    public interface OnClearListener {
        void OnClearOperand();

        /**
         * Initialise replacement operand for the 'clear' operation.
         * 
         * @param oldOperand Operand that will be replaced.
         * @return
         */
        Operand initReplacement(Operand oldOperand);
    }
}
