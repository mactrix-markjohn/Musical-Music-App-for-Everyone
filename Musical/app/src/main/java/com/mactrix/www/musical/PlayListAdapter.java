package com.mactrix.www.musical;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mactrix on 2/15/2018.
 */

public class PlayListAdapter extends BaseAdapter {

    Context context;
    Cursor cursor;
    LayoutInflater inflater;
    Cursor playCursor;
    PlaylistDatabase playlistDatabase;
    MainPlayListDataBase mainPlayListDataBase;

    public PlayListAdapter (Context context, Cursor cursor,MainPlayListDataBase mainPlayListDataBase){
        this.context = context;
        this.cursor = cursor;
        inflater = LayoutInflater.from(context);
        this.mainPlayListDataBase = mainPlayListDataBase;
    }


    public class ViewHolder{
        CircleImageView playcircle;
        ImageView delete;
        TextView playtitle;
        ArrayList<String> datas;
        ArrayList<String> title;
        ArrayList<String> artist;
        ArrayList<String> album;
        ArrayList<String> albumart;

        public ViewHolder(View view, final int position){

            playcircle = (CircleImageView) view.findViewById(R.id.playcircleimage);
            playcircle.setImageBitmap(decodeImage(R.mipmap.playing));
            playtitle =(TextView)view.findViewById(R.id.playlisttitle);
            delete = (ImageView) view.findViewById(R.id.deletemenu);

            if(cursor!=null){
                if(cursor.getCount()>0){
                    cursor.moveToPosition(position);
                    String text = cursor.getString(cursor.getColumnIndex(MainPlayListDataBase.TITLE));
                    playtitle.setText(text);
                }
            }


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context,v);
                    popupMenu.inflate(R.menu.play_list);
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.action_settings: if(cursor!=null){
                                    if(cursor.getCount()>0){
                                        cursor.moveToPosition(position);
                                        long id = cursor.getLong(cursor.getColumnIndex(MainPlayListDataBase.ID));
                                        mainPlayListDataBase.deleteNote(id);
                                        Intent intent = new Intent("resuming");
                                        context.sendBroadcast(intent);
                                    }
                                }
                                    break;
                            }

                            return true;
                        }
                    });
                }
            });


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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

                            playlistDatabase = new PlaylistDatabase(context,null,null,1,taking);
                            playCursor = playlistDatabase.getAllSongs();

                            if(playCursor!=null){
                                if(playCursor.getCount()>0){
                                   /* datas = new ArrayList<>();
                                    title = new ArrayList<>();
                                    artist = new ArrayList<>();
                                    album = new ArrayList<>();
                                    albumart = new ArrayList<>();

                                    for(int i=0;i<playCursor.getCount();i++){
                                        playCursor.moveToPosition(i);
                                        datas.add(playCursor.getString(playCursor.getColumnIndex(PlaylistDatabase.DATA)));
                                        title.add(playCursor.getString(playCursor.getColumnIndex(PlaylistDatabase.TITLE)));
                                        artist.add(playCursor.getString(playCursor.getColumnIndex(PlaylistDatabase.ARTIST)));
                                        album.add(playCursor.getString(playCursor.getColumnIndex(PlaylistDatabase.ALBUM)));
                                        albumart.add(playCursor.getString(playCursor.getColumnIndex(PlaylistDatabase.ALBUMART)));

                                    }*/

                                    Intent intent = new Intent(context,PlayList.class);
                                    Bundle bundle = new Bundle();

                                    //bundle.putStringArrayList(context.getResources().getString(R.string.songarray),datas);
                                    //bundle.putStringArrayList(context.getResources().getString(R.string.titlearray),title);
                                    //bundle.putStringArrayList(context.getResources().getString(R.string.artistarray),artist);
                                    //bundle.putStringArrayList(context.getResources().getString(R.string.albumarray),album);
                                    //bundle.putStringArrayList(context.getResources().getString(R.string.albumartarray),albumart);
                                    bundle.putString("tablename",taking);
                                    intent.putExtra(context.getResources().getString(R.string.songbundle),bundle);
                                    context.startActivity(intent);


                                }else{
                                    Toast.makeText(context, "There is no Song in this Playlist", Toast.LENGTH_LONG).show();
                                }
                            }



                        }
                    }

                }
            });
        }
    }


    @Override
    public int getCount() {
        int count =0;
        if(cursor!=null){
            count = cursor.getCount();
        }

        return count;
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

        try {

            convertView = inflater.inflate(R.layout.playlist_lay,parent,false);
            new ViewHolder(convertView,position);

        }catch (InflateException e){
            convertView = inflater.inflate(R.layout.playlist_lay,parent,false);
            new ViewHolder(convertView,position);

        }catch (Exception e){
            convertView = inflater.inflate(R.layout.playlist_lay,parent,false);
            new ViewHolder(convertView,position);

        }

        return convertView;
    }

    public Bitmap decodeImage(int res){
        Bitmap bitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),res,options);
        options.inSampleSize = calculateSampleSize(options,200,200);
        options.inJustDecodeBounds=false;
        bitmap = BitmapFactory.decodeResource(context.getResources(),res,options);
        return bitmap;
    }

    public int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
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
}
