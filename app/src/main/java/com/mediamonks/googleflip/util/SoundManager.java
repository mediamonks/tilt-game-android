package com.mediamonks.googleflip.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.HashMap;

public class SoundManager implements SoundPool.OnLoadCompleteListener {
    /**
     * SoundPool left volume
     */
    private static final float LEFT_VOLUME = 1.0f;
    /**
     * SoundPool right volume
     */
    private static final float RIGHT_VOLUME = 1.0f;
    /**
     * All sounds will have equal priority
     */
    private static final int STREAM_PRIORITY = 1;
    /**
     * Potential LOOP_MODE
     */
    private static final int MODE_NO_LOOP = 0;
    /**
     * Potential LOOP_MODE
     */
    @SuppressWarnings("unused")
    private static final int MODE_LOOP_FOREVER = -1;
    /**
     * Whether sounds should loop
     */
    private static final int LOOP_MODE = MODE_NO_LOOP;
    /**
     * SoundPool playback rate
     */
    private static final float PLAYBACK_RATE = 1.0f;
    private static final String TAG = SoundManager.class.getSimpleName();

    /**
     * Inner SoundManager instance
     */
    private static SoundManager _instance = null;
    private static boolean sIsMuted;

    /**
     * Mapping of resource ids to sound ids returned by load()
     */
    private HashMap<Integer, Integer> _soundMap = new HashMap<>();

    private HashMap<Integer, Integer> _streamIdMap;

    /**
     * Application Context
     */
    private static Context sContext;

    /**
     * Maximum concurrent streams that can play
     */
    private static final int MAX_STREAMS = 1;

    /**
     * SoundPool instance
     */
    private SoundPool _soundPool;

    /**
     * Private constructor for singleton
     */
    private SoundManager() {
        _soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        _soundPool.setOnLoadCompleteListener(this);

        _streamIdMap = new HashMap<>();
    }

    /**
     * Static access to internal instance
     */
    public static SoundManager getInstance() {
        if (sContext == null) {
            throw new IllegalArgumentException("Impossible to get the instance. This class must be initialized before");
        }

        if (_instance == null) {
            _instance = new SoundManager();
        }
        return _instance;
    }

    /**
     * To initialize the class. It must be called before call the method getInstance()
     *
     * @param context
     */
    public static void initialize(Context context) {
        sContext = context;

        sIsMuted = Prefs.getBoolean(PrefKeys.MUTED, false);
    }

    /**
     * Loads a sound. Called automatically by play() if not already loaded
     */
    public void load(int id) {
        if (!isSoundLoaded(id)) {
            _soundMap.put(id, _soundPool.load(sContext, id, 1));
        }
    }

    /**
     * Test if sound is loaded, call with id from R.raw
     *
     * @param resourceId
     * @return true|false
     */
    public boolean isSoundLoaded(int resourceId) {
        return _soundMap.containsKey(resourceId);
    }

    /**
     * Unload sound, prints warning if sound is not loaded
     */
    public void unload(int id) {
        if (_soundMap.containsKey(id)) {
            int soundId = _soundMap.remove(id);
            _soundPool.unload(soundId);
        } else {
            Log.w(TAG, "sound: " + id + " is not loaded!");
        }
    }

    public void play(int resourceId, float volume) {
        if (sIsMuted) return;

        if (!isSoundLoaded(resourceId)) {
            throw new Error("Load sound before playing it");
        }

        int streamId = _soundPool.play(_soundMap.get(resourceId), LEFT_VOLUME * volume, RIGHT_VOLUME * volume, STREAM_PRIORITY, LOOP_MODE, PLAYBACK_RATE);
        _streamIdMap.put(_soundMap.get(resourceId), streamId);
    }

    public void play(int resourceId) {
        if (sIsMuted) return;

        if (isSoundLoaded(resourceId)) {
            int streamId = _soundPool.play(_soundMap.get(resourceId), LEFT_VOLUME, RIGHT_VOLUME, STREAM_PRIORITY, LOOP_MODE, PLAYBACK_RATE);
            _streamIdMap.put(_soundMap.get(resourceId), streamId);
        } else {
            load(resourceId);
        }
    }

    public void stop(int resourceId) {
        if (_soundMap.containsKey(resourceId)) {
            int soundId = _soundMap.get(resourceId);
            if (_streamIdMap.containsKey(soundId)) {
                _soundPool.stop(_streamIdMap.get(soundId));
            }
        }
    }



    /**
     * If the sound is being loaded for the first time, we should wait until it
     * is completely loaded to play it.
     */
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
/*
        int streamId = _soundPool.play(sampleId, LEFT_VOLUME, RIGHT_VOLUME, STREAM_PRIORITY, LOOP_MODE, PLAYBACK_RATE);

        _streamIdMap.put(sampleId, streamId);
*/
    }

    public static void toggleMute () {
        sIsMuted = !sIsMuted;

        Prefs.putBoolean(PrefKeys.MUTED, sIsMuted);
    }

    public static boolean isMuted () {
        return sIsMuted;
    }
}