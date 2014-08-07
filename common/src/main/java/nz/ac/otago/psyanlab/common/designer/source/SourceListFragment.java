/*
 Copyright (C) 2014 Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.common.designer.source;

import nz.ac.otago.psyanlab.common.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SourceListFragment extends Fragment {
    private static final ShowSourceListener sDummy = new ShowSourceListener() {
        @Override
        public void showSource(long id) {
        }
    };

    private SourceCallbacks mCallbacks;

    private View mRootView;

    private ShowSourceListener mShowSourceListener = sDummy;

    private ViewHolder mViews;

    protected OnClickListener mAddSourceClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.startImportSourcesUI();
        }
    };

    protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mShowSourceListener.showSource(id);
            mRootView.setBackgroundResource(R.color.card_background);
        }
    };

    public void deselectItem() {
        mRootView.setBackgroundResource(R.drawable.background_designer_program_default);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof SourceCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (SourceCallbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_source_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRootView = view;

        mViews = new ViewHolder(view);
        mViews.initViews();
        updateBackground();
    }

    public void setShowSourceListener(ShowSourceListener listener) {
        mShowSourceListener = listener;
    }

    private void updateBackground() {
        if (mViews.mList.getCheckedItemPosition() == ListView.INVALID_POSITION) {
            mRootView.setBackgroundResource(R.drawable.background_designer_program_default);
        } else {
            mRootView.setBackgroundResource(R.color.card_background);
        }
    }

    public interface ShowSourceListener {
        void showSource(long id);
    }

    class ViewHolder {
        private View mAdd;

        private View mEmpty;

        private ListView mList;

        public ViewHolder(View view) {
            mList = (ListView)view.findViewById(R.id.list);
            mAdd = view.findViewById(R.id.button_add);
            mEmpty = view.findViewById(android.R.id.empty);
        }

        public void initViews() {
            mAdd.setOnClickListener(mAddSourceClickListener);
            mList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            mList.setOnItemClickListener(mOnItemClickListener);
            mList.setAdapter(mCallbacks.getSourcesAdapter());
            mList.setDrawSelectorOnTop(false);
            mList.setEmptyView(mEmpty);
        }
    }
}
