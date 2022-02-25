package com.mactrix.www.musical;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Mactrix on 2/20/2018.
 */

public class SingListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    int[] instrumentals;
    String[] titles;
    String[] artists;
    MusicStudio musicStudio;
    SharedPref place;
    int pose;
    SharedPref singConst;


    public SingListAdapter(Context context,int[] instrumental, String[] title, String[] artist){
        this.context = context;
        instrumentals = instrumental;
        titles = title;
        artists = artist;
        inflater = LayoutInflater.from(context);
        musicStudio = new MusicStudio(context);
        place = new SharedPref(context,"singlist");
        registeronStop();
        singConst = new SharedPref(context,context.getResources().getString(R.string.singConst));

    }

    private class ViewHolder{

        ImageView indicator;
        TextView title;
        TextView artist;
        //TextView album;
        ImageView sing;

        public ViewHolder(View view, final int position){
          indicator = (ImageView)view.findViewById(R.id.songcircleimage);
          title = (TextView)view.findViewById(R.id.songtitle);
          artist = (TextView)view.findViewById(R.id.songartist);
          sing = (ImageView)view.findViewById(R.id.songmenu);


          title.setText(titles[position]);
          artist.setText(artists[position]);

          view.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  if(musicStudio!=null){
                      if(musicStudio.isPlaying()){
                          musicStudio.pauseMedia();
                          indicator.setBackgroundColor(context.getResources().getColor(R.color.transperent));
                          sing.setVisibility(View.GONE);
                      }else if(!musicStudio.isPlaying()){
                          musicStudio.reset();
                          musicStudio.playMusic(instrumentals[position]);
                          indicator.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                          sing.setVisibility(View.VISIBLE);
                          place.setInt(1);
                          pose = position;
                      }
                  }




              }
          });

          sing.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  if(singConst.getInt()==10) {

                      musicStudio.reset();

                      Bundle bundle = new Bundle();
                      Intent intent = new Intent(context, SingingStudio.class);

                      bundle.putString("singtitle", titles[position]);
                      bundle.putString("singartist", artists[position]);
                      bundle.putInt("singres", instrumentals[position]);

                      intent.putExtra("singingbundle", bundle);
                      context.startActivity(intent);
                  }else{
                      android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                      builder.setTitle("Notice!!!");
                      builder.setIcon(R.drawable.cancelhead);
                      builder.setMessage("Sorry, this feature you want to access is currently Locked.\n" +
                              "Click the 'Unlock Button' to Unlock this feature. Thank you");
                      builder.setPositiveButton("Unlock", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              context.startActivity(new Intent(context,PurchaseRoom.class));
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
          });

            if(musicStudio!=null){
                if(musicStudio.isPlaying()&&pose==position){
                    indicator.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                }
            }
        }
    }


    @Override
    public int getCount() {
        return instrumentals.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            convertView = inflater.inflate(R.layout.instrumentallist,parent,false);
            new ViewHolder(convertView,position);

        }catch (InflateException e){
            convertView = inflater.inflate(R.layout.instrumentallist,parent,false);
            new ViewHolder(convertView,position);
           // getView(position,convertView,parent);
        }catch (Exception e){
            convertView = inflater.inflate(R.layout.instrumentallist,parent,false);
            new ViewHolder(convertView,position);

           // getView(position,convertView,parent);
        }


        return convertView;
    }

    public void registeronStop(){
        IntentFilter filter = new IntentFilter("onStop");
        context.registerReceiver(onStop,filter);
    }


    private final BroadcastReceiver onStop = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            musicStudio.stopMedia();


            //context.unregisterReceiver(onStop);
        }
    };


}
