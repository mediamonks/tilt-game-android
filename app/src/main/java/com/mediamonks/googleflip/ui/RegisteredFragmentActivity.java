package com.mediamonks.googleflip.ui;

import android.support.v4.app.FragmentActivity;

import com.mediamonks.googleflip.GoogleFlipGameApplication;

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
