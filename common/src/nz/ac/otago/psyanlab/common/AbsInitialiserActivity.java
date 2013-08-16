
package nz.ac.otago.psyanlab.common;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public abstract class AbsInitialiserActivity extends Activity {
    private boolean mLandscapeDone;

    private int mLandscapeHeight;

    private int mLandscapeWidth;

    private boolean mPortraitDone;

    private int mPortraitHeight;

    private int mPortraitWidth;

    public int getLandscapeHeight() {
        return mLandscapeHeight;
    }

    public int getLandscapeWidth() {
        return mLandscapeWidth;
    }

    public int getPortraitHeight() {
        return mPortraitHeight;
    }

    public int getPortraitWidth() {
        return mPortraitWidth;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        View content = getWindow().getDecorView().getRootView();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLandscapeWidth = content.getWidth();
            mLandscapeHeight = content.getHeight();
            mLandscapeDone = true;

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPortraitWidth = content.getWidth();
            mPortraitHeight = content.getHeight();
            mPortraitDone = true;
        }

        if (mPortraitDone && mLandscapeDone) {
            onInitialisationComplete();
        } else if (mPortraitDone) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }, 33);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }, 33);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.blank);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        }, 33);
    }

    protected abstract void onInitialisationComplete();
}
