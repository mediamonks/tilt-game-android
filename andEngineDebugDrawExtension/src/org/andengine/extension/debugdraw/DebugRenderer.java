package org.andengine.extension.debugdraw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.andengine.entity.Entity;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Shape.Type;



public class DebugRenderer extends Entity {
	private PhysicsWorld mWorld;
	private final VertexBufferObjectManager mVBO;

	// bodies to be rendered
	private HashMap<Body, RenderOfBody> mBodiesToBeRenderred = new HashMap<Body, RenderOfBody>();
	private Set<RenderOfBody> mBodiesInactiveSet = new HashSet<RenderOfBody>();
	private Set<RenderOfBody> mBodiesActiveSet = new HashSet<RenderOfBody>();

	// joints to be rendered
	private HashMap<Joint, IRenderOfJoint> mJointsToBeRenderred = new HashMap<Joint, IRenderOfJoint>();
	private Set<IRenderOfJoint> mJointsInactiveSet = new HashSet<IRenderOfJoint>();
	private Set<IRenderOfJoint> mJointsActiveSet = new HashSet<IRenderOfJoint>();

	private final float mJointMarkerSize;
	private boolean mDrawJoints = true;
	private boolean mDrawBodies = true;

	/**
	 * To construct the renderer physical world is needed (to access physics)
	 * and VBO (to construct visible representations)
	 * @param world
	 * @param pVBO
	 */
	public DebugRenderer(PhysicsWorld world, VertexBufferObjectManager pVBO) {
		this(world, pVBO, 5);
	}

	public DebugRenderer(PhysicsWorld world, VertexBufferObjectManager pVBO, float pJointMarkerSize) {
		super();
		this.mWorld = world;
		this.mVBO = pVBO;
		this.mJointMarkerSize = pJointMarkerSize;
	}

	/**
	 * This is where all the magic happens. Bodies representations are rendered.
	 * Dead bodies (not being part of physical world anymore) are removed from
	 * the rendering.
	 */
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		// *** BODIES

		if (isDrawBodies()) {
			mBodiesActiveSet.clear();
			mBodiesInactiveSet.clear();
			Iterator<Body> iterator = mWorld.getBodies();
			while (iterator.hasNext()) {
				Body body = iterator.next();
				RenderOfBody renderOfBody;
				if (!mBodiesToBeRenderred.containsKey(body)) {
					renderOfBody = new RenderOfBody(body, mVBO);
					mBodiesToBeRenderred.put(body, renderOfBody);
					this.attachChild(renderOfBody);
				} else {
					renderOfBody = mBodiesToBeRenderred.get(body);
				}

				mBodiesActiveSet.add(renderOfBody);

				/**
				 * This is where debug renders are moved to match body position.
				 * These 4 lines probably have to be modified if you are not using new
				 * GLES2-AnchorCenter branch of AE (i.e. you are using old GLES2 branch)
				 */
				renderOfBody.updateColor();
				renderOfBody
						.setRotationCenter(
								body.getMassData().center.x
										* PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
								body.getMassData().center.y
										* PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT);
				renderOfBody.setRotation((float) (360 - body.getAngle()
						* (180 / Math.PI)));
				renderOfBody
						.setPosition(
								body.getPosition().x
										* PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
								body.getPosition().y
										* PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT);
			}
			/**
			 * Get rid of all bodies that were not rendered in this iteration
			 */
			// inactive = renderred - active
			mBodiesInactiveSet.addAll(mBodiesToBeRenderred.values());
			mBodiesInactiveSet.removeAll(mBodiesActiveSet);
			for (RenderOfBody killme : mBodiesInactiveSet) {
				this.detachChild(killme);
			}
			mBodiesToBeRenderred.values().removeAll(mBodiesInactiveSet);
		}
		

		// *** JOINTS

