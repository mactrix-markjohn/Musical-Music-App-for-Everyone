package com.mactrix.www.musical;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Mactrix on 2/6/2018.
 */

public class TabPagerAdapter extends FragmentPagerAdapter {
    int tabCount;
    AppCompatActivity compatActivity;

    public TabPagerAdapter(FragmentManager fm, int numberOfTab) {
        super(fm);
        tabCount=numberOfTab;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment songList = new SongList();
        Fragment artistList = new ArtistFragment();
        Fragment albumList = new AlbumFragment();
        Fragment playList = new PlaylistPrime();
        switch (position){
            case 0:return songList;
            case 1:return artistList;
            case 2:return albumList;
            case 3:return playList;
            default:return songList ;
        }


    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
