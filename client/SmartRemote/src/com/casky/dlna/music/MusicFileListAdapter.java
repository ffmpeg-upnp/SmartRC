package com.casky.dlna.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.casky.dlna.utils.BitmapLruCache;
import com.casky.smartremote.R;
import com.casky.dlna.main.MediaFileSelectionManager;
import com.casky.dlna.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 项目名称：Smart_DLNA
 * 类名称：MusicListAdapter
 * 类描述：
 * 创建人：shaojiansong
 * 创建时间＄1�714-9-25 下午3:53:02
 * 修改人：shaojiansong
 * 修改时间＄1�714-9-25 下午3:53:02
 * 修改备注＄1�7
 * 版本＄1�7 1.0
 */
public class MusicFileListAdapter extends CursorAdapter implements OnScrollListener, OnItemClickListener {
    private MainFragmentMusic mFrag;
    private MusicSelectionManager mSelectionManager;
    private Set<BitmapWorkerTask> taskCollection;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;
    private boolean isFirstEnter = true;
    private TextView musicFragTBCountTv = null;
    private Button musicFragTbCancelBtn = null;
    private Button musicFragTbSlcAllBtn = null;
    private ListView mListView = null;

    public MusicFileListAdapter(MainFragmentMusic mFrag,Cursor c,int flags,RelativeLayout musicFragTitleBar,ListView mListView) {
        super(mFrag.getActivity(), c, flags);
        this.mFrag = mFrag;
        this.mListView = mListView;
        musicFragTBCountTv = (TextView) musicFragTitleBar.findViewById(R.id.music_frag_tv_TitleText);
        musicFragTbCancelBtn = (Button) musicFragTitleBar.findViewById(R.id.music_frag_btn_cancel_mltslc);
        musicFragTbSlcAllBtn = (Button) musicFragTitleBar.findViewById(R.id.music_frag_btn_SelectAll);
        this.mSelectionManager = new MusicSelectionManager(this);

        taskCollection = new HashSet<BitmapWorkerTask>();
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        if(newCursor != null) {
            initSelectionManager(newCursor);
        }
        return super.swapCursor(newCursor);
    }

