package musicplayer.cn.musicplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import musicplayer.cn.musicplayer.activity.MainActivity;
import musicplayer.cn.musicplayer.activity.OnlineMusic;
import musicplayer.cn.musicplayer.constant.DbFinal;
import musicplayer.cn.musicplayer.module.OnlineMusicInfo;
import musicplayer.cn.myapplication.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OnlineListAdapter extends BaseAdapter {
    // 上下文
    private Context context;
    // 数据源
    private Cursor cursor;
    // 用于判断checkbox
    private List<Boolean> popCheckStatus;
    // 正在播放的歌曲位置
    private int playPosition = 0;

    private View musicview;

    private List<OnlineMusicInfo> musiclist;
    public OnlineListAdapter(Context context,List<OnlineMusicInfo> list){
        this.context = context;
        this.musiclist = list;
    }

    @Override
    public int getCount() {
        return musiclist == null ? 0 : musiclist.size();
    }

    @Override
    public Object getItem(int position) {
        return musiclist == null ? null : musiclist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return musiclist == null ? 0 : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        musicview = convertView;
        if (musicview == null) {
            holder = new ViewHolder();
            musicview = LayoutInflater.from(context).inflate(
                    R.layout.online_music_listview_item, null);
            holder.title = (TextView) musicview
                    .findViewById(R.id.onlinemusic_listitem_song_text);
            holder.title.setText(musiclist.get(position).getMusicName());
            holder.artist = (TextView) musicview
                    .findViewById(R.id.onlinemusic_listitem_artist_text);
            holder.artist.setText(musiclist.get(position).getMusicAuthor());

            holder.download = (Button) musicview
                    .findViewById(R.id.onlinemusic_download_action);

            holder.indicator = (ImageView) musicview
                    .findViewById(R.id.onlinemusic_listitem_indicator);
            musicview.setTag(holder);
        } else {
            holder = (ViewHolder) musicview.getTag();
        }

        if (position == playPosition) {
            holder.indicator.setVisibility(View.VISIBLE);
        } else {
            holder.indicator.setVisibility(View.GONE);
        }
        holder.download.setTag(position);
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("本条音乐名称：",holder.title.toString());
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder().url("http://114.115.216.215:8080/download/music/"+holder.title.getText()+".mp3").build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Looper.prepare();
                        Toast.makeText(context, "正在下载请勿退出", Toast.LENGTH_LONG).show();
                        File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/Amusic");
                        if(!directory.exists()) { directory.mkdirs(); }
                        File file = new File(directory, holder.title.getText()+".mp3");
                        InputStream inputStream= response.body().byteStream();
                        byte[] buffer = new byte[2048];
                        long contentLength = response.body().contentLength();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        int subLength = 0, sumLength = 0;
                        while((subLength = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, subLength);
                            sumLength += subLength;
                            int progress = (int)(sumLength * 1.0 / contentLength * 100);
                        }
                        fileOutputStream.flush();
                        Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                });

            }
        });
        return musicview;
    }
    public void setPlayPosition(int playPosition) {
        this.playPosition = playPosition;
    }
    private class ViewHolder {
        TextView title;
        TextView artist;
        Button download;
        ImageView indicator;
    }
}

