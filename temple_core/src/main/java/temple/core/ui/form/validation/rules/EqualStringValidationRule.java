package temple.core.ui.form.validation.rules;

import temple.core.common.interfaces.IHasValue;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public class EqualStringValidationRule extends AbstractValidationRule implements IValidationRule {
    private final IHasValue _target2;

    public EqualStringValidationRule(IHasValue element, IHasValue element2) {
        super(element);

        _target2 = element2;
    }

    @Override
    public boolean isValid() {
        return _target.getValue().equals(_target2.getValue());
    }
}