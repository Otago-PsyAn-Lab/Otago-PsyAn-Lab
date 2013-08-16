
package nz.ac.otago.psyanlab.single;

import nz.ac.otago.psyanlab.common.PaleActivity;
import nz.ac.otago.psyanlab.common.util.Args;

import android.os.Bundle;

public class LobbyActivity extends PaleActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ScreenValues screen = getIntent().getParcelableExtra(
                nz.ac.otago.psyanlab.single.Args.SCREEN_VALUES);
        UserDelegate userDelegate = new UserDelegate();
        userDelegate.setScreen(screen);
        getIntent().putExtra(Args.USER_DELEGATE, userDelegate);
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(false);
    }
}
