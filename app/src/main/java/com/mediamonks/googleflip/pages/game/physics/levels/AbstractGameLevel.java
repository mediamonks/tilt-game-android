package com.mediamonks.googleflip.pages.game.physics.levels;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mediamonks.googleflip.BuildConfig;
import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;

import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;

import temple.core.utils.PaintUtils;

/**
 * Base class for game levels
 */
public abstract class AbstractGameLevel {
    private static final String TAG = AbstractGameLevel.class.getSimpleName();
    private static final float DEG2RAD = (float) (Math.PI / 180.0);

    private Bitmap _bitmap;
    private Canvas _bitmapCanvas;
    private Bitmap _tmpBitmap;
    private Canvas _tmpCanvas;
    private Paint _bitmapPaint;
    private Paint _erasePaint;
    private int _circleThickness = 30;

    protected int _width;
    protected int _height;
    protected boolean _isInitialized;
    protected float _density;
    protected float _scaledDensity;
    protected float _originalWidth = 1080;
    protected float _originalHeight = 1920;
    protected float _scale;

    public void init(int width, int height, float scale, float density) {
        _width = width;
        _height = height;
        _scale = scale;

        _density = density;
        _scaledDensity = _density / _scale;

        _bitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
        _bitmapCanvas = new Canvas(_bitmap);
        _bitmapPaint = PaintUtils.createFillPaint(GoogleFlipGameApplication.sContext.getResources().getColor(R.color.grey));

        _erasePaint = PaintUtils.createFillPaint(GoogleFlipGameApplication.sContext.getResources().getColor(R.color.red));
        _erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        _isInitialized = true;
    }

    protected Vector2 getScaledVector(int x, int y) {
        return Vector2Pool.obtain((float) x * _scale, _height - (float) y * _scale);
    }

    protected void assertInitialized() {
        if (BuildConfig.DEBUG && !_isInitialized) throw new AssertionError("Call init first");
    }

    /**
     * Utility methods
     */
    protected void createBox(PhysicsWorld world, FixtureDef fixtureDef, float x, float y, float width, float height) {
        createBox(world, fixtureDef, x, y, width, height, 0);
    }

    protected void createBox(PhysicsWorld world, FixtureDef fixtureDef, float x, float y, float width, float height, float rotation) {
        createBoxBody(world, fixtureDef, x, y, width, height, rotation);
        drawBox(x, y, width, height, rotation);
    }

    protected void createOpenCircle(PhysicsWorld world, FixtureDef fixtureDef, float startAngle, float endAngle,
                                    float radius, float centerX, float centerY, int segmentCount) {
        createOpenCircleBody(world, fixtureDef, startAngle, endAngle, radius, centerX, centerY, segmentCount);
        drawOpenCircle(startAngle, endAngle, radius, centerX, centerY);
    }

    protected void createOpenCircle(PhysicsWorld world, FixtureDef fixtureDef, float startAngle, float endAngle,
                                    float radius, float centerX, float centerY, int segmentCount, float startAngleOffset, float endAngleOffset) {
        createOpenCircleBody(world, fixtureDef, startAngle, endAngle, radius, centerX, centerY, segmentCount);
        drawOpenCircle(startAngle, endAngle, radius, centerX, centerY, startAngleOffset, endAngleOffset);
    }

    protected void createBoxBody(PhysicsWorld world, FixtureDef fixtureDef, float x, float y, float width, float height) {
        createBoxBody(world, fixtureDef, x, y, width, height, 0);
    }

    protected void createBoxBody(PhysicsWorld world, FixtureDef fixtureDef, float x, float y, float width, float height, float rotation) {
        PhysicsFactory.createBoxBody(world, x * _scale, _height - y * _scale, width * _scale, height * _scale, rotation,
                BodyDef.BodyType.StaticBody, fixtureDef);
    }

    protected void createOpenCircleBody(PhysicsWorld world, FixtureDef fixtureDef, float startAngle, float endAngle,
                                        float radius, float centerX, float centerY, int segmentCount) {
        createOpenCircleBody(world, fixtureDef, startAngle, endAngle, radius, radius - _circleThickness, centerX, centerY, segmentCount);//18
    }

