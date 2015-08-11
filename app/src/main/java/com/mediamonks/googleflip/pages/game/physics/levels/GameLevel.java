package com.mediamonks.googleflip.pages.game.physics.levels;

import android.graphics.Bitmap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

/**
 * Interface for game level physics & UI classes
 */
public interface GameLevel {
	/**
	 * Initialize the level
	 * @param width level width
	 * @param height level height
	 * @param scale level scale
	 * @param density level density
	 */
	void init(int width, int height, float scale, float density);

	/**
	 * Create the physics & UI
	 * @param world the physics world to create the physical objects in
	 * @param fixtureDef fixture definition for walls
	 */
	void createLevel(PhysicsWorld world, FixtureDef fixtureDef);

	/**
	 * @return the position of the ball spawn location
	 */
	Vector2 getBallSpawnLocation();

	/**
	 * @return the position of the sink hole
	 */
	Vector2 getSinkholeLocation();

	/**
	 * the url of the background image
	 */
	String getBackgroundUrl();

	/**
	 * @return the background bitmap
	 */
	Bitmap getBackground();

	/**
	 * @return the duration of the level in seconds
	 */
	float getLevelDuration();

	void dispose();
}
