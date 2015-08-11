package com.mediamonks.googleflip.pages.game;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mediamonks.googleflip.BuildConfig;
import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.Fragments;
import com.mediamonks.googleflip.data.constants.GameType;
import com.mediamonks.googleflip.data.constants.IntentKeys;
import com.mediamonks.googleflip.data.constants.LevelColor;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.data.vo.LevelVO;
import com.mediamonks.googleflip.pages.game.management.Player;
import com.mediamonks.googleflip.pages.game.physics.control.GameLevelStateListener;
import com.mediamonks.googleflip.pages.game.physics.control.TutorialLevelController;
import com.mediamonks.googleflip.pages.game.physics.control.WorldController;
import com.mediamonks.googleflip.pages.game.physics.levels.GameLevel;
import com.mediamonks.googleflip.pages.game.physics.levels.TutorialLevel1;
import com.mediamonks.googleflip.pages.game.physics.levels.TutorialLevel2;
import com.mediamonks.googleflip.pages.game.physics.levels.TutorialLevel3;
import com.mediamonks.googleflip.pages.game_flow.multiplayer.MultiPlayerGameFlowActivity;
import com.mediamonks.googleflip.pages.game_flow.singleplayer.SinglePlayerGameFlowActivity;
import com.mediamonks.googleflip.pages.home.HomeActivity;
import com.mediamonks.googleflip.ui.animation.AnimationCallback;
import com.mediamonks.googleflip.ui.animation.LargeAnimatedTextView;
import com.mediamonks.googleflip.ui.animation.SmallAnimatedTextView;
import com.mediamonks.googleflip.util.LevelColorUtil;
import com.mediamonks.googleflip.util.ScreenUtil;
import com.mediamonks.googleflip.util.SoundManager;
import com.pixplicity.easyprefs.library.Prefs;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleLayoutGameActivity;
import org.andengine.util.adt.color.ColorUtils;

import java.io.IOException;

import temple.core.ui.CustomButton;

/**
 * Activity for the game
 */
public class FlipGameActivity extends SimpleLayoutGameActivity implements GameLevelStateListener {
    private static final String TAG = FlipGameActivity.class.getSimpleName();

    public static final String ARG_TUTORIAL_LEVEL = "argTutorialLevel";

    private static final Class[] TUTORIAL_LEVELS = new Class[]{
            TutorialLevel1.class,
            TutorialLevel2.class,
            TutorialLevel3.class
    };

