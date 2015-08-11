package temple.core.ui.form.validation.rules;

import temple.core.ui.form.validation.rules.IValidationRule;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public class ValidationRuleData {
    private final IValidationRule _rule;
    private final String _message;

    public ValidationRuleData(IValidationRule rule, String message) {
        _rule = rule;
        _message = message;
    }

    public IValidationRule getRule() {
        return _rule;
    }

    public String getMessage() {
        return _message;
    }
}