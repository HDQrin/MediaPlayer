package musicplayer.cn.musicplayer.service;

import java.util.List;

import musicplayer.cn.musicplayer.entity.MusicInfo;
import musicplayer.cn.musicplayer.module.OnlineMusicInfo;

public interface OnOnlinePlayerStateChangeListener {
    void onStateChange(int state, int mode, List<OnlineMusicInfo> musicList,
                       int position);
}
