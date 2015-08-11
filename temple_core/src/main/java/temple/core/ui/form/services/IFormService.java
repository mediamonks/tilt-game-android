package temple.core.ui.form.services;

import temple.core.ui.form.result.IFormResult;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public interface IFormService {
    IFormResult submit(Object data);

    void setListener(OnFormResultListener listener);

    interface OnFormResultListener {
        void onFormServiceEvent(FormServiceEvent event);
    }
}