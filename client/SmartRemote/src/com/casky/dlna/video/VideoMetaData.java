package com.casky.dlna.video;

import com.casky.dlna.main.MediaFileMetaData;
import com.casky.dlna.utils.Utils;

public class VideoMetaData implements MediaFileMetaData{
	public String id;
	public String name;
	public String title;
	public String path;
	public String mimeType;
	public long size;
	public String resolution;
	public String artist;
	public long duration;
	
	public VideoMetaData(String id, String title,String name, String path,
			String artist, String resolution, String mimeType, long size,
			long duration){
		this.id = id;
		this.title = title;
		this.name = name;
		this.path = path;
		this.artist = artist;
		this.resolution = resolution;
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
	
	public String getName(){
		return name;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getArtist() {
		return artist;
	}


	public String getResolution() {
		return resolution;
	}


	public long getDuration() {
		return duration;
	}
	
	@Override
	public String getMetadataString(){
		return Utils.createMetadataForVideo(id, name, path, artist, resolution, mimeType, size, duration);
	}

}
