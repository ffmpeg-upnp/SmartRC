package com.casky.remote.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**  
* 项目名称：SmartRemote
* 类名称：XmlParser  
* 类描述： 将从本地获取的声音识别结果XML转换为可读的字符串(暂时未使用)
* 创建人：wangbo
* 创建时间：2014-9-3 下午5:16:15
* 修改人：wangbo
* 修改时间：2014-9-3 下午5:16:15
* 修改备注：   
* 版本： 1.0    
*
 */
public class XmlParser {

	public static String parseNluResult(String xml) 
	{
		StringBuffer buffer = new StringBuffer();
		try
		{
			// DOM builder
			DocumentBuilder domBuilder = null;
			// DOM doc
			Document domDoc = null;	

			// init DOM
			DocumentBuilderFactory domFact = DocumentBuilderFactory.newInstance();
			domBuilder = domFact.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xml.getBytes());
			domDoc = domBuilder.parse(is);

			// 获取根节点
			Element root = (Element) domDoc.getDocumentElement();
			
			Element raw = (Element)root.getElementsByTagName("rawtext").item(0);
			buffer.append("【识别结果】" + raw.getFirstChild().getNodeValue());
			buffer.append("\n");
			
			Element e = (Element)root.getElementsByTagName("result").item(0);
			
			Element focus = (Element)e.getElementsByTagName("focus").item(0);
			buffer.append("【FOCUS】" + focus.getFirstChild().getNodeValue());
			buffer.append("\n");
			
			Element action = (Element)e.getElementsByTagName("action").item(0);
			Element operation = (Element)action.getElementsByTagName("operation").item(0);
			buffer.append("【ACTION】" + operation.getFirstChild().getNodeValue());
			buffer.append("\n");
			

		}catch(Exception e){
			e.printStackTrace();
		};
		buffer.append("\n");
		buffer.append("【ALL】" + xml);
		return buffer.toString();
	}
}
