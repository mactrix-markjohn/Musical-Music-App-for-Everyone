package com.mactrix.www.musical;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Created by Mactrix on 2/19/2018.
 */

public class LyricsDatabase extends SQLiteOpenHelper {

    Cursor cursor;

    public static final String ID = "_id";
    public static final String TITLE ="title";
    public static final String LYRICS="lyrics";


    static final String NAME = "lyricsdatabase.db";
    static final int VERSION = 1;
    static final String TABLENAME="tablelyrics";

    static final String CREATE = "CREATE TABLE playlist (_id integer primary key, title text,lyrics text)";

    String[] columning = new String[]{ID,TITLE,LYRICS};

    public LyricsDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, NAME, factory, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLENAME+" (_id INTEGER PRIMARY KEY, title TEXT, lyrics TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE "+TABLENAME+" ADD COLUMN ALARM INTEGER");
        onCreate(db);
    }

    public long insertLyrics(String title,String lyrics){
        SQLiteDatabase dbase = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(TITLE,title);
        value.put(LYRICS,lyrics);
        long df = dbase.insert(TABLENAME,null,value);


        //dbase.close();
        return df;
    }

    public boolean deleteLyrics(long id){
        SQLiteDatabase dbase = this.getWritableDatabase();

        //  return dbase.delete(TABLENAME,ID+"=?",new String[]{Long.toString(id)})>0;
        // return dbase.delete(TABLENAME,ID+"="+id,null)>0;

        boolean df=dbase.delete(TABLENAME,ID+"=?",new String[]{Long.toString(id)})>0;


        //dbase.close();

        return df;
    }

    public Cursor getAllLyrics(){
        SQLiteDatabase dbase = this.getWritableDatabase();
        Cursor cursor =dbase.query(TABLENAME,columning,null,null,null,null,null);
        //dbase.close();
        return cursor;

    }
    public int updateLyrics(long id,String title, String lyrics){
        SQLiteDatabase dbase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE,title);
        values.put(LYRICS,lyrics);
        int update = dbase.update(TABLENAME,values,ID+"=?",new String[]{Long.toString(id)});

        return update;
    }
}
