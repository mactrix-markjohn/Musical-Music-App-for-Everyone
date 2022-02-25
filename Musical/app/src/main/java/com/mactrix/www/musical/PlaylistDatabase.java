package com.mactrix.www.musical;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mactrix on 2/15/2018.
 */

public class PlaylistDatabase extends SQLiteOpenHelper {

    Cursor cursor;

    public static final String ID = "_id";
    public static final String DATA = "data";
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String ALBUM = "album";
    public static final String ALBUMART = "albumart";


    public static final String NAME = "musicaldatabase.db";
    public static final int VERSION = 1;
    public static final String TABLENAME="mytable";

    String[] columning = new String[]{ID,DATA,TITLE,ARTIST,ALBUM,ALBUMART};




    public PlaylistDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, String tableName) {
        super(context, tableName, factory, 1);
        //TABLENAME = tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLENAME+" (_id integer primary key, data text, title text, artist text, album text,albumart text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE "+TABLENAME+" ADD COLUMN ALARM INTEGER");
        onCreate(db);
    }

    public long insertSong(String data, String title, String artist, String album,String albumart){
        SQLiteDatabase dbase = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(DATA,data);
        value.put(TITLE,title);
        value.put(ARTIST,artist);
        value.put(ALBUM,album);
        value.put(ALBUMART,albumart);
        long df = dbase.insert(TABLENAME,null,value);


        //dbase.close();
        return df;
    }

    public boolean deleteNote(long id){
        SQLiteDatabase dbase = this.getWritableDatabase();

        //  return dbase.delete(TABLENAME,ID+"=?",new String[]{Long.toString(id)})>0;
        // return dbase.delete(TABLENAME,ID+"="+id,null)>0;

        boolean df=dbase.delete(TABLENAME,ID+"=?",new String[]{Long.toString(id)})>0;


       //  dbase.close();

        return df;
    }

    public Cursor getAllSongs(){
        SQLiteDatabase dbase = this.getWritableDatabase();
        Cursor cursor =dbase.query(TABLENAME,columning,null,null,null,null,null);
     //   dbase.close();
        return cursor;

    }



}
