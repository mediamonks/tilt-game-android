package temple.core.ui.form.validation.rules;

import temple.core.common.interfaces.IHasValue;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public abstract class AbstractValidationRule implements IValidationRule {
    protected final IHasValue _target;

    protected AbstractValidationRule(IHasValue target) {
        _target = target;
    }

    public IHasValue getTarget() {
        return _target;
    }
}