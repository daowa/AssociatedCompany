package com.Others;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.poi.xssf.usermodel.ListAutoNumber;

import com.db.DBFunction;
import com.db.FileFunction;
import com.myClass.NLPIR;
import com.myClass.U;

public class SMDAFood {
	
	public static void getFoodList() throws SQLException, IOException{
		//��ѯ
		String sql = "SELECT * FROM smda.tb_baiduwaimai where recommend_dishes != ''";
		ResultSet rs = DBFunction.getRS(sql);
		Map<String, Integer> mapFood = new HashMap<String, Integer>();
		while(rs.next()){
			String[] foods = rs.getString("recommend_dishes").replaceAll("\"", "").replaceAll(" ", "")
					.replaceAll("[-��]", ",").replaceAll("\\+", ",").replaceAll("��", ",").replaceAll("ʮ", ",")
					.replaceAll("��", ",").replaceAll("\\?", ",")
					.replaceAll("��", ",").replaceAll("��", ",").replaceAll("��", ",").replaceAll("�B", ",").replaceAll("\\|", ",")
					.replace("\t", ",")
					.split(",");
			for(String food : foods){
				//����±Ѽ���ǡ���С�������±Ѽ���ǣ���У��������
				food = food.replaceAll("[����(����].*?[����)����]", "");
				//����**�齴**�������
				food = food.replaceAll("\\*", "");
				//������ʽ����\/�С��������¿���_����������������9��˫��֥��ԭ��89Ԫ�������������������
				food = food.replaceAll("\\\\/.*", "").replaceAll("_.*", "").replaceAll("kg", "��").replaceAll("(\\d+).", "")
						.replaceAll("��.", "").replaceAll("\\/.", "");
				//ȥ������ݡ�����С�ݡ���
				food = food.replaceAll("���", "").replaceAll("С��", "")
						.replaceAll("��һ", "").replaceAll("�ܶ�", "").replaceAll("����", "").replaceAll("һ��", "")
						.replaceAll("����", "").replaceAll("����", "").replaceAll("����", "").replaceAll("����", "")
						.replaceAll("ÿ��", "").replaceAll("[����,��]", "").replaceAll("\\.", "")
						.replaceAll("[����<>����һ!]", "").replaceAll("��", "").replaceAll("��", "").replaceAll("\\?", "")
						.replaceAll("A��", "").replaceAll("cal", "")
						.replaceAll("����װ", "").replaceAll("����", "").replaceAll("һ��", "").replaceAll("���", "")
						.replaceAll("\\-", "").replaceAll(" ", "").trim();
				//����Ϊ�յĲ�Ʒ
				if(food == null || food.length() == 0) continue;
				U.print(food);
				mapFood.put(food, mapFood.get(food) == null ? 1 : mapFood.get(food)+1);
			}
		}
		//����
		TreeMap<String, Integer> sorted_map = U.sortMap(mapFood);
		U.print("���в�Ʒ����" + sorted_map.size());
		//���
		FileFunction.writeMap_KV(sorted_map, "E:\\work\\smda����+���ݾ���\\foodList.txt");//����˾���ͳ���Ƶ�����
	}
	
	public static void getFoodWordsList() throws NumberFormatException, IOException{
		//��ȡʳƷ�б�
		Map<String, Integer> mapTemp = FileFunction.readMap_SI("E:\\work\\smda����+���ݾ���\\foodList.txt");
		TreeMap<String, Integer> mapFoodFrequency = U.sortMap(mapTemp);
		U.print("����ʳ�" + mapFoodFrequency.size());
		
		//�ִ�
		Map<String, Integer> mapWordFrequency = new HashMap<>();
		NLPIR.NlpirInit();
		List<String> listFood = new ArrayList<>();
		for(Map.Entry<String, Integer> entry : mapFoodFrequency.entrySet()){
			String food = "";
			for(String s : NLPIR.wordSegmentateWithoutCharacteristic(entry.getKey())){
				food += s + ",";
				mapWordFrequency.put(s, mapWordFrequency.get(s) == null ? entry.getValue() : mapWordFrequency.get(s)+entry.getValue());
			}
			listFood.add(food + entry.getValue());
		}
		NLPIR.NlpirExit();
		
		//ȥ��ͣ�ô�
		String[] stopWords = {"��", "��", "[", "]", "?", "��", "&", "#", "@", "!", "��"};
 		for(String stop : stopWords){
 			if(mapWordFrequency.get(stop) != null)
 				mapWordFrequency.remove(stop);
 		}
		
		//���ִʺ��ʳ���б�д�룬�Ա��ڷ����´�
		FileWriter fw = new FileWriter("E:\\work\\smda����+���ݾ���\\foodSeperate.txt");
		for(String food : listFood){
			fw.write(food + "\r\n");
		}
		fw.close();
		U.print("д��foodSeperate�ɹ�");
		//�����´ʡ������û��ʵ�ȣ�ֻ��Ҫһ�β���
//		findNewWord(mapFoodFrequency);//�����´�
		
		//�����Ƶ
 		TreeMap<String, Integer> sorted_map = U.sortMap(mapWordFrequency);
		U.print("���д�����" + sorted_map.size());
		FileFunction.writeMap_KV(sorted_map, "E:\\work\\smda����+���ݾ���\\foodWordFrequency.txt");//����Ƶ���
	}
	
