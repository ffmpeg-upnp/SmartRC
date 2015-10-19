package com.casky.remote.rc.kbmouse;


import com.casky.remote.rc.network.Client;
import com.casky.remote.widget.DashedLine;
import com.casky.remote.rc.main.MainFragmentRemote;
import com.casky.remote.rc.network.SoftKeyBoardListener;
import com.casky.smartremote.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
/**
 * 
*    
* 项目名称：SmartRemote   
* 类名称：FragmentMouse   
* 类描述：鼠标的Fragment类，包含滚轮   
* 创建人：shaojiansong   
* 创建时间＄1�714-8-25 下午2:23:25   
* 修改人：shaojiansong   
* 修改时间＄1�714-8-25 下午2:23:25   
* 修改备注＄1�7   
* 版本＄1�7 1.0   
*
 */
public class FragmentMouse extends Fragment{
	public static final String TAG = "RIME";
	private final float CURSOR_MOVE_SCALE = 0.8f;
	private final float WHEEL_SCALE = 0.3f;
	private int WHEEL_AREA_WIDTH = 80;
	private final int WAIT_MSEC = 100;	
	private int mScrollVal;
	private float mPreX;
	private float mPreY;
	private float mDx = 0.0f;
	private float mDy = 0.0f;
	private int mMouseSense = 50;
	private long mDownTime;
	private long nowTime;
	
	private int mDownX;
	private int mDownY;
	private DrawView draw = null;
	private ViewGroup root;
	/** Button for send 'Remote controller back message' to TV.*/
	private Button backButton; 
	 /** Button for send text in edit text to TV.*/
	private Button sendButton;
	/**Button for delete a letter on input box of TV side.*/
	private Button deleteButton;
	/**Button for send a enter signal to TV.*/
	private Button enterButton; 
	/**input box to input text which able to send to TV.*/
	private EditText et;
	/**object of soft keyboard listener.*/
	private SoftKeyBoardListener sbl = null;	
	private int screenWidth;
	private int screenHeight;
	private Bitmap scroll;
	private int scrollWidth;
	private int scrollHeight;
	private int dashLineWidth;
	private int dashLineHeight;
	private boolean hasMeasured = false;
	private float scrollX; 
	private com.casky.remote.widget.DashedLine dashedLine;
	private enum TOUCHSTATE {
		Wait,						
		SinglePress,				
		SinglePressMove,			
		SingleClick,
		Scroll,	
	}
	private TOUCHSTATE mTouchState = TOUCHSTATE.Wait;
    @Override
    public View onCreateView(LayoutInflater inflater, 
    ViewGroup container, Bundle savedInstanceState) {
    	//---Inflate the layout for this fragment---    
    	ViewGroup rootView = (ViewGroup) inflater.inflate(
    			R.layout.rc_fragment_mouse, container, false);
    	draw = new DrawView(getActivity()) ;
    	draw.setMinimumHeight(100);
    	draw.setMinimumWidth(100);
    	root = rootView;
    	scroll = BitmapFactory.decodeResource(getResources(), R.drawable.rc_mouse_scroll);
    	scrollWidth = scroll.getWidth();
    	scrollWidth = scrollWidth/2;
    	scrollHeight = scroll.getHeight();
    	scrollHeight = scrollHeight /2;
    	rootView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub

				if( mTouchState == TOUCHSTATE.Wait )
				{
					stateWait(arg1);
				}
				else if( mTouchState == TOUCHSTATE.SinglePress )
				{
					stateSinglePress(arg1);
				}
				else if( mTouchState == TOUCHSTATE.SinglePressMove )
				{
					stateSinglePressMove(arg1);
				}
				else if( mTouchState == TOUCHSTATE.Scroll)
				{
					stateScroll(arg1);
				}
			
				return true;
			}
		});
        return rootView;
    }
    
    private void ButtonInit(){
    	backButton = (Button) getActivity().findViewById(R.id.rc_frag_mouse_btn_back);
    	deleteButton = (Button) getActivity().findViewById(R.id.rc_frag_mouse_btn_delete);
    	enterButton = (Button) getActivity().findViewById(R.id.rc_frag_mouse_btn_enter);
    	sendButton = (Button) getActivity().findViewById(R.id.rc_frag_mouse_btn_send);
    	et = (EditText) getActivity().findViewById(R.id.rc_frag_mouse_et_inputmethod);
    }
    
    /**
     * Receive message from soft keyboard listener and setup edit Text ' visibility and input type.
     * */
    @SuppressLint("HandlerLeak")
	Handler editTextHandle = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
	    		case 0:
	    			et.setVisibility(View.INVISIBLE);
	    			break;
	    		default:
	    			et.setVisibility(View.VISIBLE);
	    		break;
    		}
    		et.setInputType(msg.what);    		
    		et.invalidate(); // 刷新界面
    		super.handleMessage(msg);
    	}
    };
    
    private void ButtonlistenerInit(){
    	
    	OnClickListener ocl = new OnClickListener(){
    		
			@Override
			public void onClick(View arg0) {
				
				if(MainFragmentRemote.sendClient == null){
					return;
				}
				switch(arg0.getId()){
					case R.id.rc_frag_mouse_btn_delete:
						MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(createMessage(Client.INPUTMETHOD_MESSAGE,"&delete"));
						break;						
					case R.id.rc_frag_mouse_btn_enter:
						MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(createMessage(Client.INPUTMETHOD_MESSAGE,"&enter"));						
						break;
					case R.id.rc_frag_mouse_btn_back:
						if (MainFragmentRemote.sendClient != null) {
							Integer RcID = Integer.parseInt(((Button)arg0).getTag().toString());
			        		String message = new String(RcID.toString());
			        		MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(createMessage(Client.REMOTE_MESSAGE,message));
						}
						else {
							return;
						}
					case R.id.rc_frag_mouse_btn_send:
						String strSendtoTv = et.getText().toString(); 
						MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(createMessage(Client.INPUTMETHOD_MESSAGE,"#"+strSendtoTv));
						et.setText("");
						break;
								
				}

			}
    		
    	};
    	
    	backButton.setOnClickListener(ocl);
    	enterButton.setOnClickListener(ocl);
    	deleteButton.setOnClickListener(ocl);
    	sendButton.setOnClickListener(ocl);
    	
    	/**
    	 * If focus on edit text, show enter button, delete button and send button.<br>
    	 * if not, clear edit text and invisible enter button, delete button and send button.
    	 * */
    	et.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View arg0, boolean hasfocus) {
				// TODO Auto-generated method stub
				if(hasfocus){

					if(sbl.getSoftKeyboardStatus() == true){
						enterButton.setVisibility(View.VISIBLE);
						deleteButton.setVisibility(View.VISIBLE);
						sendButton.setVisibility(View.VISIBLE);
					}else{
						throwoffFocus();
						et.setText("");
					}
				}else{
					et.setText("");
					enterButton.setVisibility(View.INVISIBLE);
					deleteButton.setVisibility(View.INVISIBLE);
					sendButton.setVisibility(View.INVISIBLE);
				}
				
			}});
   }
    
   /**
    * Create a listener to communicate with TV side
    * */
   private void startRecInfoFromTv(){
	   sbl = new SoftKeyBoardListener(editTextHandle,this.getActivity());
   }
   
   /**
    * Release the soft keyboard listener
    * */
   private void stopRecInfoFromTv(){
	   Log.d(TAG,"--------releaseListenStatus outer!--------------- ");
	   if(sbl != null){
		   sbl.releaseListenStatus();		   
	   }
   }
   
