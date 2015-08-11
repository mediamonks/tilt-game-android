package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level23 extends AbstractGameLevel implements GameLevel {

	public Level23() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		int boxWidth = 30;
		createBox(world, fixtureDef, 266, 343, boxWidth, 819, 45);
		createBox(world, fixtureDef, 266, 343, boxWidth, 819, -45);

		createBox(world, fixtureDef, 815, 345, boxWidth, 819, 45);
		createBox(world, fixtureDef, 957, 204, boxWidth, 418, -45);
		//--
		int boxHeight = 30;
		createBox(world, fixtureDef, 97, 1010, 191, boxHeight);

		createBox(world, fixtureDef, 541, 1010, boxWidth, 819, 45);
		createBox(world, fixtureDef, 633, 918, boxWidth, 1079, -45);

		createBox(world, fixtureDef, 986, 1010, 191, boxHeight);
		//--
		createBox(world, fixtureDef, 266, 1653, boxWidth, 819, 45);
		createBox(world, fixtureDef, 266, 1653, boxWidth, 819, -45);

		createBox(world, fixtureDef, 958, 1799, boxWidth, 414, 45);
		createBox(world, fixtureDef, 815, 1655, boxWidth, 819, -45);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(514, 334);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(966, 839);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level23.png";
	}
}
