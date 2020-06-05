package com.nuu.sinopulsarmusicplayer;

public class Track {

    private String title;
    private String artist;
    private String songPath;
    private String songTime;
    private int image, song, albumSerial;

    public Track(String title, String artist, int image, int song, String songPath, String songTime, int albumSerial) {
        this.title = title;
        this.artist = artist;
        this.image = image;
        this.song = song;
        this.songPath = songPath;
        this.songTime = songTime;
        this.albumSerial = albumSerial;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getSong() {
        return song;
    }

    public void setSong(int song) {
        this.song = song;
    }

    public String getSongTime() {
        return songTime;
    }

    public void setSongTime(String songTime) {
        this.songTime = songTime;
    }

    public String getSongPath() {
        return songPath;
    }

    public int getAlbumSerial() {
        return albumSerial;
    }
}
