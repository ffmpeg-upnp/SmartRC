package com.casky.dlna.video;

import android.content.ContentResolver;
import android.net.Uri;
import android.widget.BaseAdapter;

import com.casky.dlna.main.MediaFile;
import com.casky.dlna.main.MediaFileSelectionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class VideoSelectionManger extends MediaFileSelectionManager{
	
	private static final String TAG = "VideoSelectionManger";
	private HashMap<Integer,Video> videoMap = null;
	private HashMap<Integer,Video> selectedVideo = null;
	private BaseAdapter videoListAdapter = null;
	private boolean selectAll = false;
	private boolean multiselect = false;
	
	VideoSelectionManger(BaseAdapter videoListAdapter) {
		this.videoListAdapter = videoListAdapter;
		this.videoMap = new HashMap<Integer, Video>();
		selectedVideo = new HashMap<Integer, Video>();
	}

	@Override
	public void startMultipleSelect() {
		multiselect = true;
		videoListAdapter.notifyDataSetChanged();
	}

	@Override
	public void cancelMultipleSelect() {
		// TODO Auto-generated method stub
		unSelectAll();
		multiselect = false;
		videoListAdapter.notifyDataSetChanged();;
	}
	
	@Override
	public void setIsAllSelected(boolean allSelected) {
		selectAll = allSelected;
	}

	@Override
	public void selectAll() {
		// TODO Auto-generated method stub
		selectAll = true;
		for(int i = 0; i< videoMap.size(); i++){			
			videoMap.get(i).getMediaFileSelector().doSelect(true);
			selectedVideo.put(i,videoMap.get(i));
		}
		videoListAdapter.notifyDataSetChanged();;
	}

	@Override
	public void unSelectAll() {
		// TODO Auto-generated method stub
		selectAll = false;
		for(int i = 0; i< videoMap.size(); i++){			
			videoMap.get(i).getMediaFileSelector().doSelect(false);
		}
		selectedVideo.clear();
		videoListAdapter.notifyDataSetChanged();;
	}

	@Override
	public boolean isAllSelected() {
		return selectAll;
	}

	@Override
	public boolean isMultipleSelect() {
		// TODO Auto-generated method stub
		return multiselect;
	}
	
	@Override
	public List<Video> getAllFiles() {
//		Iterator<Entry<Integer, Video>> i = videoMap.entrySet().iterator();
		List<Video> allVideoList = new ArrayList<Video>();
//		while(i.hasNext()){
//			musicList.add(i.next().getValue());
//		}
		for(int i=0;i<videoMap.size();i++){
			allVideoList.add(videoMap.get(i));
		}
		return allVideoList;
	}

	@Override
	public List<Video> getSelectFiles() {
		Iterator<Entry<Integer, Video>> i = selectedVideo.entrySet().iterator();
		List<Video> selectedVideoList = new ArrayList<Video>();
		while(i.hasNext()){
			selectedVideoList.add(i.next().getValue());
		}
		Collections.sort(selectedVideoList,Collections.reverseOrder());
		return selectedVideoList;
	}

	@Override
	public int getMediaFileType() {
		return MediaFileSelectionManager.TYPE_VIDEO;
	}

	@Override
	public void addMediaFile(int position, MediaFile mediaFile) {
		videoMap.put(position, (Video)mediaFile);
	}

	@Override
	public Video getMediaFile(int position) {
		return videoMap.get(position);
	}

	@Override
	public void removeMediaFile(int position) {
		videoMap.remove(position);
	}

	@Override
	public void addSelectedMediaFile(int position, MediaFile mediaFile) {
		selectedVideo.put(position, (Video)mediaFile);
	}

	@Override
	public void removeSelectedMediaFile(int position) {
		selectedVideo.remove(position);
	}

	@Override
	public int getMediaFileCount() {
		return videoMap.size();
	}

	@Override
	public int getSelectedMediaFileCount() {
		return selectedVideo.size();
	}

    @Override
    public Uri getUriInMediaStore() {
        return MediaFileSelectionManager.URI_VIDEO;
    }

    @Override
    public String getIDStringInMediaStore() {
        return MediaFileSelectionManager.ID_VIDEO;
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
        selectedVideo.clear();
    }

}
