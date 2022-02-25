package com.mactrix.www.musical;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MyMusicService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener
        ,MediaPlayer.OnErrorListener,MediaPlayer.OnSeekCompleteListener,MediaPlayer.OnInfoListener,MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener{



    private final IBinder iBinder=new LocalBinder();
    MediaPlayer mediaPlayer;
    String mediaFile;
    int resumePosition;
    AudioManager audioManager;
    ArrayList<String> songs;
    int currentplace;
    int currentIndex;
    boolean shuffler;
    boolean repeatone;
    boolean repeatall;
    ArrayList<String> titles;
    ArrayList<String> albums;
    ArrayList<String> artists;
    ArrayList<String> albumArts;
    public static final String ACTION_PLAY="com.mactrix.ACTION_PLAY";
    public static final String ACTION_PAUSE="com.mactrix.ACTION_PAUSE";
    public static final String ACTION_NEXT="com.mactrix.ACTION_NEXT";
    public static final String ACTION_PREVIOUS="com.mactrix.ACTION_PREVIOUS";
    public static final String ACTION_STOP="com.mactrix.ACTION_STOP";

    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSessionCompat;
    private MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID=101;

    public enum PlaybackStatus{PLAYING, PAUSEED}
    PlaybackStatus playbackStatus;




    public MyMusicService() {

    }

    public int onStartCommand(Intent intent,int flags,int startid){
        //Bundle bundle;
       // bundle = intent.getBundleExtra(getResources().getString(R.string.songbundle));
        initService();
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                initMediaSession();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch (Exception e){

        }

        registerServiceSetUp();
        registerNotifyPlayer();
        registerRepeatOne();
        registerShuffle();
        registerSeekTo();

        /*if(bundle!=null) {
            //mediaFile = bundle.getString(getResources().getString(R.string.songdata));
            songs = bundle.getStringArrayList(getResources().getString(R.string.songarray));
            //currentplace = bundle.getInt(getResources().getString(R.string.songindex));
            //currentIndex=currentplace;
            titles= bundle.getStringArrayList(getResources().getString(R.string.titlearray));
            albums = bundle.getStringArrayList(getResources().getString(R.string.albumarray));
            artists = bundle.getStringArrayList(getResources().getString(R.string.artistarray));
            albumArts=bundle.getStringArrayList(getResources().getString(R.string.albumartarray));

        }else{
            stopSelf();
        }*/
        //Request audio Focus
        if(requestAudioFocus()==false){
            stopSelf();
        }



        if(mediaFile!=null&&mediaFile!=""){

            //playMedia();



            Intent startIntent = new Intent("start");
            Bundle bundling = new Bundle();
            bundling.putString("starttitle",getTitles());
            bundling.putString("startartist",getArtist());
            bundling.putString("startalbum",getAlbums());
            bundling.putBoolean("startboolean",true);
            bundling.putString("startalbumart",getAlbumArt());
            startIntent.putExtra("startbundle",bundling);
            sendBroadcast(startIntent);

        }

       // handlerIncomingActions(intent);


        //return super.onStartCommand(intent,flags,startid);
        return START_STICKY;
    }

    public ArrayList<String> getTitleArray(){return titles; }
    public ArrayList<String> getArtistArray(){return artists; }
    public ArrayList<String> getAlbumArray(){return albums; }
    public ArrayList<String> getSongsArray(){return songs; }
    public String getTitles(){
        return titles.get(currentIndex);
    }
    public String getArtist(){
        return artists.get(currentIndex);
    }
    public String getAlbums(){
        return albums.get(currentIndex);
    }
    public String getAlbumArt(){
        return albumArts.get(currentIndex);
    }

    public boolean isPlaying(){
        boolean isPlaying=false;
        try {
            isPlaying = mediaPlayer.isPlaying();
        }catch (IllegalStateException e){
            mediaPlayer = new MediaPlayer();
            isPlaying = mediaPlayer.isPlaying();
        }catch (Exception e){
            mediaPlayer = new MediaPlayer();
        }


        return isPlaying;
    }

    public int getCurrentIndex(){
        return currentIndex;
    }

    public String getCurrentTitle(){
        return titles.get(currentIndex);
    }

    public int getCurrentMusicTime(){
        int c=0;


        try {
            c = mediaPlayer.getCurrentPosition();

        }catch (IllegalStateException e){
            mediaPlayer = new MediaPlayer();
           // mediaPlayer.reset();
            //mediaPlayer.release();
           // Toast.makeText(this, "Error! trying to access Media in an Illegal state", Toast.LENGTH_SHORT).show();
        }
        return c;
    }
    public int getMusicDuration(){
        int d = 1;

        if(mediaPlayer!=null) {
            if (mediaPlayer.isPlaying()) {
                d = mediaPlayer.getDuration();
            }
        }
        return d;
    }
    public void reset(){
        mediaPlayer.reset();
    }

    public void playMedia(){


            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.reset();

                try {
                    mediaPlayer.setDataSource(mediaFile);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    Toast.makeText(this, "Invalid Song", Toast.LENGTH_SHORT).show();
                    skipToNext();
                }


                //mediaPlayer.start();
            }else{
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(mediaFile);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    Toast.makeText(this, "Invalid Song", Toast.LENGTH_SHORT).show();
                    skipToNext();
                }


            }



    }

    public void playMedia(int index){
        if(songs!=null) {
            currentIndex = index;
            mediaFile = songs.get(index);
            mediaPlayer.reset();
            playMedia();
            Intent intent = new Intent("start");
            Bundle bundling = new Bundle();
            bundling.putInt("startIndex", currentIndex);
            intent.putExtra("startbundle", bundling);
            sendBroadcast(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                buildNotification(PlaybackStatus.PLAYING);
                updateMetaData();
            }

        }else {
            Toast.makeText(this, "Selete a Song from the List", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopMedia(){

        if(mediaPlayer==null) return;
        try {


            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();

            }
        }catch (IllegalStateException e){
            mediaPlayer = new MediaPlayer();
           // mediaPlayer.stop();
        }
    }
    public void pauseMedia(){
        if(songs!=null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                resumePosition = mediaPlayer.getCurrentPosition();
            }

            Intent intent = new Intent("pause");
            sendBroadcast(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                buildNotification(PlaybackStatus.PAUSEED);
            }
        }
    }

    public void resumeMedia(){
        if(songs!=null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(resumePosition);
                mediaPlayer.start();
            }
            Intent intent = new Intent("resume");
            sendBroadcast(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                buildNotification(PlaybackStatus.PLAYING);
            }
        }

    }

    public void skipToNext(){

        if(songs!=null) {
            if (shuffler) {
                Random random = new Random();
                currentIndex = random.nextInt(songs.size() - 1);
                mediaFile = songs.get(currentIndex);
                playMedia();
            } else {

                if (currentIndex == songs.size() - 1) {
                    currentIndex = 0;
                    mediaFile = songs.get(currentIndex);
                    mediaPlayer.reset();
                    playMedia();
                } else {
                    currentIndex++;
                    mediaFile = songs.get(currentIndex);
                    mediaPlayer.reset();
                    playMedia();
                }
            }
            Intent startIntent = new Intent("start");
            Bundle bundling = new Bundle();
       /* bundling.putString("starttitle",getTitles());
        bundling.putString("startartist",getArtist());
        bundling.putString("startalbum",getAlbums());
        bundling.putString("startalbumart",getAlbumArt());*/
            bundling.putInt("startIndex", currentIndex);
            startIntent.putExtra("startbundle", bundling);
            sendBroadcast(startIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

        }else{
            Toast.makeText(this, "Selete a song from the List", Toast.LENGTH_SHORT).show();
        }
    }

    public void skipToprevious(){
        if(songs!=null) {
            if (shuffler) {
                Random random = new Random();
                currentIndex = random.nextInt(songs.size() - 1);
                mediaFile = songs.get(currentIndex);
                playMedia();
            } else {


                if (currentIndex == 0) {
                    currentIndex = songs.size() - 1;
                    mediaFile = songs.get(currentIndex);
                    mediaPlayer.reset();
                    playMedia();
                } else {
                    currentIndex--;
                    mediaFile = songs.get(currentIndex);
                    mediaPlayer.reset();
                    playMedia();
                }
            }

            Intent startIntent = new Intent("start");
            Bundle bundling = new Bundle();
        /* bundling.putString("starttitle",getTitles());
        bundling.putString("startartist",getArtist());
        bundling.putString("startalbum",getAlbums());
        bundling.putString("startalbumart",getAlbumArt());*/
            bundling.putInt("startIndex", currentIndex);
            startIntent.putExtra("startbundle", bundling);
            sendBroadcast(startIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

        }else{
            Toast.makeText(this, "Selete a song from the List", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean getShuffleState(){
        return shuffler;
    }
    public boolean getRepeatOne(){
        return repeatone;
    }

    public void onShuffle(){
        shuffler=true;
    }
    public void offShuffle(){
        shuffler=false;
    }

    public void onRepeatAll(){
        repeatall=true;
    }
    public void offRepeatAll(){
        repeatall=false;
    }

    public void onRepeatOne(){
        repeatone=true;
    }
    public void offRepeatOne(){
        repeatone=false;
    }

    public void seekTo(int seekedTime){
        mediaPlayer.pause();
        mediaPlayer.seekTo(seekedTime);
        mediaPlayer.start();
    }






    public void initService(){
        mediaPlayer = new MediaPlayer();
        //Set Up the Listeners

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);

        mediaPlayer.reset();


        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //stopSelf();


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       // throw new UnsupportedOperationException("Not yet implemented");

        return iBinder;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_GAIN: if(mediaPlayer==null){
                initService();
            }else if(!mediaPlayer.isPlaying()){

                    //mediaPlayer.seekTo(resumePosition);
                    //mediaPlayer.start();

                //mediaPlayer.start();
                //mediaPlayer.setVolume(1.0f,1.0f);
            }
                //Intent intent = new Intent("resume");
                //sendBroadcast(intent);

            break;
            case AudioManager.AUDIOFOCUS_LOSS:if(mediaPlayer.isPlaying())
            {
                mediaPlayer.pause();
                resumePosition = mediaPlayer.getCurrentPosition();
                //pauseMedia();
                //mediaPlayer.stop();
                //mediaPlayer.release();
               // mediaPlayer=null;
            }
                Intent intentr = new Intent("pause");
                sendBroadcast(intentr);
            break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if(mediaPlayer.isPlaying()){
                    //mediaPlayer.pause();
                   // pauseMedia();
                    mediaPlayer.pause();
                    resumePosition = mediaPlayer.getCurrentPosition();
                }
                Intent intentw = new Intent("pause");
                sendBroadcast(intentw);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if(mediaPlayer.isPlaying()){
                   // mediaPlayer.setVolume(0.1f,0.1f);
                }
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                if(mediaPlayer.isPlaying()){
                    //mediaPlayer.setVolume(3.0f,3.0f);
                }

                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                if(!mediaPlayer.isPlaying()){

                        mediaPlayer.seekTo(resumePosition);
                        mediaPlayer.start();

                }
                Intent intenta = new Intent("resume");
                sendBroadcast(intenta);

                break;
        }


    }

    public boolean requestAudioFocus(){
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        if(result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            return true;
        }
        return false;
    }
    public boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED==audioManager.abandonAudioFocus(this);
    }



    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

       /* if(!shuffler&&!repeatall){
            if(currentIndex==songs.size()-1){
                stopMedia();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        }*/

        if(repeatone){
            mediaFile = songs.get(currentIndex);
            playMedia();
        }else if(shuffler){
            Random random =new Random();
            currentIndex = random.nextInt(songs.size()-1);
            mediaFile = songs.get(currentIndex);
            playMedia();
        }else if(repeatall){
            skipToNext();
        }else{
            skipToNext();
        }



        Intent intent= new Intent(getResources().getString(R.string.songcomplete));
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.songcompletebundleintent),currentIndex);
        intent.putExtra(getResources().getString(R.string.songcompletebundleintent),bundle);
        sendBroadcast(intent);

        Intent startIntent = new Intent("start");
        Bundle bundling = new Bundle();
        /* bundling.putString("starttitle",getTitles());
        bundling.putString("startartist",getArtist());
        bundling.putString("startalbum",getAlbums());
        bundling.putString("startalbumart",getAlbumArt());*/
        bundling.putInt("startIndex",currentIndex);
        startIntent.putExtra("startbundle",bundling);
        sendBroadcast(startIntent);



       // stopMedia();
        //Stop the Service
        //stopSelf();

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //playMedia();
        //mediaPlayer.reset();

        mediaPlayer.start();

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    public class LocalBinder extends Binder{
        public MyMusicService getService(){
            return MyMusicService.this;
        }
    }

    public void onDestroy(){
        super.onDestroy();

        try {

            unregisterReceiver(serviceSetUp);
            unregisterReceiver(notifyPlayer);
            unregisterReceiver(seekTo);
            unregisterReceiver(shuffle);
            unregisterReceiver(repeatOne);

        }catch (IllegalArgumentException e){
            Toast.makeText(this, "On Destroy", Toast.LENGTH_SHORT).show();
        }

        if(mediaPlayer!=null){
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            removeNotifiation();
        }
    }
    private final BroadcastReceiver play = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    private void registerPlayBroadcast(){

    }

    private final BroadcastReceiver next = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    private void registerNext(){

    }

    private final BroadcastReceiver previous = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    private void registerPrevious(){

    }

    private final BroadcastReceiver pause = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    private void registerPause(){

    }

    private final BroadcastReceiver resume = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    private void registerResume(){

    }

    private final BroadcastReceiver shuffle = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                resumePosition = mediaPlayer.getCurrentPosition();
            }

            Intent intenta = new Intent("pause");
            sendBroadcast(intenta);
        }
    };
    private void registerShuffle(){
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_HDMI_AUDIO_PLUG);
        registerReceiver(shuffle,intentFilter);
    }

    private final BroadcastReceiver repeatOne = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                resumePosition = mediaPlayer.getCurrentPosition();
            }

            Intent intenta = new Intent("pause");
            sendBroadcast(intenta);
               // pauseMedia();
        }
    };
    private void registerRepeatOne(){
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(repeatOne,intentFilter);
    }

    private final BroadcastReceiver serviceSetUp = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle;
            bundle = intent.getBundleExtra(getResources().getString(R.string.songbundle));

            if(bundle!=null) {
                //mediaFile = bundle.getString(getResources().getString(R.string.songdata));
                songs = bundle.getStringArrayList(getResources().getString(R.string.songarray));
                //currentplace = bundle.getInt(getResources().getString(R.string.songindex));
                //currentIndex=currentplace;
                titles= bundle.getStringArrayList(getResources().getString(R.string.titlearray));
                albums = bundle.getStringArrayList(getResources().getString(R.string.albumarray));
                artists = bundle.getStringArrayList(getResources().getString(R.string.artistarray));
                albumArts=bundle.getStringArrayList(getResources().getString(R.string.albumartarray));
                currentIndex=0;

            }else{
                stopSelf();
            }


        }
    };
    private void registerServiceSetUp(){

        IntentFilter filter = new IntentFilter(getResources().getString(R.string.servicesetup));
        registerReceiver(serviceSetUp,filter);

    }

    private final BroadcastReceiver seekTo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                resumePosition = mediaPlayer.getCurrentPosition();
            }

            Intent intenta = new Intent("pause");
            sendBroadcast(intenta);
        }
    };
    private void registerSeekTo(){
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(seekTo,intentFilter);

    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initMediaSession() throws RemoteException{
        if(mediaSessionManager!=null){return;}
        //mediaSessionManager exists

        // Create MediaSessionManager
        mediaSessionManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
        //Create MediaSession
        mediaSessionCompat = new MediaSessionCompat(getApplicationContext(),"Musical");

        //Get MediaSession transport controls
        transportControls = mediaSessionCompat.getController().getTransportControls();

        //set MediaSession to be ready to receiver Media Command
        mediaSessionCompat.setActive(true);

        //Indicate that the MediaSession handles transport control commands throught its MediaSessionCompat Callback
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set MediaSession's MetaData
       // updateMetaData();

        //Attach Callback to receiv MediaSession updates
        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
               // updateMetaData();
                resumeMedia();
            }
            @Override
            public void onPause(){
                super.onPause();
                pauseMedia();
                //updateMetaData();
            }
            @Override
            public void onSkipToNext(){
                super.onSkipToNext();
                skipToNext();
                updateMetaData();
            }
            @Override
            public void onSkipToPrevious(){
                super.onSkipToPrevious();
                skipToprevious();
                updateMetaData();
            }
            @Override
            public void onStop(){
                super.onStop();
                stopMedia();
            }
            @Override
            public void onSeekTo(long position){
                super.onSeekTo(position);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        }

       /* if(file!=null) {
            albumArt  = BitmapFactory.decodeFile(file);
        }else{
            albumArt = BitmapFactory.decodeResource(getResources(),R.mipmap.cele6);
        }*/
        return albumArt;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateMetaData(){
        Bitmap albumArt = decodeAlbumArt(songs.get(currentIndex));

        mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,titles.get(currentIndex))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,artists.get(currentIndex))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM,albums.get(currentIndex))
                .build());
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void buildNotification(PlaybackStatus playbackStatus){
        int notificationAction = R.drawable.paudingin;
        PendingIntent play_pauseAction = null;

        if(playbackStatus==PlaybackStatus.PLAYING){
            notificationAction=R.drawable.paudingin;
            play_pauseAction=playbackAction(1);
        }else if(playbackStatus==PlaybackStatus.PAUSEED){
            notificationAction=R.drawable.playingo;
            play_pauseAction=playbackAction(0);
        }

        Bitmap largeIcon = decodeAlbumArt(songs.get(currentIndex));

        Intent playingIntent = new Intent(this,MusicPlace.class);
        Bundle bundle = new Bundle();

        bundle.putStringArrayList(getResources().getString(R.string.songarray), songs);
        bundle.putInt(getResources().getString(R.string.songindex), currentIndex);
        bundle.putStringArrayList(getResources().getString(R.string.titlearray), titles);
        bundle.putStringArrayList(getResources().getString(R.string.artistarray), artists);
        bundle.putStringArrayList(getResources().getString(R.string.albumarray), albums);

        playingIntent.putExtra(getResources().getString(R.string.songbundle), bundle);

       PendingIntent pending= PendingIntent.getActivity(this,10,playingIntent,PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setShowWhen(false)
        .setStyle(new NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken()).setShowActionsInCompactView(0,1,2))
        .setColor(getResources().getColor(R.color.blurtrans))
        .setLargeIcon(largeIcon)
        .setSmallIcon(R.drawable.musicalclearicon)
        .setContentIntent(pending)
        .setContentTitle(titles.get(currentIndex))
        .setContentText(artists.get(currentIndex))
        .setContentInfo(albums.get(currentIndex))
        .addAction(R.drawable.previousing,"Previous",playbackAction(3))
        .addAction(notificationAction,"Pause",play_pauseAction)
        .addAction(R.drawable.nextingy,"Next",playbackAction(2));



        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());





    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void removeNotifiation(){
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private PendingIntent playbackAction(int actionNumber){
       // Intent playNotify = new Intent("notifyPlayer");

        Intent playbackAction = new Intent("notifyPlayer");

        switch (actionNumber){
            case 0: //playbackAction.setAction(ACTION_PLAY);
            playbackAction.putExtra("notice",actionNumber);
            return PendingIntent.getBroadcast(this,actionNumber,playbackAction,0);
            case 1: //playbackAction.setAction(ACTION_PAUSE);
            playbackAction.putExtra("notice",actionNumber);
            return PendingIntent.getBroadcast(this,actionNumber,playbackAction,0);
            case 2: //playbackAction.setAction(ACTION_NEXT);
            playbackAction.putExtra("notice",actionNumber);
            return PendingIntent.getBroadcast(this,actionNumber,playbackAction,0);
            case 3: //playbackAction.setAction(ACTION_PREVIOUS);
            playbackAction.putExtra("notice",actionNumber);
            return PendingIntent.getBroadcast(this,actionNumber,playbackAction,0);
            default:break;

        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handlerIncomingActions(Intent playbackAction){
        if(playbackAction==null||playbackAction.getAction()==null){return;}

        String actionString = playbackAction.getAction();

        if(actionString.equalsIgnoreCase(ACTION_PLAY)){
            transportControls.play();
        }else if(actionString.equalsIgnoreCase(ACTION_PAUSE)){
            transportControls.pause();
        }else if(actionString.equalsIgnoreCase(ACTION_NEXT)){
            transportControls.skipToNext();
        }else if(actionString.equalsIgnoreCase(ACTION_PREVIOUS)){
            transportControls.skipToPrevious();
        }else if(actionString.equalsIgnoreCase(ACTION_STOP)){
            transportControls.stop();
        }
    }

    private final BroadcastReceiver notifyPlayer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent notifyIntent = new Intent();
            int actionNumber = intent.getIntExtra("notice",-1);

            switch (actionNumber){
                case 0:notifyIntent.setAction(ACTION_PLAY);
                    break;
                case 1:notifyIntent.setAction(ACTION_PAUSE);
                    break;
                case 2:notifyIntent.setAction(ACTION_NEXT);
                    break;
                case 3:notifyIntent.setAction(ACTION_PREVIOUS);
                    break;
                default:notifyIntent.setAction(ACTION_PAUSE);
                    break;
            }



            handlerIncomingActions(notifyIntent);

        }
    };

    private void registerNotifyPlayer(){
        IntentFilter intentFilter = new IntentFilter("notifyPlayer");
        registerReceiver(notifyPlayer,intentFilter);
    }




}
