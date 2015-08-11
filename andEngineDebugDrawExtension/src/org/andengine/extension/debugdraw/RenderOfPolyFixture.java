package org.andengine.extension.debugdraw;

import org.andengine.extension.debugdraw.primitives.PolyLine;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Polygonal fixture representation
 * @author nazgee
 */
class RenderOfPolyFixture extends RenderOfFixture {
	public RenderOfPolyFixture(Fixture fixture, VertexBufferObjectManager pVBO) {
		super(fixture);

		PolygonShape fixtureShape = (PolygonShape) fixture.getShape();
		int vSize = fixtureShape.getVertexCount();
		float[] xPoints = new float[vSize];
		float[] yPoints = new float[vSize];

		Vector2 vertex = Vector2Pool.obtain();
		for (int i = 0; i < fixtureShape.getVertexCount(); i++) {
			fixtureShape.getVertex(i, vertex);
			xPoints[i] = vertex.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
			yPoints[i] = vertex.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
		}
		Vector2Pool.recycle(vertex);

		mEntity = new PolyLine(0, 0, xPoints, yPoints, pVBO);
	}
}
