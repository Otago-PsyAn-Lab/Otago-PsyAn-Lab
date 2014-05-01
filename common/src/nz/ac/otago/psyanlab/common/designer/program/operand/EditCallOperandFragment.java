
package nz.ac.otago.psyanlab.common.designer.program.operand;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.OperandDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.program.object.PickObjectDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar.DialogueResultListener;
import nz.ac.otago.psyanlab.common.designer.util.MethodAdapter.MethodData;
import nz.ac.otago.psyanlab.common.designer.util.OperandListItemViewBinder;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.designer.util.RequestCodes;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.CallValue;
import nz.ac.otago.psyanlab.common.model.operand.FloatValue;
import nz.ac.otago.psyanlab.common.model.operand.IntegerValue;
import nz.ac.otago.psyanlab.common.model.operand.StringValue;
import nz.ac.otago.psyanlab.common.model.operand.kind.CallOperand;
import nz.ac.otago.psyanlab.common.util.TonicFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * A fragment that provides a UI to input a call value as an operand.
 */
public class EditCallOperandFragment extends AbsOperandFragment implements
        OperandDataChangeListener {
    private ViewHolder mViews;

    protected OnItemSelectedListener mActionMethodOnItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mCallValue.actionMethod != (int)id) {
                // Update operands for newly selected method.
                MethodData data = (MethodData)parent.getAdapter().getItem(position);
                Class<?>[] params = data.method.getParameterTypes();

                for (int i = 0; i < params.length; i++) {
                    Class<?> param = params[i];
                    if (param.isAssignableFrom(float.class)) {
                        if (i < mCallValue.operands.size()) {
                            Long operandId = mCallValue.operands.get(i);
                            if (mCallbacks.getOperand(operandId).getType() != Operand.TYPE_FLOAT) {
                                mCallbacks.updateOperand(operandId, new FloatValue());
                            }
                        } else {
                            mCallValue.operands.add(mCallbacks.createOperand(new FloatValue()));
                        }
                    } else if (param.isAssignableFrom(int.class)) {
                        if (i < mCallValue.operands.size()) {
                            Long operandId = mCallValue.operands.get(i);
                            if (mCallbacks.getOperand(operandId).getType() != Operand.TYPE_INTEGER) {
                                mCallbacks.updateOperand(operandId, new IntegerValue());
                            }
                        } else {
                            mCallValue.operands.add(mCallbacks.createOperand(new IntegerValue()));
                        }
                    } else if (param.isAssignableFrom(String.class)) {
                        if (i < mCallValue.operands.size()) {
                            Long operandId = mCallValue.operands.get(i);
                            if (mCallbacks.getOperand(operandId).getType() != Operand.TYPE_STRING) {
                                mCallbacks.updateOperand(operandId, new StringValue());
                            }
                        } else {
                            mCallValue.operands.add(mCallbacks.createOperand(new StringValue()));
                        }
                    }
                }

                // Strip any trailing items.
                while (true) {
                    if (mCallValue.operands.size() == params.length) {
                        break;
                    }
                    mCallValue.operands.remove(params.length);
                }

                mCallValue.actionMethod = (int)id;
                mViews.updateViews(mCallValue);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    protected CallValue mCallValue;

    protected OnItemClickListener mOnParameterItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Operand operand = mCallbacks.getOperand(id);
            showEditOperandDialogue(id, operand.getType());
        }
    };

    protected DialogueResultListener mOperandActionDialogueResultListener = new DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            mCallbacks.clearDialogueResultListener(RequestCodes.OPERAND_ACTION_OBJECT);
            long objectId = data.getLong(PickObjectDialogueFragment.RESULT_OBJECT_ID);
            int objectKind = data.getInt(PickObjectDialogueFragment.RESULT_OBJECT_KIND);

            mCallValue.actionObject = new ExperimentObjectReference(objectKind, objectId);

            mViews.updateViews(mCallValue);
        }
    };

    protected OnClickListener mOperandActionOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.pickExperimentObject(mSceneId, mCallValue.type,
                    RequestCodes.OPERAND_ACTION_OBJECT);
        }
    };;

    protected ProgramComponentAdapter<Operand> mParameterAdapter;

    protected long mSceneId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_call_operand, container, false);
        ListView list = (ListView)view.findViewById(R.id.parameters);
        list.addHeaderView(inflater.inflate(R.layout.action_header_content, list, false));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallbacks.removeOperandDataChangeListener(this);
    }

    @Override
    public void onOperandDataChange() {
        mParameterAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Operand operand = mCallbacks.getOperand(mObjectId);
        if (operand instanceof CallValue) {
            mCallValue = (CallValue)operand;
        } else {
            mCallValue = new CallValue(operand);
        }

        mCallbacks.addOperandDataChangeListener(this);

        mViews = new ViewHolder(view);
        mViews.initViews();
        mViews.setViewValues(mCallValue);

        mCallbacks.registerDialogueResultListener(RequestCodes.OPERAND_ACTION_OBJECT,
                mOperandActionDialogueResultListener);
    }

    @Override
    public void saveOperand() {
        mCallbacks.updateOperand(mObjectId, mCallValue);
    }

    protected void showEditOperandDialogue(long id, int type) {
        EditOperandDialogFragment dialog = EditOperandDialogFragment.newDialog(mSceneId, id, type,
                getString(R.string.title_edit_operand, Operand.getTypeString(getActivity(), type)));
        dialog.show(getChildFragmentManager(), "dialog_edit_operand");
    }

    public class ViewHolder extends TonicFragment.ViewHolder<CallOperand> {
        public Spinner actionMethod;

        public Button actionObject;

        private ListView parameters;

        public ViewHolder(View view) {
            actionObject = (Button)view.findViewById(R.id.action_object);
            actionMethod = (Spinner)view.findViewById(R.id.action_method);
            parameters = (ListView)view.findViewById(R.id.parameters);
        }

        @Override
        public void initViews() {
            actionObject.setOnClickListener(mOperandActionOnClickListener);
            actionMethod.setOnItemSelectedListener(mActionMethodOnItemSelectedListener);

            mParameterAdapter = new ProgramComponentAdapter<Operand>(mCallbacks.getOperands(),
                    null, new OperandListItemViewBinder(getActivity(), mCallbacks));
            parameters.setAdapter(mParameterAdapter);
            parameters.setOnItemClickListener(mOnParameterItemClickListener);
            parameters.setDivider(null);
        }

        @Override
        public void setViewValues(CallOperand operand) {
            ExperimentObjectReference actionObject = operand.getActionObject();
            if (actionObject != null) {
                setAction(operand);
            } else {
                unsetAction();
            }
        }

        public void updateViews(CallValue callValue) {
            if (callValue.actionObject != null) {
                setAction(callValue);
            } else {
                unsetAction();
            }
            mParameterAdapter.setKeys(mCallValue.getOperands());
        }

        private void setAction(CallOperand operand) {
            actionObject.setText(mCallbacks.getExperimentObject(operand.getActionObject())
                    .getPrettyName(getActivity()));
            actionMethod.setEnabled(true);
            SpinnerAdapter methodsAdapter = mCallbacks.getMethodsAdapter(mCallbacks
                    .getExperimentObject(operand.getActionObject()).getClass(), Void.TYPE);
            actionMethod.setAdapter(methodsAdapter);
            for (int i = 0; i < methodsAdapter.getCount(); i++) {
                if ((int)methodsAdapter.getItemId(i) == operand.getActionMethod()) {
                    actionMethod.setSelection(i);
                    break;
                }
            }

        }

        private void unsetAction() {
            actionObject.setText(null);
            actionMethod.setEnabled(false);
            actionMethod.setAdapter(null);
        }
    }
}
