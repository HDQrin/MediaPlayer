package musicplayer.cn.musicplayer.module;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import musicplayer.cn.musicplayer.activity.OnlineMusic;

public class OnlineMusicInfo implements Serializable, Parcelable {
    private int musicId;
    private String musicName;
    private String musicAuthor;

    public OnlineMusicInfo(int musicId, String musicName, String musicAuthor){
        this.musicId = musicId;
        this.musicName = musicName;
        this.musicAuthor = musicAuthor;
    }
    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicAuthor() {
        return musicAuthor;
    }

    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
