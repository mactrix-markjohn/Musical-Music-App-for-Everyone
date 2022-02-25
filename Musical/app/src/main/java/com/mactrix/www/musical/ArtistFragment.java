package com.mactrix.www.musical;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Mactrix on 2/4/2018.
 */

public class ArtistFragment extends Fragment {

    Context context;
    ListView listView;
    ArrayList<String> artistNames;
    ArrayList<String> artistAlbumArt;
    ArrayList<String> datas;
    ArrayList<String> albums;
    ArrayList<String> titles;
    ArrayList<String> numberOfsong;
    ArrayList<TheArtist> theArtist;
    private ArrayList<MySongs> mySongs;
    private ImageView playcover;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View v=inflater.inflate(R.layout.artist_layout,container,false);

        listView = (ListView)v.findViewById(R.id.artistlisting);
        playcover = (ImageView)v.findViewById(R.id.playcover);


        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        artistNames = new ArrayList<>();
        // artistAlbumArt = new ArrayList<>();
        numberOfsong = new ArrayList<>();
        theArtist = new ArrayList<>();


        Bitmap cover = BitmapFactory.decodeResource(getResources(),R.mipmap.singguiter);
        playcover.setImageBitmap(blur(cover,10));


        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {


            //Loading loading = new Loading();
            //loading.execute();

                   loadSongs();
                   loadArtists();


            ArtistListAdapter artistListAdapter = new ArtistListAdapter(context,theArtist,mySongs);
            listView.setAdapter(artistListAdapter);
        }
    }

    public void loadArtists(){


        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String artistAlbumart="";



        Cursor cursor=contentResolver.query(uri,null,null,null,null);

        if(cursor!=null&&cursor.getCount()>0){
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToPosition(i);

                String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));

                long artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID));

                Uri art = MediaStore.Audio.Artists.Albums.getContentUri("external",artistId);
                Cursor artCursor = contentResolver.query(art,null,null,null,null);

                if(artCursor!=null&&artCursor.getCount()>0){
                    artistAlbumArt = new ArrayList<>();
                    for(int j=0;j<artCursor.getCount();j++){
                        artCursor.moveToPosition(j);
                       // if(artCursor.moveToNext()) {
                            artistAlbumart = artCursor.getString(artCursor.getColumnIndex(MediaStore.Audio.Artists.Albums.ALBUM_ART));
                            this.artistAlbumArt.add(artistAlbumart);
                        //}
                    }
                }

                int numberOfsongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));


                theArtist.add(new TheArtist(artistName,this.artistAlbumArt,numberOfsongs) );


            }
            Collections.sort(theArtist, new Comparator<TheArtist>() {
                @Override
                public int compare(TheArtist o1, TheArtist o2) {
                    return o1.getArtist().compareToIgnoreCase(o2.getArtist());
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

    private class Loading extends AsyncTask<Object,Void,Object>{

        @Override
        protected Object doInBackground(Object... objects) {
            Object o = new Object();
            loadSongs();
            loadArtists();
            return o;
        }
        @Override
        protected void onPostExecute(Object o){
            ArtistListAdapter artistListAdapter = new ArtistListAdapter(context,theArtist,mySongs);
            listView.setAdapter(artistListAdapter);
        }
    }

    public Bitmap blur(Bitmap inbit,float BLUR_RADIUS){
        if(inbit==null) return null;

        Bitmap outbit = Bitmap.createBitmap(inbit);



        RenderScript renderScript = RenderScript.create(context);
        Allocation alloin= Allocation.createFromBitmap(renderScript,inbit);
        Allocation alloout = Allocation.createFromBitmap(renderScript,outbit);

        // Intrinsic Gausian blur filter
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            intrinsicBlur.setRadius(BLUR_RADIUS);
            intrinsicBlur.setInput(alloin);
            intrinsicBlur.forEach(alloout);
            alloout.copyTo(outbit);

        }
        return outbit;

    }



}
