package com.mactrix.www.musical;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mactrix on 2/6/2018.
 */

public class SongListAdapter extends BaseAdapter {

    SharedPref singConst;
    Context context;
    ArrayList<MySongs> songs;
    MyMusicService musicService;
    LayoutInflater inflater;
    ArrayList<String> datas;
    ArrayList<String> titles;
    ArrayList<String> artists;
    ArrayList<String> albums;
    ArrayList<String> albumArts;
    ArrayList<String> albuming;
    ArrayList<String> albumKey;
    enum Play{PLAY,PAUSE,RESUME}
    Play play;
    Intent player;
    boolean isServiceBound;
    int pose=0;
    SharedPref sharedPref;
    SharedPref place;

    int share=0;

    SharedPref point;





    public SongListAdapter(Context contxt, ArrayList<MySongs> song, MyMusicService service){

        context=contxt;
        songs=song;
        //musicService=service;

        inflater=LayoutInflater.from(context);



        player  = new Intent(contxt,MyMusicService.class);
        contxt.bindService(player,serviceConnection,contxt.BIND_AUTO_CREATE);
        sharedPref = new SharedPref(contxt,"ServiceControl");
        place =new SharedPref(contxt,"Place");
        point = new SharedPref(contxt,"point");


        datas = new ArrayList<>();
        titles = new ArrayList<>();
        artists = new ArrayList<>();
        albums = new ArrayList<>();
        albumArts = new ArrayList<>();
        albuming=new ArrayList<>();
        albumKey = new ArrayList<>();

        if(sharedPref.getInt()==-1){
            sharedPref.setInt(share);
        }

        for(int i=0;i<song.size();i++){
            datas.add(song.get(i).getData());
            titles.add(song.get(i).getTitle());
            artists.add(song.get(i).getArtist());
            albums.add(song.get(i).getAlbum());
            albumArts.add(song.get(i).getAlbumArt());
            albuming.add(song.get(i).getAlbuming());
            albumKey.add(song.get(i).getAlbumKey());
        }

        if(sharedPref.getInt()==0) {

       Intent sendIntent = new Intent(contxt.getResources().getString(R.string.servicesetup));

        Bundle songBundle = new Bundle();
        //songBundle.putString(context.getResources().getString(R.string.songdata),data);
        songBundle.putStringArrayList(context.getResources().getString(R.string.songarray),datas);
        //songBundle.putInt(context.getResources().getString(R.string.songindex),position);
        songBundle.putStringArrayList(context.getResources().getString(R.string.albumarray),albums);
        songBundle.putStringArrayList(context.getResources().getString(R.string.artistarray),artists);
        songBundle.putStringArrayList(context.getResources().getString(R.string.albumartarray),albumArts);
        songBundle.putStringArrayList(context.getResources().getString(R.string.titlearray),titles);

        /*player.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
        contxt.startService(player);*/
        sendIntent.putExtra(contxt.getResources().getString(R.string.songbundle),songBundle);
        //contxt.sendBroadcast(sendIntent);
        }

        share=sharedPref.getInt();
        share++;
        sharedPref.setInt(share);

    }


    private class ViewHolder{
        CircleImageView songCircleImage;
        ImageView songMenu;
        TextView songTitle;
        TextView songArtist;
        TextView songAlbum;
        ImageView songFloatPlay;
        String data;
        String artist;
        String albumArt;
        String album;
        String title;
        int counting =0;


        int count=0;


