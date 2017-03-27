package com.Others;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.bag.HashBag;
import org.apache.poi.hmef.attribute.MAPIDateAttribute;

import com.db.DBFunction;
import com.myClass.U;

public class SMDANet {
	
	//Ŀ���̻��������
	public static void SMDANet_targetShop(String targetShopName, int thresholdNode, int thresholdLine) throws SQLException, IOException{
		String sql = "";
		ResultSet rs = null;
		
		//����targetShopName��ȡĿ���̼����е�id
		List<String> listTargetId = new ArrayList<>();
		sql = "select shop_id from tb_baiduwaimaishop where shop_name like '%" + targetShopName + "%'";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			listTargetId.add(rs.getString("shop_id"));
		}
		
		//��ȡĿ���̻���һ�������̻���id
		String targets = "";
		for(String id : listTargetId){
			targets += "'" + id + "'" + ",";
			sql = "select target from smda.net_line_new where source = '" + id + "'";
			rs = DBFunction.getRS(sql);
			while(rs.next()){
				if(!targets.contains(rs.getString("target")))
					targets += "'" + rs.getString("target") + "'" + ",";
			}
		}
		targets = targets.substring(0, targets.length()-1);
		U.print("�Ѳ�ѯ��Ŀ���̻�����һ�������̻�id����");
		U.print(targets);
		
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
		
		//shopid - locationName
		Map<String, String> mapShopidLocationname = new HashMap();
		sql = "select A.shopid, B.locationname "
				+ "from smda.tb_shoplocation as A left join smda.tb_locationname as B "
				+ "on A.locationid = B.locationid";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			mapShopidLocationname.put(rs.getString("shopid"), rs.getString("locationname"));
		}
		U.print("shopid-locationname��Ӧ��ɣ���" + mapShopidLocationname.size() + "�Ҳ�ͬid�̼�");
		
		//locationname&dTime - weather
		int INDEX_TPMAX = 0;
		int INDEX_TPMIN = 1;
		int INDEX_TPDF = 2;
		int INDEX_RAINSUM = 3;
		int INDEX_RAINBUSY = 4;
		int INDEX_WORKDAY = 5;
		Map<String, String[]> mapLocationWeather = new HashMap<>();
		sql = "SELECT station_name, date(day) as dTime, temperature_max, temperature_min, temperature_df, rainfall_sum, busyrainfall_sum, isworkday FROM smda.feature_day";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			String[] s = {rs.getString("temperature_max"), rs.getString("temperature_min"), rs.getString("temperature_df"), rs.getString("rainfall_sum"), rs.getString("busyrainfall_sum"), rs.getString("isworkday")};
			mapLocationWeather.put(rs.getString("station_name") + rs.getString("dTime"), s);
		}
		U.print("locaiontname&dTime-weather��Ӧ���");
		
		//��ȡÿ�ҵ��̵��û��б�
		//�ɸĽ�����sql��ѯΪ��һ��
		Map<String, HashSet<String>> mapNameUsers = new HashMap<>();
		Map<String, Integer> mapNameUsercounts = new HashMap<>();
		sql = "SELECT UNIX_TIMESTAMP(arrive_time) as uTime, date(arrive_time) as dTime, waimai_release_id, pass_uid " +
				"from tb_baiduwaimai where waimai_release_id in (" + targets + ")";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			//����ʱ��ɸѡ
			if(rs.getInt("uTime") < 1420041600 || rs.getInt("uTime") > 1462032000) continue;
			
			//��������ɸѡ
			String shopid = rs.getString("waimai_release_id");
			String locationName = mapShopidLocationname.get(shopid);
			String shopName = mapShopidShopname.get(shopid);
			String[] s = mapLocationWeather.get((locationName + rs.getString("dTime")));
			if(s == null) continue;//ȱ�����̵����ڵ�����������Ϣ
			//��ͬ�������ж�
