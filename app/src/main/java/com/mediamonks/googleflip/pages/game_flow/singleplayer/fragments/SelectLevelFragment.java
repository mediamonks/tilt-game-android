package com.mediamonks.googleflip.pages.game_flow.singleplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.vo.LevelResultVO;
import com.mediamonks.googleflip.data.vo.LevelVO;
import com.mediamonks.googleflip.pages.game.FlipGameActivity;
import com.mediamonks.googleflip.pages.game_flow.singleplayer.adapters.LevelAdapter;
import com.mediamonks.googleflip.pages.home.HomeActivity;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.mediamonks.googleflip.util.SoundManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment for selecting a level
 */
public class SelectLevelFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = SelectLevelFragment.class.getSimpleName();

    @Bind(R.id.list)
    protected ListView _list;

    public static SelectLevelFragment newInstance() {
        return new SelectLevelFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(R.layout.fragment_select_level, inflater, container);
        ButterKnife.bind(this, view);

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int itemHeight;
        int rows = 8;

        do {
            itemHeight = (screenHeight / rows) - 1;
            rows--;
        } while (itemHeight < 90 * getResources().getDisplayMetrics().density);

        List<LevelVO> levels = new ArrayList<>();
        levels.add(new LevelVO());

        if (GoogleFlipGameApplication.getUserModel().getLevels() == null) {
            getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));
            return null;
        }
        levels.addAll(GoogleFlipGameApplication.getUserModel().getLevels());

        List<LevelResultVO> results = new ArrayList<>();
        results.addAll(GoogleFlipGameApplication.getUserModel().getLevelResults());

        LevelAdapter adapter = new LevelAdapter(getActivity(), levels, itemHeight, results);
        _list.setAdapter(adapter);
        _list.setOnItemClickListener(this);
        _list.setSelection(GoogleFlipGameApplication.getUserModel().getSelectedLevelIndex());

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SoundManager.getInstance().play(R.raw.tap);

        startLevel(position);
    }

    private void startLevel(int position) {
        final Intent intent = new Intent(getActivity(), FlipGameActivity.class);
        if (position == 0) {
            intent.putExtra(FlipGameActivity.ARG_TUTORIAL_LEVEL, 0);
        } else {
            GoogleFlipGameApplication.getUserModel().selectLevelByIndex(position - 1);
        }
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_down_in, R.anim.no_change);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }
}
