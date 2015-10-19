package com.casky.dlna.control;

import android.os.Message;
import android.util.Log;

import com.casky.dlna.control.DlnaCommandManager.LocalPlayCallback;
import com.casky.dlna.control.DlnaCommandManager.LocalStopCallback;
import com.casky.dlna.main.DLNAFragmentFactory;
import com.casky.dlna.music.MainFragmentMusic;
import com.casky.dlna.utils.UriBuilder;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.GetCurrentTransportActions;
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.messagebox.parser.MessageElement;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.renderingcontrol.callback.GetMute;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;

/**
 * 
*    
* 项目名称：Smart_DLNA   
* 类名称：DlnaServiceManager   
* 类描述：   
* 创建人：shaojiansong   
* 创建时间＄1�714-9-23 下午1:54:53   
* 修改人：shaojiansong   
* 修改时间＄1�714-9-23 下午1:54:53   
* 修改备注＄1�7   
* 版本＄1�7 1.0   
*
 */
public class DlnaServiceManager{
	private DeviceDisplay mDeviceDisplay;
	private String id;
	private AndroidUpnpService mAndroidUpnpService;
    private Service<?, ?> mAvTransportService;
    private Service<?, ?> mRenderingControlService;
    private Service<?, ?> mConnectmanagerService;
    private UriBuilder.MediaType nMediaType;
    private String nMetaDataString;
    private DlnaCommandManager mDlnaCommandManager;

    private static final String TAG = "DlnaServiceManager";
    
    private static boolean isDLNAPlaying = false;
    
	public DlnaServiceManager(AndroidUpnpService androidUpnpService, String metaData){
		
		this.mAndroidUpnpService = androidUpnpService;
		this.nMetaDataString = metaData;
		this.mDlnaCommandManager = DlnaCommandManager.getInstance();
	}
	
//	public void setDataSource(DeviceDisplay deviceDisplay, UriBuilder.MediaType mediaType, String mediaId, String metaData){
//		
//		this.mDeviceDisplay = deviceDisplay;
//		this.nMediaType = mediaType;
//		this.id = mediaId;
//		this.nMetaDataString = metaData;
//		this.mAvTransportService = mDeviceDisplay.getDevice().findService(new UDAServiceType("AVTransport"));
//		this.mRenderingControlService = mDeviceDisplay.getDevice().findService(new UDAServiceType("RenderingControl"));
//		this.mConnectmanagerService = mDeviceDisplay.getDevice().findService(new UDAServiceType("ConnectionManager"));
//	}
	
	public void setPushMediaData(UriBuilder.MediaType mediaType, String mediaId, String metaData){
		this.nMediaType = mediaType;
		this.id = mediaId;
		this.nMetaDataString = metaData;
	}
	
	public void setRenderDevice(DeviceDisplay deviceDisplay){
		this.mDeviceDisplay = deviceDisplay;
		this.mAvTransportService = mDeviceDisplay.getDevice().findService(new UDAServiceType("AVTransport"));
		this.mRenderingControlService = mDeviceDisplay.getDevice().findService(new UDAServiceType("RenderingControl"));
		this.mConnectmanagerService = mDeviceDisplay.getDevice().findService(new UDAServiceType("ConnectionManager"));
		Log.e(TAG, "mDeviceDisplay  = "+ mDeviceDisplay.getDevice().toString());
	}
	
    public void setFinishCallback(DlnaCommandManager.LocalFinishCallback finishCallback){
        mDlnaCommandManager.setFinishCallback(finishCallback);
    }

    public boolean isDLNAPlaying(){
        return isDLNAPlaying;
    }

