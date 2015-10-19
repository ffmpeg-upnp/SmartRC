package com.casky.remote.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
* 项目名称：SmartRemote
* 类名称：JsonParser  
* 类描述： 将从网络获取的声音识别结果Json转换为可读的字符串
* 创建人：wangbo
* 创建时间：2014-9-3 下午5:15:08
* 修改人：wangbo
* 修改时间：2014-9-3 下午5:15:08
* 修改备注：   
* 版本： 1.0    
*
 */
public class JsonParser {

	private static String GrammarResult = null;
	
	/**
	* @param json
	* @return 转换结果
	* 方法描述： 没用上
	* 创建人：wangbo
	* 创建时间：2014-9-3 下午5:34:09
	 */
	public static String parseIatResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				// 转写结果词，默认使用第一个结果
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				JSONObject obj = items.getJSONObject(0);
				ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return ret.toString();
	}
	
	/**
	* @param json
	* @return 转换结果
	* 方法描述： 将json转换为可以识别的字符串
	* 创建人：wangbo
	* 创建时间：2014-9-3 下午5:36:00
	 */
	public static String parseGrammarResult(String json) {
		StringBuffer ret = new StringBuffer();
		int sc = 0;
		int lastSc = 0;
		String lastW = null;
		GrammarResult = null;
		
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				for(int j = 0; j < items.length(); j++)
				{
					JSONObject obj = items.getJSONObject(j);
					if(obj.getString("w").contains("nomatch"))
					{
						ret.append("没有匹配结果.");
						return ret.toString();
					}
					
					ret.append("【结果】" + obj.getString("w"));
					ret.append("【置信度】" + (sc = obj.getInt("sc")));
					ret.append("\n");
					if(obj.getInt("sc") > lastSc){
						lastW = obj.getString("w");
						lastSc = sc;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret.append("没有匹配结果.");
		} 
		GrammarResult = lastW;
		return ret.toString();
	}
	
	/**
	* @return 识别结果
	* 方法描述： 返回置信度最高的字符串
	* 创建人：wangbo
	* 创建时间：2014-9-3 下午5:36:27
	 */
	public static String getGrammarResult(){
		return GrammarResult;
	}
}
