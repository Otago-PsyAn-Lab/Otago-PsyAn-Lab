
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

package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SelectPropDialogueFragment extends DialogFragment {
    public static SelectPropDialogueFragment newDialogue() {
        SelectPropDialogueFragment f = new SelectPropDialogueFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    public DataSetObserver mPropDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            if (mPropAdapter.getCount() == 0) {
                dismiss();
            }
        }
    };

    public OnItemClickListener mPropSelectedListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            EditPropDialogueFragment dialogue = EditPropDialogueFragment.newEditDialogue(position);
            dialogue.show(getChildFragmentManager(), StageActivity.DIALOGUE_EDIT);
        }
    };

    private StageCallbacks mCallbacks;

    private ListAdapter mPropAdapter;

    private ViewHolder mViews;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof StageCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (StageCallbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogue_select_prop, null);

        mViews = new ViewHolder(view);
        mViews.setViewValues();
        mViews.initViews();

        getDialog().setTitle(R.string.title_select_prop);

        return view;
    }

    class ViewHolder {
        public ListView props;

        public ViewHolder(View view) {
            props = (ListView)view.findViewById(R.id.props);
        }

        public void initViews() {
            mPropAdapter = mCallbacks.getPropAdapter();
            mPropAdapter.registerDataSetObserver(mPropDataObserver);
            props.setAdapter(mPropAdapter);
            props.setOnItemClickListener(mPropSelectedListener);
        }

        public void setViewValues() {
        }
    }
}
