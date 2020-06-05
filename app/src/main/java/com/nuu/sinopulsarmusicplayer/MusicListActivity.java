package com.nuu.sinopulsarmusicplayer;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nuu.sinopulsarmusicplayer.Adapter.MusicListAdapter;
import com.nuu.sinopulsarmusicplayer.Model.MusicListModel;

import java.io.IOException;
import java.util.ArrayList;

import static com.nuu.sinopulsarmusicplayer.AlbumActivity.position;
import static com.nuu.sinopulsarmusicplayer.AlbumActivity.isThisPage;
import static com.nuu.sinopulsarmusicplayer.AlbumActivity.mediaPlayer;
import static com.nuu.sinopulsarmusicplayer.AlbumActivity.tracks;

public class MusicListActivity extends baseActivity {
    private ImageView imvPicture;
    private ImageButton play, previous, next;
    private LinearLayout llGoMusic;
    private TextView title, tvSingerName;
    private RecyclerView lvAlbum;
    private ProgressBar progressBar;
    private VideoThreed vdThread;

    private boolean isPlaying = false;
    private Context mContext;
    private ArrayList<MusicListModel> musicList;
    public static boolean isSecondPage = true;
    private MusicListAdapter albumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        mContext = MusicListActivity.this;
        isSecondPage = true;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //position = bundle.getInt("posistion");
        init();
        initData();
        initListener();

