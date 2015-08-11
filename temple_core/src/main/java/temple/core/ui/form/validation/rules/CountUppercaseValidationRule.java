package temple.core.ui.form.validation.rules;

import temple.core.common.interfaces.IHasValue;
import temple.core.ui.form.validation.rules.AbstractValidationRule;
import temple.core.ui.form.validation.rules.IValidationRule;

/**
 * Created by erikpoort on 14/08/14.
 * MediaMonks
 */
public class CountUppercaseValidationRule extends AbstractValidationRule
        implements IValidationRule {
    private final int _numberOfUppercases;

    public CountUppercaseValidationRule(IHasValue element, int numberOfUppercases) {
        super(element);

        _numberOfUppercases = numberOfUppercases;
    }

    private int countUppercases() {
        String string = getTarget().getValue().toString();

        char[] charArray = string.toCharArray();

        int leni = charArray.length;
        int count = 0;

        for (int i = 0; i < leni; i++) {
            if (charArray[i] >= 65 && charArray[i] <= 90) {
                count++;
            }
        }

        return count;
    }

    @Override
    public boolean isValid() {
        return countUppercases() >= _numberOfUppercases;
    }
}
