/**
 * 
 */
package com.casky.dlna.picture;

import com.casky.dlna.main.MediaFile;

/** 
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�Picture  
 * �������� Picture��
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-9-17 ����10:46:02
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-9-17 ����10:46:02
 * �汾�� 1.0    
 */
public class Picture extends MediaFile {
	
	private PictureMetaData metadata;

	public Picture(int position,
			PictureMetaData metadata){
		super(metadata,position);
		this.metadata = metadata;
	}
	
	
	@Override
	public PictureMetaData getMetaData(){
		return (PictureMetaData)metadata;
	}
	


}
