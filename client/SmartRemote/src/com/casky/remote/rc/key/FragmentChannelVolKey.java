package com.casky.remote.rc.key;


import com.casky.remote.rc.main.MainFragmentRemote;
import com.casky.remote.rc.network.Client;
import com.casky.remote.setting.Hapticswitch;
import com.casky.smartremote.R;
import com.casky.remote.utils.Helper;
import android.annotation.SuppressLint;
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
import android.widget.Button;

public class FragmentChannelVolKey extends Fragment {
	private boolean isMoveDown;
	private int pressedTime;
	private float buttonX0;
	private float buttonX1;
	private float buttonY0;
	private float buttonY1;
	private Integer RcID = null;
	private static final int SEND_MESSAGE_START = 0;
	private static final int SEND_MESSAGE_FINISH = 1;

	private Button btnVolUpPressed = null;
	private Button btnVolDownPressed = null;
	String TAG = "ChaVolKey";
	
	 /**
     * @param: void
     * @return: void
     * @author fujiangtao
     * @description: handle message send and finish
     * */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage (Message msg) {
			switch(msg.what) {
                 
			case SEND_MESSAGE_START:
				if(btnVolUpPressed != null){
					RcID = Integer.parseInt(btnVolUpPressed.getTag().toString());
				}else if(btnVolDownPressed != null){
					RcID = Integer.parseInt(btnVolDownPressed.getTag().toString());
				}
				
				String message = new String(RcID.toString());
				Log.d(TAG, "send msg is " + message + " pressedTime" + pressedTime);
				if(MainFragmentRemote.sendClient != null)
				{
					MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(Helper.createMessage(Client.REMOTE_MESSAGE,message));
				}
				break;
             case SEND_MESSAGE_FINISH:
            	 Log.d(TAG,"SEND_MESSAGE_FINISH");
            	 setMoveDown(false);
                break;           
           
             }
         }
     };

    @Override
    public View onCreateView(LayoutInflater inflater, 
        ViewGroup container, Bundle savedInstanceState) {
    	
    	ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.rc_key_page_channel_vol, container, false);
        return rootView;
    }
    
    /**
     * @param: void
     * @return: void
     * @author fujiangtao
     * @description: init vol+&- button touch and onClick listener
     * */
    private void ButtonlistenerInit()
    {
    	final Button.OnClickListener buttonOnClickListener = new Button.OnClickListener(){
            public void onClick(final View v){
            	mHandler.obtainMessage(SEND_MESSAGE_START).sendToTarget();
            }    
     
        };
    	
    	final OnTouchListener buttonOnTouchListener=new OnTouchListener() 
    	{ 
    		public boolean onTouch(final View v, MotionEvent event)
    		{
    			
    			
    			switch (v.getId())
    			{
    			case R.id.rc_key_channel_btn_vol_up:
    				if(btnVolUpPressed == null)
    				{
    					btnVolUpPressed = (Button)v;
    					btnVolDownPressed = null;
    				}
    				break;
    			case R.id.rc_key_channel_btn_vol_down:
    				if(btnVolDownPressed == null)
    				{
    					btnVolDownPressed = (Button)v;
    					btnVolUpPressed = null;
    				}
    				break;
    			default:
    				Log.d(TAG,"btn null");
    				break;
    			}
    			
    			
    			switch (event.getAction())
    			{
    			case MotionEvent.ACTION_DOWN:
    				{
    					//Log.d(TAG,"move down");
    					isMoveDown = true;
    					pressedTime = 0;
    					Hapticswitch.configHaptics(v);
    					
    					/*when vol+&- button was pressed, start thread to send key message to TV*/
    					Thread t = new Thread(){
    						@Override
    						public void run(){
    							super.run();
    							while(isMoveDown)
    							{
    								if(pressedTime > 0){
    									mHandler.obtainMessage(SEND_MESSAGE_START).sendToTarget();
    								}
    								
    								try{
    									Thread.sleep(500);
    									if(pressedTime < 100)
    									{
    										pressedTime++;
    									}
    								}catch(InterruptedException e){
    									e.printStackTrace();
    								}
    							}
    						}
    					};
    					t.start();
    				}
    				break;
				case MotionEvent.ACTION_UP:
    				//Log.d(TAG,"ACTION_up");
    				setMoveDown(false);
    				break;
    			case MotionEvent.ACTION_MOVE:
    				//Log.d(TAG,"ACTION_move");
    				if(btnVolUpPressed != null){
    					buttonX0 = 0;
	    				buttonX1 = btnVolUpPressed.getRight();
	    				buttonY0 = 0;
	    				buttonY1 = btnVolUpPressed.getHeight();
    				}else if(btnVolDownPressed != null){
    					buttonX0 = 0;
	    				buttonX1 = btnVolDownPressed.getRight();
	    				buttonY0 = 0;
	    				buttonY1 = btnVolDownPressed.getHeight();
    				}
    				/*if coordinate beyond button boundary, step send message */
    				/*
    				Log.d(TAG,"buttonX0:"+buttonX0+" buttonX1:"+buttonX1
    						+" buttonY0:"+buttonY0+" buttonY1:"+buttonY1
    						+" x:"+event.getX()+" y:"+event.getY());
    				*/
    				if((event.getX() < buttonX0) || (event.getX() > buttonX1)
    						|| (event.getY() < buttonY0) || (event.getY() > buttonY1))
    				{
    					mHandler.obtainMessage(SEND_MESSAGE_FINISH).sendToTarget();;
    				}
    				break;
    			}
    			
				return false;

    		}
    	};

    	Button btn_volup = (Button)getActivity().findViewById(R.id.rc_key_channel_btn_vol_up);
    	btn_volup.setOnClickListener(buttonOnClickListener);
    	btn_volup.setOnTouchListener(buttonOnTouchListener);    	
    	
    	Button btn_voldown = (Button)getActivity().findViewById(R.id.rc_key_channel_btn_vol_down);
    	btn_voldown.setOnTouchListener(buttonOnTouchListener);
    	btn_voldown.setOnClickListener(buttonOnClickListener);
    	
    }
    
    /**
     * @param: boolean mlongClicked
     * @return: void
     * @description: set vol-&+ button background picture when it been pressed down or up
     * */
    
    public void setMoveDown(boolean mClicked)
    {
    	//longClicked = mClicked;
    	isMoveDown = mClicked;
    }
    
    /**
     * @param: Bundle savedInstanceState
     * @return: void
     * @description: call button touch listener initialization function when activity created
     * */
    public void onActivityCreated(Bundle savedInstanceState){
    	
    	ButtonlistenerInit();
    	super.onActivityCreated(savedInstanceState);
    }
    
}
