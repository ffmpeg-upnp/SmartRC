package com.casky.dlna.picture;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;

import com.casky.dlna.main.MediaFile;
import com.casky.dlna.main.MediaFileSelectionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
* ��Ŀ��ƣ�Smart_DLNA
* ����ƣ�PictureSelectionManager  
* ������������MainActivity����PictureFragment�µ�item�Ķ�ѡ 
* �����ˣ�wangbo
* ����ʱ�䣺2014-9-19 ����2:45:25
* �޸��ˣ�wangbo
* �޸�ʱ�䣺2014-9-19 ����2:45:25
* �汾�� 1.0
 */
public class PictureSelectionManager extends MediaFileSelectionManager {

    private static final String TAG = "PictureSelectionManager";

	private HashMap<String,Picture> pictureMap = null;
	private HashMap<String,Picture> checkedPicture = null;
	private boolean isMultipleSelect = false;
	private boolean selectAll = false;
	private CursorAdapter adapter = null;
	
	private String directory = null;
	
	public PictureSelectionManager(CursorAdapter adapter){
		this.adapter = adapter;
		pictureMap = new HashMap<String, Picture>();
		checkedPicture = new HashMap<String,Picture>();
	}
	
	public void initDirectory(String directory){
        if(directory!=null)
		this.directory = directory;
	}

    public String getDirectory(){
        return directory;
    }

    public String getKeyString(int position){
        return createKeyString(directory, position);
    }

	@Override
	public void startMultipleSelect() {
		isMultipleSelect = true;
		adapter.notifyDataSetChanged();
	}

	@Override
	public void cancelMultipleSelect() {
		isMultipleSelect = false;
		unSelectAll();
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void selectAll() {
		String keyString = null;
		for(int i = 0; i< pictureMap.size(); i++){
			keyString = createKeyString(directory, i);
			pictureMap.get(keyString).getMediaFileSelector().doSelect(true);
			checkedPicture.put(keyString,pictureMap.get(keyString));
		}
		selectAll = true;
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void unSelectAll() {
		Iterator<Entry<String, Picture>> i = pictureMap.entrySet().iterator();
		while(i.hasNext()){
			i.next().getValue().getMediaFileSelector().doSelect(false);
		}
		cleanSelectedPicture();
		selectAll = false;
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean isAllSelected() {
		return selectAll;
	}
	
	@Override
	public void setIsAllSelected(boolean allSelected) {
		selectAll = allSelected;
	}
	
	@Override
	public boolean isMultipleSelect() {
		return isMultipleSelect;
	}

	@Override
	public List<Picture> getSelectFiles() {
		ArrayList<Picture> pictureList = new ArrayList<Picture>();
		Iterator<Entry<String, Picture>> i = checkedPicture.entrySet().iterator();
		while(i.hasNext()){
			pictureList.add((Picture) i.next().getValue());
			Collections.sort(pictureList);
		}
		return pictureList;
	}
	

	@Override
	public List<Picture> getAllFiles() {
		List<Picture> pictureList = new ArrayList<Picture>();
		for(int i=0;i<pictureMap.size();i++){
			String keyStr = createKeyString(directory, i);
			pictureList.add(pictureMap.get(keyStr));
		}
		return pictureList;
	}
	
	@Override
	public int getMediaFileType() {
		return MediaFileSelectionManager.TYPE_PICTURE;
	}

	@Override
	public void addMediaFile(int position,MediaFile p){
		String keyString = createKeyString(directory,position);
		pictureMap.put(keyString,(Picture)p);
	}
	
	@Override
	public void removeMediaFile(int position){
		String keyString = createKeyString(directory,position);
		pictureMap.remove(keyString);
	}
	
	@Override
	public Picture getMediaFile(int position){
		String keyString = createKeyString(directory,position);
		return pictureMap.get(keyString);
	}
	
	@Override
	public void addSelectedMediaFile(int position,MediaFile p){
		String keyString = createKeyString(directory,position);
		checkedPicture.put(keyString,(Picture)p);
	}
	
	@Override
	public void removeSelectedMediaFile(int position){
		String keyString = createKeyString(directory,position);
        Log.d(TAG,"remove:"+checkedPicture.get(keyString));
        checkedPicture.remove(keyString);
	}
	
	
	public void cleanSelectedPicture(){
		checkedPicture.clear();
	}
	
	public HashMap<String,Picture> getPictureMap(){
		return pictureMap;
	}
	
	public static String createKeyString(String directory,int position){
		return directory + "," + position;
	}

	@Override
	public int getMediaFileCount() {
		return pictureMap.size();
	}

	@Override
	public int getSelectedMediaFileCount() {
		return checkedPicture.size();
	}

    @Override
    public Uri getUriInMediaStore() {
        return MediaFileSelectionManager.URI_PICTURE;
    }

    @Override
    public String getIDStringInMediaStore() {
        return MediaFileSelectionManager.ID_PICTURE;
    }

    @Override
    public void deleteFile(ContentResolver resolver) {
        int count = getSelectedMediaFileCount();
        for(int i=0;i<count;i++) {
            resolver.delete(
                    this.getUriInMediaStore(),
                    this.getIDStringInMediaStore() + "=" + getSelectFiles().get(i).getMetaData().getId(),
                    null);
        }
        cleanSelectedPicture();
    }
}
