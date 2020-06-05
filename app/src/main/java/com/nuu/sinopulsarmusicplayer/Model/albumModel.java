package com.nuu.sinopulsarmusicplayer.Model;

public class albumModel {
    private int albumPicture;
    private String albumName, albumNum;

    public albumModel(int albumPicture, String albumName, String albumNum){
        this.albumPicture = albumPicture;
        this.albumName = albumName;
        this.albumNum = albumNum;
    }

    public int getAlbumPicture(){
        return albumPicture;
    }

    public String getAlbumName(){
        return albumName;
    }

    public String getAlbumNum(){
        return albumNum;
    }
}
