package com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.myClass.U;

public class DBFunction {

	private static String dbDriver="com.mysql.jdbc.Driver";   
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
	
}
