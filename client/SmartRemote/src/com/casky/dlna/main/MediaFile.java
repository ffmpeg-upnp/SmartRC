/**
 * 
 */
package com.casky.dlna.main;


/** 
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�MediaFile  
 * �������� Media�ļ�������
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-9-17 ����10:15:49
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-9-17 ����10:15:49
 * �汾�� 1.0    
 */
public class MediaFile implements Comparable<MediaFile>{

	private MediaFileMetaData metaData = null;
	private MediaFileSelector selector = null;
	private int position = 0;
	
	/**
	 * MediaFile���췽��
	 */
	public MediaFile(MediaFileMetaData metaData,int position) {
		this.metaData = metaData;
		this.position = position;
		selector = new MediaFileSelector(this);
	}
	
	public MediaFileMetaData getMetaData(){
		return metaData;
	}
	
	public int getPosition(){
		return position;
	}
	
	public MediaFileSelector getMediaFileSelector(){
		return selector;
	}
	
	@Override
	public int compareTo(MediaFile arg0) {
		MediaFile mf = arg0;
		return Integer.parseInt(mf.getMetaData().getId()) - Integer.parseInt(this.getMetaData().getId());
	}
}
