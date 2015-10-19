package com.casky.dlna.picture;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.casky.smartremote.R;
import com.casky.dlna.main.DLNAFragmentFactory;
import com.casky.dlna.main.MainFragmentDLNA;
import com.casky.main.slidingmenu.RemoteApplication;
import com.casky.dlna.picture.sub.GalleryFragment;
import com.casky.dlna.picture.sub.SubFragmentPicture;
import com.casky.dlna.picture.utils.AsyncImageLoader;
import com.casky.dlna.picture.utils.PictureFragmentSwitcher;
import com.casky.dlna.utils.BitmapCallbackImpl;
import com.casky.dlna.utils.BitmapLruCache;
import com.casky.dlna.utils.Utils;

/**  
* ��Ŀ��ƣ�Smart_DLNA
* ����ƣ�AlbumCursorAdapter  
* �����������ڽ���ȡ����ͼƬ�󶨵�view�ϵ�CursorAdapter 
* �����ˣ�wangbo
* ����ʱ�䣺2014-9-12 ����1:50:27
* �޸��ˣ�wangbo
* �޸�ʱ�䣺2014-9-12 ����1:50:27
*
* �汾�� 1.0    
 */
public class PictureCursorAdapter extends CursorAdapter implements OnScrollListener,OnItemClickListener{

	public static final int flag = CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER;
	
	private static final String TAG = "PictureCursorAdapter";
	
	private LayoutInflater inflater = null;
	private AsyncImageLoader imageLoader = null;
	
	private PictureSelectionManager selectionManager = null;
	private PictureSelectionManager oldSelectionManager = null;
	private boolean shouldCloneSelectionManager = false;
	private static int BitmapPathIndex = -1;
	private static int BitmapIdIndex = -1;
	private static int bucketDisplayNameIndex = -1;
	private static int BitmapSizeIndex = -1;
	private static int BitmapMimeTypeIndex = -1;
	private static int BitmapTitleIndex = -1;
	private static int BitmapDirectoryIndex = -1;
	
	private String directory = null;
	
	private Fragment mFrag = null;
	private Button picSubTbSlcAllBtn = null;
	private TextView selectCountTV = null;
	
	//private boolean isFirstTime = true;
	private int scrFirst = 0;
	private int scrVisible = 0;
	private int firstVisibleItem = 0;
	private int visibleItemCount = 28;
	
	private int screenWidth = 0;
	private int screenHeight = 0;

	private Cursor mCursor = null;
	

	public PictureCursorAdapter(Fragment frag, Cursor c,Button picSubTbSlcAllBtn,TextView selectCountTV, int flags) {
		super(frag.getActivity(), c, flags);
		mFrag = frag;
		inflater = LayoutInflater.from(frag.getActivity());

		imageLoader = new AsyncImageLoader();
		
		if(frag instanceof MainFragmentDLNA ){
			loadApplicationShare();
			if(oldSelectionManager == null){
				shouldCloneSelectionManager = false;
			}else{
				//shouldCloneSelectionManager = true;
				shouldCloneSelectionManager = false;
			}
		}else{
			shouldCloneSelectionManager = false;
		}
		selectionManager = new PictureSelectionManager(this);
		this.selectCountTV = selectCountTV;
		this.picSubTbSlcAllBtn = picSubTbSlcAllBtn;
		getWindowMetric();
	}

    public void setDirectory(String dir){
        this.directory = dir;
        selectionManager.initDirectory(directory);
    }
	
	public void loadApplicationShare(){
		RemoteApplication share = (RemoteApplication) mFrag.getActivity().getApplication().getApplicationContext();
		oldSelectionManager = share.getPicManager();		
	}
	
	public void saveApplicationShare(){
		RemoteApplication share = (RemoteApplication) mFrag.getActivity().getApplication().getApplicationContext();
		share.setPicManager(selectionManager);
	}
	
	public void removeApplicationShare(){
		RemoteApplication share = (RemoteApplication) mFrag.getActivity().getApplication().getApplicationContext();
		share.removePicManager();
	}
	
	public String getDirectory(){
		return directory;
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor) {
		if(newCursor != null && mCursor != newCursor &&newCursor.getCount() != 0){
			initSelectionManager(newCursor);
			mCursor = newCursor;
		}
		return super.swapCursor(newCursor);
	}
	
