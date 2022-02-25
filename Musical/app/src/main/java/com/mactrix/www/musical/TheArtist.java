package com.mactrix.www.musical;

import java.util.ArrayList;

/**
 * Created by Mactrix on 2/12/2018.
 */

public class TheArtist {

    String artist;
    String artistAlbumArtist;
    int numberOfsong;
    ArrayList<String> albumArt;

    public TheArtist(String artist,String artistAlbumArtist,int numberOfsong){
        this.artist=artist;
        this.artistAlbumArtist = artistAlbumArtist;
        this.numberOfsong = numberOfsong;

    }
     public TheArtist(String artist,ArrayList<String> artistAlbumArtist,int numberOfsong){
        this.artist=artist;
        this.albumArt = artistAlbumArtist;
        this.numberOfsong = numberOfsong;

    }

    public ArrayList<String> getAlbumArt(){
         return albumArt;
    }



    public String getArtist(){
        return artist;
    }
    public String getArtistAlbumArtist(){
        return artistAlbumArtist;
    }
    public int getNumberOfsong(){
        return numberOfsong;
    }
}
