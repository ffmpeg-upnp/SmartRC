package com.casky.main.slidingmenu;

import com.casky.dlna.main.DLNAFragmentFactory;
import com.casky.dlna.main.MainFragmentDLNA;
import com.casky.dlna.picture.sub.AlbumListFragment;
import com.casky.dlna.picture.sub.GalleryFragment;
import com.casky.dlna.picture.sub.SubFragmentPicture;
import com.casky.dlna.picture.utils.PictureFragmentSwitcher;
import com.casky.dlna.utils.BitmapLruCache;
import com.casky.dlna.utils.Utils;
import com.casky.main.slidingmenu.MenuFragment.SLMenuListOnItemClickListener;
import com.casky.remote.rc.main.ExitApplication;
import com.casky.remote.rc.main.MainFragmentRemote;
import com.casky.remote.rc.network.Client;
import com.casky.smartremote.R;
import com.casky.remote.setting.Hapticswitch;
import com.casky.remote.setting.SettingFragment;
import com.casky.remote.utils.Helper;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import android.content.ServiceConnection;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
/**
 * 
*    
* 项目名称：SmartRemote   
* 类名称：MainActivity   
* 类描述：程序的框架类，包含ActionBar，Tab和Fragment   
* 创建人：shaojiansong   
* 创建时间＄1�714-8-25 下午2:07:57   
* 修改人：shaojiansong   
* 修改时间＄1�714-8-25 下午2:07:57   
* 修改备注＄1�7   
* 版本＄1�7 1.0    
*
 */
public class MainActivity extends SlidingFragmentActivity implements SLMenuListOnItemClickListener{
	private static String TAG = "MainActivity";
	private SlidingMenu mSlidingMenu;
	private Toast toast = null;
	
	public final static String EXTRA_MESSAGE_IP = "com.casky.TVIP";

    public static final int SLIDING_MENU_TOGGLE = 0x01;

	private Fragment fragmentSetting = null;
	private SettingFragment fragmentSetingTemp = null;
	private MainFragmentRemote fragmentMainRC = null;
	private Fragment fragmentMain =null;
    private Fragment current_fragment = null;
    private Fragment DLNA_fragment = null;
    private ServiceConnection serviceConnection;
    private FragmentManager fm = getSupportFragmentManager();
    private PictureFragmentSwitcher fragSwitcher = null;


