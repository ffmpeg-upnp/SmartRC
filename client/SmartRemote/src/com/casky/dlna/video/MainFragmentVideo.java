package com.casky.dlna.video;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.casky.smartremote.R;
import com.casky.dlna.main.MainFragmentDLNA;
import com.casky.dlna.main.Manageable;
import com.casky.main.slidingmenu.RemoteApplication;
import com.casky.dlna.utils.Utils;

import java.util.HashMap;
import java.util.List;

public class MainFragmentVideo extends Fragment implements Manageable {
		
	private static final String MFV = "VideoFragment";

    public static final String KEY_POSITION = "listposition";
    public static final int flag = CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER;

	private ListView mVideoListView;
	private VideoFileListAdapter mVideoListViewAdapter;
	private HashMap<Integer, Video> videoMap;
	
	private RelativeLayout VideoFragTitleBar = null;
	private TextView VideoFragTBCountTv = null;
	private Button VideoFragTbCancelBtn = null;
	private Button VideoFragTbSlcAllBtn = null;
	
	public static int screenWidth;
	public static int screenHeight;
    private VideoLoaderCallbacks loaderCallbacks;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		ViewGroup rootView = (ViewGroup) inflater.inflate(
			R.layout.dlna_fragment_main_video, container, false);
		getWindowMetric();

		//ViewGroup rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dlna_fragment_main_video, null);
		
		VideoFragTitleBar = (RelativeLayout) rootView.findViewById(R.id.video_frag_rl_titleBar);
		VideoFragTBCountTv = (TextView) rootView.findViewById(R.id.video_frag_tv_TitleText);
		VideoFragTbCancelBtn = (Button) rootView.findViewById(R.id.video_frag_btn_cancel_mltslc);
		VideoFragTbSlcAllBtn = (Button) rootView.findViewById(R.id.video_frag_btn_SelectAll);
		
		mVideoListView = (ListView) rootView.findViewById(R.id.video_frag_lv_videoList);
		Log.v(MFV, "new VideoProvider");
        mVideoListViewAdapter = new VideoFileListAdapter(this,null,flag,VideoFragTBCountTv);
        loaderCallbacks = new VideoLoaderCallbacks(getActivity(),mVideoListViewAdapter);
        getLoaderManager().initLoader(
                0,
                null,
                loaderCallbacks);

        mVideoListView.setAdapter(mVideoListViewAdapter);
		mVideoListView.setOnItemClickListener(mVideoListViewAdapter);
		mVideoListView.setOnScrollListener(mVideoListViewAdapter);
		
        VideoFragTbCancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mVideoListViewAdapter.getVideoSelectionManager().cancelMultipleSelect();
				MainFragmentDLNA.changeSelectionUI(false);
			}

		});
        
        /**
         * ȫѡ��ť�ĵ���¼�
         */
        VideoFragTbSlcAllBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(!mVideoListViewAdapter.getVideoSelectionManager().isAllSelected()){
					mVideoListViewAdapter.getVideoSelectionManager().selectAll();
					VideoFragTbSlcAllBtn.setText(getActivity().getString(R.string.dlna_title_bar_unselect_all));
				}else{
					mVideoListViewAdapter.getVideoSelectionManager().unSelectAll();
					VideoFragTbSlcAllBtn.setText(getActivity().getString(R.string.dlna_title_bar_select_all));
				}
				VideoFragTBCountTv.setText(Utils.createTitleText(mVideoListViewAdapter.getVideoSelectionManager().getSelectFiles().size()));
			}
		});

		return rootView;
	}
	
	private void getWindowMetric(){
    	WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
    	screenWidth = wm.getDefaultDisplay().getWidth();
    	screenHeight = wm.getDefaultDisplay().getHeight();
    }
	
	public void startMediaPlayer(int position){
        Intent intent = new Intent();
        intent.setClass(getActivity(), VideoPlayWindow.class);
        List<Video> videoList = mVideoListViewAdapter.getVideoSelectionManager().getAllFiles();

        RemoteApplication share = (RemoteApplication) getActivity().getApplication().getApplicationContext();
		share.setVideoList(videoList);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        intent.putExtras(bundle);
        startActivity(intent);
//		 playWindow = new VideoPlayWindow(this,videoMap);
//		playWindow.showPopWindow();
	}
	
 
	@Override
	public VideoSelectionManger getMediaFileSelectionManager() {
		// TODO Auto-generated method stub
		return mVideoListViewAdapter.getVideoSelectionManager();
	}

	@Override
	public void showTitleBar() {
		VideoFragTBCountTv.setText(Utils.createTitleText(getMediaFileSelectionManager().getSelectFiles().size()));
		VideoFragTitleBar.setVisibility(View.VISIBLE);
		
	}

	@Override
	public void hideTitleBar() {
		VideoFragTitleBar.setVisibility(View.GONE);
	}

    @Override
    public void refreshTitleCount(){
        VideoFragTBCountTv.setText(Utils.createTitleText(getMediaFileSelectionManager().getSelectFiles().size()));
    }
}
