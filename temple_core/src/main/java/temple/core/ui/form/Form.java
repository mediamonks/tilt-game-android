package temple.core.ui.form;

import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import temple.core.common.interfaces.IDebuggable;
import temple.core.common.interfaces.IHasValue;
import temple.core.common.interfaces.IResettable;
import temple.core.ui.form.result.FormFieldError;
import temple.core.ui.form.result.IFormFieldError;
import temple.core.ui.form.result.IFormResult;
import temple.core.ui.form.services.FormServiceEvent;
import temple.core.ui.form.services.IFormService;
import temple.core.ui.form.validation.IHasError;
import temple.core.ui.form.validation.Validator;
import temple.core.ui.form.validation.rules.IValidationRule;
import temple.core.ui.form.validation.rules.ValidationRuleData;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 * <p/>
 * Semi-port from AS-temple
 */
public class Form implements IFormService.OnFormResultListener {
    private static final String TAG = Form.class.getSimpleName();

    private Validator _validator;
    private IFormService _formService;
    private HashMap<String, Object> _data;
    private HashMap<String, FormElementData> _elements;
    private HashMap<IHasValue, String> _names;
    private boolean _debug;
    private boolean _enabled = true;
    private HashMap<String, String> _prefillData;
    private ArrayList<View> _submitButtons;
    private ArrayList<View> _resetButtons;
    private int _elementIndex;
    private boolean _disableOnSubmit = true;
    private OnFormListener _listener;

    public Form(IFormService formService, boolean debug) {
        super();

        _validator = new Validator();

        _data = new HashMap<String, Object>();
        _elements = new HashMap<String, FormElementData>();
        _names = new HashMap<IHasValue, String>();

        _submitButtons = new ArrayList<View>();
        _resetButtons = new ArrayList<View>();

        _debug = debug;

        _formService = formService;
    }

    public IHasValue add(IHasValue element) {
        return add(element, null, null, null, -1, true);
    }

    public IHasValue add(IHasValue element, String name) {
        return add(element, name, null, null, -1, true);
    }

    public IHasValue add(IHasValue element, String name, Class validationRule) {
        return add(element, name, validationRule, null, -1, true);
    }

    public IHasValue add(IHasValue element, String name, Class validationRule,
                         String errorMessage) {
        return add(element, name, validationRule, errorMessage, -1, true);
    }

    public IHasValue add(IHasValue element, String name, Class validationRule, String errorMessage,
                         int index) {
        return add(element, name, validationRule, errorMessage, index, true);
    }

    public IHasValue add(IHasValue element, String name, Class<IValidationRule> validationRule,
                         String errorMessage, int index, boolean submit) {
        if (_debug) {
            if (submit && name != null) {
                Log.i(TAG, "add: " + element + " '" + name + "'");
            } else {
                Log.i(TAG, "add: " + element + (name == null ? "" : " '" + name + "'")
                        + ", value will not be submitted to service");
            }
        }

        if (name == null) {
            name = String.valueOf(_elementIndex);
            _elementIndex++;
        }

        if (element == null) {
            Log.e(TAG, "element can not be null");
            return null;
        }

        if (_elements.get(name) != null) {
            Log.e(TAG, "element with name '" + name + "' already exists");
            return null;
        }

        if (_names.get(element) != null) {
            Log.e(TAG, "element already exists in form");
            return null;
        }

        _names.put(element, name);
        _elements.put(name,
                new FormElementData(name, element, index == -1 ? _elements.size() : index, submit));

        if (validationRule != null) {
            IValidationRule validationRuleObject;

            try {
                Constructor<IValidationRule> constructor = validationRule
                        .getConstructor(IHasValue.class);
                validationRuleObject = constructor.newInstance(element);
            } catch (Exception e) {
                Log.e(TAG, "invalid validation rule: " + name);
                return null;
            }

            _validator.addValidationRule(validationRuleObject, errorMessage);
        }

        if (_prefillData != null && _prefillData.get(name) != null) {
            element.setValue(_prefillData.get(name));
        }

        if (_debug && element instanceof IDebuggable) {
            ((IDebuggable) element).setDebug(_debug);
        }

        return element;
    }

    public void remove(IHasValue element) {
        if (_debug) {
            Log.i(TAG, "remove: " + element);
        }

        _elements.remove(getNameFor(element));
        _names.remove(element);

        if (_validator != null) {
            _validator.removeElement(element);
        }
    }

    public boolean has(String name) {
        return _elements.get(name) != null;
    }

