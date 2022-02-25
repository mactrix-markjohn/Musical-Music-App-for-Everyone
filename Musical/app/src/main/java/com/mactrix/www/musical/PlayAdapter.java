package com.mactrix.www.musical;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.PopupMenu;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mactrix on 2/15/2018.
 */

public class PlayAdapter extends BaseAdapter {

    private final Intent player;
    private final SharedPref place;
    private final ArrayList<String> datas;
    private final ArrayList<String> titles;
    private final ArrayList<String> artists;
    private final ArrayList<String> albums;
    private final ArrayList<String> albumArts;
    Context context;
    PlaylistDatabase playlistDatabase;
    Cursor cursor;
    LayoutInflater inflater;
    int pose=0;
    SharedPref singConst;

    enum Play{PLAY,PAUSE,RESUME}
    Play play;


    public PlayAdapter(Context context, PlaylistDatabase database, Cursor cursor){
        this.context = context;
        playlistDatabase = database;
        this.cursor = cursor;
        inflater = LayoutInflater.from(context);
        player  = new Intent(context,MyMusicService.class);
        context.bindService(player,serviceConnection,context.BIND_AUTO_CREATE);
        place =new SharedPref(context,"Place");
        play = Play.PLAY;
        singConst = new SharedPref(context,context.getResources().getString(R.string.singConst));


        datas = new ArrayList<>();
        titles = new ArrayList<>();
        artists = new ArrayList<>();
        albums = new ArrayList<>();
        albumArts = new ArrayList<>();


        if(cursor!=null){
            if(cursor.getCount()>0){
                for(int i=0;i<cursor.getCount();i++){
                    cursor.moveToPosition(i);
                    datas.add(cursor.getString(cursor.getColumnIndex(PlaylistDatabase.DATA)));
                    titles.add(cursor.getString(cursor.getColumnIndex(PlaylistDatabase.TITLE)));
                    artists.add(cursor.getString(cursor.getColumnIndex(PlaylistDatabase.ARTIST)));
                    albums.add(cursor.getString(cursor.getColumnIndex(PlaylistDatabase.ALBUM)));
                    albumArts.add(cursor.getString(cursor.getColumnIndex(PlaylistDatabase.ALBUMART)));
                }
            }
        }

    }

    public class ViewHolder{


        private final CircleImageView songCircleImage;
        private final ImageView songMenu;
        private final TextView songTitle;
        private final TextView songArtist;
        private final TextView songAlbum;
        private final ImageView songFloatPlay;
        int counting =0;

        public ViewHolder(final View view, final int position){

            play = Play.PLAY;
            songCircleImage = (CircleImageView)view.findViewById(R.id.songcircleimage);
            songMenu = (ImageView)view.findViewById(R.id.songmenu);
            songTitle = (TextView)view.findViewById(R.id.songtitle);
            songArtist =(TextView)view.findViewById(R.id.songartist);
            songAlbum = (TextView)view.findViewById(R.id.songalbum);
            songFloatPlay = (ImageView)view.findViewById(R.id.songfloatplay);


            if(cursor!=null){
                if(cursor.getCount()>0){
                    cursor.moveToPosition(position);

                    //loadImage(cursor.getString(cursor.getColumnIndex(PlaylistDatabase.DATA)),songCircleImage);
                    if(decodeAlbumArt(cursor.getString(cursor.getColumnIndex(PlaylistDatabase.DATA)))!=null){
                        Glide.with(context).load(decodeAlbumArt(cursor.getString(cursor.getColumnIndex(PlaylistDatabase.DATA)))).into(songCircleImage);
                    }else{
                        Glide.with(context).load(R.mipmap.playing).into(songCircleImage);
                    }

                   // songCircleImage.setImageBitmap(decodeAlbumArt(cursor.getString(cursor.getColumnIndex(PlaylistDatabase.DATA))));
                    songTitle.setText(cursor.getString(cursor.getColumnIndex(PlaylistDatabase.TITLE)));
                    songArtist.setText(cursor.getString(cursor.getColumnIndex(PlaylistDatabase.ARTIST)));
                    songAlbum.setText(cursor.getString(cursor.getColumnIndex(PlaylistDatabase.ALBUM)));
                }
            }

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context,v);
                    popupMenu.inflate(R.menu.popplay);
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.playsong: //Intent playerIntent = new Intent(context, MyMusicService.class);



                                  /*  Bundle songBundle = new Bundle();
                                    songBundle.putString(context.getResources().getString(R.string.songdata), data);
                                    songBundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);
                                    songBundle.putInt(context.getResources().getString(R.string.songindex), position);

                                    playerIntent.putExtra(context.getResources().getString(R.string.songbundle), songBundle);
                                    context.startService(playerIntent);*/

                                    musicService.reset();
                                    musicService.playMedia(position);
                                    play = Play.PAUSE;
                                    songFloatPlay.setImageResource(R.drawable.pausing);

                                    //play=Play.PAUSE;
                            //        view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                                    break;
                                case R.id.playdelete:




