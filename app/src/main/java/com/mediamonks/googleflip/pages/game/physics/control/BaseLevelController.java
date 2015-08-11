package com.mediamonks.googleflip.pages.game.physics.control;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.pages.game.physics.levels.GameLevel;
import com.mediamonks.googleflip.pages.game.physics.util.ContactListenerAdapter;
import com.mediamonks.googleflip.util.SoundManager;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * Base class for level control
 */
public abstract class BaseLevelController implements LevelController {
    protected GameLevelStateListener _gameLevelStateListener;
    protected GameLevel _gameLevel;
    protected PhysicsWorld _physicsWorld;
    protected Engine _engine;
    protected Sprite _ballSprite;
    protected boolean _isLevelComplete;
    protected boolean _runChecks = true;
    protected ContactListener _contactListener;

    @Override
    public void init(GameLevel gameLevel, PhysicsWorld physicsWorld, Engine engine) {
        _gameLevel = gameLevel;
        _physicsWorld = physicsWorld;
        _engine = engine;

        _contactListener = new ContactListenerAdapter() {
            @Override
            public void beginContact(Contact contact) {
                checkCollision(contact);
            }
        };
        _physicsWorld.setContactListener(_contactListener);
    }

    protected abstract void checkCollision(Contact contact);

    @Override
    public void setGameLevelStateListener(GameLevelStateListener handler) {
        _gameLevelStateListener = handler;
    }

    @Override
    public void reset() {
        _runChecks = true;
    }

    protected void setBallOut() {
        // game over, make sure we skip running checks
        _runChecks = false;

        SoundManager.getInstance().play(R.raw.fall_off);

        if (_gameLevelStateListener != null) {
            _gameLevelStateListener.onBallOut();
        }
    }

    protected void setLevelComplete() {
        // game over, make sure we skip running checks
        _runChecks = false;
        _isLevelComplete = true;

        SoundManager.getInstance().play(R.raw.into_portal);

        if (_gameLevelStateListener != null) {
            _gameLevelStateListener.onLevelComplete();
        }
    }

    @Override
    public void setBallSprite(Sprite ballSprite) {
        _ballSprite = ballSprite;
    }

    @Override
    public void start() {

    }

    @Override
    public void dispose() {
        _gameLevel = null;
        _physicsWorld = null;
        _engine = null;
        _ballSprite = null;
        _gameLevelStateListener = null;
    }
}
