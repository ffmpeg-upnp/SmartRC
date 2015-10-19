package com.casky.dlna.server;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.casky.dlna.content.AudioContainer;
import com.casky.dlna.content.CustomContainer;
import com.casky.dlna.content.ImageContainer;
import com.casky.dlna.content.VideoContainer;

import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

/**
 * 
*    
* 项目名称：Smart_DLNA   
* 类名称：ContentDirectoryService   
* 类描述：   
* 创建人：shaojiansong   
* 创建时间：2014-9-23 下午1:55:47   
* 修改人：shaojiansong   
* 修改时间：2014-9-23 下午1:55:47   
* 修改备注：   
* 版本： 1.0   
*
 */
public class ContentDirectoryService extends AbstractContentDirectoryService
{
	private final static String TAG = "ContentDirectoryService";

	public final static char SEPARATOR = '$';

	// Type
	public final static int ROOT_ID   = 0;
	public final static int VIDEO_ID  = 1;
	public final static int AUDIO_ID  = 2;
	public final static int IMAGE_ID  = 3;

	// Test
	public final static String VIDEO_TXT  = "Videos";
	public final static String AUDIO_TXT  = "Music";
	public final static String IMAGE_TXT  = "Images";

	// Type subfolder
	public final static int ALL_ID    = 0;
	public final static int FOLDER_ID = 1;
	public final static int ARTIST_ID = 2;
	public final static int ALBUM_ID  = 3;

	// Prefix item
	public final static String VIDEO_PREFIX     = "v-";
	public final static String AUDIO_PREFIX     = "a-";
	public final static String IMAGE_PREFIX     = "i-";
	public final static String DIRECTORY_PREFIX = "d-";


	private static Context ctx;
	private static String baseURL;

	public ContentDirectoryService()
	{
		Log.e(TAG, "Call default constructor...");
	}

	public ContentDirectoryService(Context ctx, String baseURL)
	{
		this.ctx = ctx;
		this.baseURL = baseURL;
	}

	public void setContext(Context ctx)
	{
		this.ctx = ctx;
	}

	public void setBaseURL(String baseURL)
	{
		this.baseURL = baseURL;
	}

	@Override
	public BrowseResult browse(String objectID, BrowseFlag browseFlag,
			String filter, long firstResult, long maxResults,
			SortCriterion[] orderby) throws ContentDirectoryException
	{
		Log.d(TAG, "Will browse " + objectID);

		try
		{
			DIDLContent didl = new DIDLContent();
			TextUtils.StringSplitter ss = new TextUtils.SimpleStringSplitter(SEPARATOR);
			Log.d("syo", "ss is " + ss);
			ss.setString(objectID);
			Log.d("syo", "ss1 is " + ss);
			int type = -1;

			for (String s : ss)
			{
				Log.d("syo", "s is " + s);
				int i = Integer.parseInt(s);
				if(type==-1)
				{
					type = i;
					if(type!=ROOT_ID && type!=VIDEO_ID && type!=AUDIO_ID && type!=IMAGE_ID)
						throw new ContentDirectoryException(ContentDirectoryErrorCode.NO_SUCH_OBJECT, "Invalid type!");
				}
			}

			Container container = null;

			Log.d(TAG, "Browsing type " + type);

			Container rootContainer = new CustomContainer( "" + ROOT_ID, "" + ROOT_ID,
					"SmartRemote", "SmartRemote", baseURL);


			// Video
			Container allVideoContainer = new VideoContainer(""+ ALL_ID, "" + VIDEO_ID,
					VIDEO_TXT, "SmartRemote", baseURL, ctx);
			rootContainer.addContainer(allVideoContainer);
			rootContainer.setChildCount(rootContainer.getChildCount() + 1);
			
			// Audio
			Container allAudioContainer = new AudioContainer("" + ALL_ID, "" + AUDIO_ID,
					AUDIO_TXT, "SmartRemote", baseURL, ctx, null, null);
			rootContainer.addContainer(allAudioContainer);
			rootContainer.setChildCount(rootContainer.getChildCount() + 1);
			// Image
			Container allImageContainer = new ImageContainer( "" + ALL_ID, "" + IMAGE_ID, IMAGE_TXT,
					"SmartRemote", baseURL, ctx);
			rootContainer.addContainer(allImageContainer);
			rootContainer.setChildCount(rootContainer.getChildCount() + 1);

			if(type==ROOT_ID) 
			{	
				container = rootContainer;
			}
			else if(type==VIDEO_ID)
			{
				Log.d(TAG, "Listing all videos...");
				container = allVideoContainer;
			}
			else if(type==AUDIO_ID)
			{
				Log.d(TAG, "Listing all songs...");
				container = allAudioContainer;
			}
			else if(type==IMAGE_ID)
			{

					Log.d(TAG, "Listing all images...");
					container = allImageContainer;
			}

			if(container!=null)
			{
				Log.d(TAG, "List container...");

				// Get container first
				for(Container c : container.getContainers())
					didl.addContainer(c);

				Log.d(TAG, "List item...");

				// Then get item
				for(Item i : container.getItems())
					didl.addItem(i);

				Log.d(TAG, "Return result...");

				int count = container.getChildCount();
				Log.d(TAG, "Child count : " + count);
				String answer = "";
				try{
					answer = new DIDLParser().generate(didl);
				}
				catch (Exception ex)
				{
					throw new ContentDirectoryException(
					ContentDirectoryErrorCode.CANNOT_PROCESS, ex.toString());
				}
				Log.d(TAG, "answer : " + answer);

				return new BrowseResult(answer,count,count);
			}
		}
		catch (Exception ex)
		{
			throw new ContentDirectoryException(
				ContentDirectoryErrorCode.CANNOT_PROCESS, ex.toString());
		}

		Log.e(TAG, "No container for this ID !!!");
		throw new ContentDirectoryException(ContentDirectoryErrorCode.NO_SUCH_OBJECT);
	}
}
