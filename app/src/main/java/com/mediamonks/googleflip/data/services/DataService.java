package com.mediamonks.googleflip.data.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mediamonks.googleflip.BuildConfig;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.vo.LevelResultVO;
import com.mediamonks.googleflip.data.vo.LevelVO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * IntentService extension for doing internet related work in the background
 */
public class DataService extends IntentService {
    private static final String TAG = DataService.class.getSimpleName();

    public static final String ACTION_LOAD_LEVELS = "action_load_levels";

    public static final String KEY_LEVELS = "key_levels";
    public static final String KEY_LEVEL_RESULTS = "key_level_results";

    public static void startService(Context context, Class<?> cls, String action) {
        startService(context, cls, action, new Bundle());
    }

    public static void startService(Context context, Class<?> cls, String action, Bundle extras) {
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        intent.putExtras(extras);

        context.startService(intent);
    }

    public static void loadLevels(Context context) {
        startService(context, DataService.class, ACTION_LOAD_LEVELS);
    }

    public DataService() {
        super(DataService.class.getCanonicalName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getAction()) {
            case ACTION_LOAD_LEVELS:
                loadLevels(intent);
                break;
        }
    }

    private void loadLevels(Intent intent) {
        XmlResourceParser parser = getResources().getXml(R.xml.levels);

        String levelPackage = "";
        String controllerPackage = "";
        List<LevelVO> newLevels = new ArrayList<>();

        try {
            parser.next();
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (parser.getName()) {
                        case "levels":
                            levelPackage = parser.getAttributeValue(null, "levelpackage");
                            controllerPackage = parser.getAttributeValue(null, "controllerpackage");
                            break;
                        case "level":
                            newLevels.add(LevelVO.createFromXML(parser, levelPackage, controllerPackage));
                            break;
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        List<LevelVO> allLevels = new ArrayList<>(newLevels);

        if (newLevels.size() > 0) {
            // retrieve current set of stored levels
            List<LevelVO> oldLevels = cupboard().withContext(this).query(LevelVO.URI, LevelVO.class).list();

            for (LevelVO oldLevelVO : oldLevels) {
                LevelVO newLevelVO = getLevelById(newLevels, oldLevelVO.id);
                if (newLevelVO != null) {
                    newLevelVO.unlocked = oldLevelVO.unlocked;
                }
            }

            cupboard().withContext(this).put(LevelVO.URI, LevelVO.class, newLevels);

            // determine if there are new levels
            boolean hasNewLevels = (oldLevels.size() == 0) || newLevels.removeAll(oldLevels);

            // insert empty LevelResultVO instances into database for new levels
            if (hasNewLevels) {
                List<LevelResultVO> newResults = new ArrayList<>();
                for (LevelVO levelVO : newLevels) {
                    newResults.add(new LevelResultVO(levelVO.id));
                }
                cupboard().withContext(this).put(LevelResultVO.URI, LevelResultVO.class, newResults);
            }
        }

        List<LevelResultVO> results = cupboard().withContext(this).query(LevelResultVO.URI, LevelResultVO.class).list();

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_LOAD_LEVELS)
                .putParcelableArrayListExtra(KEY_LEVELS, (ArrayList<? extends Parcelable>) allLevels)
                .putParcelableArrayListExtra(KEY_LEVEL_RESULTS, (ArrayList<? extends Parcelable>) results));
    }

    private LevelVO getLevelById (List<LevelVO> levels, Long id) {
        for (LevelVO levelVO : levels) {
            if (levelVO.id.equals(id)) {
                return levelVO;
            }
        }
        return null;
    }
}
