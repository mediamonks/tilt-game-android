package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * Second tutorial level
 */
public class TutorialLevel2 extends AbstractGameLevel implements GameLevel {
	public TutorialLevel2() {
		_originalWidth = 1080;
		_originalHeight = 1920;
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		createBox(world, fixtureDef, _originalWidth / 2, 14, _originalWidth, 28);
		createBox(world, fixtureDef, _originalWidth / 2, _originalHeight - (14 + (90 * _scaledDensity)), _originalWidth, 28);
		createBox(world, fixtureDef, _originalWidth / 2, 873, 542, 30);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector((int) (_originalWidth / 2), 294);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(887, 1405 - (int) (100 * Math.max(0, _scaledDensity - 3)));
	}

	@Override
	public String getBackgroundUrl() {
		return "background_tutorial_2.png";
	}

	@Override
	public float getLevelDuration() {
		return 0;
	}
}
