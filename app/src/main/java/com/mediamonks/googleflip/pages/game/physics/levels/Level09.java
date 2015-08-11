package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level09 extends AbstractGameLevel implements GameLevel {
	public Level09() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		//small circle
		createOpenCircle(world, fixtureDef, 270, 280.5f, 410, -18, 968, 5);
		createOpenCircle(world, fixtureDef, -51.5f, 90, 410, -18, 968, 40);

		//middle circle
		createOpenCircle(world, fixtureDef, -90, 67.5f, 688, -29, 999, 50);
		createOpenCircle(world, fixtureDef, 79, 90, 688, -29, 999, 10);

		//big circle
		createOpenCircle(world, fixtureDef, 270, 309, 904, -20, 999, 20);
		createOpenCircle(world, fixtureDef, -43, 90, 904, -20, 999, 50);

		createBox(world, fixtureDef, 540, 1904, 1080, 30);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(667, 1778);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(132, 1158);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level9.png";
	}
}
