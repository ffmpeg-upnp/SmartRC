package com.casky.remote.rc.network;

import java.io.Serializable;
import java.util.HashMap;
/**
 * 
*    
* 项目名称：SmartRemote   
* 类名称：PingResult   
* 类描述： 存储Tv设备网络信息类，实现了Serializable接口，用于Activity之间数据的传递
* 创建人：shaojiansong   
* 创建时间：2014-8-25 下午2:21:57   
* 修改人：shaojiansong   
* 修改时间：2014-8-25 下午2:21:57   
* 修改备注：   
* 版本： 1.0   
*
 */
public class PingResult extends HashMap<String, String> implements Comparable<PingResult>, Serializable
{
	private static final long serialVersionUID = 7124190058356822372L;	
	private final String IP;
	private final String HostName;
	private int IPArr[];
	
	public PingResult(String IP,String HostName)
	{
		this.IP = IP;
		this.HostName = HostName;
		this.IPArr = splitIP(IP);
		this.put("IP", IP);
		this.put("HostName", HostName);
	}
	
	private int[] splitIP(String IP)
	{
		String IPstr[] = IP.split(".");
		int IPint[] = new int[4];
		for(int i=0;i<IPstr.length;i++){
			IPint[i] = Integer.parseInt(IPstr[i]);
		}
		return IPint;
	}

	@Override
	public int compareTo(PingResult another) {
		
		for(int i=0;i<IPArr.length;i++){
			int cmp = this.IPArr[i] - another.IPArr[i];
			if(cmp != 0) return cmp;
		}
		return 0;
	}
	
	@Override
	public boolean equals(Object object) 
	{
		// TODO Auto-generated method stub
		PingResult pr = (PingResult)object;
		return (this.IP.equals(pr.IP)  && this.HostName.equals(pr.HostName));
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return IP + "\n" + HostName;
	}
	
	public void printIP(){
		System.out.println(this.IP);
	}
}
