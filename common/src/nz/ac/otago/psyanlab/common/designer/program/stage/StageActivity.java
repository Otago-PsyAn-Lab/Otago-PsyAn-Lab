
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.util.Args;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

/**
 * Activity containing the stage editor. In this the user lays out props and
 * sets their initial properties.
 */
public class StageActivity extends FragmentActivity {
    private Parcelable mExperimentDelegate;

    private ArrayList<Prop> mProps;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(Args.EXPERIMENT_PROPS)) {
                Prop[] props = extras.getParcelableArray(Args.EXPERIMENT_PROPS);
            }
        }

        Stage stage = new Stage(this);
        stage.setAdapter(null);
        setContentView(stage);

    }
}
