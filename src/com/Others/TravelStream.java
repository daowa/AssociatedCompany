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

	//����ɼ�¿������Ҫ�õ���ҳ��ַ
	public static void outputLvMaMa() throws IOException{
		//������ֵ
		int EXCELINDEX_SPOT = 0;
		int EXCELINDEX_ID = 1;
		int EXCELINDEX_PAGENUMBER = 2;
		//��������
		Map<String, Integer> mapSpotID = new HashMap();
		Map<String, Integer> mapSpotPagenumber = new HashMap<>();
		//��excel�е���Ϣ����
		XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\��ʿ��������\\���ݲɼ��ĵ�\\¿����.xlsx", 0);
		for(int i = 35; i < 42; i++){
			String spot = U.getCellStringValue(sheet.getRow(i).getCell(EXCELINDEX_SPOT));
			String sid = U.getCellStringValue(sheet.getRow(i).getCell(EXCELINDEX_ID));
			int id = Integer.parseInt(sid.substring(0, sid.length()-2));
			String spagenumber = U.getCellStringValue(sheet.getRow(i).getCell(EXCELINDEX_PAGENUMBER));
			int pagenumber = Integer.parseInt(spagenumber.substring(0, spagenumber.length()-2));
			mapSpotID.put(spot, id);
			mapSpotPagenumber.put(spot, pagenumber);
		}
		//д��txt
		List<String> listHTML = new ArrayList<>();
		for(Entry<String, Integer> entry : mapSpotID.entrySet()){
			for(int i = 1; i <= mapSpotPagenumber.get(entry.getKey()); i++){
				listHTML.add("http://ticket.lvmama.com/vst_front/comment/newPaginationOfComments?" +
						"type=all&currentPage=" + i +
						"&totalCount=0&placeId=" + mapSpotID.get(entry.getKey())+
						"&productId=&placeIdType=PLACE&isPicture=&isBest=&isPOI=Y&isELong=N");
			}
		}
		FileFunction.writeList(listHTML, "E:\\work\\��ʿ��������\\���ݲɼ��ĵ�\\¿����.txt");
	}
	
	//�����ж����û��������������
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
		
		//����
		TreeMap<String, Integer> sorted_mapName = U.sortMap(mapNameCount);
		
		//���
		FileFunction.writeMap_KV(sorted_mapName, "E:\\work\\��ʿ��������\\���ݷ���\\map_userName.txt");
	}
	
	//��������ʽ���ļ�
	//������ʾ���Ϻ���ʿ�Ὺҵǰ���ǿ�ҵ�󣬿�ҵǰ��before����ҵ����after��������all����ҵǰͬ����beforeSame
	public static void outputNet(String timeType) throws SQLException, IOException{
		//���뾰���Լ����Ӧ��id
		Map<Integer, String> mapIdSpot = new HashMap<>();
		Map<String, Integer> mapSpotId = new HashMap<>();
		List<String> listSpots = FileFunction.readFile("E:\\work\\��ʿ��������\\���ݷ���\\spotList.txt");
		for(int i = 0; i < listSpots.size(); i++){
			mapIdSpot.put(i+1, listSpots.get(i));
			mapSpotId.put(listSpots.get(i), i+1);
		}
		
		//�����û���
		Map<String, Integer> mapUsersCount = FileFunction.readMap_SI("E:\\work\\��ʿ��������\\���ݷ���\\map_userName.txt");
		//����ͣ�õ��û���
		List<String> listStopUsers = FileFunction.readFile("E:\\work\\��ʿ��������\\���ݷ���\\stopUsers.txt");
		
		//��ȡ���ݿ⣬��ȡ����ͼ����Ϣ
		Map<String, Integer> mapTravelStream = new HashMap<>();
		Map<String, Integer> mapSameDayStream = new HashMap<>();
		List<String> listTravelStream = new ArrayList<>();//���������ߣ��硰1-2������ʽ��������
		List<String> listSameDayStream = new ArrayList<>();//ͬһ������������硰1-2-3������ʽ��������
		for(Entry<String, Integer> entry : mapUsersCount.entrySet()){
			//��������
			if(listStopUsers.contains(entry.getKey())) continue;//����ͣ�õ��û���
			if(entry.getValue() < 2) continue;//�����޷��γ����������
			//�����ݿ��ȡ���û�����
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
			String sameDayStream = "";//��¼samedayStream
			while(rs.next()){
				String nowSpot = getNormalizeSpot(listSpots, rs.getString("spot_name"));//��ȡ��׼���ľ�����
				int nowTime = Integer.parseInt(rs.getString("t"));
				if(lastSpot.equals("") && lastTime == -1){//��һ�Σ�ֻ����lastSpot��lastTime������
					lastSpot = nowSpot;
					lastTime = nowTime;
					continue;
				}
				if(lastSpot.equals(nowSpot)){//�������������ͬ��������
					lastSpot = nowSpot;
					lastTime = nowTime;
					continue;
				}
				if(nowTime == lastTime){//���������ͬһ�죬��¼ΪSameDayStream��������
					if(sameDayStream == "")//��һ�Σ���Ҫ����ԭ�еľ���
						sameDayStream += lastSpot + ",";
					sameDayStream += nowSpot + ",";
					lastSpot = nowSpot;
					lastTime = nowTime;
					continue;//���������ͬһ�죬����
				}
				if(nowTime - lastTime > 604800) continue;//������ε����������һ�ܣ���Ϊ���������Σ����γ�������
				//��¼��������Ϣ
				String line = lastSpot + "," + nowSpot;
				listTravelStream.add(line);
				U.mapAddCount(mapTravelStream, line);
				//����lastSpot��lastTime
				lastSpot = nowSpot;
				lastTime = nowTime;
			}
			//��¼SameDayStream��Ϣ
			if(sameDayStream.contains(","))//ȥ�����һ��","
				sameDayStream = sameDayStream.substring(0, sameDayStream.length()-1);
			if(sameDayStream.split(",").length > 1){//����һ�죬˵��������������������ͬһ����
				U.print(sameDayStream);
				listSameDayStream.add(sameDayStream);
				sameDayStream = "";
			}
		}
		FileFunction.writeList(listTravelStream, "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "listTravelStream.txt");
		FileFunction.writeList(listSameDayStream, "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "listSameDayStream.txt");
		//��һ��������Ҳд��map
		int countSameDay = 0;
		for(String sameDayStream : listSameDayStream){
			String[] spots = sameDayStream.split(",");
			if(spots.length > 2) continue;//������һ����������������������
			countSameDay++;
			for(int i = 0; i < spots.length; i++){
				for(int j = i+1; j < spots.length; j++){
					String line = U.getCompareString(spots[i], spots[j]);
					U.mapAddCount(mapSameDayStream, line);
				}
			}
		}
		U.print("ÿ��������:" + countSameDay);
		
		//�������ͼ
		String pathNode = "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "Node.csv";
		writeCSV_Node(mapIdSpot, pathNode);
		
		//���������������͵���������������
		String pathLine_integrityStream = "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "_ig_Line.csv";
		String pathNet_integrityStream = "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "_ig_Net.net";
		writeCSV_Line_IntegrityStream(mapTravelStream, mapSameDayStream, mapSpotId, pathLine_integrityStream);
		writeNet_IntegrityStream(mapIdSpot, mapSpotId, mapTravelStream, mapSameDayStream, pathNet_integrityStream);
		
		//������������������
		String pathLine_onlyDependableStream = "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "_od_Line.csv";
		String pathNet_onlyDependableStream = "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "_od_Net.net";
		writeCSV_Line_OnlyDependableStream(mapTravelStream, mapSpotId, pathLine_onlyDependableStream);
		writeNet_OnlyDependableStream(mapIdSpot, mapSpotId, mapTravelStream, pathNet_onlyDependableStream);
		
		//������������������
		String pathLine_sameDayStream = "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "_sd_Line.csv";
		String pathNet_sameDayStream = "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "_sd_Net.net";
		writeCSV_Line_SameDayStream(mapSameDayStream, mapSpotId, pathLine_sameDayStream);
		writeNet_SameDayStream(mapIdSpot, mapSpotId, mapSameDayStream, pathNet_sameDayStream);
	}
	
	//���ÿ���ο͵�������
	public static void outputUserStream(String timeType) throws SQLException, IOException{
		//���뾰���Լ����Ӧ��id
		Map<Integer, String> mapIdSpot = new HashMap<>();
		Map<String, Integer> mapSpotId = new HashMap<>();
		List<String> listSpots = FileFunction.readFile("E:\\work\\��ʿ��������\\���ݷ���\\spotList.txt");
		for(int i = 0; i < listSpots.size(); i++){
			mapIdSpot.put(i+1, listSpots.get(i));
			mapSpotId.put(listSpots.get(i), i+1);
		}
		
		//�����û���
		Map<String, Integer> mapUsersCount = FileFunction.readMap_SI("E:\\work\\��ʿ��������\\���ݷ���\\map_userName.txt");
		//����ͣ�õ��û���
		List<String> listStopUsers = FileFunction.readFile("E:\\work\\��ʿ��������\\���ݷ���\\stopUsers.txt");
		
		//��ȡ���ݿ⣬��ȡ�û�������
		Map<String, String> mapUserStream = new HashMap<>();
		Map<String, Integer> mapUserStreamCount = new HashMap<>();
		for(Entry<String, Integer> entry : mapUsersCount.entrySet()){
			//��������
			if(listStopUsers.contains(entry.getKey())) continue;//����ͣ�õ��û���
			if(entry.getValue() < 2) continue;//�����޷��γ����������
			//�����ݿ��ȡ���û�����
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
				String nowSpot = getNormalizeSpot(listSpots, rs.getString("spot_name"));//��ȡ��׼���ľ�����
				int nowTime = Integer.parseInt(rs.getString("t"));
				if(lastSpot.equals("") && lastTime == -1){//��һ�Σ�ֻ����lastSpot��lastTime������
					lastSpot = nowSpot;
					lastTime = nowTime;
					continue;
				}
				if(lastSpot.equals(nowSpot)){//�������������ͬ��������
					lastSpot = nowSpot;
					lastTime = nowTime;
					continue;
				}
				if(nowTime - lastTime > 604800) continue;//������ε����������һ�ܣ���Ϊ���������Σ����γ�������
				//��¼��������Ϣ
				if(stream.equals(""))
					stream = lastSpot + "," + nowSpot;
				else
					stream += "," + nowSpot;
				//����lastSpot��lastTime
				lastSpot = nowSpot;
				lastTime = nowTime;
			}
			if(stream.split(",").length > 1){
				mapUserStream.put(entry.getKey(), stream);
				mapUserStreamCount.put(entry.getKey(), stream.split(",").length);
			}
		}
		
		//���
		String path_userStream = "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "_userStream.txt";
		String path_userStreamCount = "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "_userStreamCount.txt";
		FileFunction.writeMap_KV(mapUserStream, path_userStream);
		FileFunction.writeMap_KV(mapUserStreamCount, path_userStreamCount);
	}
	
	//�����Ϻ���ʿ���ڵڼ�������
	public static void getDisneyRank() throws NumberFormatException, IOException{
		//��ʼ��
		int[] disneyRank = new int[20];
		//��ȡ�ο�����������
		Map<String, String> mapUserStream = FileFunction.readMap_SS("E:\\work\\��ʿ��������\\���ݷ���\\after_userStream.txt");
		//������ͳ���Ϻ���ʿ���ڵڼ�λ
		for(Entry<String, String> entry : mapUserStream.entrySet()){
			String[] stream = entry.getValue().split(",");
			for(int i = 0; i < stream.length; i++){
				if(stream[i].equals("�Ϻ�Ұ������԰"))
					disneyRank[i]++;
			}
		}
		//��ӡ�����
		for(int i = 0; i < disneyRank.length; i++)
			U.print(i+1 + ":" + disneyRank[i]);
	}
	
	//�������ݿ���spot_name�ֶΣ���ȡ��׼���ľ�����
	private static String getNormalizeSpot(List<String> listSpots, String spot){
		if(spot.contains("÷����")){
			spot = "�Ϻ��������ɭ�ֹ�԰";
		}
		else if(spot.contains("�º�")){
			spot = "�Ϻ��º�������԰";
		}
		else if(spot.contains("���ƹʾ���������ʷ�����")){
			spot = "���Ƽ����";
		}
		else if(spot.contains("��ɳʪ��")){
			spot = "�����";
		}
		else if(spot.contains("��ҽ�") || spot.contains("�ù���8����Ʊ����Ʊ������������")){
			spot = "��ҽǹ���";
		}
		else if(spot.contains("�Ϻ�����ǰ����")){
			spot = "ǰ����̬��";
		}
		else if(spot.contains("2015�´�������Ʊ�����ڰ���ػݡ�")){
			spot = "ǰ����̬��";
		}
		else if(spot.contains("����Ʊ+��Ļ��ӰƱ") || spot.contains("��Ļ��ӰƱ")
				|| spot.contains("����Ʊ+�����ӰƱ") || spot.contains("����Ʊ+�����ӰƱ+�����ӰƱ")){
			spot = "��������";
		}
		else if(spot.contains("��������̨��ʪ��ɭ�ֹ�԰")){
			spot = "�Ϻ���̨��";
		}
		else if(spot.contains("�����ˡ���Ů��") || spot.contains("̽�ض�����Ʊ�����ϻ���ʳ")
				|| spot.contains("����Ʊ������������") || spot.contains("����Ʊ���Ź���")){
			spot = "�Ϻ�Ұ������԰";
		}
		else if(spot.contains("������ƻ����԰") || spot.contains("��ͯ��԰100Ԫ�ֽ�ȯ")){
			spot = "����";
		}
		else if(spot.contains("�Ϻ�̫�����������ζȼ���")){
			spot = "�Ϻ�̫�������ʾ��ֲ�";
		}
		else if(spot.contains("�Ϻ����ź�̲ˮ��԰") || spot.contains("�������ѧ��Ʊ��ƾ������Ч����ȫ���ƴ�ѧ��֤�����֤��֤��԰��")
				|| spot.contains("�ճ�����Ʊ��ˢ������Ч���֤��԰��") || spot.contains("�ճ�����Ʊ��2.14-15���˽ڣ�18����������Ůͬ��һ���ⵥ��")
				|| spot.contains("ҹ������Ʊ��ˢ������Ч���֤��԰��")){
			spot = "�Ϻ����ֹ�";
		}
		else if(spot.contains("�Ϻ����԰") || spot.contains("�������԰")){
			spot = "������԰";
		}
		
		for(String key : listSpots){
			if(spot.contains(key)){//�ҵ������ˣ����в���
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
			fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
			fw.write((i+1) + " \"" + mapIdSpot.get(i+1) + "\"");
		}
		fw.write("\r\n");
		fw.write("*Arcs");
		for(Entry<String, Integer> entry : mapTravelStream.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
			fw.write(mapSpotId.get(line[0]) + " " + mapSpotId.get(line[1]) + " " + weight);
		}
		fw.close();
	}
	private static void writeNet_SameDayStream(Map<Integer, String> mapIdSpot, Map<String, Integer> mapSpotId, Map<String, Integer> mapSameDayStream, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("*Vertices " + mapIdSpot.size());
		for(int i = 0; i < mapIdSpot.size(); i++){
			fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
			fw.write((i+1) + " \"" + mapIdSpot.get(i+1) + "\"");
		}
		fw.write("\r\n");
		fw.write("*Edges");
		for(Entry<String, Integer> entry : mapSameDayStream.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
			fw.write(mapSpotId.get(line[0]) + " " + mapSpotId.get(line[1]) + " " + weight);
		}
		fw.close();
	}
	private static void writeNet_IntegrityStream(Map<Integer, String> mapIdSpot, Map<String, Integer> mapSpotId, Map<String, Integer> mapTravelStream, Map<String, Integer> mapSameDayStream, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("*Vertices " + mapIdSpot.size());
		for(int i = 0; i < mapIdSpot.size(); i++){
			fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
			fw.write((i+1) + " \"" + mapIdSpot.get(i+1) + "\"");
		}
		fw.write("\r\n");
		fw.write("*Arcs");
		for(Entry<String, Integer> entry : mapTravelStream.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
			fw.write(mapSpotId.get(line[0]) + " " + mapSpotId.get(line[1]) + " " + weight);
		}
		fw.write("\r\n");
		fw.write("*Edges");
		for(Entry<String, Integer> entry : mapSameDayStream.entrySet()){
			String[] line = entry.getKey().split(",");
			int weight = entry.getValue();
			fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
			fw.write(mapSpotId.get(line[0]) + " " + mapSpotId.get(line[1]) + " " + weight);
		}
		fw.close();
	}
	
	//������
	public static void getDegree(String timeType) throws FileNotFoundException{
		//��������
		String path = "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "_ig_Line.csv";
		List<String> listLine = FileFunction.readFile(path);
		//���뾰���Լ����Ӧ��id
		Map<Integer, String> mapIdSpot = new HashMap<>();
		Map<String, Integer> mapSpotId = new HashMap<>();
		List<String> listSpots = FileFunction.readFile("E:\\work\\��ʿ��������\\���ݷ���\\spotList.txt");
		for(int i = 0; i < listSpots.size(); i++){
			mapIdSpot.put(i+1, listSpots.get(i));
			mapSpotId.put(listSpots.get(i), i+1);
		}
		//������Ⱥͳ���
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
		//������ɢϵ��
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
		//�������
		TreeMap<String, Integer> sort_indegree = U.sortMap(mapInDegress);
		TreeMap<String, Integer> sort_outdegree = U.sortMap(mapOutDegree);
		//���
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
		U.print("��ɢϵ��");
		for(Entry<String, Double> entry : mapDiffusion.entrySet()){
			if(entry.getKey().equals("null")) continue;
			U.print(mapIdSpot.get(Integer.parseInt(entry.getKey())) + "\t" + entry.getValue());
		}
		U.print(mapIdSpot.toString());
	}
	
	//���������о�
	public static void singDay(String timeType) throws FileNotFoundException{
		//��������
		String path = "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "listSameDayStream.txt";
		List<String> listStreams = FileFunction.readFile(path);
		//���㵥�����Ƶ��������
		Map<String, Integer> mapStreams = new HashMap<>();
		for(String line : listStreams){
			String[] datas = line.split(",");
			if(datas.length > 2) continue;//������һ�쳬�����ε�������
			for(int i = 0; i < datas.length; i++)
				for(int j = i+1; j < datas.length; j++){
					U.mapAddCount(mapStreams, U.getCompareString(datas[i], datas[j]));
				}
		}
		//����
		TreeMap<String, Integer> sort = U.sortMap(mapStreams);
		U.print(sort.toString());
	}
	//�����������о�������������������������������֮��
	public static void topStream(String timeType) throws FileNotFoundException{
		//���뾰���Լ����Ӧ��id
		Map<String, String> mapIdSpot = new HashMap<>();
		List<String> listSpots = FileFunction.readFile("E:\\work\\��ʿ��������\\���ݷ���\\spotList.txt");
		for(int i = 0; i < listSpots.size(); i++){
			mapIdSpot.put(i+1 + "", listSpots.get(i));
		}
		//��������
		String path = "E:\\work\\��ʿ��������\\���ݷ���\\" + timeType + "_ig_Line.csv";
		List<String> listStreams = FileFunction.readFile(path);
		//������������ֵ
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
		//����
		TreeMap<String, Integer> sort = U.sortMap(mapStreamCount);
		//��ӡ
		U.print("�ܹ���" + all);
		U.print(sort.toString());
	}
	
	
	
	
	//�����ж��ٸ�1���ں��ı�Ե�����У���Ҫ�����ܶ�ʱʹ�ã�
	public static void calculateHowManOne() throws FileNotFoundException{
		//��������
		List<String> lines = FileFunction.readFile("E:\\work\\��ʿ��������\\���ݽ��\\temp.txt");
		//����
		int count = 0;
		for(String line : lines){
			U.print(line.length() - line.replaceAll("1", "").length());
			count += line.length() - line.replaceAll("1", "").length();
		}
		U.print("���ս����" + count);
	}
	
	
	
	
	
	//����id�б���ӡ��Ӧ�ľ�����
	public static void printSpotName(String intput) throws FileNotFoundException{
		//���뾰���Լ����Ӧ��id
		Map<Integer, String> mapIdSpot = new HashMap<>();
		Map<String, Integer> mapSpotId = new HashMap<>();
		List<String> listSpots = FileFunction.readFile("E:\\work\\��ʿ��������\\���ݷ���\\spotList.txt");
		for(int i = 0; i < listSpots.size(); i++){
			mapIdSpot.put(i+1, listSpots.get(i));
			mapSpotId.put(listSpots.get(i), i+1);
		}
		String output = "";
		//��ӡ����Ӧ�ľ�����
		for(String in : intput.split(",")){
			output += mapIdSpot.get(Integer.parseInt(in)) + ",";
		}
		U.print(output.substring(0, output.length()-1));
	}
	
}
