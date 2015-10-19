/**
 * 
 */
package com.casky.dlna.picture.sub;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.casky.smartremote.R;
import com.casky.dlna.picture.Picture;
import com.casky.dlna.picture.utils.AsyncImageLoader;
import com.casky.dlna.utils.BitmapCallbackImpl;

import java.util.List;

/** 
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�GalleryListAdapter  
 * �������� 
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-10-11 ����6:00:46
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-10-11 ����6:00:46
 * �汾�� 1.0    
 */
public class GalleryListAdapter extends BaseAdapter {

	private List<Picture> sourceList = null;
	private LayoutInflater inflater = null;
	private AsyncImageLoader imageLoader = null;
	private Fragment frag = null;

	public GalleryListAdapter(Fragment frag,List<Picture> sourceList){
		this.sourceList = sourceList;
		this.inflater = LayoutInflater.from(frag.getActivity());
		this.frag = frag;
		imageLoader = new AsyncImageLoader();
	}

	@Override
	public int getCount() {
		return sourceList.size();
	}

	@Override
	public Object getItem(int position) {
		return sourceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/* (non-Javadoc)
	 * @see at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter#getCoverFlowItem(int, android.view.View, android.view.ViewGroup)
	 */
//	@Override
//	public View getCoverFlowItem(int position, View reusableView,
//			ViewGroup parent) {
//		reusableView = inflater.inflate(R.layout.dlna_picuture_gallery_list_item, null);
//		ImageView imageContent= (ImageView)reusableView.findViewById(R.id.pic_gallery_iv_item);
//		String bitmapPath = selectionManager.getPicture(position).getMetaData().getPath();
//		imageLoader.LoadBitmap(bitmapPath, true,new BitmapCallbackImpl(imageContent));
//		return reusableView;
//	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.dlna_picuture_gallery_list_item, null);
		ImageView imageContent= (ImageView)convertView.findViewById(R.id.pic_gallery_iv_item);
		imageContent.setTag(position);
		String bitmapPath = sourceList.get(position).getMetaData().getPath();
		imageLoader.LoadBitmap(bitmapPath, true,new BitmapCallbackImpl(imageContent,position));
		return convertView;
	}

    public void removeItem(int position){
        //sourceList.remove(position);
        this.notifyDataSetChanged();
    }

}
