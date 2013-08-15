
package nz.ac.otago.psyanlab.common.designer.meta;

import nz.ac.otago.psyanlab.common.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Fragment that enables the UI for editing the experiment meta-data.
 */
public class MetaFragment extends Fragment {
    private Callbacks mCallbacks;

    private ViewHolder mViews;

    private Details mExperimentDetails;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_details, container, false);
    }

    @Override
    public void onDetach() {
        if (mViews != null) {
            mExperimentDetails.name = mViews.name.getText().toString();
            mExperimentDetails.version = mViews.version.getText().toString();
            mExperimentDetails.description = mViews.description.getText().toString();
            mExperimentDetails.authors = mViews.authors.getText().toString();
            mCallbacks.storeDetails(mExperimentDetails);
        }
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViews = new ViewHolder(view);
        mExperimentDetails = mCallbacks.getExperimentDetails();
        mViews.setViewValues(mExperimentDetails);
    }

    public static interface Callbacks {
        void storeDetails(Details details);

        Details getExperimentDetails();
    }

    public static class Details {
        public String authors;

        public String description;

        public String name;

        public String version;
    }

    public class ViewHolder {
        public EditText authors;

        public EditText description;

        public EditText name;

        public EditText version;

        public ViewHolder(View view) {
            name = (EditText)view.findViewById(R.id.name);
            version = (EditText)view.findViewById(R.id.version);
            authors = (EditText)view.findViewById(R.id.authors);
            description = (EditText)view.findViewById(R.id.description);
        }

        public void setViewValues(Details ed) {
            name.setText(ed.name);
            version.setText(ed.version);
            description.setText(ed.description);
            authors.setText(ed.authors);
        }
    }
}