	public static void getFoodWordsNet(int thresholdNode, int thresholdLine) throws IOException{
		//��ȡword-frequency
		U.print("��ȡ ��-��Ƶ �ļ�");
		Map<String, Integer> mapWordFrequency = FileFunction.readMap_SI("E:\\work\\smda����+���ݾ���\\foodWordFrequency.txt");
		//Ϊ������ֵ�ͷ�ͣ�õĴ�����id
		//��ȡͣ�ô�
		U.print("��ȡͣ�ô�");
		List<String> listStops = FileFunction.readFile("E:\\work\\smda����+���ݾ���\\vocabulary\\stopWords.txt");
		U.print("��������ֵ�ͷ�ͣ�ôʵĴ�");
		int id = 0;
		Map<String, Integer> mapWordId = new HashMap<>();
		Map<Integer, String> mapIdword = new HashMap<>();
		for(Map.Entry<String, Integer> entry : mapWordFrequency.entrySet()){
			if(entry.getValue() >= thresholdNode || !listStops.contains(entry.getKey())){
				id++;
				mapWordId.put(entry.getKey(), id);
				mapIdword.put(id, entry.getKey());
			}
		}
		//��ȡ�ִ�
		U.print("��ȡ�ִ�");
		List<String> listSeperate = FileFunction.readFile("E:\\work\\smda����+���ݾ���\\foodSeperate.txt");
		//������
		U.print("�����ߵ�Ȩֵ");
		Map<String, Integer> mapLineWeight = new HashMap<>();
		for(String temp : listSeperate){
			String[] keywords = temp.split(",");
			int weight = Integer.parseInt(keywords[keywords.length-1]);
			for(int i = 0; i < keywords.length-1; i++){
				if(mapWordId.get(keywords[i]) == null) continue;//ͨ������id���ж��Ƿ�ͨ�����ɸѡ
				for(int j = i+1; j < keywords.length-1; j++){
					if(mapWordId.get(keywords[j]) == null) continue;
					String line = U.getCompareString(mapWordId.get(keywords[i]) + "", mapWordId.get(keywords[j]) + "");
					mapLineWeight.put(line, mapLineWeight.get(line) == null ? weight : mapLineWeight.get(line)+weight);
				}
			}
		}
		//ɸѡ��
		U.print("ɸѡ��");
		Iterator<Map.Entry<String, Integer>> it = mapLineWeight.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = it.next();
			if(entry.getValue() < thresholdLine)
				it.remove();
		}
		//�����ߣ����������������ҳ��´�
		U.print("������");
		TreeMap<String, Integer> sorted_map = U.sortMap(mapLineWeight);
		FileWriter fw = new FileWriter("E:\\work\\smda����+���ݾ���\\foodLineWeight.txt");
		for(Map.Entry<String, Integer> entry : sorted_map.entrySet()){
			String s1 = mapIdword.get(Integer.parseInt(entry.getKey().split(",")[0]));
			String s2 = mapIdword.get(Integer.parseInt(entry.getKey().split(",")[1]));
			fw.write(s1 + "," + s2 + ":" + entry.getValue() + "\r\n");
		}
		fw.close();
		U.print("д��foodLineWeight�ɹ�");
		
