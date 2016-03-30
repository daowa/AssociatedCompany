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

	//将公司名和出现频次写入txt
	public static void writeCompanyAndFrequency(TreeMap<String, Integer> map) throws IOException{
		FileWriter fw = new FileWriter("E:/work/关联公司/txt/companyAndFrequency.txt");
		Set<Entry<String, Integer>> set = map.entrySet();
		for(Entry<String, Integer> i : set){
			fw.write(i.getKey() + "\t" + i.getValue());
			fw.write("\r\n");
		}
		fw.close();
		U.print("公司名与出现频次已输出到：E:/work/关联公司/txt/companyAndFrequency.txt");
	}
	
	//仅将公司名输出
	public static void writeCompanyName(TreeMap<String, Integer> map) throws IOException{
		FileWriter fw = new FileWriter("E:/work/关联公司/txt/companyName.txt");
		Set<Entry<String, Integer>> set = map.entrySet();
		for(Entry<String, Integer> i : set){
			fw.write(i.getKey());
			fw.write("\r\n");
		}
		fw.close();
		U.print("公司名已输出到：E:/work/关联公司/txt/companyName.txt");
	}
	
}
