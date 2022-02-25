package com.mactrix.www.musical;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.InflateException;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CollectSongs extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    ImageView headImage;
    TextView headText;
    ListView listView;
    private ArrayList<String> songs;
    private ArrayList<String> titles;
    private ArrayList<String> albumArts;
    private ArrayList<String> artists;
    private ArrayList<String> albums;
    private ArrayList<String> datas;
    private ArrayList<String> titless;
    private ArrayList<String> albumArtss;
    private ArrayList<String> artistss;
    private ArrayList<String> albumss;
    String filePic;
    String artistname;
    private RelativeLayout smallbottomsheet;
    private RelativeLayout largebottomshet;
    private CircleImageView songcircleImage;
    private TextView songTitle;
    private TextView songArtist;
    private TextView songAlbum;
    private ImageView songfloatPlay;
    private TextView bottomtextcount;
    private TextView bottomtextfinish;
    private ImageView bottomImage;
    private TextView bottomsongTitle;
    private TextView bottomsongArtist;
    private TextView album;
    private SeekBar seeking;
    private SeekBar bottomseek;
    private Handler handler;
    private MyMusicService musicService;
    private Intent serviceIntent;
    private Intent sendIntent;
    private boolean start;
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView bottomfabplay;
    private int currentIndex;
    private ImageView bottomfabnext;
    private ImageView bottomfabprevious;
    private int position;
    private AppLovinAd appLovnAd;
    private SharedPref sharedPref;
    private CountDownTimer downTimer;
    ImageView collectingimage;
    MoPubInterstitial interstitial;

    enum Play{PLAY,PAUSE,RESUME}
    enum Player{PLAY,RESUME}
    Player player;
    Play play;
    CollectiveAdapter collectiveAdapter;

    InterstitialAd collectAds;
    AdRequest collectReq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_collect_songs);
        }catch (InflateException e){
            setContentView(R.layout.activity_collect_songs);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collectingimage = (ImageView)findViewById(R.id.collectingimage);
        Bitmap imaging = BitmapFactory.decodeResource(getResources(),R.mipmap.imaging);
        collectingimage.setImageBitmap(blur(imaging,10));

        sharedPref = new SharedPref(this,getResources().getString(R.string.adsConst));
        if(sharedPref.getInt()!=10) {

            collectAds = new InterstitialAd(this);
            collectReq = new AdRequest.Builder().build();

            collectAds.setAdUnitId("ca-app-pub-2742716340205774/8179892449");



            downTimer = new CountDownTimer(25000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished == 10000) {
                        Toast.makeText(CollectSongs.this, "Sorry, Ads will be shown in less than 10 secs", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFinish() {


                    collectAds.loadAd(collectReq);
                    collectAds.setAdListener(new AdListener(){
                        public void onAdLoaded(){

                            if(collectAds.isLoaded()){
                                collectAds.show();
                            }

                        }
                    });





                    /*interstitial = new MoPubInterstitial(CollectSongs.this,"7cada9c6b4f0448690c29a58b5317bfe");
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

                      /*  }

                        @Override
                        public void onInterstitialClicked(MoPubInterstitial interstitial) {

                        }

                        @Override
                        public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                            // startActivity(new Intent(Musical.this,LovedThings.class));

                        }
                    });*/


                  /*  AppLovinSdk.initializeSdk(CollectSongs.this);
                    AppLovinSdk.getInstance(CollectSongs.this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                        @Override
                        public void adReceived(AppLovinAd appLovinAd) {
                            appLovnAd = appLovinAd;

                        }

                        @Override
                        public void failedToReceiveAd(int i) {

                        }
                    });
                    AppLovinInterstitialAdDialog adDialog = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(CollectSongs.this), CollectSongs.this);
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


            IntentFilter pauseFilter = new IntentFilter("pause");
        IntentFilter resumeFilter = new IntentFilter("resume");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        handler = new Handler();
        registerReceiver(pause,pauseFilter);
        registerReceiver(resume,resumeFilter);
        registerStartBroadcast();
        regiterResuming();

        play = Play.PLAY;
        player =Player.RESUME;
        start=false;
        registerServiceBound();

        serviceIntent = new Intent(this,MyMusicService.class);
        bindService(serviceIntent,serviceConnection,BIND_AUTO_CREATE);
        sendIntent = new Intent(getResources().getString(R.string.servicesetup));

        headImage = (ImageView) findViewById(R.id.collectheadImage);
        headText = (TextView)findViewById(R.id.collectorheadtext);
        listView = (ListView)findViewById(R.id.collectorlist);

        smallbottomsheet =(RelativeLayout)findViewById(R.id.smallbottomsheet);
        largebottomshet=(RelativeLayout)findViewById(R.id.bottomsheet);

        songcircleImage= (CircleImageView) findViewById(R.id.songcircleimage);
        songcircleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing();
            }
        });
        songTitle=(TextView) findViewById(R.id.songtitle);
        songTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing();
            }
        });
        songArtist=(TextView)findViewById(R.id.songartist);
        songArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing();
            }
        });
        songAlbum=(TextView)findViewById(R.id.songalbum);
        songAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing();
            }
        });
        songfloatPlay=(ImageView) findViewById(R.id.songfloatplay);

        bottomtextcount=(TextView)findViewById(R.id.bottomtextcount);
        bottomtextfinish=(TextView)findViewById(R.id.bottomtextfinish);

        bottomImage=(ImageView)findViewById(R.id.bottomimage);
        bottomsongTitle=(TextView)findViewById(R.id.buttomsongtitle);
        bottomsongTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing();
            }
        });
        bottomsongArtist=(TextView)findViewById(R.id.buttomsongartist);
        bottomsongArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing();
            }
        });
        album=(TextView)findViewById(R.id.album);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing();
            }
        });

        seeking = (SeekBar)findViewById(R.id.seeking);
        bottomseek=(SeekBar)findViewById(R.id.bottomseek);


        Bundle bundle = getIntent().getBundleExtra(getResources().getString(R.string.songbundle));

        if(bundle!=null) {
            // songdata = bundle.getString(getResources().getString(R.string.songdata));
            songs = bundle.getStringArrayList(getResources().getString(R.string.songarray));

            //currentPlace= bundle.getInt(getResources().getString(R.string.songindex));
            ///currentIndex=currentPlace;
            // currentIndex = musicService.getCurrentIndex();

            // title = bundle.getString(getResources().getString(R.string.title));
            titles = bundle.getStringArrayList(getResources().getString(R.string.titlearray));

            // albumart = bundle.getString(getResources().getString(R.string.albumart));
            albumArts = bundle.getStringArrayList(getResources().getString(R.string.albumartarray));

            // artistName = bundle.getString(getResources().getString(R.string.artist));
            artists = bundle.getStringArrayList(getResources().getString(R.string.artistarray));

            //album = bundle.getString(getResources().getString(R.string.album));
            albums = bundle.getStringArrayList(getResources().getString(R.string.albumarray));
            filePic = bundle.getString("filepic");
            artistname = bundle.getString("artistname");
        }

        headImage.setImageBitmap(decodeAlbumArt(filePic));
        headText.setText(artistname);
        collectiveAdapter = new CollectiveAdapter(this,songs,titles,albums,artists,albumArts);
        listView.setAdapter(collectiveAdapter);




        bottomseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if(datas!=null) {
                    handler.removeCallbacks(runnable);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // handler.removeCallbacks(runnable);
                //musicService.seekTo(convertProgresstoMillisec(seekBar.getProgress(),musicService.getMusicDuration()));
                if(datas!=null) {
                    musicService.seekTo(seekBar.getProgress() * 1000);
                    bottomseek.setProgress(seekBar.getProgress());
                    updateSeekBar();
                }else{
                    Toast.makeText(CollectSongs.this, "No Song Playing. Please Selete a Song..", Toast.LENGTH_SHORT).show();
                }

            }
        });

        bottomfabplay=(ImageView) findViewById(R.id.bottomfabplay);
        bottomfabplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!musicService.isPlaying()) {
                    // musicService.playMedia(currentIndex);
                    if (player ==Player.PLAY) {
                        musicService.playMedia(currentIndex);
                        player = Player.RESUME;
                    } else if (player == Player.RESUME){
                        musicService.resumeMedia();}
                    bottomfabplay.setImageResource(R.drawable.pause_round_button);

                    play = Play.PAUSE;
                } else if (musicService.isPlaying()) {
                    musicService.pauseMedia();
                    bottomfabplay.setImageResource(R.drawable.playbutton_circle);
                    play =Play.PLAY;
                }

                    /* if (play == Play.PLAY) {

                         musicService.playMedia();

                         play = Play.PAUSE;
                     } else if (play == Play.PAUSE) {
                         musicService.pauseMedia();
                         play = Play.PLAY;
                     }*/


            }
        });

        bottomfabnext=(ImageView) findViewById(R.id.bottomfabnext);
        bottomfabnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                musicService.skipToNext();
                // currentIndex = musicService.getCurrentIndex();

            }
        });

        bottomfabprevious=(ImageView) findViewById(R.id.bottomfabprevious);
        bottomfabprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                musicService.skipToprevious();


            }
        });


        songfloatPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!musicService.isPlaying()) {

                    //musicService.playMedia(currentIndex);
                    if(player == Player.PLAY){
                        musicService.playMedia(currentIndex);
                        player= Player.RESUME;
                    }else if(player== Player.RESUME) {
                        musicService.resumeMedia();
                    }
                    songfloatPlay.setImageResource(R.drawable.pause_round_button);

                    play = Play.PAUSE;
                } else if (musicService.isPlaying()) {
                    musicService.pauseMedia();
                    songfloatPlay.setImageResource(R.drawable.playbutton_circle);
                    play =Play.PLAY;
                }

            }
        });

        /*listView = (ListView)findViewById(R.id.songlistview);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            loadSongs();
            songListAdapter = new SongListAdapter(this,mySongs,musicService);
            listView.setAdapter(songListAdapter);

        }*/

        //songListAdapter = new SongListAdapter(this,)

        View v = findViewById(R.id.bottommainsheet);

        /*largebottomshet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing();
            }
        });*/

      /*  smallbottomsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sing();

            }
        });*/

        bottomSheetBehavior = BottomSheetBehavior.from(v);
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setHideable(false);


        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_COLLAPSED: largebottomshet.setVisibility(View.GONE);
                        smallbottomsheet.setVisibility(View.VISIBLE);

                        bottomSheetBehavior.setPeekHeight(200);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: largebottomshet.setVisibility(View.VISIBLE);
                        smallbottomsheet.setVisibility(View.GONE);
                        bottomSheetBehavior.setPeekHeight(200);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });




        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    public void onResume(){
        super.onResume();
        position = listView.getVerticalScrollbarPosition();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(collectiveAdapter);
                listView.setVerticalScrollbarPosition(position);
            }
        }, 1000);

    }
    @Override
    public void onStart(){
        super.onStart();
        listView.setAdapter(collectiveAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.collect_songs, menu);
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

    public void sing(){
        if(datas!=null) {
            Intent intent = new Intent(CollectSongs.this, MusicPlace.class);

            Bundle bundle = new Bundle();

            bundle.putStringArrayList(getResources().getString(R.string.songarray), datas);

            bundle.putInt(getResources().getString(R.string.songindex), currentIndex);


            bundle.putStringArrayList(getResources().getString(R.string.titlearray), titless);


            bundle.putStringArrayList(getResources().getString(R.string.artistarray), artistss);

            // bundle.putString(context.getResources().getString(R.string.albumart), albumArt);
            //bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArts);


            bundle.putStringArrayList(getResources().getString(R.string.albumarray), albumss);

            intent.putExtra(getResources().getString(R.string.songbundle), bundle);


            // context.stopService(player);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Please Selete a Song", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateSeekBar(){
        handler.postDelayed(runnable, 100);
    }

    private int count;
    private int duration;
    private int currentTime;
    private Runnable runnable =new Runnable() {
        @Override
        public void run() {
            // int duration=musicService.getMusicDuration();


            if(count==0) {
                duration = musicService.getMusicDuration();
            }



            currentTime = musicService.getCurrentMusicTime();



            int secf = currentTime/1000;
            int secr = duration/1000;

            bottomtextcount.setText(convertmillsecondstoTimer(currentTime));
            bottomtextfinish.setText(convertmillsecondstoTimer(duration));
            bottomseek.setMax(secr);
            seeking.setMax(secr);
            // int progress = (secf/secr)*100;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //musicSeek.incrementProgressBy((int)1.2);
                // musicSeek.setKeyProgressIncrement((int)1.2);

                bottomseek.setProgress(secf);
                seeking.setProgress(secf);
            }else{
                bottomseek.setProgress(secf);
                seeking.setProgress(secf);
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

    public void registerServiceBound(){
        IntentFilter filter = new IntentFilter("servicebound");
        registerReceiver(serviceBound,filter);
    }


    public void registerStartBroadcast(){
        IntentFilter filter = new IntentFilter("start");
        registerReceiver(startBroadcast,filter);



    }

    private final BroadcastReceiver serviceBound = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            datas = musicService.getSongsArray();
            titless = musicService.getTitleArray();
            artistss = musicService.getArtistArray();
            albumss = musicService.getAlbumArray();
            currentIndex = musicService.getCurrentIndex();

            if(datas!=null) {

                songcircleImage.setImageBitmap(decodealbumArt(datas.get(currentIndex)));
                songTitle.setText(titless.get(currentIndex));
                songArtist.setText(artistss.get(currentIndex));
                songAlbum.setText(albumss.get(currentIndex));

                bottomImage.setImageBitmap(decodealbumArt(datas.get(currentIndex)));
                bottomsongTitle.setText(titless.get(currentIndex));
                bottomsongArtist.setText(artistss.get(currentIndex));
                album.setText(albumss.get(currentIndex));
                if (musicService.isPlaying()) {
                    updateSeekBar();
                    songfloatPlay.setImageResource(R.drawable.pause_round_button);
                    bottomfabplay.setImageResource(R.drawable.pause_round_button);
                }else{
                    songfloatPlay.setImageResource(R.drawable.playbutton_circle);
                    bottomfabplay.setImageResource(R.drawable.playbutton_circle);
                }

                player = Player.RESUME;
            }




        }
    };





    private final BroadcastReceiver startBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle= intent.getBundleExtra("startbundle");
            /*String title = bundle.getString("starttitle");
            String artist = bundle.getString("startartist");
            String albums = bundle.getString("startalbum");
            String albumArt=bundle.getString("startalbumart");
            start = bundle.getBoolean("startboolean");*/

            currentIndex = bundle.getInt("startIndex");

            datas = musicService.getSongsArray();
            titless = musicService.getTitleArray();
            artistss = musicService.getArtistArray();
            albumss = musicService.getAlbumArray();

            songcircleImage.setImageBitmap(decodealbumArt(datas.get(currentIndex)));
            songTitle.setText(titless.get(currentIndex));
            songArtist.setText(artistss.get(currentIndex));
            songAlbum.setText(albumss.get(currentIndex));

            bottomImage.setImageBitmap(decodealbumArt(datas.get(currentIndex)));
            bottomsongTitle.setText(titless.get(currentIndex));
            bottomsongArtist.setText(artistss.get(currentIndex));
            album.setText(albumss.get(currentIndex));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    duration = musicService.getMusicDuration();
                }
            }, 500);

            updateSeekBar();


            songfloatPlay.setImageResource(R.drawable.pause_round_button);
            bottomfabplay.setImageResource(R.drawable.pause_round_button);

            player= Player.RESUME;
            Intent intenting = new Intent("resuming");
            context.sendBroadcast(intenting);

        }
    };

    public Bitmap decodealbumArt(String file){
        Bitmap albumArt=null;

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();

        try {
            metadataRetriever.setDataSource(file);
            byte[] art = metadataRetriever.getEmbeddedPicture();

            if (art != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(art, 0, art.length, options);
                options.inSampleSize = calculateSampleSize(options, 200, 200);
                options.inJustDecodeBounds = false;
                albumArt = BitmapFactory.decodeByteArray(art, 0, art.length, options);
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(getResources(), R.mipmap.playingheading, options);
                options.inSampleSize = calculateSampleSize(options, 200, 200);
                options.inJustDecodeBounds = false;

                albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.playingheading, options);
            }

        }catch (IllegalArgumentException e){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.mipmap.playingheading, options);
            options.inSampleSize = calculateSampleSize(options, 200, 200);
            options.inJustDecodeBounds = false;

            albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.playingheading, options);
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
        return albumArt;

    }



    public Bitmap decodeAlbumArt(String file){
        Bitmap albumArt = null;



            try {
                if (file != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file, options);
                    options.inSampleSize = calculateSampleSize(options, 500, 500);
                    options.inJustDecodeBounds = false;
                    albumArt = BitmapFactory.decodeFile(file, options);


                } else {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(getResources(), R.mipmap.playing, options);
                    options.inSampleSize = calculateSampleSize(options, 500, 500);
                    options.inJustDecodeBounds = false;
                    albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.playing, options);

                }
            } catch (IllegalArgumentException e) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(getResources(), R.mipmap.playing, options);
                options.inSampleSize = calculateSampleSize(options, 500, 500);
                options.inJustDecodeBounds = false;
                albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.playing, options);
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


    public boolean isServiceBound;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyMusicService.LocalBinder localBinder = (MyMusicService.LocalBinder)service;
            musicService = localBinder.getService();
            isServiceBound = true;

            Intent intent = new Intent("servicebound");
            sendBroadcast(intent);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;

        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("CurrentIndex",currentIndex);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState ){
        super.onRestoreInstanceState(savedInstanceState);
        currentIndex = savedInstanceState.getInt("CurrentIndex");

    }

    BroadcastReceiver pause = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            songfloatPlay.setImageResource(R.drawable.playbutton_circle);
            bottomfabplay.setImageResource(R.drawable.playbutton_circle);
        }
    };
    BroadcastReceiver resume = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            songfloatPlay.setImageResource(R.drawable.pause_round_button);
            bottomfabplay.setImageResource(R.drawable.pause_round_button);
        }
    };
    @Override
    public void onDestroy(){
        super.onDestroy();



        //NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //notificationManager.cancel(101);
        //musicService.stopMedia();
        //musicService.mediaPlayer.release();
        //musicService.stopSelf();
        unregisterReceiver(serviceBound);
        unregisterReceiver(startBroadcast);
        unregisterReceiver(pause);
        unregisterReceiver(resuming);
       // SharedPref sharedPref = new SharedPref(this,"ServiceControl");
        //sharedPref.setInt(-1);
        unregisterReceiver(resume);
        unbindService(serviceConnection);






    }
    private final BroadcastReceiver resuming = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }
    };

    public void regiterResuming(){
        IntentFilter intentFilter = new IntentFilter("resuming");
        registerReceiver(resuming,intentFilter);
    }
    @Override
    public void onStop(){
        super.onStop();
        if(sharedPref.getInt()!=10){
            downTimer.cancel();
        }

    }
    @Override
    public void onPause(){
        super.onPause();

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


}
