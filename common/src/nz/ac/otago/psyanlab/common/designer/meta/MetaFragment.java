
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
        public EditText authors;

        public EditText description;

        public EditText name;

        public EditText version;

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
        };;

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
        };;

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
        };;

        public ViewHolder(View view) {
            name = (EditText)view.findViewById(R.id.name);
            version = (EditText)view.findViewById(R.id.version);
            authors = (EditText)view.findViewById(R.id.authors);
            description = (EditText)view.findViewById(R.id.description);
        }

        public void initViews() {
            name.setText(mCallbacks.getName());
            version.setText(String.valueOf(mCallbacks.getVersion()));
            description.setText(mCallbacks.getDescription());
            authors.setText(mCallbacks.getAuthors());

            name.addTextChangedListener(mNameWatcher);
            version.addTextChangedListener(mVersionWatcher);
            authors.addTextChangedListener(mAuthorsWatcher);
            description.addTextChangedListener(mDescriptionWatcher);
        }
    }
}
