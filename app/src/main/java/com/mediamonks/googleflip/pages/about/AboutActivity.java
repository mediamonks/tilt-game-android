package com.mediamonks.googleflip.pages.about;

import android.os.Bundle;

import com.mediamonks.googleflip.BuildConfig;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.ui.RegisteredFragmentActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import temple.core.ui.CustomTextView;

/**
 * Activity showing information about the app
 */
public class AboutActivity extends RegisteredFragmentActivity {
    private static String TAG = AboutActivity.class.getSimpleName();

    @Bind(R.id.tv_version)
    protected CustomTextView _versionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        _versionText.setText(getString(R.string.version, BuildConfig.VERSION_NAME));
    }

    @OnClick(R.id.close_button)
    protected void onCloseButtonClick() {
        onBackPressed();
    }
}
