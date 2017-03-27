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
		int FOODWORDLIMIT = 1508;//��ȡtopN��ʳ��
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
				"and recommend_dishes != ''";
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
			//��Ʒ�ִ�
			List<String> segmentationResult = getFoodword(dishes);
			//����ʳƷ���ϣ��ж����м�¼�Ƿ��иò�Ʒ���������Ӹ�ά�ȵ���������������
			for(String nowFoodWord : listFoodwords){
				//�ж��Ƿ������ǰ��Ʒ,������������
				if(!segmentationResult.contains(nowFoodWord)) continue;
				//������vector��ӣ��ۼƸ��������µ�����
				if(mapFoodwordBase.get(nowFoodWord) == null){//���ж��Ƿ��������ݣ����򴴽�
					mapFoodwordBase.put(nowFoodWord, getBaseVector(14));
				}
				mapFoodwordBase.put(nowFoodWord, addVector(baseArray, mapFoodwordBase.get(nowFoodWord))); 
			}
		}
		NLPIR.NlpirExit();
		
		//д�����ݿ�
		DBFunction.insertFoodwordBase(mapFoodwordBase);
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
	private static List<String> getFoodword(String dishes){
		List<String> result = new ArrayList<>();
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
			result.addAll(Arrays.asList(NLPIR.wordSegmentateWithoutCharacteristic(food)));
		}
		return result;
	}
	
	
	
	//��ȡ�����̵��ڲ�ͬ�����ķֲ�
	public static void getShopnameWeatherBase() throws SQLException, FileNotFoundException, UnsupportedEncodingException{
		
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
		Map<String, Vector<Integer>> mapShopnameBase = new HashMap<>();
		
		//��ȡ���е���id(���ݿ���id)�����ֵĶ�Ӧ��ϵ
		Map<String, String> mapShopidShopname = new HashMap<>();
		List<String> listShopName = new ArrayList<>();
		sql = "select shop_id, shop_name from tb_baiduwaimaishop";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			String shopName = rs.getString("shop_name").split("��")[0].split("[(]")[0];//��ȡ����
			String shopID = rs.getString("shop_id");
			mapShopidShopname.put(shopID, shopName);
			if(!listShopName.contains(shopName))
				listShopName.add(shopName);
		}
		U.print("�ѽ�����id�������һһ��Ӧ����" + listShopName.size() + "�Ҳ�ͬ�����̼�");
		
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
		
		//��ѯ��������ȡshopid
		sql = "SELECT waimai_release_id, date(arrive_time) as t " +
				"FROM smda.tb_baiduwaimai where arrive_time > '2015-01-01' and arrive_time < '2016-05-01'";
		rs = DBFunction.getRS(sql);
		int timer = 0;
		while(rs.next()){
			//������ڴ���ڼ�������
			U.print("������" + ++timer + "������");
			//��ȡʳ��
			String shopid = rs.getString("waimai_release_id");
			String shopname = mapShopidShopname.get(shopid);
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
			
			if(mapShopnameBase.get(shopname) == null){//���ж��Ƿ��������ݣ����򴴽�
				mapShopnameBase.put(shopname, getBaseVector(14));
			}
			mapShopnameBase.put(shopname, addVector(baseArray, mapShopnameBase.get(shopname)));
		}
		
		//д�����ݿ�
		DBFunction.insertShopnameBase(mapShopnameBase);
	}
	
	
	
	
	
	
	
	
	
		//��ȡ���û��ڲ�ͬ�����ķֲ�
		public static void getUserWeatherBase2() throws SQLException, FileNotFoundException, UnsupportedEncodingException{
			
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
			Map<String, String> mapUidName = new HashMap<>();
			Map<String, List<String>> mapUidLocationid = new HashMap<>();
			Map<String, Vector<Integer>> mapUidBase = new HashMap<>();
			
			//shopid��locationname����
			//shopid��locationid����
			Map<String, String> mapShopidLocationname = new HashMap();
			Map<String, String> mapShopidLocationid = new HashMap<>();
			sql = "select A.shopid, A.locationid, B.locationname "
					+ "from smda.tb_shoplocation as A left join smda.tb_locationname as B "
					+ "on A.locationid = B.locationid";
			rs = DBFunction.getRS(sql);
			while(rs.next()){
				mapShopidLocationname.put(rs.getString("shopid"), rs.getString("locationname"));
				mapShopidLocationid.put(rs.getString("shopid"), rs.getString("locationid"));
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
			
			//��ѯ��������ȡ�û���
			sql = "SELECT pass_uid, pass_name, waimai_release_id, date(arrive_time) as t " +
					"FROM smda.tb_baiduwaimai where arrive_time > '2015-01-01' and arrive_time < '2016-05-01'";
			rs = DBFunction.getRS(sql);
			NLPIR.NlpirInit();
			int timer = 0;
			while(rs.next()){
				//������ڴ���ڼ�������
				U.print("������" + ++timer + "������");
				//��ȡ�û�
				String uid = rs.getString("pass_uid");
				String name = rs.getString("pass_name");
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
				//д��uid-name
				if(mapUidName.get(uid) == null)
					mapUidName.put(uid, name);
				//д��uid-locationid
				List<String> tempListLocationid = mapUidLocationid.get(uid);
				if(tempListLocationid == null) 
					tempListLocationid = new ArrayList<>();
				if(!tempListLocationid.contains(mapShopidLocationid.get(shopid))){
					tempListLocationid.add(mapShopidLocationid.get(shopid));
					mapUidLocationid.put(uid, tempListLocationid);
				}
				//д��uid-base
				if(mapUidBase.get(uid) == null){//���ж��Ƿ��������ݣ����򴴽�
					mapUidBase.put(uid, getBaseVector(14));
				}
				mapUidBase.put(uid, addVector(baseArray, mapUidBase.get(uid))); 
			}
			NLPIR.NlpirExit();
			
			//д�����ݿ�
			DBFunction.insertUserBase2(mapUidName, mapUidLocationid, mapUidBase);
		}
	
	

		
	//�����������������top20������ʳ�ģ���¼�����ݿ�
	//������ʾƽ������������ֵ
	public static void getWeatherIncreasefoods(int threshold) throws SQLException{
		int[] daycountA = {517, 2058, 2065, 515};
		int[] daycountB = {548, 2033, 2055, 519};
		int[] daycountC = {3548, 1356, 251};
		int[] daycountD = {3455, 1031, 669};
		Map<String, String> mapWeatherIncrease = new HashMap<>();
		for(int a = 0; a < 4; a++){//ƽ���¶�
			for(int b = 0; b < 4; b++){//�²�
				for(int c = 0; c < 3; c++){//�ܽ���
					for(int d = 0; d < 3; d++){//�ڼ���
						String sqlWorkday = d == 0 ? "workday_weekday" : d == 1 ? "workday_weekend" : "workday_holiday";
						String sql = "SELECT *, tp_avg0+tp_avg1+tp_avg2+tp_avg3+tp_df0+tp_df1+tp_df2+tp_df3+rain_sum0+rain_sum1+rain_sum2+workday_weekday+workday_weekend+workday_holiday as allCount FROM smda.foodword_base";
						ResultSet rs = DBFunction.getRS(sql);
						Map<String, Double> mapFoodIncrease = new HashMap<>();
						while(rs.next()){
							//��ȡ
							String food = rs.getString("foodword");
							double tpAvg = rs.getInt("tp_avg" + a);
							double tpDf = rs.getInt("tp_df" + b);
							double rainSum = rs.getInt("rain_sum" + c);
							double workday = rs.getInt(sqlWorkday);
							double all = rs.getDouble("allCount");
							//����
							double avgAll = all/4 / 469.55;
							if(avgAll < threshold) continue;
							double avgWeather = (11*tpAvg/daycountA[a] + 11*tpDf/daycountB[b] + 11*rainSum/daycountC[c] + 11*workday/daycountD[d]) / 4;
							double increase = (avgWeather/avgAll-1) * 100;
							mapFoodIncrease.put(food, increase);
						}
						//����
						TreeMap<String, Double> sort = U.sortMap2(mapFoodIncrease);
						String result = "";
						int limit = 30;
						for(Entry<String, Double> entry : sort.entrySet()){
							if(entry.getKey() == null) break;//����û��30������
							//���׵�ͣ�ôʴ���
							if(entry.getKey().equals("��") || entry.getKey().equals("��") || entry.getKey().equals("ƴ")) continue;
							if(limit-- == 0) break;
							result += entry.getKey() + ":" + entry.getValue() + ",";
						}
						result = result.substring(0, result.length()-1);
						mapWeatherIncrease.put(""+a+b+c+d, result);
					}
				}
			}
		}
		DBFunction.insertWeatherFoodIncrease(mapWeatherIncrease);
	}
	
	//������������¸������������������û�����¼�����ݿ�
	//������ʾ�û��ܵĹ�����������ֵ
	public static void getWeatherIncreaseUsers(int threshold) throws SQLException{
		//��ȡlocationid�б�
		List<String> listLocationid = new ArrayList<>();
		String sql = "SELECT locationid FROM smda.tb_locationname";
		ResultSet rs = DBFunction.getRS(sql);
		while(rs.next()){
			listLocationid.add(rs.getString("locationid"));
		}
		U.print("locationid�б��ȡ���,��" + listLocationid.size() + "��locationid");
		
		//��ȡlocationid��passid�Ķ�Ӧ
		Map<String, String> mapLocationidUsers = new HashMap<>();
		sql = "select passid, locationid from smda.user_weather2";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			String passid = "'" + rs.getString("passid") + "'";
			List<String> locationids = Arrays.asList(rs.getString("locationid").replace("[", "").replace("]", "").split(","));
			for(String locationid : locationids){
				if(mapLocationidUsers.get(locationid) == null)
					mapLocationidUsers.put(locationid, "");
				mapLocationidUsers.put(locationid, mapLocationidUsers.get(locationid) + passid + ",");
			}
		}
		U.print("locationid-passid��Ӧ���");
		
		//��ȡ����
		int[] daycountA = {517, 2058, 2065, 515};
		int[] daycountB = {548, 2033, 2055, 519};
		int[] daycountC = {3548, 1356, 251};
		int[] daycountD = {3455, 1031, 669};
		List<List<String>> list2WeatherIncrease = new ArrayList<>();
		int timer = 0;
		for(String locationid : listLocationid){//locationid
			U.print(timer++ + "/"+locationid);
			//���ж��Ƿ��и�id���û�
			if(mapLocationidUsers.get(locationid) == null)
				continue;
			String passids = mapLocationidUsers.get(locationid).substring(0, mapLocationidUsers.get(locationid).length()-1);
			sql = "SELECT *, tp_avg0+tp_avg1+tp_avg2+tp_avg3+tp_df0+tp_df1+tp_df2+tp_df3+rain_sum0+rain_sum1+rain_sum2+workday_weekday+workday_weekend+workday_holiday as allCount " +
					"FROM smda.user_weather2 where passid in ("
					+ passids + ")";
			rs = DBFunction.getRS(sql);
			Map<String, Double> mapUserIncrease = new HashMap<>();
			for(int a = 0; a < 4; a++){//ƽ���¶�
				for(int b = 0; b < 4; b++){//�²�
					for(int c = 0; c < 3; c++){//�ܽ���
						for(int d = 0; d < 3; d++){//�ڼ���
							List<String> listWeatherIncrease = new ArrayList<>();
							rs.beforeFirst();//�Ƚ��α��λ
							while(rs.next()){
								//��ȡ
								String sqlWorkday = d == 0 ? "workday_weekday" : d == 1 ? "workday_weekend" : "workday_holiday";
								String user = rs.getString("passname");
								double tpAvg = rs.getInt("tp_avg" + a);
								double tpDf = rs.getInt("tp_df" + b);
								double rainSum = rs.getInt("rain_sum" + c);
								double workday = rs.getInt(sqlWorkday);
								double all = rs.getDouble("allCount");
								//��ֵ
								if(all < threshold) continue;
								//����
								double avgAll = all/4 / 469.55;
								double avgWeather = (11*tpAvg/daycountA[a] + 11*tpDf/daycountB[b] + 11*rainSum/daycountC[c] + 11*workday/daycountD[d]) / 4;
								double increase = (avgWeather/avgAll-1) * 100;
								mapUserIncrease.put(user, increase);
							}
							//����
							TreeMap<String, Double> sort = U.sortMap2(mapUserIncrease);
							String result = "";
							int limit = 20;
							for(Entry<String, Double> entry : sort.entrySet()){
								if(entry.getKey() == null) break;//��һЩû��20���û�
								if(limit-- == 0) break;
								result += entry.getKey() + ":" + entry.getValue() + ",";
							}
							if(result.length() == 0) continue;//��һЩ��ȡ�����û�
							result = result.substring(0, result.length()-1);
							listWeatherIncrease.add(""+a+b+c+d);
							listWeatherIncrease.add(locationid);
							listWeatherIncrease.add(result);
							list2WeatherIncrease.add(listWeatherIncrease);
						}
					}
				}
			}
		}
		U.print("��ʼ�������ݿ�");
		DBFunction.insertWeatherUserIncrease(list2WeatherIncrease);
	}
	
	
	
	
	
	
	
	
	//����һ���û����ҳ϶�(Ŀǰֻ������Ϣ�أ���Ϣ��Խ�ߣ�Խ���ҳ�)
	public static void getUserLoyalty() throws SQLException{
		//��¼�û���������ĵ���
		Map<String, Map<String, Integer>> mapUserShopcount = new HashMap<>();
		String sql = "SELECT pass_uid, waimai_release_id FROM smda.tb_baiduwaimai";
		ResultSet rs = DBFunction.getRS(sql);
		while(rs.next()){
			String uid = rs.getString("pass_uid");
			String shopid = rs.getString("waimai_release_id");
			U.print(uid + "|" + shopid);
			Map<String, Integer> mapShopCount = mapUserShopcount.get(uid);
			if(mapShopCount == null)
				mapShopCount = new HashMap<>();
			U.mapAddCount(mapShopCount, shopid);
			mapUserShopcount.put(uid, mapShopCount);
		}
		//��ȡǰ���ҵ�ı���
		Map<String, List<String>> mapUserTop = new HashMap<>();
		for(Entry<String, Map<String, Integer>> entry : mapUserShopcount.entrySet()){
			mapUserTop.put(entry.getKey(), U.getMapTopPercentage(entry.getValue(), 3));
		}
		//������Ϣ��
		Map<String, Double> mapUserComentropy = new HashMap<>();
		for(Entry<String, Map<String, Integer>> entry : mapUserShopcount.entrySet()){
			mapUserComentropy.put(entry.getKey(), U.getComentropy(entry.getValue()));
		}
		//�������ݿ�
		DBFunction.insertUserLoyalty(mapUserComentropy, mapUserTop);
		U.print("done");
	}
	
	//��������ÿ�µ�ƽ���ҳ϶�(������ʾ�û�������̵����ֵ)
	public static void getUserAvgComentropy_ALL(int threshold) throws SQLException, IOException{
			Map<String, Map<String, Map<String, Integer>>> mapYearMonth_UserShopCount = new HashMap<>();
			//��¼ÿ�����û���������ĵ���
			String sql = "SELECT year(order_time) as year, month(order_time) as month, pass_uid, waimai_release_id " +
					"FROM smda.tb_baiduwaimai";
			ResultSet rs = DBFunction.getRS(sql);
			while(rs.next()){
				String uid = rs.getString("pass_uid");
				String shopid = rs.getString("waimai_release_id");
				String year = rs.getString("year");
				String month = rs.getString("month");
				Map<String, Map<String, Integer>> mapUserShopcount = mapYearMonth_UserShopCount.get(year + "-" + month);
				if(mapUserShopcount == null) 
					mapUserShopcount = new HashMap<>();
				Map<String, Integer> mapShopCount = mapUserShopcount.get(uid);
				if(mapShopCount == null) 
					mapShopCount = new HashMap<>();
				U.mapAddCount(mapShopCount, shopid);
				mapUserShopcount.put(uid, mapShopCount);
				mapYearMonth_UserShopCount.put(year + "-" + month, mapUserShopcount);
			}
			
			//����ÿ���µ�ƽ����Ϣ��
			Map<String, Double> mapYMComentropy = new HashMap<>();
			for(Entry<String, Map<String, Map<String, Integer>>> entry : mapYearMonth_UserShopCount.entrySet()){
				//��������Ϣ��
				double all = 0;
				int count = 0;
				for(Entry<String, Map<String, Integer>> entry2 : entry.getValue().entrySet()){
					if(entry2.getValue().size() < threshold) continue;//��ֵɸѡ
					count ++;
					all += U.getComentropy(entry2.getValue());
				}
				//����ƽ����Ϣ��
				double average = all / count;
				//��¼
				mapYMComentropy.put(entry.getKey(), average);
			}
			//д��txt
			FileFunction.writeMap_KV(mapYMComentropy, "E:\\work\\smda����+���ݾ���\\�û�\\all_avgComentropy.txt");
			U.print("done");
	}
	
	//����ĳ�ҵ�ÿ�µ�ƽ���ҳ϶�(������ʾ�û�������̵����ֵ)
	public static void getUserAvgComentropy_ShopName(String targetShopName, int threshold) throws SQLException, IOException{
			String sql = "";
			ResultSet rs = null;
			
			//����targetShopName��ȡĿ���̼����е�id
			String sqlShopId = "";
			sql = "select shop_id from tb_baiduwaimaishop where shop_name like '%" + targetShopName + "%'";
			rs = DBFunction.getRS(sql);
			while(rs.next()){
				sqlShopId += "'" + rs.getString("shop_id") + "'" + ",";
			}
			sqlShopId = sqlShopId.substring(0, sqlShopId.length()-1);
			
			//��ȡ�����û��б�
			String sqlUserId = "";
			sql = "SELECT pass_uid FROM smda.tb_baiduwaimai where waimai_release_id in (" + sqlShopId + ")";
			rs = DBFunction.getRS(sql);
			while(rs.next()){
				if(!sqlUserId.contains(rs.getString("pass_uid")))
					sqlUserId += "'" + rs.getString("pass_uid") + "'" + ",";
			}
			if(sqlUserId.length() == 0){
				U.print("�޼��������û�");
				return;//�޽��
			}
			sqlUserId = sqlUserId.substring(0, sqlUserId.length()-1);
			
			Map<String, Map<String, Map<String, Integer>>> mapYearMonth_UserShopCount = new HashMap<>();
			//��¼ÿ�����û���������ĵ���
			sql = "SELECT year(order_time) as year, month(order_time) as month, pass_uid, waimai_release_id FROM smda.tb_baiduwaimai" +
					" where pass_uid in (" + sqlUserId + ")";
			U.print(sql);
			rs = DBFunction.getRS(sql);
			while(rs.next()){
				String uid = rs.getString("pass_uid");
				String shopid = rs.getString("waimai_release_id");
				String year = rs.getString("year");
				String month = rs.getString("month");
				Map<String, Map<String, Integer>> mapUserShopcount = mapYearMonth_UserShopCount.get(year + "-" + month);
				if(mapUserShopcount == null) 
					mapUserShopcount = new HashMap<>();
				Map<String, Integer> mapShopCount = mapUserShopcount.get(uid);
				if(mapShopCount == null) 
					mapShopCount = new HashMap<>();
				U.mapAddCount(mapShopCount, shopid);
				mapUserShopcount.put(uid, mapShopCount);
				mapYearMonth_UserShopCount.put(year + "-" + month, mapUserShopcount);
			}
			
			//����ÿ���µ�ƽ����Ϣ��
			Map<String, Double> mapYMComentropy = new HashMap<>();
			for(Entry<String, Map<String, Map<String, Integer>>> entry : mapYearMonth_UserShopCount.entrySet()){
				//��������Ϣ��
				double all = 0;
				int count = 0;
				for(Entry<String, Map<String, Integer>> entry2 : entry.getValue().entrySet()){
					if(entry2.getValue().size() < threshold) continue;//��ֵɸѡ
					count ++;
					all += U.getComentropy(entry2.getValue());
				}
				//����ƽ����Ϣ��
				double average = all / count;
				//��¼
				mapYMComentropy.put(entry.getKey(), average);
			}
			//д��txt
			FileFunction.writeMap_KV(mapYMComentropy, "E:\\work\\smda����+���ݾ���\\�û�\\" + targetShopName + "_avgComentropy.txt");
			U.print("done");
	}
	
	
	//����ÿ�ҵ��̵�ƽ���ҳ϶�,������ʾ����̵���û���������Ӧ���Ƕ���
	public static void getUserAvgComentropy_ByShopid(int threshold) throws SQLException{
		String sql = "";
		ResultSet rs = null;
		
		//���� shopid-userid
		Map<String, List<String>> mapShopidPassuids = new HashMap<>();
		sql = "SELECT waimai_release_id, pass_uid FROM smda.tb_baiduwaimai";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			String shopid = rs.getString("waimai_release_id");
			String passuid = "'" + rs.getString("pass_uid") + "'";
			List<String> passuids = mapShopidPassuids.get(shopid);
			if(passuids == null)
				passuids = new ArrayList<>();
			if(!passuids.contains(passuid))
				passuids.add(passuid);
			mapShopidPassuids.put(shopid, passuids);
		}
		
		U.print(mapShopidPassuids.size());
		
		//����ÿ��shopnid��Ӧ��ƽ����Ϣ��
		int timer = 0;
		Map<String, Double> mapShopidAvgcomentropy = new HashMap<>();
		for(Entry<String, List<String>> entry : mapShopidPassuids.entrySet()){
			U.print("�����" + timer++ + "�ҹ�˾");
			List<String> passuids = entry.getValue();
			if(passuids.size() < threshold) continue;
			String sqlPassuids = "";
			for(String uid : passuids){
				sqlPassuids += uid + ",";
			}
			sql = "SELECT comentropy FROM smda.user_loyalty where pass_uid in (" + sqlPassuids.substring(0, sqlPassuids.length()-1) + ")";
			rs = DBFunction.getRS(sql);
			int count = 0;
			double all = 0;
			while(rs.next()){
				count ++;
				all += rs.getDouble("comentropy");
			}
			U.print("�û�����:" + count);
			if(count == 0) continue;
			mapShopidAvgcomentropy.put(entry.getKey(), all/count);
		}
		U.print("��˾����:" + mapShopidAvgcomentropy.size());
		
		//�������ݿ�
		DBFunction.insertShopidLoyalty(mapShopidAvgcomentropy);
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
