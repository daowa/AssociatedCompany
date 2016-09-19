package com.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	//无编码读取，将每一行写入List中
	public static List<String> readFile(String address) throws FileNotFoundException{
		List<String> list = new ArrayList<String>();
		File file = new File(address);
		FileInputStream in = new FileInputStream(file);  
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			//以行为单位读取关键词
			String s = "";
			while((s = reader.readLine()) != null){
				s = s.trim();
				list.add(s);
			}
			reader.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(reader != null)
					reader.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	//有编码格式读取（如mysql常输出的utf-8），将每一行写入List中
	public static List<String> readFile(String address, String encode) throws FileNotFoundException{
		List<String> list = new ArrayList<String>();
		File file = new File(address);
		FileInputStream in = new FileInputStream(file);  
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, encode));
			//以行为单位读取关键词
			String s = "";
			while((s = reader.readLine()) != null){
				s = s.trim();
				list.add(s);
			}
			reader.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(reader != null)
					reader.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	//读取txt，并返回map格式数据
	public static Map<String, Integer> readMap_SI(String address) throws NumberFormatException, IOException{
		Map<String, Integer> map = new HashMap<>();
		File file = new File(address);
		InputStreamReader stream = new InputStreamReader(new FileInputStream(file));
		BufferedReader reader = new BufferedReader(stream);
		String line = "";
		while((line = reader.readLine()) != null){
			if(line == "") break;//说明读到最最后一行了
			String[] cpType = line.split("\t");
			map.put(cpType[0], Integer.parseInt(cpType[1]));
		}
		return map;
	}
	public static Map<String, String> readMap_SS(String address) throws NumberFormatException, IOException{
		Map<String, String> map = new HashMap<>();
		File file = new File(address);
		InputStreamReader stream = new InputStreamReader(new FileInputStream(file));
		BufferedReader reader = new BufferedReader(stream);
		String line = "";
		while((line = reader.readLine()) != null){
			if(line == "") break;//说明读到最最后一行了
			String[] cpType = line.split("\t");
			map.put(cpType[0], cpType[1]);
		}
		return map;
	}

	//将Map的键值对都写入txt
	public static void writeMap_KV(Map map, String address) throws IOException{
		FileWriter fw = new FileWriter(address);
		int count = 0;//记录共有多少条记录
		Set<Entry<String, Object>> set = map.entrySet();
		for(Entry<String, Object> i : set){
			count ++;
			fw.write(i.getKey() + "\t" + i.getValue().toString());
			fw.write("\r\n");
		}
		fw.close();
		U.print("已输出到：" + address + " ,共" + count + "条记录");
	}
	//仅将Map的key写入txt
	public static void writeMap_K(Map map, String address) throws IOException{
		FileWriter fw = new FileWriter(address);
		int count = 0;//记录共有多少条记录
		Set<Entry<String, Object>> set = map.entrySet();
		for(Entry<String, Object> i : set){
			count ++;
			fw.write(i.getKey());
			fw.write("\r\n");
		}
		fw.close();
		U.print("已输出到：" + address + " ,共" + count + "条记录");
	}
	
	
	//将关联网络输出成pajek可以读取的格式
	//输出成为.net格式，仅包含点，以及点之间是否有连线
	//第一个参数是id列表，第二个参数是“id-公司”的map,第三个对象是写入的地址
	public static void writeNet_Simple(List<Integer> idList, Map<Integer, String> mapIdCompany, String address) throws IOException{
		FileWriter fw = new FileWriter(address);
		fw.write("*Vertices " + idList.size());
		for(int fwi = 0; fwi < idList.size(); fwi++){
			fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
			fw.write((fwi+1) + " \"" + mapIdCompany.get(idList.get(fwi)) + "\"");
		}
		fw.write("\r\n");
		fw.write("*Edges");
		for(int fwi = 0; fwi < idList.size(); fwi++){
			for(int fwj = 0; fwj < idList.size(); fwj++){
				fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
				fw.write((fwi+1) + " " + (fwj+1));
			}
		}
		fw.close();
	}
	//第一个参数是id列表，第二个参数是“id-公司”的map,第三个参数是关系矩阵（引用传递），第四个对象是写入的地址
	public static void writeNet_Weight(List<Integer> idList, Map<Integer, String> mapIdCompany, byte[][] matrix, String address) throws IOException{
		FileWriter fw = new FileWriter(address);
		fw.write("*Vertices " + idList.size());
		for(int fwi = 0; fwi < idList.size(); fwi++){
			fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
			fw.write((fwi+1) + " \"" + mapIdCompany.get(idList.get(fwi)) + "\"");
		}
		fw.write("\r\n");
		fw.write("*Edges");
		for(int fwi = 0; fwi < idList.size(); fwi++){
			for(int fwj = 0; fwj < idList.size(); fwj++){
				int weight = matrix[idList.get(fwi)][idList.get(fwj)];
				for(int weightI = 0; weightI < weight; weightI++){
					fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
					fw.write((fwi+1) + " " + (fwj+1));
				}
			}
		}
		fw.close();
		U.print("已输出到" + address);
	}
}
