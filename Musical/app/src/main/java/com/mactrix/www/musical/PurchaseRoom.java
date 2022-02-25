package com.mactrix.www.musical;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.startapp.android.publish.ads.splash.SplashConfig;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.List;

public class PurchaseRoom extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SharedPref singConst ;
    SharedPref adsConst;
    SharedPref sharedPref;

    int sharedValue;
    String singSkuID;
    String adsSkuID;
    PurchasesUpdatedListener purchasesUpdatedListener;
    BillingClient client;
    private BillingFlowParams.Builder buildering;
    AdView adView;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adsConst = new SharedPref(this,getResources().getString(R.string.adsConst));

        if (adsConst.getInt()!=10) {




            StartAppSDK.init(this, "202870072", true);
            StartAppAd.showSplash(this,savedInstanceState,new SplashConfig().setTheme(SplashConfig.Theme.DEEP_BLUE)
                    .setAppName("Musical Music Player and Singing Studio")
                    .setLogo(R.mipmap.musicaliconing));
        }

        setContentView(R.layout.activity_purchase_room);

       ImageView roomback = (ImageView)findViewById(R.id.roomback);
       roomback.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onBackPressed();
           }
       });
        singSkuID="buysing";
        adsSkuID="removeads";
        sharedValue=10;

        singConst = new SharedPref(this,getResources().getString(R.string.singConst));

        sharedPref = new SharedPref(this,"sharePref");


        if (adsConst.getInt()!=10) {


            adView = (AdView)findViewById(R.id.adView);

            adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);


        }


        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
                if(responseCode== BillingClient.BillingResponse.OK&&purchases!=null){

                    for(Purchase purchase: purchases){
                        if(purchase.getSku().equals(singSkuID)){

                           singConst.setInt(sharedValue);
                            Toast.makeText(PurchaseRoom.this, "Thank you for Buying our product", Toast.LENGTH_LONG).show();
                            //comsume();

                        }else if(purchase.getSku().equals(adsSkuID)){

                           adsConst.setInt(sharedValue);
                            //token=purchase.getPurchaseToken();
                            Toast.makeText(PurchaseRoom.this, "Thank you for the Buying our Product", Toast.LENGTH_LONG).show();
                            //comsume();
                        }
                    }
                }else if(responseCode== BillingClient.BillingResponse.USER_CANCELED){
                    Toast.makeText(PurchaseRoom.this, "Are you sure you want to Cancel the Purchase of this wonderful item", Toast.LENGTH_SHORT).show();

                }else if(responseCode== BillingClient.BillingResponse.ITEM_ALREADY_OWNED){
                    for (Purchase purchasess : purchases) {
                        if (purchasess.getSku().equals(singSkuID)) {
                            singConst.setInt(sharedValue);
                            Toast.makeText(getBaseContext(), "Item is already Bought", Toast.LENGTH_SHORT).show();
                        }else if(purchasess.getSku().equals(adsSkuID)){
                            adsConst.setInt(sharedValue);
                            Toast.makeText(getBaseContext(), "Item is Already Bought by you", Toast.LENGTH_LONG).show();
                        }
                    }
                }else if(responseCode== BillingClient.BillingResponse.ERROR){
                    Toast.makeText(getBaseContext(),"There was an Error in the process",Toast.LENGTH_LONG).show();
                    //blurbutton.setEnabled(true);
                }else if(responseCode== BillingClient.BillingResponse.BILLING_UNAVAILABLE){
                    Toast.makeText(getBaseContext(),"Billing is Unavailable",Toast.LENGTH_LONG).show();
                    //blurbutton.setEnabled(true);
                }else if(responseCode== BillingClient.BillingResponse.DEVELOPER_ERROR){
                    Toast.makeText(getBaseContext(),"Sorry for the Error",Toast.LENGTH_LONG).show();
                    //blurbutton.setEnabled(true);
                }


            }
        };


        client = BillingClient.newBuilder(this).setListener(purchasesUpdatedListener).build();


        client.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int responseCode) {

            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });




        /*List<String> skuList = new ArrayList<>();
        skuList.add("android.test.purchased");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        client.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {

            }
        });*/
        client.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, new PurchaseHistoryResponseListener() {
            @Override
            public void onPurchaseHistoryResponse(int responseCode, List<Purchase> purchasesList) {
                if (purchasesList!=null) {
                    if (responseCode == BillingClient.BillingResponse.OK) {
                        for (Purchase purchasess : purchasesList) {
                            if (purchasess.getSku().equals(singSkuID)) {
                                singConst.setInt(sharedValue);
                                Toast.makeText(getBaseContext(), "Item is already Bought", Toast.LENGTH_SHORT).show();
                            }else if(purchasess.getSku().equals(adsSkuID)){
                                adsConst.setInt(sharedValue);
                                Toast.makeText(getBaseContext(), "Item is Already Bought by you", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else if (responseCode== BillingClient.BillingResponse.SERVICE_UNAVAILABLE) {
                        Toast.makeText(getBaseContext(), "Please Turn Connectivity ON, so that I can query your Purchase History", Toast.LENGTH_SHORT).show();
                    }else if(responseCode== BillingClient.BillingResponse.ITEM_ALREADY_OWNED){
                        for (Purchase purchasess : purchasesList) {
                            if (purchasess.getSku().equals(singSkuID)) {
                                singConst.setInt(sharedValue);
                                Toast.makeText(getBaseContext(), "Item is Already Bought by you", Toast.LENGTH_LONG).show();
                            }else if(purchasess.getSku().equals(adsSkuID)){
                                adsConst.setInt(sharedValue);
                                Toast.makeText(getBaseContext(), "Item is Already Bought by you", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });



        Button buysing = (Button)findViewById(R.id.buysing);
        buysing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singdialog();
            }
        });

        Button removeads = (Button)findViewById(R.id.removeads);
        removeads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildering = BillingFlowParams.newBuilder().setSku(adsSkuID).setType(BillingClient.SkuType.INAPP);
                clientcall();
            }
        });

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

    public void singdialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Singing Studio Unlocking");
        builder.setMessage("Hello, We see that you want to Unlock the Singing Studio feature of Ours.\n" +
                "In order to do so, We have provided you with two Options.. Below are the options;\n\n" +
                "1. You have to Purchase the feature by Clicking the 'Purchase Button'\n" +
                "2. You have to Share this App to 15 friends of yours. They have to download the App.\n\n" +
                "Note that the second Options is Time Limited. We have give just 20 days for this Options to be Open" +
                " So you have to Start now with the Sharing.\n\nThank you for Everything");
        builder.setPositiveButton("Purchase", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                buildering = BillingFlowParams.newBuilder().setSku(singSkuID).setType(BillingClient.SkuType.INAPP);
                clientcall();


            }
        });
        builder.setNegativeButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int count=0;
                if(sharedPref.getInt()==-1){
                    sharedPref.setInt(count);

                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.mactrix.www.musical");
                startActivity(Intent.createChooser(intent,"Complete Task with any of the Following..."));

                count = sharedPref.getInt();
                count++;
                sharedPref.setInt(count);


                if(sharedPref.getInt()==5){
                    Toast.makeText(getBaseContext(), "Wow, you have done well so far, Thanking for Sharing our App", Toast.LENGTH_LONG).show();
                }else if(sharedPref.getInt()==10){
                    Toast.makeText(getBaseContext(), "You are almost there to Unlock the Blur Effect, Just a Little more to go. Thanks once more.", Toast.LENGTH_LONG).show();

                }else if(sharedPref.getInt()==15){
                    Toast.makeText(getBaseContext(), "Congratulation, you have Unlocked the Blur Effect. Restart this page to use the Effect. Thank you for Sharing. We appreciate...", Toast.LENGTH_LONG).show();
                    singConst.setInt(sharedValue);
                }



            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             dialog.cancel();
            }
        });


        builder.create().show();
    }
    public void clientcall(){
        int responceCode=client.launchBillingFlow(this,buildering.build());
    }

    @Override
    public void onBackPressed() {

            super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.purchase_room, menu);
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
}
