package com.mactrix.www.musical;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.ExtractedTextRequest;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Search extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView back;
    EditText searchSpace;
    ImageView searchPress;
    ListView searchList;
    ArrayList<MySongs> mySongs;
    ArrayList<String> datas;
    ArrayList<String> titles;
    ArrayList<String> artists;
    ArrayList<String> album;
    ArrayList<String> albumArt;
    RelativeLayout nothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        back = (ImageView)findViewById(R.id.searchback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchSpace=(EditText)findViewById(R.id.searchspace);
        searchPress = (ImageView) findViewById(R.id.searchpress);
        searchList = (ListView)findViewById(R.id.searchlist);
        nothing = (RelativeLayout)findViewById(R.id.nothing);

        loadSongs();




        /*searchSpace.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {


                String entry = String.valueOf(searchSpace.getText());

                datas = new ArrayList<>();
                titles = new ArrayList<>();
                artists = new ArrayList<>();
                album = new ArrayList<>();
                albumArt = new ArrayList<>();

                for(int i=0;i<mySongs.size();i++){
                    if(mySongs.get(i).getTitle().contains(entry)||mySongs.get(i).getArtist().contains(entry)||mySongs.get(i).getAlbum().contains(entry)){
                        datas.add(mySongs.get(i).getData());
                        titles.add(mySongs.get(i).getTitle());
                        artists.add(mySongs.get(i).getArtist());
                        album.add(mySongs.get(i).getAlbum());
                        albumArt.add(mySongs.get(i).getAlbumArt());
                    }
                }

                SearchAdapter searchAdapter = new SearchAdapter(Search.this,datas,titles,artists,album,albumArt);
                searchList.setAdapter(searchAdapter);




                return true;
            }
        });*/
        searchSpace.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                try {

                    String entry = String.valueOf(searchSpace.getText());

                    datas = new ArrayList<>();
                    titles = new ArrayList<>();
                    artists = new ArrayList<>();
                    album = new ArrayList<>();
                    albumArt = new ArrayList<>();

                    for(int i=0;i<mySongs.size();i++){
                        if(mySongs.get(i).getTitle().toLowerCase().contains(entry.toLowerCase())||mySongs.get(i).getArtist().toLowerCase().contains(entry.toLowerCase())||mySongs.get(i).getAlbum().toLowerCase().contains(entry.toLowerCase())){
                            datas.add(mySongs.get(i).getData());
                            titles.add(mySongs.get(i).getTitle());
                            artists.add(mySongs.get(i).getArtist());
                            album.add(mySongs.get(i).getAlbum());
                            albumArt.add(mySongs.get(i).getAlbumArt());
                        }
                    }

                    SearchAdapter searchAdapter = new SearchAdapter(Search.this,datas,titles,artists,album,albumArt);
                    searchList.setAdapter(searchAdapter);

                    if(searchList.getCount()>0){
                        nothing.setVisibility(View.GONE);
                        searchList.setVisibility(View.VISIBLE);
                    }else{
                        searchList.setVisibility(View.GONE);
                        nothing.setVisibility(View.VISIBLE);
                    }

                }catch (NullPointerException e){
                    loadSongs();
                    String entry = String.valueOf(searchSpace.getText());

                    if(entry==null){
                        entry=" ";
                    }



                    datas = new ArrayList<>();
                    titles = new ArrayList<>();
                    artists = new ArrayList<>();
                    album = new ArrayList<>();
                    albumArt = new ArrayList<>();

                    for(int i=0;i<mySongs.size();i++){
                        if(entry.toLowerCase().contains(mySongs.get(i).getTitle().toLowerCase())||entry.toLowerCase().contains(mySongs.get(i).getArtist().toLowerCase())||entry.toLowerCase().contains(mySongs.get(i).getAlbum().toLowerCase())){
                            datas.add(mySongs.get(i).getData());
                            titles.add(mySongs.get(i).getTitle());
                            artists.add(mySongs.get(i).getArtist());
                            album.add(mySongs.get(i).getAlbum());
                            albumArt.add(mySongs.get(i).getAlbumArt());
                        }
                    }

                    SearchAdapter searchAdapter = new SearchAdapter(Search.this,datas,titles,artists,album,albumArt);
                    searchList.setAdapter(searchAdapter);

                    if(searchList.getCount()>0){
                        nothing.setVisibility(View.GONE);
                        searchList.setVisibility(View.VISIBLE);
                    }else{
                        searchList.setVisibility(View.GONE);
                        nothing.setVisibility(View.VISIBLE);
                    }

                }catch (Exception e){
                    loadSongs();

                    String entry = String.valueOf(searchSpace.getText());

                    if(entry==null){
                        entry=" ";
                    }

                    datas = new ArrayList<>();
                    titles = new ArrayList<>();
                    artists = new ArrayList<>();
                    album = new ArrayList<>();
                    albumArt = new ArrayList<>();

                    for(int i=0;i<mySongs.size();i++){
                        if(entry.toLowerCase().contains(mySongs.get(i).getTitle().toLowerCase())||entry.toLowerCase().contains(mySongs.get(i).getArtist().toLowerCase())||entry.toLowerCase().contains(mySongs.get(i).getAlbum().toLowerCase())){
                            datas.add(mySongs.get(i).getData());
                            titles.add(mySongs.get(i).getTitle());
                            artists.add(mySongs.get(i).getArtist());
                            album.add(mySongs.get(i).getAlbum());
                            albumArt.add(mySongs.get(i).getAlbumArt());
                        }
                    }

                    SearchAdapter searchAdapter = new SearchAdapter(Search.this,datas,titles,artists,album,albumArt);
                    searchList.setAdapter(searchAdapter);

                    if(searchList.getCount()>0){
                        nothing.setVisibility(View.GONE);
                        searchList.setVisibility(View.VISIBLE);
                    }else{
                        searchList.setVisibility(View.GONE);
                        nothing.setVisibility(View.VISIBLE);
                    }

                }




                return true;
            }
        });

        searchPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    String entry = String.valueOf(searchSpace.getText());

                    datas = new ArrayList<>();
                    titles = new ArrayList<>();
                    artists = new ArrayList<>();
                    album = new ArrayList<>();
                    albumArt = new ArrayList<>();

                    for(int i=0;i<mySongs.size();i++){

                        if(mySongs.get(i).getTitle().toLowerCase().contains(entry.toLowerCase())||mySongs.get(i).getArtist().toLowerCase().contains(entry.toLowerCase())||mySongs.get(i).getAlbum().toLowerCase().contains(entry.toLowerCase())){
                            datas.add(mySongs.get(i).getData());
                            titles.add(mySongs.get(i).getTitle());
                            artists.add(mySongs.get(i).getArtist());
                            album.add(mySongs.get(i).getAlbum());
                            albumArt.add(mySongs.get(i).getAlbumArt());
                        }
                    }

                    SearchAdapter searchAdapter = new SearchAdapter(Search.this,datas,titles,artists,album,albumArt);
                    searchList.setAdapter(searchAdapter);

                    if(searchList.getCount()>0){
                        nothing.setVisibility(View.GONE);
                        searchList.setVisibility(View.VISIBLE);
                    }else{
                        searchList.setVisibility(View.GONE);
                        nothing.setVisibility(View.VISIBLE);
                    }

                }catch (NullPointerException e){
                    String entry = String.valueOf(searchSpace.getText());

                    datas = new ArrayList<>();
                    titles = new ArrayList<>();
                    artists = new ArrayList<>();
                    album = new ArrayList<>();
                    albumArt = new ArrayList<>();

                    for(int i=0;i<mySongs.size();i++){

                        if(entry.toLowerCase().contains(mySongs.get(i).getTitle().toLowerCase())||entry.toLowerCase().contains(mySongs.get(i).getArtist().toLowerCase())||entry.toLowerCase().contains(mySongs.get(i).getAlbum().toLowerCase())){
                            datas.add(mySongs.get(i).getData());
                            titles.add(mySongs.get(i).getTitle());
                            artists.add(mySongs.get(i).getArtist());
                            album.add(mySongs.get(i).getAlbum());
                            albumArt.add(mySongs.get(i).getAlbumArt());
                        }
                    }

                    SearchAdapter searchAdapter = new SearchAdapter(Search.this,datas,titles,artists,album,albumArt);
                    searchList.setAdapter(searchAdapter);

                    if(searchList.getCount()>0){
                        nothing.setVisibility(View.GONE);
                        searchList.setVisibility(View.VISIBLE);
                    }else{
                        searchList.setVisibility(View.GONE);
                        nothing.setVisibility(View.VISIBLE);
                    }

                }catch (Exception e){
                    String entry = String.valueOf(searchSpace.getText());

                    datas = new ArrayList<>();
                    titles = new ArrayList<>();
                    artists = new ArrayList<>();
                    album = new ArrayList<>();
                    albumArt = new ArrayList<>();

                    for(int i=0;i<mySongs.size();i++){

                        if(entry.toLowerCase().contains(mySongs.get(i).getTitle().toLowerCase())||entry.toLowerCase().contains(mySongs.get(i).getArtist().toLowerCase())||entry.toLowerCase().contains(mySongs.get(i).getAlbum().toLowerCase())){
                            datas.add(mySongs.get(i).getData());
                            titles.add(mySongs.get(i).getTitle());
                            artists.add(mySongs.get(i).getArtist());
                            album.add(mySongs.get(i).getAlbum());
                            albumArt.add(mySongs.get(i).getAlbumArt());
                        }
                    }

                    SearchAdapter searchAdapter = new SearchAdapter(Search.this,datas,titles,artists,album,albumArt);
                    searchList.setAdapter(searchAdapter);

                    if(searchList.getCount()>0){
                        nothing.setVisibility(View.GONE);
                        searchList.setVisibility(View.VISIBLE);
                    }else{
                        searchList.setVisibility(View.GONE);
                        nothing.setVisibility(View.VISIBLE);
                    }

                }






            }
        });


        searchSpace.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //String entry = String.valueOf(searchSpace.getText());

            }
        });








       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadSongs(){
        try{

            ContentResolver contentResolver = getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Uri ari= MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;










            String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
            String sortOrder = MediaStore.Audio.Media.TITLE+"ASC";

            Cursor cursor = contentResolver.query(uri,null,null,null,null);
            Cursor acursor= contentResolver.query(ari,null,null,null,null);
            String albumart="";
            String albuming="";
            mySongs = new ArrayList<>();

            if(cursor!=null &&cursor.getCount()>0){


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

        }catch(SecurityException e){

            Toast.makeText(this, "Sorry, this feature is having a Security issue with this device. Please make sure you turn off all Security Restrictions. Thank you", Toast.LENGTH_LONG).show();

        }



    }
}
