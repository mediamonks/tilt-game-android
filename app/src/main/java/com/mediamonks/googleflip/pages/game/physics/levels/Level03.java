package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;

/**
 * UI/physics implementation of game level
 */
public class Level03 extends AbstractGameLevel implements GameLevel {

	public Level03() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		Vector2 center = Vector2Pool.obtain(541, 942);

		createOpenCircle(world, fixtureDef, -70, 250, 133, center.x, center.y, 20);
		createOpenCircle(world, fixtureDef, 103, 438.5f, 262, center.x, center.y, 30);
		createOpenCircle(world, fixtureDef, -81, 260, 395, center.x, center.y, 40);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		assertInitialized();

		return getScaledVector(541, 941);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		assertInitialized();

		return getScaledVector(541, 1709);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level3.png";
	}
}
