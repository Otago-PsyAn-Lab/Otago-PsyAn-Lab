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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * In accordance with Section 7(b) of the GNU General Public License version 3,
 * all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.common.designer.variable;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Variable;
import nz.ac.otago.psyanlab.common.model.variable.StringVariable;

public class VariableListFragment extends Fragment {

    private static final ShowVariableListener sDummy = new ShowVariableListener() {
        @Override
        public void showVariable(long id) {
        }
    };

    private ShowVariableListener mShowVariableListener = sDummy;

    protected View.OnClickListener mAddVariableClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Variable variable = new StringVariable();
            long id = mCallbacks.addVariable(variable);
            variable.name = getString(R.string.default_name_new_variable, id + 1);
            mCallbacks.putVariable(id, variable);

            mShowVariableListener.showVariable(id);

            mViews.selectId(id);
        }
    };

    protected AdapterView.OnItemClickListener mOnItemClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mShowVariableListener.showVariable(id);
            mRootView.setBackgroundResource(R.color.card_background);
        }
    };

    private VariableCallbacks mCallbacks;

    private View mRootView;

    private ViewHolder mViews;

    public void deselectItem() {
        mRootView.setBackgroundResource(R.drawable.background_designer_program_default);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_variable_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRootView = view;

        mViews = new ViewHolder(view);
        mViews.initViews();
        updateBackground();
    }

    public void setShowVariableListener(ShowVariableListener listener) {
        mShowVariableListener = listener;
    }

    private void updateBackground() {
        if (mViews.mList.getCheckedItemPosition() == ListView.INVALID_POSITION) {
            mRootView.setBackgroundResource(R.drawable.background_designer_program_default);
        } else {
            mRootView.setBackgroundResource(R.color.card_background);
        }
    }

    public interface ShowVariableListener {
        void showVariable(long id);
    }

    class ViewHolder {

        private View mAdd;

        private View mEmpty;

        private ListView mList;

        public ViewHolder(View view) {
            mList = (ListView) view.findViewById(R.id.list);
            mAdd = view.findViewById(R.id.button_add);
            mEmpty = view.findViewById(android.R.id.empty);
        }

        public void initViews() {
            mAdd.setOnClickListener(mAddVariableClickListener);
            mList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            mList.setOnItemClickListener(mOnItemClickListener);
            mList.setAdapter(mCallbacks.getVariablesAdapter());
            mList.setDrawSelectorOnTop(false);
            mList.setEmptyView(mEmpty);
        }

        public void selectId(long id) {
            ListAdapter adapter = mList.getAdapter();
            for (int i = 0; i < mList.getCount(); i++) {
                if (mList.getItemIdAtPosition(i) == id) {
                    mList.setItemChecked(i, true);
                    break;
                }
            }
        }
    }
}
