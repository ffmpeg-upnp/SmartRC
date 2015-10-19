package com.casky.dlna.music;

import com.casky.dlna.main.MediaFile;
import com.casky.dlna.main.MediaFileMetaData;

/**
 * 项目名称：Smart_DLNA
 * 类名称：MusicFile
 * 类描述：
 * 创建人：shaojiansong
 * 创建时间＄1�714-9-25 下午3:53:44
 * 修改人：shaojiansong
 * 修改时间＄1�714-9-25 下午3:53:44
 * 修改备注＄1�7
 * 版本＄1�7 1.0
 */
public class MusicFile extends MediaFile {

    private boolean isPlaying = false;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public MusicFile(MediaFileMetaData metaData, int position) {
        super(metaData, position);
    }

    @Override
    public MusicMetaData getMetaData() {
        return (MusicMetaData) super.getMetaData();
    }

}
