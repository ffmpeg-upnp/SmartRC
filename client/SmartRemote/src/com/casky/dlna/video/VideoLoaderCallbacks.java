package com.casky.dlna.video;

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
public class VideoLoaderCallbacks implements LoaderCallbacks<Cursor> {

	public static final Uri Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

	public static final String[] STORE_VIDEOS = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.RESOLUTION,
            MediaStore.Video.Media.ARTIST,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
    };

	private CursorAdapter mAdapter = null;
	private Context mContext = null;

	public VideoLoaderCallbacks(Context mContext, CursorAdapter mAdapter) {
		this.mContext = mContext;
		this.mAdapter = mAdapter;
	}

	/**
	 * �����µļ�����
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		CursorLoader cursorLoader = new CursorLoader(
				mContext,
				Uri,
                STORE_VIDEOS,
				null,
                null,
                null);
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
