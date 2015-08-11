package temple.core.ui.form.validation;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public interface IHasError {
    public boolean hasError();

    public void showError();

    public void showError(String message);

    public void hideError();
}