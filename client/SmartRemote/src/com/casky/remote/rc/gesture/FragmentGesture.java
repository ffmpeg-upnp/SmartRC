package com.casky.remote.rc.gesture;


import java.util.ArrayList;

import com.casky.remote.rc.main.MainFragmentRemote;
import com.casky.remote.rc.network.Client;
import com.casky.smartremote.R;
import com.casky.remote.utils.Helper;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
class AnimationProp{
	ImageView img;
	int id;
	String cmd;
}
public class FragmentGesture extends Fragment {
	private String TAG = "mylog"; 
	private GestureLibrary libraries;
	private ImageView ok_img;
	private ImageView right_img;
	private ImageView left_img;
	private ImageView up_img;
	private ImageView down_img;
	private ImageView return_img;
	private static float ori_x = 0;
	private static float ori_y = 0;
	private static float des_y = 0;
	private static float des_x = 0;
	private final float DistanceOfOk = 20;

	private enum GesResult{
		gOk,gRight,gLeft,gUp,gDown,gReturn,gUnknown
	};
	
    @Override
    public View onCreateView(LayoutInflater inflater, 
    ViewGroup container, Bundle savedInstanceState) {
    	Log.d(TAG, "onCreateView");
    	//---Inflate the layout for this fragment---    
    	ViewGroup rootView = (ViewGroup) inflater.inflate(
    			R.layout.rc_fragment_gesture, container, false);
    	libraries = GestureLibraries.fromRawResource(getActivity(), R.raw.gestures);
    	libraries.load();
    	GestureOverlayView overlayView = (GestureOverlayView)rootView.findViewById(R.id.rc_frag_gesture_gov_gesture_overlay);
    	overlayView.addOnGestureListener(new GestureListener());
    	
    	ok_img = (ImageView)rootView.findViewById(R.id.rc_frag_gesture_iv_ok);
    	right_img = (ImageView)rootView.findViewById(R.id.rc_frag_gesture_iv_rightarrow);
    	left_img = (ImageView)rootView.findViewById(R.id.rc_frag_gesture_iv_leftarrow);
    	up_img = (ImageView)rootView.findViewById(R.id.rc_frag_gesture_iv_uparrow);
    	down_img = (ImageView)rootView.findViewById(R.id.rc_frag_gesture_iv_downarrow);
    	return_img = (ImageView)rootView.findViewById(R.id.rc_frag_gesture_iv_return);
        return rootView;
    }
    

    
    private final class GestureListener implements OnGestureListener{
    	
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {

            Log.i(TAG,"onGestureStarted()");
            ori_x = event.getX();
            ori_y = event.getY();     
            
        }

        public void onGesture(GestureOverlayView overlay, MotionEvent event) {        	
        	 Log.i(TAG,"onGesture()");
        }

        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {

            Log.i(TAG,"onGestureEnded()"+"overlay.getGesture()"+overlay.getGesture());
            des_x = event.getX();
            des_y = event.getY();               	
            GestureMainProcesor(GestureHandler(overlay.getGesture()));            
            overlay.cancelGesture();
            overlay.clear(true);
        }



