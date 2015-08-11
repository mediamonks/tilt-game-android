package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level11 extends AbstractGameLevel implements GameLevel {
	public Level11() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		createWave(world, fixtureDef, 141, 0.6f, 0, 470, 874, 8);

		createWave(world, fixtureDef, 155, 0.6f, 0, 1124, 874, 8);

		createWave(world, fixtureDef, 141, 0.6f, 0, 1124, 874, 8);

		createWave(world, fixtureDef, 161, 0.4f, 0, 1841, 683, 0);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(133, 202);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(923, 1757);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level11.png";
	}
}
