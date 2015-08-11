package com.mediamonks.googleflip.pages.game.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.support.annotation.Nullable;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.ui.animation.AnimationCallback;
import com.mediamonks.googleflip.util.SoundManager;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Ball view
 */
public class Ball extends Sprite {
	private static final String TAG = Ball.class.getSimpleName();

	private ObjectAnimator _inAnimator;
	private ObjectAnimator _outAnimator;
	private AnimationCallback _inAnimationCallback;

	public Ball(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);

		_inAnimator = ObjectAnimator.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat("scaleX", 1.0f), PropertyValuesHolder.ofFloat("scaleY", 1.0f));
		_inAnimator.setInterpolator(new DecelerateInterpolator());
		_inAnimator.setDuration(500);
		_inAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				SoundManager.getInstance().play(R.raw.ball_appear);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (_inAnimationCallback != null) {
					_inAnimationCallback.onComplete();
				}
			}
		});

		_outAnimator = ObjectAnimator.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat("scaleX", 0f), PropertyValuesHolder.ofFloat("scaleY", 0f));
		_outAnimator.setInterpolator(new AccelerateInterpolator());
		_outAnimator.setDuration(250);

		setScale(0);
	}

	public void show () {
		show(0, null);
	}
	public void show (long delay) {
		show(delay, null);
	}

	public void show(long delay, final AnimationCallback callback) {
		if (mDisposed) return;

		_inAnimationCallback = callback;

		_inAnimator.setStartDelay(delay);
		_inAnimator.start();
	}

	public void hide(long delay){
		_outAnimator.setStartDelay(delay);
		_outAnimator.start();
	}

	@Override
	public void dispose() {
		if (mDisposed) return;

		_inAnimator.removeAllListeners();
		_inAnimationCallback = null;
		if (_inAnimator.isRunning()) _inAnimator.cancel();
		_inAnimator = null;

		_outAnimator.removeAllListeners();
		if (_outAnimator.isRunning()) _outAnimator.cancel();
		_outAnimator = null;

		super.dispose();
	}
}