                                    if (cursor != null) {
                                        if (cursor.getCount() > 0) {
                                            cursor.moveToPosition(position);
                                            long id = cursor.getLong(cursor.getColumnIndex(PlaylistDatabase.ID));
                                            playlistDatabase.deleteNote(id);
                                            Intent intent = new Intent("resuming");
                                            intent.putExtra("delete",5);
                                            context.sendBroadcast(intent);
                                            // Toast.makeText(context, "Refresh this page for the Song to properly delete", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    break;
                                case R.id.singalong:
                            if (singConst.getInt() != 10) {

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

                    PopupMenu popupMenu = new PopupMenu(context,v);
                    popupMenu.inflate(R.menu.popplay);
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.playsong: //Intent playerIntent = new Intent(context, MyMusicService.class);



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
                          //          view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                                    break;
                                case R.id.playdelete:if(cursor!=null){
                                    if(cursor.getCount()>0){
                                        cursor.moveToPosition(position);
                                        long id = cursor.getLong(cursor.getColumnIndex(PlaylistDatabase.ID));
                                        playlistDatabase.deleteNote(id);
                                        Intent intent = new Intent("resuming");
                                        intent.putExtra("delete",5);
                                        context.sendBroadcast(intent);
                                       // Toast.makeText(context, "Refresh this page for the Song to properly delete", Toast.LENGTH_LONG).show();
                                    }
                                }
                                    break;
                                case R.id.singalong:
                                    if (singConst.getInt() != 10) {

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


                        if(place.getInt()==3) {
                            Intent intent = new Intent(context, MusicPlace.class);

                            Bundle bundle = new Bundle();

                            bundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);

                            bundle.putInt(context.getResources().getString(R.string.songindex), position);


                            bundle.putStringArrayList(context.getResources().getString(R.string.titlearray), titles);


                            bundle.putStringArrayList(context.getResources().getString(R.string.artistarray), artists);


                            bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArts);


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

                                sendBundle(position);


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

                        sendBundle(position);




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
                            place.setInt(3);

                            play= Play.RESUME;

                        }else if(play== Play.RESUME){
                            songFloatPlay.setImageResource(R.drawable.pausing);

                            if(pose==position&&place.getInt()==3) {
                                musicService.resumeMedia();
                            }else{
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        musicService.playMedia(position);
                                    }
                                }, 300);
                                place.setInt(3);



                            }
                            //play=Play.PAUSE;
                            //view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                        }
                        songFloatPlay.setImageResource(R.drawable.pausing);

                        //play=Play.PAUSE;
                        //view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                        counting++;
                    }else if(musicService.isPlaying()/*play==Play.PAUSE*/) {
                        songFloatPlay.setImageResource(R.drawable.playingshit);




                        musicService.pauseMedia();

                        view.setBackgroundColor(context.getResources().getColor(R.color.efblack));
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
                    if(musicService.getCurrentIndex()==position&&place.getInt()==3){
                        view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                        songFloatPlay.setImageResource(R.drawable.pausing);
                    }
                }else if(!musicService.isPlaying()){
                    songFloatPlay.setImageResource(R.drawable.playingshit);
                }
            }*/






        }
    }

    public void sendBundle(final int position){
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
            }
        }, 300);
        place.setInt(3);



                    /*player.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                    context.startService(player);*/


        bundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);

        bundle.putInt(context.getResources().getString(R.string.songindex), position);


        bundle.putStringArrayList(context.getResources().getString(R.string.titlearray), titles);


        bundle.putStringArrayList(context.getResources().getString(R.string.artistarray), artists);


        bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArts);


        bundle.putStringArrayList(context.getResources().getString(R.string.albumarray), albums);

        intent.putExtra(context.getResources().getString(R.string.songbundle), bundle);


        // context.stopService(player);
        context.startActivity(intent);
    }


    @Override
    public int getCount() {
        int count =0;
        if(cursor!=null){
            count = cursor.getCount();
        }

        return count;
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
                convertView = inflater.inflate(R.layout.playlist_layout,parent,false);
            }

            new ViewHolder(convertView,position);

        }catch (InflateException e){
            convertView = inflater.inflate(R.layout.playlist_layout,parent,false);
            new ViewHolder(convertView,position);

        }catch (Exception e){
            convertView = inflater.inflate(R.layout.playlist_layout,parent,false);
            new ViewHolder(convertView,position);

        }

        return convertView;
    }


    public byte[] decodeAlbumArt(String file){
        Bitmap albumArt=null;
        byte[] art=null;

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {


            metadataRetriever.setDataSource(file);

             art = metadataRetriever.getEmbeddedPicture();

            /*if(art!=null){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(art,0,art.length,options);
                options.inSampleSize = calculateSampleSize(options,100,100);
                options.inJustDecodeBounds=false;
                albumArt = BitmapFactory.decodeByteArray(art,0,art.length,options);
            }else{
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);
                options.inSampleSize = calculateSampleSize(options,100,100);
                options.inJustDecodeBounds=false;

                albumArt = BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);
            }*/
        }catch (IllegalArgumentException e){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);
            options.inSampleSize = calculateSampleSize(options,100,100);
            options.inJustDecodeBounds=false;

            albumArt = BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap arting = Bitmap.createBitmap(albumArt);
            arting.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
            art = outputStream.toByteArray();

        }catch (OutOfMemoryError e){
            decodeAlbumArt(file);
        }catch (Exception e){
            decodeAlbumArt(file);
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


    public int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
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

    public void loadImage(String data, CircleImageView circleImageView){

        Glide.with(context).load(data).asBitmap().placeholder(R.mipmap.playing).into(circleImageView);

    }
}
