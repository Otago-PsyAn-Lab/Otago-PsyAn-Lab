
package nz.ac.otago.psyanlab.common.designer.program.operand;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.OperandDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.program.object.PickObjectDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar.DialogueResultListener;
import nz.ac.otago.psyanlab.common.designer.util.MethodAdapter.MethodData;
import nz.ac.otago.psyanlab.common.designer.util.OperandListItemViewBinder;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.designer.util.RequestCodes;
import nz.ac.otago.psyanlab.common.model.ExperimentObject;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.CallValue;
import nz.ac.otago.psyanlab.common.model.operand.StubOperand;
import nz.ac.otago.psyanlab.common.model.operand.kind.CallOperand;
import nz.ac.otago.psyanlab.common.model.util.ModelUtils;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.ParameterId;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * A fragment that provides a UI to input a call value as an operand.
 */
public class EditCallOperandFragment extends AbsOperandFragment implements
        OperandDataChangeListener {
    private ViewHolder mViews;

    protected OnItemSelectedListener mActionMethodOnItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long methodId) {
            if (mCallValue.actionMethod != (int)methodId) {
                // Update operands for newly selected method.
                MethodData data = (MethodData)parent.getAdapter().getItem(position);
                Class<?>[] params = data.method.getParameterTypes();

                // Get the prop and instantiate our parameter name factory.
                ExperimentObject actionObject = mCallbacks
                        .getExperimentObject(mCallValue.actionObject);
                final NameResolverFactory parameterNameFactory = ModelUtils
                        .getParameterNameFactory(actionObject.getClass());

                ArrayList<Long> operands = new ArrayList<Long>();

                for (int parameterPosition = 0; parameterPosition < params.length; parameterPosition++) {
                    Class<?> parameterType = params[parameterPosition];

                    final ParameterId paramAnnotation = getParameterIdAnnotation(parameterPosition,
                            data.method);

                    // Sanity check; expect annotation is present for parameter.
                    if (paramAnnotation == null) {
                        throw new RuntimeException("Missing annotation for parameter "
                                + parameterPosition + " of method " + methodId + " on class "
                                + actionObject.getClass().getName());
                    }

                    final int parameterId = paramAnnotation.value();

                    // Switch upon the parameter type and reuse the old operand
                    // if it fits, or create a new operand to fill it.

                    if (parameterType.isAssignableFrom(float.class)) {
                        operands.add(createParameterOperand(parameterId, Operand.TYPE_FLOAT,
                                parameterNameFactory));
                    } else if (parameterType.isAssignableFrom(int.class)) {
                        operands.add(createParameterOperand(parameterId, Operand.TYPE_INTEGER,
                                parameterNameFactory));
                    } else if (parameterType.isAssignableFrom(String.class)) {
                        operands.add(createParameterOperand(parameterId, Operand.TYPE_STRING,
                                parameterNameFactory));
                    }
                }

                // Cleanup operands orphaned by matching process.
                for (Long opId : mCallValue.operands) {
                    if (!operands.contains(opId)) {
                        mCallbacks.deleteOperand(opId);
                    }
                }
                mCallValue.operands = operands;

                // Update operand with new method and type.
                mCallValue.actionMethod = (int)methodId;
                Class<?> returnType = data.method.getReturnType();
                if (returnType.equals(Boolean.TYPE)) {
                    mCallValue.type = Operand.TYPE_BOOLEAN;
                } else if (returnType.equals(Integer.TYPE)) {
                    mCallValue.type = Operand.TYPE_INTEGER;
                } else if (returnType.equals(Float.TYPE)) {
                    mCallValue.type = Operand.TYPE_FLOAT;
                } else if (returnType.equals(String.class)) {
                    mCallValue.type = Operand.TYPE_STRING;
                }

                mViews.updateViews(mCallValue);
            }
        }

        /**
         * Get the parameter id annotation for a parameter of the given method.
         * 
         * @param parameterPosition Position of parameter in method.
         * @param method Method to query.
         * @return
         */
        private ParameterId getParameterIdAnnotation(int parameterPosition, Method method) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int j = 0; j < parameterAnnotations.length; j++) {
                Annotation annotation = (ParameterId)parameterAnnotations[parameterPosition][j];
                if (annotation instanceof ParameterId) {
                    return (ParameterId)annotation;
                }
            }
            return null;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

        /**
         * Finds any existing operand assigned to the given parameter id and
         * attempts to reuse it for the given parameter. Otherwise, the supplied
         * operand is used.
         * 
         * @param parameterId Parameter id to match on.
         * @param newOperand New operand to use if the parameter doesn't have an
         *            existing operand, or the old operand can not be coerced to
         *            the required type.
         * @param desiredType Desired type for the final operand as required by
         *            the parameter.
         * @param nameFactory
         * @return Id of the operand that filled the parameter.
         */
        private long createParameterOperand(int parameterId, int desiredType,
                NameResolverFactory nameFactory) {
            String paramName = getString(nameFactory.getResId(parameterId));

            long operandId = -1;
            Operand operand = null;
            for (int i = 0; i < mCallValue.operands.size(); i++) {
                long currentId = mCallValue.operands.get(i);
                Operand currentOperand = mCallbacks.getOperand(currentId);
                if (currentOperand.getTag() == parameterId) {
                    operand = currentOperand;
                    operandId = currentId;
                    break;
                }
            }

            // Couldn't find a match so create a stub and use that.
            if (operand == null) {
                operand = new StubOperand(paramName);
            }

            // Try to coerce the type of the operand. If it fails create a new
            // stub one instead of reusing the old one.
            if (!operand.attemptRestrictType(desiredType)) {
                operandId = -1;
                operand = new StubOperand(paramName);
                if (!operand.attemptRestrictType(desiredType)) {
                    // If stub operands are failing to coerce correctly then we
                    // will actually attempt twice, ohwell.
                    throw new RuntimeException("Stub operand failed to bind to requested type "
                            + desiredType + ". This should not be possible.");
                }
            }

            operand.setTag(parameterId);
            if (operandId == -1) {
                return mCallbacks.createOperand(operand);
            }

            mCallbacks.updateOperand(operandId, operand);
            return operandId;
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

    protected OnClickListener mOperandActionObjectOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.pickExperimentObject(mSceneId, mCallValue.type,
                    RequestCodes.OPERAND_ACTION_OBJECT);
        }
    };

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
        if (mCallValue.actionMethod != CallValue.INVALID_METHOD) {
            mCallbacks.updateOperand(mObjectId, mCallValue);
        }
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
            actionObject.setOnClickListener(mOperandActionObjectOnClickListener);
            actionMethod.setOnItemSelectedListener(mActionMethodOnItemSelectedListener);

            mParameterAdapter = new ProgramComponentAdapter<Operand>(mCallbacks.getOperands(),
                    mCallValue.operands, new OperandListItemViewBinder(getActivity(), mCallbacks));
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
                    .getExperimentObject(operand.getActionObject()).getClass(), mOperandType);
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
