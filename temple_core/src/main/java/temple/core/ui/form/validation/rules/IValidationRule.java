package temple.core.ui.form.validation.rules;


import temple.core.common.interfaces.IHasValue;
import temple.core.ui.form.validation.IValidatable;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public interface IValidationRule extends IValidatable {
    public IHasValue getTarget();
}