package musicplayer.cn.musicplayer.service;


public interface OnSeekChangeListener {

	void onSeekChange(int progress, int max, String time, String duration);
}
