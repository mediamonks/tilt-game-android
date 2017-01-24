package com.mediamonks.googleflip.pages.game.physics.control;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.data.constants.LevelColor;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.data.vo.LevelVO;
import com.mediamonks.googleflip.pages.game.physics.constants.ObjectName;
import com.mediamonks.googleflip.pages.game.physics.constants.Physics;
import com.mediamonks.googleflip.pages.game.physics.levels.GameLevel;
import com.mediamonks.googleflip.pages.game.ui.Ball;
import com.mediamonks.googleflip.pages.game.ui.Flash;
import com.mediamonks.googleflip.pages.game.ui.SpawnHole;
import com.mediamonks.googleflip.ui.animation.AnimationCallback;
import com.mediamonks.googleflip.util.ClassUtil;
import com.mediamonks.googleflip.util.LevelColorUtil;
import com.pixplicity.easyprefs.library.Prefs;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.debugdraw.DebugRenderer;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.EmptyBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.BaseBitmapTextureAtlasSourceDecorator;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.adt.color.Color;
import org.andengine.util.adt.color.ColorUtils;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;
import org.hitlabnz.sensor_fusion_demo.orientationProvider.OrientationProvider;
import org.hitlabnz.sensor_fusion_demo.representation.EulerAngles;

import java.io.IOException;
import java.util.Locale;

import temple.core.utils.font.FontFaceType;

/**
 * Controller for the game world
 */
public class WorldController extends Entity {
    private static final String TAG = WorldController.class.getSimpleName();

    // fixture definition for the sensors
    public static final FixtureDef SENSOR_FIX_DEF = PhysicsFactory.createFixtureDef(0, 0, 0, true);

    // conversion factor from gyro rotation to gravity for the ball
    private static final float BOUNDS_THICKNESS_FACTOR = .005f;
    private static final float BALL_SIZE_FACTOR = .025f;
    private static final float SINKHOLE_SIZE_FACTOR = .15f;
    private static final float SMALL_SINKHOLE_SIZE_FACTOR = .11f;
    private static final float BAR_HEIGHT = 70;

    private static final FixtureDef OBSTACLE_FIX_DEF = PhysicsFactory.createFixtureDef(0, .5f, .01f);
    private static final FixtureDef BALL_FIX_DEF = PhysicsFactory.createFixtureDef(Physics.BALL_DENSITY, .45f, .01f);
    private static final float MAX_ORIENTATION_ANGLE = .35f;

    private int _width;
    private int _height;
    private float _scale;
    private float _density;
    private Engine _engine;
    private boolean _doDebugDraw;
    private PhysicsWorld _physicsWorld;
    private OrientationProvider _orientationProvider;
    private Vector2 _gravity = Vector2Pool.obtain();
    private LevelController _levelController;
    private GameLevelStateListener _gameLevelStateListener;
    private Body _ball;
    private Ball _ballSprite;
    private GameLevel _gameLevel;
    private boolean _isAnimating;
    private float _radToGravity = (float) (Physics.GRAVITY_FACTOR / Math.PI);
    private final Vector2 _gravityCorrection;
    private Rectangle _timerBar;
    private float _timePassed;
    private float _levelDuration;
    private Text _timeText;
    private boolean _started;
    private SpawnHole _sinkHole;
    private SpawnHole _spawnHole;
    private SpawnHole _sinkHoleBorder;
    private Flash _flash;
    private Flash _timesUpFlash;
    private int _backgroundColor;
    private int _nextBackgroundColor;
    private AnimationCallback _flashAnimationCallback;
    private AnimationCallback _spawnHoleAnimationCallback;
    private boolean _doDestructBall;
    private boolean _isLevelCompleted;
    private SpriteBackground _background;
    private int _screenRotation;

    public WorldController(int width, int height, float scale, float density, Engine engine) {
        _width = width;
        _height = height;
        _scale = scale;
        _density = density;
        _engine = engine;

        // set physics value from preferences if they have been changed
        BALL_FIX_DEF.density = Prefs.getFloat(PrefKeys.BALL_DENSITY, Physics.BALL_DENSITY);
        _radToGravity = _scale * (float) (Prefs.getFloat(PrefKeys.GRAVITY_FACTOR, Physics.GRAVITY_FACTOR) / Math.PI);
        OBSTACLE_FIX_DEF.restitution = Prefs.getFloat(PrefKeys.WALL_ELASTICITY, Physics.WALL_ELASTICITY);
        _gravityCorrection = Vector2Pool.obtain(Prefs.getFloat(PrefKeys.CALIBRATION_X, 0), Prefs.getFloat(PrefKeys.CALIBRATION_Y, 0));
    }

