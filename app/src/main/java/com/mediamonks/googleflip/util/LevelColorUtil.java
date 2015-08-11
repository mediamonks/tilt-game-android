package com.mediamonks.googleflip.util;

import android.content.res.Resources;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.LevelColor;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for level color
 */
public class LevelColorUtil {
    private static Map<LevelColor, Integer> sColorMap;

    public static int fromLevelColor(LevelColor levelColor) {
        if (levelColor == null) {
            return 0;
        }

        return sColorMap.get(levelColor);
    }

    public static void initColorMap() {
        Resources resources = GoogleFlipGameApplication.sContext.getResources();

        sColorMap = new HashMap<>();
        sColorMap.put(LevelColor.BLUE, resources.getColor(R.color.blue));
        sColorMap.put(LevelColor.PINK, resources.getColor(R.color.pink));
        sColorMap.put(LevelColor.CYAN, resources.getColor(R.color.cyan));
        sColorMap.put(LevelColor.PURPLE, resources.getColor(R.color.purple));
        sColorMap.put(LevelColor.YELLOW, resources.getColor(R.color.yellow));
    }
}
