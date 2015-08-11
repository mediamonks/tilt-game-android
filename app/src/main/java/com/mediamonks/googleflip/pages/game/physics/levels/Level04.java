package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;

/**
 * UI/physics implementation of game level
 */
public class Level04 extends AbstractGameLevel implements GameLevel {

	public Level04() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		Vector2 center = Vector2Pool.obtain(540, 1003);

		createOpenCircle(world, fixtureDef, 0, 360, 386, center.x, center.y, 50);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		assertInitialized();

		return getScaledVector(540, 276);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		assertInitialized();

		return getScaledVector(540, 1695);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level4.png";
	}
}