//			if(Double.parseDouble(s[INDEX_TPMAX]) < 27.82) continue;//���������������ʾ��������
//			if(Double.parseDouble(s[INDEX_TPMIN]) > 3.5) continue;//���������������ʾ��������
//			if(Double.parseDouble(s[INDEX_RAINSUM]) < 9.5) continue;//���������������ʾ�ۼƽ�������
//			if(Double.parseDouble(s[INDEX_RAINBUSY]) == 0) continue;//���������������ʾ��ʱ��������
//			if(!s[INDEX_WORKDAY].equals("������")) continue;//���������������ʾ������
//			if(!s[INDEX_WORKDAY].equals("��ĩ")) continue;//���������������ʾ��ĩ
//			if(s[INDEX_WORKDAY].equals("������") || s[INDEX_WORKDAY].equals("��ĩ")) continue;//���������������ʾ����
			
			String passID = rs.getString("pass_uid");
			HashSet<String> users = new HashSet<>();
			if(mapNameUsers.get(shopName) == null) //���ޣ����½�
				users = new HashSet<>();
			else //���У������
				users = mapNameUsers.get(shopName);
			users.add(passID);
			mapNameUsers.put(shopName, users);
		}
		
		
		for(String name : mapNameUsers.keySet()){
			mapNameUsercounts.put(name, mapNameUsers.get(name).size());
		}
		
		//��ʼ�����磬��������ϵд�����
		int[][] matrix = new int[6000][6000];
		//д������±�
		Map<Integer, String> mapIndexName = new HashMap<>();
		Map<String, Integer> mapNameIndex = new HashMap<>();
		for(int i = 0; i < listShopName.size(); i++){
			mapIndexName.put(i, listShopName.get(i));
			mapNameIndex.put(listShopName.get(i), i);
		}
		
		//д�����
		for(int i = 0; i < listShopName.size(); i++){
			for(int j = i+1; j < listShopName.size(); j++){
//				U.print("i:" + i + ",j:" + j);
				HashSet<String> hsI = mapNameUsers.get(mapIndexName.get(i));
				HashSet<String> hsJ = mapNameUsers.get(mapIndexName.get(j));
				if(hsI == null || hsJ == null || hsI.size() < thresholdNode || hsJ.size() < thresholdNode){ //����û�еĵ㣬˵���������ֵ�ˣ�ֱ����Ϊ0
					continue;
				}
				else{
					HashBag bag=new HashBag();//HashBag��һ��org.apache.commons.collections.bag���е��࣬���Ժܼ򵥵�������������еĽ���
					bag.addAll(hsI);
					bag.retainAll(hsJ);
					int lineSize = bag.size();
//					U.print("hsI:" + hsI.size() + ",hsJ:" + hsJ.size() + ",lineSize:" + lineSize);
					if(lineSize >= thresholdLine) matrix[i][j] = lineSize;
				}
			}
		}
		//������ֵɸѡidlist
		//�ų���������û����ϵ�ĵ�
		List<Integer> idList = getIdList(matrix, listShopName.size(), 0);//0��ʾ�Ǹ��㶼���ԣ�1��ʾȥ��������
		//������ֵɸѡ��ģ̫С�ĵ�
		for(int i = 0; i < idList.size(); i++){
			if(mapNameUsercounts.get(mapIndexName.get(idList.get(i))) == null 
				|| mapNameUsercounts.get(mapIndexName.get(idList.get(i))) < thresholdNode){
				idList.remove(i);
				i--;
			}
		}
		U.print("idList:" + idList.size());
		
		//д���ļ�
		String pathNode = "E:\\work\\smda����+���ݾ���\\" + targetShopName + "node.csv";
		String pathLine = "E:\\work\\smda����+���ݾ���\\" + targetShopName + "line.csv";
		writeCSV_Node(idList, mapIndexName, mapNameUsercounts, pathNode);
		writeCSV_Line(idList, matrix, thresholdLine, pathLine);
		U.print("done");
	}
	
	//�����������
	public static void SMDANet(int thresholdNode, int thresholdLine) throws SQLException, IOException{
		String sql = "";
		ResultSet rs = null;
		
		//��ȡ���е���id
		List<String> listShopID = new ArrayList<>();
		sql = "SELECT shopid FROM tb_shoplocation where locationid = \"ad0644a32f89c3b4\"";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			listShopID.add(rs.getString("shopid"));
		}
		U.print("�ѻ�ȡ���е���id");

		
		//��ȡ���е���id(���ݿ���id)�����ֵĶ�Ӧ��ϵ
		Map<String, String> mapShopidShopname = new HashMap<>();
		List<String> listShopName = new ArrayList<>();
		String tempIDs = "";
		for(String shopid : listShopID){
			tempIDs += "\"" + shopid + "\",";
		}
		sql = "select shop_id, shop_name from tb_baiduwaimaishop where shop_id in (" + tempIDs.substring(0, tempIDs.length()-1) + ")";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			mapShopidShopname.put(rs.getString("shop_id"), rs.getString("shop_name"));
			listShopName.add(rs.getString("shop_name"));
		}
		U.print("�ѽ�����id�������һһ��Ӧ");
		
		//��ȡÿ�ҵ��̵��û��б�
		//�ɸĽ�����sql��ѯΪ��һ��
		int dida = 0;
		Map<String, HashSet<String>> mapNameUsers = new HashMap<>();
		Map<String, Integer> mapNameUsercounts = new HashMap<>();
		for(String shopID : listShopID){
			if(mapShopidShopname.get(shopID) == null) continue;//�ų�һЩ��������Ϣ�ĵ���
			HashSet<String> users = new HashSet<>();
			sql = "SELECT pass_uid FROM tb_baiduwaimai_v1 where waimai_release_id = \"" + shopID + "\"";
			rs = DBFunction.getRS(sql);
			while(rs.next()){
				users.add(rs.getString("pass_uid"));
			}
			mapNameUsers.put(mapShopidShopname.get(shopID), users);
			mapNameUsercounts.put(mapShopidShopname.get(shopID), users.size());
			U.print("dida: " + dida++);
		}
		U.print("�ѻ�ȡÿ�ҵ��̵��û��б�");
		
		//��ʼ�����磬��������ϵд�����
		int[][] matrix = new int[5000][5000];
		//д������±�
		Map<Integer, String> mapIndexName = new HashMap<>();
		Map<String, Integer> mapNameIndex = new HashMap<>();
		for(int i = 0; i < listShopName.size(); i++){
			mapIndexName.put(i, listShopName.get(i));
			mapNameIndex.put(listShopName.get(i), i);
		}
		//д�����
		for(int i = 0; i < listShopName.size(); i++){
			for(int j = i+1; j < listShopName.size(); j++){
				HashBag bag=new HashBag();//HashBag��һ��org.apache.commons.collections.bag���е��࣬���Ժܼ򵥵�������������еĽ���
				bag.addAll(mapNameUsers.get(mapIndexName.get(i)));
				bag.retainAll(mapNameUsers.get(mapIndexName.get(j)));
				U.print(bag.size());
				matrix[i][j] = bag.size();
			}
		}
		//������ֵɸѡidlist
		//�ų���������û����ϵ�ĵ�
		List<Integer> idList = getIdList(matrix, listShopName.size(), 1);
		//������ֵɸѡ��ģ̫С�ĵ�
		for(int i = 0; i < idList.size(); i++){
			if(mapNameUsercounts.get(mapIndexName.get(idList.get(i))) < thresholdNode){
				idList.remove(i);
				i--;
			}
		}
		//д���ļ�
		String pathNode = "E:\\work\\smda����+���ݾ���\\node.csv";
		String pathLine = "E:\\work\\smda����+���ݾ���\\line.csv";
		writeCSV_Node(idList, mapIndexName, mapNameUsercounts, pathNode);
		writeCSV_Line(idList, matrix, thresholdLine, pathLine);
		U.print("done");
	}
	
	
	
	
	
	//ȫ�о������
	public static void SMDANet2(int thresholdNode, int thresholdLine) throws SQLException, IOException{
		String sql = "";
		ResultSet rs = null;
		
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
		
		//shopid - locationName
		Map<String, String> mapShopidLocationname = new HashMap();
		sql = "select A.shopid, B.locationname "
				+ "from smda.tb_shoplocation as A left join smda.tb_locationname as B "
				+ "on A.locationid = B.locationid";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			mapShopidLocationname.put(rs.getString("shopid"), rs.getString("locationname"));
		}
		U.print("shopid-locationname��Ӧ��ɣ���" + mapShopidLocationname.size() + "�Ҳ�ͬid�̼�");
		
		//locationname&dTime - weather
		int INDEX_TPMAX = 0;
		int INDEX_TPMIN = 1;
		int INDEX_TPDF = 2;
		int INDEX_RAINSUM = 3;
		int INDEX_RAINBUSY = 4;
		int INDEX_WORKDAY = 5;
		Map<String, String[]> mapLocationWeather = new HashMap<>();
		sql = "SELECT station_name, date(day) as dTime, temperature_max, temperature_min, temperature_df, rainfall_sum, busyrainfall_sum, isworkday FROM smda.feature_day";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			String[] s = {rs.getString("temperature_max"), rs.getString("temperature_min"), rs.getString("temperature_df"), rs.getString("rainfall_sum"), rs.getString("busyrainfall_sum"), rs.getString("isworkday")};
			mapLocationWeather.put(rs.getString("station_name") + rs.getString("dTime"), s);
		}
		U.print("locaiontname&dTime-weather��Ӧ���");
		
		//��ȡÿ�ҵ��̵��û��б�
		//�ɸĽ�����sql��ѯΪ��һ��
		Map<String, HashSet<String>> mapNameUsers = new HashMap<>();
		Map<String, Integer> mapNameUsercounts = new HashMap<>();