    public void setDoDebugDraw(boolean doDebugDraw) {
        _doDebugDraw = doDebugDraw;
    }

    /**
     * Clear the current level
     */
    public void clearLevel() {
        _started = false;

        if (_levelController != null) {
            unregisterUpdateHandler(_levelController);

            _levelController.dispose();
            _levelController = null;
        }

        if (_gameLevel != null) {
            _gameLevel.dispose();
            _gameLevel = null;
        }

        if (_orientationProvider != null) {
            _orientationProvider.stop();
        }

        if (_physicsWorld != null) {
            _physicsWorld.dispose();
        }

        detachChildren();

        if (_ballSprite != null) _ballSprite.dispose();
        if (_timeText != null) _timeText.dispose();
        if (_timerBar != null) _timerBar.dispose();
        if (_sinkHole != null) _sinkHole.dispose();
        if (_spawnHole != null) _spawnHole.dispose();
        if (_flash != null) _flash.dispose();

        _flashAnimationCallback = null;
        _spawnHoleAnimationCallback = null;

        if(_background != null) _background.getSprite().dispose();
    }

    @Override
    public void dispose() {
        _engine = null;
        _physicsWorld = null;

        super.dispose();
    }

    /**
     * Create a new level
     *
     * @param levelVO             level description
     * @param backgroundColor     background color
     * @param nextBackgroundColor background color of next level, used for sinkhole
     * @return the created game level
     */
    public GameLevel createLevel(LevelVO levelVO, int backgroundColor, int nextBackgroundColor) {
        if (levelVO == null) return null;

        _backgroundColor = backgroundColor;
        _nextBackgroundColor = nextBackgroundColor;

        return createLevel(ClassUtil.getClassForName(levelVO.levelClass), ClassUtil.getClassForName(levelVO.controllerClass), levelVO.id < 1);
    }