        mediaPlayer.getDuration();
        mediaPlayer.getCurrentPosition();
        mediaPlayer.setOnCompletionListener(this);
    }

    void init(){
        previous = findViewById(R.id.imb_previous);
        play = findViewById(R.id.imb_play);
        next = findViewById(R.id.imb_next);
        title = findViewById(R.id.tv_title);
        tvSingerName = findViewById(R.id.tv_singer_name);

        lvAlbum = findViewById(R.id.lv_album);
        imvPicture = findViewById(R.id.imv_picture);
        progressBar = findViewById(R.id.progressBar);
        llGoMusic = findViewById(R.id.ll_go_music);

    }

    void initData(){
        musicList = new ArrayList<>();
        for(int i = 0; i< tracks.size(); i++){
            MusicListModel albumModel = new MusicListModel(tracks.get(i).getTitle(), tracks.get(i).getArtist(), String.valueOf(i+1), tracks.get(i).getSongTime());
            musicList.add(albumModel);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        lvAlbum.setLayoutManager(linearLayoutManager);
        lvAlbum.setItemAnimator(new DefaultItemAnimator());
        albumAdapter = new MusicListAdapter(mContext, musicList);
        lvAlbum.setAdapter(albumAdapter);

        title.setText(tracks.get(position).getTitle());
        Glide.with(mContext).load(tracks.get(position).getImage())
                .thumbnail(Glide.with(mContext).load(R.drawable.t1))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imvPicture);
        isThisPage = false;
        if(mediaPlayer.isPlaying()){
            onTrackPlay();
            isPlaying = true;
        }
        mediaPlayer.setOnCompletionListener(this);
        for (int i = 0; i < musicList.size(); i++) {
            if(i==position){
                musicList.get(i).setFlag(true);
            }
            else {
                musicList.get(i).setFlag(false);
            }
            albumAdapter.notifyDataSetChanged();
        }
    }

    void initListener(){
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTrackPrevious();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTrackNext();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying){
                    onTrackPause();
                } else {
                    onTrackPlay();
                }
            }
        });

        llGoMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, musicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("songName", tracks.get(position).getTitle());
                intent.putExtras(bundle);
                startActivityForResult(intent, 101);
            }
        });

        albumAdapter.setOnItemClickListener(new MusicListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                for (int i = 0; i < musicList.size(); i++) {
                    if(i==position){
                        musicList.get(i).setFlag(true);
                    }
                    else {
                        musicList.get(i).setFlag(false);
                    }
                    albumAdapter.notifyDataSetChanged();
                }


                if(AlbumActivity.position != position){
                    mediaPlayer.stop();
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(tracks.get(position).getSongPath());
                        mediaPlayer.prepare();                   // 准备
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.setOnCompletionListener(MusicListActivity.this);
                    mediaPlayer.start();
                }

                AlbumActivity.position = position;
                Intent intent = new Intent();
                intent.setClass(mContext, musicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("songName", musicList.get(position).getAlbumName());
                intent.putExtras(bundle);
                startActivityForResult(intent, 101);
            }
        });

    }

    @Override
    public void onTrackPrevious() {
        if(isSecondPage) {
            if (position > 0) {
                position--;
            }else{
                position=0;
            }
            for (int i = 0; i < musicList.size(); i++) {
                if(i==position){
                    musicList.get(i).setFlag(true);
                }
                else {
                    musicList.get(i).setFlag(false);
                }
                albumAdapter.notifyDataSetChanged();
            }
            CreateNotification.createNotification(MusicListActivity.this, tracks.get(position),
                    R.drawable.ic_pause_black_24dp, position, tracks.size() - 1);
            title.setText(tracks.get(position).getTitle());
            tvSingerName.setText(tracks.get(position).getArtist());
            Glide.with(mContext).load(tracks.get(position).getImage())
                    .thumbnail(Glide.with(mContext).load(R.drawable.t1))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imvPicture);
            mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(tracks.get(position).getSongPath());
                mediaPlayer.prepare();                   // 准备
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnCompletionListener(this);
            onTrackPlay();
        }
    }

    @Override
    public void onTrackPlay() {
        super.onTrackPlay();

        if(isSecondPage) {
            CreateNotification.createNotification(MusicListActivity.this, tracks.get(position),
                    R.drawable.ic_pause_black_24dp, position, tracks.size() - 1);
            play.setImageResource(R.drawable.ic_pause_black_24dp);
            title.setText(tracks.get(position).getTitle());
            tvSingerName.setText(tracks.get(position).getArtist());
            isPlaying = true;
            mediaPlayer.start();

            if (vdThread == null || !vdThread.isAlive()) {
                vdThread = new VideoThreed();
                vdThread.start();
            }
        }
    }

    @Override
    public void onTrackPause() {
        super.onTrackPause();

        if(isSecondPage) {
            CreateNotification.createNotification(MusicListActivity.this, tracks.get(position),
                    R.drawable.ic_play_arrow_black_24dp, position, tracks.size() - 1);
            play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            title.setText(tracks.get(position).getTitle());
            isPlaying = false;
            mediaPlayer.pause();
        }
    }

    @Override
    public void onTrackNext() {
        if(isSecondPage) {
            if (position < tracks.size() - 1) {
                position++;
            } else {
                position = 0;
            }
            for (int i = 0; i < musicList.size(); i++) {
                if(i==position){
                    musicList.get(i).setFlag(true);
                }
                else {
                    musicList.get(i).setFlag(false);
                }
                albumAdapter.notifyDataSetChanged();
            }
            CreateNotification.createNotification(MusicListActivity.this, tracks.get(position),
                    R.drawable.ic_pause_black_24dp, position, tracks.size() - 1);
            title.setText(tracks.get(position).getTitle());
            tvSingerName.setText(tracks.get(position).getArtist());
            Glide.with(mContext).load(tracks.get(position).getImage())
                    .thumbnail(Glide.with(mContext).load(R.drawable.t1))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imvPicture);
            mediaPlayer.stop();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(tracks.get(position).getSongPath());
                mediaPlayer.prepare();                   // 准备
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnCompletionListener(this);
            onTrackPlay();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(vdThread!=null){
            vdThread.interrupt();
        }
    }

    @Override
    protected void stopPlay() {
        onTrackPause();
    }

    @Override
    public void stopNotification(){}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            isThisPage = true;
            isSecondPage = false;
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    class VideoThreed extends Thread {
        @Override
        public void run() {
            int nowtime, mMax, sMax;
            while (isSecondPage) {
                try {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        nowtime = mediaPlayer.getCurrentPosition();//得到当前歌曲播放进度(秒)
                        mMax = mediaPlayer.getDuration();//最大秒数

                        Message message = new Message();
                        message.arg1 = nowtime;
                        message.arg2 = mMax;

                        updateHandler.sendMessage(message);
                        Thread.sleep(300);// 每间隔1秒发送一次更新消息
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressBar.setMax(msg.arg2);
            progressBar.setProgress(msg.arg1);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode){
            case 101:
                isThisPage = false;
                for (int i = 0; i < musicList.size(); i++) {
                    if(i==position){
                        musicList.get(i).setFlag(true);
                    }
                    else {
                        musicList.get(i).setFlag(false);
                    }
                    albumAdapter.notifyDataSetChanged();
                }
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.ic_pause_black_24dp);
                }else{
                    play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
                Glide.with(mContext).load(tracks.get(position).getImage())
                        .thumbnail(Glide.with(mContext).load(R.drawable.t1))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imvPicture);
                title.setText(tracks.get(position).getTitle());
                tvSingerName.setText(tracks.get(position).getArtist());

                if(vdThread==null||!vdThread.isAlive()){
                    vdThread = new VideoThreed();
                    vdThread.start();
                }
                break;
        }
    }
}