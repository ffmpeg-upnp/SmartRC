package com.casky.dlna.picture.sub;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.casky.smartremote.R;
import com.casky.dlna.main.DLNAFragmentFactory;
import com.casky.dlna.picture.utils.AsyncImageLoader;
import com.casky.dlna.picture.utils.PictureFragmentSwitcher;
import com.casky.dlna.utils.BitmapCallbackImpl;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class AlbumListCursorAdapter extends CursorAdapter {

	public static int flag = CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER;
	
	private static final String TAG = "AlbumListCursorAdapter";
	
	private LayoutInflater inflater = null;
	private AsyncImageLoader imageLoader = null;
	private List<String> bucketDisplayNameList = null;
	private List<Dir> dirList = null;
	private String bitmapPath = null;
	private static int bucketDisplayNameIndex = -1;
	private Fragment mFrag = null;

	public AlbumListCursorAdapter(Activity activity,Fragment frag, Cursor c, int flags) {
		super(activity, c, flags);
		mFrag = frag;
		inflater = LayoutInflater.from(activity);

		imageLoader = new AsyncImageLoader();
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor) {
        Log.d(TAG,"swapCursor");
		if(newCursor != null){
			initSelectionManager(newCursor);
		}else{
            Log.d(TAG,"swapCursor null");
        }
		return super.swapCursor(newCursor);
	}

	@Override
	public int getCount() {
		int count = 0;
		if(dirList == null){
			count =  super.getCount();
		}else{
			count = dirList.size();
		}
		Log.d(TAG, "count = " + count);
		return count;
	}
	
	/**
	* ������������ʼ�� 
	* @param cursor
	 */
	private void initSelectionManager(Cursor cursor){
        Log.d(TAG,"initSelectionManager");
		int	BitmapPathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		
		if(bucketDisplayNameIndex == -1){
			bucketDisplayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
		}

        if(bucketDisplayNameList != null){
            bucketDisplayNameList.clear();
        }else {
            bucketDisplayNameList = new ArrayList<String>();
        }

        if(dirList != null){
            dirList.clear();
        }else {
            dirList = new ArrayList<Dir>();
        }

		String bitmapBucketDisplayName = null;
		cursor.moveToFirst();
        int index=0;
		for(int i=0;i<cursor.getCount();i++){
			bitmapBucketDisplayName = cursor.getString(bucketDisplayNameIndex);
	    	if(!bucketDisplayNameList.contains(bitmapBucketDisplayName)){
                index++;
	    		bitmapPath = cursor.getString(BitmapPathIndex);
	    		dirList.add(new Dir(bucketDisplayNameList.size(),bitmapBucketDisplayName,bitmapPath));
	    		bucketDisplayNameList.add(bitmapBucketDisplayName);
	    		Log.d(TAG,"bitmapBucketDisplayName :" + bitmapBucketDisplayName);
	    	}
	    	int id = bucketDisplayNameList.indexOf(bitmapBucketDisplayName);
	    	dirList.get(id).count++;
	    	cursor.moveToNext();
		}
	}

	/**
	* �������� checkbox����¼�
	 */
	private class onItemTouchListener implements OnTouchListener{

		Dir dir = null;
		final FragmentManager fm = mFrag.getFragmentManager();
		
		public onItemTouchListener(final Dir dir){
			this.dir = dir;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {

		switch(MotionEventCompat.getActionMasked(event)){
			case MotionEvent.ACTION_DOWN:
				v.setBackgroundColor(mFrag.getActivity().getResources().getColor(R.color.gray));
				Log.d(TAG,"ACTION_DOWN");
				break;
			
			
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_CANCEL:
				Log.d(TAG,"ACTION_MOVE");
				v.setBackgroundColor(mFrag.getActivity().getResources().getColor(R.color.white));
				break;
			
			
			case MotionEvent.ACTION_UP:
				Log.d(TAG,"ACTION_UP");
				v.setBackgroundColor(mFrag.getActivity().getResources().getColor(R.color.white));
				//SubFragmentPicture subFrag = (SubFragmentPicture) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentSubPicture);	
				SubFragmentPicture subFrag = new SubFragmentPicture();
				DLNAFragmentFactory.setFragInstance(DLNAFragmentFactory.FragmentSubPicture,
						subFrag);

            	Bundle bundle = new Bundle();  
	            bundle.putString("dir", dir.name);  
            	subFrag.setArguments(bundle);

				
	            new PictureFragmentSwitcher(fm).switchFragment(
	            		DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentAlbumList),
	            		subFrag);
				    	
				//mFrag.getActivity().findViewById(R.id.btnMutiSelect).setVisibility(View.VISIBLE);
				break;
				
			default:
				Log.d(TAG,"ACTION_?" + MotionEventCompat.getActionMasked(event));
				break;
			}
			return true;
			
		}
		
	}

	/**
	 * ������getView����
	 */
	@Override
	public void bindView(View v, Context arg1, Cursor arg2) {
        final Dir dir = dirList.get(arg2.getPosition());
        final String dirName = dir.name;
        final String bitmapPath = dir.firstPicPath;
        final int count = dir.count;

        ImageView itemView = (ImageView) v.findViewById(R.id.picture_album_itm_iv);
        TextView itemTxt =(TextView) v.findViewById(R.id.picture_album_itm_tv);

        itemView.setTag(arg2.getPosition());
        onItemTouchListener itmListener = new onItemTouchListener(dir);
        v.setOnTouchListener(itmListener);
        imageLoader.LoadBitmap(bitmapPath,true, new BitmapCallbackImpl(itemView,arg2.getPosition()));
        itemTxt.setText(dirName + "(" + count + ")");
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		View view = inflater.inflate(R.layout.dlna_picuture_album_item, arg2,false);
		return view;
	}
	
	public class Dir{
		public int id;
		public int count;
		public String name;
		public String firstPicPath;
		
		Dir(int id,String name,String firstPicPath){
			this.id = id;
			this.name = name;
			this.firstPicPath = firstPicPath;
			this.count = 0;
		}
	}

}
