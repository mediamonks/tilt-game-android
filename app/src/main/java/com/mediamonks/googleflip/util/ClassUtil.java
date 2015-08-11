package com.mediamonks.googleflip.util;

import android.util.Log;

/**
 * class related utilities
 */
public class ClassUtil {
    private static final String TAG = ClassUtil.class.getSimpleName();

    public static Object createInstance(String className) {
        Object object = null;
        try {
            Class cls = getClassForName(className);
            object = (cls == null) ? null : cls.newInstance();
        } catch (InstantiationException e) {
            Log.e(TAG, "onCreate: couldn't instantiate class for " + className);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "onCreate: no access to class for " + className);
        }

        return object;
    }

    public static Class getClassForName (String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "getClassForName: couldn't find class for " + className);
            return null;
        }
    }
}
