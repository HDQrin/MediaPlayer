package musicplayer.cn.musicplayer.lyric;

import java.util.List;


public interface ILrcBuilder {
    List<LrcRow> getLrcRows(String rawLrc);
}
