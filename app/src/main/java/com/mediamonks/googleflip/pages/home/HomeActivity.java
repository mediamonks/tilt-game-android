package com.mediamonks.googleflip.pages.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.ActivityRequestCode;
import com.mediamonks.googleflip.data.constants.Fragments;
import com.mediamonks.googleflip.data.constants.GameType;
import com.mediamonks.googleflip.data.constants.IntentKeys;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.data.services.DataService;
import com.mediamonks.googleflip.pages.calibration.CalibrationActivity;
import com.mediamonks.googleflip.pages.connect.ConnectActivity;
import com.mediamonks.googleflip.pages.game_flow.singleplayer.SinglePlayerGameFlowActivity;
import com.mediamonks.googleflip.ui.RegisteredFragmentActivity;
import com.mediamonks.googleflip.util.SoundManager;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import temple.core.net.BroadcastReceiver;
import temple.core.ui.CustomButton;
import temple.core.ui.CustomTextView;
import temple.core.utils.AlertUtils;

/**
 * Activity for home screen
 */
public class HomeActivity extends RegisteredFragmentActivity {
    private static String TAG = HomeActivity.class.getSimpleName();

    @Bind(R.id.logo_circle)
    protected View _logoCircle;
    @Bind(R.id.explanation)
    protected CustomTextView _explanation;
    @Bind(R.id.logo)
    protected ImageView _logo;
    @Bind(R.id.android_experiment)
    protected ImageView _androidExperiment;
    @Bind(R.id.single_player_button)
    protected CustomButton _singlePlayerButton;
    @Bind(R.id.multi_player_button)
    protected CustomButton _multiPlayerButton;