	/**
	* ������������ʼ�� 
	* @param cursor
	 */
	private void initSelectionManager(Cursor cursor){

		if(BitmapPathIndex == -1){
			BitmapPathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		}
		
		if(BitmapIdIndex == -1){
			BitmapIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
		}
		
		if(bucketDisplayNameIndex == -1){
			bucketDisplayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
		}
		
		if(BitmapSizeIndex == -1){
			BitmapSizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
		}
		
		if(BitmapMimeTypeIndex == -1){
			BitmapMimeTypeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
		}
		
		if(BitmapTitleIndex == -1){
			BitmapTitleIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
		}
		
		if(BitmapDirectoryIndex == -1){
			BitmapDirectoryIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
		}
		
		cursor.moveToFirst();
		
//		directory = cursor.getString(BitmapDirectoryIndex);
//        Log.d(TAG,"initDirectory:"+directory);

	
		Picture pic = null;
		for(int i=0;i<cursor.getCount();i++){
			if(!shouldCloneSelectionManager){
				long id = cursor.getLong(BitmapIdIndex); 
				String path = cursor.getString(BitmapPathIndex);
				String title = cursor.getString(BitmapTitleIndex);
				String mimeType = cursor.getString(BitmapMimeTypeIndex);
				long size = cursor.getLong(BitmapSizeIndex);
				
				pic = new Picture(i,new PictureMetaData(String.valueOf(id),title,path,
					mimeType,size));
			}else{
				pic = oldSelectionManager.getMediaFile(i);
				selectionManager.setIsAllSelected(oldSelectionManager.isAllSelected());
				if(oldSelectionManager.isMultipleSelect()){
					selectionManager.startMultipleSelect();
				}
			}
	    	selectionManager.addMediaFile(i,pic);
	    	cursor.moveToNext();
		}
		
		saveApplicationShare();
	}
	


    /**
     * @see android.widget.ListAdapter#getView(int, android.view.View, android.view.ViewGroup)
     * ��д�����getView������ȡ��bindView����
     */
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        if(selectionManager.getDirectory() == null){
            selectionManager.initDirectory(getDirectory());
        }

        ViewHolder viewHolder = null;

        if(convertView == null){
        	convertView = newView(mContext, mCursor, parent);
        	viewHolder = new ViewHolder();
        	viewHolder.itemView = (ImageView) convertView.findViewById(R.id.pic_detail_iv_item);
        	viewHolder.itemchkBox = (CheckBox)convertView.findViewById(R.id.pic_detail_cb_item);
        	convertView.setTag(viewHolder);
        }else{
        	viewHolder = (ViewHolder) convertView.getTag();
        }


    	if(selectionManager.isMultipleSelect()){
    		viewHolder.itemchkBox.setVisibility(View.VISIBLE);
		}else{
			viewHolder.itemchkBox.setVisibility(View.INVISIBLE);
		}

    	viewHolder.itemView.setTag(position);

//        Log.d(TAG, "selectionManager:"+selectionManager.getMediaFileCount()+
//                ",position:"+position+
//                ",keyString:"+selectionManager.getKeyString(position));
        final PictureMetaData metadata = selectionManager.getMediaFile(position).getMetaData();
        final String bitmapPath = metadata.getPath();

        boolean state = readSelectionState(position,selectionManager);

		selectionManager.getMediaFile(position).getMediaFileSelector().doSelect(state);

		Bitmap bm = BitmapLruCache.getBitmapCache(bitmapPath);

