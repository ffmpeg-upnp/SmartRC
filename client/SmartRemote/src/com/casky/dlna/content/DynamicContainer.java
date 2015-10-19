package com.casky.dlna.content;

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import org.fourthline.cling.support.model.container.Container;

import java.util.List;

/**
 * 
*    
* 项目名称：Smart_DLNA   
* 类名称：DynamicContainer   
* 类描述：   
* 创建人：shaojiansong   
* 创建时间：2014-9-23 下午1:53:02   
* 修改人：shaojiansong   
* 修改时间：2014-9-23 下午1:53:02   
* 修改备注：   
* 版本： 1.0   
*
 */
public abstract class DynamicContainer extends CustomContainer
{
	protected Context ctx;
	protected BaseColumns mediaColumns;
	protected Uri uri;

	protected String where = null;
	protected String[] whereVal = null;
	protected String orderBy = null;

	public DynamicContainer(String id, String parentID, String title, String creator, String baseURL,
	                        Context ctx, MediaStore.MediaColumns mediaColumns, Uri uri)
	{
		super(id, parentID, title, creator, baseURL);
		this.uri = uri;
		this.mediaColumns = mediaColumns;
		this.ctx = ctx;
	}

	// Dynamic container should re-implement those

	@Override
	public abstract Integer getChildCount();

	@Override
	public abstract List<Container> getContainers();

}
