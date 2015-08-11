package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level20 extends AbstractGameLevel implements GameLevel {
	public Level20() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		createOpenCircle(world, fixtureDef, 90, 180, 300, 1082, 0, 20);

		createOpenCircle(world, fixtureDef, -45, 135.5f, 205, 446, 394, 30);

		createOpenCircle(world, fixtureDef, 90, 270, 300, 1080, 825, 30);

		createOpenCircle(world, fixtureDef, -180, 34, 300, 271, 1081, 30);

		createOpenCircle(world, fixtureDef, 41, 225, 275, 503, 1258, 30);
		createOpenCircle(world, fixtureDef, 225, 310, 303, 920, 1658, 20);

		createOpenCircle(world, fixtureDef, 270, 360, 300, 0, 1920, 20);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(442, 387);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(906, 1555);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level20.png";
	}
}
