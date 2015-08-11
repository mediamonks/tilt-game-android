package temple.core.ui.form.result;

import java.util.ArrayList;

import temple.core.common.interfaces.IResult;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public interface IFormResult extends IResult {
    public ArrayList<IFormFieldError> getErrors();
}