package org.andengine.extension.debugdraw;

import org.andengine.entity.Entity;

import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Base implementation of fixture and it's graphical representation bound together
 * @author nazgee
 */
abstract class RenderOfFixture implements IRenderOfFixture {
	protected final Fixture mFixture;
	protected Entity mEntity;

	public RenderOfFixture(Fixture fixture) {
		super();
		this.mFixture = fixture;
	}

	@Override
	public Fixture getFixture() {
		return mFixture;
	}

	@Override
	public Entity getEntity() {
		return mEntity;
	}
}