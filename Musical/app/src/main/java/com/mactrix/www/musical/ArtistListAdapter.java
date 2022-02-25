package com.mactrix.www.musical;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mactrix on 2/12/2018.
 */

public class ArtistListAdapter extends BaseAdapter {

    ArrayList<TheArtist> theArtist;
    ArrayList<MySongs> mySongs;
    ArrayList<String> artistNames;
    ArrayList<String> artistAlbumArt;
    ArrayList<String> datas;
    ArrayList<String> albums;
    ArrayList<String> titles;
    ArrayList<Integer> numberOfsong;
    LayoutInflater inflater;
    Context context;


    public ArtistListAdapter(Context context, ArrayList<TheArtist> theArtist,ArrayList<MySongs> mySongs){

        this.context = context;
        this.theArtist=theArtist;
        this.mySongs = mySongs;
        inflater = LayoutInflater.from(context);


        numberOfsong = new ArrayList<>();


        /*for(int i=0;i<mySongs.size();i++){
            datas.add(mySongs.get(i).getData());
            titles.add(mySongs.get(i).getTitle());
            artistNames.add(mySongs.get(i).getArtist());
            albums.add(mySongs.get(i).getAlbum());
            artistAlbumArt.add(mySongs.get(i).getAlbumArt());
        }*/

    }

    public class ViewHolding{
        CircleImageView circleImageView;
        TextView artistName;
        TextView numberOfsongs;
        String filepic;


