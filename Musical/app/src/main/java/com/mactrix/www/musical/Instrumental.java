package com.mactrix.www.musical;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Mactrix on 2/19/2018.
 */

public class Instrumental extends Fragment {


    Context context;
    ListView listView;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context= context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.instrumental,parent,false);

        listView = (ListView)v.findViewById(R.id.instrumentlist);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

       /* int[] instrumental = new int[]{R.raw.adele_hello
                ,R.raw.johnlegend_all_of_me
                ,R.raw.nickiminaj_pill_n_portions
                ,R.raw.samsmith_too_good_at_goodbye
                ,R.raw.tatiana_manaois_helplessly};

        String[] title = new String[]{"Hello"
                ,"All of Me"
                ,"Pill n Portions"
                ,"Too Good at GoodBye"
                ,"Helplessly"};

        String[] artist = new String[]{"Adele"
               , "John Legend",
                "Nicki Minaj","Sam Smith","Tatiana Manaois"};



        SingListAdapter singListAdapter = new SingListAdapter(context,instrumental,title,artist);
        listView.setAdapter(singListAdapter);*/






    }

    @Override
    public void onPause(){
        super.onPause();
        Intent intent = new Intent("onStop");
        context.sendBroadcast(intent);
    }

    @Override
    public void onStop(){
        super.onStop();
        Intent intent = new Intent("onStop");
        context.sendBroadcast(intent);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Intent intent = new Intent("onStop");
        context.sendBroadcast(intent);
    }


}