    private WorldController _worldController;
    private LinearLayout _readyLayout;
    private LargeAnimatedTextView _goalLabel1;
    private LargeAnimatedTextView _goalLabel2;
    private LargeAnimatedTextView _readyLabel;
    private SmallAnimatedTextView _levelLabel;
    private int _cameraWidth;
    private int _cameraHeight;
    private boolean _isTutorial;
    private int _tutorialLevel;
    private float _levelTime;
    private boolean _levelCompletedSuccesfully;
    private Runnable _clearRunnable = new Runnable() {
        @Override
        public void run() {
            if (_worldController != null) {
                _worldController.clearLevel();
            }
        }
    };
    private GameType _gameType;
    private int _backgroundColor;
    private int _nextBackgroundColor;
    private float _scale;
    private boolean _skipButtonEnabled = false;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_flipgame;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.rendersurfaceview;
    }

    @Override
    protected void onSetContentView() {
        super.onSetContentView();

        GoogleFlipGameApplication.getUserModel().randomizeBackgroundColor();

        _gameType = GameType.values()[Prefs.getInt(PrefKeys.GAME_TYPE, GameType.SINGLE_PLAYER.ordinal())];

        LevelColor levelColor = LevelColor.BLUE;
        String levelName = "";
        switch (_gameType) {
            case MULTI_PLAYER:
                Player player = GoogleFlipGameApplication.getGameClient().getPlayer();
                if (player != null && player.getClientVO() != null) {
                    levelColor = GoogleFlipGameApplication.getGameClient().getPlayer().getClientVO().levelColor;
                    _nextBackgroundColor = LevelColorUtil.fromLevelColor(LevelColor.YELLOW);
                    levelName = getString(R.string.round_name, GoogleFlipGameApplication.getGameClient().getCurrentRoundIndex() + 1);
                } else {
                    // this may happen after a crash, so go home
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
            case SINGLE_PLAYER:
                levelColor = GoogleFlipGameApplication.getUserModel().getCurrentBackgroundColor();
                _nextBackgroundColor = LevelColorUtil.fromLevelColor(GoogleFlipGameApplication.getUserModel().getNextBackgroundColor());
                levelName = getString(R.string.level_name, GoogleFlipGameApplication.getUserModel().getSelectedLevelIndex() + 1);
                break;
        }

        Intent intent = getIntent();
        if (intent.hasExtra(ARG_TUTORIAL_LEVEL)) {
            _isTutorial = true;
            _tutorialLevel = intent.getIntExtra(ARG_TUTORIAL_LEVEL, 0);
            GoogleFlipGameApplication.getUserModel().setTutorialLevel(_tutorialLevel);
            _backgroundColor = LevelColorUtil.fromLevelColor(LevelColor.PURPLE);
            _nextBackgroundColor = LevelColorUtil.fromLevelColor(LevelColor.YELLOW);
        } else {
            _backgroundColor = LevelColorUtil.fromLevelColor(levelColor);
        }

        LinearLayout skipLayout = (LinearLayout) getWindow().getDecorView().findViewById(R.id.skip_container);
        CustomButton skipButton = (CustomButton) skipLayout.findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_skipButtonEnabled) {
                    _skipButtonEnabled = false;
                    onLevelFailed();
                }
            }
        });

        _readyLayout = (LinearLayout) getWindow().getDecorView().findViewById(R.id.ready_container);
        _readyLayout.setBackgroundColor(_backgroundColor);

        if (_isTutorial) {
            _readyLayout.setVisibility(View.GONE);
        } else {
            TextView levelLabel = (TextView) _readyLayout.findViewById(R.id.level_label);
            levelLabel.setText(levelName);
            skipLayout.setVisibility(View.GONE);
        }

        int r = Color.red(_backgroundColor);
        int b = Color.blue(_backgroundColor);
        int g = Color.green(_backgroundColor);
        int darkenBackgroundColor = Color.rgb((int) (r * .5), (int) (g * .5), (int) (b * .5));
        getWindow().getDecorView().setBackgroundColor(darkenBackgroundColor);
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            getWindowManager().getDefaultDisplay().getRealSize(size);
        } else {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            size.x = displayMetrics.widthPixels;
            size.y = displayMetrics.heightPixels;
        }

        float desiredRatio = (float) (1080.0 / 1920.0);
        final float realRatio = size.x / size.y;

        int measuredWidth;
        int measuredHeight;
        if (realRatio < desiredRatio) {
            measuredWidth = size.x;
            measuredHeight = Math.round(measuredWidth / desiredRatio);
        } else {
            measuredHeight = size.y;
            measuredWidth = Math.round(measuredHeight * desiredRatio);
        }

        _scale = measuredWidth / 1080.0f;

        _cameraWidth = measuredWidth;
        _cameraHeight = measuredHeight;

        return new EngineOptions(
                true,
                ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(1080, 1920),
                new Camera(0, 0, _cameraWidth, _cameraHeight));
    }

    @Override
    protected void onCreateResources() throws IOException {
    }

    @Override
    protected Scene onCreateScene() throws IOException {
        return null;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        final Scene scene = new Scene();
        scene.getBackground().setColor(ColorUtils.convertARGBPackedIntToColor(_backgroundColor));

        _worldController = new WorldController(_cameraWidth, _cameraHeight, _scale, getResources().getDisplayMetrics().density, getEngine());
        _worldController.setDoDebugDraw(BuildConfig.BOX2D_DEBUG);
        _worldController.setOrientationProvider(GoogleFlipGameApplication.getOrientationProvider(this));
        _worldController.setGameLevelStateListener(this);

        pOnCreateSceneCallback.onCreateSceneFinished(scene);
    }

    @Override
    public void onPopulateScene(final Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
        pOnPopulateSceneCallback.onPopulateSceneFinished();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createLevel();
            }
        });
    }

    private void createLevel() {
        if (_worldController == null) {
            stopWorld();
            return;
        }

        if (_isTutorial) {
            LevelVO level = new LevelVO();
            level.id = (long) -1;
            level.controllerClass = TutorialLevelController.class.getName();
            level.levelClass = TUTORIAL_LEVELS[_tutorialLevel].getName();

            GameLevel gameLevel = _worldController.createLevel(level, _backgroundColor, _nextBackgroundColor);
            if (gameLevel == null) {
                startActivity(new Intent(this, HomeActivity.class));
                return;
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (_worldController == null) {
                        stopWorld();
                        return;
                    }

                    _worldController.start();
                    _skipButtonEnabled = true;
                }
            }, 2000);

        } else {
            GameLevel gameLevel = _worldController.createLevel(GoogleFlipGameApplication.getUserModel().getSelectedLevel(), _backgroundColor,
                    _nextBackgroundColor);
            if (gameLevel == null) {
                startActivity(new Intent(this, HomeActivity.class));
                return;
            }

            _skipButtonEnabled = false;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    _goalLabel1 = (LargeAnimatedTextView) _readyLayout.findViewById(R.id.goal_label1);
                    _goalLabel2 = (LargeAnimatedTextView) _readyLayout.findViewById(R.id.goal_label2);
                    _readyLabel = (LargeAnimatedTextView) _readyLayout.findViewById(R.id.ready_label);
                    _levelLabel = (SmallAnimatedTextView) _readyLayout.findViewById(R.id.level_label);

                    if (_gameType == GameType.MULTI_PLAYER && GoogleFlipGameApplication.getGameClient().getCurrentRoundIndex() == 0) {
                        _levelLabel.setVisibility(View.GONE);
                        _readyLabel.setVisibility(View.GONE);

                        _goalLabel1.show();
                        _goalLabel2.show(200, new AnimationCallback() {
                            @Override
                            public void onComplete() {
                                hideGoal();
                            }
                        });
                    } else {
                        _goalLabel1.setVisibility(View.GONE);
                        _goalLabel2.setVisibility(View.GONE);

                        showReady();
                    }
                }
            }, 100);
        }
    }

    private void hideGoal() {
        Animation label1FadeOut = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out);
        Animation label2FadeOut = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out);

        Animation.AnimationListener readyListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animatwion) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                _goalLabel1.setVisibility(View.GONE);
                _goalLabel2.setVisibility(View.GONE);

                showReady();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };

        label1FadeOut.setAnimationListener(readyListener);
        _goalLabel1.startAnimation(label1FadeOut);
        _goalLabel2.startAnimation(label2FadeOut);
    }

    private void showReady() {
        SoundManager.getInstance().play(R.raw.ready);

        _levelLabel.setVisibility(View.VISIBLE);
        _readyLabel.setVisibility(View.VISIBLE);

        _levelLabel.show(500);
        _readyLabel.show(new AnimationCallback() {
            @Override
            public void onComplete() {
                Animation readyTransitionOut = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out);

                Animation.AnimationListener readyListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        _readyLayout.setVisibility(View.GONE);
                        _worldController.start();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                };

                readyTransitionOut.setAnimationListener(readyListener);
                _readyLayout.startAnimation(readyTransitionOut);
            }
        });
    }

    @Override
    public synchronized void onResumeGame() {
        super.onResumeGame();

        if (_worldController != null && !_worldController.hasParent()) {
            getEngine().getScene().attachChild(_worldController);
        }
    }

    @Override
    public void onBallOut() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (_worldController != null) {
                    _worldController.respawnBall();
                }
            }
        });
    }

    @Override
    public void onTimeOut() {
        SoundManager.getInstance().play(R.raw.time_up);

        onLevelFailed();
    }

    @Override
    public void onLevelFailed() {
        if (_worldController == null) {
            return;
        }

        _levelTime = _worldController.getTimePassed();
        _levelCompletedSuccesfully = false;

        _worldController.setIsLevelCompleted(true);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _worldController.playTimeUpAnimation();
            }
        });
    }

    @Override
    public void onLevelComplete() {
        _levelCompletedSuccesfully = true;
        _levelTime = _worldController.getTimePassed();

        _worldController.setIsLevelCompleted(true);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _worldController.playLevelEndAnimation();
            }
        });
    }

    @Override
    public void onOutAnimationComplete() {
        toResult(_levelCompletedSuccesfully);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            ScreenUtil.setFullScreen(getWindow().getDecorView());
        }
    }

    private void toResult(Boolean success) {
        Log.d(TAG, "toResult: ");

        if (_isTutorial) {
            if (_tutorialLevel < TUTORIAL_LEVELS.length - 1) {
                goNextTutorialLevel();
                return;
            } else {
                Prefs.putBoolean(PrefKeys.TUTORIAL_COMPLETE, true);
            }
        }

        Long levelId = _isTutorial ? -1 : GoogleFlipGameApplication.getUserModel().getSelectedLevelId();

        stopWorld();

        Intent intent;
        switch (_gameType) {
            case SINGLE_PLAYER:
                intent = new Intent(this, SinglePlayerGameFlowActivity.class);
                intent.putExtra(IntentKeys.FRAGMENT, Fragments.GAME_FLOW_RESULT_LEVEL);
                intent.putExtra(IntentKeys.RESULT_SUCCESS, success);
                intent.putExtra(IntentKeys.RESULT_TIME, _levelTime);
                intent.putExtra(IntentKeys.RESULT_LEVEL_ID, levelId);
                intent.putExtra(IntentKeys.IS_TUTORIAL, _isTutorial);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case MULTI_PLAYER:
                // send result to server
                GoogleFlipGameApplication.getGameClient().setRoundComplete(levelId, _levelTime, success);

                intent = new Intent(this, MultiPlayerGameFlowActivity.class);
                intent.putExtra(IntentKeys.FRAGMENT, Fragments.GAME_FLOW_SCOREBOARD);
                startActivity(intent);

                finish();
                break;
        }
    }

    private void goNextTutorialLevel() {
        if (_worldController == null) {
            return;
        }

        _tutorialLevel++;
        GoogleFlipGameApplication.getUserModel().setTutorialLevel(_tutorialLevel);

        getEngine().runSafely(_clearRunnable);
        _worldController.reset();

        createLevel();
        getEngine().start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        GoogleFlipGameApplication.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        GoogleFlipGameApplication.clearActivity(this);
    }

    private void stopWorld() {
        if (_worldController == null) return;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getEngine().getScene().detachChildren();

                _worldController.setGameLevelStateListener(null);
                _worldController.clearLevel();
                _worldController.dispose();
                _worldController = null;

                mEngine.stop();
            }
        };
        getEngine().runSafely(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();

        GoogleFlipGameApplication.clearActivity(this);
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        stopWorld();

        Intent intent;

        switch (_gameType) {
            case MULTI_PLAYER:
                GoogleFlipGameApplication.stopGame();

                intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case SINGLE_PLAYER:
                intent = new Intent(this, SinglePlayerGameFlowActivity.class);
                intent.putExtra(IntentKeys.FRAGMENT, Fragments.GAME_FLOW_SELECT_LEVEL);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: isFinishing = " + isFinishing());
        stopWorld();

        GoogleFlipGameApplication.clearActivity(this);
        Runtime.getRuntime().gc();

        super.onDestroy();
    }
}

