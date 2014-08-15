
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
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class RenameOperandDialogueFragment extends DialogFragment {
    private static final String ARG_OPERAND_ID = "arg_operand_id";

    /**
     * Create a new dialogue to clear operand, or not.
     */
    public static RenameOperandDialogueFragment newDialog(long operandId) {
        RenameOperandDialogueFragment f = new RenameOperandDialogueFragment();
        Bundle args = new Bundle();
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

    private Operand mOperand;

    private long mOperandId;

    private OnClickListener mPositiveListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            String name = mName.getText().toString();
            String oldName = mOperand.name;
            mOperand.name = name;
            mCallbacks.putOperand(mOperandId, mOperand);
            mOnRenameListener.onRename(name, oldName);
            dismiss();
        }
    };

    protected EditText mName;

    private OnRenameListener mOnRenameListener;

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
            mOperandId = args.getLong(ARG_OPERAND_ID, -1);
        }

        if (mOperandId == -1) {
            throw new RuntimeException("Invalid operand id.");
        }

        mOperand = mCallbacks.getOperand(mOperandId);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_rename_variable, null);
        mName = (EditText)view.findViewById(R.id.name);
        mName.setText(mOperand.getName());

        // Thanks to serwus <http://stackoverflow.com/users/1598308/serwus>,
        // who posted at <http://stackoverflow.com/a/20325852>. Modified to
        // support unicode codepoints and validating first character of input.
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                    int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);

                int offset = 0;
                String s = source.toString();

                while (offset < s.length()) {
                    final int codePoint = s.codePointAt(offset);
                    if ((offset == 0 && isAllowedAsFirst(codePoint))
                            || (offset > 0 && isAllowed(codePoint))) {
                        sb.appendCodePoint(codePoint);
                    } else {
                        keepOriginal = false;
                    }
                    offset += Character.charCount(codePoint);
                }

                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned)source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isAllowed(int codePoint) {
                return Character.isLetterOrDigit(codePoint);
            }

            private boolean isAllowedAsFirst(int codePoint) {
                return Character.isLetter(codePoint);
            }
        };

        mName.setFilters(new InputFilter[] {
            filter
        });

        // Build dialogue.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.title_rename_variable, mOperand.getName()))
                .setView(view).setPositiveButton(R.string.action_rename, mPositiveListener)
                .setNegativeButton(R.string.action_cancel, mNegativeListener);

        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return dialog;
    }

    public void setOnRenameListener(OnRenameListener listener) {
        mOnRenameListener = listener;
    }

    public interface OnRenameListener {
        void onRename(String name, String oldName);
    }
}
