package temple.core.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import temple.core.R;
import temple.core.utils.font.FontCache;
import temple.core.utils.font.FontFaceType;


/**
 * Created by erikpoort on 22/07/14.
 * MediaMonks
 */
public class CustomTextView extends TextView {

    private static final String TAG = CustomTextView.class.getSimpleName();

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(context, attrs);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        // Typeface
        if (!isInEditMode()) {
            TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
            int ordinal = values.getInt(R.styleable.CustomTextView_typeface, 0);
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
        return "CustomTextView{" +
                "text=" + getText() +
                ", id=" + getId() +
                '}';
    }
}