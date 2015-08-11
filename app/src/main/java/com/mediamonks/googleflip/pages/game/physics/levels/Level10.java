package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level10 extends AbstractGameLevel implements GameLevel {

	public Level10() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		int boxHeight = 30;
		createBox(world, fixtureDef, 115, 313, 278, boxHeight);
		createBox(world, fixtureDef, 827, 313, 573, boxHeight);

		createBox(world, fixtureDef, 263, 620, 573, boxHeight);
		createBox(world, fixtureDef, 974, 620, 277, boxHeight);

		createBox(world, fixtureDef, 722, 928, 732, boxHeight);

		createBox(world, fixtureDef, 432, 1236, 895, boxHeight);
		createBox(world, fixtureDef, 640, 1543, 906, boxHeight);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(115, 205);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(899, 1754);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level10.png";
	}
}