	@SuppressLint("HandlerLeak")
	public Handler  MainHandler=new Handler() 
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
				switch (msg.what)
				{
				case SLIDING_MENU_TOGGLE:
					Log.v(TAG, "toggle receiced");
		            toggle(); /*auto open or close SlidingMenu*/
					break;
				
				default:
					break;
				}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.rc_main_fragment_content);
		//set the Behind View
        setBehindContentView(R.layout.rc_main_fragment_menu);
     // customize the SlidingMenu
        mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setShadowDrawable(R.drawable.main_menu_shadow);//设置阴影图片
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width); //设置阴影图片的宽庄1�7
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset); //SlidingMenu划出时主页面显示的剩余宽庄1�7
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        
      //设置 SlidingMenu 内容
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MenuFragment menuFragment = new MenuFragment();
        fragmentTransaction.replace(R.id.rc_key_frequent_btn_menu, menuFragment);
        fragmentTransaction.commit();
        Log.v(TAG, "First new MainFragmentRemote");     
        fragmentMain = switchtSelectFragment(fragmentMain, MainFragmentRemote.class, true);
    	fragmentMainRC = (MainFragmentRemote)fragmentMain;
        MainMenuUIUpdater UIupdater = new MainMenuUIUpdater();
        UIupdater.HandlerRegister(MainHandler);

        serviceConnection = ((RemoteApplication)getApplication()).getMusicPlayerServiceConnection();
        fragSwitcher = new PictureFragmentSwitcher(fm);
        Utils.initContext(getApplication());
	}
		
	@Override
	protected void onResume() {
		Log.v(TAG, "onResume");
		super.onResume();
	}
	
	protected void onStop()
	{
		Log.v(TAG, "onStop");
		super.onStop();
	}

    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (current_fragment != DLNA_fragment &&
                keyCode == KeyEvent.KEYCODE_BACK &&
                event.getRepeatCount() == 0) {
        	ExitApplication.getInstance().exit(this);//dialog(this);  
            return true;  
        }  else if (current_fragment == DLNA_fragment &&
                keyCode == KeyEvent.KEYCODE_BACK &&
                event.getRepeatCount() == 0) {
            MainFragmentDLNA mainFragDLNA = (MainFragmentDLNA) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentMainDLNA);
            AlbumListFragment albumListFrag = (AlbumListFragment) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentAlbumList);
            SubFragmentPicture subPicFrag = (SubFragmentPicture) DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentSubPicture);
            GalleryFragment galleryFrag = (GalleryFragment)DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentGallery);

            switch(PictureFragmentSwitcher.CurrentFragment){
                case PictureFragmentSwitcher.FragmentDLNA:
                    //findViewById(R.id.dlna_main_rl_title).setVisibility(View.VISIBLE);
                    ExitApplication.getInstance().exit(this);
                    break;

                case PictureFragmentSwitcher.FragmentAlbumList:
                    fragSwitcher.switchFragment(
                            albumListFrag,
                            mainFragDLNA);
                    return true;
                case PictureFragmentSwitcher.FragmentSubPicture:
                    fragSwitcher.switchFragment(
                            subPicFrag,
                            albumListFrag);
                    subPicFrag.getMediaFileSelectionManager().cancelMultipleSelect();
                    return true;
                case PictureFragmentSwitcher.FragmentGallery:
                    if(PictureFragmentSwitcher.LastFragment == PictureFragmentSwitcher.FragmentDLNA){
                        fragSwitcher.switchFragment(
                                galleryFrag,
                                mainFragDLNA);
                    }else if(PictureFragmentSwitcher.LastFragment == PictureFragmentSwitcher.FragmentSubPicture){
                        fragSwitcher.switchFragment(
                                galleryFrag,
                                subPicFrag);
                    }

                    return true;
            }
        }
        return true;  
    }
    
    private Fragment switchtSelectFragment(Fragment selectFragment, Class<?> fragment,boolean firstentry){
    	if(selectFragment == null){
    		//Log.v(TAG, fragment.getName());
    		selectFragment = Fragment.instantiate(this, fragment.getName());    		
    	}else{
    		if(current_fragment != selectFragment){    			
    		}
    		else{
    			toggle();
    			return selectFragment;
    		}
    	}
    	
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		FragmentTransaction ft = fragmentManager.beginTransaction();  
		if(current_fragment != null){
			ft.hide(current_fragment);
			
			if(!selectFragment.isAdded()){
				Log.v(TAG, "1.before add!" + "selectFragment"+selectFragment);
				ft.add(R.id.rc_main_frag_content_fl_content, selectFragment);
			}
			ft.show(selectFragment);
			ft.commit();
		}else{
			Log.v(TAG, "before replace!" + "current_fragment"+current_fragment);
			if(!selectFragment.isAdded()){
				//Log.v(TAG, "2.before add!" + "selectFragment"+selectFragment);
				ft.add(R.id.rc_main_frag_content_fl_content, selectFragment).show(selectFragment).commit();
			}
		}
		current_fragment = selectFragment;
		if(!firstentry){			
			MainHandler.sendEmptyMessage(SLIDING_MENU_TOGGLE);
        }
        return selectFragment;
    }

	@Override
	public void selectItem(int position, String title) {
 
	    switch (position) {  
	    case 0:  
	    	fragmentMain = switchtSelectFragment(fragmentMain, MainFragmentRemote.class, false);

	        break;  
	    case 1:
            DLNA_fragment = DLNAFragmentFactory.getFragInstance(DLNAFragmentFactory.FragmentMainDLNA);
            switchtSelectFragment(DLNA_fragment,MainFragmentDLNA.class,false);
            PictureFragmentSwitcher.setCurrentFragment(PictureFragmentSwitcher.FragmentDLNA);
	        break;  
	    case 2:  
	        //fragment = new PhotosFragment();  
	        break;  
	    case 3:

	    	fragmentSetting = switchtSelectFragment(fragmentSetting, SettingFragment.class, false);
	        break;
	    default:  
	        break;  
	    }  
	
	}
	
    public void onButtonClicked(View v)
    {
    	Hapticswitch.configHaptics(v);
    	Log.d(TAG, "onButtonClicked! ");
    	if (v instanceof Button) {
    		if (MainFragmentRemote.sendClient != null) {
    			Integer RcID = Integer.parseInt(((Button)v).getTag().toString());
        		String message = new String(RcID.toString());
        		Log.d(TAG, "send msg is " + message);
        		MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(Helper.createMessage(Client.REMOTE_MESSAGE, message));
			}
		}
    }
     
 	private void showTip(String str){
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setText(str);
		toast.show();
	}

    @Override
    protected void onDestroy(){
        super.onDestroy();
        BitmapLruCache.recycleAllBitmap();
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
    }
}
