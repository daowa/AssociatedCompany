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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.myClass.M;
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
	
	//从net中读取公司名列表
	public static List<String> readCompanyName(String path) throws NumberFormatException, IOException{
		List<String> list = new ArrayList<String>();
		File file = new File(path);
		InputStreamReader stream = new InputStreamReader(new FileInputStream(file));
		BufferedReader reader = new BufferedReader(stream);
		//读取共有多少个公司
		String line = reader.readLine();
		int count = Integer.parseInt(line.substring(10, line.length()));
		U.print("读取" + count + "家公司");
		for(int i = 0; i < count; i++){
			line = reader.readLine();
			Pattern p = Pattern.compile("\".*\"");
			Matcher m=p.matcher(line);
			if(m.find())
				list.add(m.group(0).substring(1, m.group(0).length()-1));
		}
		return list;
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
	public static void writeNet_Simple(List<Integer> idList, Map<Integer, String> mapIdCompany, byte[][] matrix, String address) throws IOException{
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
				if(matrix[idList.get(fwi)][idList.get(fwj)] > 0){
					fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
					fw.write((fwi+1) + " " + (fwj+1));
				}
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
	//第一个参数是id列表，第二个参数是“id-公司”的map,第三个参数是关系矩阵（引用传递），第四个对象是写入的地址，第五个参数是确定颜色的规则, 第六个参数是“公司-属性”的map（用于确定颜色 ，可不填）
	public static void writeNet_Color(List<Integer> idList, Map<Integer, String> mapIdCompany, byte[][] matrix, String address, int colorRule, Map map) throws IOException{
		FileWriter fw = new FileWriter(address);
		fw.write("*Vertices " + idList.size());
		for(int fwi = 0; fwi < idList.size(); fwi++){
			String cpName = mapIdCompany.get(idList.get(fwi));
			fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
			String color = "";
			if(colorRule == M.COLOR_ADDRESS)
				color = U.getAddressColor(map.get(cpName).toString());
			else if(colorRule == M.COLOR_COMPANYTYPE)
				color = U.getCompanyTypeColor(map.get(cpName)!=null ? Integer.parseInt(map.get(cpName).toString()) : M.COMPANYTYPE_NOIPO);
			else if(colorRule == M.COLOR_STARCOMPANY){
				if(cpName.equals(map.get("star")))
					color = "Red";
				else
					color = "Gray";
			}
			fw.write((fwi+1) + " \"" + cpName + "\"" + " ic " + color);
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
	}
	
	
	//将结构洞、中心度等输出
	//输出结构化的中心性分析的txt,形如“1,万科企业股份有限公司,3.341,0.180,2.079,0.000”
	public static void writeCentrality(String txtName) throws IOException{
		List<String> list = FileFunction.readFile("E:\\work\\关联公司\\txt\\中心度研究\\" + txtName + ".txt");
		List<String> output = new ArrayList<String>();
		for(int i = 15; i < list.size(); i++){
			String line = list.get(i);
			if(line.equals("")) break;
			output.add(line.replaceAll(" {2,}", ","));
		}
		FileWriter fw = new FileWriter("E:\\work\\关联公司\\txt\\中心度研究\\output\\result_" + txtName + ".txt");
		for(int i = 0; i < output.size(); i++){
			fw.write(output.get(i) + "\r\n");
		}
		fw.close();
		U.print("done");
	}
	//输出结构化的结构洞分析的txt,形如“深圳发展银行股份有限公司,1.000,1.000,1.000,1.000,0.000”
	public static void writeStructualHoles(String txtName) throws IOException{
		List<String> list = FileFunction.readFile("E:\\work\\关联公司\\txt\\结构洞研究\\" + txtName + ".txt");
		List<String> output = new ArrayList<String>();
		for(int i = 14; i < list.size(); i++){
			String line = list.get(i);
			if(line.equals("")) break;
			output.add(line.trim().replaceAll(" {2,}", ","));
		}
		FileWriter fw = new FileWriter("E:\\work\\关联公司\\txt\\结构洞研究\\output\\result_" + txtName + ".txt");
		for(int i = 0; i < output.size(); i++){
			fw.write(output.get(i) + "\r\n");
		}
		fw.close();
		U.print("done");
	}
	
	
	//输出Partition所需的格式
	//即“天虹商场股份有限公司 type 0”这种格式
	//第一个参数表示公司列表，第二个参数表示“公司-分类”的键值对，第三个参数表示“分类-类型数字”键值对，第四个参数表示输出的地址
	public static void writePartition(List<String> cpList, Map<String, String> mapCompanyClassify, Map<String, Integer> mapClassifyType, String address) throws IOException{
		FileWriter fw = new FileWriter(address);
		fw.write("dl nr = " + cpList.size() + ", nc = 1 format = edgelist2" + "\r\n");
		fw.write("row labels embedded" + "\r\n");
		fw.write("col labels embedded" + "\r\n");
		fw.write("data:" + "\r\n");
		for(String cpName : cpList){
			cpName = cpName.trim().replaceAll(" ", "");
			int type = -1;
			if(mapCompanyClassify.get(cpName) != null)
				type = mapClassifyType.get(mapCompanyClassify.get(cpName));
			fw.write(cpName + " type " + type + "\r\n");
			
			if(type == -1) U.print(cpName);
		}
		fw.close();
		U.print("done");
	}
}
