package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level01 extends AbstractGameLevel implements GameLevel {
	private static final String TAG = Level01.class.getSimpleName();

	public Level01() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		assertInitialized();

		createBox(world, fixtureDef, 555, 1548, 578, 30);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		assertInitialized();

		return getScaledVector(540, 383);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		assertInitialized();

		return getScaledVector(540, 1695);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level1.png";
	}
}
