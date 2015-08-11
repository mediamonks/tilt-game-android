package temple.core.ui.form.validation.rules;

import temple.core.common.interfaces.IHasValue;
import temple.core.ui.form.validation.rules.AbstractValidationRule;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public class MinimumLengthValidationRule extends AbstractValidationRule implements IValidationRule {
    private final int _length;

    public MinimumLengthValidationRule(IHasValue element, int length) {
        super(element);

        _length = length;
    }

    @Override
    public boolean isValid() {
        return ((String) _target.getValue()).length() >= _length;
    }
}