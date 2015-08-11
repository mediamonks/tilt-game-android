package org.andengine.extension.debugdraw;

import org.andengine.extension.debugdraw.primitives.Ellipse;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Circular fixture representation
 * @author nazgee
 */
class RenderOfCircleFixture extends RenderOfFixture {
	public RenderOfCircleFixture(Fixture fixture, VertexBufferObjectManager pVBO) {
		super(fixture);

		CircleShape fixtureShape = (CircleShape) fixture.getShape();
		Vector2 position = fixtureShape.getPosition();
		float radius = fixtureShape.getRadius() * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

		mEntity = new Ellipse(position.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
				position.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
				radius, radius, pVBO);
	}
}