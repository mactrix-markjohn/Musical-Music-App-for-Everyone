package com.mactrix.www.musical;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by Mactrix on 2/19/2018.
 */

public class MyOwnSongs extends Fragment {


    Context context;
    ListView listView;
    ArrayList<String> datas;
    ArrayList<String> titles;
    ArrayList<String> artists;
    ArrayList<String> albums;
    private ArrayList<MySongs> mySongs;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context= context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.ownsongs,parent,false);

        listView = (ListView)v.findViewById(R.id.ownlist);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){





            Loading loading = new Loading();
            loading.execute();



           /* loadSongs();
            SinglistOwnAdapter singlistOwnAdapter = new SinglistOwnAdapter(context,mySongs);
            listView.setAdapter(singlistOwnAdapter);*/

        }





    }


    public void loadSongs(){
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri ari= MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
        String sortOrder = MediaStore.Audio.Media.TITLE+"ASC";

        Cursor cursor = contentResolver.query(uri,null,null,null,null);

        String albumart="";
        String albuming="";

        if(cursor!=null &&cursor.getCount()>0){
            mySongs = new ArrayList<>();

            for(int i=0,j=0;i<cursor.getCount();j++,i++){
                cursor.moveToPosition(i);



                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String albumKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));

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

    private class Loading extends AsyncTask<Object,Void,Object> {

        @Override
        protected Object doInBackground(Object... objects) {
            Object o = new Object();
            loadSongs();
            //loadArtists();
            return o;
        }
        @Override
        protected void onPostExecute(Object o){
            SinglistOwnAdapter artistListAdapter = new SinglistOwnAdapter(context,mySongs);
            listView.setAdapter(artistListAdapter);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Intent intent = new Intent("onStop");
        context.sendBroadcast(intent);
    }

    @Override
    public void onStop(){
        super.onStop();
        Intent intent = new Intent("onStop");
        context.sendBroadcast(intent);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Intent intent = new Intent("onStop");
        context.sendBroadcast(intent);
    }


}
