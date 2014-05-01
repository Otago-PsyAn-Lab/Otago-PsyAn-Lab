
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.ActionDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.program.object.PickObjectDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.program.operand.EditOperandDialogFragment;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar.DialogueResultListener;
import nz.ac.otago.psyanlab.common.designer.util.MethodAdapter.MethodData;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.designer.util.RequestCodes;
import nz.ac.otago.psyanlab.common.model.Action;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.FloatValue;
import nz.ac.otago.psyanlab.common.model.operand.IntegerValue;
import nz.ac.otago.psyanlab.common.model.operand.StringValue;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class ActionFragment extends BaseProgramFragment implements ActionDataChangeListener {
    private static final String ARG_SCENE_ID = "arg_scene_id";

    public static BaseProgramFragment newInstance(long id, long sceneId) {
        ActionFragment fragment = init(new ActionFragment(), id);
        Bundle args = fragment.getArguments();
        args.putLong(ARG_SCENE_ID, sceneId);
        fragment.setArguments(args);
        return fragment;
    }

    protected OnItemSelectedListener mActionMethodOnItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mAction.actionMethod != (int)id) {
                // Update operands for newly selected method.
                MethodData data = (MethodData)parent.getAdapter().getItem(position);
                Class<?>[] params = data.method.getParameterTypes();

                for (int i = 0; i < params.length; i++) {
                    Class<?> param = params[i];
                    if (param.isAssignableFrom(float.class)) {
                        if (i < mAction.operands.size()) {
                            Long operandId = mAction.operands.get(i);
                            if (mCallbacks.getOperand(operandId).getType() != Operand.TYPE_FLOAT) {
                                mCallbacks.updateOperand(operandId, new FloatValue());
                            }
                        } else {
                            mAction.operands.add(mCallbacks.createOperand(new FloatValue()));
                        }
                    } else if (param.isAssignableFrom(int.class)) {
                        if (i < mAction.operands.size()) {
                            Long operandId = mAction.operands.get(i);
                            if (mCallbacks.getOperand(operandId).getType() != Operand.TYPE_INTEGER) {
                                mCallbacks.updateOperand(operandId, new IntegerValue());
                            }
                        } else {
                            mAction.operands.add(mCallbacks.createOperand(new IntegerValue()));
                        }
                    } else if (param.isAssignableFrom(String.class)) {
                        if (i < mAction.operands.size()) {
                            Long operandId = mAction.operands.get(i);
                            if (mCallbacks.getOperand(operandId).getType() != Operand.TYPE_STRING) {
                                mCallbacks.updateOperand(operandId, new StringValue());
                            }
                        } else {
                            mAction.operands.add(mCallbacks.createOperand(new StringValue()));
                        }
                    }
                }

                // Strip any trailing items.
                while (true) {
                    if (mAction.operands.size() == params.length) {
                        break;
                    }
                    mAction.operands.remove(params.length);
                }

                mAction.actionMethod = (int)id;
                mCallbacks.updateAction(mObjectId, mAction);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    public OnClickListener mActionObjectOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.pickExperimentObject(mSceneId, ExperimentObjectReference.HAS_SETTERS,
                    RequestCodes.OPERAND_ACTION_OBJECT);
        }
    };

    public TextWatcher mNameWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String newName = s.toString();
            if (!TextUtils.equals(mAction.name, newName)) {
                mAction.name = newName;
                mCallbacks.updateAction(mObjectId, mAction);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    protected OnItemClickListener mOnParameterItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Operand operand = mCallbacks.getOperand(id);
            showEditOperandDialogue(id, operand.getType());
        }
    };

    protected void showEditOperandDialogue(long id, int type) {
        EditOperandDialogFragment dialog = EditOperandDialogFragment.newDialog(mSceneId, id, type,
                getString(R.string.title_edit_operand, Operand.getTypeString(getActivity(), type)));
        dialog.show(getChildFragmentManager(), "dialog_edit_operand");
    }

    public ProgramComponentAdapter<Operand> mParameterAdapter;

    private Action mAction;

    private DialogueResultListener mActionObjectDialogueResultListener = new DialogueResultListener() {

        @Override
        public void onResult(Bundle data) {
            long objectId = data.getLong(PickObjectDialogueFragment.RESULT_OBJECT_ID);
            int objectKind = data.getInt(PickObjectDialogueFragment.RESULT_OBJECT_KIND);

            mAction.actionObject = new ExperimentObjectReference(objectKind, objectId);

            mCallbacks.updateAction(mObjectId, mAction);
        }
    };

    private long mSceneId;

    private ViewHolder mViews;

    protected LayoutInflater mInflater;

    @Override
    public void onActionDataChange() {
        Action old = mAction;
        mAction = mCallbacks.getAction(mObjectId);
        if (mAction == null) {
            removeSelf();
        }

        mViews.updateViews(mAction, old);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View v = inflater.inflate(R.layout.fragment_designer_program_action, container, false);
        ListView list = (ListView)v.findViewById(R.id.parameters);
        list.addHeaderView(inflater.inflate(R.layout.action_header_content, list, false));
        list.addFooterView(inflater.inflate(R.layout.separator, list, false));
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks.removeActionDataChangeListener(this);
        saveChanges();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSceneId = getArguments().getLong(ARG_SCENE_ID);

        mAction = mCallbacks.getAction(mObjectId);
        mCallbacks.addActionDataChangeListener(this);

        mParameterAdapter = mCallbacks.getOperandAdapter(mObjectId,
                ExperimentDesignerActivity.OPERAND_ACCESS_SCOPE_ACTION);

        mViews = new ViewHolder(view);
        mViews.setViewValues(mAction);
        mViews.initViews();

        mCallbacks.registerDialogueResultListener(RequestCodes.OPERAND_ACTION_OBJECT,
                mActionObjectDialogueResultListener);
    }

    @Override
    public void setIsLastInList(boolean isLastInList) {
    }

    private void saveChanges() {
        mCallbacks.updateAction(mObjectId, mAction);
    }

    @Override
    protected ViewHolder getViewHolder() {
        return mViews;
    }

    private class ViewHolder extends BaseProgramFragment.ViewHolder<Action> {
        public Spinner actionMethod;

        public Button actionObject;

        public TextView name;

        private ListView parametersList;

        public ViewHolder(View view) {
            super(view);
            name = (EditText)view.findViewById(R.id.name);
            actionObject = (Button)view.findViewById(R.id.action_object);
            actionMethod = (Spinner)view.findViewById(R.id.action_method);
            parametersList = (ListView)view.findViewById(R.id.parameters);
        }

        @Override
        public void initViews() {
            name.addTextChangedListener(mNameWatcher);

            actionObject.setOnClickListener(mActionObjectOnClickListener);
            actionMethod.setOnItemSelectedListener(mActionMethodOnItemSelectedListener);

            parametersList.setAdapter(mParameterAdapter);
            parametersList.setOnItemClickListener(mOnParameterItemClickListener);
            parametersList.setDivider(null);
        }

        @Override
        public void setViewValues(Action action) {
            name.setText(action.name);
            if (action.actionObject != null) {
                setAction(action);
            } else {
                unsetAction();
            }
        }

        public void updateViews(Action newAction, Action oldAction) {
            if (!TextUtils.equals(newAction.name, oldAction.name)) {
                name.setText(newAction.name);
            }
            if (newAction.actionObject != null) {
                setAction(newAction);
            } else {
                unsetAction();
            }
        }

        private void setAction(Action action) {
            actionObject.setText(mCallbacks.getExperimentObject(action.actionObject).getPrettyName(
                    getActivity()));
            actionMethod.setEnabled(true);
            SpinnerAdapter methodsAdapter = mCallbacks.getMethodsAdapter(mCallbacks
                    .getExperimentObject(action.actionObject).getClass(), Void.TYPE);
            actionMethod.setAdapter(methodsAdapter);
            for (int i = 0; i < methodsAdapter.getCount(); i++) {
                if ((int)methodsAdapter.getItemId(i) == mAction.actionMethod) {
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
