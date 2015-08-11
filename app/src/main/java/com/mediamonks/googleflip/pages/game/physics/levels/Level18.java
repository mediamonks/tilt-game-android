package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level18 extends AbstractGameLevel implements GameLevel {

	public Level18() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		int boxWidth = 30;
		createBox(world, fixtureDef, 212, 1081, boxWidth, 1678);

		int boxHeight = 30;
		for(int i = 0; i < 4; i++) {
			if(i == 0) {
				createBox(world, fixtureDef, 370, i * 385 + 257, 336, boxHeight);
			} else {
				createBox(world, fixtureDef, 385, i * 385 + 257, 366, boxHeight);
			}
		}

		for(int i = 0; i < 3; i++) {
			if(i == 0) {
				createBox(world, fixtureDef, 784, 498, 294, boxHeight);
			} else {
				createBox(world, fixtureDef, 694, i * 386 + 448, 456, boxHeight);
			}
		}
		createBox(world, fixtureDef, 652, 409, boxWidth, 177);
		createOpenCircle(world, fixtureDef, 180, 360, 225, 877, 336, 30);
		createBox(world, fixtureDef, 933, 955, boxWidth, 1279);
		createOpenCircle(world, fixtureDef, 0, 180, 225, 870, 1591, 30);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(101, 1785);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(794, 387);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level18.png";
	}
}
