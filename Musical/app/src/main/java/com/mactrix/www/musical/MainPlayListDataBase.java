package com.mactrix.www.musical;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mactrix on 2/15/2018.
 */

public class MainPlayListDataBase extends SQLiteOpenHelper {

    Cursor cursor;

    public static final String ID = "_id";
    public static final String TITLE ="title";


    static final String NAME = "musicaldatabase.db";
    static final int VERSION = 1;
    static final String TABLENAME="playlist";

    static final String CREATE = "CREATE TABLE playlist (_id integer primary key, title text)";

    String[] columning = new String[]{ID,TITLE};



    public MainPlayListDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, NAME, factory, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE playlist (_id INTEGER PRIMARY KEY, title TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("ALTER TABLE "+TABLENAME+" ADD COLUMN ALARM INTEGER");
        onCreate(db);
    }

    public long insertSong(String title){
        SQLiteDatabase dbase = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(TITLE,title);
        long df = dbase.insert(TABLENAME,null,value);


        //dbase.close();
        return df;
    }

    public boolean deleteNote(long id){
        SQLiteDatabase dbase = this.getWritableDatabase();

        //  return dbase.delete(TABLENAME,ID+"=?",new String[]{Long.toString(id)})>0;
        // return dbase.delete(TABLENAME,ID+"="+id,null)>0;

        boolean df=dbase.delete(TABLENAME,ID+"=?",new String[]{Long.toString(id)})>0;


        //dbase.close();

        return df;
    }

    public Cursor getAllSongs(){
        SQLiteDatabase dbase = this.getWritableDatabase();
        Cursor cursor =dbase.query(TABLENAME,columning,null,null,null,null,null);
        //dbase.close();
        return cursor;

    }





}
