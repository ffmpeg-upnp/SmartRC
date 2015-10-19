package com.casky.dlna.main;

public class RenderSelectCallBridge {
	public static RenderSelectCallBridge mBridge;
	private renderSelectCallBack mSelectCallBack;
	
	private RenderSelectCallBridge(){
	}
	
	public static RenderSelectCallBridge getInstance(){
		if (mBridge == null) {
			mBridge = new RenderSelectCallBridge();
		}
		return mBridge;
	}
	
	public boolean invokeMethod(){
		if (mSelectCallBack != null) {
			mSelectCallBack.doSelect();
			return true;
		}else{
			return false;
		}
		
	}
	
	public void setRenderSelectCallBack(renderSelectCallBack rSelectCallBack){
		
		mSelectCallBack = rSelectCallBack;
		if (mSelectCallBack != null) {
			System.out.println("mSelectCallBack is " + rSelectCallBack.toString());	
		}
	}
	
	public static interface renderSelectCallBack{
		public void doSelect();
	}
}
