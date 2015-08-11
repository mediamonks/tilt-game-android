package com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.scoreboard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mediamonks.googleflip.pages.game_flow.multiplayer.ui.PlayerNamesPage;

/**
 * Viewpager adapter for scoreboard pages
 */
public class ScoreboardPagerAdapter extends FragmentPagerAdapter {
    public ScoreboardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PlayerNamesPage.newInstance();
            case 1:
                return ScoreboardPlayerTimesPage.newInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
