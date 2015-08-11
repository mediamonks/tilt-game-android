package com.mediamonks.googleflip.pages.game.physics.control;

import com.mediamonks.googleflip.pages.game.physics.levels.GameLevel;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * Interface for level controllers
 */
public interface LevelController extends IUpdateHandler {
    void init(GameLevel gameLevel, PhysicsWorld physicsWorld, Engine engine);

    void setBallSprite (Sprite ballSprite);

    void start ();

    void setGameLevelStateListener(GameLevelStateListener handler);

    void dispose();
}
