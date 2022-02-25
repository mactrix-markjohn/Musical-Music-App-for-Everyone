package com.mactrix.www.musical;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinAdType;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Musical extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<MySongs> mySongs;
    BottomSheetBehavior bottomSheetBehavior;
    int currentTime;
    RelativeLayout smallbottomsheet;
    RelativeLayout largebottomshet;

    CircleImageView songcircleImage;
    TextView songTitle;
    TextView songArtist;
    TextView songAlbum;
    ImageView songfloatPlay;
    enum Player{PLAY,RESUME}
    Player player;

    ImageView bottomImage;
    TextView bottomsongTitle;
    TextView bottomsongArtist;
    TextView album;
    SeekBar seeking;
    SeekBar bottomseek;
    TextView bottomtextcount;
    TextView bottomtextfinish;
    ImageView bottomfabplay;
    ImageView bottomfabnext;
    ImageView bottomfabprevious;
    MyMusicService musicService;
    boolean isServiceBound;
    Intent serviceIntent;
    Intent sendIntent;
    private ArrayList<String> datas;
    private ArrayList<String> titles;
    private ArrayList<String> artists;
    private ArrayList<String> albums;
    private ArrayList<String> albumArts;
    private ArrayList<String> albuming;
    private ArrayList<String> albumKey;

    enum Play{PLAY,PAUSE,RESUME}
    Play play;
    boolean start;
    private Handler handler;
    ListView listView;
    SongListAdapter songListAdapter;
    int currentIndex;
    AppLovinAd appLovnAd;
    SharedPref sharedPref;
    CountDownTimer downTimer;
    MoPubInterstitial interstitial;

    InterstitialAd musicalAds;
    AdRequest musicalReq;





    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this,"202870072",true);
        try{
            setContentView(R.layout.activity_musical);

        }catch (InflateException e){
            setContentView(R.layout.activity_musical);

        }catch (Exception e){
            setContentView(R.layout.activity_musical);
        }

        StartAppAd.disableSplash();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this,"ca-app-pub-2742716340205774~1702114009");






        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},12);
        IntentFilter pauseFilter = new IntentFilter("pause");
        IntentFilter resumeFilter = new IntentFilter("resume");
        handler = new Handler();
        registerReceiver(pause,pauseFilter);
        registerReceiver(resume,resumeFilter);

        sharedPref = new SharedPref(this,getResources().getString(R.string.adsConst));
        if(sharedPref.getInt()!=10) {


            musicalAds = new InterstitialAd(this);
            musicalAds.setAdUnitId("ca-app-pub-2742716340205774/3215857298");
            musicalReq = new AdRequest.Builder().build();


            downTimer = new CountDownTimer(25000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished == 10000) {
                        Toast.makeText(Musical.this, "Sorry, Ads will be shown in less than 10 secs", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFinish() {


                    musicalAds.loadAd(musicalReq);
                    musicalAds.setAdListener(new AdListener(){

                        public void onAdLoaded(){
                          if(musicalAds.isLoaded()){
                              musicalAds.show();
                          }
                        }
                    });


                    /*interstitial = new MoPubInterstitial(Musical.this,"7cada9c6b4f0448690c29a58b5317bfe");
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

                        /*   }

                        @Override
                        public void onInterstitialClicked(MoPubInterstitial interstitial) {

                        }

                        @Override
                        public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                           // startActivity(new Intent(Musical.this,LovedThings.class));

                        }
                    });*/

                   /* AppLovinSdk.initializeSdk(Musical.this);
                    AppLovinSdk.getInstance(Musical.this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                        @Override
                        public void adReceived(AppLovinAd appLovinAd) {
                            appLovnAd = appLovinAd;

                        }

                        @Override
                        public void failedToReceiveAd(int i) {

                        }
                    });
                    AppLovinInterstitialAdDialog adDialog = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(Musical.this), Musical.this);
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

        play = Play.PLAY;
        player = Player.RESUME;
        start=false;
        registerServiceBound();

        //bind service
        serviceIntent = new Intent(this,MyMusicService.class);
        bindService(serviceIntent,serviceConnection,BIND_AUTO_CREATE);
        sendIntent = new Intent(getResources().getString(R.string.servicesetup));



       /* Bundle songBundle = new Bundle();
        //songBundle.putString(context.getResources().getString(R.string.songdata),data);
        songBundle.putStringArrayList(getResources().getString(R.string.songarray),datas);
        //songBundle.putInt(context.getResources().getString(R.string.songindex),position);
        songBundle.putStringArrayList(getResources().getString(R.string.albumarray),albums);
        songBundle.putStringArrayList(getResources().getString(R.string.artistarray),artists);
        songBundle.putStringArrayList(getResources().getString(R.string.albumartarray),albumArts);
        songBundle.putStringArrayList(getResources().getString(R.string.titlearray),titles);

        sendIntent.putExtra(getResources().getString(R.string.songbundle),songBundle);
        sendBroadcast(sendIntent);*/

        //startService(serviceIntent);

        smallbottomsheet =(RelativeLayout)findViewById(R.id.smallbottomsheet);
       largebottomshet=(RelativeLayout)findViewById(R.id.bottomsheet);




        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);


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
         //bottomseek.setMax(100);
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
                     Toast.makeText(Musical.this, "There is no Song playing. Selete a Song", Toast.LENGTH_SHORT).show();
                 }

             }
         });




         bottomfabplay=(ImageView) findViewById(R.id.bottomfabplay);
         bottomfabplay.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {


                 if (!musicService.isPlaying()) {
                     // musicService.playMedia(currentIndex);
                     if (player == Player.PLAY) {
                         musicService.playMedia(currentIndex);
                         player = Player.RESUME;
                     } else if (player == Player.RESUME){
                         musicService.resumeMedia();}
                     bottomfabplay.setImageResource(R.drawable.pause_round_button);

                     play = Play.PAUSE;
                 } else if (musicService.isPlaying()) {
                     musicService.pauseMedia();
                     bottomfabplay.setImageResource(R.drawable.playbutton_circle);
                     play = Play.PLAY;
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
                            player=Player.RESUME;
                        }else if(player==Player.RESUME) {
                            musicService.resumeMedia();
                        }
                        songfloatPlay.setImageResource(R.drawable.pause_round_button);

                        play = Play.PAUSE;
                    } else if (musicService.isPlaying()) {
                        musicService.pauseMedia();
                        songfloatPlay.setImageResource(R.drawable.playbutton_circle);
                        play = Play.PLAY;
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


        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.music));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.artist));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.album));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.playinglist));




        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        PagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        /*final SongList songList = new SongList();
        FragmentManager fragmentManager = getSupportFragmentManager();
         final FragmentTransaction  transaction = fragmentManager.beginTransaction();*/


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

               /* switch (tab.getPosition()){




                }*/
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


            Bundle pagerBundle = getIntent().getBundleExtra("page");
            if(pagerBundle!=null){
                viewPager.setCurrentItem(pagerBundle.getInt("page"));
            }





        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        registerStartBroadcast();
    }

    public void sing(){

        if(datas!=null) {

            Intent intent = new Intent(Musical.this, MusicPlace.class);

            Bundle bundle = new Bundle();

            bundle.putStringArrayList(getResources().getString(R.string.songarray), datas);

            bundle.putInt(getResources().getString(R.string.songindex), currentIndex);


            bundle.putStringArrayList(getResources().getString(R.string.titlearray), titles);


            bundle.putStringArrayList(getResources().getString(R.string.artistarray), artists);

            // bundle.putString(context.getResources().getString(R.string.albumart), albumArt);
            //bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray), albumArts);


            bundle.putStringArrayList(getResources().getString(R.string.albumarray), albums);

            intent.putExtra(getResources().getString(R.string.songbundle), bundle);


            // context.stopService(player);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Please Selete a Song...", Toast.LENGTH_SHORT).show();
        }
    }

    public void loading(){
        loadSongs();
        datas = new ArrayList<>();
        titles = new ArrayList<>();
        artists = new ArrayList<>();
        albums = new ArrayList<>();
        albumArts = new ArrayList<>();
        albuming=new ArrayList<>();
        albumKey = new ArrayList<>();

        for(int i=0;i<mySongs.size();i++){
            datas.add(mySongs.get(i).getData());
            titles.add(mySongs.get(i).getTitle());
            artists.add(mySongs.get(i).getArtist());
            albums.add(mySongs.get(i).getAlbum());
            albumArts.add(mySongs.get(i).getAlbumArt());
            albuming.add(mySongs.get(i).getAlbuming());
            albumKey.add(mySongs.get(i).getAlbumKey());
        }

    }

    public int convertProgresstoMillisec(int progress,int duration){
        int time = (progress/100)*duration;
        return time;
    }

    public void updateSeekBar(){
        handler.postDelayed(runnable, 100);
    }

    private int count;
    private int duration;
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


   /* private Runnable runnable =new Runnable() {
        @Override
        public void run() {
            int duration=musicService.getMusicDuration();
            int currentTime = musicService.getCurrentMusicTime();

            bottomtextcount.setText(convertmillsecondstoTimer(currentTime));
            bottomtextfinish.setText(convertmillsecondstoTimer(duration));

            int progress = (currentTime/duration)*100;
            bottomseek.setProgress(progress);

            handler.postDelayed(runnable,100);
        }
    };*/

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
            titles = musicService.getTitleArray();
            artists = musicService.getArtistArray();
            albums = musicService.getAlbumArray();
            currentIndex = musicService.getCurrentIndex();

            if(datas!=null) {

                songcircleImage.setImageBitmap(decodeAlbumArt(datas.get(currentIndex)));
                songTitle.setText(titles.get(currentIndex));
                songArtist.setText(artists.get(currentIndex));
                songAlbum.setText(albums.get(currentIndex));

                bottomImage.setImageBitmap(decodeAlbumArt(datas.get(currentIndex)));
                bottomsongTitle.setText(titles.get(currentIndex));
                bottomsongArtist.setText(artists.get(currentIndex));
                album.setText(albums.get(currentIndex));
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
            if(bundle!=null) {

                currentIndex = bundle.getInt("startIndex");

                datas = musicService.getSongsArray();
                titles = musicService.getTitleArray();
                artists = musicService.getArtistArray();
                albums = musicService.getAlbumArray();

                songcircleImage.setImageBitmap(decodeAlbumArt(datas.get(currentIndex)));
                songTitle.setText(titles.get(currentIndex));
                songArtist.setText(artists.get(currentIndex));
                songAlbum.setText(albums.get(currentIndex));

                bottomImage.setImageBitmap(decodeAlbumArt(datas.get(currentIndex)));
                bottomsongTitle.setText(titles.get(currentIndex));
                bottomsongArtist.setText(artists.get(currentIndex));
                album.setText(albums.get(currentIndex));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        duration = musicService.getMusicDuration();
                    }
                }, 500);

                updateSeekBar();


                songfloatPlay.setImageResource(R.drawable.pause_round_button);
                bottomfabplay.setImageResource(R.drawable.pause_round_button);

                player = Player.RESUME;
                Intent intenting = new Intent("resuming");
                context.sendBroadcast(intenting);
            }

        }
    };

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
                options.inSampleSize = calculateSampleSize(options, 200, 200);
                options.inJustDecodeBounds = false;
                albumArt = BitmapFactory.decodeByteArray(art, 0, art.length, options);
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(getResources(), R.mipmap.playing, options);
                options.inSampleSize = calculateSampleSize(options, 200, 200);
                options.inJustDecodeBounds = false;

                albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.playing, options);
            }

        }catch (IllegalArgumentException e){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.mipmap.playing, options);
            options.inSampleSize = calculateSampleSize(options, 200, 200);
            options.inJustDecodeBounds = false;

            albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.playing, options);
        }catch (IllegalStateException e){
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
        return albumArt;

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setMessage("Are you sure, you want to Exit this Awesome App.");
            builder.setPositiveButton("Yes, Of course", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(sharedPref.getInt()!=10){
                        StartAppAd.onBackPressed(Musical.this);
                    }
                    Musical.super.onBackPressed();
                    Toast.makeText(Musical.this, "Then Goodbye Fella. See ya when I see ya..", Toast.LENGTH_LONG).show();

                }
            });

            builder.setNegativeButton("No !!!!, I want more fun", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Toast.makeText(Musical.this, "Ha!!!, Then what are you waiting for, Have more Fun!!!", Toast.LENGTH_LONG).show();
                }
            });

            builder.setNeutralButton("Check out Our other Apps", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Musical.this,LovedThings.class));
                    Toast.makeText(Musical.this, "Thanks for trying to know more about Us. Have fun with your Music...", Toast.LENGTH_LONG).show();
                }
            });



            builder.create().show();




        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.musical, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }else*/ if(id==R.id.searchmenu) {
            startActivity(new Intent(this,Search.class));
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
            musicService.pauseMedia();
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(101);
            startActivity(new Intent(Musical.this,IntroKara.class));

        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(this,LyricsStudio.class));

        } else if (id == R.id.nav_manage) {

            startActivity(new Intent(this,LyricsStore.class));

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.mactrix.www.musical");
            startActivity(Intent.createChooser(intent,"Complete this Action with one of the Following..."));

        } else if (id == R.id.nav_send) {

            Intent send = new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.mactrix.www.musical"));
            startActivity(send);

        }else  if(id == R.id.loved){
            startActivity(new Intent(this,LovedThings.class));
            //interstitial.show();
        }else if(id==R.id.video){
            try{

                Intent intent = new Intent("com.mactrix.www.VideoMotion");
                startActivity(intent);

            }catch (ActivityNotFoundException e){

                musicalDialog();

            }

        }else if(id==R.id.removingads){
            startActivity(new Intent(this,PurchaseRoom.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadSongs(){
        ContentResolver contentResolver = getContentResolver();
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



        }
        cursor.close();


    }

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



        try {

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(101);
            musicService.stopMedia();
            musicService.mediaPlayer.release();
            musicService.stopSelf();
            unregisterReceiver(serviceBound);
            unregisterReceiver(startBroadcast);
            unregisterReceiver(pause);
            SharedPref sharedPref = new SharedPref(Musical.this, "ServiceControl");
            sharedPref.setInt(-1);
            unregisterReceiver(resume);
            unbindService(serviceConnection);
        }catch (NullPointerException e){

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(101);
            unregisterReceiver(serviceBound);
            unregisterReceiver(startBroadcast);
            unregisterReceiver(pause);
            SharedPref sharedPref = new SharedPref(Musical.this, "ServiceControl");
            sharedPref.setInt(-1);
            unregisterReceiver(resume);
            unbindService(serviceConnection);

        }catch (Exception e){
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(101);
            unregisterReceiver(serviceBound);
            unregisterReceiver(startBroadcast);
            unregisterReceiver(pause);
            SharedPref sharedPref = new SharedPref(Musical.this, "ServiceControl");
            sharedPref.setInt(-1);
            unregisterReceiver(resume);
            unbindService(serviceConnection);
        }






    }

    @Override
    public void onStop(){
        super.onStop();

        if(sharedPref.getInt()!=10){
            downTimer.cancel();
        }

        //downTimer.cancel();
    }

    public void musicalDialog(){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Plugin Not Found");
        builder.setIcon(R.drawable.video);
        builder.setMessage("Sorry, the Video Plugin can not be found in you device. But do not worry " +
                "Click the 'Get Plugin' to get the Plugin and have fun with your favourite Video. Thank you...");
        builder.setPositiveButton("Get Plugin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent send = new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.mactrix.www.videomotion"));
                startActivity(send);
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
}
