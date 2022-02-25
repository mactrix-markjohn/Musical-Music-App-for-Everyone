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
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSIllegalArgumentException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicPlace extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MyMusicService musicService;
    boolean serviceBound;
    ImageView musicback;
    ImageView prevoisFab;
    ImageView nextFab;
    SeekBar musicSeek;
    TextView musiccount;
    TextView musiclength;
    ImageView musicsongList;
    ImageView musicRepeat;
    ImageView musicShuffle;
    ImageView musicPlayList;
    ImageView musickaraoeke;
    CircleImageView musicfore;
    TextView musicArtist;
    TextView musicTitle;
    int currentPlace;
    ArrayList<String> songs;
    //String songdata;
    ArrayList<String> titles;
    //String title;
   // ArrayList<String> albumArts;
   // String albumart;
    ArrayList<String> artists;
   // String artistName;
    ArrayList<String> albums;
    //String album;
    int currentIndex;
    Intent playerIntent;
    Handler handler;
    private AppLovinAd appLovnAd;
    private SharedPref sharedPref;
    private CountDownTimer downTimer;
    SharedPref singConst;
    MoPubInterstitial interstitial;

    enum Shuffle{ON,OFF}
    Shuffle shuffle;
    enum Repeat{ALL,ONE}
    Repeat repeat;
    enum Period{NOW,LATER}
    Period period;
    int duration;
    int count =0;
     ImageView fab;
     ImageView playlistMenu ;

     InterstitialAd musicAd;
     AdRequest musicReq;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_place);

        period = Period.NOW;

        singConst = new SharedPref(this,getResources().getString(R.string.singConst));
        sharedPref = new SharedPref(this,getResources().getString(R.string.adsConst));
        if(sharedPref.getInt()!=10) {


            musicAd = new InterstitialAd(this);
            musicReq = new AdRequest.Builder().build();
            musicAd.setAdUnitId("ca-app-pub-2742716340205774/9544945006");


            downTimer = new CountDownTimer(25000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if(millisUntilFinished==10000){
                        Toast.makeText(MusicPlace.this, "Sorry, Ads will be shown in less than 10 secs", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFinish() {

                    musicAd.loadAd(musicReq);
                    musicAd.setAdListener(new AdListener(){
                        public void onAdLoaded(){
                            if(musicAd.isLoaded()){
                                musicAd.show();
                            }
                        }
                    });


                    /*interstitial = new MoPubInterstitial(MusicPlace.this,"7cada9c6b4f0448690c29a58b5317bfe");
                    interstitial.load();

                    interstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                        @Override
                        public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                            interstitial.show();

                        }

                        @Override
                        public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                            // startActivity(new Intent(Musical.this,LovedThings.class));

                        }

                        @Override
                        public void onInterstitialShown(MoPubInterstitial interstitial) {
                           /* new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(Musical.this,LovedThings.class));
                                }
                            }, 2000);*/

                        /*}

                        @Override
                        public void onInterstitialClicked(MoPubInterstitial interstitial) {

                        }

                        @Override
                        public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                            // startActivity(new Intent(Musical.this,LovedThings.class));

                        }
                    });*/


                   /* AppLovinSdk.initializeSdk(MusicPlace.this);
                    AppLovinSdk.getInstance(MusicPlace.this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                        @Override
                        public void adReceived(AppLovinAd appLovinAd) {
                            appLovnAd = appLovinAd;

                        }

                        @Override
                        public void failedToReceiveAd(int i) {

                        }
                    });
                    AppLovinInterstitialAdDialog adDialog = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(MusicPlace.this), MusicPlace.this);
                    adDialog.setAdClickListener(new AppLovinAdClickListener() {
                        @Override
                        public void adClicked(AppLovinAd appLovinAd) {

                        }
                    });
                    adDialog.setAdLoadListener(new AppLovinAdLoadListener() {
                        @Override
                        public void adReceived(AppLovinAd appLovinAd) {
                        }

                        @Override
                        public void failedToReceiveAd(int i) {

                        }
                    });
                    adDialog.setAdDisplayListener(new AppLovinAdDisplayListener() {
                        @Override
                        public void adDisplayed(AppLovinAd appLovinAd) {

                        }

                        @Override
                        public void adHidden(AppLovinAd appLovinAd) {

                        }
                    });
                    adDialog.showAndRender(appLovnAd);*/

                }
            };
            downTimer.start();

        }




        playerIntent = new Intent(this,MyMusicService.class);
        bindService(playerIntent,serviceConnection,BIND_AUTO_CREATE);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        handler = new Handler();
        musiccount = (TextView)findViewById(R.id.musiccount);
        musiclength = (TextView)findViewById(R.id.musiclength);
        musicArtist = (TextView)findViewById(R.id.musicartist);
        musicTitle = (TextView)findViewById(R.id.musictitle);
        musicback = (ImageView)findViewById(R.id.musicback);
        musicfore = (CircleImageView)findViewById(R.id.musicfore);
        musicSeek = (SeekBar)findViewById(R.id.musicseek);
        shuffle = Shuffle.OFF;
        repeat = Repeat.ALL;

        ImageView backPress = (ImageView)findViewById(R.id.backpress);
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //musicService.onRepeatAll();



        registerOnCompleteBroadcast();
        registerStart();

        Bundle bundle = getIntent().getBundleExtra(getResources().getString(R.string.songbundle));

        if(bundle!=null) {
           // songdata = bundle.getString(getResources().getString(R.string.songdata));
            songs = bundle.getStringArrayList(getResources().getString(R.string.songarray));

            currentPlace= bundle.getInt(getResources().getString(R.string.songindex));
            currentIndex=currentPlace;
           // currentIndex = musicService.getCurrentIndex();

           // title = bundle.getString(getResources().getString(R.string.title));
            titles = bundle.getStringArrayList(getResources().getString(R.string.titlearray));

           // albumart = bundle.getString(getResources().getString(R.string.albumart));
            //albumArts = bundle.getStringArrayList(getResources().getString(R.string.albumartarray));

           // artistName = bundle.getString(getResources().getString(R.string.artist));
            artists = bundle.getStringArrayList(getResources().getString(R.string.artistarray));

            //album = bundle.getString(getResources().getString(R.string.album));
            albums = bundle.getStringArrayList(getResources().getString(R.string.albumarray));
        }else{
            //title = musicService.getTitles();
            //album = musicService.getAlbums();
            //albumart = musicService.getAlbumArt();
            //artistName = musicService.getArtist();

        }

        if(savedInstanceState!=null) {
            currentIndex = savedInstanceState.getInt("CurrentIndex");
            duration = savedInstanceState.getInt("duration");
            period = Period.LATER;
        }

        if(serviceBound){
            currentIndex = musicService.getCurrentIndex();
        }

       // if(serviceBound){
           /* Bundle songBundle = new Bundle();
            songBundle.putString(getResources().getString(R.string.songdata),songdata);
            songBundle.putStringArrayList(getResources().getString(R.string.songarray),songs);
            songBundle.putInt(getResources().getString(R.string.songindex),currentIndex);
            songBundle.putStringArrayList(getResources().getString(R.string.albumarray),albums);
            songBundle.putStringArrayList(getResources().getString(R.string.artistarray),artists);
            songBundle.putStringArrayList(getResources().getString(R.string.albumartarray),albumArts);
            songBundle.putStringArrayList(getResources().getString(R.string.titlearray),titles);
            //musicService.stopSelf();
            playerIntent.putExtra(getResources().getString(R.string.songbundle),songBundle);
            startService(playerIntent);*/


            /*currentIndex = musicService.getCurrentIndex();
            musicTitle.setText(titles.get(currentIndex));
            musicArtist.setText(artists.get(currentIndex));
            musicfore.setImageBitmap(decodeAlbumArt(albumArts.get(currentIndex)));
            musicback.setImageBitmap(decodeAlbumArt(albumArts.get(currentIndex)));*/
            //updateSeekBar();
       // }else{


            /*bindService(playerIntent,serviceConnection,BIND_AUTO_CREATE);


           Bundle songBundle = new Bundle();
           songBundle.putString(getResources().getString(R.string.songdata),songdata);
           songBundle.putStringArrayList(getResources().getString(R.string.songarray),songs);
           songBundle.putInt(getResources().getString(R.string.songindex),currentIndex);
           songBundle.putStringArrayList(getResources().getString(R.string.albumarray),albums);
           songBundle.putStringArrayList(getResources().getString(R.string.artistarray),artists);
           songBundle.putStringArrayList(getResources().getString(R.string.albumartarray),albumArts);
           songBundle.putStringArrayList(getResources().getString(R.string.titlearray),titles);
           // musicService.stopSelf();
           playerIntent.putExtra(getResources().getString(R.string.songbundle),songBundle);
           startService(playerIntent);*/

           updateSeekBar();

           try {

               if (artists != null) {
                   musicArtist.setText(artists.get(currentIndex));
                   musicTitle.setText(titles.get(currentIndex));
                   musicback.setImageBitmap(blur(decodeAlbumArt(songs.get(currentIndex)), 10));
                   musicfore.setImageBitmap(decodeAlbumArt(songs.get(currentIndex)));
               }
           }catch (RSIllegalArgumentException e){

               if (artists != null) {
                   musicArtist.setText(artists.get(currentIndex));
                   musicTitle.setText(titles.get(currentIndex));
                   musicback.setImageBitmap(blur(decodeAlbumArt(songs.get(currentIndex)), 10));
                   musicfore.setImageBitmap(decodeAlbumArt(songs.get(currentIndex)));
               }

           }catch (Exception e){

               if (artists != null) {
                   musicArtist.setText(artists.get(currentIndex));
                   musicTitle.setText(titles.get(currentIndex));
                   musicback.setImageBitmap(blur(decodeAlbumArt(songs.get(currentIndex)), 10));
                   musicfore.setImageBitmap(decodeAlbumArt(songs.get(currentIndex)));
               }

           }
        //}






        fab = (ImageView) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(musicService.isPlaying()){
                    musicService.pauseMedia();
                    fab.setImageResource(R.drawable.playbutton_circle);
                    updateSeekBar();

                    onpause();
                }else if(!musicService.isPlaying()){
                    fab.setImageResource(R.drawable.pause_round_button);
                    musicService.resumeMedia();
                    updateSeekBar();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            duration = musicService.getMusicDuration();
                        }
                    }, 1000);
                }



               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        /*if(musicService.isPlaying()){
            fab.setImageResource(R.drawable.pause);
        }else if(!musicService.isPlaying()){
            fab.setImageResource(R.drawable.play);
        }*/

        nextFab = (ImageView) findViewById(R.id.nextfab);
        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.skipToNext();
                if(currentIndex==songs.size()-1){
                    currentIndex=0;
                }else{
                    currentIndex++;
                }

                musicArtist.setText(artists.get(currentIndex));
                musicTitle.setText(titles.get(currentIndex));
                musicfore.setImageBitmap(decodeAlbumArt(songs.get(currentIndex)));
                musicback.setImageBitmap(blur(decodeAlbumArt(songs.get(currentIndex)),10));
                duration = musicService.getMusicDuration();
                updateSeekBar();

                if(musicService.isPlaying()){
                    fab.setImageResource(R.drawable.pause_round_button);
                }else if(!musicService.isPlaying()){
                    fab.setImageResource(R.drawable.pause_round_button);
                }

            }
        });

        prevoisFab = (ImageView) findViewById(R.id.previousfab);
        prevoisFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.skipToprevious();
                if(currentIndex==0){
                    currentIndex=songs.size()-1;

                }else{
                    currentIndex--;
                }
                musicArtist.setText(artists.get(currentIndex));
                musicTitle.setText(titles.get(currentIndex));
                musicfore.setImageBitmap(decodeAlbumArt(songs.get(currentIndex)));
                musicback.setImageBitmap(blur(decodeAlbumArt(songs.get(currentIndex)),10));
                duration = musicService.getMusicDuration();
                updateSeekBar();

                if(musicService.isPlaying()){
                    fab.setImageResource(R.drawable.pause_round_button);
                }else if(!musicService.isPlaying()){
                    fab.setImageResource(R.drawable.pause_round_button);
                }

            }
        });



       // musicSeek.setMax(100);
        musicSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(runnable);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //handler.removeCallbacks(runnable);
               // musicService.seekTo(convertProgresstoMillisec(seekBar.getProgress(),musicService.getMusicDuration()));
                musicService.seekTo(seekBar.getProgress()*1000);
                musicSeek.setProgress(seekBar.getProgress());
                updateSeekBar();

            }
        });






        musicShuffle = (ImageView)findViewById(R.id.musicshuffle);
        musicShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffle==Shuffle.OFF){
                    musicService.onShuffle();
                    musicShuffle.setImageResource(R.drawable.shuffleblue);
                    shuffle=Shuffle.ON;
                }else if(shuffle==Shuffle.ON){
                    musicService.offShuffle();
                    musicShuffle.setImageResource(R.drawable.shuffle);
                    shuffle=Shuffle.OFF;
                }
            }
        });



        musicsongList = (ImageView)findViewById(R.id.musicsonglist);
        musicsongList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
                /*Intent intent = new Intent(MusicPlace.this,Musical.class);
                Bundle bund = new Bundle();
                bund.putInt("page",2);
                intent.putExtra("page",bund);
                startActivity(intent);*/
               // startActivity(new Intent(MusicPlace.this,Musical.class));

            }
        });

        musicPlayList = (ImageView)findViewById(R.id.musicplaylist);
        musicPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MusicPlace.this,Musical.class);
                Bundle bund = new Bundle();
                bund.putInt("page",3);
                intent.putExtra("page",bund);
                startActivity(intent);
                 //startActivity(new Intent(MusicPlace.this,Musical.class));

                //startActivity(new Intent(MusicPlace.this,PlayList.class));

            }
        });

        musicRepeat = (ImageView)findViewById(R.id.musicrepeat);
        musicRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(repeat==Repeat.ALL){
                    musicService.onRepeatOne();
                    musicService.offRepeatAll();
                    musicShuffle.setEnabled(false);
                    musicRepeat.setImageResource(R.drawable.repeat_one);
                    //musicRepeat.setBackgroundColor(getResources().getColor(R.color.blurtrans));
                    repeat=Repeat.ONE;
                }else if(repeat==Repeat.ONE){
                    musicService.offRepeatOne();
                    musicService.onRepeatAll();
                    musicShuffle.setEnabled(true);
                    musicRepeat.setImageResource(R.drawable.repeat_all);
                    musicRepeat.setBackgroundColor(getResources().getColor(R.color.transperent));
                    repeat=Repeat.ALL;
                }

            }
        });

        musickaraoeke = (ImageView)findViewById(R.id.musickaraoeke);
        musickaraoeke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(singConst.getInt()!=10) {

                    musicService.pauseMedia();

                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(MusicPlace.this, SingingStudio.class);

                    bundle.putString("singtitle", titles.get(currentIndex));
                    bundle.putString("singartist", artists.get(currentIndex));
                    bundle.putString("singdata", songs.get(currentIndex));
                    bundle.putString("singalbum", albums.get(currentIndex));

                    intent.putExtra("singingstudio", bundle);
                    startActivity(intent);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(101);
                }else {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MusicPlace.this);
                    builder.setTitle("Notice!!!");
                    builder.setIcon(R.drawable.cancelhead);
                    builder.setMessage("Sorry, this feature you want to access is currently Locked.\n" +
                            "Click the 'Unlock Button' to Unlock this feature. Thank you");
                    builder.setPositiveButton("Unlock", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(MusicPlace.this,PurchaseRoom.class));
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
                //startActivity(new Intent(MusicPlace.this,IntroKara.class));


            }
        });

        playlistMenu = (ImageView)findViewById(R.id.playlistmenu);
        playlistMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MusicPlace.this,v);
                popupMenu.inflate(R.menu.addplay);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.addplaylist: playDialog(currentIndex);
                        }
                        return true;
                    }
                });
            }
        });




        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                duration = musicService.getMusicDuration();
            }
        }, 1000);*/
    }
    @Override
    public void onStart(){
        super.onStart();
        if(serviceBound) {
            if (musicService.isPlaying()) {
                fab.setImageResource(R.drawable.pause_round_button);
            }else if(!musicService.isPlaying()){
                fab.setImageResource(R.drawable.pause_round_button);
            }
        }
    }
    @Override
    public void onResume(){
        super.onResume();
       /* if(serviceBound) {
            if (musicService.isPlaying()) {
                fab.setImageResource(R.drawable.pause);
            }else if(!musicService.isPlaying()){
                fab.setImageResource(R.drawable.pause);
            }
        }*/

    }

    public Bitmap decodeAlbumArt(String file){
        Bitmap albumArt=null;

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {

            metadataRetriever.setDataSource(file);
            byte[] art = metadataRetriever.getEmbeddedPicture();

            if (art != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(art, 0, art.length, options);
                options.inSampleSize = calculateSampleSize(options, 500, 500);
                options.inJustDecodeBounds = false;
                albumArt = BitmapFactory.decodeByteArray(art, 0, art.length,options);
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(getResources(), R.mipmap.playing, options);
                options.inSampleSize = calculateSampleSize(options, 500, 500);
                options.inJustDecodeBounds = false;

                albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.playing,options);
            }
        }catch (IllegalArgumentException e){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.mipmap.playing, options);
            options.inSampleSize = calculateSampleSize(options, 500, 500);
            options.inJustDecodeBounds = false;

            albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.playing,options);
        }catch (RuntimeException e){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.mipmap.playing, options);
            options.inSampleSize = calculateSampleSize(options, 500, 500);
            options.inJustDecodeBounds = false;

            albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.playing,options);
        }catch (Exception e){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.mipmap.playing, options);
            options.inSampleSize = calculateSampleSize(options, 500, 500);
            options.inJustDecodeBounds = false;

            albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.playing,options);
        }

       /* if(file!=null) {
            albumArt  = BitmapFactory.decodeFile(file);
        }else{
            albumArt = BitmapFactory.decodeResource(getResources(),R.mipmap.cele6);
        }*/
        return albumArt;

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

    public int convertProgresstoMillisec(int progress,int duration){
        int time = (progress/100)*duration;
        return time;
    }

    public void updateSeekBar(){


        handler.postDelayed(runnable, 100);
    }

     private Runnable runnable =new Runnable() {
        @Override
        public void run() {
           // int duration=musicService.getMusicDuration();
            int currentTime;

            if(period==Period.NOW) {
                duration = musicService.getMusicDuration();
                period= Period.LATER;
            }
            try{
                currentTime = musicService.getCurrentMusicTime();
            }catch (NullPointerException e){
                currentTime = 0;
            }


            int secf = currentTime/1000;
            int secr = duration/1000;

            musiccount.setText(convertmillsecondstoTimer(currentTime));
            musiclength.setText(convertmillsecondstoTimer(duration));
            musicSeek.setMax(secr);
           // int progress = (secf/secr)*100;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //musicSeek.incrementProgressBy((int)1.2);
               // musicSeek.setKeyProgressIncrement((int)1.2);

                musicSeek.setProgress(secf);
            }else{
                musicSeek.setProgress(secf);
            }

            handler.postDelayed(runnable,100);
            count++;
        }
    };

    public String convertmillsecondstoTimer(int millisec){
        String timing="";
        String secondstime="";
        int hours = millisec/(1000*60*60);
        int munite= (millisec%(1000*60*60))/(1000*60);
        int seconds=(millisec%(1000*60*60))%(1000*60)/1000;


        if(hours>0){
            timing=hours+":";
        }

        if(seconds<10){
            secondstime="0"+seconds;
        }else{
            secondstime=""+seconds;
        }

        String theTime=timing+munite+":"+secondstime;

        return theTime;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.music_place, menu);
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
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MyMusicService.LocalBinder binder = (MyMusicService.LocalBinder)service;
            musicService = binder.getService();
            serviceBound = true;


            //Toast.makeText(MusicPlace.this, "Service Bound", Toast.LENGTH_SHORT).show();
            currentIndex = musicService.getCurrentIndex();
            musicService.offShuffle();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound=false;

        }
    };

    private void playAudio(String media){
        // Check if service is active
        if(!serviceBound){
            Intent playerIntent = new Intent(this,MyMusicService.class);

            playerIntent.putExtra("song",media);

            startService(playerIntent);
            bindService(playerIntent,serviceConnection,BIND_AUTO_CREATE);
        }else{
            // Service is active Send media with BroadcastReciever
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putBoolean("ServiceState",serviceBound);
        savedInstanceState.putInt("CurrentIndex",currentIndex);
        savedInstanceState.putInt("duration",duration);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState ){
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound=savedInstanceState.getBoolean("ServiceState");
        currentIndex = savedInstanceState.getInt("CurrentIndex");
        duration = savedInstanceState.getInt("duration");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //if(serviceBound){

            //service is activie
            //musicService.stopSelf();

            unbindService(serviceConnection);
     //   }

        unregisterReceiver(onComplete);
        unregisterReceiver(start);
        unregisterReceiver(pausing);
    }

    private final BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra(getResources().getString(R.string.songcompletebundleintent));
            currentIndex=bundle.getInt(getResources().getString(R.string.songcompletebundleintent));
            musicArtist.setText(artists.get(currentIndex));
            musicTitle.setText(titles.get(currentIndex));
            musicfore.setImageBitmap(decodeAlbumArt(songs.get(currentIndex)));
            musicback.setImageBitmap(blur(decodeAlbumArt(songs.get(currentIndex)),10));
            duration = musicService.getMusicDuration();
            updateSeekBar();
        }
    };

    public void registerOnCompleteBroadcast(){
        IntentFilter intentFilter = new IntentFilter(getResources().getString(R.string.songcomplete));
        registerReceiver(onComplete,intentFilter);
    }

    private final BroadcastReceiver start = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle= intent.getBundleExtra("startbundle");
            currentIndex = bundle.getInt("startIndex");
            try {
                musicArtist.setText(artists.get(currentIndex));
                musicTitle.setText(titles.get(currentIndex));
                musicfore.setImageBitmap(decodeAlbumArt(songs.get(currentIndex)));
                musicback.setImageBitmap(blur(decodeAlbumArt(songs.get(currentIndex)), 10));
            }catch (IndexOutOfBoundsException e){
                currentIndex=0;
                musicArtist.setText(artists.get(currentIndex));
                musicTitle.setText(titles.get(currentIndex));
                musicfore.setImageBitmap(decodeAlbumArt(songs.get(currentIndex)));
                musicback.setImageBitmap(blur(decodeAlbumArt(songs.get(currentIndex)), 10));

            }catch (RSIllegalArgumentException e){

                currentIndex=0;
                musicArtist.setText(artists.get(currentIndex));
                musicTitle.setText(titles.get(currentIndex));
                musicfore.setImageBitmap(decodeAlbumArt(songs.get(currentIndex)));
                musicback.setImageBitmap(blur(decodeAlbumArt(songs.get(currentIndex)), 10));

            }catch (Exception e){
                currentIndex=0;
                musicArtist.setText(artists.get(currentIndex));
                musicTitle.setText(titles.get(currentIndex));
                musicfore.setImageBitmap(decodeAlbumArt(songs.get(currentIndex)));
                musicback.setImageBitmap(blur(decodeAlbumArt(songs.get(currentIndex)), 10));
            }
            //duration = musicService.getMusicDuration();


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    duration = musicService.getMusicDuration();
                }
            }, 500);
            updateSeekBar();

            if(musicService.getRepeatOne()){
                musicShuffle.setEnabled(false);
                musicRepeat.setImageResource(R.drawable.repeat_one);
                //musicRepeat.setBackgroundColor(getResources().getColor(R.color.blurtrans));
                repeat=Repeat.ONE;
            }

        }
    };

    private final BroadcastReceiver pausing = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fab.setImageResource(R.drawable.playbutton_circle);
           // duration = musicService.getMusicDuration();
        }
    };

    public void registerStart(){
        IntentFilter intentFilter = new IntentFilter("start");
        registerReceiver(start,intentFilter);

        IntentFilter intentFilt = new IntentFilter("pause");
        registerReceiver(pausing,intentFilt);
    }

    public Bitmap blur(Bitmap inbit,float BLUR_RADIUS){
        if(inbit==null) return null;

        Bitmap outbit = Bitmap.createBitmap(inbit);



        RenderScript renderScript = RenderScript.create(this);
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

    public void playDialog(final int positioning){

        ListView listView = new ListView(this);
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.playtext,R.id.playlisttextlist);
        MainPlayListDataBase mainPlayListDataBase = new MainPlayListDataBase(this,null,null,1);
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
        listView.setDividerHeight(5);
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

                        PlaylistDatabase playlistDatabase = new PlaylistDatabase(MusicPlace.this,null,null,1,taking);
                        playlistDatabase.insertSong(songs.get(positioning),titles.get(positioning),artists.get(positioning),albums.get(positioning)," albumArt");
                        Toast.makeText(MusicPlace.this, "Song Added to Playlist, you can Exit now.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });





        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

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

    public void onpause(){
        sharedPref = new SharedPref(this,getResources().getString(R.string.adsConst));
        if(sharedPref.getInt()!=10) {


            musicAd = new InterstitialAd(this);
            musicReq = new AdRequest.Builder().build();
            musicAd.setAdUnitId("ca-app-pub-2742716340205774/9544945006");

            downTimer = new CountDownTimer(15000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if(millisUntilFinished==5000){
                        Toast.makeText(MusicPlace.this, "Sorry, Ads will be shown in less than 5 secs", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFinish() {


                    musicAd.loadAd(musicReq);
                    musicAd.setAdListener(new AdListener(){
                        public void onAdLoaded(){
                            if(musicAd.isLoaded()){
                                musicAd.show();
                            }
                        }
                    });

                    /*interstitial = new MoPubInterstitial(MusicPlace.this,"7cada9c6b4f0448690c29a58b5317bfe");
                    interstitial.load();

                    interstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                        @Override
                        public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                            interstitial.show();

                        }

                        @Override
                        public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                            // startActivity(new Intent(Musical.this,LovedThings.class));

                        }

                        @Override
                        public void onInterstitialShown(MoPubInterstitial interstitial) {
                           /* new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(Musical.this,LovedThings.class));
                                }
                            }, 2000);*/

                        /*}

                        @Override
                        public void onInterstitialClicked(MoPubInterstitial interstitial) {

                        }

                        @Override
                        public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                            // startActivity(new Intent(Musical.this,LovedThings.class));

                        }
                    });*/


                    /*AppLovinSdk.initializeSdk(MusicPlace.this);
                    AppLovinSdk.getInstance(MusicPlace.this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                        @Override
                        public void adReceived(AppLovinAd appLovinAd) {
                            appLovnAd = appLovinAd;

                        }

                        @Override
                        public void failedToReceiveAd(int i) {

                        }
                    });
                    AppLovinInterstitialAdDialog adDialog = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(MusicPlace.this), MusicPlace.this);
                    adDialog.setAdClickListener(new AppLovinAdClickListener() {
                        @Override
                        public void adClicked(AppLovinAd appLovinAd) {

                        }
                    });
                    adDialog.setAdLoadListener(new AppLovinAdLoadListener() {
                        @Override
                        public void adReceived(AppLovinAd appLovinAd) {
                        }

                        @Override
                        public void failedToReceiveAd(int i) {

                        }
                    });
                    adDialog.setAdDisplayListener(new AppLovinAdDisplayListener() {
                        @Override
                        public void adDisplayed(AppLovinAd appLovinAd) {

                        }

                        @Override
                        public void adHidden(AppLovinAd appLovinAd) {

                        }
                    });
                    adDialog.showAndRender(appLovnAd);*/

                }
            };
            downTimer.start();

        }
    }

    public void onStop(){
        super.onStop();
        if(sharedPref.getInt()!=10){
            downTimer.cancel();
        }
    }
}
