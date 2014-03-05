
package nz.ac.otago.psyanlab.single;

import nz.ac.otago.psyanlab.common.PaleActivity;
import nz.ac.otago.psyanlab.common.util.Args;

import android.os.Bundle;

public class LobbyActivity extends PaleActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getIntent().putExtra(Args.USER_DELEGATE, new UserDelegate());
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(false);
    }
}
