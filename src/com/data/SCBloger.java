package com.data;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.myClass.U;

public class SCBloger {
	
	private static String dbDriver="com.mysql.jdbc.Driver";   
    private static String dbUrl="jdbc:mysql://222.204.246.117:3306/scienceblog?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static String dbUser="dzf";  
    private static String dbPass="dzf";
    
    private static Connection cnn = getConn();
    private static List<String> nameList = new ArrayList<>();
    private static List<String> connectList = new ArrayList<>();
	
	public static void SCBlorger() throws SQLException, IOException{
		initNameList();//取两张表，将名字不重复地写入list
		initConnectList();//关联两张表，将关系写入list
		writeNet();//将信息写入.net格式文件，以绘制网络图
	}
	
	
	
	
	//连接数据库
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
    
    
    private static void initNameList() throws SQLException{
    	String sql;
    	Statement stmt;
    	ResultSet rs;
    	String name;
    	//从博文表中获取所有姓名
    	sql = "select author from " + "scienceblog.20140109_update_basedata";
		try {
			stmt = cnn.createStatement(); 
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				name = rs.getString("author");
				U.print(name);
				if(!nameList.contains(name))
					nameList.add(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//从评论表中获取所有姓名
    	sql = "select ComAuthor from " + "scienceblog.20140109_update_basedata_commentinfor";
		try {
			stmt = cnn.createStatement(); 
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				name = rs.getString("ComAuthor");
				U.print(name);
				if(!nameList.contains(name))
					nameList.add(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		U.print(nameList.size());
    }
    
    private static void initConnectList(){
    	//关联表，并获取关系
    	String sql = "SELECT author, ComAuthor FROM scienceblog.20140109_update_basedata inner join scienceblog.20140109_update_basedata_commentinfor on scienceblog.20140109_update_basedata.id = scienceblog.20140109_update_basedata_commentinfor.Mid;";
		try {
			Statement stmt = cnn.createStatement(); 
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				String author = rs.getString("author");
				String ComAuthor = rs.getString("ComAuthor");
				String connect = getID(ComAuthor) + " " + getID(author);
				U.print(connect);
				if(!connectList.contains(connect))
					connectList.add(connect);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    private static int getID(String name){
    	return nameList.indexOf(name) + 1;
    }

    private static void writeNet() throws IOException{
    	U.print("开始写入文件");
    	String address = "E:\\work\\科学网博客\\scblog.net";
    	FileWriter fw = new FileWriter(address);
		fw.write("*Vertices " + nameList.size());
		for(int fwi = 0; fwi < nameList.size(); fwi++){
			fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
			fw.write((fwi+1) + " \"" + nameList.get(fwi) + "\"");
		}
		fw.write("\r\n");
		fw.write("*Edges");
		for(int fwi = 0; fwi < connectList.size(); fwi++){
			fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
			fw.write(connectList.get(fwi));
		}
		fw.close();
		U.print("写入文件完成");
    }
}
