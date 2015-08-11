package temple.core.ui.form.result;

import temple.core.common.interfaces.IResult;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public class Result implements IResult {
    private boolean _success;
    private String _message;
    private String _code;

    public Result(boolean success, String message, String code) {
        _success = success;
        _message = message;
        _code = code;
    }

    @Override
    public boolean getSuccess() {
        return _success;
    }

    public void setSuccess(boolean success) {
        _success = success;
    }

    @Override
    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    @Override
    public String getCode() {
        return _code;
    }

    public void setCode(String code) {
        _code = code;
    }
}