        public ViewHolder(final View view, final int position){

            play = Play.PLAY;



            songCircleImage = (CircleImageView)view.findViewById(R.id.songcircleimage);
            songMenu = (ImageView)view.findViewById(R.id.songmenu);
            songTitle = (TextView)view.findViewById(R.id.songtitle);
            songArtist =(TextView)view.findViewById(R.id.songartist);
            songAlbum = (TextView)view.findViewById(R.id.songalbum);
            songFloatPlay = (ImageView)view.findViewById(R.id.songfloatplay);

            data = datas.get(position);
            artist = artists.get(position);
            albumArt = albumArts.get(position);
            album = albums.get(position);
            title = titles.get(position);



           // for(int i=0;i<albuming.size();i++){
              // if(albumKey.get(position)==albuming.get(i)){
           // Innering innering = new Innering(position);
            //innering.execute();
                   // songCircleImage.setImageBitmap(decodeAlbumArt(datas.get(position)));
            if(decodeAlbumArt(datas.get(position))!=null){
                Glide.with(context).load(decodeAlbumArt(datas.get(position))).into(songCircleImage);
            }else{
                Glide.with(context).load(R.mipmap.playing).into(songCircleImage);
            }

                  //  break;
               // }
          // }

           // songCircleImage.setImageBitmap(decodeAlbumArt(albumArt));


            songTitle.setText(title);
            songArtist.setText(artist);
            songAlbum.setText(album);


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
                                    point.setText(title);
                                    play=Play.PAUSE;
                                    songFloatPlay.setImageResource(R.drawable.pausing);

                                    //play=Play.PAUSE;
                                    //view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                                    break;
                                case R.id.gotoartist:
                                    singConst = new SharedPref(context,context.getResources().getString(R.string.singConst));
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
                                  point.setText(title);
                                    play=Play.PAUSE;
                                    songFloatPlay.setImageResource(R.drawable.pausing);

                                    //play=Play.PAUSE;
                                    //view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                                    break;
                                case R.id.gotoartist:  singConst = new SharedPref(context,context.getResources().getString(R.string.singConst));
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


                        if(place.getInt()==1) {
                            Intent intent = new Intent(context, MusicPlace.class);

                            Bundle bundle = new Bundle();
                            bundle.putString(context.getResources().getString(R.string.songdata), data);
                            bundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);

                            bundle.putInt(context.getResources().getString(R.string.songindex), position);

                            bundle.putString(context.getResources().getString(R.string.title), title);
                            bundle.putStringArrayList(context.getResources().getString(R.string.titlearray), titles);

                            bundle.putString(context.getResources().getString(R.string.artist), artist);
                            bundle.putStringArrayList(context.getResources().getString(R.string.artistarray), artists);

                            bundle.putString(context.getResources().getString(R.string.albumart), albumArt);
                            bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArts);

