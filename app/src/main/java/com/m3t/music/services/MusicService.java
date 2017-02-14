package com.m3t.music.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.m3t.music.activities.MainActivity;
import com.m3t.music.models.Song;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by namvp aka meo_3_the.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = "MUSIC SERVICE";

    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> playlist;
    private int position;
    private IBinder musicBind = new MusicBinder();

    @Override
    public void onCreate() {
        //  Create the service
        super.onCreate();

        //  Initial position
        position = 0;

        //  Create player
        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        //  Set Player Properties
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //  Listener
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    public void setPlaylist(ArrayList<Song> playlist) {
        this.playlist = playlist;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return super.onUnbind(intent);
    }

    private void playSong() {
        //  Play a song
        mMediaPlayer.reset();

        //  Get the song
        Song playingSong = playlist.get(position);
        long currentSong = playingSong.getId();
        String title = playingSong.getSongName();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);

        //  Play the track
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            //  massage the error
            Log.e(TAG, "Error setting data source");
        }
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //  Start playback
        mp.start();

        Intent notIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // TODO: 14/02/2017 Build notification layout
    }



    public void setSong(int songIndex) {
        //  Set the position for the current track
        this.position = songIndex;
    }

    public void pickSong(View view) {
        //  Pick a song from playlist
        //  MediaPlayer will move to the chosen song and start playing

        setSong(Integer.parseInt(view.getTag().toString()));
        playSong();
    }

    public int getPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void seek(int position) {
        mMediaPlayer.seekTo(position);
    }

    public void go() {
        mMediaPlayer.start();
    }

    public void playNext() {
        //  Skip to next
        position++;
        if (position >= playlist.size()) position = 0;
        playSong();
    }

    public void playPrev() {
        //  Back to previous
        position--;
        if (position < 0) position = playlist.size() - 1;
        playSong();
    }

    @Override
    public void onDestroy() {
        // TODO: 14/02/2017 stop foreground 
        super.onDestroy();
    }
}
