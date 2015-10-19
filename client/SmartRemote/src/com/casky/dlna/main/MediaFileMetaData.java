package com.casky.dlna.main;


public interface MediaFileMetaData {
	
	public String getId();
	public String getPath();
	public String getMimeType();
	public long getSize();
	public String getMetadataString();
}
