package com.mactrix.www.musical;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.service.restrictions.RestrictionsReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by Mactrix on 2/15/2018.
 */

public class PlaylistPrime extends Fragment {

    ListView playList;
    RelativeLayout playrelative;
    Button defaultAdd;
    FloatingActionButton playlistAdd;
    RelativeLayout defaultLayout;
    Context context;
    MainPlayListDataBase mainPlayListDataBase;
    PlaylistDatabase playlistDatabase;
    Cursor cursor;
    PlayListAdapter playListAdapter;
    EditText insertSpace;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
     View v = inflater.inflate(R.layout.the_playlist,parent,false);

     playList =(ListView)v.findViewById(R.id.playlist);
     playrelative= (RelativeLayout)v.findViewById(R.id.playrelative);
     defaultLayout = (RelativeLayout)v.findViewById(R.id.playlistdefault);
     defaultAdd = (Button)v.findViewById(R.id.playlistdefaultaddbotton);
     playlistAdd = (FloatingActionButton)v.findViewById(R.id.playlistfloatadd);


     return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mainPlayListDataBase = new MainPlayListDataBase(context,null,null,1);
        cursor = mainPlayListDataBase.getAllSongs();
        playListAdapter = new PlayListAdapter(context,cursor,mainPlayListDataBase);
        playList.setAdapter(playListAdapter);
        regiterResuming();

        if(playList.getCount()==0){
            playrelative.setVisibility(View.GONE);
            defaultLayout.setVisibility(View.VISIBLE);
        }else{
            defaultLayout.setVisibility(View.GONE);
            playrelative.setVisibility(View.VISIBLE);
        }


        defaultAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDialog();
            }
        });

        playlistAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDialog();
            }
        });








    }

    @Override
    public void onResume(){
        super.onResume();
        cursor = mainPlayListDataBase.getAllSongs();
        playListAdapter = new PlayListAdapter(context,cursor,mainPlayListDataBase);
        playList.setAdapter(playListAdapter);

        if(playList.getCount()==0){
            playrelative.setVisibility(View.GONE);
            defaultLayout.setVisibility(View.VISIBLE);
        }else{
            defaultLayout.setVisibility(View.GONE);
            playrelative.setVisibility(View.VISIBLE);
        }


    }
    @Override
    public void onStart(){
        super.onStart();
        cursor = mainPlayListDataBase.getAllSongs();
        playListAdapter = new PlayListAdapter(context,cursor,mainPlayListDataBase);
        playList.setAdapter(playListAdapter);

        if(playList.getCount()==0){
            playrelative.setVisibility(View.GONE);
            defaultLayout.setVisibility(View.VISIBLE);
        }else{
            defaultLayout.setVisibility(View.GONE);
            playrelative.setVisibility(View.VISIBLE);
        }
    }

    public void insertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Playlist name");
        insertSpace = new EditText(context);
        builder.setView(insertSpace);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.cancel();
            }
        });
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playTitle = String.valueOf(insertSpace.getText());
                mainPlayListDataBase.insertSong(playTitle);
                playlistDatabase = new PlaylistDatabase(context,null,null,1,playTitle);
                onResume();
            }
        });


        builder.create().show();
    }

  private final BroadcastReceiver resuming = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
          onResume();
      }
  };

    public void regiterResuming(){
        IntentFilter intentFilter = new IntentFilter("resuming");
        context.registerReceiver(resuming,intentFilter);
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        //context.unregisterReceiver(resuming);
    }


}
