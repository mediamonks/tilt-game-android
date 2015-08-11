package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level14 extends AbstractGameLevel implements GameLevel {
	public Level14() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		int boxWidth = 30;
		createBox(world, fixtureDef, 220, 1010, boxWidth, 1298);
		createBox(world, fixtureDef, 457, 987, boxWidth, 1342);
		createBox(world, fixtureDef, 676, 944, boxWidth, 1424);
		createBox(world, fixtureDef, 806, 881, boxWidth, 1045);
		createBox(world, fixtureDef, 935, 1016, boxWidth, 1315);

		int boxHeight = 30;
		createBox(world, fixtureDef, 577, 1659, 743, boxHeight);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(800, 1796);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(800, 1530);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level14.png";
	}
}
