package com.casky.dlna.picture;

import com.casky.dlna.main.MediaFileMetaData;
import com.casky.dlna.utils.Utils;

public class PictureMetaData implements MediaFileMetaData{
	public String id;
	public String title;
	public String path;
	public String mimeType;
	public long size;
	
	public PictureMetaData(String id, String title, String path,
			String mimeType, long size){
		this.id = id;
		this.title = title;
		this.path = path;
		this.mimeType = mimeType;
		this.size = size;
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
	
	public String getTitle(){
		return title;
	}

	@Override
	public String getMetadataString() {
		return Utils.createMetadataForPhoto(id, title, path, mimeType, size);
	}
}
