package temple.core.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by michielb on 3-12-2014.
 */
public class BitmapUtils {

	public static Bitmap loadImage(int bitmapId, Resources resources) {
		return loadImage(bitmapId, resources, 3);
	}

	public static Bitmap loadImage(int bitmapId, Resources resources, int tries) {
		Bitmap bitmap;

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inDither = true;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			bitmap = BitmapFactory.decodeResource(resources, bitmapId, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();

			System.gc();

			if (--tries > 0) {
				return loadImage(bitmapId, resources, tries);
			} else {
				return null;
			}
		}

		return bitmap;
	}
}
