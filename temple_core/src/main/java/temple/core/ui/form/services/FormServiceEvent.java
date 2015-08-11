package temple.core.ui.form.services;

import android.util.Log;

import temple.core.ui.form.result.IFormResult;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public class FormServiceEvent {
    private static final String TAG = FormServiceEvent.class.getSimpleName();

    private final FormEventType _type;

    public enum FormEventType {SUCCESS, RESULT, ERROR}

    ;

    private IFormResult _result;

    public FormServiceEvent(FormEventType type) {
        _type = type;
        _result = null;

        if (type == FormEventType.RESULT) {
            Log.e(TAG, "Result can not be null if type equals '" + FormEventType.RESULT + "'");
            return;
        }
    }

    public FormServiceEvent(FormEventType type, IFormResult result) {
        _type = type;
        _result = result;
    }

    public IFormResult getResult() {
        return _result;
    }

    public FormEventType getType() {
        return _type;
    }
}
