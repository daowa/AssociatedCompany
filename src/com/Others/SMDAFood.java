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
		//查询
		String sql = "SELECT * FROM smda.tb_baiduwaimai where recommend_dishes != ''";
		ResultSet rs = DBFunction.getRS(sql);
		Map<String, Integer> mapFood = new HashMap<String, Integer>();
		while(rs.next()){
			String[] foods = rs.getString("recommend_dishes").replaceAll("\"", "").replaceAll(" ", "")
					.replaceAll("[-、]", ",").replaceAll("\\+", ",").replaceAll("＋", ",").replaceAll("十", ",")
					.replaceAll("？", ",").replaceAll("\\?", ",")
					.replaceAll("加", ",").replaceAll("含", ",").replaceAll("送", ",").replaceAll("˙", ",").replaceAll("\\|", ",")
					.replace("\t", ",")
					.split(",");
			for(String food : foods){
				//处理“卤鸭锁骨【大盒】”，“卤鸭锁骨（大盒）”的情况
				food = food.replaceAll("[【（(《＜].*?[】）)》＞]", "");
				//处理“**麻酱**”的情况
				food = food.replaceAll("\\*", "");
				//处理“美式薯条\/盒”、“百事可乐_冰”、“肉香四溢9寸双层芝心原价89元”、“腐竹／两”的情况
				food = food.replaceAll("\\\\/.*", "").replaceAll("_.*", "").replaceAll("kg", "克").replaceAll("(\\d+).", "")
						.replaceAll("／.", "").replaceAll("\\/.", "");
				//去掉“大份”、“小份”等
				food = food.replaceAll("大份", "").replaceAll("小份", "")
						.replaceAll("周一", "").replaceAll("周二", "").replaceAll("周三", "").replaceAll("一周", "")
						.replaceAll("周四", "").replaceAll("周五", "").replaceAll("周六", "").replaceAll("周日", "")
						.replaceAll("每份", "").replaceAll("[。★,，]", "").replaceAll("\\.", "")
						.replaceAll("[〈〉<>（）一!]", "").replaceAll("“", "").replaceAll("”", "").replaceAll("\\?", "")
						.replaceAll("A梦", "").replaceAll("cal", "")
						.replaceAll("单个装", "").replaceAll("单个", "").replaceAll("一个", "").replaceAll("免费", "")
						.replaceAll("\\-", "").replaceAll(" ", "").trim();
				//处理为空的餐品
				if(food == null || food.length() == 0) continue;
				U.print(food);
				mapFood.put(food, mapFood.get(food) == null ? 1 : mapFood.get(food)+1);
			}
		}
		//排序
		TreeMap<String, Integer> sorted_map = U.sortMap(mapFood);
		U.print("共有餐品数：" + sorted_map.size());
		//输出
		FileFunction.writeMap_KV(sorted_map, "E:\\work\\smda气象+数据竞赛\\foodList.txt");//将公司名和出现频次输出
	}
	
	public static void getFoodWordsList() throws NumberFormatException, IOException{
		//读取食品列表
		Map<String, Integer> mapTemp = FileFunction.readMap_SI("E:\\work\\smda气象+数据竞赛\\foodList.txt");
		TreeMap<String, Integer> mapFoodFrequency = U.sortMap(mapTemp);
		U.print("共有食物：" + mapFoodFrequency.size());
		
		//分词
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
		
		//去除停用词
		String[] stopWords = {"【", "】", "[", "]", "?", "？", "&", "#", "@", "!", "！"};
 		for(String stop : stopWords){
 			if(mapWordFrequency.get(stop) != null)
 				mapWordFrequency.remove(stop);
 		}
		
		//将分词后的食物列表写入，以便于发现新词
		FileWriter fw = new FileWriter("E:\\work\\smda气象+数据竞赛\\foodSeperate.txt");
		for(String food : listFood){
			fw.write(food + "\r\n");
		}
		fw.close();
		U.print("写入foodSeperate成功");
		//发现新词、读入用户词典等，只需要一次操作
//		findNewWord(mapFoodFrequency);//发现新词
		
		//输出词频
 		TreeMap<String, Integer> sorted_map = U.sortMap(mapWordFrequency);
		U.print("共有词数：" + sorted_map.size());
		FileFunction.writeMap_KV(sorted_map, "E:\\work\\smda气象+数据竞赛\\foodWordFrequency.txt");//将词频输出
	}
	
	public static void getFoodWordsNet(int thresholdNode, int thresholdLine) throws IOException{
		//读取word-frequency
		U.print("读取 词-词频 文件");
		Map<String, Integer> mapWordFrequency = FileFunction.readMap_SI("E:\\work\\smda气象+数据竞赛\\foodWordFrequency.txt");
		//为超过阈值和非停用的词设置id
		//读取停用词
		U.print("读取停用词");
		List<String> listStops = FileFunction.readFile("E:\\work\\smda气象+数据竞赛\\vocabulary\\stopWords.txt");
		U.print("处理超过阈值和非停用词的词");
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
		//读取分词
		U.print("读取分词");
		List<String> listSeperate = FileFunction.readFile("E:\\work\\smda气象+数据竞赛\\foodSeperate.txt");
		//计算线
		U.print("计算线的权值");
		Map<String, Integer> mapLineWeight = new HashMap<>();
		for(String temp : listSeperate){
			String[] keywords = temp.split(",");
			int weight = Integer.parseInt(keywords[keywords.length-1]);
			for(int i = 0; i < keywords.length-1; i++){
				if(mapWordId.get(keywords[i]) == null) continue;//通过有无id来判断是否通过点的筛选
				for(int j = i+1; j < keywords.length-1; j++){
					if(mapWordId.get(keywords[j]) == null) continue;
					String line = U.getCompareString(mapWordId.get(keywords[i]) + "", mapWordId.get(keywords[j]) + "");
					mapLineWeight.put(line, mapLineWeight.get(line) == null ? weight : mapLineWeight.get(line)+weight);
				}
			}
		}
		//筛选线
		U.print("筛选线");
		Iterator<Map.Entry<String, Integer>> it = mapLineWeight.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = it.next();
			if(entry.getValue() < thresholdLine)
				it.remove();
		}
		//排序线，并输出，用于组合找出新词
		U.print("排序线");
		TreeMap<String, Integer> sorted_map = U.sortMap(mapLineWeight);
		FileWriter fw = new FileWriter("E:\\work\\smda气象+数据竞赛\\foodLineWeight.txt");
		for(Map.Entry<String, Integer> entry : sorted_map.entrySet()){
			String s1 = mapIdword.get(Integer.parseInt(entry.getKey().split(",")[0]));
			String s2 = mapIdword.get(Integer.parseInt(entry.getKey().split(",")[1]));
			fw.write(s1 + "," + s2 + ":" + entry.getValue() + "\r\n");
		}
		fw.close();
		U.print("写入foodLineWeight成功");
		
		writeCSVNode(mapWordId, mapWordFrequency, "E:\\work\\smda气象+数据竞赛\\foodNetNode.csv");
		writeCSVLine(mapLineWeight, "E:\\work\\smda气象+数据竞赛\\foodNetLine.csv");
		U.print("done");
		
	}
	
	//发现新词，需人工添加进用户词典
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
	
	
	//获取各种食材在不同天气的分布
	public static void getFoodWordsWeatherBase() throws SQLException, FileNotFoundException, UnsupportedEncodingException{
		
		//初始化定值
		int FOODWORDLIMIT = 2;//读取topN的食材
		//各天气销量数组的下标
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
		//各天气区间的分割数值
		double INTERVAL_TPAVG_01 = 4.62918;
		double INTERVAL_TPAVG_12 = 15.15;
		double INTERVAL_TPAVG_23 = 26.275;
		double INTERVAL_TPDF_01 = 2.8;
		double INTERVAL_TPDF_12 = 6.7;
		double INTERVAL_TPDF_23 = 11.6;
		double INTERVAL_RAIN_01 = 0.0001;
		double INTERVAL_RAIN_12 = 25;
		
		//初始化变量
		String sql;
		ResultSet rs;
		Map<String, Vector<Integer>> mapFoodwordBase = new HashMap<>();
		
		//shopid和locationname关联
		Map<String, String> mapShopidLocationname = new HashMap();
		sql = "select A.shopid, B.locationname "
				+ "from smda.tb_shoplocation as A left join smda.tb_locationname as B "
				+ "on A.locationid = B.locationid";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			mapShopidLocationname.put(rs.getString("shopid"), rs.getString("locationname"));
		}
		U.print("shopid-locationname对应完成，共" + mapShopidLocationname.size() + "家不同id商家");
		
		//locationname和天气关联
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
		U.print("locaiontname&dTime-weather对应完成");
		
		//获取topN的材料
		List<String> listFoodwords = new ArrayList<>();
		List<String> lines = FileFunction.readFile("E:\\work\\smda气象+数据竞赛\\foodWordFrequency.txt");
		for(int i = 0; i < FOODWORDLIMIT; i++){
			listFoodwords.add(lines.get(i).split("\t")[0]);
		}
		
		//查询外卖表，获取非空的推荐菜品
		sql = "SELECT recommend_dishes, waimai_release_id, date(arrive_time) as t " +
				"FROM smda.tb_baiduwaimai where arrive_time > '2015-01-01' and arrive_time < '2016-05-01'" +
				"and recommend_dishes != '' limit 100";
		rs = DBFunction.getRS(sql);
		NLPIR.NlpirInit();
		int timer = 0;
		while(rs.next()){
			//输出现在处理第几条数据
			U.print("处理到第" + ++timer + "条订单");
			//获取食材
			String dishes = rs.getString("recommend_dishes");
			String shopid = rs.getString("waimai_release_id");
			String time = rs.getString("t");
			//获取天气特征
			if(mapShopidLocationname.get(shopid) == null) continue;//对于缺失地址信息的shopid，跳过
			String weatherFeature[] = mapLocationWeather.get(mapShopidLocationname.get(shopid) + time);
			//将天气特征编入销量数组
			Vector<Integer> baseArray = getBaseVector(14);
			//温度特征
			double tpavg = Double.parseDouble(weatherFeature[INDEX_TPAVG]);
			if(tpavg < INTERVAL_TPAVG_01)
				baseArray.set(INDEXBASE_TPAVG_0, 1);
			else if(INTERVAL_TPAVG_01 <= tpavg && tpavg < INTERVAL_TPAVG_12)
				baseArray.set(INDEXBASE_TPAVG_1, 1);
			else if(INTERVAL_TPAVG_12 <= tpavg && tpavg < INTERVAL_TPAVG_23)
				baseArray.set(INDEXBASE_TPAVG_2, 1);
			else if(INTERVAL_TPAVG_23 <= tpavg)
				baseArray.set(INDEXBASE_TPAVG_3, 1);
			//温差特征
			double tpdf = Double.parseDouble(weatherFeature[INDEX_TPDF]);
			if(tpdf < INTERVAL_TPDF_01)
				baseArray.set(INDEXBASE_TPDF_0, 1);
			else if(INTERVAL_TPDF_01 <= tpdf && tpdf < INTERVAL_TPDF_12)
				baseArray.set(INDEXBASE_TPDF_1, 1);
			else if(INTERVAL_TPDF_12 <= tpdf && tpdf < INTERVAL_TPDF_23)
				baseArray.set(INDEXBASE_TPDF_2, 1);
			else if(INTERVAL_TPDF_23 <= tpdf)
				baseArray.set(INDEXBASE_TPDF_3, 1);
			//降雨特征
			double rainsum = Double.parseDouble(weatherFeature[INDEX_RAINSUM]);
			if(rainsum < INTERVAL_RAIN_01)
				baseArray.set(INDEXBASE_RAIN_0, 1);
			else if(INTERVAL_RAIN_01 <= rainsum && rainsum < INTERVAL_RAIN_12)
				baseArray.set(INDEXBASE_RAIN_1, 1);
			else if(INTERVAL_RAIN_12 <= rainsum)
				baseArray.set(INDEXBASE_RAIN_2, 1);
			//节假日特征
			String workday = weatherFeature[INDEX_WORKDAY];
			if(workday.equals("工作日"))
				baseArray.set(INDEXBASE_ISWORKDAY_WD, 1);
			else if(workday.equals("周末"))
				baseArray.set(INDEXBASE_ISWORKDAY_WK, 1);
			else
				baseArray.set(INDEXBASE_ISWORKDAY_HO, 1);
			//遍历食品材料，判断现有记录是否有该菜品。有则增加各维度的销量，无则跳过
			for(String nowFoodWord : listFoodwords){
				//菜品分词，判断是否包含当前餐品
				//不包含则跳过
				if(!hasFoodword(dishes, nowFoodWord)) continue;
				//包含则vector相加，累计各种天气下的销量
				if(mapFoodwordBase.get(nowFoodWord) == null){//先判断是否已有数据，无则创建
					mapFoodwordBase.put(nowFoodWord, getBaseVector(14));
				}
				mapFoodwordBase.put(nowFoodWord, addVector(baseArray, mapFoodwordBase.get(nowFoodWord))); 
			}
		}
		U.print(mapFoodwordBase.toString());
		NLPIR.NlpirExit();
		
		//写入数据库
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
				.replaceAll("[-、]", ",").replaceAll("\\+", ",").replaceAll("＋", ",").replaceAll("十", ",")
				.replaceAll("？", ",").replaceAll("\\?", ",")
				.replaceAll("加", ",").replaceAll("含", ",").replaceAll("送", ",").replaceAll("˙", ",").replaceAll("\\|", ",")
				.replace("\t", ",")
				.split(",");
		for(String food : foods){
			//处理“卤鸭锁骨【大盒】”，“卤鸭锁骨（大盒）”的情况
			food = food.replaceAll("[【（(《＜].*?[】）)》＞]", "");
			//处理“**麻酱**”的情况
			food = food.replaceAll("\\*", "");
			//处理“美式薯条\/盒”、“百事可乐_冰”、“肉香四溢9寸双层芝心原价89元”、“腐竹／两”的情况
			food = food.replaceAll("\\\\/.*", "").replaceAll("_.*", "").replaceAll("kg", "克").replaceAll("(\\d+).", "")
					.replaceAll("／.", "").replaceAll("\\/.", "");
			//去掉“大份”、“小份”等
			food = food.replaceAll("大份", "").replaceAll("小份", "")
					.replaceAll("周一", "").replaceAll("周二", "").replaceAll("周三", "").replaceAll("一周", "")
					.replaceAll("周四", "").replaceAll("周五", "").replaceAll("周六", "").replaceAll("周日", "")
					.replaceAll("每份", "").replaceAll("[。★,，]", "").replaceAll("\\.", "")
					.replaceAll("[〈〉<>（）一!]", "").replaceAll("“", "").replaceAll("”", "").replaceAll("\\?", "")
					.replaceAll("A梦", "").replaceAll("cal", "")
					.replaceAll("单个装", "").replaceAll("单个", "").replaceAll("一个", "").replaceAll("免费", "")
					.replaceAll("\\-", "").replaceAll(" ", "").trim();
			//处理为空的餐品
			if(food == null || food.length() == 0) continue;
			//分词
			List<String> listFW = Arrays.asList(NLPIR.wordSegmentateWithoutCharacteristic(food));
			//包含目标食材，返回true
			if(listFW.contains(foodword))
				return true;
		}
		return false;
	}
	
	//计算各种食材与不同天气维度的相关性
	public static void getFoodWordsWeatherRelativity(){
		
	}
	
	//计算食材在各种天气下的响应值
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
