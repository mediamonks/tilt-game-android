package org.andengine.extension.debugdraw;

import org.andengine.entity.Entity;

import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Binds fixture and it's graphical representation together
 * @author nazgee
 */
interface IRenderOfFixture {
	public Fixture getFixture();
	public Entity getEntity();
}