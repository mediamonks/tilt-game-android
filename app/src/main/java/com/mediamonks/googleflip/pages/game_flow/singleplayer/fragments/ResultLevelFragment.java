package com.mediamonks.googleflip.pages.game_flow.singleplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.Fragments;
import com.mediamonks.googleflip.data.constants.IntentKeys;
import com.mediamonks.googleflip.data.constants.LevelResult;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.data.vo.LevelResultVO;
import com.mediamonks.googleflip.pages.game.FlipGameActivity;
import com.mediamonks.googleflip.pages.game_flow.singleplayer.SinglePlayerGameFlowActivity;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.mediamonks.googleflip.ui.animation.LargeAnimatedTextView;
import com.mediamonks.googleflip.ui.animation.SmallAnimatedTextView;
import com.mediamonks.googleflip.util.LevelColorUtil;
import com.mediamonks.googleflip.util.SoundManager;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import temple.core.ui.CustomButton;

/**
 * Fragment for showing level result
 */
public class ResultLevelFragment extends BaseFragment {
	private static final String TAG = ResultLevelFragment.class.getSimpleName();
	private static final int[] LOST_TEXTS = {R.string.lose1, R.string.lose2, R.string.lose3, R.string.lose4, R.string.lose5};

	@InjectView(R.id.score_label1)
	protected LargeAnimatedTextView _scoreLabel1;
	@InjectView(R.id.score_label2)
	protected LargeAnimatedTextView _scoreLabel2;
	@InjectView(R.id.record_label)
	protected SmallAnimatedTextView _recordLabel;
	@InjectView(R.id.next_button)
	protected CustomButton _nextButton;

	private Boolean _success;
	private boolean _isTutorial;
	private LevelResult _levelResult;

	public static ResultLevelFragment newInstance() {
		return new ResultLevelFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = createView(R.layout.fragment_result_level, inflater, container);
		view.setBackgroundColor(LevelColorUtil.fromLevelColor(GoogleFlipGameApplication.getUserModel().getCurrentBackgroundColor()));
		ButterKnife.inject(this, view);

		Bundle extras = getActivity().getIntent().getExtras();
		if (extras != null) {
			_success = extras.getBoolean(IntentKeys.RESULT_SUCCESS);
			float time = extras.getFloat(IntentKeys.RESULT_TIME);
			Long levelId = extras.getLong(IntentKeys.RESULT_LEVEL_ID);
			_isTutorial = extras.getBoolean(IntentKeys.IS_TUTORIAL);


			if (_isTutorial) {
				GoogleFlipGameApplication.getUserModel().unlockLevel(0);
				_levelResult = LevelResult.NEW;

				if (_success) {
					_scoreLabel1.setText(String.format("%.1f", time) + "s");
				} else {
					_scoreLabel1.setText(getResources().getString(R.string.done));
				}

				_scoreLabel2.setVisibility(View.GONE);

				_recordLabel.setText(getResources().getString(R.string.to_first_level));
				_recordLabel.setVisibility(View.VISIBLE);
			} else {
				if (_success) {
					_scoreLabel1.setText(String.format("%.1f", time) + "s");

					_nextButton.setVisibility(GoogleFlipGameApplication.getUserModel().hasNextLevel() ? View.VISIBLE : View.GONE);

					_levelResult = GoogleFlipGameApplication.getUserModel().updateLevelResult(new LevelResultVO(levelId, time, _success));

					if (!GoogleFlipGameApplication.getUserModel().hasNextLevel() && !Prefs.getBoolean(PrefKeys.LEVELS_COMPLETE, false)) {
						Prefs.putBoolean(PrefKeys.LEVELS_COMPLETE, true);

						_scoreLabel1.setText(getResources().getString(R.string.yay));
						_scoreLabel2.setText(getResources().getString(R.string.you_won));
						_recordLabel.setVisibility(View.GONE);
					} else {
						_scoreLabel2.setVisibility(View.GONE);

						// update score
						switch (_levelResult) {
							case BETTER:
								_recordLabel.setText(getResources().getString(R.string.new_record));
								break;
							case NEW:
								_recordLabel.setVisibility(View.GONE);
								break;
							case WORSE:
								Float bestTime = GoogleFlipGameApplication.getUserModel().getResultForLevel(levelId).seconds;
								_recordLabel.setText(getResources().getString(R.string.your_record) + " " + String.format("%.1f", bestTime) + "s");
								break;
						}
					}
				} else {
					_levelResult = LevelResult.FAIL;

					String str = getResources().getString(LOST_TEXTS[(int) (Math.floor(Math.random() * LOST_TEXTS.length))]);

					if (str.contains("\n")) {
						_scoreLabel1.setText(str.split("\n")[0]);
						_scoreLabel2.setText(str.split("\n")[1]);
					} else {
						_scoreLabel1.setText(str);
						_scoreLabel2.setVisibility(View.GONE);
					}

					_recordLabel.setVisibility(View.GONE);
					_nextButton.setText(getResources().getString(R.string.play_again));
				}
			}

			_scoreLabel1.show();

			if (_scoreLabel2.getVisibility() == View.VISIBLE) {
				_scoreLabel2.show(200);
			}

			if (_recordLabel.getVisibility() == View.VISIBLE) {
				_recordLabel.show(500);
			}
		}

		return view;
	}

	@OnClick(R.id.exit_button)
	protected void onExitButtonClick() {
		SoundManager.getInstance().play(R.raw.tap);

		Intent intent = new Intent(getActivity(), SinglePlayerGameFlowActivity.class);
		intent.putExtra(IntentKeys.FRAGMENT, Fragments.GAME_FLOW_SELECT_LEVEL);

		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.no_change, R.anim.slide_up_out);
	}

	@OnClick(R.id.next_button)
	protected void onNextButtonClick() {
		SoundManager.getInstance().play(R.raw.tap);

		if (_isTutorial) {
			GoogleFlipGameApplication.getUserModel().selectLevelByIndex(0);
		} else if (_success) {
			GoogleFlipGameApplication.getUserModel().selectNextLevel();
		}

		startActivity(new Intent(getActivity(), FlipGameActivity.class));
		getActivity().overridePendingTransition(R.anim.slide_down_in, R.anim.no_change);
	}

	@Override
	public void onResume() {
		super.onResume();

		switch (_levelResult) {
			case BETTER:
				SoundManager.getInstance().play(R.raw.high_score);
				break;
			case NEW:
			case WORSE:
				SoundManager.getInstance().play(R.raw.level_done);
				break;
			case FAIL:
				break;
		}
	}

	@Override
	public void onDestroy() {
		_levelResult = null;

		super.onDestroy();
	}
}
