
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

package nz.ac.otago.psyanlab.common.designer.subject;

import nz.ac.otago.psyanlab.common.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class EditOptionDialogueFragment extends DialogFragment {
    private static final String ARG_POSITION = "arg_position";

    private static final String ARG_TEXT = "arg_text";

    private static final OnDoneListener sDummyListener = new OnDoneListener() {
        @Override
        public void onDone(int position, String text) {
            Log.w("OPAL", "onDone for edit option dummy listener called.");
        }

        @Override
        public void onDelete(int position) {
            Log.w("OPAL", "onDelete for edit option dummy listener called.");
        }
    };

    public static EditOptionDialogueFragment getDialogue() {
        EditOptionDialogueFragment f = new EditOptionDialogueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, -1);
        f.setArguments(args);
        return f;
    }

    public static EditOptionDialogueFragment getDialogue(int position, String text) {
        EditOptionDialogueFragment f = new EditOptionDialogueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putInt(ARG_POSITION, position);
        f.setArguments(args);
        return f;
    }

    private OnClickListener mNegativeListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dismiss();
        }
    };

    private OnClickListener mPositiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mDoneListener.onDone(mPosition, mViews.getText());
            dismiss();
        }
    };

    private OnClickListener mNeutralListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mDoneListener.onDelete(mPosition);
            dismiss();
        }
    };

    protected OnDoneListener mDoneListener = sDummyListener;

    protected int mPosition;

    protected ViewHolder mViews;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_edit_option, null);
        mViews = new ViewHolder(view);

        int confirmText;
        int title;
        Bundle args = getArguments();
        if (args.containsKey(ARG_TEXT)) {
            if (savedInstanceState == null) {
                mViews.setText(args.getString(ARG_TEXT));
            }
            confirmText = R.string.action_confirm;
            title = R.string.title_edit_option;
        } else {
            confirmText = R.string.action_create;
            title = R.string.title_new_option;
        }
        mPosition = args.getInt(ARG_POSITION);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setView(view).setPositiveButton(confirmText, mPositiveListener)
                .setNegativeButton(R.string.action_discard, mNegativeListener);
        if (mPosition >= 0) {
            builder.setNeutralButton(R.string.action_delete, mNeutralListener);
        }

        return builder.create();
    }

    public void setOnDoneListener(OnDoneListener listener) {
        mDoneListener = listener;
    }

    public interface OnDoneListener {
        void onDone(int position, String text);

        void onDelete(int position);
    }

    protected class ViewHolder {
        private TextView mText;

        public ViewHolder(View view) {
            mText = (TextView)view.findViewById(R.id.text);
        }

        public String getText() {
            return mText.getText().toString();
        }

        public void setText(String text) {
            mText.setText(text);
        }

    }
}
