package com.mediamonks.googleflip.pages.game.physics.control;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.pages.game.physics.constants.ObjectName;
import com.mediamonks.googleflip.util.SoundManager;

/**
 * Level controller for game levels
 */
public class GameLevelController extends BaseLevelController {
    private static final String TAG = GameLevelController.class.getSimpleName();

    private static final float MIN_BALL_SOUND_SPEED = 5;
    private static final float MIN_IMPACT_SOUND_SPEED = 2;
    private static final float MAX_IMPACT_SOUND_SPEED = 35;

    @Override
    public void onUpdate(float pSecondsElapsed) {
    }

    @Override
    protected void checkCollision(Contact contact) {
        if (_runChecks) {
            checkContact(contact);
        }
    }

    private boolean checkContact(Contact contact) {
        // ignore not touching contacts
        if (!contact.isTouching()) return false;

        Fixture fixtureA = contact.getFixtureA();
        if (fixtureA == null) return false;

        Fixture fixtureB = contact.getFixtureB();
        if (fixtureB == null) return false;

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        String nameA = (String) bodyA.getUserData();
        String nameB = (String) bodyB.getUserData();

        if (nameA == null || nameB == null) {               // nameless bodies, check if sound needs to be played
            checkCollisionSound(contact, bodyA, bodyB);
            return false;
        } else if (nameA.equals(ObjectName.EDGE_SENSOR_NAME) || nameB.equals(ObjectName.EDGE_SENSOR_NAME)) {    // check edges
            Log.d(TAG, "onUpdate: OFF THE EDGE");

            setBallOut();

            return true;
        } else if (nameA.equals(ObjectName.SINKHOLE_NAME) || nameB.equals(ObjectName.SINKHOLE_NAME)) {  // check sinkhole
            Log.d(TAG, "onUpdate: SINKHOLE");

            setLevelComplete();

            return true;
        }

        return false;
    }

    private void checkCollisionSound(Contact contact, Body bodyA, Body bodyB) {
        WorldManifold manifold = contact.getWorldManifold();
        Vector2 contactPoint = manifold.getPoints()[0];
        Vector2 vel1 = bodyA.getLinearVelocityFromWorldPoint(contactPoint);
        Vector2 vel2 = bodyB.getLinearVelocityFromWorldPoint(contactPoint);
        Vector2 impactVelocity = vel1.sub(vel2);
        float impactLen = impactVelocity.len();
        if (impactLen > MIN_BALL_SOUND_SPEED) {
            float dot = Math.abs(impactVelocity.dot(manifold.getNormal()));
            if (dot > MIN_IMPACT_SOUND_SPEED) {
                float volume = (float) Math.min((dot - MIN_IMPACT_SOUND_SPEED) / MAX_IMPACT_SOUND_SPEED, 1.0);
                SoundManager.getInstance().play(R.raw.bounce_1, volume);
            }
        }
    }
}
