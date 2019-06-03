package musicplayer.cn.musicplayer.service;



import java.util.List;

import musicplayer.cn.musicplayer.entity.MusicInfo;

public interface OnPlayerStateChangeListener {
	void onStateChange(int state, int mode, List<MusicInfo> musicList,
                       int position);
}
