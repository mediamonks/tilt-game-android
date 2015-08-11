package temple.core.ui.form.validation.rules;


import temple.core.common.interfaces.IHasValue;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public class EmptyStringValidationRule extends AbstractValidationRule {
    public EmptyStringValidationRule(IHasValue target) {
        super(target);
    }

    @Override
    public boolean isValid() {
        return !((String) _target.getValue()).isEmpty();
    }
}
