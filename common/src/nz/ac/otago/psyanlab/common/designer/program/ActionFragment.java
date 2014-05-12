
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.ActionDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.program.operand.EditCallOperandFragment;
import nz.ac.otago.psyanlab.common.model.Action;
import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    private Action mAction;

    private ViewHolder mViews;

    protected LayoutInflater mInflater;

    private long mSceneId;

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

        mAction = mCallbacks.getAction(mObjectId);
        mCallbacks.addActionDataChangeListener(this);

        mViews = new ViewHolder(view);
        mViews.setViewValues(mAction);
        mViews.initViews();

        Bundle args = getArguments();
        mSceneId = args.getLong(ARG_SCENE_ID);

        mOperandFragment = EditCallOperandFragment.init(new EditCallOperandFragment(), mSceneId,
                mAction.operandId, ExperimentObjectReference.HAS_SETTERS);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.operand_fragment_container, mOperandFragment, "OperandFragment");
        ft.commit();
    }

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

    private EditCallOperandFragment mOperandFragment;

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
        public TextView name;

        public ViewHolder(View view) {
            super(view);
            name = (EditText)view.findViewById(R.id.name);
        }

        @Override
        public void initViews() {
            name.addTextChangedListener(mNameWatcher);
        }

        @Override
        public void setViewValues(Action action) {
            name.setText(action.name);
        }

        public void updateViews(Action newAction, Action oldAction) {
            if (!TextUtils.equals(newAction.name, oldAction.name)) {
                name.setText(newAction.name);
            }
        }
    }
}
