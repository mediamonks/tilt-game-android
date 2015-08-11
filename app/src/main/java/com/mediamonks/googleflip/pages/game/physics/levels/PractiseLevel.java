package com.mediamonks.googleflip.pages.game.physics.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mediamonks.googleflip.pages.game.physics.constants.ObjectName;
import com.mediamonks.googleflip.pages.game.physics.control.WorldController;

import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * Game level to practise game play
 */
public class PractiseLevel extends AbstractGameLevel implements GameLevel {

	private static final String TAG = PractiseLevel.class.getSimpleName();
	private Body _centerSensor;

	public PractiseLevel() {
		_originalWidth = 156;
		_originalHeight = 277;
	}

	@Override
	public void createLevel(PhysicsWorld world, FixtureDef fixtureDef) {
		createBox(world, fixtureDef, _originalWidth / 2, _originalHeight, _originalWidth, 5);
		createBox(world, fixtureDef, _originalWidth / 2, 0, _originalWidth, 5);
		createBox(world, fixtureDef, 0, _originalHeight / 2, 5, _originalHeight);
		createBox(world, fixtureDef, _originalWidth, _originalHeight / 2, 5, _originalHeight);

		_centerSensor = PhysicsFactory.createCircleBody(world, _scale * _originalWidth / 2, _scale * _originalHeight / 2, .1f * _width,
				BodyDef.BodyType.StaticBody, WorldController.SENSOR_FIX_DEF);
		_centerSensor.setUserData(ObjectName.CENTER_SENSOR_NAME);
	}

	@Override
	public Vector2 getBallSpawnLocation() {
		assertInitialized();

		return getScaledVector(78, 137);
	}

	@Override
	public Vector2 getSinkholeLocation() {
		return null;
	}

	public Body getCenterSensor() {
		return _centerSensor;
	}

	@Override
	public String getBackgroundUrl() {
		return null;
	}

	@Override
	public float getLevelDuration() {
		return 0.0f;
	}
}
