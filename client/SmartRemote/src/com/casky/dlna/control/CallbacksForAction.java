package com.casky.dlna.control;

import com.casky.dlna.control.DlnaCommandManager.LocalPlayCallback;
import com.casky.dlna.control.DlnaCommandManager.LocalStopCallback;

import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;

/**
 * 
*    
* 项目名称：Smart_DLNA   
* 类名称：CallbacksForAction   
* 类描述：   
* 创建人：shaojiansong   
* 创建时间：2014-9-23 下午1:54:20   
* 修改人：shaojiansong   
* 修改时间：2014-9-23 下午1:54:20   
* 修改备注：   
* 版本： 1.0   
*
 */
public interface CallbacksForAction {
	public void avSetTransportActionCallback(boolean success, UpnpResponse rsp, String defaultMsg);
	public void playActionCallback(boolean success, UpnpResponse rsp, String defaultMsg, LocalPlayCallback playCallback);
	public void seekActionCallback(boolean success, UpnpResponse rsp, String defaultMsg);
	public void pauseActionCallback(boolean success, UpnpResponse rsp, String defaultMsg);
	public void resumeActionCallback(boolean success, UpnpResponse rsp, String defaultMsg);
	public void stopActionCallback(boolean success, UpnpResponse rsp, String defaultMsg, LocalStopCallback stopCallback);
	public void getPositionInfoActionCallback(boolean success, UpnpResponse rsp, String defaultMsg, PositionInfo positionInfo);
	public void getTransportInfoActionCallback(boolean success, UpnpResponse rsp, String defaultMsg, TransportInfo transportInfo);
	public void getVolumeActionCallback(boolean success, UpnpResponse rsp, String defaultMsg, int currentVol);
	public void setVolumeActionCallback(boolean success, UpnpResponse rsp, String defaultMsg);
	public void getMuteActionCallback(boolean success, UpnpResponse rsp, String defaultMsg);
	public void getMuteActionCallback(boolean success, boolean currentMute);
	public void setMuteActionCallback(boolean success, UpnpResponse rsp, String defaultMsg);
	public void getMediaInfoInfoActionCallback(boolean success, UpnpResponse rsp, String defaultMsg, MediaInfo mediaInfo);
	public void getCurrentTransportActionCallback(boolean success, UpnpResponse rsp, String defaultMsg, TransportAction[] actions);
}