    /**
     * Create a new level
     *
     * @param levelClass      class which will show the UI
     * @param controllerClass class which will control the level
     * @param isTutorial      if true, the level is a tutorial level
     * @return the created game level
     */
    private GameLevel createLevel(Class levelClass, Class controllerClass, boolean isTutorial) {
        _physicsWorld = new PhysicsWorld(_gravity, false);

        if (isTutorial) {
            _backgroundColor = LevelColorUtil.fromLevelColor(LevelColor.PURPLE);
        }

        GameLevel level;
        try {
            level = (GameLevel) levelClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        level.init(_width, _height, _scale, _density);
        level.createLevel(_physicsWorld, OBSTACLE_FIX_DEF);

        createBackground(level.getBackground(), null);

        if (_doDebugDraw) {
            DebugRenderer debugRenderer = new DebugRenderer(_physicsWorld, _engine.getVertexBufferObjectManager());
            _engine.getScene().attachChild(debugRenderer);
        }

        createSinkHole(level.getSinkholeLocation());
        createSpawnHole(level.getBallSpawnLocation());
        createBall(level.getBallSpawnLocation());

        initTimer(level.getLevelDuration());

        createEdgeSensors();

        if (controllerClass != null) {
            createLevelController(controllerClass, level);
        } else {
            _levelController = null;
        }

        _gameLevel = level;
        _orientationProvider.start();

        _flash = createFlash(_nextBackgroundColor);
        _timesUpFlash = createFlash(LevelColorUtil.fromLevelColor(LevelColor.YELLOW));

        return level;
    }

    /**
     * Respawn ball after it's fallen off the side
     */
    public void respawnBall() {
        if (_isLevelCompleted) return;

        _doDestructBall = true;

        final Vector2 location = _gameLevel.getBallSpawnLocation();

        //show ball again
        _ballSprite.setScale(0);
        _ballSprite.setX(location.x);
        _ballSprite.setY(location.y);

        _isAnimating = true;

        _spawnHoleAnimationCallback = new AnimationCallback() {
            @Override
            public void onComplete() {
                createPhysicalBall(location);

                if (_levelController != null) _levelController.reset();
                _isAnimating = false;
            }
        };

        if (_flashAnimationCallback == null) {
            _flashAnimationCallback = new AnimationCallback() {
                @Override
                public void onComplete() {
                    _spawnHole.show();
                    _ballSprite.show();
                    _spawnHole.hide(300, _spawnHoleAnimationCallback);
                }
            };
        }

        // play double flash
        _flash.play(2, _flashAnimationCallback);
    }

    /**
     * Initialize the timer bar
     * @param levelDuration duration in seconds for full bar
     */
    private void initTimer(float levelDuration) {
        _levelDuration = levelDuration;
        _timePassed = 0.0f;

        float scaleY = _height / 1920f;
        scaleY = scaleY + ((1 - scaleY) * .5f);
        int barHeight = (int) ((BAR_HEIGHT + ((_density - 1.5) * 30)) * scaleY);

        if (levelDuration > 0) {
            Rectangle timerBarBackground = new Rectangle(_width / 2, _height - barHeight / 2, _width, barHeight, _engine.getVertexBufferObjectManager());
            timerBarBackground.setColor(_backgroundColor);
            attachChild(timerBarBackground);

            timerBarBackground = new Rectangle(_width / 2, _height - barHeight / 2, _width, barHeight, _engine.getVertexBufferObjectManager());
            timerBarBackground.setColor(new Color(0, 0, 0, .3f));
            attachChild(timerBarBackground);

            _timerBar = new Rectangle(_width / 2, _height - barHeight / 2, _width, barHeight, _engine.getVertexBufferObjectManager());
            _timerBar.setColor(ColorUtils.convertARGBPackedIntToColor(0xFFFDD420));
            _timerBar.setScaleX(0);
            _timerBar.setScaleCenterX(0);
            attachChild(_timerBar);

            Typeface typeFace = Typeface.createFromAsset(GoogleFlipGameApplication.sContext.getAssets(), FontFaceType.FUTURA_MEDIUM.getAssetName());
            Font smallWhiteFont = FontFactory.create(_engine.getFontManager(), _engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR,
                    typeFace, 20 * _density, Color.WHITE_ARGB_PACKED_INT);
            smallWhiteFont.load();

            _timeText = new Text(_width, _height - barHeight / 2, smallWhiteFont, "00.0", _engine.getVertexBufferObjectManager());
            _timeText.setOffsetCenterX(0);
            _timeText.setX(_width - (_timeText.getWidth() + (15 * _density)));
            _timeText.setText(String.format(Locale.getDefault(), "%.01f", 0.0f));

            attachChild(_timeText);
        }
    }

    private void createBackground(final Bitmap background, @Nullable final String backgroundUrl) {
        if (background == null) return;

        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(_engine.getTextureManager(), _width, _height, TextureOptions.BILINEAR);
        IBitmapTextureAtlasSource baseTextureSource = new EmptyBitmapTextureAtlasSource(_width, _height);

        final IBitmapTextureAtlasSource bitmapTextureAtlasSource = new BaseBitmapTextureAtlasSourceDecorator(baseTextureSource) {
            @Override
            protected void onDecorateBitmap(Canvas pCanvas) throws Exception {
                if (backgroundUrl != null) {
                    Bitmap template = BitmapFactory.decodeStream(GoogleFlipGameApplication.sContext.getAssets().open(backgroundUrl));
                    pCanvas.drawBitmap(template, 0, 0, mPaint);
                }

                pCanvas.drawColor(_backgroundColor);
                pCanvas.drawBitmap(background, 0, 0, mPaint);
            }

            @Override
            public BaseBitmapTextureAtlasSourceDecorator deepCopy() {
                throw new IModifier.DeepCopyNotSupportedException();
            }
        };

        ITextureRegion mDecoratedBalloonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromSource(bitmapTextureAtlas, bitmapTextureAtlasSource,
                0, 0);
        bitmapTextureAtlas.load();

        Sprite sprite = new Sprite(0, 0, mDecoratedBalloonTextureRegion, _engine.getVertexBufferObjectManager());
        sprite.setOffsetCenter(0, 0);
        sprite.setWidth(_width);
        sprite.setHeight(_height);
        _background = new SpriteBackground(sprite);
        _engine.getScene().setBackground(_background);
    }

    private Flash createFlash(int color) {
        Flash flash = new Flash(0, 0, _width, _height, _engine.getVertexBufferObjectManager());
        flash.setOffsetCenter(0, 0);
        flash.setAlpha(0);
        flash.setColor(color);

        attachChild(flash);

        return flash;
    }

    private void createLevelController(Class controllerClass, GameLevel level) {
        if (_levelController != null) {
            _levelController.dispose();
            _levelController = null;
        }

        try {
            _levelController = (LevelController) controllerClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        _levelController.init(level, _physicsWorld, _engine);
        _levelController.setGameLevelStateListener(_gameLevelStateListener);
        _levelController.setBallSprite(_ballSprite);
    }

    private void createBall(Vector2 location) {
        if (location == null) return;

        createPhysicalBall(location);

        AssetBitmapTexture texture;
        try {
            texture = new AssetBitmapTexture(_engine.getTextureManager(), GoogleFlipGameApplication.sContext.getAssets(), "ball.png");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        texture.load();

        _ballSprite = new Ball(location.x, location.y, TextureRegionFactory.extractFromTexture(texture), _engine.getVertexBufferObjectManager());
        _ballSprite.setWidth(2 * BALL_SIZE_FACTOR * _width);
        _ballSprite.setHeight(2 * BALL_SIZE_FACTOR * _width);
        attachChild(_ballSprite);
    }

    private void createPhysicalBall(Vector2 location) {
        if (location == null) return;

        _ball = PhysicsFactory.createCircleBody(_physicsWorld, location.x, location.y, BALL_SIZE_FACTOR * _width, 0, BodyDef.BodyType.DynamicBody,
                BALL_FIX_DEF);
        _ball.setFixedRotation(true);
        _ball.setUserData(ObjectName.BALL_NAME);
    }

    private void createEdgeSensors() {
        float thickness = BOUNDS_THICKNESS_FACTOR * _width;
        float barHeight = _timerBar != null ? _timerBar.getHeight() : 0;

        createEdgeSensor(_width / 2, thickness / 2, _width, thickness);
        createEdgeSensor(_width / 2, _height - ((thickness / 2) + barHeight), _width, thickness);
        createEdgeSensor(thickness / 2, _height / 2, thickness, _height);
        createEdgeSensor(_width - thickness / 2, _height / 2, thickness, _height);
    }

    private void createSinkHole(Vector2 location) {
        if (location == null) return;

        Body sinkHoleBody = PhysicsFactory.createCircleBody(_physicsWorld, location.x, location.y, SINKHOLE_SIZE_FACTOR * _width / 2,
                BodyDef.BodyType.StaticBody, SENSOR_FIX_DEF);
        sinkHoleBody.setUserData(ObjectName.SINKHOLE_NAME);

        _sinkHoleBorder = drawHole(location, "portal_border.png");
        _sinkHoleBorder.setColor(ColorUtils.convertARGBPackedIntToColor(_nextBackgroundColor));

        _sinkHole = drawHole(location, "portal.png");
        assert _sinkHole != null;
        _sinkHole.setWidth(SMALL_SINKHOLE_SIZE_FACTOR * _width);
        _sinkHole.setHeight(SMALL_SINKHOLE_SIZE_FACTOR * _width);
    }

    private SpawnHole drawHole(Vector2 location, String textureName) {
        AssetBitmapTexture texture;
        try {
            texture = new AssetBitmapTexture(_engine.getTextureManager(), GoogleFlipGameApplication.sContext.getAssets(), textureName, TextureOptions.BILINEAR);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        texture.load();
        SpawnHole hole = new SpawnHole(location.x, location.y, TextureRegionFactory.extractFromTexture(texture), _engine.getVertexBufferObjectManager());
        hole.setWidth(SINKHOLE_SIZE_FACTOR * _width);
        hole.setHeight(SINKHOLE_SIZE_FACTOR * _width);
        attachChild(hole);

        return hole;
    }

    private void createSpawnHole(Vector2 location) {
        if (location == null) return;
        _spawnHole = drawHole(location, "portal.png");
    }

    private void createEdgeSensor(float x, float y, float w, float h) {
        Body body = PhysicsFactory.createBoxBody(_physicsWorld, x, y, w, h, BodyDef.BodyType.StaticBody, SENSOR_FIX_DEF);
        body.setUserData(ObjectName.EDGE_SENSOR_NAME);
    }

    public void setOrientationProvider(OrientationProvider orientationProvider, int rotation) {
        _orientationProvider = orientationProvider;
        _screenRotation = rotation;
    }

    /**
     * Game loop
     */
    @Override
    public void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);

        if (_levelController == null) {
            return;
        }

        if (_started) {
            // update gravity from phone orientation
            EulerAngles eulerAngles = _orientationProvider.getEulerAngles();
            // get limited roll & pitch
            float roll = MathUtils.bringToBounds(-MAX_ORIENTATION_ANGLE, MAX_ORIENTATION_ANGLE, eulerAngles.getRoll());
            float pitch = MathUtils.bringToBounds(-MAX_ORIENTATION_ANGLE, MAX_ORIENTATION_ANGLE, eulerAngles.getPitch());
            // correct for screen orientation, different on tablets than on phones
            float swap;
            switch (_screenRotation) {
                case Surface.ROTATION_0:
                    break;
                case Surface.ROTATION_270:
                    swap = pitch;
                    pitch = -roll;
                    roll = swap;
                    break;
                case Surface.ROTATION_180:
                    pitch = -pitch;
                    roll = -roll;
                    break;
                case Surface.ROTATION_90:
                    swap = pitch;
                    pitch = roll;
                    roll = -swap;
                    break;
            }
            _gravity.set(_radToGravity * roll, _radToGravity * pitch);
            _gravity.add(_gravityCorrection);
            _physicsWorld.setGravity(_gravity);

            checkDestroyBall();

            // update ball location, if there's a ball, we're not animating, and the level hasn't been completed
            if (_ball != null && !_isAnimating && !_isLevelCompleted) {
                _timePassed += pSecondsElapsed;

                // update physics world
                _physicsWorld.onUpdate(pSecondsElapsed);

                // check if the ball needs to be destroyed
                checkDestroyBall();

                // recheck, since world update may have removed the ball
                if (_ball != null && !_isAnimating) {
                    Vector2 ballPosition = _ball.getPosition();
                    ballPosition.mul(PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
                    _ballSprite.setPosition(ballPosition.x, ballPosition.y);
                }

                if (_levelDuration > 0) {
                    updateTimer(pSecondsElapsed);
                }
            }
        }
    }

    public void checkDestroyBall() {
        if (_doDestructBall) {
            _doDestructBall = false;
            _physicsWorld.destroyBody(_ball);
            _ball = null;
        }
    }

    /**
     * update the timer bar
     */
    private void updateTimer(float pSecondsElapsed) {
        _timerBar.setScaleX(_timePassed / _levelDuration);
        _timeText.setText(String.format(Locale.getDefault(), "%.01f", _timePassed));

        if (_timerBar.getScaleX() > 1.0f) {
            _timePassed = _levelDuration;

            Log.d(TAG, "updateTimer: TIME OUT!");

            if (_gameLevelStateListener != null) {
                _gameLevelStateListener.onTimeOut();
            }
        }
    }

    public void playTimeUpAnimation() {
        _isAnimating = true;

        _timesUpFlash.play(3, new AnimationCallback() {
            @Override
            public void onComplete() {
                if (_gameLevelStateListener != null) {
                    _gameLevelStateListener.onOutAnimationComplete();
                }
                _isAnimating = false;
            }
        });
    }

    public void playLevelEndAnimation() {
        Vector2 destPos = _gameLevel.getSinkholeLocation();

        // if level doesn't have a sink, don't bother with the animation
        if (_ballSprite == null || destPos == null) {
            _gameLevelStateListener.onOutAnimationComplete();
            return;
        }

        _isAnimating = true;

        long duration = 200;

        ObjectAnimator.ofFloat(_ballSprite, "x", destPos.x).setDuration(duration).start();
        ObjectAnimator animator = ObjectAnimator.ofFloat(_ballSprite, "y", destPos.y).setDuration(duration);
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                _ballSprite.hide(100);

                _sinkHole.hide(200, null);
                _sinkHoleBorder.hide(300, new AnimationCallback() {
                    @Override
                    public void onComplete() {
                        if (_gameLevelStateListener != null) {
                            _gameLevelStateListener.onOutAnimationComplete();
                        }

                        _isAnimating = false;
                    }
                });
            }
        });
    }

    public void start() {
        _sinkHole.show();
        _sinkHoleBorder.show();

        _spawnHole.show(500, null);
        _spawnHole.hide(1200, new AnimationCallback() {
            @Override
            public void onComplete() {
                _started = true;
                _isLevelCompleted = false;

                if (_levelController != null) {
                    registerUpdateHandler(_levelController);
                }
            }
        });
        _ballSprite.show(500, null);
    }

    @Override
    public void reset() {
        _started = false;
        _physicsWorld.reset();
    }

    public void setGameLevelStateListener(GameLevelStateListener handler) {
        _gameLevelStateListener = handler;
    }

    public float getTimePassed() {
        return _timePassed;
    }

    public void setIsLevelCompleted(boolean isLevelCompleted) {
        _isLevelCompleted = isLevelCompleted;
    }
}
