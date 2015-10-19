package com.casky.dlna.video;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.casky.dlna.picture.Picture;
import com.casky.smartremote.R;
import com.casky.dlna.main.MediaFile;
import com.casky.dlna.main.MediaFileSelector;
import com.casky.dlna.picture.utils.AsyncImageLoader;
import com.casky.dlna.utils.BitmapCallbackImpl;
import com.casky.dlna.utils.BitmapLruCache;
import com.casky.dlna.utils.Utils;

import java.util.HashMap;


public class VideoFileListAdapter extends CursorAdapter implements OnItemClickListener,OnScrollListener{
	private static final String TAG = "VListAdapter";
	private MainFragmentVideo mFrag = null;

	private LayoutInflater mLayoutInflater;
	private TextView videoFragTBCountTv = null;
	private int scrFirst = 0;
	private int scrVisible = 0;
	private int firstVisibleItem = 0;
	private int visibleItemCount = 16;
	private AsyncImageLoader imageLoader = null;

	private VideoSelectionManger mSelectManager = null;
	
	public VideoFileListAdapter(Fragment mFrag, Cursor c, int flags,TextView videoFragTBCountTv){
        super(mFrag.getActivity(), c, flags);
        mLayoutInflater = LayoutInflater.from(mFrag.getActivity());
		this.mFrag = (MainFragmentVideo) mFrag;
		this.videoFragTBCountTv = videoFragTBCountTv;
		mSelectManager =  new VideoSelectionManger(this);
		imageLoader = new AsyncImageLoader();
	}

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        if(newCursor != null){
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

        Video video = null;
        for(int i=0;i<cursor.getCount();i++){

            String id = String.valueOf(cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
            String title = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            String resolution = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));
            String artist = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
            String displayName = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
            String mimeType = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
            String path = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            long duration = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            long size = cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

            VideoMetaData videoMetadata = new VideoMetaData(id,title, displayName,path, artist, resolution, mimeType, size, duration);
            video = new Video(videoMetadata, cursor.getPosition());
            mSelectManager.addMediaFile(i,video);
            cursor.moveToNext();
        }
    }

	@Override
	public Object getItem(int position) {
		return position;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.dlna_video_file_list_item, null);
			holder.img = (ImageView)convertView.findViewById(R.id.video_item_iv_thumbnail);
			holder.title = (TextView)convertView.findViewById(R.id.video_item_tv_title);
			holder.time = (TextView)convertView.findViewById(R.id.video_item_tv_time);
			holder.checkBox = (CheckBox)convertView.findViewById(R.id.video_item_cb_select);			
			convertView.setTag(holder);
		}else {
			Log.v(TAG, "convertView:" + convertView);
			holder = (ViewHolder)convertView.getTag();
		}
		holder.img.setTag(position);
		if(mSelectManager.isMultipleSelect()){
			holder.checkBox.setVisibility(View.VISIBLE);
		}else{
			holder.checkBox.setVisibility(View.INVISIBLE);
		}
		
		MediaFile mf = mSelectManager.getMediaFile(position);
		MediaFileSelector mfs = mf.getMediaFileSelector();
		holder.checkBox.setChecked(mfs.isSelected());
		
		holder.title.setText(mSelectManager.getMediaFile(position).getMetaData().getTitle());
		long fileDuration = mSelectManager.getMediaFile(position).getMetaData().getDuration() / 1000;
		int hour = (int)(fileDuration / (60 * 60));
		int min = (int)((fileDuration - hour*60) / 60);
		int sec = (int)(fileDuration - hour*60*60 - min*60);
		holder.time.setText(hour+":"+min+":"+sec);

		String bitmapPath = mSelectManager.getMediaFile(position).getMetaData().getPath();
		Bitmap bm = BitmapLruCache.getBitmapCache(bitmapPath);
		
		if(bm != null){
			holder.img.setImageBitmap(bm);
		}else if(position < firstVisibleItem || position > firstVisibleItem + visibleItemCount){
			holder.img.setImageBitmap(bm);
        	return convertView;
        }else{
        	imageLoader.getVideoThumbnail(bitmapPath, 80,120,new BitmapCallbackImpl(holder.img,position));
        }
		
		return convertView;
	}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    @Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		CheckBox cb = (CheckBox)view.findViewById(R.id.video_item_cb_select);
		if(!getVideoSelectionManager().isMultipleSelect()){	
			mFrag.startMediaPlayer(position);
		}else{		
			if(!mSelectManager.getMediaFile(position).getMediaFileSelector().isSelected()){
				mSelectManager.getMediaFile(position).getMediaFileSelector().doSelect(true);
				mSelectManager.addSelectedMediaFile(position, mSelectManager.getMediaFile(position));
			}else{
				mSelectManager.getMediaFile(position).getMediaFileSelector().doSelect(false);
				mSelectManager.removeSelectedMediaFile(position);
			}
			videoFragTBCountTv.setText(Utils.createTitleText(mSelectManager.getSelectFiles().size()));
			cb.setChecked(mSelectManager.getMediaFile(position).getMediaFileSelector().isSelected());
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
			VideoFileListAdapter.this.firstVisibleItem = scrFirst;
			VideoFileListAdapter.this.visibleItemCount = scrVisible;
			notifyDataSetChanged();
		}
		
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		scrFirst = firstVisibleItem;
		scrVisible = visibleItemCount;
	}
	
	 
	public VideoSelectionManger getVideoSelectionManager(){
		return mSelectManager;
	}

	final class ViewHolder{
		ImageView img;
		TextView title;
		TextView time;
		CheckBox checkBox;
	}
}
