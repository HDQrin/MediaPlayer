package musicplayer.cn.musicplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import musicplayer.cn.musicplayer.adapter.OnlineListAdapter;
import musicplayer.cn.musicplayer.module.OnlineMusicInfo;
import musicplayer.cn.musicplayer.service.OnOnlinePlayerStateChangeListener;
import musicplayer.cn.musicplayer.util.MediaPlayerUtil;
import musicplayer.cn.myapplication.R;
import musicplayer.com.loopj.android.http.BaseClient;
import musicplayer.com.loopj.android.http.JsonHttpResponseHandler;


public class OnlineMusic extends Activity {
    // 歌曲显示listview
    private ListView lvMusic;
    // 无歌曲时显示的图片
    private ImageView nothingImg;
    // 歌曲adapter
    private OnlineListAdapter adapter;
    // 标题栏处的按钮
    private ImageButton back, scan;
    // 查询本地数据库得到的cursor
    private Cursor curLocal;
    //当前歌曲播放位置
    int CURRENT = 0;
    // 正在扫描对话框
    private Dialog scanDialog;

    // 播放音乐
    private ArrayList<OnlineMusicInfo> musicList;

    // miniplayer专辑图片，歌曲名，歌手名
    private ImageView album;
    private TextView title;
    private TextView artist;
    private ImageView indicator;
    // miniplayer播放，下一首按钮
    private ImageView last,play, next;
    // miniplayer跳转到播放显示歌词页面
    private RelativeLayout miniPlayer;

    // 播放状态改变监听器
    private OnOnlinePlayerStateChangeListener changeListener;
    private Boolean flag = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_music);
        nothingImg = (ImageView) findViewById(R.id.online_nothing_img);
        lvMusic = (ListView) findViewById(R.id.online_listview);
        back = (ImageButton) findViewById(R.id.online_actionbar_menu);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        scan = (ImageButton) findViewById(R.id.online_actionbar_scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvMusic.setVisibility(View.VISIBLE);
                scanDialog();
                BaseClient.get("http://114.115.216.215:8080/MusicPlayer/musiclist.action",null,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.e("onSuccess", response.toString());
                        try {
                            if (response.getInt("status") == 200) {
                                JSONArray value = response.getJSONArray("musicinfo");
                                Log.e("content", value.toString());
                                musicList = new ArrayList<OnlineMusicInfo>();
                                for (int i = 0; i < value.length(); i++) {
                                    JSONObject jsonObject =  (JSONObject) value.get(i);
                                    int musicid=jsonObject.getInt("musicId");
                                    String musicname = jsonObject.getString("musicName");
                                    String musicauthor = jsonObject.getString("musicAuthor");
                                    OnlineMusicInfo musicInfo = new OnlineMusicInfo(musicid,musicname,musicauthor);
                                    musicList.add(musicInfo);
                                }
                                adapter = new OnlineListAdapter(OnlineMusic.this,musicList);
                                lvMusic.setAdapter(adapter);
                                Log.e("音乐列表",musicList.toString());
                                scanDialog.dismiss();
                                nothingImg.setVisibility(View.GONE);
                                lvMusic.setVisibility(View.VISIBLE);
                            }
                            else{
                                scanDialog.dismiss();
                                nothingImg.setVisibility(View.VISIBLE);
                                lvMusic.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                });
                nothingImg.setVisibility(View.GONE);
            }
        });
        setListViewClick();
        initMiniPlayer();
    }

    /**
     * 初始化miniplayer中控件，以及设置监听事件
     */
    private void initMiniPlayer() {

        album = (ImageView) findViewById(R.id.online_miniplayer_album);
        title = (TextView) findViewById(R.id.online_miniplayer_song);
        artist = (TextView) findViewById(R.id.online_miniplayer_artist);
        last = (ImageView) findViewById(R.id.online_miniplayer_last);
        play = (ImageView) findViewById(R.id.online_miniplayer_play);
        next = (ImageView) findViewById(R.id.online_miniplayer_next);
        miniPlayer = (RelativeLayout) findViewById(R.id.online_miniplayer_layout);
        last.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(CURRENT==0)
                {
                    CURRENT= musicList.size()-1;
                    PlayMusic();
                    if (adapter != null) {
                        adapter.setPlayPosition(CURRENT);
                        adapter.notifyDataSetChanged();
                    }
                }
                else
                {
                    CURRENT--;
                    PlayMusic();
                    if (adapter != null) {
                        adapter.setPlayPosition(CURRENT);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.setMediaPlayerState(play);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(CURRENT==musicList.size()-1)
                {
                    CURRENT=0;
                    PlayMusic();
                    if (adapter != null) {
                        adapter.setPlayPosition(CURRENT);
                        adapter.notifyDataSetChanged();
                    }
                }
                else
                {
                    CURRENT++;
                    PlayMusic();
                    if (adapter != null) {
                        adapter.setPlayPosition(CURRENT);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    /**
     * listView监听事件
     */
    private MediaPlayerUtil mMediaPlayer = new MediaPlayerUtil();
    private int prevposition;
    private void setListViewClick() {

        lvMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView item_music = view.findViewById(R.id.onlinemusic_listitem_song_text);
                indicator = view.findViewById(R.id.onlinemusic_listitem_indicator);
               // indicator.setVisibility(View.GONE);
                Log.e("音乐名：",item_music.getText().toString());
                String url = null;
                try {
                    url = URLEncoder.encode(item_music.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(url.indexOf("+")!= -1)
                    url = url.replace("+","%20");
                mMediaPlayer.initMediaPlayer(getApplication(),url);
                CURRENT = position;
                title.setText(musicList.get(CURRENT).getMusicName());
                artist.setText(musicList.get(CURRENT).getMusicAuthor());
                if (adapter != null) {
                    adapter.setPlayPosition(CURRENT);
                    adapter.notifyDataSetChanged();
                }
                play.setImageResource(R.drawable.player_pause);


            }
        });
    }


    /**
     * 扫描对话框
     */
    private void scanDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineMusic.this);
        builder.setCancelable(true);
        View dialogView = LayoutInflater.from(OnlineMusic.this).inflate(
                R.layout.scan_dialog, null);
        builder.setView(dialogView);
        RotateAnimation anim = new RotateAnimation(0, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(1000);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(-1);
        ImageView dialogImg = (ImageView) dialogView
                .findViewById(R.id.scan_img);
        dialogImg.startAnimation(anim);
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                scanDialog.dismiss();
                nothingImg.setVisibility(View.VISIBLE);
                lvMusic.setVisibility(View.GONE);
            }
        });
        scanDialog = builder.create();
        scanDialog.show();

    }

    /** 播放音乐 */
    public void PlayMusic()
    {
        String url = null;
        try {
            url = URLEncoder.encode(musicList.get(CURRENT).getMusicName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(url.indexOf("+")!= -1)
            url = url.replace("+","%20");


        mMediaPlayer.initMediaPlayer(getApplication(),url);
        play.setImageResource(R.drawable.player_pause);
        title.setText(musicList.get(CURRENT).getMusicName());
        artist.setText(musicList.get(CURRENT).getMusicAuthor());
        //Toast.makeText(this, musicList.get(CURRENT).getMusicName(), Toast.LENGTH_SHORT).show();
    }
}
