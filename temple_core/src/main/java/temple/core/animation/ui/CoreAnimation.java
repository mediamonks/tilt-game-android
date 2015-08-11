package temple.core.animation.ui;

import android.graphics.Paint;
import android.util.SparseArray;

/**
 * Created by erikpoort on 28/11/14.
 * MediaMonks
 */
public abstract class CoreAnimation {
	protected final Paint _paint;
	protected int _index = 0;

	private SparseArray<OnFrameOccurred> _frameListeners;

	public CoreAnimation() {
		_paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		_frameListeners = new SparseArray<>();
	}

	public void setAntiAlias(boolean antiAlias) {
		_paint.setAntiAlias(antiAlias);
	}

	public void setFrameListenerOnce(int frame, OnFrameOccurred onFrameOccurred) {
		_frameListeners.put(frame, onFrameOccurred);
	}

	public void clearListeners() {
		_frameListeners.clear();
	}

	protected void checkListeners() {
		OnFrameOccurred listener = _frameListeners.get(_index, null);

		if (listener != null) {
			listener.onFrameOccurred();

			_frameListeners.remove(_index);
		}
	}

	public abstract boolean isAnimating();

	public interface OnFrameOccurred {
		void onFrameOccurred();
	}
}