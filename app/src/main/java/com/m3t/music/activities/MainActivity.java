package com.m3t.music.activities;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;

import com.m3t.music.controlers.MusicController;
import com.m3t.music.models.Song;
import com.m3t.music.services.MusicService;
import com.m3t.myapplication.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MediaController.MediaPlayerControl {

    public static final String TAG = "MUSIC";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fabShuffle)
    FloatingActionButton fabShuffle;
    @BindView(R.id.navView)
    NavigationView navView;
    @BindView(R.id.drawer)
    DrawerLayout drawer;

    ArrayList<Song> songArrayList;
    private boolean mBound = false;
    private Intent playIntent;
    private MusicService mService;
    private MusicController mController;

    private boolean paused = false;
    private boolean playbackPaused = false;


    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;

            //  get Service
            mService = binder.getService();

            // pass list
            mService.setPlaylist(songArrayList);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
        setController();

    }

    @Override
    protected void onStart() {
        //  When Activity start
        //  Set views up
        super.onStart();

        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            setController();
            paused = false;
        }
    }

    @Override
    protected void onStop() {
        mController.hide();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  Inflate the menu
        //  This adds items to the action bar if it present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void init() {
        songArrayList = new ArrayList<>();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawerOpen, R.string.drawerClose);
        drawer.addDrawerListener(toggle);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();
        navView.setNavigationItemSelectedListener(this);
        queryAllSong();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.navHome:
                break;
            case R.id.navPlayQueue:
                break;
            case R.id.navPlaylist:
                break;
            case R.id.navArtist:
                break;
            case R.id.navAlbum:
                break;
            case R.id.navSong:
                break;
            case R.id.navGenre:
                break;
            case R.id.navSetting:
                break;
            case R.id.navHelp:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void queryAllSong() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int mIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int mTitleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int mArtistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                long mId = musicCursor.getLong(mIdColumn);
                String mTitle = musicCursor.getString(mTitleColumn);
                String mArtist = musicCursor.getString(mArtistColumn);

                if (!mTitle.contains("Facebook") && !mTitle.contains("Hangout"))
                    songArrayList.add(new Song(mId, mTitle, mArtist));
            } while (musicCursor.moveToNext());

            musicCursor.close();

            Log.d(TAG, songArrayList.toString());
        }
    }

    private void setController() {
        //  Set controller up
        mController = new MusicController(this);
        mController.setPrevNextListeners(v -> playNext(),
                v -> playPrev());

        mController.setMediaPlayer(this);
        mController.setAnchorView(new View(this));
        mController.setEnabled(true);
    }

    private void playPrev() {
        mService.playPrev();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        mController.show(0);
    }

    private void playNext() {
        mService.playNext();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        mController.show(0);
    }

    private void pickSong(View view) {
        mService.setSong(Integer.parseInt(view.getTag().toString()));
        mService.playSong();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        mController.show(0);
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        mService = null;
        mBound = false;

        super.onDestroy();
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mService != null && mBound && mService.isPlaying()) return mService.getPosition();
        return 0;
    }

    @Override
    public int getDuration() {
        if (mService != null && mBound && mService.isPlaying()) return mService.getDuration();
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return mService != null && mBound && mService.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public void pause() {
        playbackPaused = true;
        mService.pause();
    }

    @Override
    public void seekTo(int pos) {
        mService.seek(pos);
    }

    @Override
    public void start() {
        mService.go();
    }

}
