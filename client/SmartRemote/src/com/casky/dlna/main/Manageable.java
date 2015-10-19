/**
 * 
 */
package com.casky.dlna.main;

/**
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�Manageable  
 * �������� �ṩý���ļ���ѡ����ӿ�
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-9-17 ����6:08:15
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-9-17 ����6:08:15
 * �汾�� 1.0    
 */
public interface Manageable {

	/**
	* �����������õ�Fragment��MediaFileSelectionManager(���Կ��ƶ�ѡ) 
	* @return MediaFileSelectionManager
	 */
	public MediaFileSelectionManager getMediaFileSelectionManager();
	
	public void showTitleBar();
	public void hideTitleBar();
    public void refreshTitleCount();
}
