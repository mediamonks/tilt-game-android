package com.mediamonks.googleflip.ui;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediamonks.googleflip.util.Navigator;

import butterknife.ButterKnife;

/**
 * Base class for fragments
 */
public class BaseFragment extends Fragment {

	private Navigator _navigator;

	public void setNavigator(Navigator value){
		_navigator = value;
	}

	protected View createView (int id, LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(id, container, false);

		ButterKnife.inject(this, view);

		return view;
	}

    protected void navigateTo (String name) {
        _navigator.navigateTo(name);
    }
}
