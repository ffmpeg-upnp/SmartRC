package com.casky.dlna.music;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

/**
* ��Ŀ��ƣ�Smart_DLNA
* ����ƣ�MusicLoaderCallbacks
* �������� ʵ���˼�����LoaderCallbacks�ӿڵ�
* �����ˣ�wangbo
* ����ʱ�䣺2014-9-12 ����10:26:45
* �޸��ˣ�wangbo
* �޸�ʱ�䣺2014-9-12 ����10:26:45
* �޸ı�ע��
* �汾�� 1.0
*
 */
public class MusicLoaderCallbacks implements LoaderCallbacks<Cursor> {

	public static final Uri Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public static final String Selection = MediaStore.Audio.Media.DURATION + ">?";
    public static final String[] SelectionArgs = new String[]{"10000"};

	public static final String[] STORE_MUSICS = {
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE
    };

	private CursorAdapter mAdapter = null;
	private Context mContext = null;

	public MusicLoaderCallbacks(Context mContext, CursorAdapter mAdapter) {
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
                STORE_MUSICS,
                Selection,
                SelectionArgs,
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
