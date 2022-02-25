package com.mactrix.www.musical;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Mactrix on 2/9/2018.
 */

public class SongList extends Fragment {
    boolean isServiceBound;

    Context context;
    ListView listView;
    private ArrayList<MySongs> mySongs;
    private Intent serviceIntent;
    private SongListAdapter songListAdapter;
    private MyMusicService musicService;
    int position;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
        serviceIntent = new Intent(context,MyMusicService.class);
        context.bindService(serviceIntent,serviceConnection,BIND_AUTO_CREATE);
        registerStartBroadcast();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.songlist_fragment,container,false);

        listView = (ListView)view.findViewById(R.id.songlistview);


        return view;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        regiterResuming();

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);



        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            loadSongs();
            songListAdapter = new SongListAdapter(context,mySongs,musicService);
            listView.setAdapter(songListAdapter);

        }


    }

    @Override
    public void onResume(){
        super.onResume();
        listView.setAdapter(songListAdapter);
        listView.setVerticalScrollbarPosition(position);
    }
    @Override
    public void onStart(){
        super.onStart();
        listView.setAdapter(songListAdapter);

    }

    @Override
    public void onSaveInstanceState(Bundle savedState){
        super.onSaveInstanceState(savedState);
    }




    public void loadSongs(){
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri ari= MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;










        String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
        String sortOrder = MediaStore.Audio.Media.TITLE+"ASC";

        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        Cursor acursor= contentResolver.query(ari,null,null,null,null);
        String albumart="";
        String albuming="";

        if(cursor!=null &&cursor.getCount()>0){
            mySongs = new ArrayList<>();

            for(int i=0,j=0;i<cursor.getCount();j++,i++){
                cursor.moveToPosition(i);

                if(!acursor.isLast()) {
                    acursor.moveToPosition(i);

                }

                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String albumKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));
                if(!acursor.isLast()) {
                    albumart = acursor.getString(acursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    albuming = acursor.getString(acursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_KEY));
                }




                //String albumcolumn = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.));
                //String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                //String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                mySongs.add(new MySongs(data,title,album,artist,albumart,albuming,albumKey));


            }

            Collections.sort(mySongs, new Comparator<MySongs>() {
                @Override
                public int compare(MySongs o1, MySongs o2) {
                    return o1.getTitle().compareToIgnoreCase(o2.getTitle());
                }
            });

        }

        cursor.close();


    }



    public ServiceConnection serviceConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyMusicService.LocalBinder localBinder = (MyMusicService.LocalBinder)service;
            musicService = localBinder.getService();
            isServiceBound = true;


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;

        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
       // SharedPref sharedPref = new SharedPref(context,"ServiceControl");
        //sharedPref.setInt(-1);
        context.unregisterReceiver(start);
        context.unregisterReceiver(resuming);
        context.unbindService(serviceConnection);
    }

    public void registerStartBroadcast(){
        IntentFilter filter = new IntentFilter("start");
        context.registerReceiver(start,filter);



    }

    private final BroadcastReceiver start = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //onResume();
        }
    };

    private final BroadcastReceiver resuming = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            position = listView.getVerticalScrollbarPosition();
            //onResume();
        }
    };

    public void regiterResuming(){
        IntentFilter intentFilter = new IntentFilter("resuming");
        context.registerReceiver(resuming,intentFilter);
    }


}
