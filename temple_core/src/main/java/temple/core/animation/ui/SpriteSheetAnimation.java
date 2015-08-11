package temple.core.animation.ui;

import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import temple.core.animation.data.vo.FrameVO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by erikpoort on 26/11/14.
 * MediaMonks
 */
public class SpriteSheetAnimation extends CoreAnimation {
	private ArrayList<FrameVO> _frames;

	private Bitmap _animation;
	private boolean _animating = false;
	private float _spriteSize;
	private String _animationId;
	private int _width;
	private int _height;
	private float _scale;
	private float _anchorX;
	private float _anchorY;

	public SpriteSheetAnimation() {
		_spriteSize = 320.0f;
	}

	private ArrayList<FrameVO> parseXML(XmlResourceParser parser) {
		ArrayList<FrameVO> frames = new ArrayList<>();

		try {
			parser.next();
			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("TextureAtlas")) {
					} else if (parser.getName().equals("SubTexture")) {
						frames.add(new FrameVO(parser));
					}
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException | IOException e) {
			e.printStackTrace();
		}

		return frames;
	}

	public void setSpriteSheet(String animationId, Bitmap spritesheet, XmlResourceParser xml) {
		if (!animationId.equals(_animationId) || _animation == null) {
			_animationId = animationId;

			_animation = spritesheet;
			_index = 0;

			_frames = parseXML(xml);
		}
	}

	public void startAnimation() {
		_index = 0;
		_animating = true;
	}

	@Override
	public boolean isAnimating() {
		return _animating;
	}

	public int getEndFrame() {
		return _frames.size() - 1;
	}

	@Override
	protected void checkListeners() {
		super.checkListeners();

		if (_index == _frames.size()) {
			_animating = false;
		}
	}

	public void clear() {
		_animating = false;
		_animation = null;
	}

	public void draw(Canvas canvas) {
		if (_animating) {
			drawFrame(canvas, _index++);
			checkListeners();
		}
	}

	public void drawFrame(Canvas canvas, int frameIdx) {

		if (_width == 0 || _height == 0) {
			_width = canvas.getWidth();
			_height = canvas.getHeight();

			if (_width > _height) {
				_scale = (float) _width / _spriteSize;
			} else {
				_scale = (float) _height / _spriteSize;
			}

			float toWidth = _spriteSize * _scale;
			float toHeight = _spriteSize * _scale;

			_anchorX = (toWidth - _width) / 2.0f;
			_anchorY = (toHeight - _height) / 2.0f;
		}

		canvas.save();
		canvas.scale(_scale, _scale, _anchorX, _anchorY);

		frameIdx = Math.max(0, Math.min(frameIdx, getEndFrame()));
		FrameVO frame = _frames.get(frameIdx);
		if (frame.isRotated()) {
			canvas.save();
			canvas.rotate(-90, frame.getFrame().left, frame.getFrame().top);
			canvas.translate(-frame.getBounds().width(), 0);
		}

		canvas.drawBitmap(_animation, frame.getBounds(), frame.getFrame(), _paint);

		if (frame.isRotated()) {
			canvas.restore();
		}

		canvas.restore();
	}
}