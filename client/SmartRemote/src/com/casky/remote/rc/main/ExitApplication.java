package com.casky.remote.rc.main;
import java.util.LinkedList;
import java.util.List;

import com.casky.smartremote.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

class ExitThread implements Runnable
{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
        try {            
    	 	Thread.sleep(50);        
    	 } 
        catch (InterruptedException e) {
        }
        
		ExitApplication.getInstance().exit();
	}
	
}

 public class ExitApplication extends Application {

	 private List<Activity> activityList=new LinkedList<Activity>();
	 private static ExitApplication instance;
	
	 private ExitApplication()
	 {
	 }
	 
	 protected void dialog(Context context) {  
	     AlertDialog.Builder builder = new Builder(context);  
	     builder.setMessage(R.string.infobox_content);  
	     builder.setTitle(R.string.infobox_title); 
	     builder.setCancelable(false);
	     builder.setPositiveButton(R.string.infobox_OK,  
		     new android.content.DialogInterface.OnClickListener() {  
		         @Override  
		         public void onClick(DialogInterface dialog, int which) {  
		             dialog.dismiss();  
		             ExitThread et = new ExitThread();
		             Thread t = new Thread(et);
		             t.start();
		             if (MainFragmentRemote.sendClient != null) {
		            	 MainFragmentRemote.sendClient.closeClient();
					}
		         }
		     }   		 
	    ); 
	
	     builder.setNegativeButton(R.string.infobox_Cancel,  
		     new android.content.DialogInterface.OnClickListener() {  
		         @Override  
		         public void onClick(DialogInterface dialog, int which) {  
		             dialog.dismiss();  
		         }  
	     });  
	     builder.create().show();  
	 }  

	 /**
	  * Get ExitApplication instance
	  */
	 public static ExitApplication getInstance()
	 {
		 if(null == instance)
		 {
			 instance = new ExitApplication();
		 }
		 return instance;
	
	 }
	 
	 /**
	  * add Activity to container
	  */
	 public void addActivity(Activity activity)
	 {
		 activityList.add(activity);
	 }
	 
	 /**
	  * Complete all activity
	  * @param context
	  */
	 public void exit(Context context)
	 {
		 dialog(context);
	 }
	 public void exit()
	 {
		 
		 for(Activity activity:activityList)
		 {
			 activity.finish();
		 }
		
		 System.exit(0);
	
	 }
 }

