package com.casky.dlna.content;

import com.casky.dlna.server.ContentDirectoryService;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.WriteStatus;
import org.fourthline.cling.support.model.container.Container;

/**
 * 
*    
* 项目名称：Smart_DLNA   
* 类名称：CustomContainer   
* 类描述：   
* 创建人：shaojiansong   
* 创建时间：2014-9-23 下午1:52:55   
* 修改人：shaojiansong   
* 修改时间：2014-9-23 下午1:52:55   
* 修改备注：   
* 版本： 1.0   
*
 */
public class CustomContainer extends Container
{
	protected String baseURL = null;

	public CustomContainer(String id, String parentID, String title, String creator, String baseURL)
	{
		this.setClazz(new DIDLObject.Class("object.container"));

		if(parentID==null || parentID.compareTo(""+ContentDirectoryService.ROOT_ID)==0)
			setId(id);
		else if(id==null)
			setId(parentID);
		else
			setId(parentID + ContentDirectoryService.SEPARATOR + id);

		setParentID(parentID);
		setTitle(title);
		setCreator(creator);
		setRestricted(true);
		setSearchable(true);
		setWriteStatus(WriteStatus.NOT_WRITABLE);
		setChildCount(0);

		this.baseURL = baseURL;
	}
}
