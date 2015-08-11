package temple.core.ui.form.result;

import temple.core.common.interfaces.IHasValue;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public class FormFieldError extends Result implements IFormFieldError {
    private String _name;
    private IHasValue _field;

    public FormFieldError(String name) {
        super(false, null, null);
        _name = name;
        _field = null;
    }

    public FormFieldError(String name, IHasValue element) {
        super(false, null, null);
        _name = name;
        _field = element;
    }

    public FormFieldError(String name, IHasValue element, String message) {
        super(false, message, null);
        _name = name;
        _field = element;
    }

    public FormFieldError(String name, IHasValue element, String message, FormErrorCode code) {
        super(false, message, code.name());
        _name = name;
        _field = element;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public IHasValue getField() {
        return _field;
    }

    public void setField(IHasValue field) {
        _field = field;
    }
}
