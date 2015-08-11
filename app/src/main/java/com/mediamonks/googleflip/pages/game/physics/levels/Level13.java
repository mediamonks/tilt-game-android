package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level13 extends AbstractGameLevel implements GameLevel {
	public Level13() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		float height = 1680;
		float width = 30;

		createBox(world, fixtureDef, 196, 843, width, height);
		createBox(world, fixtureDef, 420, 1083, width, height);
		createBox(world, fixtureDef, 644, 843, width, height);
		createBox(world, fixtureDef, 858, 1083, width, height);

		drawBox(196, 843, width, height);
		drawBox(420, 1083, width, height);
		drawBox(644, 843, width, height);
		drawBox(858, 1083, width, height);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(89, 220);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(980, 1800);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level13.png";
	}
}
