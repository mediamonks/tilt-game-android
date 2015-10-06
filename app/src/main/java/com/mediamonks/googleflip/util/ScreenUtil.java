package com.mediamonks.googleflip.util;

import android.os.Build;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;

/**
 * Screen related utilities
 */
public class ScreenUtil {
	private static final String TAG = ScreenUtil.class.getSimpleName();

	public static void setFullScreen(final View decorView) {
		int options = decorView.getSystemUiVisibility();

        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (hasBackKey && hasHomeKey) {
            return;
        }

		// Navigation bar hiding:  Backwards compatible to ICS.
		if (Build.VERSION.SDK_INT >= 14) {
			options |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			options |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
			options |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}

		// Status bar hiding: Backwards compatible to Jellybean
		if (Build.VERSION.SDK_INT >= 16) {
			options |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			options |= View.SYSTEM_UI_FLAG_FULLSCREEN;
		}

		if (Build.VERSION.SDK_INT >= 18) {
			options |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}

		decorView.setSystemUiVisibility(options);
	}
}
