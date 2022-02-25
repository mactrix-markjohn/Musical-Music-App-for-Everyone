package com.mactrix.www.musical;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.renderscript.RenderScript;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.adview.AppLovinAdView;
import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class SingingStudio extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView studioBack;
    TextView studioTitle;
    TextView studioArtist;
    ImageView studioFore;
    RelativeLayout studioRelativeLyrics;
    EditText studioLyricSpace;
    Button studioSpaceSave;
    ImageView studioRecord;
    FloatingActionButton studioPlay;
    FloatingActionButton stopFab;
    SeekBar studioSeek;
    TextView studioCount;
    TextView studioFinish;
    ImageView studioMusic;
    ImageView studioLyrics;
    ImageView studioSingList;
    ImageView studioLyricsStore;
    ImageView studioLyricsTablet;
    ImageView studioMusicVolume;
    ImageView studioBacking;
    RelativeLayout studiorelativeCountDown;
    TextView studioCountDown;
    ImageView studioextra;
    EditText editText;
    private LyricsDatabase lyricsDatabase;
    private String lyricss;
    private String titling;
    private long id;
    String savetitle;
    String path;
    private SharedPref sharedPref;
    private CountDownTimer downTimer;
    private AppLovinAd appLovnAd;
    CountDownTimer countDownTimer;
    MoPubInterstitial interstitial;

    enum Period{NOW,LATER}
    Period period;
    enum SetP{THEPLAY,RPLAY}
    SetP setP;

    enum Saving{SAVE,UPDATE}
    Saving saving;

    enum RecordPlay{PLAY,RESUME}
    RecordPlay recordPlay;

    enum LyricState{VISIBLE,GONE}
    LyricState lyricState;

    String title;
    String artist;
    int songresID;
    String data;
    String album;

    MusicStudio musicStudio;
    RecordStudio recordStudio;
    private Handler handler;

    enum Timing{NOT,TIME}
    Timing timing;

    Bundle singingBundle;
    Bundle singingStudio;

    InterstitialAd singingAd;
    AdRequest singingReq;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_singing_studio);

        }catch (InflateException e){
            setContentView(R.layout.activity_singing_studio);

        }


        sharedPref = new SharedPref(this,getResources().getString(R.string.adsConst));
        if(sharedPref.getInt()!=10) {

            singingAd = new InterstitialAd(this);
            singingReq = new AdRequest.Builder().build();
            singingAd.setAdUnitId("ca-app-pub-2742716340205774/8179892449");

            downTimer = new CountDownTimer(25000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished == 10000) {
                        Toast.makeText(SingingStudio.this, "Sorry, Ads will be shown in less than 10 secs", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFinish() {


                    singingAd.loadAd(singingReq);
                    singingAd.setAdListener(new AdListener(){
                        public void onAdLoaded(){
                            if(singingAd.isLoaded()){
                                singingAd.show();
                            }
                        }
                    });

                    /*interstitial = new MoPubInterstitial(SingingStudio.this,"7cada9c6b4f0448690c29a58b5317bfe");
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


                  /*  AppLovinSdk.initializeSdk(SingingStudio.this);
                    AppLovinSdk.getInstance(SingingStudio.this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                        @Override
                        public void adReceived(AppLovinAd appLovinAd) {
                            appLovnAd = appLovinAd;

                        }

                        @Override
                        public void failedToReceiveAd(int i) {

                        }
                    });
                    AppLovinInterstitialAdDialog adDialog = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(SingingStudio.this), SingingStudio.this);
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







        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handler = new Handler();
        registerOnComplete();
        registerHeadSet();
        registerNotice();
        registerStop();
        lyricState = LyricState.GONE;
        saving = Saving.SAVE;
        recordPlay = RecordPlay.PLAY;
        setP = SetP.THEPLAY;






        lyricsDatabase = new LyricsDatabase(this,null,null,1);

        musicStudio = new MusicStudio(this);
        recordStudio = new RecordStudio(this);

        studiorelativeCountDown = (RelativeLayout)findViewById(R.id.studiorelatviecountdown);
        studioextra = (ImageView)findViewById(R.id.studioextra);
        studioextra.setEnabled(false);
        studioCountDown = (TextView)findViewById(R.id.studiocountdown);
        studioBack = (ImageView)findViewById(R.id.studioback);
        studioTitle = (TextView)findViewById(R.id.studiotitle);
        studioArtist = (TextView)findViewById(R.id.studioartist);
        studioFore = (ImageView)findViewById(R.id.studiofore);
        studioRelativeLyrics = (RelativeLayout)findViewById(R.id.studiorelativelyrics);
        studioLyricSpace = (EditText)findViewById(R.id.studiolyricsspace);
        studioSpaceSave = (Button)findViewById(R.id.studiospacesave);
        studioRecord = (ImageView)findViewById(R.id.studiorecord);
        studioPlay = (FloatingActionButton)findViewById(R.id.studioplay);
        studioSeek = (SeekBar)findViewById(R.id.studioseek);
        studioCount = (TextView)findViewById(R.id.studiocount);
        studioFinish=(TextView)findViewById(R.id.studiofinish);
        studioMusic = (ImageView)findViewById(R.id.studiomusic);
        studioLyrics=(ImageView)findViewById(R.id.studiolyrics);
        studioSingList=(ImageView)findViewById(R.id.studiosinglist);
        studioSingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicStudio.pauseMedia();
                startActivity(new Intent(SingingStudio.this,SingList.class));
            }
        });
        studioLyricsStore=(ImageView)findViewById(R.id.studiolyricsstore);
        studioLyricsStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicStudio.pauseMedia();
                startActivity(new Intent(SingingStudio.this,LyricsStore.class));
            }
        });
        studioLyricsTablet = (ImageView)findViewById(R.id.studiolyricstablet);
        studioLyricsTablet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicStudio.pauseMedia();
                startActivity(new Intent(SingingStudio.this,LyricsStudio.class));
            }
        });
        studioMusicVolume = (ImageView)findViewById(R.id.studiomusicvolume);
        studioBacking = (ImageView)findViewById(R.id.studiobacking);
        studioBacking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        stopFab = (FloatingActionButton) findViewById(R.id.fab);

        singingBundle = getIntent().getBundleExtra("singingbundle");
        singingStudio = getIntent().getBundleExtra("singingstudio");

        if(singingBundle!=null){
            title = singingBundle.getString("singtitle");
            artist = singingBundle.getString("singartist");
            songresID=singingBundle.getInt("singres");

            singingBundlePath(title,artist,songresID);
        }

        if(singingStudio!=null){
            title= singingStudio.getString("singtitle");
            artist = singingStudio.getString("singartist");
            data = singingStudio.getString("singdata");

            singingStudioPath(title,artist,data);
        }

        studioSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicStudio.seekTo(seekBar.getProgress()*1000);
                studioSeek.setProgress(seekBar.getProgress());
                updateSeekBar();
            }
        });

        studioLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lyricState==LyricState.GONE){
                    studioRelativeLyrics.setVisibility(View.VISIBLE);
                    lyricState=LyricState.VISIBLE;
                }else if(lyricState==LyricState.VISIBLE){
                    studioRelativeLyrics.setVisibility(View.GONE);
                    lyricState=LyricState.GONE;
                }
            }
        });

        studioSpaceSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveDialog();
            }
        });


        studioMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicStudio.isPlaying()){
                    musicStudio.pauseMedia();
                    studioMusic.setImageResource(R.drawable.playmusic);
                    updateSeekBar();
                }else if(!musicStudio.isPlaying()){
                    musicStudio.resumeMedia();
                    studioMusic.setImageResource(R.drawable.ic_pause);
                    updateSeekBar();
                }
            }
        });

        studioMusicVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volumeSetUp();
            }
        });

        studioextra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordSave();
            }
        });

        stopFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    recordStudio.stop();
                   // recordSave();
                if(musicStudio.isPlaying()) {
                    musicStudio.pauseMedia();
                }
                Intent intent = new Intent("completesong");
                sendBroadcast(intent);

            }
        });

        studioPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setP = SetP.RPLAY;

                if(musicStudio.isPlaying()){
                    musicStudio.pauseMedia();
                    studioPlay.setImageResource(R.drawable.play);
                }else{
                    File file = new File(path);
                    musicStudio.playMedia(file.getAbsolutePath());
                    studioPlay.setImageResource(R.drawable.pause);
                    updateSeekBar();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            duration = musicStudio.getMusicDuration();
                        }
                    }, 400);


                }

                /*musicStudio.playMedia(path);

                if(!musicStudio.isPlaying()){

                    if(recordPlay==RecordPlay.PLAY){
                        if(path!=null){
                            //musicStudio.reset();
                            musicStudio.playMedia(path);

                        }
                        recordPlay=RecordPlay.RESUME;
                    }else if(recordPlay==RecordPlay.RESUME){
                        musicStudio.resumeMedia();
                    }
                    studioPlay.setImageResource(R.drawable.pause);

                }else if(musicStudio.isPlaying()){
                    musicStudio.pauseMedia();

                }*/




            }
        });

        studioRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recordStudio.hasMicrophone()){

                    if(recordStudio.isRecording()){
                        musicStudio.pauseMedia();
                        Intent intent = new Intent("completesong");
                        sendBroadcast(intent);
                       // recordStudio.stop();
                    }else{
                        //Random random = new Random();
                        //int ran = random.nextInt(2000);
                        //path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Musical/"+title+ran+".mp3";
                        //recordStudio.record(path);

                        if(singingBundle!=null){
                            title = singingBundle.getString("singtitle");
                            artist = singingBundle.getString("singartist");
                            songresID=singingBundle.getInt("singres");

                            singingBundlePath(title,artist,songresID);
                        }

                        if(singingStudio!=null){
                            title= singingStudio.getString("singtitle");
                            artist = singingStudio.getString("singartist");
                            data = singingStudio.getString("singdata");

                            singingStudioPath(title,artist,data);
                        }


                    }


                   /* if(!recordStudio.isRecording()){
                        if(recordStudio.checkVersion()){
                            recordStudio.resume();
                        }

                    }else if(recordStudio.isRecording()){
                        if(recordStudio.checkVersion()){
                            recordStudio.pause();
                        }
                    }*/
                }
            }
        });












        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
    }


    public void singingBundlePath(String title, String artist, final int resID){
        timing = Timing.TIME;
       // setP = SetP.THEPLAY;
        period = Period.NOW;
        studioTitle.setText(title);
        studioArtist.setText(artist);
        studioBack.setImageBitmap(blur(decodealbumArtBack("singingstudio"),10));
        studioFore.setImageBitmap(decodealbumArtFore("singingstudio"));

        studiorelativeCountDown.setVisibility(View.VISIBLE);


        countDownTimer = new CountDownTimer(6000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                studioCountDown.setText(""+millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                afterFinish(resID);
            }
        };
        countDownTimer.start();




    }

    public void afterFinish(int resID){

        studiorelativeCountDown.setVisibility(View.GONE);
        musicStudio.playMusic(resID);
        musicStudio.setVolume(0.2f,0.2f);

        Random random = new Random();
        int ran = random.nextInt(2000);
        path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Musical/"+title+ran+".mp3";
        //path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+title+ran+".mp3";

        if(recordStudio.hasMicrophone()) {
            recordStudio.record(path);
        }
        Toast.makeText(this, "Recording has Started", Toast.LENGTH_SHORT).show();
        updateSeekBar();
        if(musicStudio.isPlaying()){
            studioMusic.setImageResource(R.drawable.ic_pause);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    duration = musicStudio.getMusicDuration();
                }
            }, 300);
        }
        studioPlay.setEnabled(false);

    }
    public void singingStudioPath(String title, String artist, final String data){
        timing = Timing.TIME;

        period = Period.NOW;
        studioTitle.setText(title);
        studioArtist.setText(artist);
        studioBack.setImageBitmap(blur(decodealbumArtBack(data),10));
        studioFore.setImageBitmap(decodealbumArtFore(data));

        studiorelativeCountDown.setVisibility(View.VISIBLE);


        countDownTimer = new CountDownTimer(6000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                studioCountDown.setText(""+millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                try {
                    afterFinishing(data);
                }catch (IllegalStateException e){
                    Toast.makeText(SingingStudio.this, "it finshed", Toast.LENGTH_SHORT).show();
                }
            }
        };
        countDownTimer.start();

    }

    public void afterFinishing(String data){
        studiorelativeCountDown.setVisibility(View.GONE);
        musicStudio.playMedia(data);
        musicStudio.setVolume(0.2f,0.2f);

        Random random = new Random();
        int ran = random.nextInt(2000);
        path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Musical/"+title+ran+".mp3";
       // path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+title+ran+".mp3";

        if(recordStudio.hasMicrophone()){
            recordStudio.record(path);
        }
        Toast.makeText(this, "Recording has Started", Toast.LENGTH_SHORT).show();
        updateSeekBar();
        studioMusic.setImageResource(R.drawable.ic_pause);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                duration = musicStudio.getMusicDuration();
            }
        }, 300);

        //if(musicStudio.isPlaying()){

        //}
        studioPlay.setEnabled(false);
    }



    public Bitmap decodealbumArtFore(String file){
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
                albumArt = BitmapFactory.decodeByteArray(art, 0, art.length, options);
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(getResources(), R.mipmap.singingstudio, options);
                options.inSampleSize = calculateSampleSize(options, 500, 500);
                options.inJustDecodeBounds = false;

                albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.singingstudio, options);
            }

        }catch (IllegalArgumentException e){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.mipmap.singingstudio, options);
            options.inSampleSize = calculateSampleSize(options, 500, 500);
            options.inJustDecodeBounds = false;

            albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.singingstudio, options);
        }catch (Exception e){
            decodealbumArtFore(file);
        }



        return albumArt;

    }

    public Bitmap decodealbumArtBack(String file){
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
                albumArt = BitmapFactory.decodeByteArray(art, 0, art.length, options);
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(getResources(), R.mipmap.singconcert, options);
                options.inSampleSize = calculateSampleSize(options, 500, 500);
                options.inJustDecodeBounds = false;

                albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.singconcert, options);
            }

        }catch (IllegalArgumentException e){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.mipmap.singconcert, options);
            options.inSampleSize = calculateSampleSize(options, 500, 500);
            options.inJustDecodeBounds = false;

            albumArt = BitmapFactory.decodeResource(getResources(), R.mipmap.singconcert, options);
        }catch (Exception e){
            decodealbumArtBack(file);
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

    public Bitmap blur(Bitmap inBit,float radius){
        if(inBit==null){return inBit;}
        Bitmap outBit = Bitmap.createBitmap(inBit);

        RenderScript renderScript =RenderScript.create(this);
        android.renderscript.Allocation allocateIn = android.renderscript.Allocation.createFromBitmap(renderScript,inBit);
        android.renderscript.Allocation allocateOut = android.renderscript.Allocation.createFromBitmap(renderScript,outBit);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            android.renderscript.ScriptIntrinsicBlur intrinsicBlur = android.renderscript.ScriptIntrinsicBlur.create(renderScript, android.renderscript.Element.U8_4(renderScript));
            intrinsicBlur.setInput(allocateIn);
            intrinsicBlur.setRadius(radius);
            intrinsicBlur.forEach(allocateOut);
            allocateOut.copyTo(outBit);

        }



        return outBit;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            try {
                super.onBackPressed();
            }catch (IllegalStateException e){
                onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.singing_studio, menu);
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

    public void updateSeekBar(){


        handler.postDelayed(runnable, 100);
    }

    private int count=0;
    private int duration;
    private Runnable runnable =new Runnable() {
        @Override
        public void run() {
            // int duration=musicService.getMusicDuration();

            //if(count==0) {
            if(period==Period.NOW) {
                duration = musicStudio.getMusicDuration();
                period=Period.LATER;
            }
            //}
            int currentTime = musicStudio.getCurrentMusicTime();

            int secf = currentTime/1000;
            int secr = duration/1000;

            studioCount.setText(convertmillsecondstoTimer(currentTime));
            studioFinish.setText(convertmillsecondstoTimer(duration));
            studioSeek.setMax(secr);
            studioSeek.setProgress(secf);
            if(currentTime==duration&&setP==SetP.THEPLAY&&timing==Timing.TIME){
                Intent intent = new Intent("completesong");
                sendBroadcast(intent);
                timing=Timing.NOT;
            }

            handler.postDelayed(runnable,100);
            count++;
        }
    };

    final BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            studioPlay.setEnabled(true);
            studioextra.setEnabled(true);
            recordStudio.stop();
            recordSave();
        }
    };

    public void registerOnComplete(){
        IntentFilter intentFilter = new IntentFilter("completesong");
        registerReceiver(onComplete,intentFilter);
    }

    public void recordSave(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recording Completed");
        builder.setMessage("Recording has Completed, and it has been Saved at the location:\n\n"+path);
        /*builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });*/
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        builder.create().show();
    }

    public void saveTitle(){
        final EditText editText= new EditText(this);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Title");
        builder.setView(editText);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savetitle = String.valueOf(editText.getText());

                path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Musical/"+savetitle+".3gp";
               recordStudio.setOutput(path);
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


    public void saveDialog(){

        //if(state== LyricsStudio.State.UPDATE){
            //editText.setText(title);
        //}

        editText = new EditText(this);

        if(saving== Saving.UPDATE){
            editText.setText(titling);

        }




        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Lyrics Title");
        builder.setView(editText);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lyricss = String.valueOf(studioLyricSpace.getText());
                titling = String.valueOf(editText.getText());

                if(saving==Saving.SAVE){
                    id = lyricsDatabase.insertLyrics(titling,lyricss);
                    saving = Saving.UPDATE;
                    editText.setText(titling);
                    Toast.makeText(SingingStudio.this, "Lyrics has been Saved", Toast.LENGTH_LONG).show();

                }else if(saving== Saving.UPDATE){
                    lyricsDatabase.updateLyrics(id, titling, lyricss);
                    editText.setText(titling);
                    Toast.makeText(SingingStudio.this, "Lyrics has been Updated", Toast.LENGTH_LONG).show();

                }
            }
        });
        builder.create().show();

    }

    public void volumeSetUp(){
        final SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(10);
        seekBar.setProgress(2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicStudio.setVolume((float)progress/10,(float)progress/10);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Volume");
        builder.setView(seekBar);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               seekBar.setProgress(2);
               dialog.cancel();
            }
        });
        builder.create().show();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        musicStudio.pauseMedia();
        recordStudio.reset();
        unregisterReceiver(headset);
        unregisterReceiver(onComplete);
    }

    final BroadcastReceiver headset = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                //musicStudio.reset();
                //recordStudio.reset();
            Toast.makeText(SingingStudio.this, "Sorry, the use of HeadSet is not Allowed, because it produces Poor Recording result.\nPlease remove the HeadSet to Improve the Outcome of the Recording... ", Toast.LENGTH_LONG).show();
        }
    };

    public void registerHeadSet(){
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(headset,intentFilter);
    }

    public void headDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notice!!!");
        builder.setIcon(R.drawable.cancelhead);
        builder.setMessage("Sorry for the sudden Notice.\n\nYou may be wondering what this Notice might be for.\nThe reason is that" +
                "this app does not allow the use of HeadSet during Recording because the final result of the Recording is not Pleasing " +
                "and Efficient, and I bet you it is not something you want.\n\nSo to continue your Recording you have to remove the HeadSet and " +
                "click the 'Continue Button' here. ");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                /*if(singingBundle!=null){
                    title = singingBundle.getString("singtitle");
                    artist = singingBundle.getString("singartist");
                    songresID=singingBundle.getInt("singres");

                    singingBundlePath(title,artist,songresID);
                }

                if(singingStudio!=null){
                    title= singingStudio.getString("singtitle");
                    artist = singingStudio.getString("singartist");
                    data = singingStudio.getString("singdata");

                    singingStudioPath(title,artist,data);
                }*/
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

    final BroadcastReceiver recordnotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBackPressed();
        }
    };

    public void registerNotice(){
        IntentFilter intentFilter = new IntentFilter("recordnotice");
        registerReceiver(recordnotice,intentFilter);
    }

    final BroadcastReceiver recordstop = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            musicStudio.pauseMedia();
        }
    };

    public void registerStop(){
        IntentFilter intentFilter = new IntentFilter("recordstop");
        registerReceiver(recordstop,intentFilter);
    }


    public void onStop(){
        super.onStop();
        countDownTimer.cancel();
        if(sharedPref.getInt()!=10){
            downTimer.cancel();
        }
    }

}
