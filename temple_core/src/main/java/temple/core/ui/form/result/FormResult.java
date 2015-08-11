package temple.core.ui.form.result;

import java.util.ArrayList;

import temple.core.common.interfaces.IDataResult;
import temple.core.common.interfaces.IResult;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public class FormResult extends DataResult implements IFormResult {
    public static IFormResult createFormResult(IResult result) {
        return result instanceof IFormResult ? (IFormResult) result
                : new FormResult(result.getSuccess(), result.getMessage(), result.getCode(),
                result instanceof IDataResult ? ((IDataResult) result).getData() : null);
    }

    private ArrayList<IFormFieldError> _errors;

    public FormResult() {
        super(null, false);
        _errors = null;
    }

    public FormResult(boolean success) {
        super(null, success);
        _errors = null;
    }

    public FormResult(boolean success, String message) {
        super(null, success, message);
        _errors = null;
    }

    public FormResult(boolean success, String message, String code) {
        super(null, success, message, code);
        _errors = null;
    }

    public FormResult(boolean success, String message, String code, Object data) {
        super(data, success, message, code);
        _errors = null;
    }

    public FormResult(boolean success, String message, String code, Object data,
                      ArrayList<IFormFieldError> errors) {
        super(data, success, message, code);
        _errors = errors;
    }

    @Override
    public ArrayList<IFormFieldError> getErrors() {
        return _errors;
    }

    public void setErrors(ArrayList<IFormFieldError> errors) {
        _errors = errors;
    }
}