//		sql = "select UNIX_TIMESTAMP(A.arrive_time) as uTime, waimai_release_id, pass_uid, D.temperature_max, D.temperature_min, D.temperature_df, D.rainfall_sum, D.busyrainfall_sum, D.isworkday "
//				+ "FROM smda.tb_baiduwaimai as A "
//				+ "left join smda.tb_shoplocation as B on A.waimai_release_id = B.shopid "
//				+ "left join smda.tb_locationname as C on B.locationid = C.locationid "
//				+ "left join smda.feature_day as D on date(A.arrive_time) = date(D.day) and C.locationname = D.station_name "
//				+ "where UNIX_TIMESTAMP(A.arrive_time) >= 1420041600 and UNIX_TIMESTAMP(A.arrive_time) =< 1462032000";
		sql = "SELECT UNIX_TIMESTAMP(arrive_time) as uTime, date(arrive_time) as dTime, waimai_release_id, pass_uid from tb_baiduwaimai";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			//����ʱ��ɸѡ
			if(rs.getInt("uTime") < 1420041600 || rs.getInt("uTime") > 1462032000) continue;
			
			//��������ɸѡ
			String shopid = rs.getString("waimai_release_id");
			String locationName = mapShopidLocationname.get(shopid);
			String shopName = mapShopidShopname.get(shopid);
			String[] s = mapLocationWeather.get((locationName + rs.getString("dTime")));
			if(s == null) continue;//ȱ�����̵����ڵ�����������Ϣ
			//��ͬ�������ж�
