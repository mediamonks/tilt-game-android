package com.mediamonks.googleflip.util;

import android.content.Context;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

/**
 * Seek bar controller for float preference values
 */
public class FloatPrefSeekBarController implements SeekBar.OnSeekBarChangeListener {
    private Context _context;
    private SeekBar _seekBar;
    private TextView _label;
    private int _stringId;
    private String _prefsKey;
    private float _minValue;
    private float _maxValue;
    private float _defaultValue;

    /**
     *
     * @param context Context
     * @param seekBar SeekBar instance to control
     * @param label TextView to show values
     * @param stringId id of string to use for label
     * @param prefsKey  key in preferences to retrieve/store value
     */
    public FloatPrefSeekBarController(Context context, SeekBar seekBar, TextView label, int stringId, String prefsKey) {
        _context = context;
        _seekBar = seekBar;
        _label = label;
        _stringId = stringId;
        _prefsKey = prefsKey;

        _seekBar.setOnSeekBarChangeListener(this);
    }

    public void initValues(float minValue, float maxValue, float defaultValue) {
        _minValue = minValue;
        _maxValue = maxValue;
        _defaultValue = defaultValue;

        if (!Prefs.contains(_prefsKey)) {
            Prefs.putFloat(_prefsKey, defaultValue);
        }

        _seekBar.setProgress(getProgress());

        updateLabel();
    }

    private float getValue() {
        return MathUtil.getValueFromProgress(_seekBar.getProgress(), _minValue, _maxValue);
    }

    private int getProgress() {
        return MathUtil.getProgressFromValue(Prefs.getFloat(_prefsKey, _defaultValue), _minValue, _maxValue);
    }

    private void updateLabel() {
        _label.setText(_context.getString(_stringId, getValue()));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateLabel();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        updateLabel();
        Prefs.putFloat(_prefsKey, getValue());
    }
}
