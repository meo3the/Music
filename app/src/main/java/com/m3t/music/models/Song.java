package com.m3t.music.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

/**
 * Created by namvp aka meo_3_the.
 * <p>
 * Class Song.java
 */

public class Song {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long id;
    private String filePath;
    private String songName;
    private String album;
    private Bitmap albumImage;
    private String artist;
    private String albumArtist;
    private String authur;
    private String genre;

    public Song(String filePath) {
        this.filePath = filePath;
        getMediaMetadata();
    }

    public Song(long id, String songName, String artist) {
        this.id = id;
        this.songName = songName;
        this.artist = artist;
    }

    private void getMediaMetadata() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        albumImage = BitmapFactory.decodeByteArray(
                retriever.getEmbeddedPicture(), 0, retriever.getEmbeddedPicture().length
        );
        album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        albumArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
        artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        authur = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
        genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
    }

    public String getSongName() {
        return songName;
    }

    public String getAlbum() {
        return album;
    }

    public Bitmap getAlbumImage() {
        return albumImage;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public String getAuthur() {
        return authur;
    }

    public String getGenre() {
        return genre;
    }

    @Override
    public String toString() {
        return songName + " - " + artist;
}
}