    protected void createOpenCircleBody(PhysicsWorld world, FixtureDef fixtureDef, float startAngle, float endAngle,
                                        float radius, float innerRadius, float centerX, float centerY, int segmentCount) {
        float angleStep = (endAngle - startAngle) / (float) segmentCount;
        float curAngle = startAngle + angleStep / 2;
        float width = 3;
        float height = (radius - innerRadius);
        float x;
        float y;
        float angleRad;
        float radiusCorrection = (float) Math.cos(angleStep * DEG2RAD / 2);

        while (curAngle < endAngle) {
            float angleLeft = (curAngle - angleStep / 2) * DEG2RAD;
            float xLeft = centerX + radius * (float) Math.cos(angleLeft);
            float yLeft = centerY + radius * (float) Math.sin(angleLeft);

            float angleRight = (curAngle + angleStep / 2) * DEG2RAD;
            float xRight = centerX + radius * (float) Math.cos(angleRight);
            float yRight = centerY + radius * (float) Math.sin(angleRight);

            float dx = xRight - xLeft;
            float dy = yRight - yLeft;
            width = height / segmentCount + (float) (Math.sqrt(dx * dx + dy * dy));

            angleRad = curAngle * DEG2RAD;
            x = centerX + (radius * radiusCorrection) * (float) Math.cos(angleRad);
            y = centerY + (radius * radiusCorrection) * (float) Math.sin(angleRad);
            createBoxBody(world, fixtureDef, x, y, width, height, 90 - curAngle);

            curAngle += angleStep;
        }
    }

    protected void createWave(PhysicsWorld world, FixtureDef fixtureDef, float amplitude, float frequency,
                              float x, float y, float waveLength, float waveStart) {
        float steps = 100;
        for (int i = 0; i < steps; i++) {
            float angle = 2 * (float) Math.PI * frequency * (i + waveStart) / steps;
            float sin = (float) Math.sin(angle);

            createBox(world, fixtureDef, x + waveLength * (i / steps), y - amplitude * sin, 3, 40);
        }
    }

    /**
     * DRAW
     */
    protected void drawBox(float x, float y, float width, float height) {
        drawBox(x, y, width, height, 0);
    }

    protected void drawBox(float x, float y, float width, float height, float rotation) {
        float left = (x - (width * .5f)) * _scale;
        float top = (y - (height * .5f)) * _scale;
        float right = left + (width * _scale);
        float bottom = top + (height * _scale);

        _bitmapCanvas.save();
        _bitmapCanvas.rotate(-rotation, x * _scale, y * _scale);
        _bitmapCanvas.drawRect(left, top, right, bottom, _bitmapPaint);
        _bitmapCanvas.restore();
    }

    protected void drawOpenCircle(float startAngle, float endAngle, float radius, float centerX, float centerY) {
        drawOpenCircle(startAngle, endAngle, radius, radius - _circleThickness, centerX, centerY, 0, 0);//18
    }

    protected void drawOpenCircle(float startAngle, float endAngle, float radius, float centerX, float centerY,
                                  float startAngleOffset, float endAngleOffset) {
        drawOpenCircle(startAngle, endAngle, radius, radius - _circleThickness, centerX, centerY, startAngleOffset, endAngleOffset);//18
    }

    protected void drawOpenCircle(float startAngle, float endAngle, float radius, float innerRadius, float centerX, float centerY, float startAngleOffset,
                                  float endAngleOffset) {
        float height = (radius - innerRadius);
        radius += height * .5f;
        startAngle += startAngleOffset;
        endAngle += endAngleOffset;

        float left = (centerX - radius) * _scale;
        float top = (centerY - radius) * _scale;
        float right = (centerX + radius) * _scale;
        float bottom = (centerY + radius) * _scale;

        radius -= height * .5f;
        float thickness = (radius - innerRadius) * _scale;

        _tmpBitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
        _tmpCanvas = new Canvas(_tmpBitmap);
        _tmpCanvas.drawArc(new RectF(left, top, right, bottom), startAngle, endAngle - startAngle, true, _bitmapPaint);
        _tmpCanvas.drawOval(new RectF(left + thickness, top + thickness, right - thickness, bottom - thickness), _erasePaint);

        _bitmapCanvas.drawBitmap(_tmpBitmap, 0, 0, _bitmapPaint);
    }

    protected void drawPath(Point[] points) {
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        int total = points.length;
        for (int i = 0; i < total; i++) {
            Point point = points[i];
            if (i == 0) {
                path.moveTo(point.x * _scale, point.y * _scale);
            } else {
                path.lineTo(point.x * _scale, point.y * _scale);
            }
        }
        path.close();

        _bitmapCanvas.drawPath(path, _bitmapPaint);
    }

    /**
     * Default level duration
     */
    public float getLevelDuration() {
        return 25;
    }

    /**
     * Background bitmap
     *
     * @return Bitmap
     */
    public Bitmap getBackground() {
        return _bitmap;
    }

    public void dispose() {
        _bitmap = null;
        _tmpBitmap = null;

        _bitmapCanvas = null;
        _tmpCanvas = null;

        _bitmapPaint = null;
        _erasePaint = null;
    }
}
