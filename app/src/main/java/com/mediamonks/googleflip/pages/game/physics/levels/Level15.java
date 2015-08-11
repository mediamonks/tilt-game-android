package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level15 extends AbstractGameLevel implements GameLevel {

	public Level15() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		int boxWidth = 30;
		createBox(world, fixtureDef, 150, 1124, boxWidth, 1352);
		int boxHeight = 30;
		createBox(world, fixtureDef, 354, 463, 403, boxHeight);
		for (int i = 0; i < 3; i++) {
			createBox(world, fixtureDef, 469, i * 358 + 841, 636, boxHeight);
		}
		createBox(world, fixtureDef, 548, 1785, 797, boxHeight);


		for (int i = 0; i < 3; i++) {
			createBox(world, fixtureDef, 605, i * 358 + 662, 636, boxHeight);
		}
		createBox(world, fixtureDef, 930, 947, boxWidth, 1415);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(265, 1673);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(223, 278);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level15.png";
	}
}
