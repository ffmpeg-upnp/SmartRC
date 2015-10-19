/**
 * 
 */
package com.casky.dlna.picture.sub;

import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.casky.dlna.music.MainFragmentMusic;
import com.casky.dlna.utils.FileUtil;
import com.casky.smartremote.R;
import com.casky.dlna.control.DlnaCommandManager.LocalPlayCallback;
import com.casky.dlna.control.DlnaCommandManager.LocalStopCallback;
import com.casky.dlna.control.DlnaManager;
import com.casky.dlna.main.DLNAFragmentFactory;
import com.casky.dlna.main.MainFragmentDLNA;
import com.casky.main.slidingmenu.RemoteApplication;
import com.casky.dlna.main.RenderSelectCallBridge;
import com.casky.dlna.main.RenderSelectCallBridge.renderSelectCallBack;
import com.casky.dlna.picture.MainFragmentPicture;
import com.casky.dlna.picture.Picture;
import com.casky.dlna.picture.PictureMetaData;
import com.casky.dlna.picture.PictureSelectionManager;
import com.casky.dlna.picture.utils.AsyncImageLoader;
import com.casky.dlna.picture.utils.PictureFragmentSwitcher;
import com.casky.dlna.utils.BitmapCallbackImpl;
import com.casky.dlna.utils.UriBuilder;
import com.casky.dlna.utils.Utils;
import com.casky.dlna.view.RenderDialog;

import java.util.List;

/** 
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�Gallery  
 * �������� ͼƬ��ￄ1�7
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-10-11 ����3:50:50
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-10-11 ����3:50:50
 * �汾�� 1.0    
 */
