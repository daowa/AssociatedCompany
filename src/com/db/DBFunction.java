package com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.myClass.U;

public class DBFunction {

	private static String dbDriver="com.mysql.jdbc.Driver"; 
	//本地服务器
//	private static String dbUrl="jdbc:mysql://localhost:3306/travelstream?useUnicode=true&characterEncoding=utf-8&useSSL=false&8&tcpRcvBuf=2048000";
//	private static String dbUrl="jdbc:mysql://localhost:3306/sie?useUnicode=true&characterEncoding=utf-8&useSSL=false&8&tcpRcvBuf=2048000";
//	private static String dbUser="root";  
//  	private static String dbPass="abcd@123";
	//smda服务器
    private static String dbUrl="jdbc:mysql://192.168.1.119:3306/smda?useUnicode=true&characterEncoding=utf-8&useSSL=false&8&tcpRcvBuf=2048000";
    private static String dbUser="mysqlsmda";  
    private static String dbPass="123456";
    
    private static Connection cnn = getConn();
    
    private static Connection getConn()  
    {  
        Connection conn=null;  
        try  
        {  
            Class.forName(dbDriver);  
        }  
        catch (ClassNotFoundException e)  
        {  
            e.printStackTrace();  
        }  
        try  
        {  
            conn = DriverManager.getConnection(dbUrl,dbUser,dbPass);//注意是三个参数  
        }  
        catch (SQLException e)  
        {  
            e.printStackTrace();  
        }  
        return conn;  
    }  
    
    // 获取数据
    public static ResultSet getRS(String sql){
    	ResultSet rs = null;
    	try{
    		Statement stmt = cnn.createStatement();//两个参数来结果集中的指针可以移动
    		rs = stmt.executeQuery(sql); 
    	} catch (Exception e) {
    		U.print(e.toString());
			e.printStackTrace();
		}  
    	return rs;
    }
    
    
    
    
    
