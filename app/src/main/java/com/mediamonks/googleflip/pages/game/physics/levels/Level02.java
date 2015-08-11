package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level02 extends AbstractGameLevel implements GameLevel {
	public Level02() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		assertInitialized();

		// lines at an angle
		int boxWidth = 30;
		int rotation = 45;
		createBox(world, fixtureDef, 180, 454, boxWidth, 304, rotation);
		createBox(world, fixtureDef, 492, 675, boxWidth, 403, -rotation);
		createBox(world, fixtureDef, 180, 1058, boxWidth, 304, rotation);
		createBox(world, fixtureDef, 492, 1292, boxWidth, 403, -rotation);
		createBox(world, fixtureDef, 180, 1651, boxWidth, 304, rotation);

		// middle line
		createBox(world, fixtureDef, 635, 842, 30, 1685);

		// lines at the right
		int boxHeight = 30;
		createBox(world, fixtureDef, 1004, 562, 154, boxHeight);
		createBox(world, fixtureDef, 942, 1185, boxWidth, 1248);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		assertInitialized();

		return getScaledVector(189, 272);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		assertInitialized();

		return getScaledVector(977, 432);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level2.png";
	}
}
