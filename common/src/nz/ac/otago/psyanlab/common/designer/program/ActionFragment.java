
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.ActionDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Action;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
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

    private Action mAction;

    private ViewHolder mViews;

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
        return inflater.inflate(R.layout.fragment_designer_program_action, container, false);
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

        mViews = new ViewHolder(view);
        mViews.setViewValues(mAction);
        mViews.initViews();
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

        public Spinner actionObject;

        public TextView name;

        private View parametersList;

        public ViewHolder(View view) {
            super(view);
            name = (EditText)view.findViewById(R.id.name);
            actionObject = (Spinner)view.findViewById(R.id.action_object);
            actionMethod = (Spinner)view.findViewById(R.id.action_method);
            parametersList = view.findViewById(R.id.parameters);
        }

        public void initViews() {
            name.addTextChangedListener(mNameWatcher);
        }

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
