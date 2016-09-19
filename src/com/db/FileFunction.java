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
	
	//�ޱ����ȡ����ÿһ��д��List��
	public static List<String> readFile(String address) throws FileNotFoundException{
		List<String> list = new ArrayList<String>();
		File file = new File(address);
		FileInputStream in = new FileInputStream(file);  
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			//����Ϊ��λ��ȡ�ؼ���
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
	
	//�б����ʽ��ȡ����mysql�������utf-8������ÿһ��д��List��
	public static List<String> readFile(String address, String encode) throws FileNotFoundException{
		List<String> list = new ArrayList<String>();
		File file = new File(address);
		FileInputStream in = new FileInputStream(file);  
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, encode));
			//����Ϊ��λ��ȡ�ؼ���
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
	
	//��ȡtxt��������map��ʽ����
	public static Map<String, Integer> readMap_SI(String address) throws NumberFormatException, IOException{
		Map<String, Integer> map = new HashMap<>();
		File file = new File(address);
		InputStreamReader stream = new InputStreamReader(new FileInputStream(file));
		BufferedReader reader = new BufferedReader(stream);
		String line = "";
		while((line = reader.readLine()) != null){
			if(line == "") break;//˵�����������һ����
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
			if(line == "") break;//˵�����������һ����
			String[] cpType = line.split("\t");
			map.put(cpType[0], cpType[1]);
		}
		return map;
	}

	//��Map�ļ�ֵ�Զ�д��txt
	public static void writeMap_KV(Map map, String address) throws IOException{
		FileWriter fw = new FileWriter(address);
		int count = 0;//��¼���ж�������¼
		Set<Entry<String, Object>> set = map.entrySet();
		for(Entry<String, Object> i : set){
			count ++;
			fw.write(i.getKey() + "\t" + i.getValue().toString());
			fw.write("\r\n");
		}
		fw.close();
		U.print("���������" + address + " ,��" + count + "����¼");
	}
	//����Map��keyд��txt
	public static void writeMap_K(Map map, String address) throws IOException{
		FileWriter fw = new FileWriter(address);
		int count = 0;//��¼���ж�������¼
		Set<Entry<String, Object>> set = map.entrySet();
		for(Entry<String, Object> i : set){
			count ++;
			fw.write(i.getKey());
			fw.write("\r\n");
		}
		fw.close();
		U.print("���������" + address + " ,��" + count + "����¼");
	}
//	public static void writeCompanyAndFrequency(TreeMap<String, Integer> map) throws IOException{
//		FileWriter fw = new FileWriter("E:/work/������˾/txt/companyAndFrequency.txt");
//		int count = 0;//��¼���ж��ٸ���˾
//		Set<Entry<String, Integer>> set = map.entrySet();
//		for(Entry<String, Integer> i : set){
//			count ++;
//			fw.write(i.getKey() + "\t" + i.getValue());
//			fw.write("\r\n");
//		}
//		fw.close();
//		U.print("��˾�������Ƶ�����������E:/work/������˾/txt/companyAndFrequency.txt,"
//				+ "��" + count + "�ҹ�˾");
//	}
	
	//������˾�����
//	public static void writeCompanyName(TreeMap<String, Integer> map) throws IOException{
//		FileWriter fw = new FileWriter("E:/work/������˾/txt/companyName.txt");
//		Set<Entry<String, Integer>> set = map.entrySet();
//		for(Entry<String, Integer> i : set){
//			fw.write(i.getKey());
//			fw.write("\r\n");
//		}
//		fw.close();
//		U.print("��˾�����������E:/work/������˾/txt/companyName.txt");
//	}
	
}