public class GalleryFragment extends Fragment implements 
	OnTouchListener,renderSelectCallBack{
	
	public static final String TAG = "GalleryFragment";

	public static final int SOURCE_MAINFRAGPIC = 0x00;
	public static final int SOURCE_SUBFRAGPIC = 0x01;
	public static final int SOURCE_MAINFRAGPIC_SLC = 0x02;
	public static final int SOURCE_SUBFRAGPIC_SLC = 0x03;

    public static final String KEY_POSITION = "position";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_DIRECTORY = "directory";

	private static final int GESTURE_FLING_DISTANCE = 150;
	private int position = 0;
	private int source = -1;
	private PictureSelectionManager selectionManager = null;
	private List<Picture> sourceList = null;
	private AsyncImageLoader imageLoader = null;
	private Gallery imgItemList = null; 
	private RelativeLayout titleLL = null;
	private RelativeLayout menuLL = null;
	
	//���DlnaManagerʵ��
	private Application application = null;
	private DlnaManager mDlnaManager = null;

	//Render�б�ѡ��
	private RenderDialog mRenderDialog = null;
	
	private String id = null;
	private String metadataString = null;
	private Button pushBtn = null;
	private boolean shouldPush = false;
    private GalleryListAdapter adapter = null;
    private ImageView imgContent = null;
    private TextView emptyHintTv = null;
    private TextView imgCount = null;
    private LocalPlayCallbackImpl playCallback = null;
    private LocalStopCallbackImpl stopCallback = null;

    public static final int DLNA_PLAY = 0x01;
	public static final int DLNA_STOP = 0x02;
	
	private Handler galleryUIHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case DLNA_PLAY:
				pushBtn.setBackgroundResource(R.drawable.dlna_media_stop_normal);
                break;
			case DLNA_STOP:
				pushBtn.setBackgroundResource(R.drawable.dlna_multi_menu_push);
				break;
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
    			R.layout.dlna_picture_gallery, container, false);
        playCallback = new LocalPlayCallbackImpl();
        stopCallback = new LocalStopCallbackImpl();
		
		position = getArguments().getInt(KEY_POSITION);
		source = getArguments().getInt(KEY_SOURCE);
		String directory = getArguments().getString(KEY_DIRECTORY);
		
		application = getActivity().getApplication();
		mDlnaManager = ((RemoteApplication)application).getDlnaManager();
		RenderSelectCallBridge.getInstance().setRenderSelectCallBack(this);
		mRenderDialog = RenderDialog.getInstance(getActivity());
						
		MainFragmentPicture mainPicFrag = (MainFragmentPicture) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentMainPicture);;
		SubFragmentPicture subFrag = (SubFragmentPicture) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentSubPicture);
		
		
		switch(source){
			case SOURCE_MAINFRAGPIC:
				selectionManager = (PictureSelectionManager) mainPicFrag.getMediaFileSelectionManager();
				selectionManager.initDirectory(directory);
				sourceList = selectionManager.getAllFiles();
				shouldPush = false;
				break;
			case SOURCE_SUBFRAGPIC:
				selectionManager = (PictureSelectionManager) subFrag.getMediaFileSelectionManager();
				selectionManager.initDirectory(directory);
				sourceList = selectionManager.getAllFiles();
				shouldPush = false;
				break;
			case SOURCE_MAINFRAGPIC_SLC:
				selectionManager = (PictureSelectionManager) mainPicFrag.getMediaFileSelectionManager();
				selectionManager.initDirectory(directory);
				sourceList = selectionManager.getSelectFiles();
				shouldPush = true;
				break;
			case SOURCE_SUBFRAGPIC_SLC:
				selectionManager = (PictureSelectionManager) subFrag.getMediaFileSelectionManager();
				selectionManager.initDirectory(directory);
				sourceList = selectionManager.getSelectFiles();
				shouldPush = true;
				break;
		}

		imgContent = (ImageView) rootView.findViewById(R.id.pic_gallery_iv_img_content);
		imgCount = (TextView) rootView.findViewById(R.id.pic_gallery_tv_count);
		pushBtn = (Button) rootView.findViewById(R.id.pic_gallery_btn_menu_push);
		final Button btnBack = (Button) rootView.findViewById(R.id.pic_gallery_btn_back);
        final Button deleteBtn = (Button)rootView.findViewById(R.id.pic_gallery_btn_menu_delete);
        final Button shareBtn = (Button)rootView.findViewById(R.id.pic_gallery_btn_menu_share);
        emptyHintTv = (TextView) rootView.findViewById(R.id.pic_gallery_tv_empty_hint);
		
		titleLL = (RelativeLayout) rootView.findViewById(R.id.pic_gallery_rl_title);
		menuLL = (RelativeLayout) rootView.findViewById(R.id.pic_gallery_rl_dlna_menu);
		
		imageLoader = new AsyncImageLoader();
		
		imgItemList = (Gallery) rootView.findViewById(R.id.pic_gallery_glr_item_list);
		imgItemList.setCallbackDuringFling(false);
        adapter = new GalleryListAdapter(this, sourceList);
		imgItemList.setAdapter(adapter);
		
		imgItemList.setOnItemSelectedListener(new OnItemSelectedListener() {
			View v = null;
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				position = arg2;
				imgContent.setTag(position);
				if(v!=null){
					v.setBackgroundColor(Color.BLACK);
				}
				
				if(arg1!=null){
					arg1.setBackgroundColor(Color.WHITE);
					v = arg1;
				}
				
				imageLoader.LoadBitmap(sourceList.get(arg2).getMetaData().getPath(), false,new BitmapCallbackImpl(imgContent,position));
				imgCount.setText((arg2+1) + "/" + sourceList.size());
				
				if(shouldPush){
					initPushData();
					doPush();
				}
//                Toast.makeText(getActivity(),"shouldPush="+shouldPush,Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		
		pushBtn.setOnTouchListener(this);
        deleteBtn.setOnTouchListener(this);
		imgContent.setOnTouchListener(this);
        btnBack.setOnTouchListener(this);
        shareBtn.setOnTouchListener(this);

		return rootView;
	}
	
	private void startPush(){
		shouldPush = true;
		initPushData();
		mRenderDialog.showRenderDialog();
	}

    private void stopPush(){
        shouldPush = false;
        mDlnaManager.getDlnaServiceManager().stop(new LocalStopCallbackImpl());
    }
	
	@Override
	public void onResume() {
		imgItemList.setSelection(position);
		super.onResume();
		
//		if(shouldPush){
//			startPush();
//		}
	}

	@Override
	public void doSelect() {
		doPush();
	}
	
	private void initPushData(){
		PictureMetaData metadata = (sourceList.get(position)).getMetaData();
		
		id = metadata.getId();
        metadataString = metadata.getMetadataString();
	}
	
	private void doPush(){
		mDlnaManager.getDlnaServiceManager().setPushMediaData(UriBuilder.MediaType.picture, id, metadataString);//�����ļ���id��Type
		mDlnaManager.getDlnaServiceManager().play(playCallback);//��ʼ����
	}

	public class LocalPlayCallbackImpl implements LocalPlayCallback{
		@Override
		public void playCallback() {
			galleryUIHandler.sendEmptyMessage(DLNA_PLAY);
		}
	}
	
	public class LocalStopCallbackImpl implements LocalStopCallback{
		@Override
		public void stopCallback() {
			galleryUIHandler.sendEmptyMessage(DLNA_STOP);
		}
	}
	
	public void switchToNext(){
		if(position == sourceList.size()-1){
			return;
		}
		imgItemList.setSelection(++position);
	}
	
	public void switchToPrevious(){
		if(position == 0){
			return;
		}
		imgItemList.setSelection(--position);
	}

	private int xStartPos = 0;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if(event.getAction() == MotionEvent.ACTION_DOWN &&
				v.getId() == R.id.pic_gallery_iv_img_content){
			xStartPos = (int) event.getX();
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP){
			Log.e(TAG,"onTouch : " +v.toString());
			switch(v.getId()){
			case R.id.pic_gallery_iv_img_content:
				int xEndPos = (int) event.getX();
				Log.e(TAG,"xStartPos = "+xStartPos+",xEndPos"+xEndPos);
				if(xEndPos - xStartPos > GESTURE_FLING_DISTANCE){
					switchToPrevious();
					break;
				}else if(xStartPos - xEndPos > GESTURE_FLING_DISTANCE){
					switchToNext();
					break;
				}
				if(titleLL.getVisibility() == View.VISIBLE){
					titleLL.setVisibility(View.INVISIBLE);
					imgItemList.setVisibility(View.INVISIBLE);
					menuLL.setVisibility(View.GONE);
				}else{
					titleLL.setVisibility(View.VISIBLE);
					imgItemList.setVisibility(View.VISIBLE);
					menuLL.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.pic_gallery_btn_menu_push:
                if(mDlnaManager != null &&
                        mDlnaManager.getDlnaServiceManager() != null &&
                        !mDlnaManager.getDlnaServiceManager().isDLNAPlaying()){
                    startPush();
                }else{
                    stopPush();
                }
				break;
                case R.id.pic_gallery_btn_menu_delete:
                    if(position >= 0 && sourceList.size()>=0) {
                        getActivity().getContentResolver().delete(
                                selectionManager.getUriInMediaStore(),
                                selectionManager.getIDStringInMediaStore() + "=" + selectionManager.getMediaFile(position).getMetaData().getId(),
                                null);

                        sourceList.remove(position);
                        imgItemList.setSelection(position-1);
                        adapter.notifyDataSetChanged();

                        if( sourceList.size() == 0) {
                            emptyHintTv.setVisibility(View.VISIBLE);
                            imgContent.setVisibility(View.GONE);
                            imgCount.setText((position) + "/" + sourceList.size());
                            break;
                        }else {
                            if(position == sourceList.size()){
                                switchToPrevious();
                            }
                            imgCount.setText((position+1) + "/" + sourceList.size());
                            imgContent.setTag(position);
                            imageLoader.LoadBitmap(sourceList.get(position).getMetaData().getPath(), false, new BitmapCallbackImpl(imgContent, position));
                        }
                    }
                    break;
			case R.id.pic_gallery_btn_back:
				MainFragmentDLNA dlnaFrag = 
					(MainFragmentDLNA) DLNAFragmentFactory
					.getFragInstance(DLNAFragmentFactory.FragmentMainDLNA);	
				SubFragmentPicture subFrag = 
						(SubFragmentPicture) DLNAFragmentFactory
						.getFragInstance(DLNAFragmentFactory.FragmentSubPicture);
				
					if(PictureFragmentSwitcher.LastFragment == PictureFragmentSwitcher.FragmentDLNA){
						new PictureFragmentSwitcher(getFragmentManager()).switchFragment(
								GalleryFragment.this,
								dlnaFrag);
					}else if(PictureFragmentSwitcher.LastFragment == PictureFragmentSwitcher.FragmentSubPicture){
						new PictureFragmentSwitcher(getFragmentManager()).switchFragment(
								GalleryFragment.this,
								subFrag);
					}
				break;
                case R.id.pic_gallery_btn_menu_share:
                    FileUtil.shareSingleMediaFile(getActivity(),sourceList.get(position));
                break;
			}

			xStartPos = 0;
		}
		
		return false;
	}
}
