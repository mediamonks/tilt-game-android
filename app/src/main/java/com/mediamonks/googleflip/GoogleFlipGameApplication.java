package com.mediamonks.googleflip;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.mediamonks.googleflip.data.models.UserModel;
import com.mediamonks.googleflip.data.services.DataService;
import com.mediamonks.googleflip.data.vo.LevelResultVO;
import com.mediamonks.googleflip.data.vo.LevelVO;
import com.mediamonks.googleflip.pages.game.management.GameClient;
import com.mediamonks.googleflip.pages.game.management.GameClientImpl;
import com.mediamonks.googleflip.pages.game.management.GameServer;
import com.mediamonks.googleflip.pages.game.management.GameServerImpl;
import com.mediamonks.googleflip.util.LevelColorUtil;
import com.mediamonks.googleflip.util.SoundManager;
import com.pixplicity.easyprefs.library.Prefs;

import org.hitlabnz.sensor_fusion_demo.orientationProvider.AccelerometerCompassProvider;
import org.hitlabnz.sensor_fusion_demo.orientationProvider.AccelerometerProvider;
import org.hitlabnz.sensor_fusion_demo.orientationProvider.OrientationProvider;
import org.hitlabnz.sensor_fusion_demo.orientationProvider.RotationVectorProvider;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import temple.core.net.BroadcastReceiver;
import temple.multiplayer.net.bluetooth.service.AbstractBluetoothService;
import temple.multiplayer.net.bluetooth.service.BluetoothClientService;
import temple.multiplayer.net.bluetooth.service.BluetoothServerService;
import temple.multiplayer.net.common.service.ServiceMessageHandler;

/**
 * Main application class
 */
