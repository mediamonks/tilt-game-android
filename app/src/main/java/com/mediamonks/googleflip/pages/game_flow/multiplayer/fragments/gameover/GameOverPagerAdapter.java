package com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.gameover;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mediamonks.googleflip.pages.game_flow.multiplayer.ui.PlayerNamesPage;

/**
 * Multiplayer game over viewpager page adapter
 */
public class GameOverPagerAdapter extends FragmentPagerAdapter {
    public GameOverPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return GameOverResultPage.newInstance();
            case 1:
                return PlayerNamesPage.newInstance();
        }

        return null;
    }


    @Override
    public int getCount() {
        return 2;
    }
}