		if (isDrawJoints()) {
			mJointsActiveSet.clear();
			mJointsInactiveSet.clear();
			Iterator<Joint> iteratorJoints = mWorld.getJoints();
			while (iteratorJoints.hasNext()) {
				Joint joint = iteratorJoints.next();
				IRenderOfJoint renderOfJoint;
				if (!mJointsToBeRenderred.containsKey(joint)) {
					renderOfJoint = new RenderOfJointPolyline(joint, mVBO,
							mJointMarkerSize);
					mJointsToBeRenderred.put(joint, renderOfJoint);
					this.attachChild(renderOfJoint.getEntity());
				} else {
					renderOfJoint = mJointsToBeRenderred.get(joint);
				}

				mJointsActiveSet.add(renderOfJoint);
				renderOfJoint.update();
				renderOfJoint.getEntity().setColor(
						jointToColor(renderOfJoint.getJoint()));
			}
			/**
			 * Get rid of all joints that were not rendered in this iteration
			 */
			// inactive = renderred - active
			mJointsInactiveSet.addAll(mJointsToBeRenderred.values());
			mJointsInactiveSet.removeAll(mJointsActiveSet);
			for (IRenderOfJoint killme : mJointsInactiveSet) {
				this.detachChild(killme.getEntity());
			}
			mJointsToBeRenderred.values().removeAll(mJointsInactiveSet);
		}
	}

	/**
	 * Translates b2d Fixture to appropriate color, depending on body state/type
	 * Modify to suit your needs
	 * @param fixture
	 * @return
	 */
	private static Color fixtureToColor(Fixture fixture) {
		if (fixture.isSensor()) {
			return Color.PINK;
		} else {
			Body body = fixture.getBody();
			if (!body.isActive()) {
				return Color.BLACK;
			} else {
				if (!body.isAwake()) {
					return Color.RED;
				} else {
					switch (body.getType()) {
					case StaticBody:
						return Color.CYAN;
					case KinematicBody:
						return Color.WHITE;
					case DynamicBody:
					default:
						return Color.GREEN;
					}
				}
			}
		}
	}

	/**
	 * Translates b2d Joint to appropriate color, depending on state/type
	 * Modify to suit your needs
	 * @param joint
	 * @return
	 */

	private static Color jointToColor(Joint joint) {
		switch (joint.getType()) {
		case RevoluteJoint:
		case PrismaticJoint:
		case DistanceJoint:
		case PulleyJoint:
		case MouseJoint:
		case GearJoint:
		case WeldJoint:
		case FrictionJoint:
			return Color.WHITE;

		case Unknown:
		default:
			return Color.WHITE;
		}
	}

	/**
	 * Physical body representation- it contains of multiple IRenderOfFixture
	 * @author nazgee
	 *
	 */
	private class RenderOfBody extends Entity {
		public LinkedList<IRenderOfFixture> mRenderFixtures = new LinkedList<IRenderOfFixture>();

		public RenderOfBody(Body pBody, VertexBufferObjectManager pVBO) {
			ArrayList<Fixture> fixtures = pBody.getFixtureList();

			/**
			 * Spawn all IRenderOfFixture for this body that are out there,
			 * and bind them to this RenderOfBody
			 */
			for (Fixture fixture : fixtures) {
				IRenderOfFixture renderOfFixture;
				if (fixture.getShape().getType() == Type.Circle) {
					renderOfFixture = new RenderOfCircleFixture(fixture, pVBO);
				} else {
					renderOfFixture = new RenderOfPolyFixture(fixture, pVBO);
				}

				updateColor();
				mRenderFixtures.add(renderOfFixture);
				this.attachChild(renderOfFixture.getEntity());
			}
		}

		public void updateColor() {
			for (IRenderOfFixture renderOfFix : mRenderFixtures) {
				renderOfFix.getEntity().setColor(fixtureToColor(renderOfFix.getFixture()));
			}
		}
	}

	public boolean isDrawJoints() {
		return mDrawJoints;
	}

	public void setDrawJoints(boolean mDrawJoints) {
		this.mDrawJoints = mDrawJoints;
	}

	public boolean isDrawBodies() {
		return mDrawBodies;
	}

	public void setDrawBodies(boolean mDrawBodies) {
		this.mDrawBodies = mDrawBodies;
	}
}
