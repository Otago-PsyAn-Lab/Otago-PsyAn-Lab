package nz.ac.otago.psyanlab.common.designer;

import nz.ac.otago.psyanlab.common.model.Experiment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Fragment to persist experiment data across configuration changes.
 */
public class ExperimentHolderFragment extends Fragment {
	private Experiment mExperiment;

	public Experiment getExperiment() {
		return mExperiment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
	}

	public void setExperiment(Experiment experiment) {
		mExperiment = experiment;
	}
}
