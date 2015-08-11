package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level22 extends AbstractGameLevel implements GameLevel {
	public Level22() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		createOpenCircle(world, fixtureDef, -82, 263, 518, 539, 963, 50);

		createBox(world, fixtureDef, 540, 960, 647, 30, 45);
		createBox(world, fixtureDef, 540, 960, 647, 30, -45);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(135, 230);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(540, 1144);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level22.png";
	}
}
