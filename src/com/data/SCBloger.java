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
		initNameList();//ȡ���ű������ֲ��ظ���д��list
		initConnectList();//�������ű�����ϵд��list
		writeNet();//����Ϣд��.net��ʽ�ļ����Ի�������ͼ
	}
	
	
	
	
	//�������ݿ�
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
            conn = DriverManager.getConnection(dbUrl,dbUser,dbPass);//ע������������  
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
    	//�Ӳ��ı��л�ȡ��������
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
		//�����۱��л�ȡ��������
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
    	//����������ȡ��ϵ
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
    	U.print("��ʼд���ļ�");
    	String address = "E:\\work\\��ѧ������\\scblog.net";
    	FileWriter fw = new FileWriter(address);
		fw.write("*Vertices " + nameList.size());
		for(int fwi = 0; fwi < nameList.size(); fwi++){
			fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
			fw.write((fwi+1) + " \"" + nameList.get(fwi) + "\"");
		}
		fw.write("\r\n");
		fw.write("*Edges");
		for(int fwi = 0; fwi < connectList.size(); fwi++){
			fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
			fw.write(connectList.get(fwi));
		}
		fw.close();
		U.print("д���ļ����");
    }
}
