package temple.core.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import temple.core.R;
import temple.core.utils.font.FontCache;
import temple.core.utils.font.FontFaceType;

/**
 * Created by Roland on 5/14/2015.
 */
public class CustomButton extends Button {
	private static final String TAG = CustomButton.class.getSimpleName();

	private int _baseBackgroundColor;

	public CustomButton(Context context) {
		super(context);
	}

	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		parseAttributes(context, attrs);
	}

	public CustomButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		parseAttributes(context, attrs);
	}

	private void parseAttributes(Context context, AttributeSet attrs) {
		ColorDrawable drawable = (ColorDrawable) getBackground();
		_baseBackgroundColor = drawable.getColor();

		// Typeface
		if (!isInEditMode()) {
			TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);
			int ordinal = values.getInt(R.styleable.CustomButton_typeface, 0);
			FontFaceType type = FontFaceType.values()[ordinal];

			Typeface typeface = FontCache.get(context, type.getAssetName());

			if (typeface != null) {
				setTypeface(typeface);
			}

			values.recycle();
		}
	}

	@Override
	public String toString() {
		return "CustomButton{" +
				"text=" + getText() +
				", id=" + getId() +
				'}';
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		if(_baseBackgroundColor != 0) {
			if(!enabled) {
				setBackgroundColor(0xFFD3D3D3);
			} else {
				setBackgroundColor(_baseBackgroundColor);
			}
		}
	}
}
