package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level05 extends AbstractGameLevel implements GameLevel {

	public Level05() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		assertInitialized();

		int boxHeight = 30;
		createBox(world, fixtureDef, 633, 495, 615, boxHeight, 45);
		int boxWidth = 30;
		createBox(world, fixtureDef, 767, 1042, boxWidth, 943, 45);

		createBox(world, fixtureDef, 407, 1086, boxWidth, 1209, 45);
		createBox(world, fixtureDef, 372, 1435, 538, boxHeight, 45);
		createBox(world, fixtureDef, 717, 1623, 352, boxHeight, 45);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		assertInitialized();

		return getScaledVector(337, 1233);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		assertInitialized();

		return getScaledVector(370, 305);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level5.png";
	}
}
