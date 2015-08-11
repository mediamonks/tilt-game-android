package com.mediamonks.googleflip.pages.game.physics.levels;

import android.graphics.Point;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * UI/physics implementation of game level
 */
public class Level19 extends AbstractGameLevel implements GameLevel {

	public Level19() {
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		//Left
		int boxHeight = 30;
		int rotation = 53;
		createBoxBody(world, fixtureDef, 381, 749, 547, boxHeight, rotation);
		createBox(world, fixtureDef, 334, 1112, 401, boxHeight, -rotation);

		//Right
		createBoxBody(world, fixtureDef, 699, 749, 548, boxHeight, -rotation);
		createBox(world, fixtureDef, 744, 1114, 408, boxHeight, rotation);

		//Draw /\
		Point[] top = new Point[6];
		top[0] = new Point(200, 960);//220, 960
		top[5] = new Point(240, 960);

		top[1] = new Point(540, 515);//540, 540
		top[4] = new Point(540, 565);

		top[2] = new Point(880, 960);//860, 960
		top[3] = new Point(840, 960);
		drawPath(top);

		//Left
		int smallBoxHeight = 10;
		createBoxBody(world, fixtureDef, 378, 1711, 548, smallBoxHeight, rotation);

		//Right
		createBoxBody(world, fixtureDef, 704, 1713, 543, smallBoxHeight, -rotation);

		//Draw /\
		Point[] bottom = new Point[6];
		bottom[0] = new Point(200, 1935);//220, 1935
		bottom[5] = new Point(240, 1935);

		bottom[1] = new Point(540, 1490);//540, 1515
		bottom[4] = new Point(540, 1545);

		bottom[2] = new Point(880, 1935);//860, 960
		bottom[3] = new Point(840, 1935);
		drawPath(bottom);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(855, 321);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(384, 960);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_level19.png";
	}
}