    private Point _basePos = new Point();
    private float _maskSize;
    private float _buttonOffsetY;
    private ValueAnimator _maskAnimator;
    private ValueAnimator _buttonsAnimator;
    private BroadcastReceiver _broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        boolean isDataLoaded = GoogleFlipGameApplication.getUserModel().isDataLoaded();
        if (!isDataLoaded) {
            _broadcastReceiver = new BroadcastReceiver(this, true);
            _broadcastReceiver.addActionHandler(DataService.ACTION_LOAD_LEVELS,
                    new BroadcastReceiver.ActionHandler() {
                        @Override
                        public void onAction(String action, Intent intent) {
                            onDataLoaded();
                        }
                    });

            _multiPlayerButton.setEnabled(false);
            _singlePlayerButton.setEnabled(false);
        } else {
            onDataLoaded();
        }

        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.purple));

        if (!Prefs.contains(PrefKeys.CALIBRATION_X)) {
            _explanation.setAlpha(0);
            _multiPlayerButton.setVisibility(View.GONE);
            _singlePlayerButton.setVisibility(View.GONE);

            startCalibrationDelay();
        } else {
            _logoCircle.setVisibility(View.GONE);

            if (GoogleFlipGameApplication.getIsLanding()) {
                GoogleFlipGameApplication.setIsLanding(false);

                _explanation.setAlpha(0);
                _singlePlayerButton.setVisibility(View.GONE);
                _multiPlayerButton.setVisibility(View.GONE);

                setAnimation();
            } else {
                _androidExperiment.setVisibility(View.GONE);

                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.purple_dark));
            }
        }

        SoundManager.getInstance().load(R.raw.tap);
    }

    private void onDataLoaded() {
        enableButtons();

        if (_broadcastReceiver != null) {
            _broadcastReceiver.onPause();

            _broadcastReceiver = null;
        }
    }

    private void enableButtons() {
        _singlePlayerButton.setEnabled(true);
        _multiPlayerButton.setEnabled(true);
    }

    private void setAnimation() {
        _maskAnimator = ValueAnimator.ofFloat(0, 1);
        _maskAnimator.setInterpolator(new DecelerateInterpolator());
        _maskAnimator.setDuration(800);
        _maskAnimator.setStartDelay(1000);
        _maskAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.purple_dark));

                _logoCircle.setVisibility(View.INVISIBLE);
                _androidExperiment.setVisibility(View.GONE);
                _multiPlayerButton.setVisibility(View.INVISIBLE);
                _singlePlayerButton.setVisibility(View.INVISIBLE);
                _explanation.setAlpha(1);
            }
        });
        _maskAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                if (_logoCircle.getVisibility() == View.GONE) {
                    Rect posRect = new Rect();
                    Point size = new Point();

                    float scale = (_logo.getDrawable().getBounds().width() / 171f);
                    getWindowManager().getDefaultDisplay().getSize(size);

                    _logo.getGlobalVisibleRect(posRect);
                    _basePos.set((int) (posRect.centerX() - (scale * 24.5f)), (int) (posRect.centerY() - (scale * 36.5f)));
                    _maskSize = (getResources().getDisplayMetrics().heightPixels * 1.5f) / getResources().getDisplayMetrics().density;

                    _logoCircle.setVisibility(View.VISIBLE);
                }

                _logoCircle.setScaleX(_maskSize * value);
                _logoCircle.setScaleY(_maskSize * value);
                _logoCircle.setX(_basePos.x - _logoCircle.getWidth());
                _logoCircle.setY(_basePos.y - _logoCircle.getHeight());
                _androidExperiment.setAlpha(1 - Math.max(0, Math.min(1, value * 1.5f)));
                _explanation.setAlpha(Math.max(0, Math.min(1, (value - .25f) * 1.5f)));
            }
        });

        _buttonsAnimator = ValueAnimator.ofFloat(0, 1);
        _buttonsAnimator.setInterpolator(new DecelerateInterpolator());
        _buttonsAnimator.setDuration(300);
        _buttonsAnimator.setStartDelay(1800);
        _buttonsAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                _singlePlayerButton.setTranslationY(0);
                _multiPlayerButton.setTranslationY(0);
            }
        });
        _buttonsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                if (_singlePlayerButton.getVisibility() == View.INVISIBLE) {
                    _buttonOffsetY = _singlePlayerButton.getHeight() * 1.1f;

                    _singlePlayerButton.setVisibility(View.VISIBLE);
                    _multiPlayerButton.setVisibility(View.VISIBLE);
                }

                _singlePlayerButton.setTranslationY(_buttonOffsetY - (_buttonOffsetY * value));
                _multiPlayerButton.setTranslationY(_buttonOffsetY - (_buttonOffsetY * value));
            }
        });

        _maskAnimator.start();
        _buttonsAnimator.start();
    }

    private void startCalibrationDelay() {
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(new Intent(HomeActivity.this, CalibrationActivity.class));
                        intent.putExtra(IntentKeys.FROM, HomeActivity.TAG);
                        startActivity(intent);
                    }
                }
                , 2500);
    }

    @OnClick(R.id.single_player_button)
    protected void onSinglePlayerButtonClick() {
        SoundManager.getInstance().play(R.raw.tap);

        GoogleFlipGameApplication.getOrientationProvider(this).start();
        GoogleFlipGameApplication.getUserModel().selectNextLockedLevel();

        Prefs.putInt(PrefKeys.GAME_TYPE, GameType.SINGLE_PLAYER.ordinal());

        Intent intent = new Intent(this, SinglePlayerGameFlowActivity.class);
        intent.putExtra(IntentKeys.FRAGMENT, Fragments.GAME_FLOW_SELECT_LEVEL);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
    }

    @OnClick(R.id.multi_player_button)
    protected void onMultiPlayerButtonClick() {
        SoundManager.getInstance().play(R.raw.tap);

        Prefs.putInt(PrefKeys.GAME_TYPE, GameType.MULTI_PLAYER.ordinal());

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            AlertUtils.showAlert(this, R.string.no_bluetooth_message, R.string.no_bluetooth_title, R.string.btn_ok);
        } else if (!adapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), ActivityRequestCode.REQUEST_ENABLE_BT);
        } else {
            startMultiplayer();
        }
    }

    private void startMultiplayer() {
        GoogleFlipGameApplication.getOrientationProvider(this).start();

        startActivity(new Intent(HomeActivity.this, ConnectActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityRequestCode.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                startMultiplayer();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleFlipGameApplication.getOrientationProvider(this).stop();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (_maskAnimator != null) {
            _maskAnimator.cancel();
            _maskAnimator = null;
        }
        if (_buttonsAnimator != null) {
            _buttonsAnimator.cancel();
            _buttonsAnimator = null;
        }

        ButterKnife.unbind(this);

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        GoogleFlipGameApplication.setScreenRotation(getWindowManager().getDefaultDisplay().getRotation());

        if (_broadcastReceiver != null) {
            if (GoogleFlipGameApplication.getUserModel().isDataLoaded()) {
                onDataLoaded();
            } else {
                _broadcastReceiver.onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (_broadcastReceiver != null) {
            _broadcastReceiver.onPause();
        }
    }
}
