package com.mediamonks.googleflip.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediamonks.googleflip.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * View class for showing progress
 */
public class ProgressDialog extends DialogFragment {

    @Bind(R.id.loading)
    protected View loadingView;

    private static ProgressDialog _instance = new ProgressDialog();
    private static boolean sIsVisible;

    public static void showInstance(FragmentManager manager) {
        if (!sIsVisible) {
            sIsVisible = true;

            manager.beginTransaction().add(_instance, ProgressDialog.class.getSimpleName()).commitAllowingStateLoss();
        }
    }

    public static void dismissInstance() {
        if (sIsVisible) {
            sIsVisible = false;

            _instance.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.include_form_progress, container, false);
        ButterKnife.bind(this, view);

        loadingView.setVisibility(View.VISIBLE);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_70)));

        return view;
    }
}
