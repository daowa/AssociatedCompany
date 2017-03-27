package com.Others;

import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;  
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;  
import java.util.LinkedList;
import java.util.List;  
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
  
import org.dom4j.Document;  
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;  
import org.dom4j.Element;  
import org.dom4j.io.SAXReader;  
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.db.DBFunction;
import com.db.FileFunction;
import com.myClass.U;
import com.mysql.fabric.xmlrpc.base.Array;

public class SIE {

	//将解析出来的数据插入数据库
	public static void toDB() throws IOException, DocumentException, ParserConfigurationException, SAXException{
		String path = "E:\\work\\SIE\\data\\16.xml";
//		String path = "E:\\work\\SIE\\IConference_sie_data\\iConference_Cited_Pairs.RAW_XML.xml\\iConference_Cited_Pairs.RAW_XML.xml";

		//创建SAXReader读取器，专门用于读取xml  
        SAXReader saxReader = new SAXReader();  
        //根据saxReader的read重写方法可知，既可以通过inputStream输入流来读取，也可以通过file对象来读取   
        Document document = saxReader.read(new File(path));//必须指定文件的绝对路径  
        
        //获取根节点对象  
        Element rootElement = document.getRootElement();    
        //获取子节点  
        List<Element> listElementREC = rootElement.elements("REC");
        //开始获取数据
        List<List<String>> listsResult = new ArrayList<>();
        for(Element element : listElementREC){
	        if(element != null){
	        	//初始化所有值
	        	String wosID = "";
	        	String title = "";
	        	String time = "";
	        	String references = "";
	        	String keywords = "";
	        	
	        	//获取id
	        	wosID = element.element("UID").getText();
	        	U.print(wosID);
	        	
	        	//获取标题
	        	List<Element> listElementTitle = element.element("static_data").element("summary").element("titles").elements("title");
	        	for(Element re :listElementTitle){
	        		if(re.attributeValue("type").equals("item"))
	        			title = re.getText();
	        	}
	        	U.print(title);
	        	
	        	//获取发布时间
	        	time = element.element("static_data").element("summary").element("pub_info").attributeValue("sortdate");
	        	U.print(time);
	        	
	        	//获取引用文献的id
	        	List<Element> listElementReference = new ArrayList<>();
	        	if(element.element("static_data").element("fullrecord_metadata") != null &&
	        			element.element("static_data").element("fullrecord_metadata").element("references") != null)
	        		listElementReference = element.element("static_data").element("fullrecord_metadata").element("references").elements("reference");
	        	for(Element re : listElementReference){
	        		if(re.element("uid") != null)
	        			references += re.element("uid").getText() + ",";
	        	}
	        	if(references.length() > 0)
	        		references = references.substring(0, references.length()-1); 
	        	U.print(references);
	        	
	        	//获取关键词
	        	List<Element> listElementKeyword = new ArrayList<>();
	        	if(element.element("static_data").element("fullrecord_metadata") != null &&
	        			element.element("static_data").element("fullrecord_metadata").element("keywords") != null)
	        		listElementKeyword = element.element("static_data").element("fullrecord_metadata").element("keywords").elements("keyword");
	        	for(Element rs : listElementKeyword){
	        		keywords += rs.getText() + ",";
	        	}
	        	if(keywords.length() > 0)
	        		keywords = keywords.substring(0, keywords.length()-1);
	        	U.print(keywords);
	        	
	        	//记录该条的结果
	        	List<String> list = new ArrayList<>();
	        	list.add(wosID);
	        	list.add(title);
	        	list.add(time);
	        	list.add(references);
	        	list.add(keywords);
	        	listsResult.add(list);
	        }
        }
        DBFunction.insertArticle(listsResult);
	}
	
	//获取关键词共现网络
	public static void outputKeywordsNet(int startYear, int stopYear, int thresholdNode, int thresholdLine) throws SQLException, IOException{
		//初始化
		Map<String, Integer> mapKeywordId = new HashMap();
		Map<Integer, String> mapIdKeyword = new HashMap<>();
		Map<String, Integer> mapKeywordCount = new HashMap<>();
		Map<String, Integer> mapLineWeight = new HashMap<>();
		//读取关键词
		String sql = "SELECT year(time_) as year, keywords FROM sie.article";
		ResultSet rs = DBFunction.getRS(sql);
		int id = 1;//从1开始计算id
		while(rs.next()){
			if(rs.getInt("year") < startYear || rs.getInt("year") > stopYear)
				continue;
			String[] keywords = rs.getString("keywords").split(",");
			//为关键词赋予id
			for(String keyword : keywords){
				if(mapKeywordId.get(keyword) == null){
					mapKeywordId.put(keyword, id);
					mapIdKeyword.put(id, keyword);
					id++;
				}
				//计算每个关键词出现的次数
				U.mapAddCount(mapKeywordCount, keyword);
			}
			//计算关键词共现
			for(int i = 0; i < keywords.length; i++){
				for(int j = i+1; j < keywords.length; j++){
					U.mapAddCount(mapLineWeight, U.getCompareString(keywords[i], keywords[j]));
				}
			}
		}
		//绘制网络
		String pathNode = "E:\\work\\SIE\\数据结果\\node_" + startYear + "-" + stopYear + ".csv";
		String pathLine = "E:\\work\\SIE\\数据结果\\line_" + startYear + "-" + stopYear + ".csv";
		writeCSV_Node(mapIdKeyword, mapKeywordCount, thresholdNode, pathNode);
		writeCSV_Line(mapKeywordId, mapLineWeight, thresholdLine, pathLine);
	}
	
