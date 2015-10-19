package com.casky.dlna.main;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.casky.dlna.utils.FileUtil;
import com.casky.main.slidingmenu.MainMenuUIUpdater;
import com.casky.main.slidingmenu.RemoteApplication;
import com.casky.smartremote.R;
import com.casky.dlna.music.MainFragmentMusic;
import com.casky.dlna.picture.MainFragmentPicture;
import com.casky.dlna.picture.sub.GalleryFragment;
import com.casky.dlna.picture.utils.PictureFragmentSwitcher;
import com.casky.dlna.video.MainFragmentVideo;
import com.casky.dlna.video.Video;
import com.casky.dlna.video.VideoPlayWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fujiangtao
 * @description: DLNA ui main class
 * @create date: 2014-9-10
 * */
public class MainFragmentDLNA extends Fragment implements View.OnTouchListener{

	/*define object ragmentTabHost*/
    private static NDFragmentTabHost mTabHost;
    /*define a layout*/
    private LayoutInflater layoutInflater;
    private View main_view;
    /*define array to save Fragment UI*/
	private Class<?> fragmentArray[] = { MainFragmentPicture.class, MainFragmentVideo.class, MainFragmentMusic.class };
	private int iconArray[] = { R.drawable.dlna_tab_pic_icon, R.drawable.dlna_tab_video_icon, R.drawable.dlna_tab_music_icon };
	private String titleArray[] = null;
	
	private static ViewPager vp;
	
	private List<Fragment> list = new ArrayList<Fragment>();
	
	private static final String TAG = "DLNA main";
	
	private static MyAdapter adapter = null;


	private static RelativeLayout multiSelectMenu = null;
	private static RelativeLayout titleBar  = null;

    private  MainFragmentPicture mainFragPic = null;
    private  MainFragmentVideo mainFragVideo = null;
    private  MainFragmentMusic mainFragMusic = null;

