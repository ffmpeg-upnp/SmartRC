package com.casky.dlna.music;

import android.content.ContentResolver;
import android.net.Uri;

import com.casky.dlna.main.MediaFile;
import com.casky.dlna.main.MediaFileSelectionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 项目名称：Smart_DLNA
 * 类名称：MusicSelectionManager
 * 类描述：
 * 创建人：shaojiansong
 * 创建时间＄1�714-9-25 下午3:54:49
 * 修改人：shaojiansong
 * 修改时间＄1�714-9-25 下午3:54:49
 * 修改备注＄1�7
 * 版本＄1�7 1.0
 */
public class MusicSelectionManager extends MediaFileSelectionManager {
    private MusicFileListAdapter mAdapter;
    private boolean isMultipleSelect = false;
    private boolean selectAll = false;
    private HashMap<Integer, MusicFile> mMusicMap = null;
    private static HashMap<Integer, MusicFile> mSelectMap = null;

    public MusicSelectionManager(MusicFileListAdapter adapter) {
        this.mAdapter = adapter;
        this.mMusicMap = new HashMap<Integer, MusicFile>();
        mSelectMap = new HashMap<Integer, MusicFile>();
    }

    @Override
    public void startMultipleSelect() {
        isMultipleSelect = true;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void cancelMultipleSelect() {
        isMultipleSelect = false;
        unSelectAll();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectAll() {
        Iterator<Entry<Integer, MusicFile>> i = mMusicMap.entrySet().iterator();
        int position = 0;
        MusicFile music = null;
        Entry<Integer, MusicFile> set;
        while (i.hasNext()) {
            set = i.next();
            position = set.getKey();
            music = set.getValue();
            music.getMediaFileSelector().doSelect(true);
            addSelectedMediaFile(position, music);
        }
        selectAll = true;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void unSelectAll() {
        Iterator<Entry<Integer, MusicFile>> i = mMusicMap.entrySet().iterator();
        while (i.hasNext()) {
            i.next().getValue().getMediaFileSelector().doSelect(false);
        }
        mSelectMap.clear();
        selectAll = false;
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean isAllSelected() {
        // TODO Auto-generated method stub
        return selectAll;
    }

    @Override
    public void setIsAllSelected(boolean allSelected) {
        selectAll = allSelected;
    }

    @Override
    public boolean isMultipleSelect() {
        // TODO Auto-generated method stub
        return isMultipleSelect;
    }

    @Override
    public List<MusicFile> getSelectFiles() {
        Iterator<Entry<Integer, MusicFile>> i = mSelectMap.entrySet().iterator();
        List<MusicFile> musicList = new ArrayList<MusicFile>();
        while (i.hasNext()) {
            musicList.add(i.next().getValue());
        }
        Collections.sort(musicList, Collections.reverseOrder());
        return musicList;
    }

    @Override
    public List<MusicFile> getAllFiles() {
        Iterator<Entry<Integer, MusicFile>> i = mMusicMap.entrySet().iterator();
        List<MusicFile> musicList = new ArrayList<MusicFile>();
        while (i.hasNext()) {
            musicList.add(i.next().getValue());
        }
        return musicList;
    }

    @Override
    public int getMediaFileType() {
        return MediaFileSelectionManager.TYPE_MUSIC;
    }

    @Override
    public void addMediaFile(int position, MediaFile musicFile) {
        mMusicMap.put(position, (MusicFile) musicFile);
    }

    @Override
    public MusicFile getMediaFile(int position) {
        return mMusicMap.get(position);
    }

    @Override
    public void removeMediaFile(int position) {
        mMusicMap.remove(position);
    }

    @Override
    public void addSelectedMediaFile(int position, MediaFile musicFile) {
        mSelectMap.put(position, (MusicFile) musicFile);
    }

    @Override
    public void removeSelectedMediaFile(int position) {
        mSelectMap.remove(position);
    }

    @Override
    public int getMediaFileCount() {
        return mMusicMap.size();
    }


    @Override
    public int getSelectedMediaFileCount() {
        return mSelectMap.size();
    }

    @Override
    public Uri getUriInMediaStore() {
        return MediaFileSelectionManager.URI_MUSIC;
    }

    @Override
    public String getIDStringInMediaStore() {
        return MediaFileSelectionManager.ID_MUSIC;
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
        mSelectMap.clear();
    }
}

