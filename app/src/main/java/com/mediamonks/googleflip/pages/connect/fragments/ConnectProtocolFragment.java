package com.mediamonks.googleflip.pages.connect.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.ActivityRequestCode;
import com.mediamonks.googleflip.data.constants.Fragments;
import com.mediamonks.googleflip.data.constants.MultiplayerProtocol;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.OnClick;
import temple.core.utils.AlertUtils;

/**
 * Unused screen to select Blutooth or WifiP2P
 */
public class ConnectProtocolFragment extends BaseFragment {
    private static final String TAG = ConnectProtocolFragment.class.getSimpleName();

    public static ConnectProtocolFragment newInstance() {
        return new ConnectProtocolFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createView(R.layout.fragment_connection, inflater, container);
    }

    @OnClick(R.id.btn_bluetooth)
    protected void onBluetootButtonClicked() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            AlertUtils.showAlert(getActivity(), R.string.no_bluetooth_message, R.string.no_bluetooth_title, R.string.btn_ok);
        } else if (!adapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), ActivityRequestCode.REQUEST_ENABLE_BT);
        } else {
            goNextScreen(MultiplayerProtocol.BLUETOOTH);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityRequestCode.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                goNextScreen(MultiplayerProtocol.BLUETOOTH);
            }
        }
    }

    @OnClick(R.id.btn_wifi)
    protected void onWifiButtonClick() {
        goNextScreen(MultiplayerProtocol.WIFIP2P);
    }

    private void goNextScreen(MultiplayerProtocol protocol) {
        Prefs.putInt(PrefKeys.MULTIPLAYER_PROTOCOL, protocol.ordinal());

        navigateTo(Fragments.CONNECT_CLIENTSERVER);
    }
}