public class GoogleFlipGameApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = GoogleFlipGameApplication.class.getSimpleName();
    public static final String DEVICE_POSTFIX = "-tilt";

    public static Context sContext;

    private static UserModel sUserModel;
    private static BluetoothServerService sBluetoothServerService;
    private static BluetoothClientService sBluetoothClientService;
    private static GameServer sGameServer;
    private static GameClientImpl sGameClient;
    private static Activity sCurrentActivity;
    private static OrientationProvider sOrientationProvider;
    private static int sScreenRotation;
    private static String sBluetoothDeviceName;
    private static boolean sIsLanding;

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        sIsLanding = true;

        sContext = this;

        Prefs.initPrefs(this);

        sUserModel = new UserModel(this);

        LevelColorUtil.initColorMap();

        registerActivityLifecycleCallbacks(this);

        SoundManager.initialize(this);

        loadLevels();
    }

    private void loadLevels() {
        final BroadcastReceiver receiver = new BroadcastReceiver(this, true);
        receiver.addActionHandler(DataService.ACTION_LOAD_LEVELS, new BroadcastReceiver.ActionHandler() {
            @Override
            public void onAction(String action, Intent intent) {
                List<LevelVO> levels = intent.getParcelableArrayListExtra(DataService.KEY_LEVELS);
                sUserModel.setLevels(levels);

                List<LevelResultVO> results = intent.getParcelableArrayListExtra(DataService.KEY_LEVEL_RESULTS);
                sUserModel.setLevelResults(results);

                if (BuildConfig.UNLOCK_ALL) {
                    for (int i = 0; i < levels.size(); i++) {
                        sUserModel.unlockLevel(i);
                    }
                }

                sUserModel.setIsDataLoaded(true);

                receiver.onPause();

                new LoadSoundsTask().execute();
            }
        });
        receiver.onResume();

        DataService.loadLevels(this);
    }

    public static boolean getIsLanding() {
        return sIsLanding;
    }

    public static void setIsLanding(Boolean value) {
        sIsLanding = value;
    }

    public static UserModel getUserModel() {
        return sUserModel;
    }

    public static BluetoothServerService getBluetoothServerService() {
        if (sBluetoothServerService == null) {
            sBluetoothServerService = new BluetoothServerService(new ServiceMessageHandler());
            initBluetoothService(sBluetoothServerService);
        }

        return sBluetoothServerService;
    }

    public static BluetoothClientService getBluetoothClientService() {
        if (sBluetoothClientService == null) {
            sBluetoothClientService = new BluetoothClientService(new ServiceMessageHandler());
            initBluetoothService(sBluetoothClientService);
        }
        sBluetoothClientService.start();

        return sBluetoothClientService;
    }

    private static void initBluetoothService(AbstractBluetoothService service) {
        service.setDebug(BuildConfig.DEBUG);

        service.setInsecureUuid(sContext.getString(R.string.insecure_uuid));
        service.setSecureUuid(sContext.getString(R.string.secure_uuid));
        service.setApplicationId(BuildConfig.APPLICATION_ID);
    }


    public static GameServer getGameServer() {
        if (sGameServer == null) {
            sGameServer = new GameServerImpl();
            sGameServer.setLevels(sUserModel.getLevels());
        }

        return sGameServer;
    }

    public static GameClient getGameClient() {
        if (sGameClient == null) {
            sGameClient = new GameClientImpl();
            sGameClient.setDebug(BuildConfig.DEBUG);
        }

        return sGameClient;
    }

    public static OrientationProvider getOrientationProvider(Activity activity) {
        if (sOrientationProvider == null) {
            SensorManager sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
            if (sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0) {
                sOrientationProvider = new RotationVectorProvider(sensorManager);
            } else if (sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size() > 0) {
                sOrientationProvider = new AccelerometerCompassProvider(sensorManager);
            } else {
                sOrientationProvider = new AccelerometerProvider(sensorManager);
            }
        }
        return sOrientationProvider;
    }

    public static void stopGame() {
        if (sGameServer != null) {
            sGameServer.stop();
        }

        if (sGameClient != null) {
            sGameClient.stop();
        }

        if (sBluetoothServerService != null) {
            sBluetoothServerService.stop();
        }

        if (sBluetoothClientService != null) {
            sBluetoothClientService.stop();
        }
    }

    public static void setCurrentActivity(Activity activity) {
        sCurrentActivity = activity;
    }

    public static void clearActivity(Activity activity) {
        if (activity.equals(sCurrentActivity)) {
            sCurrentActivity = null;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (sCurrentActivity == null || activity.equals(sCurrentActivity)) {
            if (sBluetoothServerService != null) {
                sBluetoothServerService.stop();
            }
            if (sBluetoothClientService != null) {
                sBluetoothClientService.stop();
            }
            if (sOrientationProvider != null) {
                sOrientationProvider.stop();
            }

            restoreBluetoothDeviceName();
        }
    }

    public static void setOriginalBluetoothDeviceName(String name) {
        sBluetoothDeviceName = name;
    }

    public static void restoreBluetoothDeviceName() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null && sBluetoothDeviceName != null) {
            // restore bluetooth device name
            adapter.setName(sBluetoothDeviceName);
        }
    }

    class LoadSoundsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SoundManager soundManager = SoundManager.getInstance();
            soundManager.load(R.raw.tap);
            soundManager.load(R.raw.ball_appear);
            soundManager.load(R.raw.bounce_1);
            soundManager.load(R.raw.bounce_2);
            soundManager.load(R.raw.bounce_3);
            soundManager.load(R.raw.fall_off);
            soundManager.load(R.raw.high_score);
            soundManager.load(R.raw.into_portal);
            soundManager.load(R.raw.level_done);
            soundManager.load(R.raw.portal_appear);
            soundManager.load(R.raw.ready);
            soundManager.load(R.raw.time_up);

            return null;
        }
    }

    public static void setScreenRotation(int rotation) {
        sScreenRotation = rotation;
    }

    public static int getScreenRotation() {
        return sScreenRotation;
    }
}
