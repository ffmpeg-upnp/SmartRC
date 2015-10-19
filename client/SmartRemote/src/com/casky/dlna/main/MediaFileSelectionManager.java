/**
 * 
 */
package com.casky.dlna.main;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.List;

/** 
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�MediaFileManager  
 * �������� ����MediaFile�ļ�
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-9-17 ����10:36:20
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-9-17 ����10:36:20
 * �汾�� 1.0    
 */
public abstract class MediaFileSelectionManager {
	
	public static final int TYPE_PICTURE = 0x00;
	public static final int TYPE_VIDEO = 0x01;
	public static final int TYPE_MUSIC = 0x02;

    public static final Uri URI_PICTURE = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final Uri URI_VIDEO = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    public static final Uri URI_MUSIC = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    public static final String ID_PICTURE = MediaStore.Images.Media._ID;
    public static final String ID_VIDEO = MediaStore.Video.Media._ID;
    public static final String ID_MUSIC = MediaStore.Audio.Media._ID;
	
	/**
	* ������������ʼ��ѡ����ʾ����itemѡ��ť
	 */
	public abstract void startMultipleSelect();
	
	/**
	* ����������ȡ���ѡ����������itemѡ��ť
	 */
	public abstract void cancelMultipleSelect();
	
	/**
	* ����������ѡ��ȫ��
	 */
	public abstract void selectAll();
	
	/**
	* ����������ȫ��ȡ��ѡ��
	 */
	public abstract void unSelectAll();
	
	/**
	* �����������Ƿ���ȫѡ 
	* @return true:��ȫѡ,false:δȫѡ
	 */
	public abstract boolean isAllSelected();
	
	/**
	* �����������趨ȫѡ��־ 
	* @param allSelected
	 */
	public abstract void setIsAllSelected(boolean allSelected);
	
	/**
	* �����������Ƿ����ڽ��ж�ѡ 
	* @return true:��ѡ��ʼ,false:��ѡȡ��
	 */
	public abstract boolean isMultipleSelect();
	
	public abstract List<? extends MediaFile> getAllFiles();
	
	/**
	* ������������ȡѡ����ļ� 
	* @return MediaFile
	 */
	public abstract List<? extends MediaFile> getSelectFiles();
	
	/**
	* �������������ý���ļ����� 
	* @return TYPE_PICTURE ͼƬ;
	* 		TYPE_VIDEO = ��Ƶ;
	* 		TYPE_MUSIC = ��Ƶ;
	 */
	public abstract int getMediaFileType();
	
	public abstract void addMediaFile(int position,MediaFile mediaFile);
	
	public abstract MediaFile getMediaFile(int position);
	
	public abstract void removeMediaFile(int position);
	
	public abstract void addSelectedMediaFile(int position,MediaFile mediaFile);
	
	public abstract void removeSelectedMediaFile(int position);
	
	public abstract int getMediaFileCount();
	
	public abstract int getSelectedMediaFileCount();

    public abstract  Uri getUriInMediaStore();

    public abstract  String getIDStringInMediaStore();

    public abstract  void deleteFile(ContentResolver resolver);
}
