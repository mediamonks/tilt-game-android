package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level06 extends AbstractGameLevel implements GameLevel {
	public Level06() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		createOpenCircleBody(world, fixtureDef, 123, 270, 815, 775, 1080, 948, 40);
		drawOpenCircle(123, 270, 815, 775, 1080, 948, 0, 0);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(377, 235);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(407, 895);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level6.png";
	}
}
