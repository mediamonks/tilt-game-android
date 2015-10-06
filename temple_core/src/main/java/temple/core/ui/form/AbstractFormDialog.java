package temple.core.ui.form;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import butterknife.ButterKnife;
import temple.core.R;
import temple.core.net.ServiceBroadcastReceiver;
import temple.core.ui.form.result.IFormResult;
import temple.core.ui.form.services.IFormService;

/**
 * Created by erikpoort on 30/07/14.
 * MediaMonks
 */
public abstract class AbstractFormDialog extends DialogFragment {

    //    @Bind(R.id.form)
    protected View formView;
    //    @Bind(R.id.loading)
    protected View loadingView;

    protected View _view;
    protected Form _form;
    protected ServiceBroadcastReceiver _receiver;

    private int _titleTextId = -1;
    private int _negativeButtonTextId = -1;
    private int _positiveButtonTextId = -1;

    // TODO set these from the resources
    private int _negativeButtonId = -1;
    private int _positiveButtonId = -1;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _receiver = new ServiceBroadcastReceiver(getActivity());

        setButtonIds();
    }

    /**
     * Initialize the ids of the negative & positive buttons (cancel / sign in)
     * Typically this will be the following code:
     * <code>
     *      setNegativeButtonId(R.id.negative_button);
     *      setPositiveButtonId(R.id.positive_button);
     * </code>
     */
    protected abstract void setButtonIds();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = setupView();

        if (_titleTextId > 0) {
            setTitle(_titleTextId);
        }

        if (_positiveButtonTextId > 0) {
            setPositiveButtonTextId(_positiveButtonTextId);
        }

        if (_negativeButtonTextId > 0) {
            setNegativeButtonTextId(_negativeButtonTextId);
        }

        _view.findViewById(_positiveButtonId).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _form.submit();
                    }
                }
        );
        _view.findViewById(_negativeButtonId).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                }
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        _receiver.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        _receiver.onPause();
    }

    protected abstract View setupView();

    protected abstract void submitForm();

    protected void setContentView(int layoutId) {
        _view = getActivity().getLayoutInflater().inflate(layoutId, null);

        ButterKnife.bind(this, _view);

        _form = new Form(new IFormService() {
            public OnFormResultListener _listener;

            @Override
            public IFormResult submit(Object data) {
                setBusy(true);

                submitForm();

                return null;
            }

            @Override
            public void setListener(OnFormResultListener listener) {
                _listener = listener;
            }
        }, false);
    }

    public void setTitle(int resourceId) {
        if (_view != null && resourceId > 0) {
            TextView title = (TextView) _view.findViewById(R.id.title);
            title.setText(getString(resourceId).toUpperCase(Locale.US));
        } else {
            _titleTextId = resourceId;
        }
    }

    public void setNegativeButtonTextId(int resourceId) {
        if (_view != null && resourceId > 0) {
            Button button = (Button) _view.findViewById(_negativeButtonId);
            button.setText(getString(resourceId).toUpperCase(Locale.US));
        } else {
            _negativeButtonTextId = resourceId;
        }
    }

    public void setPositiveButtonTextId(int resourceId) {
        if (_view != null && resourceId > 0) {
            Button button = (Button) _view.findViewById(_negativeButtonId);
            button.setText(getString(resourceId).toUpperCase(Locale.US));
        } else {
            _positiveButtonTextId = resourceId;
        }
    }

    protected void setBusy(boolean busy) {
        loadingView.setVisibility(busy ? View.VISIBLE : View.GONE);
        formView.setVisibility(busy ? View.GONE : View.VISIBLE);

        if (busy) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        } else {
            _form.enable();
        }
    }

    /**
     * Override to show an error dialog
     * @param intent passed from the broadcast, may contain error information
     *
     * Example:
     * <code>
     * AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     *
     * // TODO set button text from string resources
     * // TODO set message dependent on intent
     * builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
     *  @Override
     *  public void onClick(DialogInterface dialog, int which) {
     *      onErrorDialogClosed();
     *  }
     * });

     *
     * </code>
     */
    protected abstract void showErrorDialog(Intent intent);

    protected void onErrorDialogClosed() {
        setBusy(false);

        _form.setEnabled(true);
    }

/*
*/

    protected void setNegativeButtonId(int negativeButtonId) {
        _negativeButtonId = negativeButtonId;
    }

    protected void setPositiveButtonId(int positiveButtonId) {
        _positiveButtonId = positiveButtonId;
    }

}
