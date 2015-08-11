package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level17 extends AbstractGameLevel implements GameLevel {

	public Level17() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		float rotation = 45;

		int boxHeight = 30;
		createBox(world, fixtureDef, 64, 432, 266, boxHeight, rotation);
		createBox(world, fixtureDef, 183, 858, 624, boxHeight, rotation);
		createBox(world, fixtureDef, 308, 1278, 965, boxHeight, rotation);
		createBox(world, fixtureDef, 543, 1588, 996, boxHeight, rotation);

		createBox(world, fixtureDef, 511, 257, 776, boxHeight, rotation);
		createBox(world, fixtureDef, 810, 502, 813, boxHeight, rotation);
		createBox(world, fixtureDef, 855, 1004, 702, boxHeight, rotation);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(121, 206);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(920, 1766);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level17.png";
	}
}
