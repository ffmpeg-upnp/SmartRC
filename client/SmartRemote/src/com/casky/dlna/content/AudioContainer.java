package com.casky.dlna.content;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.casky.dlna.server.ContentDirectoryService;

import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.seamless.util.MimeType;

import java.util.List;

/**
 * 
*    
* 项目名称：Smart_DLNA   
* 类名称：AudioContainer   
* 类描述：   
* 创建人：shaojiansong   
* 创建时间：2014-9-23 下午1:52:44   
* 修改人：shaojiansong   
* 修改时间：2014-9-23 下午1:52:44   
* 修改备注：   
* 版本： 1.0   
*
 */
public class AudioContainer extends DynamicContainer
{
	private static final String TAG = "AudioContainer";

	public AudioContainer(String id, String parentID, String title, String creator, String baseURL, Context ctx,
	                      String artist, String albumId)
	{
		super(id, parentID, title, creator, baseURL, ctx, null, null);
		uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

		if(artist!=null)
		{
			where = MediaStore.Audio.Media.ARTIST + "=?";
			whereVal = new String[]{artist};
			orderBy = MediaStore.Audio.Media.ALBUM;
		}

		if(albumId!=null)
		{
			where = MediaStore.Audio.Media.ALBUM_ID + "=?";
			whereVal = new String[]{albumId};
			orderBy = MediaStore.Audio.Media.TRACK;
		}
	}

	@Override
	public Integer getChildCount()
	{
		String[] columns = { MediaStore.Audio.Media._ID };
		return ctx.getContentResolver().query(uri, columns, where, whereVal, orderBy).getCount();
	}

	@Override
	public List<Container> getContainers()
	{
		String[] columns = {
			MediaStore.Audio.Media._ID,
			MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.Media.MIME_TYPE,
			MediaStore.Audio.Media.SIZE,
			MediaStore.Audio.Media.DURATION,
			MediaStore.Audio.Media.ALBUM
		};

		Cursor cursor = ctx.getContentResolver().query(uri, columns, where, whereVal, orderBy);
		if (cursor.moveToFirst())
		{
			do
			{
				String id = ContentDirectoryService.AUDIO_PREFIX + cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
				String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
				String creator = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
				String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
				long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
				long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
				String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));

				String extension = "";
				int dot = filePath.lastIndexOf('.');
				if (dot >= 0)
					extension = filePath.substring(dot).toLowerCase();

				Res res = new Res(new MimeType(mimeType.substring(0, mimeType.indexOf('/')),
						mimeType.substring(mimeType.indexOf('/') + 1)), size, "http://" + baseURL + "/" + id + extension);

				res.setDuration(duration / (1000 * 60 * 60) + ":"
						+ (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
						+ (duration % (1000 * 60)) / 1000);

				addItem(new MusicTrack(id, parentID, title, creator, album, new PersonWithRole(creator, "Performer"), res));

				Log.v(TAG, "Added audio item " + title + " from " + filePath);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return containers;
	}

}
