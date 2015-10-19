/**
 * 
 */
package com.casky.dlna.picture.sub;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.casky.dlna.main.MediaFile;
import com.casky.dlna.utils.FileUtil;
import com.casky.smartremote.R;
import com.casky.dlna.main.DLNAFragmentFactory;
import com.casky.dlna.main.Manageable;
import com.casky.dlna.main.MediaFileSelectionManager;
import com.casky.dlna.picture.PictureCursorAdapter;
import com.casky.dlna.picture.utils.PictureFragmentSwitcher;
import com.casky.dlna.picture.utils.PictureLoaderCallbacks;
import com.casky.dlna.utils.Utils;

import java.util.List;

/**
 * ��Ŀ��ƣ�PictureFragmentContainer
 * ����ƣ�PictureFragment  
 * �������� DLNAͼƬUI
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-9-9 ����4:50:06
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-9-9 ����4:50:06 
 */
public class SubFragmentPicture extends Fragment implements Manageable,View.OnTouchListener{

	private static final String TAG = "SubFragmentPicture";
	
	public static MediaFileSelectionManager selectionManager = null;
    private TextView btnTitleText = null;
    private Button mutipleSelecte = null;
    private Button btnBack = null;
    private Button btnSelectAll = null;
    private RelativeLayout multiSelectMenu;
    private String dir = null;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
    			R.layout.dlna_picture_sub_grid, container, false);
		
		final GridView gv = (GridView)rootView.findViewById(R.id.pic_detail_gv_content);
		
		mutipleSelecte = (Button) rootView.findViewById(R.id.pic_sub_btn_multi_select);
        btnSelectAll = (Button) rootView.findViewById(R.id.pic_sub_btn_select_all);
        btnBack = (Button) rootView.findViewById(R.id.pic_sub_btn_back);
        btnTitleText = (TextView) rootView.findViewById(R.id.pic_sub_tv_select_count);
        multiSelectMenu = (RelativeLayout)rootView.findViewById(R.id.pic_sub_rl_dlna_menu);
        final Button dlnaMenuPushBtn = (Button) multiSelectMenu.findViewById(R.id.pic_sub_btn_menu_push);
        final Button dlnaMenuDeleteBtn = (Button)multiSelectMenu.findViewById(R.id.pic_sub_btn_menu_delete);
        final Button dlnaMenuShareBtn = (Button)multiSelectMenu.findViewById(R.id.pic_sub_btn_menu_share);
        
        
        dir = getArguments().getString("dir");
        
    	final String selection = 
    			MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " like " + "?";
    	final String[] selectionArgs = {dir};
    	
		final PictureCursorAdapter adapter = new PictureCursorAdapter(this, null,btnSelectAll,btnTitleText, PictureCursorAdapter.flag);
        adapter.setDirectory(dir);
		PictureLoaderCallbacks loaderCallback = new PictureLoaderCallbacks(getActivity(), 
				adapter,selection,
				selectionArgs);
		
		getLoaderManager().initLoader(
				0, 
				null, 
				loaderCallback);		
		gv.setAdapter(adapter);
		selectionManager = adapter.getSelectionManager();
		gv.setOnScrollListener(adapter);
		gv.setOnItemClickListener(adapter);

        mutipleSelecte.setOnTouchListener(this);
        btnSelectAll.setOnTouchListener(this);
        btnSelectAll.setOnTouchListener(this) ;
        dlnaMenuPushBtn.setOnTouchListener(this);
        dlnaMenuDeleteBtn.setOnTouchListener(this);
        dlnaMenuShareBtn.setOnTouchListener(this);
        btnBack.setOnTouchListener(this);
        btnTitleText.setText(dir);
        return rootView;
	}
	


	@Override
	public MediaFileSelectionManager getMediaFileSelectionManager() {
		Log.v(TAG, "selectionManager :" + selectionManager);
		return selectionManager;
	}
	


	@Override
	public void showTitleBar() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void hideTitleBar() {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void refreshTitleCount() {

        btnTitleText.setText(Utils.createTitleText(selectionManager.getSelectFiles().size()));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        switch (view.getId()) {
            case R.id.pic_sub_btn_multi_select:
                if (!selectionManager.isMultipleSelect()) {
                    selectionManager.startMultipleSelect();
                    btnBack.setVisibility(View.GONE);
                    mutipleSelecte.setText(getActivity().getString(R.string.dlna_title_bar_cancel));
                    btnSelectAll.setVisibility(View.VISIBLE);
                    btnTitleText.setText(getActivity().getString(R.string.dlna_title_bar_select_count));
                    multiSelectMenu.setVisibility(View.VISIBLE);
                } else {
                    selectionManager.cancelMultipleSelect();
                    mutipleSelecte.setText(getActivity().getString(R.string.dlna_title_bar_multiple_select));
                    btnBack.setVisibility(View.VISIBLE);
                    btnSelectAll.setVisibility(View.GONE);
                    btnTitleText.setText(dir);
                    multiSelectMenu.setVisibility(View.GONE);
                }
                break;
            case R.id.pic_sub_btn_select_all:
                if (!selectionManager.isAllSelected()) {
                    selectionManager.selectAll();
                    btnSelectAll.setText(getActivity().getString(R.string.dlna_title_bar_unselect_all));
                } else {
                    selectionManager.unSelectAll();
                    btnSelectAll.setText(getActivity().getString(R.string.dlna_title_bar_select_all));
                }
                btnTitleText.setText(Utils.createTitleText(selectionManager.getSelectFiles().size()));
                break;
            case R.id.pic_sub_btn_menu_push:
                GalleryFragment galleryFrag = (GalleryFragment) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentGallery);
                Bundle bundle = new Bundle();
                bundle.putInt(GalleryFragment.KEY_POSITION, 0);
                bundle.putInt(GalleryFragment.KEY_SOURCE, GalleryFragment.SOURCE_SUBFRAGPIC_SLC);

                galleryFrag.setArguments(bundle);
                new PictureFragmentSwitcher(getFragmentManager()).
                        switchFragment(SubFragmentPicture.this, galleryFrag);
                break;
            case R.id.pic_sub_btn_menu_delete:
                selectionManager.deleteFile(getActivity().getContentResolver());
                refreshTitleCount();
                break;
            case R.id.pic_sub_btn_back:
                AlbumListFragment albumListFrag = (AlbumListFragment) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentAlbumList);
                ;
                new PictureFragmentSwitcher(getFragmentManager()).switchFragment(
                        SubFragmentPicture.this,
                        albumListFrag);
                selectionManager.cancelMultipleSelect();
                break;
            case R.id.pic_sub_btn_menu_share:
                if(selectionManager.getSelectedMediaFileCount() == 1){
                    FileUtil.shareSingleMediaFile(getActivity(),selectionManager.getSelectFiles().get(0));
                }else if(selectionManager.getSelectedMediaFileCount() > 1) {
                    FileUtil.shareMultipleMediaFile(getActivity(), (List<MediaFile>) selectionManager.getSelectFiles());
                }
                break;
        }
        return true;
    }
}
