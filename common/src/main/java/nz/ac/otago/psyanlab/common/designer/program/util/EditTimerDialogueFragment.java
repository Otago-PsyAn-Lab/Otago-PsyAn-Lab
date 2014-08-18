/*
 Copyright (C) 2014 Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.common.designer.program.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity;
import nz.ac.otago.psyanlab.common.designer.util.DialogueRequestCodes;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar;
import nz.ac.otago.psyanlab.common.designer.util.NumberPickerDialogueFragment;
import nz.ac.otago.psyanlab.common.model.Timer;
import nz.ac.otago.psyanlab.common.model.timer.Periodic;
import nz.ac.otago.psyanlab.common.model.timer.Reset;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

public class EditTimerDialogueFragment extends DialogFragment {
    private static final String ARG_IS_NEW = "arg_is_new";

    private static final String ARG_ID = "arg_id";

    public static EditTimerDialogueFragment newDialogue(long timerId, boolean isNew) {
        EditTimerDialogueFragment f = new EditTimerDialogueFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, timerId);
        args.putBoolean(ARG_IS_NEW, isNew);
        f.setArguments(args);
        return f;
    }

    private static class TimerKindAdapter extends BaseAdapter
            implements ListAdapter, SpinnerAdapter {
        final static private long[] sTypes =
                new long[]{Timer.TIMER_KIND_PERIODIC, Timer.TIMER_KIND_RESET};

        public static int positionOf(int type) {
            for (int i = 0; i < sTypes.length; i++) {
                if (sTypes[i] == type) {
                    return i;
                }
            }
            throw new RuntimeException("Unknown timer kind " + type);
        }

        private Context mContext;

        private LayoutInflater mInflater;

        public TimerKindAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return sTypes.length;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder) convertView.getTag();
            }

            CharSequence typeString = getTimerKindString(mContext, sTypes[position]);

            holder.textViews[0].setText(typeString);

            return convertView;
        }

        @Override
        public Long getItem(int position) {
            return sTypes[position];
        }

        @Override
        public long getItemId(int position) {
            return sTypes[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder) convertView.getTag();
            }

            holder.textViews[0].setText(getTimerKindString(mContext, sTypes[position]));

            return convertView;
        }

        private String getTimerKindString(Context context, long steppingKind) {
            if (steppingKind == Timer.TIMER_KIND_PERIODIC) {
                return context.getString(R.string.label_timer_kind_periodic);
            } else if (steppingKind == Timer.TIMER_KIND_RESET) {
                return context.getString(R.string.label_timer_kind_reset);
            } else {
                throw new RuntimeException("Unknown timer kind " + steppingKind);
            }
        }
    }

    protected ProgramCallbacks mCallbacks;

    protected Timer mTimer;

    protected AdapterView.OnItemSelectedListener mKindSelectedListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                                           long id) {
                    if (id == Timer.TIMER_KIND_PERIODIC) {
                        if (!(mTimer instanceof Periodic)) {
                            Periodic pt = new Periodic();
                            pt.name = mTimer.name;
                            pt.waitValue = mTimer.waitValue;
                            pt.iterations = -1;
                            mTimer = pt;
                        }
                    } else if (id == Timer.TIMER_KIND_RESET) {
                        if (!(mTimer instanceof Reset)) {
                            Reset rt = new Reset();
                            rt.name = mTimer.name;
                            rt.waitValue = mTimer.waitValue;
                            mTimer = rt;
                        }
                    }
                    mViews.setViewValues(mTimer);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            };

    private DialogueResultListenerRegistrar.DialogueResultListener mDialogueResultListener =
            new DialogueResultListenerRegistrar.DialogueResultListener() {
                @Override
                public void onResult(Bundle data) {
                    ((Periodic) mTimer).iterations =
                            data.getInt(NumberPickerDialogueFragment.RESULT_PICKED_NUMBER);
                    mViews.setViewValues(mTimer);
                    mCallbacks.clearDialogueResultListener(DialogueRequestCodes.ITERATION_NUMBER);
                }

                @Override
                public void onResultCancel() {
                    mCallbacks.clearDialogueResultListener(DialogueRequestCodes.ITERATION_NUMBER);
                }

                @Override
                public void onResultDelete(Bundle data) {
                    mCallbacks.clearDialogueResultListener(DialogueRequestCodes.ITERATION_NUMBER);
                }
            };

    protected View.OnClickListener mIterationsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mCallbacks.registerDialogueResultListener(DialogueRequestCodes.ITERATION_NUMBER,
                                                      mDialogueResultListener);
            NumberPickerDialogueFragment dialog = NumberPickerDialogueFragment
                    .newDialogue(R.string.title_edit_iterations, ((Periodic) mTimer).iterations, 1,
                                 DialogueRequestCodes.ITERATION_NUMBER, true);
            dialog.show(getChildFragmentManager(), "dialog_edit_iteration");
        }
    };

    protected long mTimerId;

    protected View.OnClickListener mConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mTimer = mViews.getViewValues(mTimer);
            mCallbacks.putTimer(mTimerId, mTimer);
            dismiss();
        }
    };

    protected View.OnClickListener mDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mCallbacks.deleteTimer(mTimerId);
            dismiss();
        }
    };

    protected ExperimentDesignerActivity.TimerDataChangeListener mTimerDataChangeListener =
            new ExperimentDesignerActivity.TimerDataChangeListener() {
                @Override
                public void onTimerDataChange() {
                    mTimer = mCallbacks.getTimer(mTimerId);
                    mViews.setViewValues(mTimer);
                }
            };

    protected ViewHolder mViews;

    protected View.OnClickListener mWaitValueClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private boolean mIsNew;

    protected View.OnClickListener mDiscardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mIsNew) {
                mCallbacks.deleteTimer(mTimerId);
            }
            dismiss();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProgramCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (ProgramCallbacks) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialogue_edit_timer, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks.removeTimerDataChangeListener(mTimerDataChangeListener);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            throw new RuntimeException("Expected arguments for edit timer dialogue.");
        }

        mTimerId = args.getLong(ARG_ID);
        mIsNew = args.getBoolean(ARG_IS_NEW);

        mTimer = mCallbacks.getTimer(mTimerId);

        mViews = new ViewHolder(view);
        mViews.initViews();
        mViews.setViewValues(mTimer);

        getDialog().setTitle(
                (mIsNew) ? R.string.title_dialogue_new_timer : R.string.title_dialogue_edit_timer);

        mCallbacks.addTimerDataChangeListener(mTimerDataChangeListener);
    }

    private class ViewHolder {
        private final Button mConfirm;

        private final View mDelete;

        private final View mDiscard;

        private final Button mIterations;

        private final View mIterationsLabel;

        private final Spinner mKind;

        private final EditText mName;

        private final EditText mWaitValue;

        public ViewHolder(View view) {
            mName = (EditText) view.findViewById(R.id.name);
            mWaitValue = (EditText) view.findViewById(R.id.wait_value);
            mIterations = (Button) view.findViewById(R.id.iterations);
            mIterationsLabel = view.findViewById(R.id.iterations_label);

            mKind = (Spinner) view.findViewById(R.id.kind);

            mDiscard = view.findViewById(R.id.discard);
            mConfirm = (Button) view.findViewById(R.id.confirm);
            mDelete = view.findViewById(R.id.delete);
        }

        public Timer getViewValues(Timer timer) {
            timer.name = mName.getText().toString();
            timer.waitValue = Long.parseLong(mWaitValue.getText().toString());
            return timer;
        }

        public void initViews() {
            mDiscard.setOnClickListener(mDiscardClickListener);
            mConfirm.setOnClickListener(mConfirmClickListener);
            mDelete.setOnClickListener(mDeleteClickListener);
            if (mIsNew) {
                mDelete.setVisibility(View.GONE);
                mConfirm.setText(R.string.action_create);
            } else {
                mDelete.setVisibility(View.VISIBLE);
            }

            mWaitValue.setOnClickListener(mWaitValueClickListener);
            mIterations.setOnClickListener(mIterationsClickListener);
            mKind.setOnItemSelectedListener(mKindSelectedListener);
            mKind.setAdapter(new TimerKindAdapter(getActivity()));
        }

        public void setViewValues(Timer timer) {
            if (timer == null) {
                return;
            }
            
            mName.setText(timer.name);
            mWaitValue.setText(String.valueOf(timer.waitValue));

            long kind = Timer.TIMER_KIND_PERIODIC;
            if (timer instanceof Periodic) {
                kind = Timer.TIMER_KIND_PERIODIC;
            } else if (timer instanceof Reset) {
                kind = Timer.TIMER_KIND_RESET;
            }
            for (int i = 0; i < mKind.getCount(); i++) {
                if (mKind.getItemIdAtPosition(i) == kind) {
                    mKind.setSelection(i);
                }
            }
            if (timer instanceof Periodic) {
                Periodic pt = (Periodic) timer;
                if (pt.iterations == -1) {
                    mIterations.setText(R.string.text_timer_infinite);
                } else {
                    mIterations.setText(getString(R.string.format_timer_iterations, pt.iterations));
                }
                mIterations.setVisibility(View.VISIBLE);
                mIterationsLabel.setVisibility(View.VISIBLE);
            } else {
                mIterations.setVisibility(View.GONE);
                mIterationsLabel.setVisibility(View.GONE);
            }
        }
    }
}
