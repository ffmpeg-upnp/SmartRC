/**
 * 
 */
package com.casky.dlna.picture;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.casky.smartremote.R;
import com.casky.dlna.main.DLNAFragmentFactory;
import com.casky.dlna.main.MainFragmentDLNA;
import com.casky.dlna.main.Manageable;
import com.casky.dlna.main.MediaFileSelectionManager;
import com.casky.dlna.picture.sub.AlbumListFragment;
import com.casky.dlna.picture.utils.PictureFragmentSwitcher;
import com.casky.dlna.picture.utils.PictureLoaderCallbacks;
import com.casky.dlna.utils.Utils;

/**
 * ��Ŀ��ƣ�PictureFragmentContainer
 * ����ƣ�PictureFragment  
 * �������� DLNAͼƬUI
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-9-9 ����4:50:06
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-9-9 ����4:50:06 
 */
public class MainFragmentPicture extends Fragment implements Manageable{

	public static final String picLayoutID = "itm_pic_imgview";

	private static final String TAG = "MFP";
	
	private PictureSelectionManager selectionManager = null;
	private PictureCursorAdapter adapter = null;
	
	private RelativeLayout picSubFragTitleBar = null;
	private TextView picSubTBCountTv = null;
	private Button picSubTbCancelBtn = null;
	private Button picSubTbSlcAllBtn = null;
	private RelativeLayout albmBtn = null;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
    			R.layout.dlna_fragment_main_picture, container, false);
		final GridView gv = (GridView)rootView.findViewById(R.id.pic_detail_gv_content);
		albmBtn = (RelativeLayout)rootView.findViewById(R.id.pic_detail_rl_album);
		
		picSubFragTitleBar = (RelativeLayout) rootView.findViewById(R.id.pic_detail_rl_titleBar);
		picSubTBCountTv = (TextView) rootView.findViewById(R.id.pic_detail_tv_TitleText);
		picSubTbCancelBtn = (Button) rootView.findViewById(R.id.pic_detail_btn_cancel_mltslc);
		picSubTbSlcAllBtn = (Button) rootView.findViewById(R.id.pic_detail_btn_SelectAll);
		
		final FragmentManager fm = getFragmentManager();
		
		final MainFragmentDLNA mainFragDlna = (MainFragmentDLNA) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentMainDLNA);
		final AlbumListFragment albumFrag = (AlbumListFragment) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentAlbumList);
		
		adapter = new PictureCursorAdapter(mainFragDlna, null,picSubTbSlcAllBtn,picSubTBCountTv ,PictureCursorAdapter.flag);
        adapter.setDirectory("Camera");
		PictureLoaderCallbacks loaderCallback = new PictureLoaderCallbacks(getActivity(), adapter,PictureLoaderCallbacks.cameraSelection,PictureLoaderCallbacks.cameraSelectionArgs);
		getLoaderManager().initLoader(
				0, 
				null, 
				loaderCallback);		
		gv.setAdapter(adapter);
		selectionManager = adapter.getSelectionManager();
		gv.setOnScrollListener(adapter);
		gv.setOnItemClickListener(adapter);
		
		
		
		albmBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new PictureFragmentSwitcher(fm).switchFragment(
						mainFragDlna,
						albumFrag);
				selectionManager.cancelMultipleSelect();
				MainFragmentDLNA.changeSelectionUI(false);
			}
		});
		
		/**
		 * 
		 */
		picSubTbCancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectionManager.cancelMultipleSelect();
				MainFragmentDLNA.changeSelectionUI(false);
			}
		});
		
		 /**
         * ȫѡ��ť�ĵ���¼�
         */
        picSubTbSlcAllBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!selectionManager.isAllSelected()){
					selectionManager.selectAll();
					picSubTbSlcAllBtn.setText(getActivity().getString(R.string.dlna_title_bar_unselect_all));
				}else{
					selectionManager.unSelectAll();
					picSubTbSlcAllBtn.setText(getActivity().getString(R.string.dlna_title_bar_select_all));
				}
				picSubTBCountTv.setText(Utils.createTitleText(selectionManager.getSelectFiles().size()));					
			}
		});

        return rootView;
	}
	


	@Override
	public MediaFileSelectionManager getMediaFileSelectionManager() {
		Log.v(TAG, "selectionManager :" + selectionManager);
		return selectionManager;
	}

	public String getDirectory(){
		return adapter.getDirectory();
	}

	/* (non-Javadoc)
	 * @see com.casky.com.casky.dlna.main.Manageable#showTitleBar()
	 */
	@Override
	public void showTitleBar() {
		albmBtn.setVisibility(View.GONE);
		picSubTBCountTv.setVisibility(View.VISIBLE);
		picSubTBCountTv.setText(Utils.createTitleText(selectionManager.getSelectFiles().size()));
		picSubFragTitleBar.setVisibility(View.VISIBLE);
	}



	/* (non-Javadoc)
	 * @see com.casky.com.casky.dlna.main.Manageable#hideTitleBar()
	 */
	@Override
	public void hideTitleBar() {
		albmBtn.setVisibility(View.VISIBLE);
		picSubTBCountTv.setVisibility(View.INVISIBLE);
		picSubFragTitleBar.setVisibility(View.GONE);
	}

    @Override
    public void refreshTitleCount(){
        picSubTBCountTv.setText(Utils.createTitleText(selectionManager.getSelectFiles().size()));
    }

    public void loadData(){
		adapter.loadApplicationShare();
	}
	
	public void saveData(){
		adapter.saveApplicationShare();
	}
	
	public void cleanData(){
		adapter.removeApplicationShare();
	}
}
