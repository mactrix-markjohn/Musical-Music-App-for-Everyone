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

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by Mactrix on 2/20/2018.
 */

public class SinglistOwnAdapter extends BaseAdapter {
    ArrayList<MySongs> mySongs;
    MusicStudio musicStudio;
    LayoutInflater inflater;
    Context context;
    ArrayList<String> datas;
    ArrayList<String> titles;
    ArrayList<String> artists;
    ArrayList<String> albums;
    SharedPref place;
    int pose;
    SharedPref singConst;


    public SinglistOwnAdapter(Context context, ArrayList<MySongs> mySongs){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mySongs = mySongs;
        musicStudio = new MusicStudio(context);
        place = new SharedPref(context,"singlist");

        datas = new ArrayList<>();
        titles = new ArrayList<>();
        artists = new ArrayList<>();
        albums = new ArrayList<>();

        if(mySongs!=null){
            for (int i=0;i<mySongs.size();i++){
                datas.add(mySongs.get(i).getData());
                titles.add(mySongs.get(i).getTitle());
                artists.add(mySongs.get(i).getArtist());
                albums.add(mySongs.get(i).getAlbum());
            }
        }


        registeronStop();
        singConst = new SharedPref(context,context.getResources().getString(R.string.singConst));
    }

    public class  ViewHolder{
        ImageView indicator;
        TextView title;
        TextView artist;
        TextView album;
        ImageView sing;


        public ViewHolder(View view, final int position){
            indicator = (ImageView)view.findViewById(R.id.songcircleimage);
            title = (TextView)view.findViewById(R.id.songtitle);
            artist = (TextView)view.findViewById(R.id.songartist);
            sing = (ImageView)view.findViewById(R.id.songmenu);
            album=(TextView)view.findViewById(R.id.songalbum);

            title.setText(titles.get(position));
            artist.setText(artists.get(position));
            album.setText(albums.get(position));


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //if(musicStudio!=null){
                        if(musicStudio.isPlaying()){
                            musicStudio.pauseMedia();
                            indicator.setBackgroundColor(context.getResources().getColor(R.color.transperent));
                            sing.setVisibility(View.GONE);
                        }else if(!musicStudio.isPlaying()){
                            musicStudio.reset();
                            musicStudio.playMedia(datas.get(position));
                            indicator.setBackgroundColor(context.getResources().getColor(R.color.blurtrans));
                            sing.setVisibility(View.VISIBLE);
                            place.setInt(2);
                            pose = position;
                        }
                   // }




                }
            });

            sing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(singConst.getInt()!=10) {

                        musicStudio.reset();


                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(context, SingingStudio.class);

                        bundle.putString("singtitle", titles.get(position));
                        bundle.putString("singartist", artists.get(position));
                        bundle.putString("singdata", datas.get(position));
                        bundle.putString("singalbum", albums.get(position));

                        intent.putExtra("singingstudio", bundle);
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
        return mySongs.size();
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
            //getView(position,convertView,parent);
        }catch (Exception e){

            convertView = inflater.inflate(R.layout.instrumentallist,parent,false);
            new ViewHolder(convertView,position);

            //getView(position,convertView,parent);
        }


        return convertView;
    }


    private final BroadcastReceiver stop = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            musicStudio.reset();
        }
    };

    public void registeronStop(){
        IntentFilter filter = new IntentFilter("onStop");
        context.registerReceiver(onStop,filter);
    }


    private final BroadcastReceiver onStop = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            musicStudio.stopMedia();


           // context.unregisterReceiver(onStop);
        }
    };
}
