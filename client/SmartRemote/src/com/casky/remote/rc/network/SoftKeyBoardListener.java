package com.casky.remote.rc.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.casky.remote.rc.main.MainFragmentRemote;
import com.casky.remote.utils.Helper;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 *Listen to TV side to get soft keyboard type, <br>
 *refresh soft keyboard type according to TV side. <br> 
 * */
class getTvStatusInformation implements Runnable{

	private DatagramSocket imeSocket;
	private DatagramPacket imeRecdata;
	private static final String TAG = "RIME";
	private TvStatus ts;
	byte[] message = new byte[16];
	/**data received from TV side.*/
	String iData = null;
	/**visible status of input box on phone size.*/
	private boolean aci = false;
	
	getTvStatusInformation(TvStatus ts){
		imeSocket = ts.getImeSoket();
		this.ts = ts;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{			
			imeRecdata = new DatagramPacket(message, message.length);
			try {
				while (!imeSocket.isClosed()) {
					for (int i=0;i<message.length;i++){
						message[i]=0;
					}

					Log.d(TAG,"--------Start Listen !--------------- ");
					/**Start listen to the TV side*/
					imeSocket.receive(imeRecdata);
					/**send feedback signal to TV side*/
					ts.feedBacktoTv();
					iData = new String(imeRecdata.getData());
					iData = iData.trim();
					Log.d(TAG,"---------iData : " + iData);
					if("0".equals(iData)){
						aci = false;/**means input box of TV side has no focus.*/
					}else{
						aci = true; /**means TV side focus on input box*/
					}
					ts.setSoftKeyboardStatus(aci);
					Message message = new Message();
					message.what =  Integer.parseInt(iData);
					// 鍙戦�佹秷鎭�
					ts.getRefreshHandler().sendMessage(message);/**send message to UI part to refresh type of soft keyboard.*/
					Thread.sleep(500);
				}
			}catch(Exception e){
				ts.closeImeSocket();
				imeRecdata = null;
				Log.d(TAG,"--------Exception !---------------: "+e);

			}finally{
				ts.closeImeSocket();
				imeRecdata = null;
				ts = null;
				Log.d(TAG,"--------Finally !--------------- ");									
			}
		}catch(Exception e){
			
		}

	}
}

/**
 * a storage class to store status information both TV side and UI side. <br>
 * Supply a interface to send Local IP to TV. <br>
 * send '@+IP address' to TV side as an active signal when create. <br>
 * Send '!+IP address' to TV side as an release signal when destroy. <br>
 */
class TvStatus{
	private boolean softKeyboardStatus;/**enable flag of input box on TV side*/
	private DatagramSocket imeSocket; /**UDP socket for listen to TV side*/
	private Handler refHandle; /**handler for refreshing soft keyboard type of phone side*/
	private String ipaddr;/**record IP address of phone side*/
	private Context myContext; /**Context of current activity*/
	private static final String TAG = "RIME";
	/**
	 * signal type to send to TV side <br>
	 * iCreat : @+IP address, means the mouse fragment has been active <br>
	 * iRelease : ! + IP address, means the mouse fragment has been deactive(destroy,pause,stop) <br>
	 * iConfirm: ^ + IP address, when received a message from TV side, feedback this to TV side <br>
	 */
	private enum ipCommand{
		iCreat,iRelease,iConfirm
	};
	
	TvStatus(Handler refHandle,Context ctt){
		this.refHandle = refHandle;
		this.myContext = ctt;
		softKeyboardStatus = false;
		sendIpToTv(ipCommand.iCreat);
		try {
			imeSocket = new DatagramSocket(7878);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private  Message createMessage(int what, Object object) {

		Message message = new Message();

		message.what = what;

		message.obj = object;

		return message;
	}
	
	/**
	 * get soft keyboard enable status. if true, there should be an edit text on mouse fragment. 
	 * */
	boolean  getSoftKeyboardStatus(){
		return softKeyboardStatus;
	}
	
	void setSoftKeyboardStatus(boolean sks){
		softKeyboardStatus = sks;			
	}
	
    public void closeImeSocket(){
    	if(imeSocket != null){
			imeSocket.close();
			imeSocket = null;
		}
    }
    
    public DatagramSocket getImeSoket(){
    	return imeSocket;
    }
    
    public Handler getRefreshHandler(){
    	return refHandle;
    }
    
    void feedBacktoTv(){
    	sendIpToTv(ipCommand.iConfirm);
    }
    
    /**
     * send signal to TV side with IP address according to command.
     * */
	void sendIpToTv(ipCommand stosp){

		   ipaddr = Helper.getDhcpIpString(myContext);
		   Log.d(TAG, "ipaddr : " + ipaddr + "MainActivity.sendClient" + MainFragmentRemote.sendClient);
		   if((ipaddr != null) && (MainFragmentRemote.sendClient != null)){	
			   Log.d(TAG, "ipaddr : " + ipaddr);
			   switch(stosp){
			     case iCreat:
			    	 MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(createMessage(Client.INPUTMETHOD_MESSAGE,"@"+ipaddr));
			    	 break;
			     case iRelease:
			    	 MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(createMessage(Client.INPUTMETHOD_MESSAGE,"!"+ipaddr));
			    	 break;	
			     case iConfirm:
			    	 MainFragmentRemote.sendClient.getNetWorkHandler().sendMessage(createMessage(Client.INPUTMETHOD_MESSAGE,"^"+ipaddr));
			    	 break;	 	
			   }

		   }
	}
	
	/**
	 * send release signal to TV side, tell TV side this phone does not need the soft keyboard type anymore.  
	 * */
	void release(){
		sendIpToTv(ipCommand.iRelease);
	}
}

/**
 *Soft Key Board listener Class, <br> 
 *tS : record the status of input box on TV side,  <br>
 *tRec: handler to communicate with both TV side and UI side 
 */
public class SoftKeyBoardListener {
	
	private TvStatus tS; 
	private Thread tRec = null;
	private static final String TAG = "RIME";
	

	/**
	 * Close the socket which listen to TV side for soft keyboard type.
	 * release status resource for the listener.
	 * */
	public void releaseListenStatus(){
		if(tRec == null){
			return;
		}
		Log.d(TAG,"--------releaseListenStatus Inner!--------------- ");	
		if(tS != null){
			tS.closeImeSocket();
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tS.release();
		tS = null;
		tRec = null;
		
	}
	
	/**
	 *get soft keyboard enable flag from status storage. 
	 * */
	public boolean getSoftKeyboardStatus(){
		if(tS != null){
			return tS.getSoftKeyboardStatus();
		}else{
			return false;
		}
	}
	
/**
 * Create Listener for listen to the soft keyboard status of TV side.<br>
 * @param refHandle
 * handler for refreshing soft keyboard type of phone side.<br>  
 * @param ctt
 * Context of current activity.
 * */			
	public SoftKeyBoardListener(Handler refHandle,Context ctt){
		if(tRec != null){
			return;
		}
				
		tS = new TvStatus(refHandle,ctt);
		tRec = new Thread(new getTvStatusInformation(tS));
		tRec.start();		
	}
}
