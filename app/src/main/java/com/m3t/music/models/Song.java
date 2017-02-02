package com.m3t.music.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

/**
 * Created by NamVp aka meo3the on 13/01/2017.
 */

public class Song {
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

}
