package com.mactrix.www.musical;

import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
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
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

public class SingList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RelativeLayout singbelow;
    private AppLovinAd appLovnAd;
    private SharedPref sharedPref;
    private CountDownTimer downTimer;
    MoPubInterstitial interstitial;


    InterstitialAd singAd;
    AdRequest singReq;

    AdView adView;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //AppLovinSdk.initializeSdk(this);

        sharedPref = new SharedPref(this,getResources().getString(R.string.adsConst));
        if(sharedPref.getInt()!=10) {

            singAd = new InterstitialAd(this);
            singReq = new AdRequest.Builder().build();
            singAd.setAdUnitId("ca-app-pub-2742716340205774/8179892449");

            adView = (AdView)findViewById(R.id.adView);
            adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);


            downTimer = new CountDownTimer(25000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished == 10000) {
                        Toast.makeText(SingList.this, "Sorry, Ads will be shown in less than 10 secs", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFinish() {


                    singAd.loadAd(singReq);
                    singAd.setAdListener(new AdListener(){
                        public void onAdLoaded(){
                            if(singAd.isLoaded()){
                                singAd.show();
                            }
                        }
                    });

                    /*interstitial = new MoPubInterstitial(SingList.this,"7cada9c6b4f0448690c29a58b5317bfe");
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

                       /* }

                        @Override
                        public void onInterstitialClicked(MoPubInterstitial interstitial) {

                        }

                        @Override
                        public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                            // startActivity(new Intent(Musical.this,LovedThings.class));

                        }
                    });*/





                   /* AppLovinSdk.initializeSdk(SingList.this);
                    AppLovinSdk.getInstance(SingList.this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
                        @Override
                        public void adReceived(AppLovinAd appLovinAd) {
                            appLovnAd = appLovinAd;

                        }

                        @Override
                        public void failedToReceiveAd(int i) {

                        }
                    });
                    AppLovinInterstitialAdDialog adDialog = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(SingList.this), SingList.this);
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

            /*AppLovinAdView appLovinAdView = new AppLovinAdView(AppLovinAdSize.BANNER, this);
            singbelow = (RelativeLayout) findViewById(R.id.singbelow);
            singbelow.addView(appLovinAdView);
            appLovinAdView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, AppLovinSdkUtils.dpToPx(this, AppLovinAdSize.BANNER.getHeight())));
            appLovinAdView.loadNextAd();*/
        }





        TabLayout tabLayout = (TabLayout)findViewById(R.id.singtab);
        final ViewPager viewPager = (ViewPager)findViewById(R.id.singpager);

        //tabLayout.addTab(tabLayout.newTab().setText("Instrumental"));
        tabLayout.addTab(tabLayout.newTab().setText("My Own Songs"));

        SingListPager singListPager = new SingListPager(getSupportFragmentManager(),tabLayout.getTabCount());

        viewPager.setAdapter(singListPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Intent intent = new Intent("onStop");
                sendBroadcast(intent);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                Intent intent = new Intent("onStop");
                sendBroadcast(intent);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                Intent intent = new Intent("onStop");
                sendBroadcast(intent);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        getMenuInflater().inflate(R.menu.sing_list, menu);
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
            onBackPressed();
          //  startActivity(new Intent(this,SingOwn.class));

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
            startActivity(new Intent(this,Musical.class));
        } else if (id == R.id.nav_gallery) {
            /*musicService.pauseMedia();
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(101);
            startActivity(new Intent(Musical.this,IntroKara.class));*/

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

            Intent send = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mactrix.www.musical"));
            startActivity(send);

        }else  if(id == R.id.loved){
            startActivity(new Intent(this,LovedThings.class));
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

    public void onStop(){
        super.onStop();
        if(sharedPref.getInt()!=10){
            downTimer.cancel();
        }
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
