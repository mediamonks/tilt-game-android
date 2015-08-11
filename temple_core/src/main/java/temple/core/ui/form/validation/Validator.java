package temple.core.ui.form.validation;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import temple.core.common.interfaces.IChangeable;
import temple.core.common.interfaces.IHasValue;
import temple.core.ui.form.validation.rules.IValidationRule;
import temple.core.ui.form.validation.rules.ValidationRuleData;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public class Validator implements IValidator, IChangeable.OnChangeListener {
    private static final String TAG = Validator.class.getSimpleName();

    private ArrayList<ValidationRuleData> _rules;
    private boolean _showErrors;
    private boolean _autoFocus;
    private boolean _debug;

    private OnValidateListener _onValidateListener;

    public Validator() {
        setup(true);
    }

    public Validator(boolean autoFocus) {
        setup(autoFocus);
    }

    private void setup(boolean autoFocus) {
        _rules = new ArrayList<ValidationRuleData>();
        _autoFocus = autoFocus;
    }

    public IValidationRule addValidationRule(IValidationRule rule) {
        return addValidationRule(rule, null);
    }

    public IValidationRule addValidationRule(IValidationRule rule, String message) {
        if (rule != null) {
            _rules.add(new ValidationRuleData(rule, message));
        }

        return rule;
    }

    public void removeElement(IHasValue element) {
        for (int i = _rules.size() - 1; i >= 0; i--) {
            if (_rules.get(i).getRule().getTarget() == element) {
                _rules.remove(i);
            }
        }

        if (element instanceof IChangeable) {
            ((IChangeable) element).setOnChangeListener(null);
        }
    }

    public ArrayList<ValidationRuleData> validate() {
        return validate(true, true);
    }

    public ArrayList<ValidationRuleData> validate(boolean showErrors) {
        return validate(showErrors, true);
    }

    public ArrayList<ValidationRuleData> validate(boolean showErrors, boolean keepValidating) {
        _showErrors = showErrors;

        ArrayList<ValidationRuleData> errors = new ArrayList<ValidationRuleData>();
        ArrayList<IHasValue> dictionary = new ArrayList<IHasValue>();

        IValidationRule rule;
        IHasValue target;

        for (ValidationRuleData data : _rules) {
            rule = data.getRule();
            target = rule.getTarget();

            if (target instanceof View && !((View) target).isEnabled()) {
                if (_debug) {
                    Log.i(TAG, "Target is not enabled, skip: " + data);
                }

                continue;
            }

            if (rule.isValid()) {
                if (_debug) {
                    Log.i(TAG, "Valid: " + rule);
                }

                if (_showErrors && target instanceof IHasError && !dictionary.contains(target)) {
                    ((IHasError) target).hideError();
                }
            } else {
                errors.add(data);

                if (_debug) {
                    Log.i(TAG, "Not valid: " + rule);
                }

                if (_showErrors && target instanceof IHasError && !dictionary.contains(target)) {
                    ((IHasError) target).showError(data.getMessage());
                    dictionary.add(target);
                }
            }

            if (keepValidating && target instanceof IChangeable) {
                ((IChangeable) target).setOnChangeListener(this);
            }
        }

        if (_onValidateListener != null) {
            _onValidateListener.validateComplete();
        }

        return errors;
    }

    public boolean isValid() {
        return validate().size() == 0;
    }

    public boolean isElementValid(IHasValue element) {
        return isElementValid(element, false);
    }

    public boolean isElementValid(IHasValue element, boolean showError) {
        boolean valid = true;
        String errorMessage = null;

        for (ValidationRuleData data : _rules) {
            if (data.getRule().getTarget() == element && !data.getRule().isValid()) {
                valid = false;
                errorMessage = data.getMessage();
                break;
            }
        }

        if (showError && !valid && element instanceof IHasError) {
            ((IHasError) element).showError(errorMessage);
        }

        return valid;
    }

    public ArrayList<IHasValue> getElements() {
        ArrayList<IHasValue> elements = new ArrayList<IHasValue>();

        for (ValidationRuleData data : _rules) {
            elements.add(data.getRule().getTarget());
        }

        return elements;
    }

    public ArrayList<IValidationRule> getRulesForElement(IHasValue element) {
        ArrayList<IValidationRule> rules = new ArrayList<IValidationRule>();

        for (ValidationRuleData data : _rules) {
            if (data.getRule().getTarget() == element) {
                rules.add(data.getRule());
            }
        }

        return rules;
    }

    public void stopRealtimeValidating() {
        if (_rules != null) {
            IHasValue element;

            for (ValidationRuleData data : _rules) {
                element = data.getRule().getTarget();

                if (element instanceof IChangeable) {
                    ((IChangeable) element).setOnChangeListener(null);
                }
            }
        }
    }

    public boolean isAutoFocus() {
        return _autoFocus;
    }

    public void setAutoFocus(boolean value) {
        _autoFocus = value;
    }

    public boolean getDebug() {
        return _debug;
    }

    public void setDebug(boolean value) {
        _debug = value;
    }

    @Override
    public void onChange(IHasValue element) {
        boolean isValid = true;
        String message = null;

        for (ValidationRuleData data : _rules) {
            if (data.getRule().getTarget() != element) {
                continue;
            }

            if (data.getRule().getTarget() instanceof IHasError) {
                if (!data.getRule().isValid()) {
                    isValid = false;
                    message = data.getMessage();
                    break;
                }
            }
        }

        if (isValid && _showErrors) {
            ((IHasError) element).hideError();
        } else if (_showErrors) {
            if (message != null) {
                ((IHasError) element).showError(message);
            } else {
                ((IHasError) element).showError();
            }
        }

        if (_onValidateListener != null) {
            _onValidateListener.validateComplete();
        }
    }

    public void setOnValidateListener(OnValidateListener onValidateListener) {
        _onValidateListener = onValidateListener;
    }

    public interface OnValidateListener {
        void validateComplete();
    }
}