    /**
     * ������������ʼ��
     * @param cursor
     */
    private void initSelectionManager(Cursor cursor){


        cursor.moveToFirst();

        String title = null;
        String id = null;
        String name = null;
        String path = null;
        String mimeType = null;
        long size = 0;
        String artist = null;
        String album = null;
        long duration = 0;


        while (!cursor.isAfterLast()) {

            title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE));
            id = String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)));
            artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST));
            album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM));
            duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION));
            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA));
            size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE));
            mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.MIME_TYPE));

            MusicMetaData metadata = new MusicMetaData(id, name, path, title, artist, album, mimeType, size, duration);

            MusicFile musicFile = new MusicFile(metadata, cursor.getPosition());
            mSelectionManager.addMediaFile(cursor.getPosition(),musicFile);
            cursor.moveToNext();

        }
    }


    @Override
    public Object getItem(int position) {
        return mSelectionManager.getMediaFile(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mSelectionManager.getMediaFile(position).getMetaData().getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        String keyString = String.valueOf(position);
        if (convertView == null) {

            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mFrag.getActivity()).inflate(R.layout.dlna_music_files_list_item, null);
            mViewHolder.albumImage = (ImageView) convertView.findViewById(R.id.music_file_item_iv_albumImage);
            mViewHolder.musicArtist = (TextView) convertView.findViewById(R.id.music_file_item_tv_singer);
            mViewHolder.musicTitle = (TextView) convertView.findViewById(R.id.music_file_item_tv_title);
            mViewHolder.musicDuration = (TextView) convertView.findViewById(R.id.music_file_item_tv_duration);
            mViewHolder.multiSelect = (CheckBox) convertView.findViewById(R.id.music_file_item_cb_checkbox);
            convertView.setTag(mViewHolder);
        } else {

            mViewHolder = (ViewHolder) convertView.getTag();
        }

        MusicFile preloadMusic = mSelectionManager.getMediaFile(position);
        MusicMetaData metadata = preloadMusic.getMetaData();
        String title = metadata.getTitle();
        String creator = metadata.getArtist();
        String duration = String.valueOf(metadata.getDuration());

        mViewHolder.albumImage.setTag(keyString);
        mViewHolder.musicArtist.setText(creator);
        mViewHolder.musicTitle.setText(title);
        mViewHolder.musicDuration.setText(Utils.formatTime(Long.valueOf(duration)));

        if (mSelectionManager.isMultipleSelect()) {
            mViewHolder.multiSelect.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.multiSelect.setVisibility(View.INVISIBLE);
        }

        Bitmap bitmap = getBitmapFromMemoryCache(keyString);

        if (bitmap != null) {
            mViewHolder.albumImage.setImageBitmap(bitmap);
        } else {
            mViewHolder.albumImage.setImageResource(R.drawable.dlna_music_albumm);
        }

        if (mSelectionManager.isAllSelected()) {
            mViewHolder.multiSelect.setChecked(true);
        } else {
            mViewHolder.multiSelect.setChecked(mSelectionManager.getMediaFile(position).getMediaFileSelector().isSelected());
        }

//		if(mSelectionManager.getMediaFile(position).isPlaying()){
//			convertView.setBackgroundColor(Color.YELLOW);
//		}else{
//			convertView.setBackgroundColor(Color.WHITE);
//		}

        return convertView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    public class ViewHolder {
        public ImageView albumImage;
        public TextView musicTitle;
        public TextView musicDuration;
        public TextView musicArtist;
        public CheckBox multiSelect;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        MusicFile musicFile = null;
        musicFile = mSelectionManager.getMediaFile(position);
        CheckBox multiSelect = (CheckBox) arg1.findViewById(R.id.music_file_item_cb_checkbox);

        if (mSelectionManager.isMultipleSelect()) {
            musicFile.getMediaFileSelector().doSelect();
            mSelectionManager.setIsAllSelected(false);
            musicFragTbSlcAllBtn.setText(mFrag.getActivity().getString(R.string.dlna_title_bar_select_all));
            if (musicFile.getMediaFileSelector().isSelected()) {
                mSelectionManager.addSelectedMediaFile(position, musicFile);
            } else {
                mSelectionManager.removeSelectedMediaFile(position);
            }
            musicFragTBCountTv.setText(Utils.createTitleText(mSelectionManager.getSelectFiles().size()));
        } else {
            mFrag.startPlayer(mSelectionManager.getAllFiles(), position);
            mFrag.startPlayMusic();
        }
        multiSelect.setChecked(musicFile.getMediaFileSelector().isSelected());
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        BitmapLruCache.saveBitmapCache(bitmap,key);
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return BitmapLruCache.getBitmapCache(key);
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        String keyString;

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            keyString = params[0];
            long musicId = Integer.valueOf(params[1]).longValue();
            Bitmap bitmap = Utils.getArtworkFromFile(mFrag.getActivity(), musicId, -1);
            if (bitmap != null) {
                addBitmapToMemoryCache(keyString, bitmap);
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) mListView.findViewWithTag(keyString);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            taskCollection.remove(this);
        }

    }

    @Override
    public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        if (isFirstEnter && visibleItemCount > 0) {
            loadBitmaps(firstVisibleItem, visibleItemCount);
            isFirstEnter = false;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int scrollState) {
        // TODO Auto-generated method stub
        if (scrollState == SCROLL_STATE_IDLE) {

            loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
        } else {
            cancelAllTasks();
        }
    }

    private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            String keyString = String.valueOf(i);
            Bitmap bitmap = getBitmapFromMemoryCache(keyString);
            if (bitmap == null) {
                BitmapWorkerTask task = new BitmapWorkerTask();
                taskCollection.add(task);
                task.execute(keyString, mSelectionManager.getMediaFile(i).getMetaData().getId());
            } else {
                ImageView imageView = (ImageView) mListView.findViewWithTag(keyString);
                if (imageView != null && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public void cancelAllTasks() {
        if (taskCollection != null) {
            for (BitmapWorkerTask task : taskCollection) {
                task.cancel(false);
            }
        }
    }

    public MediaFileSelectionManager getMusicFileSelectionManager() {

        return mSelectionManager;
    }


}
