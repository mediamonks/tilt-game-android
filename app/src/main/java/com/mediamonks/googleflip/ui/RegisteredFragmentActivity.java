package com.mediamonks.googleflip.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.util.ScreenUtil;

/**
 * Base activity that registers itself with the application
 */
public class RegisteredFragmentActivity extends FragmentActivity {
    private static final String TAG = RegisteredFragmentActivity.class.getSimpleName();

    @Override
    protected void onStart() {
        super.onStart();

        GoogleFlipGameApplication.setCurrentActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        GoogleFlipGameApplication.clearActivity(this);
    }

    @Override
    protected void onDestroy() {
        GoogleFlipGameApplication.clearActivity(this);

        super.onDestroy();
    }
}
