package temple.core.animation.data.vo;

import android.content.res.XmlResourceParser;
import android.graphics.Rect;

/**
 * Created by erikpoort on 28/11/14.
 * MediaMonks
 */
public class FrameVO {
	private final Rect _bounds;
	private final Rect _frame;
	private final boolean _rotated;

	public FrameVO(XmlResourceParser parser) {
		int x, y, width, height;

		x = parser.getAttributeIntValue(null, "x", 0);
		y = parser.getAttributeIntValue(null, "y", 0);
		width = parser.getAttributeIntValue(null, "width", 0);
		height = parser.getAttributeIntValue(null, "height", 0);
		_bounds = new Rect(x, y, x + width, y + height);

		x = -parser.getAttributeIntValue(null, "frameX", 0);
		y = -parser.getAttributeIntValue(null, "frameY", 0);
		_frame = new Rect(x, y, x + width, y + height);

		_rotated = parser.getAttributeBooleanValue(null, "rotated", false);
	}

	public Rect getBounds() {
		return _bounds;
	}

	public Rect getFrame() {
		return _frame;
	}

	public boolean isRotated() {
		return _rotated;
	}
}