    public IHasValue get(String name) {
        return _elements.get(name) != null ? _elements.get(name).getElement() : null;
    }

    public String getNameFor(IHasValue element) {
        return _names.get(element);
    }

    public void update(String name, boolean submit) {
        if (has(name)) {
            _elements.get(name).setSubmit(submit);
        } else {
            Log.e(TAG, "No element found with name '" + name + "'");
        }
    }

    public void removeAll() {
        if (_debug) {
            Log.i(TAG, "removeAll");
        }

        ArrayList<IHasValue> elements = new ArrayList<IHasValue>();
        for (IHasValue element : _names.keySet()) {
            elements.add(element);
        }
        for (IHasValue element : elements) {
            remove(element);
        }
    }

    public void addSubmitButton(View button) {
        if (_debug) {
            Log.i(TAG, "addSubmitButton: " + button);
        }

        _submitButtons.add(button);
        button.setOnClickListener(_submitClicked);
    }

    public void removeSubmitButton(View button) {
        if (_debug) {
            Log.i(TAG, "removeSubmitButton: " + button);
        }

        _submitButtons.remove(button);
        button.setOnClickListener(null);
    }

    public void addResetButton(View button) {
        _resetButtons.add(button);
        button.setOnClickListener(_cancelClicked);
    }

    public void removeResetButton(View button) {
        _resetButtons.remove(button);
        button.setOnClickListener(null);
    }

    public void submit() {
        if (_debug) {
            Log.i(TAG, "submit:");
        }

        if (_enabled) {
            if (validate().size() == 0) {
                if (_debug) {
                    Log.i(TAG, getData().toString());
                }

                send();
            }
        } else {
            Log.i(TAG, "submit: Form is disabled!");
        }
    }

    public void insertData(String name, Object data) {
        if (_debug) {
            Log.i(TAG, "insertData: " + name + "=" + data);
        }

        _data.put(name, data);
    }

    public void reset() {
        if (_debug) {
            Log.i(TAG, "clear: ");
        }

        _validator.stopRealtimeValidating();
        for (FormElementData data : _elements.values()) {
            if (data instanceof IHasError) {
                ((IHasError) data).hideError();
            }
            if (data instanceof IResettable) {
                ((IResettable) data).reset();
            }
        }

        if (_listener != null) {
            _listener.reset();
        }
    }

    public boolean isValid() {
        return _validator.isValid();
    }

    public ArrayList<ValidationRuleData> validate() {
        return validate(true, true);
    }

    public ArrayList<ValidationRuleData> validate(boolean showErrors) {
        return validate(showErrors, true);
    }

    public ArrayList<ValidationRuleData> validate(final boolean showErrors,
                                                  final boolean keepValidating) {
        if (_debug) {
            Log.i(TAG, "validate");
        }

        if (keepValidating) {
            _validator.setOnValidateListener(new Validator.OnValidateListener() {
                @Override
                public void validateComplete() {
                    _validator.setOnValidateListener(null);

                    if (_listener != null) {
                        if (_validator.validate(showErrors).size() == 0) {
                            _listener.validateSuccess();
                        } else {
                            _listener.validateError();
                        }
                    }

                    _validator.setOnValidateListener(this);
                }
            });
        }

        ArrayList<ValidationRuleData> errors = _validator.validate(showErrors, keepValidating);

        if (errors.size() == 0) {
            if (_debug) {
                Log.i(TAG, "Form is valid");
            }

            if (_listener != null) {
                _listener.validateSuccess();
                return errors;
            }
        }

        if (_debug) {
            Log.i(TAG, "Form is invalid");
        }

        ArrayList<IFormFieldError> fields = new ArrayList<IFormFieldError>();

        IHasValue element;

        for (ValidationRuleData validationRuleData : errors) {
            element = validationRuleData.getRule().getTarget();

            fields.add(new FormFieldError(getNameFor(element), element,
                    validationRuleData.getMessage()));
        }

        if (_listener != null) {
            _listener.validateError();
        }

        return errors;
    }

    public HashMap<String, Object> getData() {
        for (FormElementData data : _elements.values()) {
            if (data.getSubmit()) {
                _data.put(data.getName(), data.getElement().getValue());
            }
        }

        if (_debug) {
            for (String key : _data.keySet()) {
                Log.i(TAG, "ModelData: [" + key + "] : " + _data.get(key));
            }
        }

        return _data;
    }