    public void onActivityCreated(Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
        Log.d(TAG, "onCreate");
        initPagerTitlebar();
    } 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 /*instantiate layout object*/
        layoutInflater = LayoutInflater.from(getActivity());
        main_view = layoutInflater.inflate(R.layout.dlna_fragment_main,null);
		initView(main_view);
		return main_view;
		
	}
	/**
	 * @author fujiangtao
	 * @description: initialize view, include:tabhost
	 * @create date: 2014-9-10
	 * */
	
	private void initView(View rootView) {
		titleArray = new String[]{ 
				getResources().getString(R.string.dlna_tabhost_picture), 
				getResources().getString(R.string.dlna_tabhost_video),
				getResources().getString(R.string.dlna_tabhost_music) };
		vp = (ViewPager) main_view.findViewById(R.id.main_pager);
        vp.setOnPageChangeListener(new ViewPagerListener());
               
        /*instantiate TabHost object, get TabHost*/
		mTabHost = (NDFragmentTabHost) main_view.findViewById(android.R.id.tabhost);

		/*get TabHost and initialize it*/
		mTabHost.setup(getActivity(), getFragmentManager(), R.id.main_pager);
		mTabHost.getTabWidget().setDividerDrawable(null);
		mTabHost.setOnTabChangedListener(new TabHostListener());

		int count = fragmentArray.length;

		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec(titleArray[i]).setIndicator(getTabItemView(i));
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
		}

	}

	/**
	 * @param: void
     * @return: void
	 * @author fujiangtao
	 * @description: initialize view pager
	 * @create date: 2014-9-10
	 * */
	private void initPagerTitlebar() {
		mainFragPic = (MainFragmentPicture) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentMainPicture);
		mainFragVideo = (MainFragmentVideo) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentMainVideo);
		mainFragMusic = (MainFragmentMusic) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentMainMusic);;

        list.add(mainFragPic);
        list.add(mainFragVideo);
        list.add(mainFragMusic);
        
        adapter = new MyAdapter(getFragmentManager());
        vp.setAdapter(adapter);
        
        titleBar = (RelativeLayout)getActivity().findViewById(R.id.dlna_main_rl_title);
        titleBar.setVisibility(View.VISIBLE);
        multiSelectMenu = (RelativeLayout)main_view.findViewById(R.id.main_rl_dlna_menu);

        Button startMltSlcBtn = (Button) getActivity().findViewById(R.id.main_btn_start_mltslc);
        Button btnTitleText = (Button) getActivity().findViewById(R.id.dlna_main_btn_TitleText);
        Button dlnaMenuPushBtn = (Button) multiSelectMenu.findViewById(R.id.menu_push_button);
        Button dlnaMenuDeleteBtn = (Button)multiSelectMenu.findViewById(R.id.menu_delete_button);
        Button dlnaMenuShareBtn = (Button)multiSelectMenu.findViewById( R.id.menu_share_button);
        ImageView dlnaSlidingMenuIV = (ImageView)titleBar.findViewById(R.id.dlna_main_iv_imageMainMenu);

        dlnaSlidingMenuIV.setOnTouchListener(this);
        startMltSlcBtn.setOnTouchListener(this);
        dlnaMenuPushBtn.setOnTouchListener(this);
        dlnaMenuDeleteBtn.setOnTouchListener(this);
        dlnaMenuShareBtn.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(motionEvent.getAction() != MotionEvent.ACTION_DOWN){
            return true;
        }
        MediaFileSelectionManager manager =  getSelectionManager();
        switch (view.getId()){
            case R.id.dlna_main_iv_imageMainMenu:
                MainMenuUIUpdater UIupdater = new MainMenuUIUpdater();
                UIupdater.sendMessage(MainMenuUIUpdater.TOGGLE);
                break;
            case R.id.main_btn_start_mltslc:
                if(!manager.isMultipleSelect()){
                    manager.startMultipleSelect();
                    changeSelectionUI(true);
                }else{
                    manager.cancelMultipleSelect();
                    changeSelectionUI(false);
                }
                break;
            case R.id.menu_push_button:
                if(manager.getSelectedMediaFileCount() == 0){
                    return true;
                }
                Bundle bundle = null;
                switch(vp.getCurrentItem()){
                    case 0:
                        GalleryFragment galleryFrag = (GalleryFragment) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentGallery);
                        bundle = new Bundle();
                        bundle.putInt(GalleryFragment.KEY_POSITION, 0);
                        bundle.putString(GalleryFragment.KEY_DIRECTORY, mainFragPic.getDirectory());
                        bundle.putInt(GalleryFragment.KEY_SOURCE, GalleryFragment.SOURCE_MAINFRAGPIC_SLC);

                        galleryFrag.setArguments(bundle);
                        new PictureFragmentSwitcher(getFragmentManager()).
                                switchFragment(MainFragmentDLNA.this, galleryFrag);
                        break;
                    case 1:
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), VideoPlayWindow.class);
                        List<Video> videoList = (List<Video>) manager.getSelectFiles();
                        RemoteApplication share = (RemoteApplication) getActivity().getApplication().getApplicationContext();
                        share.setVideoList(videoList);
                        bundle = new Bundle();
                        bundle.putInt(MainFragmentVideo.KEY_POSITION, 0);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 2:
                        mainFragMusic.startPlayer((List<com.casky.dlna.music.MusicFile>) manager.getSelectFiles(),0);
                        mainFragMusic.resumeLocalPlay();
                        break;
                }
                break;
            case R.id.menu_delete_button:

                manager.deleteFile(getActivity().getContentResolver());
                Manageable manageableFrag = (Manageable) adapter.getItem(vp.getCurrentItem());
                manageableFrag.refreshTitleCount();
                break;
            case R.id.menu_share_button:
                if(manager.getSelectedMediaFileCount() == 1){
                    FileUtil.shareSingleMediaFile(getActivity(),manager.getSelectFiles().get(0));
                }else if(manager.getSelectedMediaFileCount() > 1) {
                    FileUtil.shareMultipleMediaFile(getActivity(), (List<MediaFile>) manager.getSelectFiles());
                }
                break;
        }
        return false;
    }
	
	public static void changeSelectionUI(boolean startSelecte){
		Manageable manageableFrag = (Manageable) adapter.getItem(vp.getCurrentItem());
		if(startSelecte){
			titleBar.setVisibility(View.GONE);
			mTabHost.setVisibility(View.GONE);
			manageableFrag.showTitleBar();
			multiSelectMenu.setVisibility(View.VISIBLE);
		}else{
			titleBar.setVisibility(View.VISIBLE);
			mTabHost.setVisibility(View.VISIBLE);
			manageableFrag.hideTitleBar();
			multiSelectMenu.setVisibility(View.GONE);
		}
	}
	
	private MediaFileSelectionManager getSelectionManager(){
		Manageable manageableFrag = (Manageable) adapter.getItem(vp.getCurrentItem());
		MediaFileSelectionManager selectionManager = 
				manageableFrag.getMediaFileSelectionManager();
		if(selectionManager == null){
			throw new IllegalStateException("This fragment didn't return a MediaFileSelectionManager");
		}
		return selectionManager;
	}

	/**
	 * @param: index
     * @return: view
	 * @author fujiangtao
	 * @description: get tab item view(include resource: image and text)
	 * @create date: 2014-9-10
	 * */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.dlna_tab_main_layout, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.rc_main_tab_iv_tabImage);
		imageView.setImageResource(iconArray[index]);
		TextView textView = (TextView) view.findViewById(R.id.rc_main_tab_tv_tabText);
		textView.setText(titleArray[index]);

		return view;
	}

    /**
	 * @author fujiangtao
	 * @description: define tabChangeListener method class
	 * @create date: 2014-9-10
	 * */
	private class TabHostListener implements OnTabChangeListener {
        @Override
        public void onTabChanged(String tabId) {
            int position = mTabHost.getCurrentTab();
            vp.setCurrentItem(position);
        }
    }
	
	/**
	 * @author fujiangtao
	 * @description: define FragmentPagerAdapter class
	 * @create date: 2014-9-10
	 * */
    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public int getCount() {        	
            return list.size();
        }

    }

    /**
	 * @author fujiangtao
	 * @description: define pageChangeListener method class
	 * @create date: 2014-9-10
	 * */
    
    class ViewPagerListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        	
        }
        
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int index) {
            TabWidget widget = mTabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(index);
            widget.setDescendantFocusability(oldFocusability);
            Manageable manageableFrag = (Manageable) adapter.getItem(vp.getCurrentItem());
            changeSelectionUI(manageableFrag.getMediaFileSelectionManager().isMultipleSelect());
            MainFragmentPicture picFrag =  (MainFragmentPicture) adapter.getItem(0);
            picFrag.saveData();
        }
    }
}
