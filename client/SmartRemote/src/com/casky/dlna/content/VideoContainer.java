package com.casky.dlna.content;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.casky.dlna.server.ContentDirectoryService;

import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.VideoItem;
import org.seamless.util.MimeType;

import java.util.List;

/**
 * 
*    
* 项目名称：Smart_DLNA   
* 类名称：VideoContainer   
* 类描述：   
* 创建人：shaojiansong   
* 创建时间：2014-9-23 下午1:53:59   
* 修改人：shaojiansong   
* 修改时间：2014-9-23 下午1:53:59   
* 修改备注：   
* 版本： 1.0   
*
 */
public class VideoContainer extends DynamicContainer
{
	private static final String TAG = "VideoContainer";

	public VideoContainer(String id, String parentID, String title, String creator, String baseURL, Context ctx)
	{
		super(id, parentID, title, creator, baseURL, ctx, null, null);
		uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	}

	@Override
	public Integer getChildCount()
	{
		String[] columns = { MediaStore.Video.Media._ID };
		return ctx.getContentResolver().query(uri, columns, where, whereVal, orderBy).getCount();
	}

	@Override
	public List<Container> getContainers()
	{
		String[] columns = {
			MediaStore.Video.Media._ID,
			MediaStore.Video.Media.TITLE,
			MediaStore.Video.Media.DATA,
			MediaStore.Video.Media.ARTIST,
			MediaStore.Video.Media.MIME_TYPE,
			MediaStore.Video.Media.SIZE,
			MediaStore.Video.Media.DURATION,
			MediaStore.Video.Media.RESOLUTION,
		};

		Cursor cursor = ctx.getContentResolver().query(uri, columns, where, whereVal, orderBy);
		if (cursor.moveToFirst())
		{
			do
			{
				String id = ContentDirectoryService.VIDEO_PREFIX + cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
				String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
				String creator = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
				String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
				String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
				long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
				long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
				String resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));

				String extension = "";
				int dot = filePath.lastIndexOf('.');
				if (dot >= 0)
					extension = filePath.substring(dot).toLowerCase();

				Res res = new Res(new MimeType(mimeType.substring(0, mimeType.indexOf('/')),
						mimeType.substring(mimeType.indexOf('/') + 1)), size, "http://" + baseURL + "/" + id + extension);
				res.setDuration(duration / (1000 * 60 * 60) + ":"
						+ (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
						+ (duration % (1000 * 60)) / 1000);
				res.setResolution(resolution);

				addItem(new VideoItem(id, parentID, title, creator, res));

				Log.v(TAG, "Added video item " + title + " from " + filePath);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return containers;
	}
}
