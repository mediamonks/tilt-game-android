package com.mediamonks.googleflip.pages.calibration;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import com.badlogic.gdx.math.Vector2;
import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.IntentKeys;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.pages.game.physics.constants.Physics;
import com.mediamonks.googleflip.pages.home.HomeActivity;
import com.mediamonks.googleflip.ui.RegisteredFragmentActivity;
import com.mediamonks.googleflip.util.ScreenUtil;
import com.mediamonks.googleflip.util.SoundManager;
import com.pixplicity.easyprefs.library.Prefs;

import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.hitlabnz.sensor_fusion_demo.orientationProvider.OrientationProvider;
import org.hitlabnz.sensor_fusion_demo.representation.EulerAngles;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import temple.core.ui.CustomButton;
import temple.core.ui.CustomTextView;
import temple.core.utils.AlertUtils;

/**
 * Activity for calibrating the phone's physics sensors
 */
public class CalibrationActivity extends RegisteredFragmentActivity {
    private static final String TAG = CalibrationActivity.class.getSimpleName();

    @Bind(R.id.btn_start_calibration)
    protected CustomButton _startCalibrationButton;
    @Bind(R.id.tv_calibration_countdown)
    protected CustomTextView _calibrationCountdownText;

    private CountDownTimer _countdownTimer;
    private CountDownTimer _calibrationTimer;
    private List<Vector2> _gravityPoints = new ArrayList<>();
    private int _currentSeconds;
    private OrientationProvider _orientationProvider;
    private float _radToGravity = (float) (Physics.GRAVITY_FACTOR / Math.PI);
    private String _fromActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calibration);
        ButterKnife.bind(this);
        ScreenUtil.setFullScreen(getWindow().getDecorView());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            _fromActivity = extras.getString(IntentKeys.FROM);
        }

        _orientationProvider = GoogleFlipGameApplication.getOrientationProvider(this);
        if (_orientationProvider != null) {
            try {
                _orientationProvider.start();
            } catch (Exception e) {
                //
                AlertUtils.showAlert(this, R.string.no_sensor_found_message, R.string.no_sensor_found_title, R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Prefs.putFloat(PrefKeys.CALIBRATION_X, 0);
                                Prefs.putFloat(PrefKeys.CALIBRATION_Y, 0);
                                startActivity(new Intent(CalibrationActivity.this, HomeActivity.class));
                            }
                        });
            }
        }

        _radToGravity = (float) (Prefs.getFloat(PrefKeys.GRAVITY_FACTOR, Physics.GRAVITY_FACTOR) / Math.PI);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.btn_start_calibration)
    protected void onStartClick() {
        SoundManager.getInstance().play(R.raw.tap);

        _startCalibrationButton.setVisibility(View.INVISIBLE);

        _calibrationCountdownText.setText(getString(R.string.calibration_countdown, 3));
        _countdownTimer = new CountDownTimer(3100, 500) {

            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = Math.min(1 + (int) (millisUntilFinished / 1000), 3);
                _calibrationCountdownText.setText(getString(R.string.calibration_countdown, seconds));
            }

            @Override
            public void onFinish() {
                _countdownTimer.cancel();
                _countdownTimer = null;

                startCalibration();
            }
        }.start();
    }

    private void startCalibration() {
        _currentSeconds = 4;

        // record gravity every 50 ms
        _calibrationTimer = new CountDownTimer(3100, 50) {

            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = Math.min(1 + (int) (millisUntilFinished / 1000), 3);
                if (seconds != _currentSeconds) {
                    _currentSeconds = seconds;
                    _calibrationCountdownText.setText(getString(R.string.calibration_running, seconds));
                }

                recordGravity();
            }

            @Override
            public void onFinish() {
                _calibrationTimer.cancel();
                _calibrationTimer = null;

                calculateAverageGravity();

                if (_fromActivity.equals(HomeActivity.class.getSimpleName())) {
                    startActivity(new Intent(CalibrationActivity.this, HomeActivity.class));
                } else {
                    onBackPressed();
                    ScreenUtil.setFullScreen(getWindow().getDecorView());
                }
            }
        }.start();
    }

    private void recordGravity() {
        EulerAngles eulerAngles = _orientationProvider.getEulerAngles();

        _gravityPoints.add(Vector2Pool.obtain(_radToGravity * eulerAngles.getRoll(), _radToGravity * eulerAngles.getPitch()));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (_countdownTimer != null) {
            _countdownTimer.cancel();
            _countdownTimer = null;
        }

        if (_calibrationTimer != null) {
            _calibrationTimer.cancel();
            _calibrationTimer = null;
        }

        _orientationProvider.stop();
    }

    private void calculateAverageGravity() {
        Vector2 gravity = Vector2Pool.obtain();

        for (int i = 10; i < _gravityPoints.size(); i++) {
            gravity.add(_gravityPoints.get(i));
        }
        gravity.mul(-1.0f / _gravityPoints.size());

        Prefs.putFloat(PrefKeys.CALIBRATION_X, gravity.x);
        Prefs.putFloat(PrefKeys.CALIBRATION_Y, gravity.y);
    }
}
