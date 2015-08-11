package temple.core.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Helper class for showing an alert dialog
 */
public class AlertUtils {
    /**
     * Show an alert with a single button, and no click handler
     */
    public static void showAlert(Context context, int messageId, int titleId, int okButtonId) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };
        showAlert(context, messageId, titleId, okButtonId, onClickListener);
    }

    /**
     * Show an alert with specified click handler
     */
    public static void showAlert(Context context, int messageId, int titleId, int okButtonId, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setMessage(messageId)
                .setTitle(titleId)
                .setPositiveButton(okButtonId, onClickListener)
                .create()
                .show();
    }
}
