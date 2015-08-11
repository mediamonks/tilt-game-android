package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level12 extends AbstractGameLevel implements GameLevel {

	public Level12() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		int boxWidth = 30;
		createBox(world, fixtureDef, 534, 370, boxWidth, 742);

		createOpenCircle(world, fixtureDef, -83, 76, 375, 636, 961, 30);

		createOpenCircle(world, fixtureDef, -201, 115, 249, 640, 964, 20);

		createOpenCircle(world, fixtureDef, 180, 398, 113, 647, 964, 20);
		createOpenCircle(world, fixtureDef, 0, 175, 123, 411, 1048, 20);

		createBox(world, fixtureDef, 534, 1309, boxWidth, 698);
		int boxHeight = 30;
		createBox(world, fixtureDef, 411, 1654, 278, boxHeight);

		createBox(world, fixtureDef, 736, 1453, boxWidth, 282);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(903, 210);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(649, 962);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level12.png";
	}
}
