
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

package nz.ac.otago.psyanlab.common.designer.meta;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.util.DetailsCallbacks;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Fragment that enables the UI for editing the experiment meta-data.
 */
public class MetaFragment extends Fragment {
    private DetailsCallbacks mCallbacks;

    private ViewHolder mViews;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof DetailsCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (DetailsCallbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViews = new ViewHolder(view);
        mViews.initViews();
    }

    public class ViewHolder {
        private TextWatcher mAuthorsWatcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                final String newString = s.toString();
                mCallbacks.updateAuthors(newString);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        private TextWatcher mDescriptionWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                final String newString = s.toString();
                mCallbacks.updateDescription(newString);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        private TextWatcher mNameWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                final String newString = s.toString();
                mCallbacks.updateName(newString);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        private TextWatcher mVersionWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                final String newString = s.toString();
                try {
                    mCallbacks.updateVersion(Integer.parseInt(newString));
                } catch (NumberFormatException e) {
                    // Ignore invalid version input.
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        protected EditText mAuthors;

        protected EditText mDescription;

        protected EditText mName;

        public ViewHolder(View view) {
            mName = (EditText)view.findViewById(R.id.name);
            mAuthors = (EditText)view.findViewById(R.id.authors);
            mDescription = (EditText)view.findViewById(R.id.description);
        }

        public void initViews() {
            mName.setText(mCallbacks.getName());
            // mVersion.setText(String.valueOf(mCallbacks.getVersion()));
            mDescription.setText(mCallbacks.getDescription());
            mAuthors.setText(mCallbacks.getAuthors());

            mName.addTextChangedListener(mNameWatcher);
            mAuthors.addTextChangedListener(mAuthorsWatcher);
            mDescription.addTextChangedListener(mDescriptionWatcher);
        }
    }
}