    public void prefill(HashMap<String, String> data) {
        _prefillData = data;

        if (_debug) {
            Log.i(TAG, "prefillData: " + _prefillData);
        }

        if (_prefillData != null) {
            for (FormElementData formElementData : _elements.values()) {
                if (_prefillData.containsKey(formElementData.getName())) {
                    formElementData.getElement()
                            .setValue(_prefillData.get(formElementData.getName()));
                } else if (_debug) {
                    Log.i(TAG, "prefillData: " + formElementData.getName() + " not found");
                }
            }
        }
    }

    public Validator getValidator() {
        return _validator;
    }

    public boolean isEnabled() {
        return _enabled;
    }

    public void setEnabled(boolean enabled) {
        if (_debug) {
            Log.i(TAG, "enabled: " + enabled);
        }

        _enabled = enabled;

        for (View view : _submitButtons) {
            view.setEnabled(enabled);
        }
        for (View view : _resetButtons) {
            view.setEnabled(enabled);
        }
    }

    public void enable() {
        setEnabled(true);
    }

    public void disable() {
        setEnabled(false);
    }

    public boolean getDisableOnSubmit() {
        return _disableOnSubmit;
    }

    public void setDisableOnSubmit(boolean disableOnSubmit) {
        _disableOnSubmit = disableOnSubmit;
    }

    public boolean getDebug() {
        return _debug;
    }

    public void setDebug(boolean debug) {
        _debug = debug;

        if (_debug) {
            Log.w(TAG, "Form is running in debug mode!");
        }
    }

    protected void send() {
        if (_debug) {
            Log.i(TAG, "send: ");
        }

        if (_formService != null) {
            if (_disableOnSubmit) {
                setEnabled(false);
            }

            _formService.setListener(this);

            IFormResult result = _formService.submit(getData());

            if (result != null && !_enabled) {
                onResult(result);

                _formService.setListener(null);
            }
        } else {
            Log.w(TAG, "send: service is not set, form can not be submitted!");
        }
    }

    protected View.OnClickListener _submitClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            submit();
        }
    };

    protected View.OnClickListener _cancelClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            reset();
        }
    };

    public void onFormServiceEvent(FormServiceEvent event) {
        switch (event.getType()) {
            case SUCCESS: {
                setEnabled(true);

                if (_debug) {
                    Log.i(TAG, "onFormServiceEvent: " + event.getType());
                }

                if (_listener != null) {
                    _listener.submitSuccess(event.getResult());
                }
                break;
            }
            case RESULT: {
                onResult(event.getResult());
                break;
            }
            case ERROR: {
                setEnabled(true);

                if (_debug) {
                    Log.e(TAG, "onFormServiceEvent: " + event.getType());
                }

                if (_listener != null) {
                    _listener.submitError(event.getResult());
                }
                break;
            }
            default: {
                if (_debug) {
                    Log.i(TAG, "onFormServiceEvent: " + event.getType());
                }
                break;
            }
        }
    }

    private void onResult(IFormResult result) {
        setEnabled(true);

        if (result.getSuccess()) {
            if (_debug) {
                Log.i(TAG, "Success " + result.getMessage());
            }
        } else {
            if (_debug) {
                Log.e(TAG, "Error " + result.getMessage());
            }

            FormElementData data;

            for (IFormFieldError error : result.getErrors()) {
                data = _elements.get(error.getField());

                if (data != null) {
                    if (error.getField() == null && error instanceof FormFieldError) {
                        ((FormFieldError) error).setField(data.getElement());
                    }
                } else {
                    if (_debug) {
                        Log.w(TAG, "No field with name '" + error.getField() + "' found");
                    }
                }

                if (_debug) {
                    Log.e(TAG,
                            "Error: " + error.getField() + " '" + error.getMessage() + "' (" + error
                                    .getCode() + ")");
                }
            }
        }

        if (_listener != null) {
            _listener.submitSuccess(result);
        }
    }

    public void setListener(OnFormListener listener) {
        _listener = listener;
    }

    private class FormElementData {
        private final String _name;
        private final IHasValue _element;
        private final int _index;
        private boolean _submit;

        private FormElementData(String name, IHasValue element, int index, boolean submit) {
            _name = name;
            _element = element;
            _index = index;
            _submit = submit;
        }

        public IHasValue getElement() {
            return _element;
        }

        public void setSubmit(boolean submit) {
            _submit = submit;
        }

        public boolean getSubmit() {
            return _submit;
        }

        public String getName() {
            return _name;
        }
    }

    public interface OnFormListener {
        void reset();

        void validateSuccess();

        void validateError();

        void submitSuccess(IFormResult result);

        void submitError(IFormResult result);
    }
}