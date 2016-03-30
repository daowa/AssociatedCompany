package com.db;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.myClass.U;

public class FileFunction {

	//����˾���ͳ���Ƶ��д��txt
	public static void writeCompanyAndFrequency(TreeMap<String, Integer> map) throws IOException{
		FileWriter fw = new FileWriter("E:/work/������˾/txt/companyAndFrequency.txt");
		Set<Entry<String, Integer>> set = map.entrySet();
		for(Entry<String, Integer> i : set){
			fw.write(i.getKey() + "\t" + i.getValue());
			fw.write("\r\n");
		}
		fw.close();
		U.print("��˾�������Ƶ�����������E:/work/������˾/txt/companyAndFrequency.txt");
	}
	
	//������˾�����
	public static void writeCompanyName(TreeMap<String, Integer> map) throws IOException{
		FileWriter fw = new FileWriter("E:/work/������˾/txt/companyName.txt");
		Set<Entry<String, Integer>> set = map.entrySet();
		for(Entry<String, Integer> i : set){
			fw.write(i.getKey());
			fw.write("\r\n");
		}
		fw.close();
		U.print("��˾�����������E:/work/������˾/txt/companyName.txt");
	}
	
}
