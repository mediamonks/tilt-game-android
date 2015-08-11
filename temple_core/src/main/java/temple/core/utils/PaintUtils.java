package temple.core.utils;

import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by michielb on 3-12-2014.
 */
public class PaintUtils {

	public static Paint createTypePaint(Typeface typeface, float textSize, int color) {
		return createTypePaint(typeface, textSize, color, true, Paint.Align.CENTER);
	}

	public static Paint createTypePaint(Typeface typeface, float textSize, int color, boolean antialias, Paint.Align align) {
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setTypeface(typeface);
		paint.setTextSize(textSize);
		paint.setAntiAlias(antialias);
		paint.setTextAlign(align);
		paint.setHinting(Paint.HINTING_ON);
		paint.setDither(true);
		return paint;
	}

	public static Paint createFillPaint(int color) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(false);
		paint.setColor(color);
		paint.setDither(true);
		return paint;
	}

	public static Paint createBitmapPaint() {
		Paint paint = new Paint();
		paint.setDither(true);
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);
		return paint;
	}
}
