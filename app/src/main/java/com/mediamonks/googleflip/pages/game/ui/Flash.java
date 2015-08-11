package com.mediamonks.googleflip.pages.game.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;

import com.mediamonks.googleflip.ui.animation.AnimationCallback;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Overlay to flash screen
 */
public class Flash extends Rectangle {
    private ObjectAnimator _animation;
    private AnimationCallback _callback;
    private int _counter = 0;
    private int _max;

    public Flash(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);

        _animation = ObjectAnimator.ofFloat(this, "alpha", 1);
        _animation.setDuration(150);
        _animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                _counter++;
                if (_counter < _max) {
                    _animation.setStartDelay(50);
                    _animation.start();
                } else {
                    setAlpha(0);
                    _counter = 0;
                    if (_callback != null) {
                        _callback.onComplete();
                    }
                }
            }
        });
    }

    @Override
    public void onAttached() {
        super.onAttached();

        setAlpha(0);
    }

    public void play(int count, AnimationCallback callback) {
        if (mDisposed) return;

        _max = count;

        _animation.setStartDelay(0);
        _animation.start();
        _callback = callback;
    }

    @Override
    public void dispose() {
        if (mDisposed) return;

        _animation.removeAllListeners();
        _callback = null;
        if (_animation.isRunning()) _animation.cancel();
        _animation = null;

        super.dispose();
    }
}
