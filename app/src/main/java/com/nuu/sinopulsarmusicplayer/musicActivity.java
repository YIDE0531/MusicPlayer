package com.nuu.sinopulsarmusicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.text.DecimalFormat;
import static com.nuu.sinopulsarmusicplayer.AlbumActivity.position;
import static com.nuu.sinopulsarmusicplayer.AlbumActivity.isThisPage;
import static com.nuu.sinopulsarmusicplayer.AlbumActivity.mediaPlayer;
import static com.nuu.sinopulsarmusicplayer.AlbumActivity.tracks;

public class musicActivity extends baseActivity implements View.OnClickListener {
    private ImageView imvPicture, imvRandom, imvPrevious, imvPlay, imvNext, imvLoop, imvBack, imvMenu;
    private TextView tvTitle, tvSinger, tvPlayTime, tvEndTime, tvNowPos;
    private SeekBar seekBar;
    private VideoThreed vdThread;

    private boolean isPlaying = false;
    private Context mContext;
    private String songName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music);
        mContext = musicActivity.this;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songName = bundle.getString("songName");

        initView();
        initData();
        initListener();
    }

    void initView(){
        imvBack =  (ImageView) findViewById(R.id.header_bar_left_btn);
        tvTitle = (TextView) findViewById(R.id.header_bar_title_text);
        tvSinger = (TextView) findViewById(R.id.tv_singer);
        imvMenu =  (ImageView) findViewById(R.id.header_bar_right_btn);

        imvPicture = (ImageView) findViewById(R.id.imv_picture);
        imvRandom = (ImageView) findViewById(R.id.imv_random);
        imvPrevious = (ImageView) findViewById(R.id.imv_previous);
        imvPlay = (ImageView) findViewById(R.id.imv_play);
        imvNext = (ImageView) findViewById(R.id.imv_next);
        imvLoop = (ImageView) findViewById(R.id.imv_loop);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        tvPlayTime = (TextView) findViewById(R.id.tv_play_time);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);
        tvNowPos = (TextView) findViewById(R.id.tv_now_postiton);
    }

    void initData(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.album1);
        imvPicture.setImageBitmap(toRoundCorner(bitmap,80.f));

        imvBack.setOnClickListener(this);
        imvMenu.setOnClickListener(this);
        imvRandom.setOnClickListener(this);
        imvPrevious.setOnClickListener(this);
        imvPlay.setOnClickListener(this);
        imvNext.setOnClickListener(this);
        imvLoop.setOnClickListener(this);

        isThisPage = false;
        if(mediaPlayer.isPlaying()){
            onTrackPlay();
            CreateNotification.createNotification(musicActivity.this, tracks.get(AlbumActivity.position),
                    R.drawable.ic_pause_black_24dp, position, tracks.size() - 1);
        }else{
            CreateNotification.createNotification(musicActivity.this, tracks.get(position),
                    R.drawable.ic_play_arrow_black_24dp, position, tracks.size() - 1);
        }

        if(mediaPlayer.isLooping()){
            imvLoop.setImageResource(R.drawable.single_repeat);
        }else{
            imvLoop.setImageResource(R.drawable.repeat);
        }

        Glide.with(mContext).load(tracks.get(position).getImage())
                .thumbnail(Glide.with(mContext).load(R.drawable.t1))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imvPicture);
        tvTitle.setText(songName);
        tvTitle.setSelected(true);
        tvSinger.setText(tracks.get(position).getArtist());
        MusicListActivity.isSecondPage = false;
        mediaPlayer.setOnCompletionListener(this);
    }

    void initListener(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 拖动停止时调用

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 拖动开始时调用

            }

            @Override
            public void onProgressChanged(SeekBar seekBar2, int progress, boolean fromUser) {
                // 拖动改变时调用
                // 获取seeKbar的当前值
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }
        });

    }

    public static Bitmap toRoundCorner(Bitmap bitmap, float pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.header_bar_left_btn:
                isThisPage = true;
                MusicListActivity.isSecondPage = true;
                finish();
                break;
            case R.id.header_bar_right_btn:

                break;
            case R.id.imv_random:

                break;
            case R.id.imv_previous:
                onTrackPrevious();
                break;
            case R.id.imv_play:
                if (isPlaying){
                    onTrackPause();
                } else {
                    onTrackPlay();
                }
                break;
            case R.id.imv_next:
                onTrackNext();
                break;
            case R.id.imv_loop:
                if(mediaPlayer.isLooping()){
                    imvLoop.setImageResource(R.drawable.repeat);
                    mediaPlayer.setLooping(false);
                    Toast.makeText(mContext, "循環撥放", Toast.LENGTH_SHORT).show();
                }else{
                    imvLoop.setImageResource(R.drawable.single_repeat);
                    mediaPlayer.setLooping(true);
                    Toast.makeText(mContext, "單曲循環", Toast.LENGTH_SHORT).show();
                }
                break;
            default:

                break;
        }
    }

    @Override
    public void onTrackPrevious() {
        if (position > 0) {
            position--;
        }else{
            position=0;
        }
        CreateNotification.createNotification(musicActivity.this, tracks.get(position),
                R.drawable.ic_pause_black_24dp, position, tracks.size() - 1);
        tvTitle.setText(tracks.get(position).getTitle());
        tvNowPos.setText(String.valueOf(position+1)+"/"+tracks.size());
        Glide.with(mContext).load(tracks.get(position).getImage())
                .thumbnail(Glide.with(mContext).load(R.drawable.t1))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imvPicture);
        Glide.with(mContext).load(R.drawable.repeat)
                .thumbnail(Glide.with(mContext).load(R.drawable.t1))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imvLoop);
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

    @Override
    public void onTrackPlay() {
        super.onTrackPlay();

        CreateNotification.createNotification(musicActivity.this, tracks.get(position),
                R.drawable.ic_pause_black_24dp, position, tracks.size() - 1);
        imvPlay.setImageResource(android.R.drawable.ic_media_pause);
        tvTitle.setText(tracks.get(position).getTitle());
        tvNowPos.setText(String.valueOf(position+1)+"/"+tracks.size());
        isPlaying = true;
        mediaPlayer.start();

        if (vdThread == null || !vdThread.isAlive()) {
            vdThread = new VideoThreed();
            vdThread.start();
        }
    }

    @Override
    public void onTrackPause() {
        super.onTrackPause();

        CreateNotification.createNotification(musicActivity.this, tracks.get(position),
                R.drawable.ic_play_arrow_black_24dp, position, tracks.size() - 1);
        imvPlay.setImageResource(android.R.drawable.ic_media_play);
        tvTitle.setText(tracks.get(position).getTitle());
        isPlaying = false;
        mediaPlayer.pause();
    }

    @Override
    public void onTrackNext() {
        if (position < tracks.size() - 1) {
            position++;
        } else {
            position = 0;
        }
        CreateNotification.createNotification(musicActivity.this, tracks.get(position),
                R.drawable.ic_pause_black_24dp, position, tracks.size() - 1);
        tvTitle.setText(tracks.get(position).getTitle());
        tvNowPos.setText(String.valueOf(position+1)+"/"+tracks.size());
        Glide.with(mContext).load(tracks.get(position).getImage())
                .thumbnail(Glide.with(mContext).load(R.drawable.t1))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imvPicture);
        Glide.with(mContext).load(R.drawable.repeat)
                .thumbnail(Glide.with(mContext).load(R.drawable.t1))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imvLoop);
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
            MusicListActivity.isSecondPage = true;

            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    class VideoThreed extends Thread {
        @Override
        public void run() {
            int nowtime, mMax, sMax;
            while (!isThisPage) {
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
            int musicTime = mediaPlayer.getCurrentPosition() / 1000;
            DecimalFormat format = new DecimalFormat("00");
            String show = format.format(musicTime / 60) + ":" + format.format(musicTime % 60);
            tvPlayTime.setText(show);

            musicTime = mediaPlayer.getDuration() / 1000;
            show = format.format(musicTime / 60) + ":" + format.format(musicTime % 60);
            tvEndTime.setText(show);

            seekBar.setMax(msg.arg2);
            seekBar.setProgress(msg.arg1);
        }
    };

}