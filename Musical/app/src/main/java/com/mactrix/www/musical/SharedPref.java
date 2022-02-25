package com.mactrix.www.musical;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mactrix on 2/13/2018.
 */

public class SharedPref {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    public SharedPref(Context context,String name){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(name,context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setInt(int number){
        editor.putInt("key",number);
        editor.commit();
    }
    public int getInt(){
        return sharedPreferences.getInt("key",-1);
    }

    public void setText(String text){
        editor.putString("keytext",text);
        editor.commit();
    }

    public String getText(){
        return sharedPreferences.getString("keytext","null");
    }

}
