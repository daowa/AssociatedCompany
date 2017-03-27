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

	//���������������ݲ������ݿ�
	public static void toDB() throws IOException, DocumentException, ParserConfigurationException, SAXException{
		String path = "E:\\work\\SIE\\data\\16.xml";
//		String path = "E:\\work\\SIE\\IConference_sie_data\\iConference_Cited_Pairs.RAW_XML.xml\\iConference_Cited_Pairs.RAW_XML.xml";

		//����SAXReader��ȡ����ר�����ڶ�ȡxml  
        SAXReader saxReader = new SAXReader();  
        //����saxReader��read��д������֪���ȿ���ͨ��inputStream����������ȡ��Ҳ����ͨ��file��������ȡ   
        Document document = saxReader.read(new File(path));//����ָ���ļ��ľ���·��  
        
        //��ȡ���ڵ����  
        Element rootElement = document.getRootElement();    
        //��ȡ�ӽڵ�  
        List<Element> listElementREC = rootElement.elements("REC");
        //��ʼ��ȡ����
        List<List<String>> listsResult = new ArrayList<>();
        for(Element element : listElementREC){
	        if(element != null){
	        	//��ʼ������ֵ
	        	String wosID = "";
	        	String title = "";
	        	String time = "";
	        	String references = "";
	        	String keywords = "";
	        	
	        	//��ȡid
	        	wosID = element.element("UID").getText();
	        	U.print(wosID);
	        	
	        	//��ȡ����
	        	List<Element> listElementTitle = element.element("static_data").element("summary").element("titles").elements("title");
	        	for(Element re :listElementTitle){
	        		if(re.attributeValue("type").equals("item"))
	        			title = re.getText();
	        	}
	        	U.print(title);
	        	
	        	//��ȡ����ʱ��
	        	time = element.element("static_data").element("summary").element("pub_info").attributeValue("sortdate");
	        	U.print(time);
	        	
	        	//��ȡ�������׵�id
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
	        	
	        	//��ȡ�ؼ���
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
	        	
	        	//��¼�����Ľ��
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
	
	//��ȡ�ؼ��ʹ�������
	public static void outputKeywordsNet(int startYear, int stopYear, int thresholdNode, int thresholdLine) throws SQLException, IOException{
		//��ʼ��
		Map<String, Integer> mapKeywordId = new HashMap();
		Map<Integer, String> mapIdKeyword = new HashMap<>();
		Map<String, Integer> mapKeywordCount = new HashMap<>();
		Map<String, Integer> mapLineWeight = new HashMap<>();
		//��ȡ�ؼ���
		String sql = "SELECT year(time_) as year, keywords FROM sie.article";
		ResultSet rs = DBFunction.getRS(sql);
		int id = 1;//��1��ʼ����id
		while(rs.next()){
			if(rs.getInt("year") < startYear || rs.getInt("year") > stopYear)
				continue;
			String[] keywords = rs.getString("keywords").split(",");
			//Ϊ�ؼ��ʸ���id
			for(String keyword : keywords){
				if(mapKeywordId.get(keyword) == null){
					mapKeywordId.put(keyword, id);
					mapIdKeyword.put(id, keyword);
					id++;
				}
				//����ÿ���ؼ��ʳ��ֵĴ���
				U.mapAddCount(mapKeywordCount, keyword);
			}
			//����ؼ��ʹ���
			for(int i = 0; i < keywords.length; i++){
				for(int j = i+1; j < keywords.length; j++){
					U.mapAddCount(mapLineWeight, U.getCompareString(keywords[i], keywords[j]));
				}
			}
		}
		//��������
		String pathNode = "E:\\work\\SIE\\���ݽ��\\node_" + startYear + "-" + stopYear + ".csv";
		String pathLine = "E:\\work\\SIE\\���ݽ��\\line_" + startYear + "-" + stopYear + ".csv";
		writeCSV_Node(mapIdKeyword, mapKeywordCount, thresholdNode, pathNode);
		writeCSV_Line(mapKeywordId, mapLineWeight, thresholdLine, pathLine);
	}
	
	//����pajek����
	//���ļ�
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
		U.print("���" + count + "����");
	}
	//���ļ�
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
		U.print("���" + count + "����");
	}
	
	
	
	
	
	
	
	
	//����������ߵ�����
	public static void outputTopCitedArticle(int limit) throws SQLException, IOException{
		//��ʼ��
		Map<String, Integer> mapWOSCount = new HashMap<>(); 
		//��ȡ��������
		String sql = "SELECT references_ FROM sie.article";
		ResultSet rs = DBFunction.getRS(sql);
		//ͳ�����ô���
		while(rs.next()){
			String[] WOSs = rs.getString("references_").split(",");
			for(String WOS : WOSs){
				U.mapAddCount(mapWOSCount, WOS);
			}
		}
		//����
		TreeMap<String, Integer> sort_WOSCount = U.sortMap(mapWOSCount);
		//���topN�������׵�ƪ�����ؼ��ʡ�id����������
		List<String> listResult = new ArrayList<>();
		for(Entry<String, Integer> entry : sort_WOSCount.entrySet()){
			if(limit-- > 0){
				String title = "";
				String keywords = "";
				String time = "";
				sql = "select title, time_, keywords from sie.article where wosID = \"" + entry.getKey() + "\"";
				rs = DBFunction.getRS(sql);
				while(rs.next()){
					title = rs.getString("title").replaceAll(",", "��");
					keywords = rs.getString("keywords").replaceAll(",", "��");
					time = rs.getString("time_");
				}
				listResult.add(title + "," + keywords + "," + time + "," + entry.getKey() + "," + entry.getValue());
			}
		}
		String pathResult = "E:\\work\\SIE\\���ݽ��\\topNCited.csv";
		FileFunction.writeList(listResult, pathResult);
	}
	
	
	
	
	
	
	//���ÿ��Ĺؼ���
	public static void outputKeywordsByYears() throws SQLException, IOException{
		//��ʼ��
		Map<Integer, Map<String, Integer>> mapYearMapkeyword = new HashMap<>();
		//��ȡ�ؼ���
		String sql = "SELECT year(time_) as year, keywords FROM sie.article";
		ResultSet rs = DBFunction.getRS(sql);
		while(rs.next()){
			int year = rs.getInt("year");
			String[] keywords = rs.getString("keywords").split(",");
			if(year < 1997 || year > 2017)
				continue;
			//���map��û��ʼ������ʼ��
			if(mapYearMapkeyword.get(year) == null){
				Map<String, Integer> tempMap = new HashMap<>();
				mapYearMapkeyword.put(year, tempMap);
			}
			//��ʼ��ֵ
			Map<String, Integer> mapKeywordCount = mapYearMapkeyword.get(year);
			for(String keyword : keywords){
				U.mapAddCount(mapKeywordCount, keyword);
			}
			mapYearMapkeyword.put(year, mapKeywordCount);
		}
		//���
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
				line += "," + entrySort.getKey().replaceAll(",", "��") + ":" + entrySort.getValue();
			}
			listResult.add(line);
		}
		String path = "E:\\work\\SIE\\���ݽ��\\TopKeywordByYear.csv";
		FileFunction.writeList(listResult, path);
	}
	
	
	
	
	
	
	//ͳ�Ʋο���������
	public static void calculateCitingCount() throws SQLException{
		//��ʼ��
		Map<String, Integer> mapReferenceCount = new HashMap<>();//���㲻�ظ�����������
		int count = 0;//�����ظ�����������
		//��������
		String sql = "SELECT references_ FROM sie.article";
		ResultSet rs = DBFunction.getRS(sql);
		//����ο���������
		while(rs.next()){
			String results[] = rs.getString("references_").split(",");
			for(String result : results){
				U.mapAddCount(mapReferenceCount, result);
				count ++;
			}
		}
		//��ӡ�����
		U.print("���ظ���" + mapReferenceCount.size());
		U.print("�ظ�" + count);
	}
	
}
