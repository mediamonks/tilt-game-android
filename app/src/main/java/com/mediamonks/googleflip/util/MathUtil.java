package com.mediamonks.googleflip.util;

/**
 * Math utilities
 */
public class MathUtil {
    public static float getValueFromProgress(int progress, float min, float max) {
        return (float) (min + (max - min) * progress / 100.0);
    }

    public static int getProgressFromValue(float value, float min, float max) {
        return (int) (100.0 * (value - min) / (max - min));
    }
}
