/*
 * Copyright (C) 2014 Tonic Artos <tonic.artos@gmail.com>
 *
 * Otago PsyAn Lab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * In accordance with Section 7(b) of the GNU General Public License version 3,
 * all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.common.designer.variable;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity;
import nz.ac.otago.psyanlab.common.model.Variable;
import nz.ac.otago.psyanlab.common.model.variable.FloatVariable;
import nz.ac.otago.psyanlab.common.model.variable.IntegerVariable;
import nz.ac.otago.psyanlab.common.model.variable.StringVariable;

public class VariableDetailFragment extends Fragment {
    protected static final int POS_STRING = 0;

    protected static final int POS_INTEGER = 1;

    protected static final int POS_FLOAT = 2;

    private static final String ARG_VARIABLE_ID = "arg_variable_id";

    protected ViewHolder mViews;

    protected VariableCallbacks mCallbacks;

    protected long mVariableId;

    private View.OnClickListener mOnDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mCallbacks.deleteVariable(mVariableId);
            mOnVariableDeletedListener.onVariableDeleted(mVariableId);
        }
    };

    protected Variable mVariable;

    protected AdapterView.OnItemSelectedListener mOnKindSelectedListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Variable oldVariable = mVariable;
                    switch (i) {
                        case POS_FLOAT:
                            mVariable = new FloatVariable(mVariable);
                            break;
                        case POS_INTEGER:
                            mVariable = new IntegerVariable(mVariable);
                            break;
                        case POS_STRING:
                            mVariable = new StringVariable(mVariable);
                            break;
                        default:
                            throw new RuntimeException("Unknown variable kind " + l);
                    }
                    mCallbacks.putVariable(mVariableId, mVariable);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            };

    protected TextWatcher mDescriptionWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void afterTextChanged(Editable s) {
            String newDescription = s.toString();
            if (!TextUtils.equals(mVariable.note, newDescription)) {
                mVariable.note = newDescription;
                mCallbacks.putVariable(mVariableId, mVariable);
            }
        }
    };

    private TextWatcher mNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void afterTextChanged(Editable s) {
            String newName = s.toString();
            if (!TextUtils.equals(mVariable.name, newName)) {
                mVariable.name = newName;
                mCallbacks.putVariable(mVariableId, mVariable);
            }
        }
    };

    private ExperimentDesignerActivity.VariableDataChangeListener mDataChangeListener =
            new ExperimentDesignerActivity.VariableDataChangeListener() {
                @Override
                public void onVariableDataChange() {
                    Variable oldVariable = mVariable;
                    mVariable = mCallbacks.getVariable(mVariableId);

                    if (mVariable == null) {
                        return;
                    }

                    mViews.updateViews(mVariable, oldVariable);
                }
            };

    protected OnVariableDeletedListener mOnVariableDeletedListener;

    public static VariableDetailFragment newInstance(long id) {
        VariableDetailFragment f = new VariableDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_VARIABLE_ID, id);
        f.setArguments(args);
        return f;
    }

    public void setOnVariableDeletedListener(OnVariableDeletedListener listener) {
        mOnVariableDeletedListener = listener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof VariableCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (VariableCallbacks) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_variable_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        mVariableId = args.getLong(ARG_VARIABLE_ID);

        mVariable = mCallbacks.getVariable(mVariableId);

        mViews = new ViewHolder(view);
        mViews.initViews();
        mViews.setViewValues(mVariable);

        mCallbacks.addVariableDataChangeListener(mDataChangeListener);
    }

    public interface OnVariableDeletedListener {

        void onVariableDeleted(long id);
    }

    public class VariableType {
        public static final long TYPE_INTEGER = 0x01;

        public static final long TYPE_STRING = 0x02;

        public static final long TYPE_FLOAT = 0x03;

        private long mId;

        private int mLabelResId;

        public VariableType(long type, int labelResId) {
            mId = type;
            mLabelResId = labelResId;
        }

        public long getId() {
            return mId;
        }

        public int getLabelResId() {
            return mLabelResId;
        }

        @Override
        public String toString() {
            return getResources().getString(mLabelResId);
        }
    }

    private class ViewHolder {
        private final View mDelete;

        private final Spinner mKind;

        private final EditText mDescription;

        private final EditText mName;

        public ViewHolder(View view) {
            mKind = (Spinner) view.findViewById(R.id.kind);
            mDescription = (EditText) view.findViewById(R.id.description);
            mName = (EditText) view.findViewById(R.id.name);
            mDelete = view.findViewById(R.id.button_delete);
        }

        public void initViews() {
            mDescription.addTextChangedListener(mDescriptionWatcher);
            mName.addTextChangedListener(mNameWatcher);
            mDelete.setOnClickListener(mOnDeleteClickListener);

            VariableType[] types = new VariableType[3];
            types[POS_STRING] = new VariableType(VariableType.TYPE_STRING, R.string.label_string);
            types[POS_INTEGER] =
                    new VariableType(VariableType.TYPE_INTEGER, R.string.label_integer);
            types[POS_FLOAT] = new VariableType(VariableType.TYPE_FLOAT, R.string.label_float);

            ArrayAdapter<VariableType> typeAdapter = new ArrayAdapter<VariableType>(getActivity(),
                                                                                    android.R.layout.simple_list_item_1,
                                                                                    types);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mKind.setAdapter(typeAdapter);
            mKind.setOnItemSelectedListener(mOnKindSelectedListener);
        }

        public void setViewValues(Variable variable) {
            mDescription.setText(variable.note);
            mName.setText(variable.name);
            setKind(variable);
        }

        public void updateViews(Variable newVariable, Variable oldVariable) {
            if (!TextUtils.equals(newVariable.note, oldVariable.note)) {
                mDescription.setText(newVariable.note);
            }
            if (!TextUtils.equals(newVariable.name, oldVariable.name)) {
                mName.setText(newVariable.name);
            }
            setKind(newVariable);
        }

        private void setKind(Variable variable) {
            if (variable instanceof IntegerVariable) {
                mKind.setSelection(POS_INTEGER);
            } else if (variable instanceof StringVariable) {
                mKind.setSelection(POS_STRING);
            } else if (variable instanceof FloatVariable) {
                mKind.setSelection(POS_FLOAT);
            }
        }
    }
}
