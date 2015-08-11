package com.mediamonks.googleflip.pages.game.physics.levels;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * Third tutorial level
 */
public class TutorialLevel3 extends AbstractGameLevel implements GameLevel {
	public TutorialLevel3() {
		_originalWidth = 1080;
		_originalHeight = 1920;
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		createBox(world, fixtureDef, _originalWidth / 2, 115, _originalWidth, 28);
		createBox(world, fixtureDef, _originalWidth / 2, _originalHeight - (14 + (90 * _scaledDensity)), _originalWidth, 28);
		createBox(world, fixtureDef, 410, 701, 820, 28);
		createBox(world, fixtureDef, 937, 902, 290, 28);
		createBox(world, fixtureDef, 688, 1102, 268, 28);
		createBox(world, fixtureDef, 542, 1008, 28, 588);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		return getScaledVector(177, 275);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return getScaledVector(390, 850);
	}

	@Override
	public String getBackgroundUrl() {
		return "background_tutorial_3.png";
	}

	@Override
	public float getLevelDuration() {
		return 30;
	}
}
