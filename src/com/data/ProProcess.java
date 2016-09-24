package com.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hwpf.model.types.HRESIAbstractType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.db.ExcelFunction;
import com.db.FileFunction;
import com.myClass.M;
import com.myClass.U;

public class ProProcess {

	//ͳ��4��excel�г��ֵĹ�˾�����������������txt
	public static void outputCompanyName() throws IOException{
		//��excel�л�ȡ����
		List<List<String>> lists = new ArrayList<List<String>>();
		for(int i = 2011; i < 2015; i++){
			String fileName = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			lists.addAll(U.getRowsList(fileName, M.EXCELINDEX_CompanyName, M.EXCELINDEX_AssociatedCompany));
		}
		
		//����ȡ�������ݽ�һ������
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(int i = 0; i < lists.size(); i++){
			String company = lists.get(i).get(0);
			String associateCompanys = lists.get(i).get(1);
			//ͳ�����幫˾Ƶ��
			int countCompanys = map.get(company) == null ? 1 : map.get(company)+1;
			map.put(company, countCompanys);
			//ͳ�ƹ�����˾Ƶ��
			associateCompanys = associateCompanys.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
			String[] names = associateCompanys.split("��");
			for(String n : names){
				int countAssociateCompnay = map.get(n) == null ? 1 : map.get(n)+1;
				map.put(n, countAssociateCompnay);
			}
		}
		
		//��map����value�Ӵ�С����
        TreeMap<String, Integer> sorted_map = U.sortMap(map);
        
        FileFunction.writeMap_KV(sorted_map, "E:/work/������˾/txt/companyAndFrequency.txt");//����˾���ͳ���Ƶ�����
        FileFunction.writeMap_K(sorted_map, "E:/work/������˾/txt/companyName.txt");//�������˾��
	}
	
	//ͳ��4��excel�г��ֵĹ�˾���������������ͣ������txt�����ظ�����˾������
	public static Map<String, Integer> outputCompanyType() throws IOException{
		//��excel�л�ȡ����
		List<List<String>> lists = new ArrayList<List<String>>();
		for(int i = 2011; i < 2015; i++){
			String fileName = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			lists.addAll(U.getRowsList(fileName, M.EXCELINDEX_CompanyName, M.EXCELINDEX_StockSymbol));
		}
		
		//����ȡ�������ݽ�һ������
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(int i = 0; i < lists.size(); i++){
			if(U.isA(lists.get(i).get(1)))
				map.put(lists.get(i).get(0), M.COMPANYTYPE_A);
			else
				map.put(lists.get(i).get(0), M.COMPANYTYPE_B);
		}

		FileFunction.writeMap_KV(map, "E:\\work\\������˾\\txt\\companyType.txt");//�����˾����
		return map;
	}
	
	//������˾��ַ�����txt
	public static Map<String, String> outputCompanyAddress() throws IOException{
		//��excel�л�ȡ����
		List<List<String>> lists = new ArrayList<List<String>>();
		for(int i = 2011; i < 2015; i++){
			String fileName = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			lists.addAll(U.getRowsList(fileName, M.EXCELINDEX_CompanyName, M.EXCELINDEX_Address));
		}
		
		//����ȡ�������ݽ�һ������
		Map<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < lists.size(); i++){
			map.put(lists.get(i).get(0), U.getCompanyAddress(lists.get(i).get(1)));
		}
		
