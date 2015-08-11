package temple.core.utils.font;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by erikpoort on 22/07/14.
 * MediaMonks
 */
public class FontCache {
    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    public static Typeface get(Context context, String name) {
        Typeface typeface = fontCache.get(name);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), name);
            } catch (Exception e) {
                return null;
            }

            fontCache.put(name, typeface);
        }

        return typeface;
    }
}