		if(bm != null){
			viewHolder.itemView.setImageBitmap(bm);
		}else if(position < firstVisibleItem || position > firstVisibleItem + visibleItemCount){
        	viewHolder.itemView.setImageBitmap(bm);
        	return convertView;
        }else{
        	imageLoader.LoadBitmap(bitmapPath, true,new BitmapCallbackImpl(viewHolder.itemView,state,position));
        }
		changeSelectUi(viewHolder.itemView,viewHolder.itemchkBox,state);
        return convertView;
    }


	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		View view = inflater.inflate(R.layout.dlna_picture_detail_item, arg2,false);
		ImageView iv = (ImageView) view.findViewById(R.id.pic_detail_iv_item);
		CheckBox cb = (CheckBox) view.findViewById(R.id.pic_detail_cb_item);

		LayoutParams params = new LayoutParams(screenWidth/4-4, screenWidth/4-4);
		
		iv.setLayoutParams(params);
		if(selectionManager.isMultipleSelect()){
			cb.setVisibility(View.VISIBLE);
		}else{
			cb.setVisibility(View.INVISIBLE);
		}
		return view;
	}
		
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
			PictureCursorAdapter.this.firstVisibleItem = scrFirst;
			PictureCursorAdapter.this.visibleItemCount = scrVisible;
			notifyDataSetChanged();
		}else{
			//AsyncImageLoader.shutDownExecutorService();
		}
		
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		scrFirst = firstVisibleItem;
		scrVisible = visibleItemCount;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		String keyString = PictureSelectionManager.createKeyString(directory, position);
		Picture pic = selectionManager.getMediaFile(position);
		ImageView itemView = (ImageView)arg1.findViewById(R.id.pic_detail_iv_item);
		CheckBox itemchkBox = (CheckBox)arg1.findViewById(R.id.pic_detail_cb_item);
		if(selectionManager.isMultipleSelect()){
			selectionManager.setIsAllSelected(false);
			pic.getMediaFileSelector().doSelect();
			changeSelectUi(itemView,itemchkBox,pic.getMediaFileSelector().isSelected());
			picSubTbSlcAllBtn.setText(mFrag.getActivity().getString(R.string.dlna_title_bar_select_all));
			if(pic.getMediaFileSelector().isSelected()){
				selectionManager.addSelectedMediaFile(position,pic);
			}else{
				selectionManager.removeSelectedMediaFile(position);
			}
			selectCountTV.setText(Utils.createTitleText(selectionManager.getSelectFiles().size()));
		}else{
			GalleryFragment galleryFrag = (GalleryFragment) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentGallery);
            Bundle bundle = new Bundle();  
            bundle.putInt(GalleryFragment.KEY_POSITION, position);
            bundle.putString(GalleryFragment.KEY_DIRECTORY, directory);
            if(mFrag instanceof MainFragmentPicture){
            	bundle.putInt(GalleryFragment.KEY_SOURCE, GalleryFragment.SOURCE_MAINFRAGPIC);
            }else if(mFrag instanceof SubFragmentPicture){
            	bundle.putInt(GalleryFragment.KEY_SOURCE, GalleryFragment.SOURCE_SUBFRAGPIC);
            }
            
            galleryFrag.setArguments(bundle);  
			new PictureFragmentSwitcher(mFrag.getFragmentManager()).
			switchFragment(mFrag, galleryFrag);
		}
	}

	/**
	* ����������ǿ��ѡ��/ȡ��ѡ�� 
	* @param selecte true:ѡ�и�ý���ļ�,false:ȡ��ѡ�и�ý���ļ�
	 */
	public void changeSelectUi(ImageView thumbnail,CheckBox cb,boolean selecte) {
	
		cb.setChecked(selecte);
		
		Drawable tmbDrable = null;
		
		if(thumbnail != null){
			tmbDrable = thumbnail.getDrawable();
		}
		
		if(tmbDrable == null){
			return;
		}
		
		if(selecte){
			thumbnail.setBackgroundColor(Color.BLACK);
			tmbDrable.setAlpha(100);
		}else{
			thumbnail.setBackgroundColor(Color.GRAY);
			tmbDrable.setAlpha(255);
		}
		
		cb.setChecked(selecte);
		
	}


	/**
	 * ������getView����
	 */
	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {

	}

	 private void getWindowMetric(){
		WindowManager wm = (WindowManager) mFrag.getActivity().getSystemService(Context.WINDOW_SERVICE);
		screenWidth = wm.getDefaultDisplay().getWidth();
		screenHeight = wm.getDefaultDisplay().getHeight();
	 }

	
	/**
	* ������������ݱ����check״̬���checkboxͼƬ������״̬ 
	* @param position
	* @return
	 */
	private boolean readSelectionState(int position,PictureSelectionManager selectionManager){
		boolean oldPicutureCheckedState = false;
		if(selectionManager.isAllSelected()){
			oldPicutureCheckedState = true;
		}else{
			oldPicutureCheckedState = selectionManager.getMediaFile(position).getMediaFileSelector().isSelected();
		}
        return oldPicutureCheckedState;
	}
	
	/**
	* ���������� ����selectionManager
	* @return
	 */
	public PictureSelectionManager getSelectionManager(){
		return selectionManager;
	}

}
