package com.mactrix.www.musical;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mactrix on 2/20/2018.
 */

public class LyricsAdapter extends BaseAdapter {

    Context context;
    Cursor cursor;
    TextView songtitle;
    ImageView musicalImage;
    LayoutInflater inflater;
    LyricsDatabase lyricsDatabase;
    public LyricsAdapter(Context context, Cursor cursor,LyricsDatabase lyricsDatabase){
        this.context = context;
        this.cursor = cursor;
        this.lyricsDatabase = lyricsDatabase;
        inflater = LayoutInflater.from(context);
    }




    @Override
    public int getCount() {
        int c=0;
        if(cursor!=null){
            if(cursor.getCount()>0){
               c = cursor.getCount();
            }
        }

        return c;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        try{
            convertView = inflater.inflate(R.layout.lyricstorelist,parent,false);
            songtitle = (TextView)convertView.findViewById(R.id.songtitle);
            musicalImage = (ImageView)convertView.findViewById(R.id.musicalimage);

            if(cursor!=null){
                if(cursor.getCount()>0){
                    cursor.moveToPosition(position);
                    String title = cursor.getString(cursor.getColumnIndex(LyricsDatabase.TITLE));
                    songtitle.setText(title);
                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent  =  new Intent(context,LyricsStudio.class);
                    bundle.putInt("lyricsStudio",position);
                    intent.putExtra("lyricsStudio",bundle);
                    context.startActivity(intent);
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context,v);
                    popupMenu.inflate(R.menu.lyricsmenu);
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.addings: context.startActivity(new Intent(context,LyricsStudio.class));
                                    break;
                                case R.id.deletings:
                                    if(cursor!=null){
                                        if(cursor.getCount()>0){
                                            cursor.moveToPosition(position);
                                            long id = cursor.getLong(cursor.getColumnIndex(LyricsDatabase.ID));
                                            lyricsDatabase.deleteLyrics(id);
                                            Intent intent = new Intent("resuming");
                                            context.sendBroadcast(intent);
                                        }
                                    }

                                    break;
                            }

                            return true;
                        }
                    });



                    return true;
                }
            });

            musicalImage.setOnClickListener(new View.OnClickListener() {
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
                                        long id = cursor.getLong(cursor.getColumnIndex(LyricsDatabase.ID));
                                        lyricsDatabase.deleteLyrics(id);
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



        }catch (Exception e){
            convertView = inflater.inflate(R.layout.lyricstorelist,parent,false);
            songtitle = (TextView)convertView.findViewById(R.id.songtitle);

            if(cursor!=null){
                if(cursor.getCount()>0){
                    cursor.moveToPosition(position);
                    String title = cursor.getString(cursor.getColumnIndex(LyricsDatabase.TITLE));
                    songtitle.setText(title);
                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent  =  new Intent(context,LyricsStudio.class);
                    bundle.putInt("lyricsStudio",position);
                    intent.putExtra("lyricsStudio",bundle);
                    context.startActivity(intent);
                }
            });
        }

        return convertView;
    }
}
