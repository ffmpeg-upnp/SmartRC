/**
 * 
 */
package com.casky.dlna.video;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.casky.smartremote.R;
import com.casky.dlna.utils.Utils;

import java.util.HashMap;
import java.util.List;

/** 
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�videoPlayListAdapter  
 * �������� 
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-10-28 ����3:09:10
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-10-28 ����3:09:10
 * �汾�� 1.0    
 */
public class VideoPlayListAdapter extends BaseAdapter implements OnItemClickListener{

    public static final String TAG = "VideoPlayListAdapter";
	private List<Video> videoPlayList = null;
	private VideoPlayWindow mActivity = null;
	private LayoutInflater inflater = null;
    private HashMap<Integer,View> mData = null;
    private int curPlayingPosition = 0;
	
	public VideoPlayListAdapter(VideoPlayWindow mActivity,List<Video> videoPlayList){
		this.mActivity = mActivity;
		this.videoPlayList = videoPlayList;
		this.inflater = LayoutInflater.from(mActivity);
        mData = new HashMap<Integer, View>();
	}

    public void setCurPlayingPosition(int position){
        this.curPlayingPosition = position;
    }
	
	@Override
	public int getCount() {
		return videoPlayList.size();
	}

	@Override
	public Object getItem(int arg0) {
        Log.d(TAG, "getItem@" + arg0 + "=" + ((TextView) mData.get(arg0).findViewById(R.id.video_play_item_tv_title)).getText());
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return Long.parseLong(videoPlayList.get(position).getMetaData().getId());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.dlna_video_play_list_item, null);
			mViewHolder =  new ViewHolder();
			mViewHolder.title = (TextView) convertView.findViewById(R.id.video_play_item_tv_title);
			mViewHolder.time = (TextView) convertView.findViewById(R.id.video_play_item_tv_time);
			convertView.setTag(mViewHolder);
		}else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		Video video = videoPlayList.get(position);
		VideoMetaData metadata = video.getMetaData();
		String title = metadata.getTitle();
		String time = String.valueOf(metadata.getDuration());

		mViewHolder.title.setText(title);
		mViewHolder.time.setText(Utils.formatTime(Long.valueOf(time)));
		
		if(position == curPlayingPosition){
            convertView.setBackgroundColor(mActivity.getResources().getColor(R.color.transparent_green));
		}else{
			convertView.setBackgroundColor(mActivity.getResources().getColor(R.color.transparent));
		}

        if(!mData.containsKey(position)){
            mData.put(position,convertView);
            Log.d(TAG,"put "+position+":"+title);
        }
		return convertView;
	}
	
	final class ViewHolder{
		TextView title;
		TextView time;
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		mActivity.setCurrentPlayIndex(position);
		mActivity.playVideo();
	}

}
