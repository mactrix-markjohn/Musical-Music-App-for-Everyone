package com.mactrix.www.musical;

import android.provider.MediaStore;

/**
 * Created by Mactrix on 2/14/2018.
 */

public class MyAlbum {

    String album;
    String albumArt;
    String artist;
    int numberOfSongs;


    public MyAlbum(String album,String albumArt,String artist,int numberofSongs){

        this.album = album;
        this.albumArt = albumArt;
        this.artist = artist;
        this.numberOfSongs = numberofSongs;


    }

    public String getAlbum(){
        return album;
    }
    public String getAlbumArt(){
        return albumArt;
    }
    public String getArtist(){
        return artist;
    }
    public int getNumberOfSongs(){
        return numberOfSongs;
    }
}
