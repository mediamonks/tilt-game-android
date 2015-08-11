package com.mediamonks.googleflip.ui.paging;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mediamonks.googleflip.R;

/**
 * View class for page indication
 */
public class PageIndicator extends View {
    private Paint _paintActive;
    private Paint _paintInactive;

    private int _numPages;
    private int _activePage;

    public PageIndicator(Context context) {
        super(context);

        setup();
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        setup();
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setup();
    }

    private void setup() {
        _paintActive = new Paint();
        _paintActive.setStyle(Paint.Style.FILL);
        _paintActive.setDither(true);
        _paintActive.setAntiAlias(true);
        _paintActive.setColor(getResources().getColor(R.color.white));

        _paintInactive = new Paint();
        _paintInactive.setStyle(Paint.Style.FILL);
        _paintActive.setAntiAlias(true);
        _paintInactive.setColor(getResources().getColor(R.color.white_30));

        if (isInEditMode()) {
            _activePage = 0;
            _numPages = 3;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int leni = _numPages;

        int sizeActive = (int) getResources().getDimension(R.dimen.page_indicator_active_size);
        int sizeInactive = (int) getResources().getDimension(R.dimen.page_indicator_inactive_size);
        int dotSpacing = (int) getResources().getDimension(R.dimen.page_indicator_spacing);

        int y = getHeight() / 2;
        int spacing = dotSpacing + sizeInactive;
        int centerX = (canvas.getWidth() - leni * sizeInactive - sizeActive) / 2;

        int x;

        for (int i = 0; i < leni; ++i) {
            x = centerX + spacing * i;

            canvas.drawCircle(x, y, sizeInactive / 2, _paintInactive);

            if (i == _activePage) {
                canvas.drawCircle(x, y, sizeActive / 2, _paintActive);
            }
        }
    }

    public void setNumPages(int numPages) {
        _numPages = numPages;

        postInvalidate();
    }

    public void setActivePage(int activePage) {
        _activePage = activePage;

        postInvalidate();
    }
}