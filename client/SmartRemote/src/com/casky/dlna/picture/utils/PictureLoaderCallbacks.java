package com.casky.dlna.picture.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

/** 
* ��Ŀ��ƣ�Smart_DLNA
* ����ƣ�PictureLoaderCallbacks  
* �������� ʵ���˼�����LoaderCallbacks�ӿڵ�
* �����ˣ�wangbo
* ����ʱ�䣺2014-9-12 ����10:26:45
* �޸��ˣ�wangbo
* �޸�ʱ�䣺2014-9-12 ����10:26:45
* �޸ı�ע��   
* �汾�� 1.0    
*
 */
public class PictureLoaderCallbacks implements LoaderCallbacks<Cursor> {
	
	public static final Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	public static final String SdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
	
	public static final String cameraSelection = 
			MediaStore.Images.Media.DATA + " like " + "?) or (" + 
			MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " like " + "?";
	public static final String[] cameraSelectionArgs = {SdCardPath + "DCIM%","Camera"};
	
	public static final String pictureListSelection = 
			MediaStore.Images.Media.DATA + " not like " + "?) and (" + 
					MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " not like " + "?";
	public static final String[] pictureListSelectionArgs = {SdCardPath + "DCIM%","Camera"};
	
	public static final String sortOrder = MediaStore.Images.Media._ID + " DESC";
	
	public static final String[] STORE_IMAGES = {
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.TITLE,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    };
	

	private CursorAdapter mAdapter = null;
	private Context mContext = null;
	private String selection = null;
	private String selectionArgs[] = null;
	
	public PictureLoaderCallbacks(Context mContext,CursorAdapter mAdapter,String selection,String[] selectionArgs) {
		this.mContext = mContext;
		this.mAdapter = mAdapter;
		this.selection = selection;
		this.selectionArgs = selectionArgs;
	}

	/**
	 * �����µļ�����
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		CursorLoader cursorLoader = new CursorLoader(
				mContext,
				imgUri,
				STORE_IMAGES,
				selection,
				selectionArgs,
				sortOrder);
		return cursorLoader;
	}

	/**
	 * �ڼ������������ʱ��Adapter��cursor�滻�������ݸ���
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		mAdapter.swapCursor(arg1);
		System.out.println("onLoadFinished " + arg1.getCount());
	}

	/**
	 * ��һ����ǰ�������ļ�����������ʱ�����ã�Ȼ��ʹ�������������Ч��
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

}
