package com.casky.remote.setting;

import com.casky.smartremote.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;


public class WiperSwitch extends View implements OnTouchListener{
	private Bitmap bg_on, bg_off, slipper_btn;
	public boolean vibraDetective=false;
	/**
	 * 按下时的x和当前的x
	 */
	private float downX, nowX;
	
	/**
	 * 记录用户是否在滑动
	 */
	private boolean onSlip = false;
	
	/**
	 * 当前的状态
	 */
	private boolean nowStatus = false;
	
	/**
	 * 监听接口
	 */
	private OnChangedListener listener;
	private static final String Tag = "WIPER";
	private Paint paint = new Paint();
	private float x = 0;
	private float WiperOffsetX = 30;
	private float WiperOffsetY = 13;
	private float TxtOffsetY = 40;
	private float TxtOffsetX = 40;
	private int TxtSize = 40;
	private boolean oldStatus;
	Rect dst = new Rect();
	public WiperSwitch(Context context) {
		super(context);
		init();
	}
	
	public WiperSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Log.d(Tag, "INIT 2 para -> TxtOffsetX :"+TxtOffsetX);
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.WiperSwitch); 
		TxtOffsetX = a.getFloat(R.styleable.WiperSwitch_txtofsx, TxtOffsetX); 
		TxtOffsetY = a.getFloat(R.styleable.WiperSwitch_txtofsy, TxtOffsetY);
		TxtSize = a.getInt(R.styleable.WiperSwitch_txtsize, TxtSize);
		WiperOffsetY = a.getFloat(R.styleable.WiperSwitch_wiperofsy, WiperOffsetY);
		
		a.recycle();
		Log.d(Tag, "INIT 2 para -> TxtOffsetX :"+TxtOffsetX);
		init();		
	}
	public void init(){
		bg_on = BitmapFactory.decodeResource(getResources(), R.drawable.setting_item_on_btn);
		bg_off = BitmapFactory.decodeResource(getResources(), R.drawable.setting_item_off_btn);
		slipper_btn = BitmapFactory.decodeResource(getResources(), R.drawable.setting_item_slipper_white_btn);
		WindowManager wm = (WindowManager) getContext() 
                .getSystemService(Context.WINDOW_SERVICE); 
	    DisplayMetrics dm = new DisplayMetrics();
	    wm.getDefaultDisplay().getMetrics(dm);
	    WiperOffsetX =0;
	    nowX = WiperOffsetX;
		setOnTouchListener(this);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (nowX < WiperOffsetX + (bg_on.getWidth()/2)){
			canvas.drawBitmap(bg_off , WiperOffsetX,WiperOffsetY, paint);//

		}else{
			canvas.drawBitmap(bg_on, WiperOffsetX,WiperOffsetY, paint);//

		}
		
		if (onSlip) { 
			if(nowX >= WiperOffsetX + bg_on.getWidth())
				x = WiperOffsetX + bg_on.getWidth() - slipper_btn.getWidth()/2;
			else
				x = nowX - slipper_btn.getWidth()/2;
		}else {
			if(nowStatus){
				x = WiperOffsetX + bg_on.getWidth() - slipper_btn.getWidth();
			}else{
				x = WiperOffsetX;
			}
		}
		
		if (x < WiperOffsetX ){
			x = WiperOffsetX;
		}
		else if(x > (WiperOffsetX + bg_on.getWidth() - slipper_btn.getWidth())){
			x = WiperOffsetX + bg_on.getWidth() - slipper_btn.getWidth();
		}
		canvas.drawBitmap(slipper_btn, x, WiperOffsetY, paint); 
		onSlip=false;

	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
			{
				if (event.getX() > WiperOffsetX + bg_off.getWidth() || 
							event.getY() > WiperOffsetY + bg_off.getHeight()||
							event.getX() < WiperOffsetX
							){
						return false;
				}else{
						onSlip = true;
						downX = event.getX();
						nowX = downX;
				}
					break;
			}
			case MotionEvent.ACTION_MOVE:
			{
				oldStatus=nowStatus;
				onSlip = true;
				nowX = event.getX();
					if(nowX >= WiperOffsetX + bg_on.getWidth()/2)
						nowStatus = true;
					else
						nowStatus = false;
				if(nowStatus != oldStatus)
				{
					if(listener != null)
					{
						listener.OnChanged(WiperSwitch.this, nowStatus);
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				onSlip = false;
				if(event.getX() >= (WiperOffsetX + bg_on.getWidth()/2)){
					nowX = WiperOffsetX + bg_on.getWidth() - slipper_btn.getWidth();
					nowStatus = true;
				}else{
					nowX = 0;
					nowStatus = false;
				}
				if(listener != null){
					listener.OnChanged(WiperSwitch.this, nowStatus);
				}
				break;
			}
		 }
			invalidate();
		return true;
	}
	

	
	/**
	 * 为WiperSwitch设置一个监听，供外部调用的方法
	 * @param listener
	 */
	public void setOnChangedListener(OnChangedListener listener){
		this.listener = listener;
	}
	//==========================================================================
	/**
	 * 设置滑动开关的初始状态，供外部调用
	 * @param checked
	 */
	public void setChecked(boolean checked){
		if(checked){
			nowX = WiperOffsetX + bg_off.getWidth();//on
		}else{
			nowX = WiperOffsetX;//off
		}
		nowStatus = checked;
	}

	
    /**
     * 回调接口
     * @author phc
     *
     */
	public interface OnChangedListener {
		public void OnChanged(WiperSwitch wiperSwitch, boolean checkState);
	}


}