    //插入数据
    //SMDA相关
    public static void insertNetNode(List<Integer> idList, Map<String, String> mapShopnameShopid, Map<Integer, String> mapIdCompany, Map<String, Integer> mapHolderWeight){
    	String sql = "insert into net_node_new(shopid, shopname, value) values(?, ?, ?)";
        try{
        	PreparedStatement preStmt =cnn.prepareStatement(sql);
        	for(int i = 0; i < idList.size(); i++){
        		U.print("node:" + i);
	            preStmt.setString(1, mapShopnameShopid.get(mapIdCompany.get(idList.get(i))));  
	            preStmt.setString(2, mapIdCompany.get(idList.get(i)));
	            preStmt.setInt(3, mapHolderWeight.get(mapIdCompany.get(idList.get(i))));
	            preStmt.addBatch();
        	}
        	preStmt.executeBatch();
        }  
        catch (SQLException e)  
        {  
            e.printStackTrace();  
        }  
    }
    public static void insertNetLine(List<Integer> idList, Map<String, String> mapShopnameShopid, Map<Integer, String> mapIdCompany, int[][] matrixWeight, int thresholdLine){
    	String sql = "insert into net_line_new(source, target, weight) values(?, ?, ?)";
    	try{
    		PreparedStatement ps = cnn.prepareStatement(sql);
    		for(int i = 0; i < idList.size(); i++){
    			for(int j = 0; j < idList.size(); j++){
    				if(matrixWeight[idList.get(i)][idList.get(j)] >= thresholdLine){
    					U.print("line:" + i + "," + j);
    					int weight = matrixWeight[idList.get(i)][idList.get(j)];
    					ps.setString(1, mapShopnameShopid.get(mapIdCompany.get(idList.get(i))));
    		    		ps.setString(2, mapShopnameShopid.get(mapIdCompany.get(idList.get(j))));
    		    		ps.setInt(3, weight);
    		    		ps.addBatch();
    				}
    			}
    		}
    		ps.executeBatch();
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    //插入foodword在不同天气下的销量数据
    public static void insertFoodwordBase(Map<String, Vector<Integer>> mapFoodwordBase){
    	String sql = "insert into foodword_base(foodword, tp_avg0, tp_avg1, tp_avg2, tp_avg3, " +
    			"tp_df0, tp_df1, tp_df2, tp_df3, " +
    			"rain_sum0, rain_sum1, rain_sum2, " +
    			"workday_weekday, workday_weekend, workday_holiday) " +
    			"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    	try{
    		PreparedStatement ps = cnn.prepareStatement(sql);
    		int timer = 0;
    		for(Entry<String, Vector<Integer>> entry : mapFoodwordBase.entrySet()){
    			U.print("插入到第" + ++timer + "条订单");
    			String foodword = entry.getKey();
    			Vector<Integer> v = entry.getValue();
    			ps.setString(1, foodword);
    			for(int i = 0; i < v.size(); i++){
    				ps.setInt(i+2, v.get(i));
    			}
    			ps.addBatch();
    		}
    		ps.executeBatch();
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    //插入shopname在不同天气下的销量数据
    public static void insertShopnameBase(Map<String, Vector<Integer>> mapShopnameBase){
    	String sql = "insert into shopname_base(shopname, tp_avg0, tp_avg1, tp_avg2, tp_avg3, " +
    			"tp_df0, tp_df1, tp_df2, tp_df3, " +
    			"rain_sum0, rain_sum1, rain_sum2, " +
    			"workday_weekday, workday_weekend, workday_holiday) " +
    			"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    	try{
    		PreparedStatement ps = cnn.prepareStatement(sql);
    		int timer = 0;
    		for(Entry<String, Vector<Integer>> entry : mapShopnameBase.entrySet()){
    			U.print("插入到第" + ++timer + "条订单");
    			String foodword = entry.getKey();
    			Vector<Integer> v = entry.getValue();
    			ps.setString(1, foodword);
    			for(int i = 0; i < v.size(); i++){
    				ps.setInt(i+2, v.get(i));
    			}
    			ps.addBatch();
    		}
    		ps.executeBatch();
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    //插入user在不同天气下的销量数据
    public static void insertUserBase2(Map<String, String> mapUidName, Map<String, List<String>> mapUidLocationid, Map<String, Vector<Integer>> mapUidBase){
    	String sql = "insert into user_weather2(passid, passname, locationid, tp_avg0, tp_avg1, tp_avg2, tp_avg3, " +
    			"tp_df0, tp_df1, tp_df2, tp_df3, " +
    			"rain_sum0, rain_sum1, rain_sum2, " +
    			"workday_weekday, workday_weekend, workday_holiday) " +
    			"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    	try{
    		PreparedStatement ps = cnn.prepareStatement(sql);
    		int timer = 0;
    		for(Entry<String, Vector<Integer>> entry : mapUidBase.entrySet()){
    			U.print("插入到第" + ++timer + "条数据");
    			String uid = entry.getKey();
    			Vector<Integer> v = entry.getValue();
    			ps.setString(1, uid);
    			ps.setString(2, mapUidName.get(uid));
    			ps.setString(3, mapUidLocationid.get(uid).toString());
    			for(int i = 0; i < v.size(); i++){
    				ps.setInt(i+4, v.get(i));
    			}
    			ps.addBatch();
    		}
    		ps.executeBatch();
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    //插入WeatherFoodIncrease
    public static void insertWeatherFoodIncrease(Map<String, String> mapWeatherIncrease){
    	String sql = "insert into weather_foodincrease(weather, increase) values(?, ?)";
    	try{
    		PreparedStatement ps = cnn.prepareStatement(sql);
    		for(Entry<String, String> entry : mapWeatherIncrease.entrySet()){
    			String weather = entry.getKey();
    			String increase = entry.getValue();
    			ps.setString(1, weather);
    			ps.setString(2, increase);
    			ps.addBatch();
    		}
    		ps.executeBatch();
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    //插入WeatherUserIncrease
    public static void insertWeatherUserIncrease(List<List<String>> list2WeatherIncrease){
    	String sql = "insert into weather_userincrease(weather, locationid, increase) values(?, ?, ?)";
    	try{
    		PreparedStatement ps = cnn.prepareStatement(sql);
    		for(List<String> list : list2WeatherIncrease){
    			String weather = list.get(0);
    			String locationid = list.get(1);
    			String increase = list.get(2);
    			ps.setString(1, weather);
    			ps.setString(2, locationid);
    			ps.setString(3, increase);
    			ps.addBatch();
    		}
    		ps.executeBatch();
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    //插入UserLoyalty
    public static void insertUserLoyalty(Map<String, Double> mapUserComentropy, Map<String, List<String>> mapUserTop){
    	String sql = "insert into user_loyalty(pass_uid, comentropy, top1, top2, top3) values(?, ?, ?, ?, ?)";
    	try{
    		PreparedStatement ps = cnn.prepareStatement(sql);
    		for(Entry<String, Double> entry : mapUserComentropy.entrySet()){
    			String uid = entry.getKey();
    			double comentropy = entry.getValue();
    			String top1 = "";
    			String top2 = "";
    			String top3 = "";
    			List<String> listTop = mapUserTop.get(entry.getKey());
    			if(listTop.size() > 0) top1 = listTop.get(0);
    			if(listTop.size() > 1) top2 = listTop.get(1);
    			if(listTop.size() > 2) top3 = listTop.get(2);
    			ps.setString(1, uid);
    			ps.setDouble(2, comentropy);
    			ps.setString(3, top1);
    			ps.setString(4, top2);
    			ps.setString(5, top3);
    			ps.addBatch();
    		}
    		ps.executeBatch();
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    //插入shopid-loyalty
    public static void insertShopidLoyalty(Map<String, Double> mapShopidAvgcomentropy){
    	String sql = "insert into shopid_loyalty(shopid, comentropy) values(?, ?)";
    	try{
    		int timer = 0;
    		PreparedStatement ps = cnn.prepareStatement(sql);
    		for(Entry<String, Double> entry : mapShopidAvgcomentropy.entrySet()){
    			U.print("插入到第" + ++timer + "条数据");
    			String uid = entry.getKey();
    			double comentropy = entry.getValue();
    			ps.setString(1, uid);
    			ps.setDouble(2, comentropy);
    			ps.addBatch();
    		}
    		ps.executeBatch();
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    
    
    
    //SIE相关
    public static void insertArticle(List<List<String>> lists){
    	String sql = "insert into article(wosID, title, time_, references_, keywords) " +
    			"values(?, ?, ?, ?, ?)";
    	try{
    		PreparedStatement ps = cnn.prepareStatement(sql);
    		int timer = 0;
    		for(List<String> list : lists){
    			U.print("插入到第" + ++timer + "条文献");
    			ps.setString(1, list.get(0));
    			ps.setString(2, list.get(1));
    			ps.setString(3, list.get(2));
    			ps.setString(4, list.get(3));
    			ps.setString(5, list.get(4));
    			ps.addBatch();
    		}
    		ps.executeBatch();
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    	}
    	U.print("插入数据库结束");
    }
	
}
