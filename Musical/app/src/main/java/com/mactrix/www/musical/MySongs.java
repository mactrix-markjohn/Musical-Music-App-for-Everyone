package com.mactrix.www.musical;

import android.media.MediaPlayer;
import android.provider.MediaStore;

/**
 * Created by Mactrix on 2/4/2018.
 */

public class MySongs {
    private String data;
    private String title;
    private String album;
    private String artist;
    private String albumArt;
    private String artistColumn;
    private String albumColumn;
    private String playlist;
    private String playlistColumn;
    private String albuming;
    private String albumKey;

    public MySongs(String data,String title,String album,String artist){
        this.data=data;
        this.title=title;
        this.album=album;
        this.artist=artist;
    }
    public MySongs(String data,String title,String album,String artist,String albumArt){
        this.data=data;
        this.title=title;
        this.album=album;
        this.artist=artist;
        this.albumArt=albumArt;
    }
    public MySongs(String data,String title,String album,String artist,String albumArt,String albuming,String albumkey){
        this.data=data;
        this.title=title;
        this.album=album;
        this.artist=artist;
        this.albumArt=albumArt;
        this.albuming = albuming;
        this.albumKey=albumkey;
    }
    public MySongs(String artistColumn, String albumColumn,String playlistColumn){
        this.artistColumn=artistColumn;
        this.albumColumn=albumColumn;
        this.playlistColumn=playlistColumn;
    }

    public void setAlbumKey(String albumKey){
        this.albumKey=albumKey;
    }
    public String getAlbumKey(){
        return albumKey;
    }

    public void setData(String data){
        this.data=data;
    }
    public String getData(){
        return data;
    }

    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return title;
    }

    public void setAlbum(String album){
        this.album = album;
    }
    public String getAlbum(){
        return album;
    }

    public String getAlbuming(){
        return albuming;
    }
    public void setAlbuming(String albuming){
        this.albuming=albuming;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }
    public String getArtist(){
        return artist;
    }

    public void setAlbumArt(String albumArt){
        this.albumArt = albumArt;
    }
    public String getAlbumArt(){
        return albumArt;
    }

    public void setAlbumColumn(String albumColumn){
        this.albumColumn = albumColumn;
    }
    public String getAlbumColumn(){
        return albumColumn;
    }

    public void setArtistColumn(String artistColumn){
        this.artistColumn = artistColumn;
    }
    public String getArtistColumn(){
        return artistColumn;
    }

    public void setPlaylist(String playlist){
        this.playlist = playlist;
    }
    public String getPlaylist(){
        return playlist;
    }

    public void setPlaylistColumn(String playlistColumn){
        this.playlistColumn = playlistColumn;
    }
    /*public String getAlbum(){
        return album;
    }*/

}
