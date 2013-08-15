
package nz.ac.otago.psyanlab.common.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ConfirmDialogFragment extends DialogFragment {
    private static final String ARG_NEGATIVE = "arg_negative";

    private static final String ARG_NEUTRAL = "arg_neutral";

    private static final String ARG_POSITIVE = "arg_positive";

    private static final String ARG_TITLE = "arg_title";

    private static final OnClickListener sDummy = new OnClickListener() {
        @Override
        public void onClick(Dialog dialog) {
        }
    };

    public static ConfirmDialogFragment newInstance(int title, int positive, int negative,
            int neutral, OnClickListener positiveClickListener,
            OnClickListener negativeClickListener, OnClickListener neutralClickListener) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, title);
        args.putInt(ARG_POSITIVE, positive);
        args.putInt(ARG_NEGATIVE, negative);
        args.putInt(ARG_NEUTRAL, neutral);
        fragment.setArguments(args);
        fragment.setOnPositiveClickListener(positiveClickListener);
        fragment.setOnNegativeClickListener(negativeClickListener);
        fragment.setOnNeutralClickListener(neutralClickListener);
        return fragment;
    }

    public static ConfirmDialogFragment newInstance(int title, int positive, int negative,
            OnClickListener positiveClickListener, OnClickListener negativeClickListener) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, title);
        args.putInt(ARG_POSITIVE, positive);
        args.putInt(ARG_NEGATIVE, negative);
        fragment.setArguments(args);
        fragment.setOnPositiveClickListener(positiveClickListener);
        fragment.setOnNegativeClickListener(negativeClickListener);
        return fragment;
    }

    private OnClickListener mNegativeListener = sDummy;

    private OnClickListener mNeutralListener;

    private OnClickListener mPositiveListener = sDummy;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        int title = arguments.getInt(ARG_TITLE);
        int positive = arguments.getInt(ARG_POSITIVE);
        int negative = arguments.getInt(ARG_NEGATIVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setPositiveButton(positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mPositiveListener.onClick(getDialog());
            }
        }).setNegativeButton(negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mNegativeListener.onClick(getDialog());
            }
        });

        if (mNeutralListener != null) {
            int neutral;
            neutral = arguments.getInt(ARG_NEUTRAL);
            builder.setNeutralButton(neutral, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mNeutralListener.onClick(getDialog());
                }
            });
        }

        return builder.create();
    }

    private void setOnNegativeClickListener(OnClickListener listener) {
        mNegativeListener = listener;
    }

    private void setOnNeutralClickListener(OnClickListener listener) {
        mNeutralListener = listener;
    }

    private void setOnPositiveClickListener(OnClickListener listener) {
        mPositiveListener = listener;
    }

    public interface OnClickListener {
        void onClick(Dialog dialog);
    }
}
