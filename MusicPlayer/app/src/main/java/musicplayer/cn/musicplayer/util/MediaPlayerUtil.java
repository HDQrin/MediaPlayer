package musicplayer.cn.musicplayer.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.ImageView;

import musicplayer.cn.myapplication.R;

public class MediaPlayerUtil extends android.media.MediaPlayer {
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public void initMediaPlayer(Context context, String musicname)
    {
        /** 初始化播放器时做判断，若播放器不为空，停止播放，释放资源 */
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=new MediaPlayer();
        }
        try {
            /** 传入音乐名称与Constants类中的常量拼接为资源地址 */
            mediaPlayer.setDataSource(context,Uri.parse("http://114.115.216.215:8080/download/music/"+musicname+".mp3"));
            /** 使用异步准备资源 */
            mediaPlayer.prepareAsync();
            /** 准备完成，播放音乐 */
            mediaPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(android.media.MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 设置音乐播放状态，暂停，播放
    public void setMediaPlayerState(ImageView imageView)
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            imageView.setImageResource(R.drawable.player_play);
            //传入ImageView 暂停时改变ImageView图标为播放图标
        }
        else{
            mediaPlayer.start();
            imageView.setImageResource(R.drawable.player_pause);
            //重新播放时，改变图标为暂停图标
        }
    }
}
