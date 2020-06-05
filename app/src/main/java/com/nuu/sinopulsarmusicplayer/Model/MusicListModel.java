package com.nuu.sinopulsarmusicplayer.Model;

public class MusicListModel {
    private String albumName, albumSinger, albumSerial, albumTime;
    private boolean isFlag;

    public MusicListModel(String albumName, String albumSinger, String albumSerial, String albumTime){
        this.albumName = albumName;
        this.albumSinger = albumSinger;
        this.albumSerial = albumSerial;
        this.albumTime = albumTime;
    }

    public String getAlbumName(){
        return albumName;
    }

    public String getSinger(){
        return albumSinger;
    }

    public String getAlbumSerial(){
        return albumSerial;
    }

    public String getAlbumTime(){
        return albumTime;
    }

    public boolean isFlag() {
        return isFlag;
    }

    public void setFlag(boolean flag) {
        isFlag = flag;
    }
}
