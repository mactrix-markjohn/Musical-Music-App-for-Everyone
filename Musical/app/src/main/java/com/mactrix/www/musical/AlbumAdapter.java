package com.mactrix.www.musical;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Mactrix on 2/14/2018.
 */

public class AlbumAdapter extends BaseAdapter {

    Context context;
    ArrayList<MyAlbum> myAlbums;
    ArrayList<MySongs> mySongs;
    LayoutInflater inflater;
    private ArrayList<String> datas;
    private ArrayList<String> titles;
    private ArrayList<String> artistNames;
    private ArrayList<String> albums;
    private ArrayList<String> artistAlbumArt;
    int posing;

    public AlbumAdapter(Context context, ArrayList<MyAlbum> myAlbums, ArrayList<MySongs> mySongs){
        this.context = context;
        this.myAlbums = myAlbums;
        this.mySongs = mySongs;
        inflater = LayoutInflater.from(context);

    }



    public class ViewHolder {
        ImageView albumImage;
        TextView album;
        TextView artist;
        TextView numverofSOngs;


        public ViewHolder(View view , final int position){

            posing = position;

            albumImage = (ImageView)view.findViewById(R.id.albumimage);
            album = (TextView)view.findViewById(R.id.albumalbum);
            artist = (TextView)view.findViewById(R.id.albumartist);
            numverofSOngs = (TextView)view.findViewById(R.id.albumcount);

            //albumImage.setImageBitmap(decodeAlbumArt(myAlbums.get(position).getAlbumArt()));

            if(myAlbums.get(position).getAlbumArt()!=null){
                try {
                    Glide.with(context).load(myAlbums.get(position).getAlbumArt()).into(albumImage);
                }catch (IllegalArgumentException e){
                    Glide.with(context).load(R.mipmap.playingheading).into(albumImage);
                }catch (Exception e){
                    Glide.with(context).load(R.mipmap.playingheading).into(albumImage);
                }

            }else{
                Glide.with(context).load(R.mipmap.playingheading).into(albumImage);

            }

            album.setText(myAlbums.get(position).getAlbum());
            artist.setText(myAlbums.get(position).getArtist());
            numverofSOngs.setText(""+myAlbums.get(position).getNumberOfSongs());


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getSongsOfArtist(myAlbums.get(position).getAlbum());
                   // String artistName = theArtist.get(position).getArtist();

                    Intent sendIntent = new Intent(context.getResources().getString(R.string.servicesetup));
                    Intent collector = new Intent(context,CollectSongs.class);

                    Bundle songBundle = new Bundle();
                    songBundle.putStringArrayList(context.getResources().getString(R.string.songarray),datas);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.albumarray),artistNames);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.artistarray),albums);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.albumartarray),artistAlbumArt);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.titlearray),titles);
                    songBundle.putString("filepic",myAlbums.get(position).getAlbumArt());
                    songBundle.putString("artistname",myAlbums.get(position).getAlbum());


                    sendIntent.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                    collector.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                    // context.sendBroadcast(sendIntent);
                    context.startActivity(collector);

                }
            });




        }

        public void getSongsOfArtist(final String artistName){

            try{
                datas = new ArrayList<>();
                albums = new ArrayList<>();
                titles = new ArrayList<>();
                artistNames = new ArrayList<>();
                artistAlbumArt = new ArrayList<>();

                for(int i=0;i<mySongs.size();i++){
                    if(mySongs.get(i).getAlbum().equalsIgnoreCase(artistName)){
                        datas.add(mySongs.get(i).getData());
                        titles.add(mySongs.get(i).getTitle());
                        artistNames.add(mySongs.get(i).getArtist());
                        albums.add(mySongs.get(i).getAlbum());
                        artistAlbumArt.add(mySongs.get(i).getAlbumArt());

                    }
                }

            }catch (NullPointerException e){

                loadSongs();
                datas = new ArrayList<>();
                albums = new ArrayList<>();
                titles = new ArrayList<>();
                artistNames = new ArrayList<>();
                artistAlbumArt = new ArrayList<>();




                        for(int i=0;i<mySongs.size();i++){
                            if(artistName.equalsIgnoreCase(mySongs.get(i).getAlbum())){
                                datas.add(mySongs.get(i).getData());
                                titles.add(mySongs.get(i).getTitle());
                                artistNames.add(mySongs.get(i).getArtist());
                                albums.add(mySongs.get(i).getAlbum());
                                artistAlbumArt.add(mySongs.get(i).getAlbumArt());

                            }
                        }








            }catch (Exception e){
                loadSongs();
                datas = new ArrayList<>();
                albums = new ArrayList<>();
                titles = new ArrayList<>();
                artistNames = new ArrayList<>();
                artistAlbumArt = new ArrayList<>();

                for(int i=0;i<mySongs.size();i++){
                    if(artistName.equalsIgnoreCase(mySongs.get(i).getAlbum())){
                        datas.add(mySongs.get(i).getData());
                        titles.add(mySongs.get(i).getTitle());
                        artistNames.add(mySongs.get(i).getArtist());
                        albums.add(mySongs.get(i).getAlbum());
                        artistAlbumArt.add(mySongs.get(i).getAlbumArt());

                    }
                }

            }


        }
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


    @Override
    public int getCount() {
        return myAlbums.size();
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
            convertView = inflater.inflate(R.layout.albumgrid,parent,false);
            new ViewHolder(convertView,position);
        }catch (InflateException e){
            convertView = inflater.inflate(R.layout.albumgrid,parent,false);
            new ViewHolder(convertView,position);
        }catch (Exception e){
            convertView = inflater.inflate(R.layout.albumgrid,parent,false);
            new ViewHolder(convertView,position);
        }



        return convertView;
    }


    public Bitmap decodeAlbumArt(String file){
        Bitmap albumArt = null;



        try {
            if (file != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file, options);
                options.inSampleSize = calculateSampleSize(options, 200, 200);
                options.inJustDecodeBounds = false;
                albumArt = BitmapFactory.decodeFile(file, options);


            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.playingheading, options);
                options.inSampleSize = calculateSampleSize(options, 200, 200);
                options.inJustDecodeBounds = false;
                albumArt = BitmapFactory.decodeResource(context.getResources(), R.mipmap.playingheading, options);

            }
        } catch (IllegalArgumentException e) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), R.mipmap.playingheading, options);
            options.inSampleSize = calculateSampleSize(options, 200, 200);
            options.inJustDecodeBounds = false;
            albumArt = BitmapFactory.decodeResource(context.getResources(), R.mipmap.playingheading, options);
        }catch (OutOfMemoryError e){
            decodeAlbumArt(file);
        }catch (Exception e){
            decodeAlbumArt(file);
        }

        return albumArt;
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
}
