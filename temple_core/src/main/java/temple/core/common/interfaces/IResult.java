package temple.core.common.interfaces;

/**
 * Created by erikpoort on 29/07/14.
 * MediaMonks
 */
public interface IResult {
    boolean getSuccess();

    String getMessage();

    String getCode();
}