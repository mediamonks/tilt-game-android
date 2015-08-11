package temple.core.ui.form.validation.rules;

import temple.core.common.interfaces.IHasValue;

/**
 * Created by erikpoort on 14/08/14.
 * MediaMonks
 */
public class CountLowercaseValidationRule extends AbstractValidationRule
        implements IValidationRule {
    private final int _numberOfUppercases;

    public CountLowercaseValidationRule(IHasValue element, int numberOfUppercases) {
        super(element);

        _numberOfUppercases = numberOfUppercases;
    }

    private int countUppercases() {
        String string = getTarget().getValue().toString();

        char[] charArray = string.toCharArray();

        int leni = charArray.length;
        int count = 0;

        for (int i = 0; i < leni; i++) {
            if (charArray[i] >= 97 && charArray[i] <= 122) {
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
