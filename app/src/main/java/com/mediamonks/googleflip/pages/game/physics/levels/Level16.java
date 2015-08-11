package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level16 extends AbstractGameLevel implements GameLevel {

	public Level16() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		int boxHeight = 30;
		createBox(world, fixtureDef, 225, 1907, 454, boxHeight);

		createBox(world, fixtureDef, 463, 347, 205, boxHeight, 45);
		int boxWidth = 30;
		createBox(world, fixtureDef, 425, 456, boxWidth, 485, 45);

		createBox(world, fixtureDef, 482, 721, boxWidth, 311, -45);
		createBox(world, fixtureDef, 482, 922, boxWidth, 311, 45);

		createBox(world, fixtureDef, 475, 1128, boxWidth, 300, -45);
		createBox(world, fixtureDef, 481, 1325, boxWidth, 311, 45);

		createBox(world, fixtureDef, 490, 1515, boxWidth, 266, -45);
		createBox(world, fixtureDef, 490, 1694, boxWidth, 282, 45);

		createBox(world, fixtureDef, 502, 1860, boxWidth, 249, -45);

		createOpenCircle(world, fixtureDef, -90, 50, 130, 618, 195, 30);

		createBox(world, fixtureDef, 650, 350, 200, boxHeight, 47);

		createBox(world, fixtureDef, 688, 515, boxWidth, 311, 45);
		createBox(world, fixtureDef, 683, 718, boxWidth, 317, -45);

		createBox(world, fixtureDef, 682, 920, boxWidth, 311, 45);
		createBox(world, fixtureDef, 678, 1123, boxWidth, 315, -45);

		createBox(world, fixtureDef, 681, 1328, boxWidth, 289, 45);
		createBox(world, fixtureDef, 702, 1490, boxWidth, 201, -45);

		createBox(world, fixtureDef, 986, 422, 192, boxHeight);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(360, 1769);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(939, 244);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level16.png";
	}
}