//			if(Double.parseDouble(s[INDEX_TPMAX]) < 27.82) continue;//���������������ʾ��������
//			if(Double.parseDouble(s[INDEX_TPMIN]) > 3.5) continue;//���������������ʾ��������
//			if(Double.parseDouble(s[INDEX_RAINSUM]) < 9.5) continue;//���������������ʾ�ۼƽ�������
//			if(Double.parseDouble(s[INDEX_RAINBUSY]) == 0) continue;//���������������ʾ��ʱ��������
//			if(!s[INDEX_WORKDAY].equals("������")) continue;//���������������ʾ������
//			if(!s[INDEX_WORKDAY].equals("��ĩ")) continue;//���������������ʾ��ĩ
			if(s[INDEX_WORKDAY].equals("������") || s[INDEX_WORKDAY].equals("��ĩ")) continue;//���������������ʾ����
			
			String passID = rs.getString("pass_uid");
//			U.print("shopid:" + shopid + ",shopName:" + shopName + ",passID:" + passID);
			HashSet<String> users = new HashSet<>();
			if(mapNameUsers.get(shopName) == null) //���ޣ����½�
				users = new HashSet<>();
			else //���У������
				users = mapNameUsers.get(shopName);
			users.add(passID);
			mapNameUsers.put(shopName, users);
		}
		
		
		for(String name : mapNameUsers.keySet()){
			mapNameUsercounts.put(name, mapNameUsers.get(name).size());
		}
		
		//��ʼ�����磬��������ϵд�����
		int[][] matrix = new int[6000][6000];
		//д������±�
		Map<Integer, String> mapIndexName = new HashMap<>();
		Map<String, Integer> mapNameIndex = new HashMap<>();
		for(int i = 0; i < listShopName.size(); i++){
			mapIndexName.put(i, listShopName.get(i));
			mapNameIndex.put(listShopName.get(i), i);
		}
		
		//д�����
		for(int i = 0; i < listShopName.size(); i++){
			for(int j = i+1; j < listShopName.size(); j++){
//				U.print("i:" + i + ",j:" + j);
				HashSet<String> hsI = mapNameUsers.get(mapIndexName.get(i));
				HashSet<String> hsJ = mapNameUsers.get(mapIndexName.get(j));
				if(hsI == null || hsJ == null || hsI.size() < thresholdNode || hsJ.size() < thresholdNode){ //����û�еĵ㣬˵���������ֵ�ˣ�ֱ����Ϊ0
					continue;
				}
				else{
					HashBag bag=new HashBag();//HashBag��һ��org.apache.commons.collections.bag���е��࣬���Ժܼ򵥵�������������еĽ���
					bag.addAll(hsI);
					bag.retainAll(hsJ);
					int lineSize = bag.size();
//					U.print("hsI:" + hsI.size() + ",hsJ:" + hsJ.size() + ",lineSize:" + lineSize);
					if(lineSize >= thresholdLine) matrix[i][j] = lineSize;
				}
			}
		}
		//������ֵɸѡidlist
		//�ų���������û����ϵ�ĵ�
		List<Integer> idList = getIdList(matrix, listShopName.size(), 0);//0��ʾ�Ǹ��㶼���ԣ�1��ʾȥ��������
		//������ֵɸѡ��ģ̫С�ĵ�
		for(int i = 0; i < idList.size(); i++){
			if(mapNameUsercounts.get(mapIndexName.get(idList.get(i))) == null 
				|| mapNameUsercounts.get(mapIndexName.get(idList.get(i))) < thresholdNode){
				idList.remove(i);
				i--;
			}
		}
		U.print("idList:" + idList.size());
		
		//д���ļ�
		String pathNode = "E:\\work\\smda����+���ݾ���\\nodeAll.csv";
		String pathLine = "E:\\work\\smda����+���ݾ���\\lineAll.csv";
		writeCSV_Node(idList, mapIndexName, mapNameUsercounts, pathNode);
		writeCSV_Line(idList, matrix, thresholdLine, pathLine);
		U.print("done");
	}
	
	
	
	//д�����ݿ�
	public static void SMDANet_toDB(int thresholdNode, int thresholdLine) throws SQLException, IOException{
		String sql = "";
		ResultSet rs = null;
		
		//��ȡ���е���id(���ݿ���id)�����ֵĶ�Ӧ��ϵ
		Map<String, String> mapShopidShopname = new HashMap<>();
		Map<String, String> mapShopnameShopid = new HashMap<>();
		List<String> listShopName = new ArrayList<>();
		sql = "select shop_id, shop_name from tb_baiduwaimaishop";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			String shopName = rs.getString("shop_name");//��ȡ����
			String shopID = rs.getString("shop_id");
			mapShopidShopname.put(shopID, shopName);
			mapShopnameShopid.put(shopName, shopID);
			if(!listShopName.contains(shopName))
				listShopName.add(shopName);
		}
		U.print("�ѽ�����id�������һһ��Ӧ����" + listShopName.size() + "���̼�");
		
		//��ȡÿ�ҵ��̵��û��б�
		//�ɸĽ�����sql��ѯΪ��һ��
		Map<String, HashSet<String>> mapNameUsers = new HashMap<>();
		Map<String, Integer> mapNameUsercounts = new HashMap<>();
		sql = "SELECT waimai_release_id, pass_uid from tb_baiduwaimai";
		rs = DBFunction.getRS(sql);
		while(rs.next()){
			String shopName = mapShopidShopname.get(rs.getString("waimai_release_id"));
			String passID = rs.getString("pass_uid");
			U.print(passID);
			HashSet<String> users = new HashSet<>();
			if(mapNameUsers.get(shopName) == null) //���ޣ����½�
				users = new HashSet<>();
			else //���У������
				users = mapNameUsers.get(shopName);
			users.add(passID);
			mapNameUsers.put(shopName, users);
		}
		for(String name : mapNameUsers.keySet()){
			mapNameUsercounts.put(name, mapNameUsers.get(name).size());
		}
		
		//��ʼ�����磬��������ϵд�����
		U.print("��ʼд�����");
		int[][] matrix = new int[8100][8100];
		//д������±�
		Map<Integer, String> mapIndexName = new HashMap<>();
		Map<String, Integer> mapNameIndex = new HashMap<>();
		for(int i = 0; i < listShopName.size(); i++){
			mapIndexName.put(i, listShopName.get(i));
			mapNameIndex.put(listShopName.get(i), i);
		}
		//д�����
		for(int i = 0; i < listShopName.size(); i++){
			for(int j = i+1; j < listShopName.size(); j++){
				U.print("i:" + i + ",j:" + j);
				HashSet<String> hsI = mapNameUsers.get(mapIndexName.get(i));
				HashSet<String> hsJ = mapNameUsers.get(mapIndexName.get(j));
				if(hsI == null || hsJ == null || hsI.size() < thresholdNode || hsJ.size() < thresholdNode){//����û�еĵ㣬˵���������ֵ�ˣ�ֱ����Ϊ0
					continue;
				}
				else{
					HashBag bag=new HashBag();//HashBag��һ��org.apache.commons.collections.bag���е��࣬���Ժܼ򵥵�������������еĽ���
					bag.addAll(hsI);
					bag.retainAll(hsJ);
					int bagSize = bag.size();
					if(bagSize >= thresholdLine){
						matrix[i][j] = bag.size();
						matrix[j][i] = bag.size();
					}
				}
			}
		}
		//������ֵɸѡidlist
		//�ų���������û����ϵ�ĵ�
		U.print("shopSize:" + listShopName.size());
		List<Integer> idList = getIdList(matrix, listShopName.size(), 1);
		//������ֵɸѡ��ģ̫С�ĵ�
		for(int i = 0; i < idList.size(); i++){
			if(mapNameUsercounts.get(mapIndexName.get(idList.get(i))) < thresholdNode){
				idList.remove(i);
				i--;
			}
		}
		U.print("idListSize:" + idList.size());
		
		//д���ļ�
		DBFunction.insertNetNode(idList, mapShopnameShopid, mapIndexName, mapNameUsercounts);
		DBFunction.insertNetLine(idList, mapShopnameShopid, mapIndexName, matrix, thresholdLine);
		U.print("done");
	}  
	
	
	
	
	
	private static List<Integer> getIdList(int[][] matrix, int nodeCount, int threshold){
		List<Integer> idList = new ArrayList<>();//��Ÿ�����ֵ��id
		for(int idi = 0; idi < nodeCount; idi++){
			int frequency = 0;
			for(int idj = 0; idj < nodeCount; idj++){
				//ͳ�Ƹù�˾���ֵ�Ƶ�ʣ�Ŀǰ��������˫���ͷ��
				if(matrix[idi][idj] > 0)
					frequency += 1;
			}
			if(frequency >= threshold)
				idList.add(idi);
		}
		return idList;
	}
	
	private static void writeCSV_Node(List<Integer> idList, Map<Integer, String> mapIdCompany, Map<String, Integer> mapHolderWeight, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("Id,Label,weighted degree\r\n");
		for(int i = 0; i < idList.size(); i++){
			fw.write((i+1) + "," + mapIdCompany.get(idList.get(i)) + "," + mapHolderWeight.get(mapIdCompany.get(idList.get(i))) + "\r\n");
		}
		fw.close();
	}
	
	private static void writeCSV_Line(List<Integer> idList, int[][] matrixWeight, int thresholdLine, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("Source,Target,Type,id,label,timeset,weight\r\n");
		int lineId = 0;
		for(int i = 0; i < idList.size(); i++){
			for(int j = 0; j < idList.size(); j++){
				if(matrixWeight[idList.get(i)][idList.get(j)] >= thresholdLine){
					int weight = matrixWeight[idList.get(i)][idList.get(j)];
					fw.write((i+1) + "," + (j+1) + "," + "unDirected" + "," + lineId++ + "," + "," + "," + weight + "\r\n");
				}
			}
		}
		fw.close();
	}
	
	
}
