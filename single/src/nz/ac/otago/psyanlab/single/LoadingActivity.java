
package nz.ac.otago.psyanlab.single;

import nz.ac.otago.psyanlab.common.AbsInitialiserActivity;

import android.content.Intent;

public class LoadingActivity extends AbsInitialiserActivity {
    @Override
    protected void onInitialisationComplete() {
        Intent i = new Intent(this, LobbyActivity.class);
        ScreenValues screen = new ScreenValues(getPortraitWidth(), getPortraitHeight(),
                getLandscapeWidth(), getLandscapeHeight());
        i.putExtra(Args.SCREEN_VALUES, screen);
        startActivity(i);
    }
}