	//绘制pajek网络
	//点文件
	private static void writeCSV_Node(Map<Integer, String> mapIdKeyword, Map<String, Integer> mapKeywordCount, int thresholdNode, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("Id,Label,weighted degree\r\n");
		int count = 0;
		for(int i = 0; i < mapIdKeyword.size(); i++){
			if(mapKeywordCount.get(mapIdKeyword.get(i+1)) >= thresholdNode){
				fw.write((i+1) + "," + mapIdKeyword.get(i+1) + "," + mapKeywordCount.get(mapIdKeyword.get(i+1)) + ("\r\n"));
				count++;
			}
		}
		fw.close();
		U.print("输出" + count + "个点");
	}
	//线文件
	private static void writeCSV_Line(Map<String, Integer> mapKeywordId, Map<String, Integer> mapLineWeight, int thresholdLine, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("Source,Target,Type,id,label,timeset,weight\r\n");
		int lineId = 0;
		int count = 0;
		for(Entry<String, Integer> entry : mapLineWeight.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			if(weight >= thresholdLine){
				fw.write(mapKeywordId.get(line[0]) + "," + mapKeywordId.get(line[1]) + "," + "unDirected" + "," + lineId++ + "," + "," + "," + weight + "\r\n");
				count++;
			}
		}
		fw.close();
		U.print("输出" + count + "条线");
	}
	
	
	
	
	
	
	
	
	//计算引用最高的文章
	public static void outputTopCitedArticle(int limit) throws SQLException, IOException{
		//初始化
		Map<String, Integer> mapWOSCount = new HashMap<>(); 
		//读取引用文献
		String sql = "SELECT references_ FROM sie.article";
		ResultSet rs = DBFunction.getRS(sql);
		//统计引用次数
		while(rs.next()){
			String[] WOSs = rs.getString("references_").split(",");
			for(String WOS : WOSs){
				U.mapAddCount(mapWOSCount, WOS);
			}
		}
		//排序
		TreeMap<String, Integer> sort_WOSCount = U.sortMap(mapWOSCount);
		//输出topN被引文献的篇名、关键词、id、被引次数
		List<String> listResult = new ArrayList<>();
		for(Entry<String, Integer> entry : sort_WOSCount.entrySet()){
			if(limit-- > 0){
				String title = "";
				String keywords = "";
				String time = "";
				sql = "select title, time_, keywords from sie.article where wosID = \"" + entry.getKey() + "\"";
				rs = DBFunction.getRS(sql);
				while(rs.next()){
					title = rs.getString("title").replaceAll(",", "，");
					keywords = rs.getString("keywords").replaceAll(",", "，");
					time = rs.getString("time_");
				}
				listResult.add(title + "," + keywords + "," + time + "," + entry.getKey() + "," + entry.getValue());
			}
		}
		String pathResult = "E:\\work\\SIE\\数据结果\\topNCited.csv";
		FileFunction.writeList(listResult, pathResult);
	}
	
	
	
	
	
	
	//输出每年的关键词
	public static void outputKeywordsByYears() throws SQLException, IOException{
		//初始化
		Map<Integer, Map<String, Integer>> mapYearMapkeyword = new HashMap<>();
		//读取关键词
		String sql = "SELECT year(time_) as year, keywords FROM sie.article";
		ResultSet rs = DBFunction.getRS(sql);
		while(rs.next()){
			int year = rs.getInt("year");
			String[] keywords = rs.getString("keywords").split(",");
			if(year < 1997 || year > 2017)
				continue;
			//如果map还没初始化，初始化
			if(mapYearMapkeyword.get(year) == null){
				Map<String, Integer> tempMap = new HashMap<>();
				mapYearMapkeyword.put(year, tempMap);
			}
			//开始赋值
			Map<String, Integer> mapKeywordCount = mapYearMapkeyword.get(year);
			for(String keyword : keywords){
				U.mapAddCount(mapKeywordCount, keyword);
			}
			mapYearMapkeyword.put(year, mapKeywordCount);
		}
		//输出
		List<String> listResult = new ArrayList<>();
		for(Entry<Integer, Map<String, Integer>> entry :mapYearMapkeyword.entrySet()){
			int limit = 20;
			String line = "";
			int year = entry.getKey();
			line += year;
			TreeMap<String, Integer> sortMap = U.sortMap(entry.getValue());
			for(Entry<String, Integer> entrySort : sortMap.entrySet()){
				if(entrySort.getKey().length() == 0) continue;
				if(limit-- == 0) continue;
				line += "," + entrySort.getKey().replaceAll(",", "，") + ":" + entrySort.getValue();
			}
			listResult.add(line);
		}
		String path = "E:\\work\\SIE\\数据结果\\TopKeywordByYear.csv";
		FileFunction.writeList(listResult, path);
	}
	
	
	
	
	
	
	//统计参考文献数量
	public static void calculateCitingCount() throws SQLException{
		//初始化
		Map<String, Integer> mapReferenceCount = new HashMap<>();//计算不重复的引用数量
		int count = 0;//计算重复的引用数量
		//读入数据
		String sql = "SELECT references_ FROM sie.article";
		ResultSet rs = DBFunction.getRS(sql);
		//计算参考文献数量
		while(rs.next()){
			String results[] = rs.getString("references_").split(",");
			for(String result : results){
				U.mapAddCount(mapReferenceCount, result);
				count ++;
			}
		}
		//打印出结果
		U.print("不重复：" + mapReferenceCount.size());
		U.print("重复" + count);
	}
	
}
