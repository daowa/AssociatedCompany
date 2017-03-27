package com.Others;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.db.DBFunction;
import com.db.ExcelFunction;
import com.db.FileFunction;
import com.myClass.U;
import com.mysql.fabric.xmlrpc.base.Array;

public class TravelStream {

	//输出采集驴妈妈所要用的网页地址
	public static void outputLvMaMa() throws IOException{
		//声明定值
		int EXCELINDEX_SPOT = 0;
		int EXCELINDEX_ID = 1;
		int EXCELINDEX_PAGENUMBER = 2;
		//声明变量
		Map<String, Integer> mapSpotID = new HashMap();
		Map<String, Integer> mapSpotPagenumber = new HashMap<>();
		//将excel中的信息读入
		XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\迪士尼旅游流\\数据采集文档\\驴妈妈.xlsx", 0);
		for(int i = 35; i < 42; i++){
			String spot = U.getCellStringValue(sheet.getRow(i).getCell(EXCELINDEX_SPOT));
			String sid = U.getCellStringValue(sheet.getRow(i).getCell(EXCELINDEX_ID));
			int id = Integer.parseInt(sid.substring(0, sid.length()-2));
			String spagenumber = U.getCellStringValue(sheet.getRow(i).getCell(EXCELINDEX_PAGENUMBER));
			int pagenumber = Integer.parseInt(spagenumber.substring(0, spagenumber.length()-2));
			mapSpotID.put(spot, id);
			mapSpotPagenumber.put(spot, pagenumber);
		}
		//写入txt
		List<String> listHTML = new ArrayList<>();
		for(Entry<String, Integer> entry : mapSpotID.entrySet()){
			for(int i = 1; i <= mapSpotPagenumber.get(entry.getKey()); i++){
				listHTML.add("http://ticket.lvmama.com/vst_front/comment/newPaginationOfComments?" +
						"type=all&currentPage=" + i +
						"&totalCount=0&placeId=" + mapSpotID.get(entry.getKey())+
						"&productId=&placeIdType=PLACE&isPicture=&isBest=&isPOI=Y&isELong=N");
			}
		}
		FileFunction.writeList(listHTML, "E:\\work\\迪士尼旅游流\\数据采集文档\\驴妈妈.txt");
	}
	
	//计算有多少用户游历过多个景点
	public static void calculate_users() throws SQLException, IOException{
		ResultSet rs = DBFunction.getRS("select user_name from travelstream.lvmama");
		Map<String, Integer> mapNameCount = new HashMap<>();
		while(rs.next()){
			U.mapAddCount(mapNameCount, rs.getString("user_name").replaceAll("\t", ""));
		}
		
		int count = 0;
		for(Entry<String, Integer> entry : mapNameCount.entrySet()){
			if(entry.getValue() > 1)
				count++;
		}
		U.print(count);
		
		//排序
		TreeMap<String, Integer> sorted_mapName = U.sortMap(mapNameCount);
		
		//输出
		FileFunction.writeMap_KV(sorted_mapName, "E:\\work\\迪士尼旅游流\\数据分析\\map_userName.txt");
	}
	
