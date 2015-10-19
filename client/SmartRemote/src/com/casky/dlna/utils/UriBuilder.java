package com.casky.dlna.utils;

import com.casky.dlna.server.ContentDirectoryService;

public class UriBuilder {
	public static enum MediaType{
		music,picture,video;
	}
	public static String buildUriForType(MediaType type, String id){
		String urlString = "";
		switch (type) {
		case music:
			urlString = "http://" + Utils.getLocalIpAddress()+":8888/" + ContentDirectoryService.AUDIO_PREFIX + id + ".mp3";
			break;
		case picture:
			urlString = "http://" + Utils.getLocalIpAddress()+":8888/" + ContentDirectoryService.IMAGE_PREFIX + id + ".jpg";
			break;
		case video:
			urlString = "http://" + Utils.getLocalIpAddress()+":8888/" + ContentDirectoryService.VIDEO_PREFIX + id + ".avi";
			break;
		default:
			break;
		}
		return urlString;
	}

}
