package org.andengine.extension.debugdraw;

import org.andengine.entity.Entity;

import com.badlogic.gdx.physics.box2d.Joint;

/**
 * Base implementation of joint and it's graphical representation bound together
 * @author nazgee
 */
abstract class RenderOfJoint implements IRenderOfJoint {
	protected final Joint mJoint;
	protected Entity mEntity;

	public RenderOfJoint(Joint fixture) {
		super();
		this.mJoint = fixture;
	}

	@Override
	public Joint getJoint() {
		return mJoint;
	}

	@Override
	public Entity getEntity() {
		return mEntity;
	}
}