package com.mactrix.www.musical;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mactrix on 2/13/2018.
 */

public class CollectiveAdapter extends BaseAdapter {
    private final Intent player;
    CircleImageView collectorImage;
    TextView collectorName;
    TextView collectorAlbum;
    Context context;
    ImageView songmenu;
    ArrayList<String> datas;
    ArrayList<String> titles;
    ArrayList<String> albums;
    ArrayList<String> artists;
    LayoutInflater inflater;
    SharedPref singConst;

    enum Play{PLAY,PAUSE,RESUME}
    Play play;
    SharedPref place;
    ArrayList<String> albumArts;

    public CollectiveAdapter(Context context, ArrayList<String> datas,ArrayList<String> titles,ArrayList<String> albums,ArrayList<String> artist,ArrayList<String> albumArts){
        inflater = LayoutInflater.from(context);
        player  = new Intent(context,MyMusicService.class);
        context.bindService(player,serviceConnection,context.BIND_AUTO_CREATE);
        this.context = context;
        this.datas = datas;
        this.titles = titles;
        this.albums = albums;
        this.artists = artist;
        this.albumArts = albumArts;
        place = new SharedPref(context,"Place");

    }


    public class ViewHolder{

        int counting = 0;

        public ViewHolder(final View view, final int position){
            collectorImage = (CircleImageView)view.findViewById(R.id.collectcircleimage);
            collectorName =(TextView)view.findViewById(R.id.collectname);
            collectorAlbum =(TextView)view.findViewById(R.id.collectsongs);
            songmenu = (ImageView)view.findViewById(R.id.songmenu);



           // collectorImage.setImageBitmap(decodeAlbumArt(datas.get(position)));
            if(decodeAlbumArt(datas.get(position))!=null){
                Glide.with(context).load(decodeAlbumArt(datas.get(position))).into(collectorImage);
            }else{
                Glide.with(context).load(R.mipmap.playing).into(collectorImage);
            }

            collectorName.setText(titles.get(position));
            collectorAlbum.setText(albums.get(position));


            songmenu.setOnClickListener(new View.OnClickListener() {
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
                                   // view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                                    break;
                                case R.id.gotoartist: singConst = new SharedPref(context,context.getResources().getString(R.string.singConst));
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

                        if(place.getInt()==2) {
                            Intent intent = new Intent(context, MusicPlace.class);

                            Bundle bundle = new Bundle();

                            bundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);
                            bundle.putInt(context.getResources().getString(R.string.songindex), position);
                            bundle.putStringArrayList(context.getResources().getString(R.string.titlearray), titles);
                            bundle.putStringArrayList(context.getResources().getString(R.string.artistarray), artists);
                            //bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArts);

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

        /*player.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
        contxt.startService(player);*/
                                sendIntent.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                                context.sendBroadcast(sendIntent);

                            }



                            Intent intent = new Intent(context, MusicPlace.class);

                            Bundle bundle = new Bundle();
                            musicService.reset();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    musicService.playMedia(position);
                                }
                            }, 300);
                            place.setInt(2);

                            bundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);
                            bundle.putInt(context.getResources().getString(R.string.songindex), position);
                            bundle.putStringArrayList(context.getResources().getString(R.string.titlearray), titles);
                            bundle.putStringArrayList(context.getResources().getString(R.string.artistarray), artists);
                            // bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArts);
                            bundle.putStringArrayList(context.getResources().getString(R.string.albumarray), albums);
                            intent.putExtra(context.getResources().getString(R.string.songbundle), bundle);

                            // context.stopService(player);
                            context.startActivity(intent);
                            counting++;
                        }


                        // context.startActivity(new Intent(context,MusicPlace.class));
                    }else {


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
                        //musicService.stopSelf();]
                        musicService.reset();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                musicService.playMedia(position);
                            }
                        }, 300);
                        place.setInt(2);

                    /*player.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                    context.startService(player);*/


                        bundle.putStringArrayList(context.getResources().getString(R.string.songarray), datas);
                        bundle.putInt(context.getResources().getString(R.string.songindex), position);
                        bundle.putStringArrayList(context.getResources().getString(R.string.titlearray), titles);
                        bundle.putStringArrayList(context.getResources().getString(R.string.artistarray), artists);
                        // bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArts);
                        bundle.putStringArrayList(context.getResources().getString(R.string.albumarray), albums);
                        intent.putExtra(context.getResources().getString(R.string.songbundle), bundle);

                        // context.stopService(player);
                        context.startActivity(intent);
                        counting++;
                    }

                }
            });

            if(isServiceBound){
                if(musicService.isPlaying()){
                    if(musicService.getCurrentIndex()==position&&place.getInt()==2){
                       // view.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                        //songFloatPlay.setImageResource(R.drawable.pause);
                    }
                }else if(!musicService.isPlaying()){
                   // view.setBackgroundColor(context.getResources().getColor(R.color.transperent));
                    //songFloatPlay.setImageResource(R.drawable.play);
                }
            }



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

            if(convertView==null){
                convertView = inflater.inflate(R.layout.collectorlayout, parent, false);

            }

                new ViewHolder(convertView,position);

        }catch (InflateException e){

                convertView = inflater.inflate(R.layout.collectorlayout, parent, false);
            new ViewHolder(convertView,position);


        }catch (Exception e){

                convertView = inflater.inflate(R.layout.collectorlayout, parent, false);
                new ViewHolder(convertView, position);

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

           /* if(art!=null){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(art,0,art.length,options);
                options.inSampleSize = calculateSampleSize(options,150,150);
                options.inJustDecodeBounds=false;
                albumArt = BitmapFactory.decodeByteArray(art,0,art.length,options);
            }else{
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);
                options.inSampleSize = calculateSampleSize(options,150,150);
                options.inJustDecodeBounds=false;

                albumArt = BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);
            }*/
        }catch (IllegalArgumentException e){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(),R.mipmap.playing,options);
            options.inSampleSize = calculateSampleSize(options,150,150);
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

    public MyMusicService musicService;
    public boolean isServiceBound;
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
