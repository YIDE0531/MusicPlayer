package com.nuu.sinopulsarmusicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nuu.sinopulsarmusicplayer.Adapter.AlbumAdapter;
import com.nuu.sinopulsarmusicplayer.Model.albumModel;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AlbumActivity extends baseActivity {
    private ImageView imvPicture;
    private ImageButton play, previous, next;
    private LinearLayout llGoMusic;
    private TextView title, tvSingerName;
    private RecyclerView lvAlbum;
    private ProgressBar progressBar;
    private VideoThreed vdThread;

    public static int position = 0;
    private boolean isPlaying = false;
    public static MediaPlayer mediaPlayer;
    private Context mContext;
    private ArrayList<albumModel> albumList ;
    public static List<Track> tracks;
    public static boolean isThisPage = true;
    private long firstTime = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        String[] permission = {WRITE_EXTERNAL_STORAGE};
        if (!hasPermissions(AlbumActivity.this, permission)){
            ActivityCompat.requestPermissions(this, permission, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }else{
            mContext = AlbumActivity.this;
            popluateTracks();
            init();
            initData();
            initListener();
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(tracks.get(position).getSongPath());
                mediaPlayer.prepare();                   // 准备
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnCompletionListener(this);
        }

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
        albumList = new ArrayList<>();
        albumModel albumModel = new albumModel(R.drawable.album1, "未知歌手", String.valueOf(tracks.size()));
        albumList.add(albumModel);
        albumModel = new albumModel(R.drawable.album1, "米津玄師專輯", "1");
        albumList.add(albumModel);
        albumModel = new albumModel(R.drawable.album1, "V.K克專輯", "0");
        albumList.add(albumModel);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        lvAlbum.setLayoutManager(linearLayoutManager);
        lvAlbum.setItemAnimator(new DefaultItemAnimator());
        AlbumAdapter albumAdapter = new AlbumAdapter(mContext, albumList);
        lvAlbum.setAdapter(albumAdapter);

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

    }

    //populate list with tracks
    private void popluateTracks(){
        tracks = new ArrayList<>();
        initListView();
//        tracks.add(new Track("【中字 HD】臉紅的思春期(Bolbbalgan4)_ You(=I)", "臉紅的思春期", R.drawable.t1, R.raw.m1,""));
//        tracks.add(new Track("▲UNNATURAL《法醫女王》／《非自然死亡》Lemon", "米津玄師", R.drawable.t2, R.raw.m1, ""));
//        tracks.add(new Track("浪子回頭", "茄子蛋", R.drawable.t3, R.raw.m1, ""));
    }

    @Override
    public void onTrackPrevious() {
        if(isThisPage) {
            if (position > 0) {
                position--;
            }else{
                position=0;
            }
            CreateNotification.createNotification(AlbumActivity.this, tracks.get(position),
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

        if(isThisPage) {
            CreateNotification.createNotification(AlbumActivity.this, tracks.get(position),
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

        if(isThisPage) {
            CreateNotification.createNotification(AlbumActivity.this, tracks.get(position),
                    R.drawable.ic_play_arrow_black_24dp, position, tracks.size() - 1);
            play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            title.setText(tracks.get(position).getTitle());
            isPlaying = false;
            mediaPlayer.pause();
        }
    }

    @Override
    public void onTrackNext() {
        if(isThisPage) {
            if (position < tracks.size() - 1) {
                position++;
            } else {
                position = 0;
            }
            CreateNotification.createNotification(AlbumActivity.this, tracks.get(position),
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
        mediaPlayer.stop();
        vdThread.interrupt();
    }

    @Override
    protected void stopPlay() {
        onTrackPause();
    }

    @Override
    protected void stopNotification() {
        super.stopNotification();
        System.exit(0);
    }

    class VideoThreed extends Thread {
        @Override
        public void run() {
            int nowtime, mMax, sMax;
            while (isThisPage) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime < 2000) {
                finish();
                //System.exit(0);
            } else {
                Toast.makeText(mContext, "再點一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //nitListView()实现对手机中MediaDataBase的扫描
    private void initListView() {
        tracks.clear();
        //获取ContentResolver的对象，并进行实例化
        ContentResolver resolver = getContentResolver();
        //获取游标
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER); //创建游标MediaStore.Audio.Media.EXTERNAL_CONTENT_URI获取音频的文件，后面的是关于select筛选条件，这里填土null就可以了
        //游标归零
        int musicTime;
        String show;
        DecimalFormat format = new DecimalFormat("00");
        int i = 0;

        if(cursor.moveToFirst()){
            do {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));            //获取歌名
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));         //获取歌唱者
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));           //获取专辑名
                int albumID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));            //获取专辑图片id
                int length = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                //创建Music对象，并赋值
                musicTime = length / 1000;
                show = format.format(musicTime / 60) + ":" + format.format(musicTime % 60);
                Track music = new Track(title, artist, R.drawable.album1, R.drawable.album1, path, show, i);
                //Log.e("asd", "length:"+length+" title:"+title+" artist:"+artist+" album:"+album+" path:"+path+" albumBip:"+albumID);
                //将music放入musicList集合中
                tracks.add(music);
                i++;
            }  while (cursor.moveToNext());
        }else {
            Toast.makeText(mContext, "本地没有音乐哦", Toast.LENGTH_SHORT).show();
        }
        cursor.close();                                                                         //关闭游标
    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mContext = AlbumActivity.this;
                    popluateTracks();
                    init();
                    initData();
                    initListener();
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(tracks.get(position).getSongPath());
                        mediaPlayer.prepare();                   // 准备
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.setOnCompletionListener(this);
                }else{
                    this.finish();
                    System.exit(0);
                }
                break;
        }
    }

}
