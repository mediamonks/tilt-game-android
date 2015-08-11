package temple.core.ui.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import temple.core.R;
import temple.core.common.interfaces.IChangeable;
import temple.core.common.interfaces.IHasValue;
import temple.core.ui.form.validation.IHasError;
import temple.core.utils.font.FontCache;
import temple.core.utils.font.FontFaceType;

/**
 * Created by erikpoort on 28/07/14.
 * MediaMonks
 */
public class CustomEditText extends EditText
        implements IFormElement, IHasValue, IChangeable, IHasError {
    private static final int[] STATE_INVALID = {R.attr.state_invalid};

    private boolean _hasError;
    private TextView _errorView;
    private IChangeable.OnChangeListener _changeListener;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(context, attrs);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {

        if (!isInEditMode()) {
            TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText);

            int ordinal = values.getInt(R.styleable.CustomEditText_typeface, 0);
            FontFaceType faceType = FontFaceType.values()[ordinal];

            Typeface typeface = FontCache.get(context, faceType.getAssetName());

            if (typeface != null) {
                setTypeface(typeface);
            }

            _hasError = values.getBoolean(R.styleable.CustomEditText_invalid, false);

            values.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        addTextChangedListener(_textWatcher);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        removeTextChangedListener(_textWatcher);
    }

    TextWatcher _textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (_changeListener != null) {
                _changeListener.onChange(CustomEditText.this);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);

        if (hasError()) {
            mergeDrawableStates(drawableState, STATE_INVALID);
        }

        return drawableState;
    }

    @Override
    public boolean hasError() {
        return _hasError;
    }

    @Override
    public void showError() {
        showError(null);
    }

    @Override
    public void showError(String message) {
        _hasError = true;

        if (!TextUtils.isEmpty(message) && _errorView != null) {
            _errorView.setText(message);
            _errorView.setVisibility(View.VISIBLE);
        }

        refreshDrawableState();
    }

    @Override
    public void hideError() {
        _hasError = false;

        if (_errorView != null) {
            _errorView.setText("");
            _errorView.setVisibility(View.GONE);
        }

        refreshDrawableState();
    }

    @Override
    public Object getValue() {
        return getText().toString();
    }

    @Override
    public void setValue(Object data) {
        setText((String) data);
    }

    public void setErrorView(TextView errorView) {
        _errorView = errorView;
    }

    @Override
    public void setOnChangeListener(OnChangeListener listener) {
        _changeListener = listener;
    }
}