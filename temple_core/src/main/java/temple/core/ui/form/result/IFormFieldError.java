package temple.core.ui.form.result;

import temple.core.common.interfaces.IHasValue;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public interface IFormFieldError {
    public String getName();

    public IHasValue getField();

    String getMessage();

    String getCode();
}