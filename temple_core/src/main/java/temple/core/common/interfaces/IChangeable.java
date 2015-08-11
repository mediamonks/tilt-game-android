package temple.core.common.interfaces;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public interface IChangeable {
    void setOnChangeListener(OnChangeListener listener);

    interface OnChangeListener {
        void onChange(IHasValue element);
    }
}