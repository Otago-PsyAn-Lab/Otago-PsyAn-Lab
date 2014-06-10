
package nz.ac.otago.psyanlab.common.designer.program.operand;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.OperandDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.program.object.PickObjectDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.program.operand.ClearOperandDialogueFragment.OnClearListener;
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
import nz.ac.otago.psyanlab.common.model.util.Type;
import nz.ac.otago.psyanlab.common.util.TonicFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
        OperandDataChangeListener, OnClearListener {
    private ViewHolder mViews;

    protected CallValue mCallValue;

    protected OnItemSelectedListener mOnMethodItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long methodId) {
            if (mCallValue.method != (int)methodId) {
                // Update operands for newly selected method.
                MethodData data = (MethodData)parent.getAdapter().getItem(position);
                Class<?>[] params = data.method.getParameterTypes();

                // Get the prop and instantiate our parameter name factory.
                ExperimentObject actionObject = mCallbacks.getExperimentObject(mCallValue.object);
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
                        operands.add(createParameterOperand(parameterId, Type.TYPE_FLOAT,
                                parameterNameFactory));
                    } else if (parameterType.isAssignableFrom(int.class)) {
                        operands.add(createParameterOperand(parameterId, Type.TYPE_INTEGER,
                                parameterNameFactory));
                    } else if (parameterType.isAssignableFrom(String.class)) {
                        operands.add(createParameterOperand(parameterId, Type.TYPE_STRING,
                                parameterNameFactory));
                    }
                }

                // Cleanup operands orphaned by matching process.
                for (Long opId : mCallValue.parameters) {
                    if (!operands.contains(opId)) {
                        mCallbacks.deleteOperand(opId);
                    }
                }
                mCallValue.parameters = operands;

                // Update operand with new method and type.
                mCallValue.method = (int)methodId;
                Class<?> returnType = data.method.getReturnType();
                if (returnType.equals(Boolean.TYPE)) {
                    mCallValue.type = Type.TYPE_BOOLEAN;
                } else if (returnType.equals(Integer.TYPE)) {
                    mCallValue.type = Type.TYPE_INTEGER;
                } else if (returnType.equals(Float.TYPE)) {
                    mCallValue.type = Type.TYPE_FLOAT;
                } else if (returnType.equals(String.class)) {
                    mCallValue.type = Type.TYPE_STRING;
                }

                mViews.updateViews(mCallValue);
            }
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
            for (int i = 0; i < mCallValue.parameters.size(); i++) {
                long currentId = mCallValue.parameters.get(i);
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
                return mCallbacks.addOperand(operand);
            }

            mCallbacks.putOperand(operandId, operand);
            return operandId;
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
                Annotation annotation = parameterAnnotations[parameterPosition][j];
                if (annotation instanceof ParameterId) {
                    return (ParameterId)annotation;
                }
            }
            return null;
        }
    };

    protected OnItemClickListener mOnParameterItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Operand operand = mCallbacks.getOperand(id);
            showEditOperandDialogue(id, operand.getType());
        }
    };

    protected OnClickListener mOnPickObjectClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.registerDialogueResultListener(RequestCodes.OPERAND_ACTION_OBJECT,
                    mOperandActionDialogueResultListener);
            mCallbacks.pickExperimentObject(mCallerKind, mCallerId, mCallValue.type,
                    RequestCodes.OPERAND_ACTION_OBJECT);
        }
    };

    protected DialogueResultListener mOperandActionDialogueResultListener = new DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            mCallbacks.clearDialogueResultListener(RequestCodes.OPERAND_ACTION_OBJECT);
            long objectId = data.getLong(PickObjectDialogueFragment.RESULT_OBJECT_ID);
            int objectKind = data.getInt(PickObjectDialogueFragment.RESULT_OBJECT_KIND);

            mCallValue.object = new ExperimentObjectReference(objectKind, objectId);

            mViews.updateViews(mCallValue);
        }
    };

    protected OnItemLongClickListener mOperandItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            int viewType = parent.getAdapter().getItemViewType(position);
            if (viewType == ListView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                return false;
            }

            showClearOperandDialogue(id);
            return true;
        }
    };

    protected ProgramComponentAdapter<Operand> mParameterAdapter;

    protected long mSceneId;

    @Override
    public Operand initReplacement(Operand oldOperand) {
        StubOperand replacement = new StubOperand(oldOperand.getName());
        replacement.type = oldOperand.type;
        replacement.tag = oldOperand.tag;
        return replacement;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_call_operand, container, false);
        ListView list = (ListView)view.findViewById(R.id.parameters);
        list.addHeaderView(inflater.inflate(R.layout.header_edit_call_operand, list, false));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallbacks.removeOperandDataChangeListener(this);
    }

    @Override
    public void onOperandCleared() {
        // Nothing to do.
    }

    @Override
    public void onOperandDataChange() {
        mParameterAdapter.notifyDataSetChanged();
        mViews.updateParameterTitleVisibility();
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
    }

    @Override
    public void saveOperand() {
        if (mCallValue.method != CallValue.INVALID_METHOD) {
            mCallbacks.putOperand(mObjectId, mCallValue);
        }
    }

    protected void showClearOperandDialogue(long id) {
        ClearOperandDialogueFragment dialogue = ClearOperandDialogueFragment.newDialog(
                R.string.title_clear_variable, id);
        dialogue.setOnClearListener(this);
        dialogue.show(getChildFragmentManager(), "dialogue_clear_operand");
    }

    protected void showEditOperandDialogue(long id, int type) {
        EditOperandDialogFragment dialog = EditOperandDialogFragment.newDialog(mCallerKind,
                mCallerId, id, type,
                getString(R.string.title_edit_operand, Type.getTypeString(getActivity(), type)));
        dialog.show(getChildFragmentManager(), "dialog_edit_operand");
    }

    public class ViewHolder extends TonicFragment.ViewHolder<CallOperand> {
        private Spinner mMethod;

        private Button mObject;

        private ListView mParameters;

        private View mParameterTitle;

        public ViewHolder(View view) {
            mObject = (Button)view.findViewById(R.id.action_object);
            mMethod = (Spinner)view.findViewById(R.id.action_method);
            mParameters = (ListView)view.findViewById(R.id.parameters);
            mParameterTitle = view.findViewById(R.id.parameter_title);
        }

        @Override
        public void initViews() {
            mObject.setOnClickListener(mOnPickObjectClickListener);
            mMethod.setOnItemSelectedListener(mOnMethodItemSelectedListener);

            mParameterAdapter = new ProgramComponentAdapter<Operand>(mCallbacks.getOperands(),
                    mCallValue.parameters, new OperandListItemViewBinder(getActivity(), mCallbacks));
            mParameters.setAdapter(mParameterAdapter);
            mParameters.setOnItemClickListener(mOnParameterItemClickListener);
            mParameters.setOnItemLongClickListener(mOperandItemLongClickListener);

            updateParameterTitleVisibility();
        }

        @Override
        public void setViewValues(CallOperand operand) {
            ExperimentObjectReference actionObject = operand.getObject();
            if (actionObject != null) {
                setAction(operand);
            } else {
                unsetAction();
            }
        }

        public void updateParameterTitleVisibility() {
            if (mParameterAdapter.getCount() == 0) {
                mParameterTitle.setVisibility(View.GONE);
            } else {
                mParameterTitle.setVisibility(View.VISIBLE);
            }
        }

        public void updateViews(CallValue callValue) {
            if (callValue.object != null) {
                setAction(callValue);
            } else {
                unsetAction();
            }
            mParameterAdapter.setKeys(mCallValue.getOperands());

            updateParameterTitleVisibility();
        }

        private void setAction(CallOperand operand) {
            mObject.setText(mCallbacks.getExperimentObject(operand.getObject())
                    .getExperimentObjectName(getActivity()));
            mMethod.setEnabled(true);
            SpinnerAdapter methodsAdapter = mCallbacks.getMethodsAdapter(mCallbacks
                    .getExperimentObject(operand.getObject()).getClass(), mOperandType);
            mMethod.setAdapter(methodsAdapter);

            for (int i = 0; i < methodsAdapter.getCount(); i++) {
                if ((int)methodsAdapter.getItemId(i) == operand.getMethod()) {
                    mMethod.setSelection(i);
                    break;
                }
            }

        }

        private void unsetAction() {
            mObject.setText(null);
            mMethod.setEnabled(false);
            mMethod.setAdapter(null);
        }
    }
}