	public void setAVTransport(final String id,final String metadata, final CallbacksForAction callbacksForAction,final LocalPlayCallback playCallback){
		
		if (mAvTransportService != null) {
			
			String url = UriBuilder.buildUriForType(nMediaType, id);
			Log.d(TAG, "url:" + url + "metadata " + metadata);
			
			ActionCallback setAVTransportURIAction = new SetAVTransportURI(mAvTransportService,url,metadata) {
				
				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
					// TODO Auto-generated method stub
					Log.d(TAG, "SetAVTransportURI failure");
					callbacksForAction.avSetTransportActionCallback(false, arg1, arg2);
				}
				
				@Override
				public void success(ActionInvocation invocation) {
					Log.d(TAG, "SetAVTransportURI success");
					super.success(invocation);
					_play(playCallback);
				}
			};
			mAndroidUpnpService.getControlPoint().execute(setAVTransportURIAction);
		}
	}

    public void initDLNASeekBar(SeekBarController.SeekBarModule module,DlnaManager manager){
        mDlnaCommandManager.initSeekBar(module,manager);
    }

    public void play(final LocalPlayCallback playCallback){
        setAVTransport(id,nMetaDataString,mDlnaCommandManager,playCallback);
    }
	
	private void _play(final LocalPlayCallback playCallback){

		if (mAvTransportService != null) {
			ActionCallback playAction = new Play(mAvTransportService) {
				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1,
						String arg2) {
					Log.d(TAG, "play failure");
					mDlnaCommandManager.playActionCallback(false, arg1, arg2,playCallback);

				}
				@Override
				public void success(ActionInvocation invocation) {
					Log.d(TAG, "play success");
					super.success(invocation);
					mDlnaCommandManager.playActionCallback(true, null, null,playCallback);
                    isDLNAPlaying = true;

				}
			};
			mAndroidUpnpService.getControlPoint().execute(playAction);
		}

	}
	public void pause(){
		
		if (mAvTransportService != null) {
			
			ActionCallback pauseAction = new Pause(mAvTransportService) {
				
				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
					// TODO Auto-generated method stub
					Log.d(TAG, "pause failure");
					mDlnaCommandManager.pauseActionCallback(false, arg1, arg2);
				}
				public void success(ActionInvocation invocation){
					Log.d(TAG, "pause success");
					super.success(invocation);
					mDlnaCommandManager.pauseActionCallback(true, null, null);
				}
			};
			mAndroidUpnpService.getControlPoint().execute(pauseAction);
		}
	}
	public void resume(){
		
		if (mAvTransportService != null) {
			
			ActionCallback playAction = new Play(mAvTransportService) {
				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1,
						String arg2) {
					Log.d(TAG, "resume failure");
					mDlnaCommandManager.resumeActionCallback(true, arg1, arg2);
				}
				@Override
				public void success(ActionInvocation invocation) {
					Log.d(TAG, "resume success");
					super.success(invocation);
					mDlnaCommandManager.resumeActionCallback(true, null, null);
				}
			};
			mAndroidUpnpService.getControlPoint().execute(playAction);
            isDLNAPlaying = true;
		}

	}
	public void stop(final LocalStopCallback stopCallback){
		
		if (mAvTransportService != null) {
			
			ActionCallback stopAction = new Stop(mAvTransportService) {
				
				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
					// TODO Auto-generated method stub
					Log.d(TAG, "stop failure");
					mDlnaCommandManager.stopActionCallback(false, arg1, arg2,stopCallback);
				}
				@Override
				public void success(ActionInvocation invocation) {
					Log.d(TAG, "stop success");
					super.success(invocation);
					mDlnaCommandManager.stopActionCallback(true, null, null,stopCallback);
                    isDLNAPlaying=false;
				}
			};
			mAndroidUpnpService.getControlPoint().execute(stopAction);
		}

	}
	public void seek(String seekVal){
		
		if (mAvTransportService != null) {
            mDlnaCommandManager.seekRunnbaleDelay();
            Log.d(TAG,"seekVal=" + seekVal);
			ActionCallback seekAction = new Seek(mAvTransportService,SeekMode.ABS_TIME,seekVal) {
				
				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
					// TODO Auto-generated method stub
					Log.e(TAG, "seek failure");
					mDlnaCommandManager.seekActionCallback(false, arg1, arg2);
				}
				@Override
				public void success(ActionInvocation invocation) {
					Log.e(TAG, "seek success");
					mDlnaCommandManager.seekActionCallback(true, null, null);
				}
			};
			mAndroidUpnpService.getControlPoint().execute(seekAction);
		}

	}
	public void getTransportInfo() {
		
		if (mAvTransportService != null) {
			
			ActionCallback getTransportInfo = new GetTransportInfo(mAvTransportService) {

					@Override
					public void received(ActionInvocation arg0,
							TransportInfo arg1) {
						mDlnaCommandManager.resumeActionCallback(true, null, null);
					}

					@Override
					public void failure(ActionInvocation arg0,
							UpnpResponse arg1, String arg2) {
						// TODO Auto-generated method stub
						mDlnaCommandManager.resumeActionCallback(false, arg1, arg2);
					}
			};
			mAndroidUpnpService.getControlPoint().execute(getTransportInfo);
		}

	}
	public void setVolume(int volumeValue) {
		if (mRenderingControlService != null) {
			ActionCallback setVolume = new SetVolume(mRenderingControlService,
					volumeValue) {

				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1,
						String arg2) {
					mDlnaCommandManager.setVolumeActionCallback(false, arg1, arg2);
				}

				@Override
				public void success(ActionInvocation invocation) {
					super.success(invocation);
					mDlnaCommandManager.setVolumeActionCallback(true, null, null);
				}

			};
			mAndroidUpnpService.getControlPoint().execute(setVolume);
		}

	}
	public void getVolume() {
		if (mRenderingControlService != null) {
			ActionCallback getVolume = new GetVolume(mRenderingControlService) {

				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1,
						String arg2) {
					super.failure(actionInvocation,arg1);
				}

				@Override
				public void success(ActionInvocation invocation) {
					super.success(invocation);
				}

				@Override
				public void received(ActionInvocation arg0, int currentVolume) {
					// TODO Auto-generated method stub
					mDlnaCommandManager.getVolumeActionCallback(true, null, null,currentVolume);
				}

			};
			mAndroidUpnpService.getControlPoint().execute(getVolume);
		}

	}
	public void getMute() {
		if (mRenderingControlService != null) {
			ActionCallback getMuteAction = new GetMute(mRenderingControlService) {

				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1,
						String arg2) {
					mDlnaCommandManager.getMuteActionCallback(false, arg1, arg2);
				}

				@Override
				public void received(ActionInvocation arg0, boolean arg1) {
					mDlnaCommandManager.getMuteActionCallback(true, arg1);
				}
			};

			mAndroidUpnpService.getControlPoint().execute(getMuteAction);
		}

	}
	public void setMute(boolean isMute) {
		if (mRenderingControlService != null) {
			ActionCallback setMuteAction = new SetMute(mRenderingControlService,
					isMute) {

				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1,
						String arg2) {
					mDlnaCommandManager.setMuteActionCallback(false, arg1, arg2);
				}

				@Override
				public void success(ActionInvocation invocation) {
					mDlnaCommandManager.setMuteActionCallback(true, null, null);
					super.success(invocation);
				}

			};
			mAndroidUpnpService.getControlPoint().execute(setMuteAction);
		}

	}
	public void getPositionInfo() {
		if (mAvTransportService != null) {
			ActionCallback getPositionInfoAction = new GetPositionInfo(
					mAvTransportService) {

				@Override
				public void received(ActionInvocation invocation,
						PositionInfo positionInfo) {
					mDlnaCommandManager.getPositionInfoActionCallback(true, null,
							"received", positionInfo);
				}

				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1,
						String arg2) {
					mDlnaCommandManager.getPositionInfoActionCallback(false, arg1, arg2,
							null);
				}
			};
			mAndroidUpnpService.getControlPoint().execute(getPositionInfoAction);
		}

	}

	public void getMediaInfo() {
		if (mAvTransportService != null) {
			ActionCallback getMediaInfoAction = new GetMediaInfo(
					mAvTransportService) {

				@Override
				public void received(ActionInvocation invocation,
						MediaInfo mediaInfo) {
					mDlnaCommandManager.getMediaInfoInfoActionCallback(true, null,
							"received", mediaInfo);
				}

				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1,
						String arg2) {
					mDlnaCommandManager.getMediaInfoInfoActionCallback(false, arg1, arg2,
							null);
				}
			};
			mAndroidUpnpService.getControlPoint().execute(getMediaInfoAction);
		}

	}
	public void getCurrentTransportAction() {
		if (mAvTransportService != null) {
			ActionCallback getCurrentTransportAction = new GetCurrentTransportActions(
					mAvTransportService) {

				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1,
						String arg2) {
					mDlnaCommandManager.getCurrentTransportActionCallback(false, arg1,
							arg2, null);
				}

				@Override
				public void received(ActionInvocation arg0,
						TransportAction[] transportAction) {
					mDlnaCommandManager.getCurrentTransportActionCallback(true, null,
							"received", transportAction);
				}
			};
			mAndroidUpnpService.getControlPoint().execute(getCurrentTransportAction);
		}
	}
}
