
package nz.ac.otago.psyanlab.common.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ConfirmDialogFragment extends DialogFragment {
    public static final String TAG = "ConfirmDialogFragment";

    private static final String ARG_MESSAGE = "arg_message";

    private static final String ARG_NEGATIVE = "arg_negative";

    private static final String ARG_NEUTRAL = "arg_neutral";

    private static final String ARG_POSITIVE = "arg_positive";

    private static final String ARG_TITLE = "arg_title";

    private static final OnClickListener sDummy = new OnClickListener() {
        @Override
        public void onClick(Dialog dialog) {
        }
    };

    public static ConfirmDialogFragment newInstance(int title, int message, int positiveLabel,
            int negativeLabel, int neutralLabel, OnClickListener positiveClickListener,
            OnClickListener negativeClickListener, OnClickListener neutralClickListener) {
        ConfirmDialogFragment fragment = ConfirmDialogFragment.newInstance(positiveLabel,
                negativeLabel, neutralLabel, positiveClickListener, negativeClickListener,
                neutralClickListener);
        Bundle args = fragment.getArguments();
        args.putInt(ARG_MESSAGE, message);
        args.putInt(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public static DialogFragment newInstance(int title, int message, int positiveLabel,
            int negativeLabel, OnClickListener positiveClickListener,
            OnClickListener negativeClickListener) {
        ConfirmDialogFragment fragment = ConfirmDialogFragment.newInstance(positiveLabel,
                negativeLabel, positiveClickListener, negativeClickListener);
        Bundle args = fragment.getArguments();
        args.putInt(ARG_MESSAGE, message);
        args.putInt(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public static ConfirmDialogFragment newInstance(int title, int positiveLabel,
            int negativeLabel, int neutralLabel, OnClickListener positiveClickListener,
            OnClickListener negativeClickListener, OnClickListener neutralClickListener) {
        ConfirmDialogFragment fragment = ConfirmDialogFragment.newInstance(positiveLabel,
                negativeLabel, neutralLabel, positiveClickListener, negativeClickListener,
                neutralClickListener);
        Bundle args = fragment.getArguments();
        args.putInt(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public static DialogFragment newInstance(int title, int positiveLabel, int negativeLabel,
            OnClickListener positiveClickListener, OnClickListener negativeClickListener) {
        ConfirmDialogFragment fragment = ConfirmDialogFragment.newInstance(positiveLabel,
                negativeLabel, positiveClickListener, negativeClickListener);
        Bundle args = fragment.getArguments();
        args.putInt(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public static ConfirmDialogFragment newInstance(int positiveLabel, int negativeLabel,
            int neutralLabel, OnClickListener positiveClickListener,
            OnClickListener negativeClickListener, OnClickListener neutralClickListener) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITIVE, positiveLabel);
        args.putInt(ARG_NEGATIVE, negativeLabel);
        args.putInt(ARG_NEUTRAL, neutralLabel);
        fragment.setArguments(args);
        fragment.setOnPositiveClickListener(positiveClickListener);
        fragment.setOnNegativeClickListener(negativeClickListener);
        fragment.setOnNeutralClickListener(neutralClickListener);
        return fragment;
    }

    public static ConfirmDialogFragment newInstance(int positiveLabel, int negativeLabel,
            OnClickListener positiveClickListener, OnClickListener negativeClickListener) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITIVE, positiveLabel);
        args.putInt(ARG_NEGATIVE, negativeLabel);
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
        int positive = arguments.getInt(ARG_POSITIVE);
        int negative = arguments.getInt(ARG_NEGATIVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (arguments.containsKey(ARG_TITLE)) {
            builder.setTitle(arguments.getInt(ARG_TITLE));
        }
        if (arguments.containsKey(ARG_MESSAGE)) {
            builder.setMessage(arguments.getInt(ARG_MESSAGE));
        }
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                mPositiveListener.onClick(getDialog());
            }
        }).setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
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
