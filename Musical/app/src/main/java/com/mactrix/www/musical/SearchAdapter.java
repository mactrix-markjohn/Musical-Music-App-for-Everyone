package com.mactrix.www.musical;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.PopupMenu;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mactrix on 2/17/2018.
 */

public class SearchAdapter extends BaseAdapter {
    private final Intent player;
    private final SharedPref sharedPref;
    private final SharedPref place;
    LayoutInflater inflater;
    Context context;
    int pose=0;
    ArrayList<String> datas;
    ArrayList<String> titles;
    ArrayList<String> artists;
    ArrayList<String> albums;
    ArrayList<String> albumArt;
    SharedPref singConst;

    enum Play{PLAY,PAUSE,RESUME}
    Play play;



    public SearchAdapter(Context context,ArrayList<String> data,ArrayList<String> title,ArrayList<String> artist,ArrayList<String> album,ArrayList<String> albumArt){
        this.context = context;
        inflater = LayoutInflater.from(context);
        datas = data;
        titles = title;
        artists = artist;
        albums = album;
        this.albumArt = albumArt;

        player  = new Intent(context,MyMusicService.class);
        context.bindService(player,serviceConnection,context.BIND_AUTO_CREATE);
        sharedPref = new SharedPref(context,"ServiceControl");
        place =new SharedPref(context,"Place");
    }

    private class ViewHolder{

        private final ImageView songCircleImage;
        private final ImageView songMenu;
        private final TextView songTitle;
        private final TextView songArtist;
        private final TextView songAlbum;
        private final ImageView songFloatPlay;
        int counting =0;


        int count=0;

        public ViewHolder(final View view, final int position){

            play = Play.PLAY;

            songCircleImage = (ImageView)view.findViewById(R.id.songcircleimage);
            songMenu = (ImageView)view.findViewById(R.id.songmenu);
            songTitle = (TextView)view.findViewById(R.id.songtitle);
            songArtist =(TextView)view.findViewById(R.id.songartist);
            songAlbum = (TextView)view.findViewById(R.id.songalbum);
            songFloatPlay = (ImageView)view.findViewById(R.id.songfloatplay);


            songTitle.setText(titles.get(position));
            songArtist.setText(artists.get(position));
            songAlbum.setText(albums.get(position));





            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context,v);
                    popupMenu.inflate(R.menu.popup);
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.playing: //Intent playerIntent = new Intent(context, MyMusicService.class);

                                  /*  Bundle songBundle = new Bundle();
                                    songBundle.putString(context.getResources().getString(R.string.songdata), data);
                                    songBundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);
                                    songBundle.putInt(context.getResources().getString(R.string.songindex), position);

                                    playerIntent.putExtra(context.getResources().getString(R.string.songbundle), songBundle);
                                    context.startService(playerIntent);*/
                                    musicService.reset();
                                    musicService.playMedia(position);
                                    play=Play.PAUSE;
                                    songFloatPlay.setImageResource(R.drawable.pausing);

                                    //play=Play.PAUSE;
                                    songCircleImage.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                                    break;
                                case R.id.gotoartist:singConst = new SharedPref(context,context.getResources().getString(R.string.singConst));
                                    if(singConst.getInt()!=10){
                                        musicService.pauseMedia();
                                        Bundle bundle = new Bundle();
                                        Intent intent = new Intent(context,SingingStudio.class);

                                        bundle.putString("singtitle",titles.get(position));
                                        bundle.putString("singartist",artists.get(position));
                                        bundle.putString("singdata",datas.get(position));
                                        bundle.putString("singalbum",albums.get(position));

                                        intent.putExtra("singingstudio",bundle);
                                        context.startActivity(intent);
                                        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
                                        notificationManager.cancel(101);
                                    }else{
                                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                                        builder.setTitle("Notice!!!");
                                        builder.setIcon(R.drawable.cancelhead);
                                        builder.setMessage("Sorry, this feature you want to access is currently Locked.\n" +
                                                "Click the 'Unlock Button' to Unlock this feature. Thank you");
                                        builder.setPositiveButton("Unlock", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                context.startActivity(new Intent(context,PurchaseRoom.class));
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        builder.create().show();
                                    }
                                    break;

                                case R.id.addtoplaylist: playDialog(position);break;
                            }

