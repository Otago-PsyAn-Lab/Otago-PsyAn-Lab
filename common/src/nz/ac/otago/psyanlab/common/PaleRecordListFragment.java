
package nz.ac.otago.psyanlab.common;

import nz.ac.otago.psyanlab.common.util.EmptyAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListAdapter;

public class PaleRecordListFragment extends ListFragment {
    public static final String TAG = "records_list_fragment";

    private ListAdapter mAdapter;

    private UserExperimentDelegateI mDelegate;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);
    }

    public void setExperimentDelegate(UserExperimentDelegateI userExperimentDelegate) {
        mDelegate = userExperimentDelegate;
        if (mDelegate == null) {
            mAdapter = new EmptyAdapter();
        } else {
            mDelegate.init(getActivity());
            mAdapter = new RecordAdapterWrapper(getActivity(), mDelegate.getRecordsAdapter(
                    R.layout.record_item, new int[] {
                            UserExperimentDelegateI.RECORD_ID, UserExperimentDelegateI.RECORD_DATE,
                            UserExperimentDelegateI.RECORD_NOTE
                    }, new int[] {
                            R.id.session_id, R.id.date, R.id.note
                    }));
        }
        setListAdapter(mAdapter);
    }
}
