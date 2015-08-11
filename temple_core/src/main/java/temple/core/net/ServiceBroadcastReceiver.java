package temple.core.net;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by STEPHAN on 8-12-2014.
 */
public class ServiceBroadcastReceiver extends BroadcastReceiver {
    private Map<String, ErrorHandler> _errorHandlerMap = new HashMap<>();

    public ServiceBroadcastReceiver(Context context) {
        super(context);

        _intentFilter = new IntentFilter(BaseService.BROADCAST_ERROR);
    }

    public ServiceBroadcastReceiver(Context context, boolean useLocalBroadcastManager) {
        super(context, useLocalBroadcastManager);

        _intentFilter = new IntentFilter(BaseService.BROADCAST_ERROR);
    }

    public void addActionHandler(String action, ActionHandler handler, ErrorHandler errorHandler) {
        addActionHandler(action, handler);
        addErrorHandler(action, errorHandler);
    }

    public void addErrorHandler(String action, ErrorHandler handler) {
        if (_errorHandlerMap.containsKey(action)) {
            throw new Error("Action " + action + " already added to map");
        }

        _errorHandlerMap.put(action, handler);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(BaseService.BROADCAST_ERROR)) {
            String originalAction = intent.getStringExtra(BaseService.KEY_ORIGINAL_ACTION);

            for (Map.Entry<String, ErrorHandler> entry : _errorHandlerMap.entrySet()) {
                if (entry.getKey().equals(originalAction)) {
                    entry.getValue().onError(originalAction, intent);
                    break;
                }
            }
        } else {
            super.onReceive(context, intent);
        }
    }

    public interface ErrorHandler {
        void onError(String action, Intent intent);
    }
}
