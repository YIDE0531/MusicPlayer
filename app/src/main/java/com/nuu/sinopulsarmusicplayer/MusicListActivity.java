package com.nuu.sinopulsarmusicplayer;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nuu.sinopulsarmusicplayer.Adapter.MusicListAdapter;
import com.nuu.sinopulsarmusicplayer.Model.MusicListModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nuu.sinopulsarmusicplayer.AlbumActivity.position;
import static com.nuu.sinopulsarmusicplayer.AlbumActivity.isThisPage;
import static com.nuu.sinopulsarmusicplayer.AlbumActivity.mediaPlayer;
import static com.nuu.sinopulsarmusicplayer.AlbumActivity.tracks;

public class MusicListActivity extends baseActivity {
    public final String regEx = "[`~!@#$%^&*()=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——|{}【】‘；：”“’。，、？]|\n|\r|\t";
    private ImageView imvPicture, imvBack;
    private ImageButton play, previous, next;
    private LinearLayout llGoMusic;
    private TextView title, tvSingerName, tvTitle, tvTitleName;
    private EditText edtSearch;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        imvBack =  (ImageView) findViewById(R.id.header_bar_left_btn);
        previous = findViewById(R.id.imb_previous);
        play = findViewById(R.id.imb_play);
        next = findViewById(R.id.imb_next);
        title = findViewById(R.id.tv_title);
        tvSingerName = findViewById(R.id.tv_singer_name);
        tvTitle = findViewById(R.id.header_bar_title_text);
        tvTitleName = findViewById(R.id.tv_singer);
        edtSearch = (EditText) findViewById(R.id.edt_search);

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
        moveToPosition((LinearLayoutManager) lvAlbum.getLayoutManager(), position);

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
        edtSearch.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
        tvTitleName.setVisibility(View.GONE);
    }

    void initListener(){
        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isThisPage = true;
                isSecondPage = false;
                finish();
            }
        });

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

                TextView textView = (TextView) view.findViewById(R.id.tv_serial);
                if(AlbumActivity.position != Integer.valueOf(textView.getText().toString())-1){
                    mediaPlayer.stop();
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(tracks.get(Integer.valueOf(textView.getText().toString())-1).getSongPath());
                        mediaPlayer.prepare();                   // 准备
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.setOnCompletionListener(MusicListActivity.this);
                    mediaPlayer.start();
                }
                AlbumActivity.position = Integer.valueOf(textView.getText().toString())-1;
                Intent intent = new Intent();
                intent.setClass(mContext, musicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("songName", musicList.get(position).getAlbumName());
                intent.putExtras(bundle);
                startActivityForResult(intent, 101);
            }
        });

        // Capture Text in EditText
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = edtSearch.getText().toString();
                if(isSpecialChar(text)){
                    //特殊字元檢核沒有通過
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "字元有誤", Toast.LENGTH_SHORT).show();
                        }
                    });
                    edtSearch.setText("");
                    return ;
                }
                if (albumAdapter!=null) {
                    albumAdapter.filter(text);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // TODO Auto-generated method stub
                if ((actionId == EditorInfo.IME_ACTION_DONE) ||
                        (actionId == EditorInfo.IME_ACTION_GO) ||
                        (actionId == EditorInfo.IME_ACTION_NEXT) ||
                        (actionId == EditorInfo.IME_ACTION_SEND) ||
                        (actionId == EditorInfo.IME_ACTION_SEARCH) ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    hideSoftInputWindow();
                }
                return true;
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
                moveToPosition((LinearLayoutManager) lvAlbum.getLayoutManager(), position);

                if(vdThread==null||!vdThread.isAlive()){
                    vdThread = new VideoThreed();
                    vdThread.start();
                }
                break;
        }
    }

    /**
     * 滑动到指定位置
     */
    public void moveToPosition(LinearLayoutManager manager, int n) {
        manager.scrollToPositionWithOffset(n, 0);
        manager.setStackFromEnd(true);
    }

    // 隱藏軟體鍵盤
    synchronized protected void hideSoftInputWindow()
    {	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            if (imm.isActive()) {
                // 將鍵盤收起來
                imm.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
    }

    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public boolean isSpecialChar(String str) {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
}