/**
 * send Ip address to tv side and wait response
 * */
   private void syncWithTvSide(){	     
	   startRecInfoFromTv();	    	   
   }
   /**
    * 状�1�7�切捄1�7
    * @param motionEvent
    */
   private void stateWait(MotionEvent motionEvent) {
	   
	   if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE)
		{
			mDownTime = System.currentTimeMillis();
			mDownX = (int)motionEvent.getX();
			mDownY = (int)motionEvent.getY();
			drawTouchBall(motionEvent);
			if( mDownX > screenWidth - WHEEL_AREA_WIDTH )
			{
				changeState( TOUCHSTATE.Scroll );
			}
			else
			{
				changeState( TOUCHSTATE.SinglePress );

			}
			throwoffFocus();
		}
	}
   /**
    * Move事件判断
    * @param motionEvent
    */
	private void stateSinglePress(MotionEvent motionEvent) 
	{
		long nowTime = System.currentTimeMillis();
		if( (nowTime - mDownTime) > WAIT_MSEC )
		{
			if (motionEvent.getAction() == MotionEvent.ACTION_MOVE )
			{
					
					int dx = mDownX - (int)motionEvent.getX();
					
					int dy = mDownY - (int)motionEvent.getY();
					
					if( ( dx*dx + dy*dy ) > 30 ){
						
						changeState( TOUCHSTATE.SinglePressMove );
						
				}
			}
			else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
				root.removeView(draw);
				changeState( TOUCHSTATE.Wait );
			}
		}
		else if( (motionEvent.getAction() == MotionEvent.ACTION_UP     ) ||
			(motionEvent.getAction() == MotionEvent.ACTION_CANCEL ) )
		{
			checkClickEvent();
			changeState(TOUCHSTATE.Wait);
			root.removeView(draw);
		}

	}
	/**
	 * Move消息的提取和发�1�7�1�7
	 * @param motionEvent
	 */
	private void stateSinglePressMove(MotionEvent motionEvent)
	{

			if( (motionEvent.getAction() == MotionEvent.ACTION_UP     ) ||
				(motionEvent.getAction() == MotionEvent.ACTION_CANCEL ) )
			{
				changeState(TOUCHSTATE.Wait);
				root.removeView(draw);
			}
			else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE )
			{

				nowTime = System.currentTimeMillis();
				mDownX = (int)motionEvent.getX();
				mDownY = (int)motionEvent.getY();
				if( (nowTime - mDownTime) > 100 )
				{
					if( mDownX > screenWidth - WHEEL_AREA_WIDTH ){
						
						changeState( TOUCHSTATE.Scroll );
						
					}else {
						
						mouseMove(motionEvent);
						
					}
				}
			}
	}
	
	private void changeState(TOUCHSTATE mState) {
		mTouchState = mState;
	}
	/**
	 * Scroll消息的提取和发�1�7�1�7
	 * @param motionEvent
	 */
	private void stateScroll(MotionEvent motionEvent)
	{
		if( (motionEvent.getAction() == MotionEvent.ACTION_UP) ||
			(motionEvent.getAction() == MotionEvent.ACTION_CANCEL ) )
		{
			changeState(TOUCHSTATE.Wait);
			dashedLine.setVisibility(View.INVISIBLE);
			root.removeView(draw);
		}
		else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE )
		{
			mDownX = (int)motionEvent.getX();
			mDownY = (int)motionEvent.getY();
			if( mDownX > screenWidth - WHEEL_AREA_WIDTH ){
				execScroll(motionEvent);
			}else {

				changeState(TOUCHSTATE.SinglePressMove);
			}

		}
	}
	public void onActivityCreated(Bundle savedInstanceState){
	    	
	    	ButtonInit();
	    	ButtonlistenerInit();  	    	    	    	    	
	    	getWindowMetric();
	    	//syncWithTvSide();
	    	dashedLine = (DashedLine) getActivity().findViewById(R.id.rc_frag_mouse_dl_dashedLine);
	    	//WHEEL_AREA_WIDTH = Helper.dip2px(getActivity(), dashedLine.getWidth());
	    	ViewTreeObserver vto = root.getViewTreeObserver();
	        /**
	         * 自定义控件宽高的获取
	         */
	    	vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
	        {
	            public boolean onPreDraw()
	            {
	                if (hasMeasured == false)
	                {

	                    dashLineHeight = dashedLine.getMeasuredHeight();
	                    dashLineWidth = dashedLine.getMeasuredWidth();   
	                    WHEEL_AREA_WIDTH = dashLineWidth;
	        	    	scrollX = (float)(screenWidth - dashLineWidth/2 - scrollWidth);
	                    Log.d("syo", "dashline width2 " + dashLineWidth);
	                    hasMeasured = true;

	                }
	                return true;
	            }
	        });
	    	super.onActivityCreated(savedInstanceState);
	    }
    private void getWindowMetric(){
    	DisplayMetrics dm = new DisplayMetrics();
    	getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
    	screenWidth = dm.widthPixels;
    	screenHeight = dm.heightPixels;
    	//Log.d(TAG,"screenWidth:"+screenWidth+" screenHeight:"+screenHeight);
    }
    public void onDestroyView() {
    	//hideTheSoftKeyBoard();
    	Log.d(TAG,"--------onDestroyView !--------------- ");
    	throwoffFocus();
    	stopRecInfoFromTv();
    	//tRec.interrupt();
    	super.onDestroy();
    }
    
    public void onStop() {
    	Log.d(TAG,"--------onStop !--------------- ");
    	throwoffFocus();
    	stopRecInfoFromTv();
    	super.onStop();
    }
    
    public void onPause() {
    	Log.d(TAG,"--------onPause !--------------- ");
    	throwoffFocus();
    	stopRecInfoFromTv();
    	super.onPause();
    }
    
    public void onResume(){
    	Log.d(TAG,"--------onResume !--------------- ");
    	syncWithTvSide();
    	super.onResume();
    }
    
    @Override  
    public void setUserVisibleHint(boolean isVisibleToUser) {  
        super.setUserVisibleHint(isVisibleToUser);  
        Log.d(TAG,"--------setUserVisibleHint !--------------- " + getUserVisibleHint()); 
    }  
    
	public static Message createMessage(int what, Object object) {

		Message message = new Message();

		message.what = what;

		message.obj = object;

		return message;
	}
	
	private void mouseMove(MotionEvent motionEvent)
	{
		String sendPosStr = "";

		float x = (float)motionEvent.getX();
		float y = (float)motionEvent.getY();
		updateTouchBall(x,y);
		float dx = (x - mPreX)*CURSOR_MOVE_SCALE;
		float dy = (y - mPreY)*CURSOR_MOVE_SCALE;
		int distance = (int) Math.sqrt(dx*dx + dy*dy);
		if( distance < 5 )
		{
			float low_val = 1.0f;
			mDx = dx * low_val + mDx * ( 1.0f - low_val );
			mDy = dy * low_val + mDy * ( 1.0f - low_val );
			dx = (int) mDx;
			dy = (int) mDy;
		}
		else if( distance < 10 )
		{
			dx *= 2;
			dy *= 2;
		}
		dx = dx * (mMouseSense / 100.0f + 0.8f );
		dy = dy * (mMouseSense / 100.0f + 0.8f );	
		int sx = (int)dx;
		int sy = (int)dy;
		sendPosStr = "0" +"#" + String.valueOf(sx) + "#" + String.valueOf(sy);
		if (MainFragmentRemote.sendClient == null) {
			return;
		}
		if(Math.abs(sx)< 60 && Math.abs(sy) <60){
			MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(createMessage(Client.MOUSE_MESSAGE,sendPosStr));
		}
		mPreX = (float)motionEvent.getX();
		mPreY = (float)motionEvent.getY();
	}
	
	private void execScroll(MotionEvent motionEvent){
		float x = scrollX;
		float y = (float)motionEvent.getY();
		updateTouchBall(x,y);
		mScrollVal = (int) ((mPreY - y) * WHEEL_SCALE);
		mPreY = (float)motionEvent.getY();
		if (MainFragmentRemote.sendClient == null) {
			return;
		}
		String sendPosStr = "3" +"#" + String.valueOf(0) + "#" + String.valueOf(mScrollVal);
		MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(createMessage(Client.MOUSE_MESSAGE,sendPosStr));
	}
	
	private void checkClickEvent() {
		if (MainFragmentRemote.sendClient == null) {
			return;
		}
		MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(createMessage(Client.MOUSE_MESSAGE,"1#0#0"));
		}	

	public void drawTouchBall(MotionEvent motionEvent){
		root.addView(draw);
		draw.currentx = (float)motionEvent.getX();
		draw.currenty = (float)motionEvent.getY();
	}
	public void updateTouchBall(float x, float y) {
		draw.currentx = x;
		draw.currenty = y;
		draw.invalidate();
	}
	/**
	 * 
	*    
	* 项目名称：SmartRemote   
	* 类名称：DrawView   
	* 类描述：跟随手指移动的小琄1�7   
	* 创建人：shaojiansong   
	* 创建时间＄1�714-8-25 下午2:27:59   
	* 修改人：shaojiansong   
	* 修改时间＄1�714-8-25 下午2:27:59   
	* 修改备注＄1�7   
	* 版本＄1�7 1.0  
	*
	 */
	@SuppressLint("DrawAllocation")
	public class DrawView  extends View{
        public float currentx = 200 ;
        public  float currenty = 300 ;
        private Canvas mCanvas;
        private Paint p;
        public DrawView(Context context) {
                super(context);
                // TODO Auto-generated constructor stub
        }
        @SuppressLint("NewApi")
		@Override
        protected void onDraw(Canvas canvas) {
                // TODO Auto-generated method stub
                super.onDraw(canvas);
                mCanvas = canvas;
                //创建画笔 ;
                p = new Paint() ;
                p.setARGB(255,0,191,255);
                p.setAntiAlias(true);
                p.setDither(true);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(12);
                //绘制丄1�7个小琄1�7 ＄1�7
                if (currentx < (screenWidth - WHEEL_AREA_WIDTH)) {
                    canvas.drawCircle(currentx, currenty, 70, p);
    				dashedLine.setVisibility(View.INVISIBLE);
				}else {
					//currentx = currentx - scrollWidth;
					currenty = currenty - scrollHeight;
					dashedLine.setVisibility(View.VISIBLE);
					canvas.drawBitmap(scroll, scrollX,currenty, p);
					//p.setStyle(Paint.Style.FILL);
					//p.setARGB(255,255,255,255);
					//canvas.drawCircle(currentx, currenty, 40, p);
				}
        }
        public void clearDraw() {
        	 //Paint paint = new Paint(); 
        	 p.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        	 mCanvas.drawPaint(p);
        	 p.setXfermode(new PorterDuffXfermode(Mode.SRC));
        	 invalidate();
		}
	}
	
/*
 * hide the soft keyboard
 * */
	void throwoffFocus(){
		if(getActivity() != null){
		if (getActivity().getCurrentFocus() != null) {  
            if (getActivity().getCurrentFocus().getWindowToken() != null) {  
            	
            	InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),  
                        InputMethodManager.HIDE_NOT_ALWAYS);  
                
                root.requestFocus();
                root.requestFocusFromTouch();
            }  
        }  

		}
	}
}
