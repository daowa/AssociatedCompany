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
	
	//��net�ж�ȡ��˾���б�
	public static List<String> readCompanyName(String path) throws NumberFormatException, IOException{
		List<String> list = new ArrayList<String>();
		File file = new File(path);
		InputStreamReader stream = new InputStreamReader(new FileInputStream(file));
		BufferedReader reader = new BufferedReader(stream);
		//��ȡ���ж��ٸ���˾
		String line = reader.readLine();
		int count = Integer.parseInt(line.substring(10, line.length()));
		U.print("��ȡ" + count + "�ҹ�˾");
		for(int i = 0; i < count; i++){
			line = reader.readLine();
			Pattern p = Pattern.compile("\".*\"");
			Matcher m=p.matcher(line);
			if(m.find())
				list.add(m.group(0).substring(1, m.group(0).length()-1));
		}
		return list;
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
	
	
	//���������������pajek���Զ�ȡ�ĸ�ʽ
	//�����Ϊ.net��ʽ���������㣬�Լ���֮���Ƿ�������
	//��һ��������id�б��ڶ��������ǡ�id-��˾����map,������������д��ĵ�ַ
	public static void writeNet_Simple(List<Integer> idList, Map<Integer, String> mapIdCompany, byte[][] matrix, String address) throws IOException{
		FileWriter fw = new FileWriter(address);
		fw.write("*Vertices " + idList.size());
		for(int fwi = 0; fwi < idList.size(); fwi++){
			fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
			fw.write((fwi+1) + " \"" + mapIdCompany.get(idList.get(fwi)) + "\"");
		}
		fw.write("\r\n");
		fw.write("*Edges");
		for(int fwi = 0; fwi < idList.size(); fwi++){
			for(int fwj = 0; fwj < idList.size(); fwj++){
				if(matrix[idList.get(fwi)][idList.get(fwj)] > 0){
					fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
					fw.write((fwi+1) + " " + (fwj+1));
				}
			}
		}
		fw.close();
	}
	//��һ��������id�б��ڶ��������ǡ�id-��˾����map,�����������ǹ�ϵ�������ô��ݣ������ĸ�������д��ĵ�ַ
	public static void writeNet_Weight(List<Integer> idList, Map<Integer, String> mapIdCompany, byte[][] matrix, String address) throws IOException{
		FileWriter fw = new FileWriter(address);
		fw.write("*Vertices " + idList.size());
		for(int fwi = 0; fwi < idList.size(); fwi++){
			fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
			fw.write((fwi+1) + " \"" + mapIdCompany.get(idList.get(fwi)) + "\"");
		}
		fw.write("\r\n");
		fw.write("*Edges");
		for(int fwi = 0; fwi < idList.size(); fwi++){
			for(int fwj = 0; fwj < idList.size(); fwj++){
				int weight = matrix[idList.get(fwi)][idList.get(fwj)];
				for(int weightI = 0; weightI < weight; weightI++){
					fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
					fw.write((fwi+1) + " " + (fwj+1));
				}
			}
		}
		fw.close();
		U.print("�������" + address);
	}
	//��һ��������id�б��ڶ��������ǡ�id-��˾����map,�����������ǹ�ϵ�������ô��ݣ������ĸ�������д��ĵ�ַ�������������ȷ����ɫ�Ĺ���, �����������ǡ���˾-���ԡ���map������ȷ����ɫ ���ɲ��
	public static void writeNet_Color(List<Integer> idList, Map<Integer, String> mapIdCompany, byte[][] matrix, String address, int colorRule, Map map) throws IOException{
		FileWriter fw = new FileWriter(address);
		fw.write("*Vertices " + idList.size());
		for(int fwi = 0; fwi < idList.size(); fwi++){
			String cpName = mapIdCompany.get(idList.get(fwi));
			fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
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
					fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
					fw.write((fwi+1) + " " + (fwj+1));
				}
			}
		}
		fw.close();
	}
	
	
	//���ṹ�������Ķȵ����
	//����ṹ���������Է�����txt,���硰1,�����ҵ�ɷ����޹�˾,3.341,0.180,2.079,0.000��
	public static void writeCentrality(String txtName) throws IOException{
		List<String> list = FileFunction.readFile("E:\\work\\������˾\\txt\\���Ķ��о�\\" + txtName + ".txt");
		List<String> output = new ArrayList<String>();
		for(int i = 15; i < list.size(); i++){
			String line = list.get(i);
			if(line.equals("")) break;
			output.add(line.replaceAll(" {2,}", ","));
		}
		FileWriter fw = new FileWriter("E:\\work\\������˾\\txt\\���Ķ��о�\\output\\result_" + txtName + ".txt");
		for(int i = 0; i < output.size(); i++){
			fw.write(output.get(i) + "\r\n");
		}
		fw.close();
		U.print("done");
	}
	//����ṹ���Ľṹ��������txt,���硰���ڷ�չ���йɷ����޹�˾,1.000,1.000,1.000,1.000,0.000��
	public static void writeStructualHoles(String txtName) throws IOException{
		List<String> list = FileFunction.readFile("E:\\work\\������˾\\txt\\�ṹ���о�\\" + txtName + ".txt");
		List<String> output = new ArrayList<String>();
		for(int i = 14; i < list.size(); i++){
			String line = list.get(i);
			if(line.equals("")) break;
			output.add(line.trim().replaceAll(" {2,}", ","));
		}
		FileWriter fw = new FileWriter("E:\\work\\������˾\\txt\\�ṹ���о�\\output\\result_" + txtName + ".txt");
		for(int i = 0; i < output.size(); i++){
			fw.write(output.get(i) + "\r\n");
		}
		fw.close();
		U.print("done");
	}
	
	
	//���Partition����ĸ�ʽ
	//��������̳��ɷ����޹�˾ type 0�����ָ�ʽ
	//��һ��������ʾ��˾�б��ڶ���������ʾ����˾-���ࡱ�ļ�ֵ�ԣ�������������ʾ������-�������֡���ֵ�ԣ����ĸ�������ʾ����ĵ�ַ
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
