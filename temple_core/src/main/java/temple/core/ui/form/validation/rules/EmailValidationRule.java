package temple.core.ui.form.validation.rules;


import android.util.Patterns;

import temple.core.common.interfaces.IHasValue;

/**
 * Created by erikpoort on 30/07/14.
 * MediaMonks
 */
public class EmailValidationRule extends AbstractValidationRule implements IValidationRule {
    public EmailValidationRule(IHasValue element) {
        super(element);
    }

    @Override
    public boolean isValid() {
        return Patterns.EMAIL_ADDRESS.matcher((CharSequence) _target.getValue()).matches();
    }
}
