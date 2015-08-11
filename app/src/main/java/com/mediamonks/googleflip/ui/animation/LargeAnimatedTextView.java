package com.mediamonks.googleflip.ui.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * View class for large animated text
 */
public class LargeAnimatedTextView extends AbstractAnimatedTextview {
	private static final String TAG = LargeAnimatedTextView.class.getSimpleName();

	private long _duration;

	private SpannableString _newText;
	private Interpolator _interpolator;

	public LargeAnimatedTextView(Context context) {
		super(context);
	}

	public LargeAnimatedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LargeAnimatedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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
		TextChar[] letters = _newText.getSpans(0, _newText.length(), TextChar.class);

		for (TextChar letter : letters) {
			_newText.removeSpan(letter);
		}
		for (int i = 0; i < _newText.length(); i++) {
			_newText.setSpan(new TextChar(getTextSize()), i, i + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
			long deltaTime = Math.min(currentTime - _startTime - _delay, _duration * _newText.length());

			if(deltaTime >= 0) {
				TextChar[] letters = _newText.getSpans(0, _newText.length(), TextChar.class);
				final int length = letters.length;
				for (int i = 0; i < length; i++) {
					TextChar letter = letters[i];
					float delta = (float) Math.max(Math.min((deltaTime - (i * (_duration / (length * 2)))), _duration), 0);

					delta = _interpolator.getInterpolation(delta / (float) _duration);

					letter.setAlpha(delta > 0 ? 1 : 0);
					letter.setScale(delta);
					letter.setDelta(delta);
				}
			}

			if (deltaTime < Math.min(3000, _duration * _newText.length())) {
				ViewCompat.postInvalidateOnAnimation(this);
			} else {
				onShowComplete();
			}
		}
	}

	private class TextChar extends ReplacementSpan {
		private float _textSize;
		private float _alpha;
		private float _scale;
		private float _delta;
		private float _diffY;
		private float _endDiffY;

		public TextChar(float textSize) {
			_textSize = textSize;
			_scale = 1;
		}

		/**
		 * @param value alpha [0-1]
		 */
		public void setAlpha(float value) {
			_alpha = Math.max(Math.min(value, 1.0f), 0.0f);
		}

		public void setScale(float value) {
			value = value + 0.4f;
			_scale = Math.max(Math.min(value, 1.0f), 0.2f);
		}

		public void setDelta(float value) {
			_delta = value;

			if (_delta < 0.5) {
				_diffY = ((-150 * (_delta + 0.5f)) + 100);
				_endDiffY = _diffY;
			} else if (_delta > 0.7) {
				_endDiffY = -50;
				float perc = (_delta - 0.7f) / 0.3f;
				_diffY = _endDiffY + 50 * perc;
			}
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
			paint.setTextSize(_textSize * _scale);
//			Log.d(TAG, "CanvasHeight: " + canvas.getHeight());
			canvas.drawText(text, start, end, x, y+_diffY, paint);//
		}

		@Override
		public void updateMeasureState(TextPaint ds) {
			ds.setTextSize(_textSize);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setTextSize(_textSize);
		}
	}
}