		public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
            Log.i(TAG,"onGestureCancelled()");          
        }        
   }
    
   /**
    * Setup animation property according to gesture
    */ 
    private AnimationProp GetAnimPropertyAccordingGes(GesResult ges){
    	AnimationProp animp = new AnimationProp();
    	switch(ges){
		case gOk:
			animp.id = R.anim.gesok;
			animp.img = ok_img;
			animp.cmd = getString(R.string.TREQ_RC_KEY_OK);
			break;
		case gLeft:
			animp.id = R.anim.gesleft;
			animp.img = left_img;
			animp.cmd = getString(R.string.TREQ_RC_KEY_Left);
			break;
		case gRight:
			animp.id = R.anim.gesright;
			animp.img = right_img;
			animp.cmd = getString(R.string.TREQ_RC_KEY_Right);
			break;
		case gUp:
			animp.id = R.anim.gesup;
			animp.img = up_img;
			animp.cmd = getString(R.string.TREQ_RC_KEY_Up);
			break;
		case gDown:
			animp.id = R.anim.gesdown;
			animp.img = down_img;
			animp.cmd = getString(R.string.TREQ_RC_KEY_Down);
			break;
		case gReturn:
			animp.id = R.anim.gesreturn;
			animp.img = return_img;
			animp.cmd = getString(R.string.TREQ_RC_KEY_Back);
			break;
		default:
			break;
	}
    	return animp;
    }
    
    /**
     * Setup animation according to animation property 
     * and start animation
     * 
     * */
    private void SetupAnimation(final AnimationProp animproperty){
    	Animation anim;
    	int id = 0; 

    	id = animproperty.id;
    	anim = AnimationUtils.loadAnimation(getActivity(),id);
    	anim.setAnimationListener(new AnimationListener() {
             
             @Override
             public void onAnimationStart(Animation animation) {
                 
                 //动画弄1�7始时让View可见
            	
            	 animproperty.img.setVisibility(View.VISIBLE);
             }
             
             //当动画重复播放时的事仄1�7
             @Override
             public void onAnimationRepeat(Animation animation) {                        
                 
             }
             
             @Override
             public void onAnimationEnd(Animation animation) {
                 
                 //动画结束时让View隐藏
            	 animproperty.img.setVisibility(View.GONE);
            	 
             }
         });
    	animproperty.img.startAnimation(anim);
    }
    
    /**
     * Handler for Gesture
     * First search the gesture library to find it matches a complicated gesture  
     * if not, use simple gesture handler to judge the gesture
     * */
    
    private GesResult GestureHandler(Gesture gesture)
    {
    	GesResult gesr = GesResult.gUnknown;
    	
    	gesr = ComplicateGestureHandler(gesture);
    	Log.i(TAG,"GestureHandler gesr:" + gesr);
    	if( gesr == GesResult.gUnknown){
    		return SimpleGestureHandler();
    	}else{
    		return gesr;
    	}
    }
    
    /**
     * Handler for Simple Gesture, such as OK UP DOWN LEFT RIGHT
     * If both  x axis and y axis < DistanceOfOk, gesture is OK
     * else If |x axis| > |y axis| && x axis > 0 gesture is Right   
     * else If |x axis| > |y axis| && x axis < 0 gesture is Left
     * else If |x axis| < |y axis| && y axis < 0 gesture is Up
     * else If |x axis| < |y axis| && y axis > 0 gesture is Down
     */
    
    private GesResult SimpleGestureHandler() {
    	float axis_x = 0;
    	float axis_y = 0;
    	axis_x = des_x - ori_x;
    	axis_y = des_y - ori_y;
    	float dist_x = Math.abs(axis_x);
    	float dist_y = Math.abs(axis_y);
    	GesResult gesr = GesResult.gOk;
    	if(dist_x < DistanceOfOk && dist_y < DistanceOfOk){
    		gesr = GesResult.gOk;
		 }else{
			if(dist_x > dist_y){
				if(dist_x == axis_x){
					gesr = GesResult.gRight;
				}else{
					gesr = GesResult.gLeft;
				}
			}else{
				if(dist_y == axis_y){
					gesr = GesResult.gDown;
				}else{
					gesr = GesResult.gUp;
				}
			} 
			
		}
    		Log.i(TAG,"SimpleGestureHandler gesr:" + gesr);
    		return gesr;
   }
        
    private void SendGestureMessage(String message)
    {
    	if (MainFragmentRemote.sendClient != null) {
    		MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(Helper.createMessage(Client.REMOTE_MESSAGE, message));
    	}
    }
    
    /**
     * Setup animation according to gesture 
     * Send gesture message to server 
     * 
     */    
    private void GestureMainProcesor(GesResult ges)
    {
    	AnimationProp animP = GetAnimPropertyAccordingGes(ges);
    	SetupAnimation(animP);
    	SendGestureMessage(animP.cmd);
    }
    
    /**
     * Get gesture from gesture library
     * the default prediction is  6 ---60% and the rang is 0 - 10 
     */
    private GesResult ComplicateGestureHandler(Gesture gesture) {
        
        ArrayList<Prediction> predictions = libraries.recognize(gesture);
        if (predictions.isEmpty()) {
            
        } else {
            
            Prediction prediction = predictions.get(0);
            double score = prediction.score; 
            if (score >= 6) { 
               if(prediction.name.equals("return")){
            	   return GesResult.gReturn;
               }                
            } else {
                return GesResult.gUnknown;
            }
        }
        return GesResult.gUnknown;
    }

}
