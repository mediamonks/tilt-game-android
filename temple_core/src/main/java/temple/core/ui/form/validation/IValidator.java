package temple.core.ui.form.validation;

import java.util.ArrayList;

import temple.core.ui.form.validation.rules.ValidationRuleData;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public interface IValidator {
    public ArrayList<ValidationRuleData> validate();

    public ArrayList<ValidationRuleData> validate(boolean showErrors);

    public ArrayList<ValidationRuleData> validate(boolean showErrors, boolean keepValidating);
}