        public ViewHolding(View view, final int position){

            circleImageView = (CircleImageView)view.findViewById(R.id.artistcircleimage);
            artistName = (TextView)view.findViewById(R.id.artistname);
            numberOfsongs = (TextView)view.findViewById(R.id.artistsongs);

           // getSongsOfArtist(theArtist.get(position).getArtist());

            //circleImageView.setImageBitmap(decodeAlbumArt(theArtist.get(position).getAlbumArt()));
            if(decodeAlbumArt(theArtist.get(position).getAlbumArt())!=null){
                Glide.with(context).load(decodeAlbumArt(theArtist.get(position).getAlbumArt())).into(circleImageView);
            }else{
                Glide.with(context).load(R.mipmap.playing).into(circleImageView);
            }
            artistName.setText(theArtist.get(position).getArtist());
            numberOfsongs.setText(""+theArtist.get(position).getNumberOfsong());



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getSongsOfArtist(theArtist.get(position).getArtist());
                    String artistName = theArtist.get(position).getArtist();

                   // Intent sendIntent = new Intent(context.getResources().getString(R.string.servicesetup));
                    Intent collector = new Intent(context,CollectSongs.class);

                    Bundle songBundle = new Bundle();
                    songBundle.putStringArrayList(context.getResources().getString(R.string.songarray),datas);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.albumarray),albums);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.artistarray),artistNames);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.albumartarray),artistAlbumArt);
                    songBundle.putStringArrayList(context.getResources().getString(R.string.titlearray),titles);
                    songBundle.putString("filepic",filepic);
                    songBundle.putString("artistname",artistName);


                    //sendIntent.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                    collector.putExtra(context.getResources().getString(R.string.songbundle),songBundle);
                   // context.sendBroadcast(sendIntent);
                    context.startActivity(collector);



                }
            });





        }

        public String decodeAlbumArt(ArrayList<String> filing){
            Bitmap albumArt = null;
            String file="";

            for(int i=0;i<filing.size();i++) {
                 file = filing.get(i);

                try {
                   /* if (file != null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(file, options);
                        options.inSampleSize = calculateSampleSize(options, 100, 100);
                        options.inJustDecodeBounds = false;
                        albumArt = BitmapFactory.decodeFile(file, options);
                        filepic =file;
                        break;

                    } else {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeResource(context.getResources(), R.mipmap.playing, options);
                        options.inSampleSize = calculateSampleSize(options, 100, 100);
                        options.inJustDecodeBounds = false;
                        albumArt = BitmapFactory.decodeResource(context.getResources(), R.mipmap.playing, options);

                    }*/
                } catch (IllegalArgumentException e) {
                    /*BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(context.getResources(), R.mipmap.playing, options);
                    options.inSampleSize = calculateSampleSize(options, 100, 100);
                    options.inJustDecodeBounds = false;
                    albumArt = BitmapFactory.decodeResource(context.getResources(), R.mipmap.playing, options);*/
                    file=null;
                }catch (OutOfMemoryError e){
                    decodeAlbumArt(filing);
                }catch (Exception e){
                    decodeAlbumArt(filing);
                }
            }
            return file;
        }

        public void getSongsOfArtist(String artistName){

            try{

                datas = new ArrayList<>();
                albums = new ArrayList<>();
                titles = new ArrayList<>();
                artistNames = new ArrayList<>();
                artistAlbumArt = new ArrayList<>();

                for(int i=0;i<mySongs.size();i++){
                    if(mySongs.get(i).getArtist().equalsIgnoreCase(artistName)){
                        datas.add(mySongs.get(i).getData());
                        titles.add(mySongs.get(i).getTitle());
                        artistNames.add(mySongs.get(i).getArtist());
                        albums.add(mySongs.get(i).getAlbum());
                        artistAlbumArt.add(mySongs.get(i).getAlbumArt());

                    }
                }

            }catch (NullPointerException e){

                datas = new ArrayList<>();
                albums = new ArrayList<>();
                titles = new ArrayList<>();
                artistNames = new ArrayList<>();
                artistAlbumArt = new ArrayList<>();

                for(int i=0;i<mySongs.size();i++){
                    if(artistName.equalsIgnoreCase(mySongs.get(i).getArtist())){
                        datas.add(mySongs.get(i).getData());
                        titles.add(mySongs.get(i).getTitle());
                        artistNames.add(mySongs.get(i).getArtist());
                        albums.add(mySongs.get(i).getAlbum());
                        artistAlbumArt.add(mySongs.get(i).getAlbumArt());

                    }
                }

            }catch (Exception e){

                datas = new ArrayList<>();
                albums = new ArrayList<>();
                titles = new ArrayList<>();
                artistNames = new ArrayList<>();
                artistAlbumArt = new ArrayList<>();

                for(int i=0;i<mySongs.size();i++){
                    if(artistName.equalsIgnoreCase(mySongs.get(i).getAlbum())){
                        datas.add(mySongs.get(i).getData());
                        titles.add(mySongs.get(i).getTitle());
                        artistNames.add(mySongs.get(i).getArtist());
                        albums.add(mySongs.get(i).getAlbum());
                        artistAlbumArt.add(mySongs.get(i).getAlbumArt());

                    }
                }

            }




        }


    }


    @Override
    public int getCount() {
        return theArtist.size();
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

            if(convertView==null) {
                convertView = inflater.inflate(R.layout.artist_list, parent, false);
                new ViewHolding(convertView, position);
            }else{
                new ViewHolding(convertView,position);
            }
        }catch (InflateException e){



                convertView = inflater.inflate(R.layout.artist_list, parent, false);
            new ViewHolding(convertView,position);
        }catch (Exception e){


                convertView = inflater.inflate(R.layout.artist_list, parent, false);
            new ViewHolding(convertView,position);
        }
        return convertView;
    }
    public int calculateSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
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

    public Bitmap decodealbumArt(String file){
        Bitmap albumArt=null;
        byte[] art=null;

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {


            metadataRetriever.setDataSource(file);

            art = metadataRetriever.getEmbeddedPicture();

            if(art!=null){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(art,0,art.length,options);
                options.inSampleSize = calculateSampleSize(options,150,150);
                options.inJustDecodeBounds=false;
                albumArt = BitmapFactory.decodeByteArray(art,0,art.length,options);
            }else{
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(context.getResources(),R.mipmap.defautling,options);
                options.inSampleSize = calculateSampleSize(options,150,150);
                options.inJustDecodeBounds=false;

                albumArt = BitmapFactory.decodeResource(context.getResources(),R.mipmap.defautling,options);
            }
        }catch (IllegalArgumentException e){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(),R.mipmap.defautling,options);
            options.inSampleSize = calculateSampleSize(options,150,150);
            options.inJustDecodeBounds=false;

            albumArt = BitmapFactory.decodeResource(context.getResources(),R.mipmap.defautling,options);
           /* ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap arting = Bitmap.createBitmap(albumArt);
            arting.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
            art = outputStream.toByteArray();*/

        }



        /*if(file!=null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file,options);
            options.inSampleSize = calculateSampleSize(options,500,500);
            options.inJustDecodeBounds=false;


            albumArt  = BitmapFactory.decodeFile(file,options);
        }else{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(),R.mipmap.cele6,options);
            options.inSampleSize = calculateSampleSize(options,200,200);
            options.inJustDecodeBounds=false;

            albumArt = BitmapFactory.decodeResource(context.getResources(),R.mipmap.cele6,options);
        }*/
        return albumArt;

    }

}
