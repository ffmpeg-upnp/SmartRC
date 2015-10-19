/**
 * 
 */
package com.casky.dlna.picture.sub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.casky.smartremote.R;
import com.casky.dlna.main.DLNAFragmentFactory;
import com.casky.dlna.main.MainFragmentDLNA;
import com.casky.dlna.picture.PictureCursorAdapter;
import com.casky.dlna.picture.utils.PictureFragmentSwitcher;
import com.casky.dlna.picture.utils.PictureLoaderCallbacks;

/** 
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�AllAlbum  
 * �������� ��ʾ�������
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-9-28 ����10:43:46 
 */
public class AlbumListFragment extends Fragment {

    public static final String TAG = "AlbumListFragment";

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
    			R.layout.dlna_picuture_album_list, container, false);
		ListView albumListView = (ListView) rootView.findViewById(R.id.picture_album_lv);
		CursorAdapter adapter = new AlbumListCursorAdapter(getActivity(),this, null, PictureCursorAdapter.flag);
		PictureLoaderCallbacks loaderCallback = new PictureLoaderCallbacks(getActivity(), adapter,
				PictureLoaderCallbacks.pictureListSelection,
				PictureLoaderCallbacks.pictureListSelectionArgs);
		getLoaderManager().initLoader(
				0, 
				null, 
				loaderCallback);		
		albumListView.setAdapter(adapter);
		
		final Button backBtn = (Button)rootView.findViewById(R.id.pic_album_btn_back);
		backBtn.setOnClickListener(new OnClickListener() {
			MainFragmentDLNA dlnaFrag = (MainFragmentDLNA) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentMainDLNA);;
			@Override
			public void onClick(View v) {
				new PictureFragmentSwitcher(getFragmentManager()).switchFragment(
						AlbumListFragment.this,
						dlnaFrag);
			}
		});

		return rootView;
	}

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d(TAG,"onHiddenChanged:"+hidden);
        super.onHiddenChanged(hidden);
    }
}
