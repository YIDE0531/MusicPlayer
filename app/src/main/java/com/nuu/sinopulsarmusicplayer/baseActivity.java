package com.nuu.sinopulsarmusicplayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.nuu.sinopulsarmusicplayer.Services.OnClearFromRecentService;

import static com.nuu.sinopulsarmusicplayer.AlbumActivity.mediaPlayer;
import static com.nuu.sinopulsarmusicplayer.AlbumActivity.tracks;
import static com.nuu.sinopulsarmusicplayer.AlbumActivity.position;

public abstract class baseActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    private NotificationManager notificationManager;
    private boolean isPlaying = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

        registerHeadsetPlugReceiver();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "KOD Dev", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case CreateNotification.ACTION_PREVIUOS:
                    onTrackPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (isPlaying){
                        onTrackPause();
                    } else {
                        onTrackPlay();
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    onTrackNext();
                    break;
            }
        }
    };

    private void registerHeadsetPlugReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        this.registerReceiver(headsetPlugReceiver, intentFilter);
    }

    private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                stopPlay();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopNotification();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(headsetPlugReceiver);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(!mediaPlayer.isLooping()){
            if(position==tracks.size()-1){
                position = -1;
            }
            onTrackNext();
        }
    }

    protected void stopNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }
    }

    protected abstract void stopPlay();

    protected void onTrackPrevious(){
    };

    protected void onTrackPause(){
        isPlaying = false;
    }
    protected void onTrackPlay(){
        isPlaying = true;
    }
    protected abstract void onTrackNext();

}