	//输出网络格式的文件
	//参数表示是上海迪士尼开业前还是开业后，开业前是before，开业后是after，所有是all，开业前同期是beforeSame
	public static void outputNet(String timeType) throws SQLException, IOException{
		//读入景点以及其对应的id
		Map<Integer, String> mapIdSpot = new HashMap<>();
		Map<String, Integer> mapSpotId = new HashMap<>();
		List<String> listSpots = FileFunction.readFile("E:\\work\\迪士尼旅游流\\数据分析\\spotList.txt");
		for(int i = 0; i < listSpots.size(); i++){
			mapIdSpot.put(i+1, listSpots.get(i));
			mapSpotId.put(listSpots.get(i), i+1);
		}
		
		//读入用户表
		Map<String, Integer> mapUsersCount = FileFunction.readMap_SI("E:\\work\\迪士尼旅游流\\数据分析\\map_userName.txt");
		//读入停用的用户名
		List<String> listStopUsers = FileFunction.readFile("E:\\work\\迪士尼旅游流\\数据分析\\stopUsers.txt");
		
		//读取数据库，获取网络图的信息
		Map<String, Integer> mapTravelStream = new HashMap<>();
		Map<String, Integer> mapSameDayStream = new HashMap<>();
		List<String> listTravelStream = new ArrayList<>();//旅游流的线，如“1-2”的形式，有向线
		List<String> listSameDayStream = new ArrayList<>();//同一天的旅游流，如“1-2-3”的形式，无向线
		for(Entry<String, Integer> entry : mapUsersCount.entrySet()){
			//过滤条件
			if(listStopUsers.contains(entry.getKey())) continue;//过滤停用的用户名
			if(entry.getValue() < 2) continue;//过滤无法形成网络的数据
			//从数据库获取该用户数据
			String timeSql = "";
			if(timeType.equals("before"))
				timeSql = " and time < '2016-06-16'";
			else if(timeType.equals("after"))
				timeSql = " and time >= '2016-06-16'";
			else if(timeType.equals("beforeSame"))
				timeSql = " and time >= '2015-06-16' and time <= '2016-02-27'";
			String sql = "SELECT spot_name, unix_timestamp(time) as t FROM travelstream.lvmama where user_name = \"" + entry.getKey() + "\"" +
					timeSql + " order by time";
			U.print(sql);
			ResultSet rs = DBFunction.getRS(sql);
			int lastTime = -1;
			String lastSpot = "";
			String sameDayStream = "";//记录samedayStream
			while(rs.next()){
				String nowSpot = getNormalizeSpot(listSpots, rs.getString("spot_name"));//获取标准化的景区名
				int nowTime = Integer.parseInt(rs.getString("t"));
				if(lastSpot.equals("") && lastTime == -1){//第一次，只更换lastSpot和lastTime就跳过
					lastSpot = nowSpot;
					lastTime = nowTime;
					continue;
				}
				if(lastSpot.equals(nowSpot)){//如果两个景点相同，则跳过
					lastSpot = nowSpot;
					lastTime = nowTime;
					continue;
				}
				if(nowTime == lastTime){//如果两个是同一天，记录为SameDayStream，并跳过
					if(sameDayStream == "")//第一次，需要加上原有的景点
						sameDayStream += lastSpot + ",";
					sameDayStream += nowSpot + ",";
					lastSpot = nowSpot;
					lastTime = nowTime;
					continue;//如果两个是同一天，跳过
				}
				if(nowTime - lastTime > 604800) continue;//如果两次点评相隔超过一周，认为是两次旅游，不形成旅游流
				//记录旅游流信息
				String line = lastSpot + "," + nowSpot;
				listTravelStream.add(line);
				U.mapAddCount(mapTravelStream, line);
				//更新lastSpot和lastTime
				lastSpot = nowSpot;
				lastTime = nowTime;
			}
			//记录SameDayStream信息
			if(sameDayStream.contains(","))//去除最后一个","
				sameDayStream = sameDayStream.substring(0, sameDayStream.length()-1);
			if(sameDayStream.split(",").length > 1){//超过一天，说明存在至少两个景点在同一天逛过
				U.print(sameDayStream);
				listSameDayStream.add(sameDayStream);
				sameDayStream = "";
			}
		}
		FileFunction.writeList(listTravelStream, "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "listTravelStream.txt");
		FileFunction.writeList(listSameDayStream, "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "listSameDayStream.txt");
		//将一日旅游流也写入map
		int countSameDay = 0;
		for(String sameDayStream : listSameDayStream){
			String[] spots = sameDayStream.split(",");
			if(spots.length > 2) continue;//不考虑一天游历超过三个景点的情况
			countSameDay++;
			for(int i = 0; i < spots.length; i++){
				for(int j = i+1; j < spots.length; j++){
					String line = U.getCompareString(spots[i], spots[j]);
					U.mapAddCount(mapSameDayStream, line);
				}
			}
		}
		U.print("每日旅游流:" + countSameDay);
		
		//输出网络图
		String pathNode = "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "Node.csv";
		writeCSV_Node(mapIdSpot, pathNode);
		
		//包含可信旅游流和单日旅游流的网络
		String pathLine_integrityStream = "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "_ig_Line.csv";
		String pathNet_integrityStream = "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "_ig_Net.net";
		writeCSV_Line_IntegrityStream(mapTravelStream, mapSameDayStream, mapSpotId, pathLine_integrityStream);
		writeNet_IntegrityStream(mapIdSpot, mapSpotId, mapTravelStream, mapSameDayStream, pathNet_integrityStream);
		
		//仅可信旅游流的网络
		String pathLine_onlyDependableStream = "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "_od_Line.csv";
		String pathNet_onlyDependableStream = "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "_od_Net.net";
		writeCSV_Line_OnlyDependableStream(mapTravelStream, mapSpotId, pathLine_onlyDependableStream);
		writeNet_OnlyDependableStream(mapIdSpot, mapSpotId, mapTravelStream, pathNet_onlyDependableStream);
		
		//仅单日旅游流的网络
		String pathLine_sameDayStream = "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "_sd_Line.csv";
		String pathNet_sameDayStream = "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "_sd_Net.net";
		writeCSV_Line_SameDayStream(mapSameDayStream, mapSpotId, pathLine_sameDayStream);
		writeNet_SameDayStream(mapIdSpot, mapSpotId, mapSameDayStream, pathNet_sameDayStream);
	}
	
	//输出每个游客的旅游流
	public static void outputUserStream(String timeType) throws SQLException, IOException{
		//读入景点以及其对应的id
		Map<Integer, String> mapIdSpot = new HashMap<>();
		Map<String, Integer> mapSpotId = new HashMap<>();
		List<String> listSpots = FileFunction.readFile("E:\\work\\迪士尼旅游流\\数据分析\\spotList.txt");
		for(int i = 0; i < listSpots.size(); i++){
			mapIdSpot.put(i+1, listSpots.get(i));
			mapSpotId.put(listSpots.get(i), i+1);
		}
		
		//读入用户表
		Map<String, Integer> mapUsersCount = FileFunction.readMap_SI("E:\\work\\迪士尼旅游流\\数据分析\\map_userName.txt");
		//读入停用的用户名
		List<String> listStopUsers = FileFunction.readFile("E:\\work\\迪士尼旅游流\\数据分析\\stopUsers.txt");
		
		//读取数据库，获取用户旅游流
		Map<String, String> mapUserStream = new HashMap<>();
		Map<String, Integer> mapUserStreamCount = new HashMap<>();
		for(Entry<String, Integer> entry : mapUsersCount.entrySet()){
			//过滤条件
			if(listStopUsers.contains(entry.getKey())) continue;//过滤停用的用户名
			if(entry.getValue() < 2) continue;//过滤无法形成网络的数据
			//从数据库获取该用户数据
			String timeSql = "";
			if(timeType.equals("before"))
				timeSql = " and time < '2016-06-16'";
			else if(timeType.equals("after"))
				timeSql = " and time >= '2016-06-16'";
			else if(timeType.equals("beforeSame"))
				timeSql = " and time >= '2015-06-16' and time <= '2016-02-27'";
			String sql = "SELECT user_name, spot_name, unix_timestamp(time) as t FROM travelstream.lvmama where user_name = \"" + entry.getKey() + "\"" +
					timeSql + " order by time";
			U.print(sql);
			ResultSet rs = DBFunction.getRS(sql);
			int lastTime = -1;
			String lastSpot = "";
			String stream = "";
			while(rs.next()){
				String nowSpot = getNormalizeSpot(listSpots, rs.getString("spot_name"));//获取标准化的景区名
				int nowTime = Integer.parseInt(rs.getString("t"));
				if(lastSpot.equals("") && lastTime == -1){//第一次，只更换lastSpot和lastTime就跳过
					lastSpot = nowSpot;
					lastTime = nowTime;
					continue;
				}
				if(lastSpot.equals(nowSpot)){//如果两个景点相同，则跳过
					lastSpot = nowSpot;
					lastTime = nowTime;
					continue;
				}
				if(nowTime - lastTime > 604800) continue;//如果两次点评相隔超过一周，认为是两次旅游，不形成旅游流
				//记录旅游流信息
				if(stream.equals(""))
					stream = lastSpot + "," + nowSpot;
				else
					stream += "," + nowSpot;
				//更新lastSpot和lastTime
				lastSpot = nowSpot;
				lastTime = nowTime;
			}
			if(stream.split(",").length > 1){
				mapUserStream.put(entry.getKey(), stream);
				mapUserStreamCount.put(entry.getKey(), stream.split(",").length);
			}
		}
		
		//输出
		String path_userStream = "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "_userStream.txt";
		String path_userStreamCount = "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "_userStreamCount.txt";
		FileFunction.writeMap_KV(mapUserStream, path_userStream);
		FileFunction.writeMap_KV(mapUserStreamCount, path_userStreamCount);
	}
	
	//计算上海迪士尼在第几个景点
	public static void getDisneyRank() throws NumberFormatException, IOException{
		//初始化
		int[] disneyRank = new int[20];
		//读取游客旅游流数据
		Map<String, String> mapUserStream = FileFunction.readMap_SS("E:\\work\\迪士尼旅游流\\数据分析\\after_userStream.txt");
		//遍历，统计上海迪士尼在第几位
		for(Entry<String, String> entry : mapUserStream.entrySet()){
			String[] stream = entry.getValue().split(",");
			for(int i = 0; i < stream.length; i++){
				if(stream[i].equals("上海野生动物园"))
					disneyRank[i]++;
			}
		}
		//打印出结果
		for(int i = 0; i < disneyRank.length; i++)
			U.print(i+1 + ":" + disneyRank[i]);
	}
	
	//根据数据库中spot_name字段，获取标准化的景点名
	private static String getNormalizeSpot(List<String> listSpots, String spot){
		if(spot.contains("梅花节")){
			spot = "上海海湾国家森林公园";
		}
		else if(spot.contains("月湖")){
			spot = "上海月湖雕墅公园";
		}
		else if(spot.contains("陈云故居暨青浦历史纪念馆")){
			spot = "陈云纪念馆";
		}
		else if(spot.contains("西沙湿地")){
			spot = "明珠湖";
		}
		else if(spot.contains("朱家角") || spot.contains("访古游8点联票成人票【景点特卖】")){
			spot = "朱家角古镇";
		}
		else if(spot.contains("上海崇明前卫村")){
			spot = "前卫生态村";
		}
		else if(spot.contains("2015新春军事套票【春节半价特惠】")){
			spot = "前卫生态村";
		}
		else if(spot.contains("成人票+球幕电影票") || spot.contains("球幕电影票")
				|| spot.contains("成人票+立体电影票") || spot.contains("成人票+立体电影票+立体电影票")){
			spot = "东方绿舟";
		}
		else if(spot.contains("吴淞口炮台湾湿地森林公园")){
			spot = "上海炮台湾";
		}
		else if(spot.contains("“三八”妇女节") || spot.contains("探秘动物套票——老虎扑食")
				|| spot.contains("成人票【景点特卖】") || spot.contains("成人票【团购】")){
			spot = "上海野生动物园";
		}
		else if(spot.contains("杭州烂苹果乐园") || spot.contains("儿童乐园100元现金券")){
			spot = "长风";
		}
		else if(spot.contains("上海太阳岛国际旅游度假区")){
			spot = "上海太阳岛国际俱乐部";
		}
		else if(spot.contains("上海玛雅海滩水公园") || spot.contains("江、浙大学生票【凭本人有效江浙全日制大学生证及身份证验证入园】")
				|| spot.contains("日场成人票【刷本人有效身份证入园】") || spot.contains("日场情侣票【2.14-15情人节，18周岁以上男女同行一人免单】")
				|| spot.contains("夜场成人票【刷本人有效身份证入园】")){
			spot = "上海欢乐谷";
		}
		else if(spot.contains("上海古猗园") || spot.contains("南翔古漪园")){
			spot = "南翔古猗园";
		}
		
		for(String key : listSpots){
			if(spot.contains(key)){//找到景区了，进行操作
				return key;
			}
		}
		return "-1";
	}
	
	private static void writeCSV_Node(Map<Integer, String> mapIdSpot, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("Id,Label\r\n");
		for(int i = 0; i < mapIdSpot.size(); i++){
			fw.write((i+1) + "," + mapIdSpot.get(i+1) + "\r\n");
		}
		fw.close();
	}
	
	private static void writeCSV_Line_OnlyDependableStream(Map<String, Integer> mapTravelStream, Map<String, Integer> mapSpotId, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("Source,Target,Type,id,label,timeset,weight\r\n");
		int lineId = 0;
		for(Entry<String, Integer> entry : mapTravelStream.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			fw.write(mapSpotId.get(line[0]) + "," + mapSpotId.get(line[1]) + "," + "Directed" + "," + lineId++ + "," + "," + "," + weight + "\r\n");
		}
		fw.close();
	}
	private static void writeCSV_Line_SameDayStream(Map<String, Integer> mapSameDayStream, Map<String, Integer> mapSpotId, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("Source,Target,Type,id,label,timeset,weight\r\n");
		int lineId = 0;
		for(Entry<String, Integer> entry : mapSameDayStream.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			fw.write(mapSpotId.get(line[0]) + "," + mapSpotId.get(line[1]) + "," + "unDirected" + "," + lineId++ + "," + "," + "," + weight + "\r\n");
		}
		fw.close();
	}
	private static void writeCSV_Line_IntegrityStream(Map<String, Integer> mapTravelStream, Map<String, Integer> mapSameDayStream, Map<String, Integer> mapSpotId, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("Source,Target,Type,id,label,timeset,weight\r\n");
		int lineId = 0;
		for(Entry<String, Integer> entry : mapTravelStream.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			fw.write(mapSpotId.get(line[0]) + "," + mapSpotId.get(line[1]) + "," + "Directed" + "," + lineId++ + "," + "," + "," + weight + "\r\n");
		}
		for(Entry<String, Integer> entry : mapSameDayStream.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			fw.write(mapSpotId.get(line[0]) + "," + mapSpotId.get(line[1]) + "," + "unDirected" + "," + lineId++ + "," + "," + "," + weight + "\r\n");
		}
		fw.close();
	}
	
	private static void writeNet_OnlyDependableStream(Map<Integer, String> mapIdSpot, Map<String, Integer> mapSpotId, Map<String, Integer> mapTravelStream, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("*Vertices " + mapIdSpot.size());
		for(int i = 0; i < mapIdSpot.size(); i++){
			fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
			fw.write((i+1) + " \"" + mapIdSpot.get(i+1) + "\"");
		}
		fw.write("\r\n");
		fw.write("*Arcs");
		for(Entry<String, Integer> entry : mapTravelStream.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
			fw.write(mapSpotId.get(line[0]) + " " + mapSpotId.get(line[1]) + " " + weight);
		}
		fw.close();
	}
	private static void writeNet_SameDayStream(Map<Integer, String> mapIdSpot, Map<String, Integer> mapSpotId, Map<String, Integer> mapSameDayStream, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("*Vertices " + mapIdSpot.size());
		for(int i = 0; i < mapIdSpot.size(); i++){
			fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
			fw.write((i+1) + " \"" + mapIdSpot.get(i+1) + "\"");
		}
		fw.write("\r\n");
		fw.write("*Edges");
		for(Entry<String, Integer> entry : mapSameDayStream.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
			fw.write(mapSpotId.get(line[0]) + " " + mapSpotId.get(line[1]) + " " + weight);
		}
		fw.close();
	}
	private static void writeNet_IntegrityStream(Map<Integer, String> mapIdSpot, Map<String, Integer> mapSpotId, Map<String, Integer> mapTravelStream, Map<String, Integer> mapSameDayStream, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("*Vertices " + mapIdSpot.size());
		for(int i = 0; i < mapIdSpot.size(); i++){
			fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
			fw.write((i+1) + " \"" + mapIdSpot.get(i+1) + "\"");
		}
		fw.write("\r\n");
		fw.write("*Arcs");
		for(Entry<String, Integer> entry : mapTravelStream.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
			fw.write(mapSpotId.get(line[0]) + " " + mapSpotId.get(line[1]) + " " + weight);
		}
		fw.write("\r\n");
		fw.write("*Edges");
		for(Entry<String, Integer> entry : mapSameDayStream.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
			fw.write(mapSpotId.get(line[0]) + " " + mapSpotId.get(line[1]) + " " + weight);
		}
		fw.close();
	}
	
	//计算点度
	public static void getDegree(String timeType) throws FileNotFoundException{
		//读入数据
		String path = "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "_ig_Line.csv";
		List<String> listLine = FileFunction.readFile(path);
		//读入景点以及其对应的id
		Map<Integer, String> mapIdSpot = new HashMap<>();
		Map<String, Integer> mapSpotId = new HashMap<>();
		List<String> listSpots = FileFunction.readFile("E:\\work\\迪士尼旅游流\\数据分析\\spotList.txt");
		for(int i = 0; i < listSpots.size(); i++){
			mapIdSpot.put(i+1, listSpots.get(i));
			mapSpotId.put(listSpots.get(i), i+1);
		}
		//计算入度和出度
		Map<String, Integer> mapInDegress = new HashMap<>();
		Map<String, Integer> mapOutDegree = new HashMap<>();
		for(int i = 1; i < listLine.size(); i++){
			String[] datas = listLine.get(i).split(",");
			String in = datas[0];
			String out = datas[1];
			int degree = datas[2].equals("Directed") ? 2 : 1;
			degree = degree * Integer.parseInt(datas[6]);
			mapInDegress.put(in, mapInDegress.get(in) == null ? degree : mapInDegress.get(in) + degree);
			mapOutDegree.put(out, mapOutDegree.get(out) == null ? degree : mapOutDegree.get(out) + degree);
		}
		//计算扩散系数
		Map<String, Double> mapDiffusion = new HashMap<>();
		for(Entry<String, Integer> entry : mapInDegress.entrySet()){
			if(entry.getKey().equals("null")) continue;
			String id = entry.getKey();
			if(mapInDegress.get(id) == null || mapOutDegree.get(id) == null)
				continue;
			int in = mapInDegress.get(id);
			int out = mapOutDegree.get(id);
			double diffusion = (double)(out - in)/(double)(out + in);
			mapDiffusion.put(id, diffusion);
		}
		//排序并输出
		TreeMap<String, Integer> sort_indegree = U.sortMap(mapInDegress);
		TreeMap<String, Integer> sort_outdegree = U.sortMap(mapOutDegree);
		//输出
		U.print("indegree");
		for(Entry<String, Integer> entry : sort_indegree.entrySet()){
			if(entry.getKey().equals("null")) continue;
			U.print(mapIdSpot.get(Integer.parseInt(entry.getKey())) + "\t" + (double)entry.getValue()/2);
		}
		U.print("outdegree");
		for(Entry<String, Integer> entry : sort_outdegree.entrySet()){
			if(entry.getKey().equals("null")) continue;
			U.print(mapIdSpot.get(Integer.parseInt(entry.getKey())) + "\t" + (double)entry.getValue()/2);
		}
		U.print("扩散系数");
		for(Entry<String, Double> entry : mapDiffusion.entrySet()){
			if(entry.getKey().equals("null")) continue;
			U.print(mapIdSpot.get(Integer.parseInt(entry.getKey())) + "\t" + entry.getValue());
		}
		U.print(mapIdSpot.toString());
	}
	
	//单日旅游研究
	public static void singDay(String timeType) throws FileNotFoundException{
		//读入数据
		String path = "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "listSameDayStream.txt";
		List<String> listStreams = FileFunction.readFile(path);
		//计算单日最高频的旅游流
		Map<String, Integer> mapStreams = new HashMap<>();
		for(String line : listStreams){
			String[] datas = line.split(",");
			if(datas.length > 2) continue;//不计算一天超过三次的旅游流
			for(int i = 0; i < datas.length; i++)
				for(int j = i+1; j < datas.length; j++){
					U.mapAddCount(mapStreams, U.getCompareString(datas[i], datas[j]));
				}
		}
		//排序
		TreeMap<String, Integer> sort = U.sortMap(mapStreams);
		U.print(sort.toString());
	}
	//所有旅游流研究，求最大的旅游流出现在哪两个景点之间
	public static void topStream(String timeType) throws FileNotFoundException{
		//读入景点以及其对应的id
		Map<String, String> mapIdSpot = new HashMap<>();
		List<String> listSpots = FileFunction.readFile("E:\\work\\迪士尼旅游流\\数据分析\\spotList.txt");
		for(int i = 0; i < listSpots.size(); i++){
			mapIdSpot.put(i+1 + "", listSpots.get(i));
		}
		//读入数据
		String path = "E:\\work\\迪士尼旅游流\\数据分析\\" + timeType + "_ig_Line.csv";
		List<String> listStreams = FileFunction.readFile(path);
		//计算旅游流的值
		Map<String, Integer> mapStreamCount = new HashMap<>();
		int all = 0;
		for(int i = 1; i < listStreams.size(); i++){
			String[] data = listStreams.get(i).split(",");
			all += Integer.parseInt(data[6]);
			for(int j = 0; j < Integer.parseInt(data[6]); j++){
				if(data[0].equals("null") || data[1].equals("null")) break;
				U.mapAddCount(mapStreamCount, U.getCompareString(mapIdSpot.get(data[0]), mapIdSpot.get(data[1])));
			}
		}
		//排序
		TreeMap<String, Integer> sort = U.sortMap(mapStreamCount);
		//打印
		U.print("总共：" + all);
		U.print(sort.toString());
	}
	
	
	
	
	//计算有多少个1（在核心边缘分析中，需要计算密度时使用）
	public static void calculateHowManOne() throws FileNotFoundException{
		//读入数据
		List<String> lines = FileFunction.readFile("E:\\work\\迪士尼旅游流\\数据结果\\temp.txt");
		//数数
		int count = 0;
		for(String line : lines){
			U.print(line.length() - line.replaceAll("1", "").length());
			count += line.length() - line.replaceAll("1", "").length();
		}
		U.print("最终结果：" + count);
	}
	
	
	
	
	
	//根据id列表，打印相应的景点名
	public static void printSpotName(String intput) throws FileNotFoundException{
		//读入景点以及其对应的id
		Map<Integer, String> mapIdSpot = new HashMap<>();
		Map<String, Integer> mapSpotId = new HashMap<>();
		List<String> listSpots = FileFunction.readFile("E:\\work\\迪士尼旅游流\\数据分析\\spotList.txt");
		for(int i = 0; i < listSpots.size(); i++){
			mapIdSpot.put(i+1, listSpots.get(i));
			mapSpotId.put(listSpots.get(i), i+1);
		}
		String output = "";
		//打印出相应的景点名
		for(String in : intput.split(",")){
			output += mapIdSpot.get(Integer.parseInt(in)) + ",";
		}
		U.print(output.substring(0, output.length()-1));
	}
	
}
