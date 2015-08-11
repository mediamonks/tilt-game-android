package com.mediamonks.googleflip.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mediamonks.googleflip.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 *
 */
public class PlayerItem extends LinearLayout {
	private static final String TAG = PlayerItem.class.getSimpleName();

	@InjectView(R.id.tv_playername)
	protected TextView _playerText;
	@InjectView(R.id.iv_playericon)
	protected ImageView _playerIcon;

	private String _playerName;
	private int _color;
	private Context _context;

	public PlayerItem(Context context) {
		super(context);
	}

	public PlayerItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayerItem(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setPlayerName(String value) {
		_playerName = value;

		if(_playerText != null) {
			_playerText.setText(_playerName);
		}
	}

	public void setPlayerColor(int value) {
		_color = value;

		if(_playerIcon != null) {
			_playerIcon.setColorFilter(value);
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.inject(this, this);

		_playerText.setText(_playerName);
		_playerIcon.setColorFilter(_color);
	}
}
