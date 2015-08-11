package com.mediamonks.googleflip.pages.game.physics.levels;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * First tutorial level
 */
public class TutorialLevel1 extends AbstractGameLevel implements GameLevel {
	public TutorialLevel1() {
		_originalWidth = 1080;
		_originalHeight = 1920;
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {

		createBox(world, fixtureDef, _originalWidth / 2, 14, _originalWidth, 28);
		createBox(world, fixtureDef, _originalWidth / 2, _originalHeight - (14 + (90 * _scaledDensity)), _originalWidth, 28);
		createBox(world, fixtureDef, 14, _originalHeight / 2, 28, _originalHeight);
		createBox(world, fixtureDef, _originalWidth - 14, _originalHeight / 2, 28, _originalHeight);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(178, 273);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(887, 1405 - (int) (100 * Math.max(0, _scaledDensity - 3)));
	}

	@Override
	public String getBackgroundUrl() {
		return "background_tutorial_1.png";
	}

	@Override
	public float getLevelDuration() {
		return 0;
	}
}