		writeCSVNode(mapWordId, mapWordFrequency, "E:\\work\\smda����+���ݾ���\\foodNetNode.csv");
		writeCSVLine(mapLineWeight, "E:\\work\\smda����+���ݾ���\\foodNetLine.csv");
		U.print("done");
		
	}
	
	//�����´ʣ����˹���ӽ��û��ʵ�
	private static void findNewWord(Map<String, Integer> mapFoodFrequency) throws IOException{
		NLPIR.NlpirInit();
		List<String> list = new ArrayList<String>();
		String s = "";
		int count = 0;
		for(Map.Entry<String, Integer> entry : mapFoodFrequency.entrySet()){
			s += entry.getKey() + ",";
			if(count++ > 10000) break;
		}
		String result = NLPIR.getNewWord(s);
		NLPIR.NlpirExit();
	}
	
	
	//��ȡ����ʳ���ڲ�ͬ�����ķֲ�
	public static void getFoodWordsWeatherBase() throws SQLException, FileNotFoundException, UnsupportedEncodingException{
		
		//��ʼ����ֵ
		int FOODWORDLIMIT = 2;//��ȡtopN��ʳ��
		//����������������±�
		int INDEXBASE_TPAVG_0 = 0;
		int INDEXBASE_TPAVG_1 = 1;
		int INDEXBASE_TPAVG_2 = 2;
		int INDEXBASE_TPAVG_3 = 3;
		int INDEXBASE_TPDF_0 = 4;
		int INDEXBASE_TPDF_1 = 5;
		int INDEXBASE_TPDF_2 = 6;
		int INDEXBASE_TPDF_3 = 7;
		int INDEXBASE_RAIN_0 = 8;
		int INDEXBASE_RAIN_1 = 9;
		int INDEXBASE_RAIN_2 = 10;
		int INDEXBASE_ISWORKDAY_WD = 11;
		int INDEXBASE_ISWORKDAY_WK = 12;
		int INDEXBASE_ISWORKDAY_HO = 13;
		//����������ķָ���ֵ
		double INTERVAL_TPAVG_01 = 4.62918;
		double INTERVAL_TPAVG_12 = 15.15;
		double INTERVAL_TPAVG_23 = 26.275;
		double INTERVAL_TPDF_01 = 2.8;
		double INTERVAL_TPDF_12 = 6.7;
		double INTERVAL_TPDF_23 = 11.6;
		double INTERVAL_RAIN_01 = 0.0001;
		double INTERVAL_RAIN_12 = 25;
		
		//��ʼ������
		String sql;
		ResultSet rs;
		Map<String, Vector<Integer>> mapFoodwordBase = new HashMap<>();
		
		//shopid��locationname����
		Map<String, String> mapShopidLocationname = new HashMap();
		sql = "select A.shopid, B.locationname "
				+ "from smda.tb_shoplocation as A left join smda.tb_locationname as B "
				+ "on A.locationid = B.locationid";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			mapShopidLocationname.put(rs.getString("shopid"), rs.getString("locationname"));
		}
		U.print("shopid-locationname��Ӧ��ɣ���" + mapShopidLocationname.size() + "�Ҳ�ͬid�̼�");
		
		//locationname����������
		int INDEX_TPAVG = 0;
		int INDEX_TPDF = 1;
		int INDEX_RAINSUM = 2;
		int INDEX_WORKDAY = 3;
		Map<String, String[]> mapLocationWeather = new HashMap<>();
		sql = "SELECT station_name, date(day) as dTime, temperature_avg, temperature_df, rainfall_sum, isworkday " +
				"FROM smda.feature_day";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			String[] s = {rs.getString("temperature_avg"), rs.getString("temperature_df"), rs.getString("rainfall_sum"), rs.getString("isworkday")};
			mapLocationWeather.put(rs.getString("station_name") + rs.getString("dTime"), s);
		}
		U.print("locaiontname&dTime-weather��Ӧ���");
		
		//��ȡtopN�Ĳ���
		List<String> listFoodwords = new ArrayList<>();
		List<String> lines = FileFunction.readFile("E:\\work\\smda����+���ݾ���\\foodWordFrequency.txt");
		for(int i = 0; i < FOODWORDLIMIT; i++){
			listFoodwords.add(lines.get(i).split("\t")[0]);
		}
		
		//��ѯ��������ȡ�ǿյ��Ƽ���Ʒ
		sql = "SELECT recommend_dishes, waimai_release_id, date(arrive_time) as t " +
				"FROM smda.tb_baiduwaimai where arrive_time > '2015-01-01' and arrive_time < '2016-05-01'" +
				"and recommend_dishes != '' limit 100";
		rs = DBFunction.getRS(sql);
		NLPIR.NlpirInit();
		int timer = 0;
		while(rs.next()){
			//������ڴ���ڼ�������
			U.print("������" + ++timer + "������");
			//��ȡʳ��
			String dishes = rs.getString("recommend_dishes");
			String shopid = rs.getString("waimai_release_id");
			String time = rs.getString("t");
			//��ȡ��������
			if(mapShopidLocationname.get(shopid) == null) continue;//����ȱʧ��ַ��Ϣ��shopid������
			String weatherFeature[] = mapLocationWeather.get(mapShopidLocationname.get(shopid) + time);
			//����������������������
			Vector<Integer> baseArray = getBaseVector(14);
			//�¶�����
			double tpavg = Double.parseDouble(weatherFeature[INDEX_TPAVG]);
			if(tpavg < INTERVAL_TPAVG_01)
				baseArray.set(INDEXBASE_TPAVG_0, 1);
			else if(INTERVAL_TPAVG_01 <= tpavg && tpavg < INTERVAL_TPAVG_12)
				baseArray.set(INDEXBASE_TPAVG_1, 1);
			else if(INTERVAL_TPAVG_12 <= tpavg && tpavg < INTERVAL_TPAVG_23)
				baseArray.set(INDEXBASE_TPAVG_2, 1);
			else if(INTERVAL_TPAVG_23 <= tpavg)
				baseArray.set(INDEXBASE_TPAVG_3, 1);
			//�²�����
			double tpdf = Double.parseDouble(weatherFeature[INDEX_TPDF]);
			if(tpdf < INTERVAL_TPDF_01)
				baseArray.set(INDEXBASE_TPDF_0, 1);
			else if(INTERVAL_TPDF_01 <= tpdf && tpdf < INTERVAL_TPDF_12)
				baseArray.set(INDEXBASE_TPDF_1, 1);
			else if(INTERVAL_TPDF_12 <= tpdf && tpdf < INTERVAL_TPDF_23)
				baseArray.set(INDEXBASE_TPDF_2, 1);
			else if(INTERVAL_TPDF_23 <= tpdf)
				baseArray.set(INDEXBASE_TPDF_3, 1);
			//��������
			double rainsum = Double.parseDouble(weatherFeature[INDEX_RAINSUM]);
			if(rainsum < INTERVAL_RAIN_01)
				baseArray.set(INDEXBASE_RAIN_0, 1);
			else if(INTERVAL_RAIN_01 <= rainsum && rainsum < INTERVAL_RAIN_12)
				baseArray.set(INDEXBASE_RAIN_1, 1);
			else if(INTERVAL_RAIN_12 <= rainsum)
				baseArray.set(INDEXBASE_RAIN_2, 1);
			//�ڼ�������
			String workday = weatherFeature[INDEX_WORKDAY];
			if(workday.equals("������"))
				baseArray.set(INDEXBASE_ISWORKDAY_WD, 1);
			else if(workday.equals("��ĩ"))
				baseArray.set(INDEXBASE_ISWORKDAY_WK, 1);
			else
				baseArray.set(INDEXBASE_ISWORKDAY_HO, 1);
			//����ʳƷ���ϣ��ж����м�¼�Ƿ��иò�Ʒ���������Ӹ�ά�ȵ���������������
			for(String nowFoodWord : listFoodwords){
				//��Ʒ�ִʣ��ж��Ƿ������ǰ��Ʒ
				//������������
				if(!hasFoodword(dishes, nowFoodWord)) continue;
				//������vector��ӣ��ۼƸ��������µ�����
				if(mapFoodwordBase.get(nowFoodWord) == null){//���ж��Ƿ��������ݣ����򴴽�
					mapFoodwordBase.put(nowFoodWord, getBaseVector(14));
				}
				mapFoodwordBase.put(nowFoodWord, addVector(baseArray, mapFoodwordBase.get(nowFoodWord))); 
			}
		}
		U.print(mapFoodwordBase.toString());
		NLPIR.NlpirExit();
		
		//д�����ݿ�
	}
	private static Vector<Integer> getBaseVector(int size){
		Vector<Integer> v = new Vector<>();
		for(int i = 0; i < size; i++){
			v.add(0);
		}
		return v;
	}
	private static Vector<Integer> addVector(Vector<Integer> v0, Vector<Integer> v1){
		Vector<Integer> v = new Vector<>();
		for(int i = 0; i < v0.size(); i++){
			v.add(v0.get(i) + v1.get(i));
		}
		return v;
	}
	private static boolean hasFoodword(String dishes, String foodword){
		String[] foods = dishes.replaceAll("\"", "").replaceAll(" ", "")
				.replaceAll("[-��]", ",").replaceAll("\\+", ",").replaceAll("��", ",").replaceAll("ʮ", ",")
				.replaceAll("��", ",").replaceAll("\\?", ",")
				.replaceAll("��", ",").replaceAll("��", ",").replaceAll("��", ",").replaceAll("�B", ",").replaceAll("\\|", ",")
				.replace("\t", ",")
				.split(",");
		for(String food : foods){
			//����±Ѽ���ǡ���С�������±Ѽ���ǣ���У��������
			food = food.replaceAll("[����(����].*?[����)����]", "");
			//����**�齴**�������
			food = food.replaceAll("\\*", "");
			//������ʽ����\/�С��������¿���_����������������9��˫��֥��ԭ��89Ԫ�������������������
			food = food.replaceAll("\\\\/.*", "").replaceAll("_.*", "").replaceAll("kg", "��").replaceAll("(\\d+).", "")
					.replaceAll("��.", "").replaceAll("\\/.", "");
			//ȥ������ݡ�����С�ݡ���
			food = food.replaceAll("���", "").replaceAll("С��", "")
					.replaceAll("��һ", "").replaceAll("�ܶ�", "").replaceAll("����", "").replaceAll("һ��", "")
					.replaceAll("����", "").replaceAll("����", "").replaceAll("����", "").replaceAll("����", "")
					.replaceAll("ÿ��", "").replaceAll("[����,��]", "").replaceAll("\\.", "")
					.replaceAll("[����<>����һ!]", "").replaceAll("��", "").replaceAll("��", "").replaceAll("\\?", "")
					.replaceAll("A��", "").replaceAll("cal", "")
					.replaceAll("����װ", "").replaceAll("����", "").replaceAll("һ��", "").replaceAll("���", "")
					.replaceAll("\\-", "").replaceAll(" ", "").trim();
			//����Ϊ�յĲ�Ʒ
			if(food == null || food.length() == 0) continue;
			//�ִ�
			List<String> listFW = Arrays.asList(NLPIR.wordSegmentateWithoutCharacteristic(food));
			//����Ŀ��ʳ�ģ�����true
			if(listFW.contains(foodword))
				return true;
		}
		return false;
	}
	
	//�������ʳ���벻ͬ����ά�ȵ������
	public static void getFoodWordsWeatherRelativity(){
		
	}
	
	//����ʳ���ڸ��������µ���Ӧֵ
	public static void getFoodWordsWeatherResponse(){
		
	}
	
	
	
	
	
	
	
	
	
	
	private static void writeCSVNode(Map<String, Integer> mapKeywordId, Map<String, Integer> mapKeywordFrequency, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("Id,Label,weighted degree\r\n");
		for(Map.Entry<String, Integer> entry : mapKeywordId.entrySet())
			fw.write(entry.getValue() + "," + entry.getKey() + "," + mapKeywordFrequency.get(entry.getKey()) + "\r\n");
		fw.close();
	}
	private static void writeCSVLine(Map<String, Integer> mapLineWeight, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("Source,Target,Type,id,label,timeset,weight\r\n");
		int lineId = 0;
		for(Map.Entry<String, Integer> entry : mapLineWeight.entrySet()){
			String id1 = entry.getKey().split(",")[0];
			String id2 = entry.getKey().split(",")[1];
			fw.write(id1 + "," + id2 + "," + "unDirected" + "," + lineId++ + "," + "," + "," + entry.getValue() + "\r\n");
		}
		fw.close();
	}
	
}
