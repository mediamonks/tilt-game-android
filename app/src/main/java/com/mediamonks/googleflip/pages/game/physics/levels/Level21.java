package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level21 extends AbstractGameLevel implements GameLevel {
	public Level21() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		createOpenCircle(world, fixtureDef, -90, 90, 580, 0, 878, 35);

		createOpenCircle(world, fixtureDef, 90, 270, 300, 1080, 1299, 30);
		createOpenCircle(world, fixtureDef, 180, 360, 300, 497, 1920, 30);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(133, 200);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(957, 1798);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level21.png";
	}
}
