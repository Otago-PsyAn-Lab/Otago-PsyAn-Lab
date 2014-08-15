
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

package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.ActionDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.program.operand.EditCallOperandFragment;
import nz.ac.otago.psyanlab.common.model.Action;
import nz.ac.otago.psyanlab.common.model.ExperimentObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

public class ActionFragment extends BaseProgramFragment implements ActionDataChangeListener {

    private static final String ARG_SCENE_ID = "arg_scene_id";

    private Action mAction;

    public TextWatcher mNameWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String newName = s.toString();
            if (!TextUtils.equals(mAction.name, newName)) {
                mAction.name = newName;
                mCallbacks.putAction(mObjectId, mAction);
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

    private long mSceneId;

    private ViewHolder mViews;

    public static BaseProgramFragment newInstance(long id, long sceneId) {
        ActionFragment fragment = init(new ActionFragment(), id);
        Bundle args = fragment.getArguments();
        args.putLong(ARG_SCENE_ID, sceneId);
        fragment.setArguments(args);
        return fragment;
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
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

        mAction = mCallbacks.getAction(mObjectId);
        mCallbacks.addActionDataChangeListener(this);

        mViews = new ViewHolder(view);
        mViews.setViewValues(mAction);
        mViews.initViews();

        Bundle args = getArguments();
        mSceneId = args.getLong(ARG_SCENE_ID);

        mOperandFragment = EditCallOperandFragment.init(new EditCallOperandFragment(),
                ExperimentObject.KIND_ACTION, mObjectId, mAction.operandId,
                ExperimentObject.HAS_SETTERS);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.operand_fragment_container, mOperandFragment, "OperandFragment");
        ft.commit();
    }

    @Override
    protected int getFavouredBackground() {
        return R.color.card_background;
    }

    private void saveChanges() {
        mCallbacks.putAction(mObjectId, mAction);
    }

    @Override
    protected ViewHolder getViewHolder() {
        return mViews;
    }

    private class ViewHolder extends BaseProgramFragment.ViewHolder<Action> {

        public TextView name;

        public ViewHolder(View view) {
            super(view);
            name = (EditText) view.findViewById(R.id.name);

            @SuppressLint("WrongViewCast")
            final GridLayout gridLayout = (GridLayout) view.findViewById(R.id.background);
            final View operandFragmentContainer = view
                    .findViewById(R.id.operand_fragment_container);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    fixGridLayoutOverflow(gridLayout, operandFragmentContainer);
                }
            });
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
