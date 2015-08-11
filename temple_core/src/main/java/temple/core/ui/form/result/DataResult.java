package temple.core.ui.form.result;

import temple.core.common.interfaces.IDataResult;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public class DataResult extends Result implements IDataResult {
    private Object _data;

    public DataResult(Object data) {
        super(true, null, null);
        _data = data;
    }

    public DataResult(Object data, boolean success) {
        super(success, null, null);
        _data = data;
    }

    public DataResult(Object data, boolean success, String message) {
        super(success, message, null);
        _data = data;
    }

    public DataResult(Object data, boolean success, String message, String code) {
        super(success, message, code);
        _data = data;
    }

    @Override
    public Object getData() {
        return _data;
    }
}