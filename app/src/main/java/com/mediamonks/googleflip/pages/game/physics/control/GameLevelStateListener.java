package com.mediamonks.googleflip.pages.game.physics.control;

/**
 * Interface for game level events listener
 */
public interface GameLevelStateListener {
    /**
     * Method called when the ball has left the level
     */
    void onBallOut();

    /**
     * Method called when the level has failed
     */
    void onLevelFailed();

    /**
     * Method called when the timer has run out
     */
    void onTimeOut();

    /**
     * Method called when the level has been completed successfully
     */
    void onLevelComplete();

    /**
     * Method called when the out animation has been completed
     */
    void onOutAnimationComplete();
}
