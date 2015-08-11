package com.mediamonks.googleflip.pages.connect.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.Fragments;
import com.mediamonks.googleflip.data.constants.MultiplayerProtocol;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import temple.core.ui.CustomButton;
import temple.core.ui.form.CustomEditText;

/**
 * Set player name for multiplayer games
 */
public class ConnectPlayerNameFragment extends BaseFragment {
    private static final String TAG = ConnectPlayerNameFragment.class.getSimpleName();

    @InjectView(R.id.tv_input_playername)
    protected CustomEditText _playerNameInput;
    @InjectView(R.id.next_button)
    protected CustomButton _nextButton;

    public static ConnectPlayerNameFragment newInstance() {
        return new ConnectPlayerNameFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(R.layout.fragment_player_name, inflater, container);
        ButterKnife.inject(this, view);

        _playerNameInput.requestFocus();

        // prefill previously set name from preferences if available
        if (Prefs.contains(PrefKeys.PLAYER_NAME)) {
            _playerNameInput.setText(Prefs.getString(PrefKeys.PLAYER_NAME, ""));
            _playerNameInput.setSelection(_playerNameInput.getText().length());
        } else {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        return view;
    }

    @OnClick(R.id.next_button)
    protected void onNextButtonClick() {
        String playerName = _playerNameInput.getText().toString().trim();

        if (!TextUtils.isEmpty(playerName)) {
            // store name in preferences
            Prefs.putString(PrefKeys.PLAYER_NAME, playerName);
            Prefs.putInt(PrefKeys.MULTIPLAYER_PROTOCOL, MultiplayerProtocol.BLUETOOTH.ordinal());

            // go to connection selection
            navigateTo(Fragments.CONNECT_CLIENTSERVER);
        } else {
            Toast.makeText(getActivity(), "Please fill in your name", Toast.LENGTH_SHORT).show();
        }
    }
}
