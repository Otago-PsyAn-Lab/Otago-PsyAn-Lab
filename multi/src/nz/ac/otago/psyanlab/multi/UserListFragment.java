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

package nz.ac.otago.psyanlab.multi;

import nz.ac.otago.psyanlab.multi.R;
import nz.ac.otago.psyanlab.multi.dbmodel.UserModel;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class UserListFragment extends Fragment {
    protected static final String STATE_ACTIVATED_POSITION = "activated_position";

    public static interface Callbacks {
        public void onUserSelected(UserModel user);

        public SimpleCursorAdapter getUsersAdapter(int layout, String[] fields,
                int[] ids);
    }

    protected static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onUserSelected(UserModel user) {
        }

        @Override
        public SimpleCursorAdapter getUsersAdapter(int layout, String[] fields,
                int[] ids) {
            return null;
        }
    };

    protected Callbacks mCallbacks = sDummyCallbacks;
    protected AbsListView mList;
    protected int mActivatedPosition = AbsListView.INVALID_POSITION;
    private SimpleCursorAdapter mAdapter;

    protected OnItemClickListener itemClickCallback = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                int position, long id) {
            Cursor c = (Cursor)mAdapter.getItem(position);
            mCallbacks.onUserSelected(new UserModel(c));
        }
    };

    protected MultiChoiceModeListener multiChoiceModeCallbacks = new MultiChoiceModeListener() {
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.fragment_user_list, menu);
            mode.setTitle(R.string.title_select_users);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position,
                long id, boolean checked) {
            final int checkedCount = mList.getCheckedItemCount();

            // Text feedback in the action bar.
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                case 1:
                    mode.setSubtitle(R.string.subtitle_one_item_selected);
                    break;
                default:
                    mode.setSubtitle(String.format(
                            getResources().getString(
                                    R.string.subtitle_fmt_num_items_selected),
                            checkedCount));
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mList = (AbsListView)view.findViewById(R.id.user_list);
        mAdapter = mCallbacks.getUsersAdapter(
                android.R.layout.simple_list_item_activated_1, new String[] {
                    UserModel.KEY_NAME
                }, new int[] {
                    android.R.id.text1
                });

        // adapter.setViewBinder(new ViewBinder() {
        // @Override
        // public boolean setViewValue(View view, Cursor cursor, int
        // columnIndex) {
        // //TODO: bind thumbnail using LruCache.
        // return false;
        // }
        // });
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(itemClickCallback);

        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != AbsListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        mList.setChoiceMode(activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE
                : AbsListView.CHOICE_MODE_NONE);
    }

    public void setActivatedPosition(int position) {
        if (position == AbsListView.INVALID_POSITION) {
            mList.setItemChecked(mActivatedPosition, false);
        } else {
            mList.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