                            bundle.putString(context.getResources().getString(R.string.album), album);
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
                                songBundle.putStringArrayList(context.getResources().getString(R.string.albumartarray),albumArts);
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
                                    point.setText(title);
                                }
                            }, 300);
                            place.setInt(1);

                            bundle.putString(context.getResources().getString(R.string.songdata), data);
                            bundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);

                            bundle.putInt(context.getResources().getString(R.string.songindex), position);

                            bundle.putString(context.getResources().getString(R.string.title), title);
                            bundle.putStringArrayList(context.getResources().getString(R.string.titlearray), titles);

                            bundle.putString(context.getResources().getString(R.string.artist), artist);
                            bundle.putStringArrayList(context.getResources().getString(R.string.artistarray), artists);

                            bundle.putString(context.getResources().getString(R.string.albumart), albumArt);
                            bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArts);

                            bundle.putString(context.getResources().getString(R.string.album), album);
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
                            songBundle.putStringArrayList(context.getResources().getString(R.string.albumartarray),albumArts);
                            songBundle.putStringArrayList(context.getResources().getString(R.string.titlearray),titles);

        /*player.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
        contxt.startService(player);*/
                            sendIntent.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                            context.sendBroadcast(sendIntent);

                        }



                        Intent intent = new Intent(context, MusicPlace.class);

                        Bundle bundle = new Bundle();


                   /* Bundle songBundle = new Bundle();
                    songBundle.putString(context.getResources().getString(R.string.songdata),data);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.songarray),datas);
                    songBundle.putInt(context.getResources().getString(R.string.songindex),position);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.albumarray),albums);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.artistarray),artists);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.albumartarray),albumArts);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.titlearray),titles);*/
                        //musicService.stopSelf();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                musicService.playMedia(position);
                                point.setText(title);
                            }
                        }, 300);
                        place.setInt(1);



                    /*player.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                    context.startService(player);*/

                        bundle.putString(context.getResources().getString(R.string.songdata), data);
                        bundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);

                        bundle.putInt(context.getResources().getString(R.string.songindex), position);

                        bundle.putString(context.getResources().getString(R.string.title), title);
                        bundle.putStringArrayList(context.getResources().getString(R.string.titlearray), titles);

                        bundle.putString(context.getResources().getString(R.string.artist), artist);
                        bundle.putStringArrayList(context.getResources().getString(R.string.artistarray), artists);

                        bundle.putString(context.getResources().getString(R.string.albumart), albumArt);
                        bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArts);

                        bundle.putString(context.getResources().getString(R.string.album), album);
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
                            songBundle.putStringArrayList(context.getResources().getString(R.string.albumartarray),albumArts);
                            songBundle.putStringArrayList(context.getResources().getString(R.string.titlearray),titles);

        /*player.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
        contxt.startService(player);*/
                            sendIntent.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                            context.sendBroadcast(sendIntent);

                        }

                        /*Intent playerIntent = new Intent(context, MyMusicService.class);

                        Bundle songBundle = new Bundle();
                        songBundle.putString(context.getResources().getString(R.string.songdata), data);
                        songBundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);
                        songBundle.putInt(context.getResources().getString(R.string.songindex), position);
                        songBundle.putStringArrayList(context.getResources().getString(R.string.albumarray),albums);
                        songBundle.putStringArrayList(context.getResources().getString(R.string.artistarray),artists);
                        songBundle.putStringArrayList(context.getResources().getString(R.string.albumartarray),albumArts);
                        songBundle.putStringArrayList(context.getResources().getString(R.string.titlearray),titles);

                        playerIntent.putExtra(context.getResources().getString(R.string.songbundle), songBundle);
                        context.startService(playerIntent);*/
                        if(play==Play.PLAY) {
                            pose=position;
                            musicService.reset();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    musicService.playMedia(position);
                                    point.setText(title);
                                }
                            }, 300);
                            place.setInt(1);

                            play=Play.RESUME;

                        }else if(play==Play.RESUME){
                            songFloatPlay.setImageResource(R.drawable.pausing);

                            if(pose==position&&place.getInt()==1) {
                                musicService.resumeMedia();
                            }else{
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        musicService.playMedia(position);
                                        point.setText(title);
                                    }
                                }, 300);
                                place.setInt(1);



                            }
                            //play=Play.PAUSE;
                            //view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                        }
                        songFloatPlay.setImageResource(R.drawable.pausing);

                        //play=Play.PAUSE;
                      //  view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                        counting++;
                    }else if(musicService.isPlaying()/*play==Play.PAUSE*/) {
                        songFloatPlay.setImageResource(R.drawable.playingshit);




                                musicService.pauseMedia();

                      //  view.setBackgroundColor(context.getResources().getColor(R.color.efblack));
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

            /*if(isServiceBound){
                if(musicService.isPlaying()){
                    if(point.getText().equalsIgnoreCase(title)&&place.getInt()==1){
                       view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                       songFloatPlay.setImageResource(R.drawable.pausing);
                    }
                }else if(!musicService.isPlaying()){
                    songFloatPlay.setImageResource(R.drawable.playingshit);
                }
            }*/




            /*if(musicService.isPlaying()){
                if(musicService.getCurrentIndex()==position){
                    view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                }
            }*/


        }

        public class Innering extends AsyncTask<Object,Void,Bitmap> {
            int position;
            public Innering(int position){
                this.position = position;
            }

            @Override
            protected Bitmap doInBackground(Object[] objects) {
               // return decodeAlbumArt(datas.get(position));
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap object) {
                songCircleImage.setImageBitmap(object);
            }
        }
    }

    public byte[] decodeAlbumArt(String file){
        Bitmap albumArt=null;
        byte[] art = null;

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {


            metadataRetriever.setDataSource(file);

         art = metadataRetriever.getEmbeddedPicture();

        if(art!=null){
            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(art,0,art.length,options);
            options.inSampleSize = calculateSampleSize(options,70,70);
            options.inJustDecodeBounds=false;
            albumArt = BitmapFactory.decodeByteArray(art,0,art.length,options);*/
        }else{
           /* BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);
            options.inSampleSize = calculateSampleSize(options,70,70);
            options.inJustDecodeBounds=false;

            albumArt = BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap arting = Bitmap.createBitmap(albumArt);
            arting.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
            art = outputStream.toByteArray();*/
        }
        }catch (IllegalArgumentException e){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);
            options.inSampleSize = calculateSampleSize(options,70,70);
            options.inJustDecodeBounds=false;

            albumArt = BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap arting = Bitmap.createBitmap(albumArt);
            arting.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
            art = outputStream.toByteArray();

        }catch (OutOfMemoryError e){
            decodeAlbumArt(file);
        }catch (Exception e){
           // decodeAlbumArt(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);
            options.inSampleSize = calculateSampleSize(options,70,70);
            options.inJustDecodeBounds=false;

            albumArt = BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap arting = Bitmap.createBitmap(albumArt);
            arting.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
            art = outputStream.toByteArray();
        }



        /*if(file!=null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file,options);
            options.inSampleSize = calculateSampleSize(options,500,500);
            options.inJustDecodeBounds=false;


            albumArt  = BitmapFactory.decodeFile(file,options);
        }else{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(),R.mipmap.cele6,options);
            options.inSampleSize = calculateSampleSize(options,200,200);
            options.inJustDecodeBounds=false;

            albumArt = BitmapFactory.decodeResource(context.getResources(),R.mipmap.cele6,options);
        }*/
        return art;

    }

    @Override
    public int getCount() {
        return songs.size();
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


                if(convertView==null){
                    convertView = inflater.inflate(R.layout.songlist, parent, false);

                }




            new ViewHolder(convertView, position);



        }catch (InflateException e){


            try{




                if(convertView==null){
                    convertView = inflater.inflate(R.layout.songlist, parent, false);

                }



                new ViewHolder(convertView, position);

            }catch (InflateException e1){

                try{


                    if(convertView==null){
                        convertView = inflater.inflate(R.layout.songlist, parent, false);

                    }


                    new ViewHolder(convertView, position);

                }catch (InflateException e2){



                    if(convertView==null){
                        convertView = inflater.inflate(R.layout.songlist, parent, false);

                    }


                    new ViewHolder(convertView, position);

                }catch (Exception e2){



                    if(convertView==null){
                        convertView = inflater.inflate(R.layout.songlist, parent, false);

                    }


                    new ViewHolder(convertView, position);

                }


            }catch (Exception e1){



                if(convertView==null){
                    convertView = inflater.inflate(R.layout.songlist, parent, false);

                }


                new ViewHolder(convertView, position);

            }


            /*if(convertView==null) {
                convertView = inflater.inflate(R.layout.songlist, parent, false);
                new ViewHolder(convertView, position);
            }else{
                //new ViewHolder(convertView,position);
            }*/

        }catch (Exception e){


                convertView = inflater.inflate(R.layout.songlist, parent, false);

            new ViewHolder(convertView, position);
            /*if(convertView==null) {
                convertView = inflater.inflate(R.layout.songlist, parent, false);
                new ViewHolder(convertView, position);
            }else{
                //new ViewHolder(convertView,position);
            }*/
        }

        return convertView;
    }
    public int calculateSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize=1;

        if(height>reqHeight||width>reqWidth){
            final int halfHeight=height/2;
            final int halfWidth=width/2;


            while ((halfHeight/inSampleSize)>=reqHeight&&(halfWidth/inSampleSize)>=reqWidth){
                inSampleSize*=2;
                if(inSampleSize==0){break;}
            }
        }
        return inSampleSize;
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
                        playlistDatabase.insertSong(datas.get(positioning),titles.get(positioning),artists.get(positioning),albums.get(positioning),albumArts.get(positioning));
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
