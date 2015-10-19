package com.casky.dlna.video;


import com.casky.dlna.main.MediaFile;

public class Video extends MediaFile{

	private boolean isPlaying = false;

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	
	public Video(VideoMetaData metadata,int position) {
        super(metadata,position);
    }


    @Override
	public VideoMetaData getMetaData() {
		return (VideoMetaData)super.getMetaData();
	}
    
}