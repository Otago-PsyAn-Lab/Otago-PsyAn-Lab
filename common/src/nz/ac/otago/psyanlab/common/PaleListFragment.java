/*
 Copyright (C) 2012, 2013 University of Otago, Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PaleListFragment extends ListFragment {
    public static final String TAG = "experiment_list_fragment";

    private static final String STATE_ACTIVATE_ON_ITEM_CLICK = "activate_on_item_click";

    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private int mActivatedPosition = 0;

    private boolean mActivateOnItemClick;

    private ListAdapter mAdapter;

    private Callbacks mCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pale_list, container, false);
    }

    public void onExperimentInsert(final long id) {
        ListView listView = getListView();
        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.getItemIdAtPosition(i) == id) {
                setActivatedPosition(i);
            }
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(id);
        setActivatedPosition(position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        outState.putBoolean(STATE_ACTIVATE_ON_ITEM_CLICK, mActivateOnItemClick);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
                setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
            }
            if (savedInstanceState.containsKey(STATE_ACTIVATE_ON_ITEM_CLICK)) {
                setActivateOnItemClick(savedInstanceState.getBoolean(STATE_ACTIVATE_ON_ITEM_CLICK));
            }
        }
    }

    public void setActivatedPosition(int position) {
        getListView().setItemChecked(position, true);

        mActivatedPosition = position;
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView()
                .setChoiceMode(
                        activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE
                                : AbsListView.CHOICE_MODE_NONE);
        mActivateOnItemClick = activateOnItemClick;
    }

    public void setUserDelegate(UserDelegateI userDelegate) {
        mAdapter = userDelegate.getExperimentsAdapter(R.layout.list_item_experiment, new int[] {
            UserDelegateI.EXPERIMENT_NAME
        }, new int[] {
            android.R.id.text1
        });
        setListAdapter(mAdapter);
    }

    public static interface Callbacks {
        public void onItemSelected(long id);
    }
}
