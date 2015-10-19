package com.casky.dlna.music;

import com.casky.dlna.main.MediaFileMetaData;
import com.casky.dlna.utils.Utils;

public class MusicMetaData implements MediaFileMetaData {
    private String title;
    private String id;
    private String name;
    private String path;
    private String mimeType;
    private long size;
    private String artist;
    private String album;
    private long duration;

    public MusicMetaData(String id, String name, String path, String title,
                         String artist, String album, String mimeType, long size,
                         long duration) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.mimeType = mimeType;
        this.size = size;
        this.duration = duration;
    }


    @Override
    public String getId() {
        return id;
    }


    @Override
    public String getPath() {
        return path;
    }


    @Override
    public String getMimeType() {
        return mimeType;
    }


    @Override
    public long getSize() {
        return size;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }


    public String getAlbum() {
        return album;
    }


    public long getDuration() {
        return duration;
    }


    @Override
    public String getMetadataString() {
        return Utils.createMetadataForMusic(id, title, path, artist, album, duration, mimeType, size);
    }
}
