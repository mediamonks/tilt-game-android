package org.andengine.extension.debugdraw;

import org.andengine.entity.Entity;

import com.badlogic.gdx.physics.box2d.Joint;

/**
 * Binds joint and it's graphical representation together
 * @author nazgee
 */
interface IRenderOfJoint {
	public Joint getJoint();
	public Entity getEntity();
	public void update();
}