

package com.casky.dlna.content;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.casky.dlna.server.ContentDirectoryService;

import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.ImageItem;
import org.seamless.util.MimeType;

import java.util.List;

/**
 * 
*    
* 项目名称：Smart_DLNA   
* 类名称：ImageContainer   
* 类描述：   
* 创建人：shaojiansong   
* 创建时间：2014-9-23 下午1:53:45   
* 修改人：shaojiansong   
* 修改时间：2014-9-23 下午1:53:45   
* 修改备注：   
* 版本： 1.0   
*
 */
public class ImageContainer extends DynamicContainer
{
	private static final String TAG = "ImageContainer";

	public ImageContainer(String id, String parentID, String title, String creator, String baseURL, Context ctx)
	{
		super(id, parentID, title, creator, baseURL, ctx, null, null);
		uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	}

	@Override
	public Integer getChildCount()
	{
		String[] columns = { MediaStore.Images.Media._ID };
		return ctx.getContentResolver().query(uri, columns, where, whereVal, orderBy).getCount();
	}

	@Override
	public List<Container> getContainers()
	{
		String[] columns = {
			MediaStore.Images.Media._ID,
			MediaStore.Images.Media.TITLE,
			MediaStore.Images.Media.DATA,
			MediaStore.Images.Media.MIME_TYPE,
			MediaStore.Images.Media.SIZE,
//			MediaStore.Images.Media.
//			MediaStore.Images.Media.WIDTH,
		};

		Cursor cursor = ctx.getContentResolver().query(uri, columns, where, whereVal, orderBy);
		if (cursor.moveToFirst())
		{
			do
			{
				String id = ContentDirectoryService.IMAGE_PREFIX + cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
				String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
				String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
				String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
				long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
//				long height = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
//				long width = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));

				String extension = "";
				int dot = filePath.lastIndexOf('.');
				if (dot >= 0)
					extension = filePath.substring(dot).toLowerCase();

				Res res = new Res(new MimeType(mimeType.substring(0, mimeType.indexOf('/')),
						mimeType.substring(mimeType.indexOf('/') + 1)), size, "http://" + baseURL + "/" + id + extension);
//				res.setResolution((int)width, (int)height);

				addItem(new ImageItem(id, parentID, title, "", res));

				Log.v(TAG, "Added image item " + title + " from " + filePath);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return containers;
	}

}