		//���
		FileFunction.writeMap_KV(map, "E:\\work\\������˾\\txt\\companyAddress.txt");//�� ��˾��-��ַ �����txt��
		return map;
	}
	
	public static void outputCompanyClassfiedType() throws IOException{
		String typeDescribe = "����ҵ";
		String[] types = {"�����뷿�ز�ҵ��������", "�������۹�������", "����ҵ��������"};
//		String typeDescribe = "����ҵ����";
//		String[] types = {"������ҵ��������", "��Ӫ��ҵ��������", "���ʿعɹ�������"};
//		String typeDescribe = "����������";
//		String[] types = {"�������������--����", "�������������--��Ӫ", "�������������--�ܿ�"};
//		String[] types = {"������������--����", "������������--��Ӫ", "������������--�ܿ�"};
//		String[] types = {"�ʽ�������������--����", "�ʽ�������������--��Ӫ", "�ʽ�������������--�ܿ�"};
		
		//��excel�л�ȡ����
		List<List<String>> lists = new ArrayList<List<String>>();
		for(int i = 2011; i < 2015; i++){
			for(String type : types){
				String fileName = "E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + i + "\\" + typeDescribe + "\\" + i + type + ".xlsx";
				File file = new File(fileName);
				if(!file.exists()){
					U.print(fileName + "������");
					continue;
				}
				//����type�ֶ�
				List<List<String>> tempLists = U.getRowsList(fileName, M.EXCELINDEX_CompanyName, M.EXCELINDEX_AssociatedCompany);
				for(int j = 0; j < tempLists.size(); j++){
					tempLists.get(j).add(type);
				}
				lists.addAll(tempLists);
			}
		}
		
		//��������
		Map<String, String> mapCompanyType = new HashMap<String, String>(); //��¼��˾���б�
		Map<String, String> mapRepeat = new HashMap<String, String>(); //��¼�ظ��Ĺ�˾�б�
		for(int i = 0; i < lists.size(); i++){
			String company = lists.get(i).get(0);
			String asCompnays = lists.get(i).get(1).replaceAll(",", "��"); //2014��excel���и��ʾ�õ���',';
			String type = lists.get(i).get(2);
			//��ȡ���������б�
			String tempCompanys = asCompnays + "��" + company;
			String[] names = tempCompanys.split("��");
			
			//�������й�˾�����д���
			for(String name : names){
				if(mapCompanyType.get(name) == null){ //����ù�˾������map�У���Ϊ�����һ��type
					mapCompanyType.put(name, type);
				}
				else if(!mapCompanyType.get(name).equals(type)){
					if(mapRepeat.get(name) == null) //֮ǰû��¼���������
						mapRepeat.put(name, mapCompanyType.get(name) + "/" + type);
					else if(!mapRepeat.get(name).contains(type)) //֮ǰ�Ѿ���¼���ˣ����ٲ����
						mapRepeat.put(name, mapRepeat.get(name) + "/" + type);
				}
			}
		}
		
		FileFunction.writeMap_KV(mapCompanyType, "E:\\work\\������˾\\txt\\companyType_" + typeDescribe + ".txt");//����˾typeд��txt
		FileFunction.writeMap_KV(mapRepeat, "E:\\work\\������˾\\txt\\repeat_" + typeDescribe + ".txt");//���ظ�typeд��txt
	}
	
	public static void outputCompanyClassfiedType_Year() throws IOException{
		String typeDescribe = "����ҵ";
		String[] types = {"�����뷿�ز�ҵ��������", "�������۹�������", "����ҵ��������"};
//		String typeDescribe = "����ҵ����";
//		String[] types = {"������ҵ��������", "��Ӫ��ҵ��������", "���ʿعɹ�������"};
//		String typeDescribe = "����������";
//		String[] types = {"�������������--����", "�������������--��Ӫ", "�������������--�ܿ�"};
//		String[] types = {"������������--����", "������������--��Ӫ", "������������--�ܿ�"};
//		String[] types = {"�ʽ�������������--����", "�ʽ�������������--��Ӫ", "�ʽ�������������--�ܿ�"};
		for(int year = 2011; year < 2012; year++){
			//��excel�л�ȡ����
			List<List<String>> lists = new ArrayList<List<String>>();
			for(String type : types){
				String fileName = "E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + year + "\\" + typeDescribe + "\\" + year + type + ".xlsx";
				File file = new File(fileName);
				if(!file.exists()){
					U.print(fileName + "������");
					continue;
				}
				//����type�ֶ�
				List<List<String>> tempLists = U.getRowsList(fileName, M.EXCELINDEX_CompanyName, M.EXCELINDEX_AssociatedCompany);
				for(int j = 0; j < tempLists.size(); j++){
					tempLists.get(j).add(type);
				}
				lists.addAll(tempLists);
			}
			
			//��������
			Map<String, String> mapCompanyType = new HashMap<String, String>(); //��¼��˾���б�
			Map<String, String> mapRepeat = new HashMap<String, String>(); //��¼�ظ��Ĺ�˾�б�
			for(int i = 0; i < lists.size(); i++){
				String company = lists.get(i).get(0);
				String asCompnays = lists.get(i).get(1).replaceAll(",", "��"); //2014��excel���и��ʾ�õ���',';
				String type = lists.get(i).get(2);
				//��ȡ���������б�
				String tempCompanys = asCompnays + "��" + company;
				String[] names = tempCompanys.split("��");
				
				//�������й�˾�����д���
				for(String name : names){
					if(mapCompanyType.get(name) == null){ //����ù�˾������map�У���Ϊ�����һ��type
						mapCompanyType.put(name, type);
					}
					else if(!mapCompanyType.get(name).equals(type)){
						if(mapRepeat.get(name) == null) //֮ǰû��¼���������
							mapRepeat.put(name, mapCompanyType.get(name) + "/" + type);
						else if(!mapRepeat.get(name).contains(type)) //֮ǰ�Ѿ���¼���ˣ����ٲ����
							mapRepeat.put(name, mapRepeat.get(name) + "/" + type);
					}
				}
			}
			
			FileFunction.writeMap_KV(mapCompanyType, "E:\\work\\������˾\\txt\\companyType_" + typeDescribe + year + ".txt");//����˾typeд��txt
		}
	}
	
	
	//��������˾д��txt
	public static void outputCompanyAssociate(int outputFormat,  int mode, boolean isOneWay, int threshold) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2011; i < 2012; i++){
			Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//��¼ÿ����˾����Ӧ��id
			Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//��¼ÿ��id����Ӧ�Ĺ�˾
			int index = 0;//�±��0��ʼ
			byte[][] matrix = new byte[40000][40000];
			
			//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
			String fileName = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			U.print("��ʼ��ȡ:" + fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet(fileName, j);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					if(mode == M.MODE_ONLYA){//��A��ģʽ��
						HSSFCell tempCell = sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol);
						String stockSymbol = U.getCellStringValue(tempCell).trim().replaceAll(" ", "");
						if(!U.isA(stockSymbol))
							continue;
					}
					//���ʹ�˾��
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					if(U.needContinue(name)) continue;//ȥ�����ؼ�������Ա���������¡����ո������
					if(mapCompanyId.get(name) == null){//����ù�˾������map�У���Ϊ�����һ��id
						mapCompanyId.put(name, index);
						mapIdCompany.put(index, name);//ͬʱΪ��id��Ӧ��company
						index++;
					}
					//���ʹ�����˾
					cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
					
					asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
					String[] names = asName.split("��");
					for(String n : names){
						if(U.needContinue(n)) continue;//ȥ�����ؼ�������Ա���������¡����ո������
						if(mapCompanyId.get(n) == null){//����ù�˾������map�У���Ϊ�����һ���±�
							mapCompanyId.put(n, index);
							mapIdCompany.put(index, n);//ͬʱΪ��id��Ӧ��company
							index++;
						}
						//���Ƶ��������幫˾ָ�������˾
						matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] = 1;
						if(!isOneWay)//���Ҫ��˫���ͷ����˫��+1
							matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] = 1;
					}
				}
			}
			U.print("�ļ���ȡ��������ʼд��txt");
			
			//��ȡmatrix��ֻѡȡ������ֵ�Ĺ�˾id��Ŀǰ��������˫���ͷ��
			List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapCompanyId.size(), threshold);
			
			//��������˾д��txt(���ҷ��ڱ��ˣ��ٸ���һ��matrix�ڴ������)
			if(outputFormat == M.OUTPUTFORMAT_NETWeight){
				String address = "E:/work/������˾/txt/nettxt_asCompany" + i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net";
				FileFunction.writeNet_Weight(idList, mapIdCompany, matrix, address);
			}
			else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){
				Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\������˾\\txt\\companyType.txt");
				String address = "E:/work/������˾/txt/cpType_asCompany" + i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net";
				FileFunction.writeNet_Color(idList, mapIdCompany, matrix, address, M.COLOR_COMPANYTYPE, map);
			}
			else if(outputFormat == M.OUTPUTFORMAT_ADDRESS){
				Map<String, String> map = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyAddress.txt");
				String address = "E:/work/������˾/txt/cpAddress_asCompany" + i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net";
				FileFunction.writeNet_Color(idList, mapIdCompany, matrix, address, M.COLOR_ADDRESS, map);
			}
			else if(outputFormat == M.OUTPUTFORMAT_STARCOMPANY){
				String star = "�����˿��˷�չ�ɷ����޹�˾";
				String address = "E:/work/������˾/txt/StarCompany" + star + i + ".net";
				Map<String, String> map = new HashMap<>();
				map.put("star", star);
				FileFunction.writeNet_Color(idList, mapIdCompany, matrix, address, M.COLOR_STARCOMPANY, map);
			}
			U.print(i + "��д�����");
		}
		U.print("done");
	}
	
	//�������ͷֵĹ�����˾д��txt
	public static void outputByClassification(int outputFormat, int threshold) throws IOException{
		for(int year = 2011; year < 2012; year++){
			File file0 = new File("E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + year);
			String[] fileList0 = file0.list();
			for(String fileName : fileList0){
				File file1 = new File("E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + year + "\\" + fileName);
				String[] fileList1 = file1.list();
				for(String excelName :fileList1){//���ڶ�ȡ��excel�ļ���..
					//���ݴ洢׼��
					U.print("��ʼ��ȡ" + excelName);
					Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//��¼ÿ��id����Ӧ�Ĺ�˾
					Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//��¼ÿ����˾����Ӧ��id
					byte[][] matrix = new byte[25265][25265];//���25265����˾��2014�꣩������ô��ľ���ռ��㹻��
					String excelAddress = "E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + year + "\\" + fileName + "\\" + excelName;
					U.getMatrix(matrix, mapIdCompany, mapCompanyId, excelAddress);//��ȡ�������
					
					//��ȡmatrix��ֻѡȡ������ֵ�Ĺ�˾id
					List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapCompanyId.size(), threshold);
					
					//���.net�ļ�
					String temp = "E:\\work\\������˾\\txt\\˫��ͼ_����ֵ\\" + year + "\\" + fileName + "\\" + excelName;
					if(outputFormat == M.OUTPUTFORMAT_NETWeight){//�������
						String address = temp.substring(0, temp.length()-4) + "net";
						FileFunction.writeNet_Weight(idList, mapIdCompany, matrix, address);
					}
					else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){//���A����ɫ
						Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\������˾\\txt\\companyType.txt");
						String address = temp.substring(0, temp.length()-5) + "colorA.net";
						FileFunction.writeNet_Color(idList, mapIdCompany, matrix, address, M.COLOR_COMPANYTYPE, map);
					}
				}
			}
		}
		U.print("done");
	}
	
	//�����ϵ��ֵĹ�˾��ϵ��
	public static void outputByStrain(int outputFormat, int threshold) throws IOException{
			File file = new File("E:\\work\\������˾\\ԭʼ����\\ϵ���");
			String[] fileList = file.list();
			for(String excelName :fileList){
				//���ݴ洢׼��
				U.print("��ʼ��ȡ" + excelName);
				Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//��¼ÿ����˾����Ӧ��id
				Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//��¼ÿ��id����Ӧ�Ĺ�˾
				byte[][] matrix = new byte[1000][1000];
				String excelAddress = "E:\\work\\������˾\\ԭʼ����\\ϵ���\\" + excelName;
				U.getMatrix(matrix, mapIdCompany, mapCompanyId, excelAddress);//��ȡ�������
					
				//��ȡmatrix��ֻѡȡ������ֵ�Ĺ�˾id
				List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapCompanyId.size(), threshold);
					
				//���.net�ļ�
				String temp = "E:\\work\\������˾\\txt\\ϵ��\\˫��ͼ\\" + excelName;
				if(outputFormat == M.OUTPUTFORMAT_NETWeight){//�������
					String address = temp.substring(0, temp.length()-4) + "net";
					FileFunction.writeNet_Weight(idList, mapIdCompany, matrix, address);
				}
				else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){//���A����ɫ
					Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\������˾\\txt\\companyType.txt");
					String address = temp.substring(0, temp.length()-5) + "colorA.net";
					FileFunction.writeNet_Color(idList, mapIdCompany, matrix, address, M.COLOR_COMPANYTYPE, map);
				}
			}
		U.print("done");
	}
	
	//���ÿһ���������ϵ���������������ʽ�������
	public static void outputTransactionType(String type) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2011; i < 2015; i++){
			Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//��¼ÿ����˾����Ӧ��id
			Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//��¼ÿ��id����Ӧ�Ĺ�˾
			int index = 0;//�±��0��ʼ
			byte[][] matrix = new byte[32767][32767];//UCINET���֧����ô�࣬������ô����Ҫ��������
			
			//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
			String fileName = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			U.print("��ʼ��ȡ:" + fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet(fileName, j);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//�ж��Ƿ�����ĳ�ֽ�������
					boolean yesPPG = false;//���yes�����ʾ�Ǹ����͹�ϵ������д��
					String transcationType = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_TransactoinType));
					transcationType = transcationType.substring(0, transcationType.length()-2);
					if(type.equals(M.TransactionType_Secured))
						if(transcationType.equals("1071") || transcationType.equals("1072"))
							yesPPG = true;
					if(type.equals(M.TransactionType_Purchase))
						if(transcationType.equals("1011") || transcationType.equals("1012"))
							yesPPG = true;
					if(type.equals(M.TransactionType_Capital))
						if(transcationType.equals("1061") || transcationType.equals("1062"))
							yesPPG = true;
					//���ʹ�˾��
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					if(U.needContinue(name)) continue;//ȥ�����ؼ�������Ա���������¡����ո������
					if(mapCompanyId.get(name) == null){//����ù�˾������map�У���Ϊ�����һ��id
						mapCompanyId.put(name, index);
						mapIdCompany.put(index, name);//ͬʱΪ��id��Ӧ��company
						index++;
					}
					//���ʹ�����˾
					cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
					
					asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
					String[] names = asName.split("��");
					for(String n : names){
						if(U.needContinue(n)) continue;//ȥ�����ؼ�������Ա���������¡����ո������
						if(mapCompanyId.get(n) == null){//����ù�˾������map�У���Ϊ�����һ���±�
							mapCompanyId.put(n, index);
							mapIdCompany.put(index, n);//ͬʱΪ��id��Ӧ��company
							index++;
						}
						if(yesPPG){
							matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] = 1;
							matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] = 1;
						}
					}
				}
			}
			U.print("�ļ���ȡ��������ʼд��txt");
			
			//��ȡmatrix
			List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapCompanyId.size(), 0);
			
			//д��.net
			String address = "E:/work/������˾/txt/TransactionType_" + type + "_" + i + ".net";
			FileFunction.writeNet_Weight(idList, mapIdCompany, matrix, address);
		}
		U.print("done");
	}
	
	
	
	
	
	
	
	//����ṹ���������Է�����txt
	public static void outputCentrality(String txtName) throws IOException{
		FileFunction.writeCentrality(txtName);
	}
	//����ṹ���Ľṹ��������txt
	public static void outputStructualHoles(String txtName) throws IOException{
		FileFunction.writeStructualHoles(txtName);
	}
	
	//�����˾����
	public static void outputPartition(String classify, int year) throws NumberFormatException, IOException{
		List<String> cpList = FileFunction.readCompanyName("E:\\work\\������˾\\txt\\nettxt_asCompany" + year + "_false_1_10.net");
		String address = "E:\\work\\������˾\\txt\\partition_" + classify + "_" + year + ".txt";
		if(classify.equals(M.Classify_EquityOwnership)){
			Map<String, String> mapCompanyClassify = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyType_����ҵ����.txt");
			Map<String, Integer> mapClassifyType = new HashMap<>();
			mapClassifyType.put("������ҵ��������", 0);
			mapClassifyType.put("��Ӫ��ҵ��������", 1);
			mapClassifyType.put("���ʿعɹ�������", 2);
			FileFunction.writePartition(cpList, mapCompanyClassify, mapClassifyType, address);
		}
		else if(classify.equals(M.Classify_Industry)){
			Map<String, String> mapCompanyClassify = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyType_����ҵ.txt");
			Map<String, Integer> mapClassifyType = new HashMap<>();
			mapClassifyType.put("�����뷿�ز�ҵ��������", 0);
			mapClassifyType.put("����ҵ��������", 1);
			mapClassifyType.put("�������۹�������", 2);
			FileFunction.writePartition(cpList, mapCompanyClassify, mapClassifyType, address);
		}
		else if(classify.equals(M.Classify_TransactionType)){
			Map<String, String> mapCompanyClassify = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyType_����������.txt");
			Map<String, Integer> mapClassifyType = new HashMap<>();
			mapClassifyType.put("�������������--����", 0);
			mapClassifyType.put("��Ӫ��ҵ��������", 1);
			mapClassifyType.put("���ʿعɹ�������", 2);
			FileFunction.writePartition(cpList, mapCompanyClassify, mapClassifyType, address);
		}
	}
	
}
