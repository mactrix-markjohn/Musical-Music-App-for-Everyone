package com.mactrix.www.musical;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Mactrix on 2/4/2018.
 */

public class AlbumFragment extends Fragment {
    Context context;
    private ArrayList<MySongs> mySongs;
    ArrayList<MyAlbum> myAlbums;
    GridView gridView;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View v=inflater.inflate(R.layout.album_fragment,container,false);
        gridView = (GridView)v.findViewById(R.id.albumgrid);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            loadSongs();
            loadAlbums();
            AlbumAdapter albumAdapter = new AlbumAdapter(context,myAlbums,mySongs);
            gridView.setAdapter(albumAdapter);
        }

    }


    public void loadAlbums(){
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri,null,null,null,null);

        if(cursor!=null&&cursor.getCount()>0){
            myAlbums = new ArrayList<>();
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToPosition(i);

                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                int numberOfsongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
                String albumArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));

                myAlbums.add(new MyAlbum(album,albumArt,artist,numberOfsongs));

            }


            Collections.sort(myAlbums, new Comparator<MyAlbum>() {
                @Override
                public int compare(MyAlbum o1, MyAlbum o2) {
                    return o1.getAlbum().compareToIgnoreCase(o2.getAlbum());
                }
            });
        }
        cursor.close();
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
}
