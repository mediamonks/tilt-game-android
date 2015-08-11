package com.mediamonks.googleflip.ui.animation;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;

import temple.core.ui.CustomTextView;

/**
 * Base class for text animation
 */
public abstract class AbstractAnimatedTextview extends CustomTextView {
	protected boolean _isAnimating;
	protected long _startTime;

	protected AnimationCallback _animationCallback;
	protected int _delay = 0;

	public AbstractAnimatedTextview(Context context) {
		super(context);
		initView();
	}

	public AbstractAnimatedTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public AbstractAnimatedTextview(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	protected void initView() {

	}

	public void show(int delay) {
		_delay = delay;
		show();
	}

	public void show(AnimationCallback onComplete) {
		_animationCallback = onComplete;
		show();
	}

	public void show(int delay, AnimationCallback onComplete) {
		_delay = delay;
		_animationCallback = onComplete;
		show();
	}

	public void show() {
		_isAnimating = true;
		ViewCompat.postInvalidateOnAnimation(this);
	}

	protected void onShowComplete(){
		_isAnimating = false;
		_startTime = 0;
		if(_animationCallback != null) {
			_animationCallback.onComplete();

			_animationCallback = null;
		}
	}
}