                            return true;
                        }
                    });


                    return true;
                }
            });

            songMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(context, "It is working", Toast.LENGTH_SHORT).show();
                    PopupMenu popupMenu = new PopupMenu(context,v);
                    popupMenu.inflate(R.menu.popup);
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.playing: //Intent playerIntent = new Intent(context, MyMusicService.class);

                                  /*  Bundle songBundle = new Bundle();
                                    songBundle.putString(context.getResources().getString(R.string.songdata), data);
                                    songBundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);
                                    songBundle.putInt(context.getResources().getString(R.string.songindex), position);

                                    playerIntent.putExtra(context.getResources().getString(R.string.songbundle), songBundle);
                                    context.startService(playerIntent);*/
                                    musicService.reset();
                                    musicService.playMedia(position);
                                    play= Play.PAUSE;
                                    songFloatPlay.setImageResource(R.drawable.pausing);

                                    //play=Play.PAUSE;
                                    songCircleImage.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                                    break;
                                case R.id.gotoartist:singConst = new SharedPref(context,context.getResources().getString(R.string.singConst));
                                    if(singConst.getInt()==10){
                                        musicService.pauseMedia();
                                        Bundle bundle = new Bundle();
                                        Intent intent = new Intent(context,SingingStudio.class);

                                        bundle.putString("singtitle",titles.get(position));
                                        bundle.putString("singartist",artists.get(position));
                                        bundle.putString("singdata",datas.get(position));
                                        bundle.putString("singalbum",albums.get(position));

                                        intent.putExtra("singingstudio",bundle);
                                        context.startActivity(intent);
                                        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
                                        notificationManager.cancel(101);
                                    }else{
                                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                                        builder.setTitle("Notice!!!");
                                        builder.setIcon(R.drawable.cancelhead);
                                        builder.setMessage("Sorry, this feature you want to access is currently Locked.\n" +
                                                "Click the 'Unlock Button' to Unlock this feature. Thank you");
                                        builder.setPositiveButton("Unlock", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                context.startActivity(new Intent(context,PurchaseRoom.class));
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        builder.create().show();
                                    }
                                    break;
                                case R.id.addtoplaylist:playDialog(position);break;
                            }

                            return true;
                        }
                    });

                }
            });


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(musicService.isPlaying()&&musicService.getCurrentIndex()==position){


                        if(place.getInt()==4) {
                            Intent intent = new Intent(context, MusicPlace.class);

                            Bundle bundle = new Bundle();

                            bundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);

                            bundle.putInt(context.getResources().getString(R.string.songindex), position);


                            bundle.putStringArrayList(context.getResources().getString(R.string.titlearray), titles);


                            bundle.putStringArrayList(context.getResources().getString(R.string.artistarray), artists);


                            bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArt);


                            bundle.putStringArrayList(context.getResources().getString(R.string.albumarray), albums);

                            intent.putExtra(context.getResources().getString(R.string.songbundle), bundle);


                            // context.stopService(player);
                            context.startActivity(intent);
                        }else{

                            if(counting==0){

                                Intent sendIntent = new Intent(context.getResources().getString(R.string.servicesetup));

                                Bundle songBundle = new Bundle();
                                //songBundle.putString(context.getResources().getString(R.string.songdata),data);
                                songBundle.putStringArrayList(context.getResources().getString(R.string.songarray),datas);
                                //songBundle.putInt(context.getResources().getString(R.string.songindex),position);
                                songBundle.putStringArrayList(context.getResources().getString(R.string.albumarray),albums);
                                songBundle.putStringArrayList(context.getResources().getString(R.string.artistarray),artists);
                                songBundle.putStringArrayList(context.getResources().getString(R.string.albumartarray),albumArt);
                                songBundle.putStringArrayList(context.getResources().getString(R.string.titlearray),titles);


                                sendIntent.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                                context.sendBroadcast(sendIntent);

                            }



                            Intent intent = new Intent(context, MusicPlace.class);

                            Bundle bundle = new Bundle();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    musicService.playMedia(position);
                                }
                            }, 300);
                            place.setInt(4);


                            bundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);

                            bundle.putInt(context.getResources().getString(R.string.songindex), position);


                            bundle.putStringArrayList(context.getResources().getString(R.string.titlearray), titles);


                            bundle.putStringArrayList(context.getResources().getString(R.string.artistarray), artists);


                            bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArt);


                            bundle.putStringArrayList(context.getResources().getString(R.string.albumarray), albums);

                            intent.putExtra(context.getResources().getString(R.string.songbundle), bundle);


                            // context.stopService(player);
                            context.startActivity(intent);
                            counting++;

                        }


                        // context.startActivity(new Intent(context,MusicPlace.class));
                    }else {


                        //sharedPref.setInt(1);

                        if(counting==0){

                            Intent sendIntent = new Intent(context.getResources().getString(R.string.servicesetup));

                            Bundle songBundle = new Bundle();
                            //songBundle.putString(context.getResources().getString(R.string.songdata),data);
                            songBundle.putStringArrayList(context.getResources().getString(R.string.songarray),datas);
                            //songBundle.putInt(context.getResources().getString(R.string.songindex),position);
                            songBundle.putStringArrayList(context.getResources().getString(R.string.albumarray),albums);
                            songBundle.putStringArrayList(context.getResources().getString(R.string.artistarray),artists);
                            songBundle.putStringArrayList(context.getResources().getString(R.string.albumartarray),albumArt);
                            songBundle.putStringArrayList(context.getResources().getString(R.string.titlearray),titles);

        /*player.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
        contxt.startService(player);*/
                            sendIntent.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                            context.sendBroadcast(sendIntent);

                        }



                        Intent intent = new Intent(context, MusicPlace.class);

                        Bundle bundle = new Bundle();




                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                musicService.playMedia(position);
                            }
                        }, 300);
                        place.setInt(4);



                    /*player.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                    context.startService(player);*/


                        bundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);

                        bundle.putInt(context.getResources().getString(R.string.songindex), position);


                        bundle.putStringArrayList(context.getResources().getString(R.string.titlearray), titles);


                        bundle.putStringArrayList(context.getResources().getString(R.string.artistarray), artists);


                        bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArt);


                        bundle.putStringArrayList(context.getResources().getString(R.string.albumarray), albums);

                        intent.putExtra(context.getResources().getString(R.string.songbundle), bundle);


                        // context.stopService(player);
                        context.startActivity(intent);
                        counting++;
                    }

                }
            });

            songFloatPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(musicService.isPlaying()&&musicService.getCurrentIndex()==position){
                        //play = Play.PAUSE;

                    }


                    if(!musicService.isPlaying()/*&&play==Play.PLAY*/) {


                        if(counting==0){

                            Intent sendIntent = new Intent(context.getResources().getString(R.string.servicesetup));

                            Bundle songBundle = new Bundle();
                            //songBundle.putString(context.getResources().getString(R.string.songdata),data);
                            songBundle.putStringArrayList(context.getResources().getString(R.string.songarray),datas);
                            //songBundle.putInt(context.getResources().getString(R.string.songindex),position);
                            songBundle.putStringArrayList(context.getResources().getString(R.string.albumarray),albums);
                            songBundle.putStringArrayList(context.getResources().getString(R.string.artistarray),artists);
                            songBundle.putStringArrayList(context.getResources().getString(R.string.albumartarray),albumArt);
                            songBundle.putStringArrayList(context.getResources().getString(R.string.titlearray),titles);

        /*player.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
        contxt.startService(player);*/
                            sendIntent.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                            context.sendBroadcast(sendIntent);

                        }


                        if(play== Play.PLAY) {
                            pose=position;
                            musicService.reset();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    musicService.playMedia(position);
                                }
                            }, 300);
                            place.setInt(4);

                            play= Play.RESUME;

                        }else if(play== Play.RESUME){
                            songFloatPlay.setImageResource(R.drawable.pausing);

                            if(pose==position&&place.getInt()==4) {
                                musicService.resumeMedia();
                            }else{
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        musicService.playMedia(position);
                                    }
                                }, 300);
                                place.setInt(4);



                            }
                            //play=Play.PAUSE;
                            //view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                        }
                        songFloatPlay.setImageResource(R.drawable.pausing);

                        //play=Play.PAUSE;
                        songCircleImage.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                        counting++;
                    }else if(musicService.isPlaying()/*play==Play.PAUSE*/) {
                        songFloatPlay.setImageResource(R.drawable.playingshit);




                        musicService.pauseMedia();

                        songCircleImage.setBackgroundColor(context.getResources().getColor(R.color.transperent));
                        //play=Play.RESUME;
                    }/*else if(play==Play.RESUME){
                        songFloatPlay.setImageResource(R.drawable.pause);

                        if(pose==position) {
                            musicService.resumeMedia();
                        }else{
                            musicService.playMedia(position);

                        }
                        play=Play.PAUSE;
                        view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                    }*/



                }
            });

            if(isServiceBound){
                if(musicService.isPlaying()){
                    if(musicService.getCurrentIndex()==position&&place.getInt()==4){
                        songCircleImage.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                        songFloatPlay.setImageResource(R.drawable.pausing);
                    }
                }else if(!musicService.isPlaying()){
                    songFloatPlay.setImageResource(R.drawable.playingshit);
                }
            }

            /*if(musicService.isPlaying()){
                if(musicService.getCurrentIndex()==position){
                    view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                }
            }*/


        }

        }



    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {

            convertView = inflater.inflate(R.layout.searchlist,parent,false);
            new ViewHolder(convertView,position);

        }catch (InflateException e){
            convertView = inflater.inflate(R.layout.searchlist,parent,false);
            new ViewHolder(convertView,position);

        }catch (Exception e){
            convertView = inflater.inflate(R.layout.searchlist,parent,false);
            new ViewHolder(convertView,position);

        }

        return convertView;
    }

    MyMusicService musicService;
    boolean isServiceBound;
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
            // context.unbindService(serviceConnection);

        }
    };

    public void playDialog(final int positioning){

        ListView listView = new ListView(context);
        final ArrayAdapter arrayAdapter = new ArrayAdapter(context,R.layout.playtext,R.id.playlisttextlist);
        MainPlayListDataBase mainPlayListDataBase = new MainPlayListDataBase(context,null,null,1);
        final Cursor cursor = mainPlayListDataBase.getAllSongs();

        if(cursor!=null){
            if(cursor.getCount()>0){
                for (int i=0;i<cursor.getCount();i++){
                    cursor.moveToPosition(i);

                    String text = cursor.getString(cursor.getColumnIndex(MainPlayListDataBase.TITLE));
                    arrayAdapter.add(text);

                }
            }
        }

        listView.setAdapter(arrayAdapter);
        listView.setPadding(5,1,0,0);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(cursor!=null){
                    if(cursor.getCount()>0){
                        cursor.moveToPosition(position);


                        String text = cursor.getString(cursor.getColumnIndex(MainPlayListDataBase.TITLE));
                        String take="";
                        StringBuffer stringBuffer = new StringBuffer();
                        for(int i=0;i<text.toCharArray().length;i++){
                            char bind = text.charAt(i);

                            if(bind!=' '){
                                stringBuffer.append(bind);
                            }
                        }

                        take = stringBuffer.toString();
                        String taking = take+".db";

                        PlaylistDatabase playlistDatabase = new PlaylistDatabase(context,null,null,1,taking);
                        playlistDatabase.insertSong(datas.get(positioning),titles.get(positioning),artists.get(positioning),albums.get(positioning),albumArt.get(positioning));
                        Toast.makeText(context, "Song Added to Playlist, you can Exit now.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });





        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle("Selete Playlist");
        builder.setIcon(R.drawable.musiv);
        builder.setView(listView);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();





    }

}
