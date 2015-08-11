package temple.core.net;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to listen for broadcasts, either from local or global broadcasts
 */
public class BroadcastReceiver extends android.content.BroadcastReceiver {
    private static final String TAG = BroadcastReceiver.class.getSimpleName();

    protected IntentFilter _intentFilter;

    private boolean _useLocalBroadcastManager;
    private Context _context;
    private Map<String, ActionHandler> _handlerMap = new HashMap<>();
    private boolean _debug;

    public BroadcastReceiver(Context context, boolean useLocalBroadcastManager) {
        this(context);

        _useLocalBroadcastManager = useLocalBroadcastManager;
    }

    public BroadcastReceiver(Context context) {
        _context = context;

        _intentFilter = new IntentFilter();
    }

    public void onResume() {
        if (_debug) Log.d(TAG, "onResume: ");

        if (_useLocalBroadcastManager) {
            LocalBroadcastManager.getInstance(_context).registerReceiver(this, _intentFilter);
        } else {
            _context.registerReceiver(this, _intentFilter);
        }
    }

    public void onPause() {
        if (_debug) Log.d(TAG, "onPause: ");

        if (_useLocalBroadcastManager) {
            LocalBroadcastManager.getInstance(_context).unregisterReceiver(this);
        } else {
            _context.unregisterReceiver(this);
        }
    }

    public void addActionHandler(String action, ActionHandler handler) {
        if (_debug) Log.d(TAG, "addActionHandler: action = " + action);

        if (_handlerMap.containsKey(action)) {
            throw new Error("Action " + action + " already added to map");
        }

        _handlerMap.put(action, handler);

        _intentFilter.addAction(action);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (_debug) Log.d(TAG, "onReceive: action = " + action);

        for (Map.Entry<String, ActionHandler> entry : _handlerMap.entrySet()) {
            if (entry.getKey().equals(action)) {
                entry.getValue().onAction(action, intent);
                break;
            }
        }
    }

    public interface ActionHandler {
        void onAction(String action, Intent intent);
    }

    public void setDebug (boolean isDebug) {
        _debug = isDebug;
    }
}
