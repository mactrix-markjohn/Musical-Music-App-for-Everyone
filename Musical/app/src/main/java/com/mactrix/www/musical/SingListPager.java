package com.mactrix.www.musical;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Mactrix on 2/20/2018.
 */

public class SingListPager extends FragmentPagerAdapter {
    int tabCount;

    public SingListPager(FragmentManager fm,int numberofPage) {
        super(fm);
        tabCount = numberofPage;
    }

    @Override
    public Fragment getItem(int position) {
        //Fragment instruemental = new Instrumental();
        Fragment myOwnSongs = new MyOwnSongs();

        switch (position){
            case 0:return myOwnSongs;

            default:return myOwnSongs ;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
