package temple.core.net;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Created by stephan on 14-1-2015.
 */
public class BaseService extends IntentService {

    public static String BROADCAST_ERROR = ".broadcast.error";

    public static final String KEY_RESPONSE = "key_response";
    public static final String KEY_ORIGINAL_ACTION = "key_originalAction";
    public static final String KEY_HTTP_STATUS = "key_httpStatus";
    public static final String KEY_API_ERROR_OBJECT = "key_api_error_object";

    private final static Object LOCK = new Object();
    private static boolean sAppIdInitialized;

    public static void setApplicationIdPrefix(String prefix) {
        BROADCAST_ERROR = prefix + BROADCAST_ERROR;

        sAppIdInitialized = true;
    }

    public static void startService(Context context, Class<?> cls, String action) {
        startService(context, cls, action, new Bundle());
    }

    public static void startService(Context context, Class<?> cls, String action, Bundle extras) {
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        intent.putExtras(extras);

        context.startService(intent);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public BaseService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (LOCK) {
        }
    }

    protected void handleError(RetrofitError error, Intent intent) {
        handleError(error, intent, null);
    }

    protected void handleError(RetrofitError error, Intent intent, String broadcast) {
        if (!sAppIdInitialized) {
            throw new Error("Call setApplicationIdPrefix(BuildConfig.APPLICATION_ID) before handling errors");
        }

        Response response = error.getResponse();
        int status = (response == null) ? 0 : response.getStatus();
        ApiErrorVO errorVO = null;
        if (response != null) {
            Header header = getHeaderByName(response.getHeaders(), "Content-Type");
            if (header != null) {
                if (header.getValue().contains("application/json")) {
                    try {
                        errorVO = (ApiErrorVO) error.getBodyAs(ApiErrorVO.class);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }

        if (errorVO == null) {
            errorVO = new ApiErrorVO();
        }
        errorVO.setErrorKind(error.getKind());

        Intent broadcastIntent = new Intent(BROADCAST_ERROR);
        broadcastIntent.putExtra(KEY_ORIGINAL_ACTION, TextUtils.isEmpty(broadcast) ? intent.getAction() : broadcast);
        broadcastIntent.putExtra(KEY_API_ERROR_OBJECT, errorVO);
        broadcastIntent.putExtra(KEY_HTTP_STATUS, status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    protected Header getHeaderByName(List<Header> headers, String name) {
        for (Header header : headers) {
            if (!TextUtils.isEmpty(header.getName()) && header.getName().equals(name)) {
                return header;
            }
        }
        return null;
    }
}
