package com.mediamonks.googleflip.ui.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

/**
 * View class for small animated text
 */
public class SmallAnimatedTextView extends AbstractAnimatedTextview {
	private SpannableString _newText;
	private int _duration;
	private DecelerateInterpolator _interpolator;

	public SmallAnimatedTextView(Context context) {
		super(context);
	}

	public SmallAnimatedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SmallAnimatedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void initView() {
		_duration = 500;
		_interpolator = new DecelerateInterpolator();
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		_newText = new SpannableString(text);
		WordSpan[] spans = _newText.getSpans(0, text.length(), WordSpan.class);

		for (WordSpan span : spans) {
			_newText.removeSpan(span);
		}

		String[] words = text.toString().split(" ");
		int charIndex = 0;
		for (int i = 0; i < words.length; i++) {
			int endWordIndex = charIndex + words[i].length();
			_newText.setSpan(new WordSpan(getTextSize()), charIndex, endWordIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			charIndex = endWordIndex + 1;
		}

		super.setText(_newText, BufferType.SPANNABLE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (_isAnimating) {
			if (_startTime == 0) {
				_startTime = AnimationUtils.currentAnimationTimeMillis();
			}

			//Animate letters here
			long currentTime = AnimationUtils.currentAnimationTimeMillis();
			long deltaTime = currentTime - _startTime - _delay;

			if(deltaTime >= 0) {
				WordSpan[] spans = _newText.getSpans(0, _newText.length(), WordSpan.class);
				final int length = spans.length;
				for (int i = 0; i < length; i++) {
					WordSpan wordSpan = spans[i];
					float delta = (float) Math.max(Math.min((deltaTime - (i * (_duration / (length * 2)))), _duration), 0);
					delta = _interpolator.getInterpolation(delta / (float) _duration);

					wordSpan.setDelta(delta);
				}
			}

			if (deltaTime < (_duration * _newText.length())) {
				ViewCompat.postInvalidateOnAnimation(this);
			} else {
				onShowComplete();
			}
		}
	}



	private class WordSpan extends ReplacementSpan {
		private final String TAG = WordSpan.class.getSimpleName();
		private float _size;
		private float _alpha;
		private float _diffY;

		public WordSpan(float size) {
			_size = size;
		}

		public void setDelta(float value) {
			_alpha = Math.max(Math.min(value, 1.0f), 0.0f);
			_diffY = -100 + value * 100;
		}

		@Override
		public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
			int size = (int) paint.measureText(text, start, end);
//			Log.d(TAG, "text: " + text + ", letter: " + text.charAt(start) + "size: " + size);
			return size;
		}

		@Override
		public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
			paint.setAlpha((int) (_alpha * 255));
//			Log.d(TAG, "alpha: " + _alpha);
			canvas.drawText(text, start, end, x, y + _diffY, paint);//
		}
	}
}
