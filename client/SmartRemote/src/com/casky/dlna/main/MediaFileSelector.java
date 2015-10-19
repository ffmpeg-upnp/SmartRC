/**
 * 
 */
package com.casky.dlna.main;

/**
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�MediaFileSelector  
 * �������� 
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-9-25 ����9:41:04
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-9-25 ����9:41:04
 * �汾�� 1.0    
 */
public class MediaFileSelector implements Selectable{

	/**
	 * �Ƿ�ѡ��
	 */
	private boolean isSelected;
	
	private MediaFile mediaFile;
	
	/**
	 * MediaFileSelector���췽��
	 */
	public MediaFileSelector(
			MediaFile mediaFile){
		this.mediaFile = mediaFile;
		this.isSelected = false;
	}


	/**
	 * �����Ƿ��Ѿ�ѡ��
	 */
	@Override
	public boolean isSelected() {
		return this.isSelected;
	}

	/**
	 * ��ݵ�ǰѡ��״̬�Զ�����ѡ��/ȡ��ѡ�в���
	 */
	@Override
	public void doSelect() {
		this.isSelected = !isSelected;
	}
	
	/**
	* ����������ǿ��ѡ��/ȡ��ѡ�� 
	* @param selecte true:ѡ�и�ý���ļ�,false:ȡ��ѡ�и�ý���ļ�
	 */
	public void doSelect(boolean selecte) {
		this.isSelected = selecte;
	}

	/**
	 * ����metadata
	 */
	public MediaFile getMediaFile() {
		return mediaFile;
	}

}
