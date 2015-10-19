/**
 * 
 */
package com.casky.dlna.main;

/** 
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�Selectable  
 * �������� �ṩѡ�нӿ�
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-9-17 ����9:59:42
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-9-17 ����9:59:42
 * �汾�� 1.0    
 */
public interface Selectable {

	/**
	* �����������ж��Ƿ�ѡ�� 
	* @return true:ѡ��,false:δѡ��
	 */
	public boolean isSelected(); 
	
	/**
	* �����������ı�ѡ��״̬
	 */
	public void doSelect();
}
