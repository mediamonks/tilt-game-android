package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level08 extends AbstractGameLevel implements GameLevel {

	public Level08() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		createOpenCircle(world, fixtureDef, -57, 88, 890, 173, 1030, 35);
		createOpenCircle(world, fixtureDef, -56, 110, 625, 190, 1004, 30);

		createBox(world, fixtureDef, 15, 1766, 30, 308);
		createBox(world, fixtureDef, 177, 1908, 326, 30);

		createOpenCircle(world, fixtureDef, -60, 78, 458, 110, 989, 20);
		createOpenCircle(world, fixtureDef, 147, 257, 500, 662, 976, 20);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(113, 1805);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(344, 823);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level8.png";
	}
}
