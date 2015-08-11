package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level07 extends AbstractGameLevel implements GameLevel {
	public Level07() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		createBox(world, fixtureDef, 712, 320, 735, 30);

		createBox(world, fixtureDef, 173, 1071, 30, 637);
		createBox(world, fixtureDef, 191, 955, 175, 30);

		createBox(world, fixtureDef, 410, 1161, 30, 457);

		createBox(world, fixtureDef, 642, 929, 30, 922);

		createBox(world, fixtureDef, 879, 1059, 30, 661);
		createBox(world, fixtureDef, 897, 955, 175, 30);

		createBox(world, fixtureDef, 526, 1396, 735, 30);

		createBox(world, fixtureDef, 368, 1562, 735, 30);

		createBox(world, fixtureDef, 538, 1906, 735, 30);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(514, 1730);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(294, 1277);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level7.png";
	}
}
