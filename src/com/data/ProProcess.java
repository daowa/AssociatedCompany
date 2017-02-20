package com.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.db.ExcelFunction;
import com.db.FileFunction;
import com.db.WordFunction;
import com.myClass.M;
import com.myClass.U;
import com.mysql.fabric.xmlrpc.base.Array;

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
		for(int i = 2011; i < 2016; i++){
			String fileName = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			lists.addAll(U.getRowsList(fileName, M.EXCELINDEX_CompanyName, M.EXCELINDEX_StockSymbol, M.EXCELINDEX_AssociatedCompany));
		}
		
		//����ȡ�������ݽ�һ������
		Map<String, Integer> map = new HashMap<String, Integer>();
		//�������й�˾
		for(int i = 0; i < lists.size(); i++){
			if(U.isA(lists.get(i).get(1)))
				map.put(lists.get(i).get(0), M.COMPANYTYPE_A);
			else
				map.put(lists.get(i).get(0), M.COMPANYTYPE_B);
		}
		//���й�˾�������Ŵ���������׹�˾����֤���й�˾��������ȷ
		for(int i = 0; i < lists.size(); i++){
			//����������׹�˾
			String asCompnays = lists.get(i).get(2).replaceAll(",", "��"); //2014��excel���и��ʾ�õ���',';
			String[] names = asCompnays.split("��");
			for(String name : names){
				if(map.get(name) == null)//mapû�б��棬˵������A�ɻ�B�ɵ����й�˾
					map.put(name, M.COMPANYTYPE_NOIPO);
			}
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
	
	public static void outputCompanyClassfiedType(String classify) throws IOException{
		int excelIndex = -1;
		if(classify.equals(M.Classify_EquityOwnership))
			excelIndex = M.EXCELINDEX_EquityOwnership;
		else if(classify.equals(M.Classify_TransactionType))
			excelIndex = M.EXCELINDEX_TransactoinType;
		else if(classify.equals(M.Classify_Industry))
			excelIndex = M.EXCELINDEX_Industry;
			
		
		//��excel�л�ȡ����
		List<List<String>> lists = new ArrayList<List<String>>();
		for(int i = 2015; i < 2016; i++){
			String fileName = "E:\\work\\������˾\\ԭʼ����\\" + i + ".xls";
			lists.addAll(U.getRowsList(fileName, M.EXCELINDEX_CompanyName, M.EXCELINDEX_AssociatedCompany, excelIndex));
		}
		
		//��������
		Map<String, String> mapCompanyType = new HashMap<String, String>(); //��¼��˾���б�
		Map<String, String> mapRepeat = new HashMap<String, String>(); //��¼�ظ��Ĺ�˾�б�
		for(int i = 0; i < lists.size(); i++){
			String company = lists.get(i).get(0);
			String asCompnays = lists.get(i).get(1).replaceAll(",", "��"); //2014��excel���и��ʾ�õ���',';
			String typeValue = lists.get(i).get(2);
			
			String type = "";
			if(classify.equals(M.Classify_EquityOwnership)){
				if(U.checkTypeValue(typeValue, M.Type_EquityOwnershipNation))
					type = "����";
				else if(U.checkTypeValue(typeValue, M.Type_EquityOwnershipPrivate))
					type = "��Ӫ";
				else if(U.checkTypeValue(typeValue, M.Type_EquityOwnershipForeign))
					type = "����";
				else 
					type = "��������";
			}
			else if(classify.equals(M.Classify_TransactionType)){
				if(U.checkTypeValue(typeValue, M.Type_TransactionPurchase))
					type = "����";
				else if(U.checkTypeValue(typeValue, M.Type_TransactionSecured))
					type = "����";
				else if(U.checkTypeValue(typeValue, M.Type_TransactionCapital))
					type = "�ʽ�����";
				else
					type = "������������";
			}
			
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
		
		FileFunction.writeMap_KV(mapCompanyType, "E:\\work\\������˾\\txt\\companyType_" + classify + ".txt");//����˾typeд��txt
		FileFunction.writeMap_KV(mapRepeat, "E:\\work\\������˾\\txt\\repeat_" + classify + ".txt");//���ظ�typeд��txt
	}
	
	public static void outputCompanyClassfiedType_Year() throws IOException{
//		String typeDescribe = "����ҵ";
//		String[] types = {"�����뷿�ز�ҵ��������", "�������۹�������", "����ҵ��������"};
		String typeDescribe = "����ҵ����";
		String[] types = {"������ҵ��������", "��Ӫ��ҵ��������", "���ʿعɹ�������"};
//		String typeDescribe = "����������";
//		String[] types = {"�������������--����", "�������������--��Ӫ", "�������������--�ܿ�"};
//		String[] types = {"������������--����", "������������--��Ӫ", "������������--�ܿ�"};
//		String[] types = {"�ʽ�������������--����", "�ʽ�������������--��Ӫ", "�ʽ�������������--�ܿ�"};
		for(int year = 2015; year < 2016; year++){
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
	public static void outputCompanyAssociate(int outputFormat,  int mode, int threshold) throws IOException{
		for(int i = 2015; i < 2016; i++){
			//����matrix
			String path = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//��¼ÿ����˾����Ӧ��id
			Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//��¼ÿ��id����Ӧ�Ĺ�˾
			byte[][] matrix = new byte[40000][40000];
			U.getMatrixHSSF(matrix, mapIdCompany, mapCompanyId, path, mode);//��ȡһ��excel�������й�˾�����Ĺ�ϵд�����
			U.print("�ļ���ȡ��������ʼд��txt");
			
			//��ȡmatrix��ֻѡȡ������ֵ�Ĺ�˾id��Ŀǰ��������˫���ͷ��
			List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapCompanyId.size(), threshold, false);
			
			//��������˾д��txt(���ҷ��ڱ��ˣ��ٸ���һ��matrix�ڴ������)
			if(outputFormat == M.OUTPUTFORMAT_NETSimple){
				String address = "E:/work/������˾/txt/NetSimple" + i + "_" + threshold + "_" + mode + ".net";
				FileFunction.writeNet_Simple(false, idList, mapIdCompany, matrix, address);
			}
			if(outputFormat == M.OUTPUTFORMAT_NETWeight){
				String address = "E:/work/������˾/txt/NetWeight" + i + "_" + threshold + "_" + mode + ".net";
				FileFunction.writeNet_Weight(idList, mapIdCompany, matrix, address);
			}
			else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){
				Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\������˾\\txt\\companyType.txt");
				String address = "E:/work/������˾/txt/NetCompanyType" + i + "_" + threshold + "_" + mode + ".net";
				FileFunction.writeNet_Color(idList, mapIdCompany, matrix, address, M.COLOR_COMPANYTYPE, map);
			}
			else if(outputFormat == M.OUTPUTFORMAT_ADDRESS){
				Map<String, String> map = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyAddress.txt");
				String address = "E:/work/������˾/txt/NetAddress" + i + "_" + threshold + "_" + mode + ".net";
				FileFunction.writeNet_Color(idList, mapIdCompany, matrix, address, M.COLOR_ADDRESS, map);
			}
			else if(outputFormat == M.OUTPUTFORMAT_STARCOMPANY){
				String star = "�����˿��˷�չ�ɷ����޹�˾";
				String address = "E:/work/������˾/txt/NetStarCompany" + star + i + ".net";
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
					List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapCompanyId.size(), threshold,false);
					
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
				List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapCompanyId.size(), threshold, false);
					
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
	public static void outputByType(int mode, int threshold, int lineThreshold, boolean direct, String... types) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		List<String> listYangQi = new ArrayList<>();
		List<String> listSubNet = new ArrayList<>();
		List<String> listSubNetSymbol = new ArrayList<>();
		if(types[0].equals(M.Type_EquityOwnershipYangQi))
			listYangQi = U.getYangQiStockSymbol(WordFunction.getRowList("E:\\work\\������˾\\����\\1103\\�������й�˾����.docx"));
		if(types[0].equals(M.Type_EquityOwnershipSubNet))
			listSubNet = FileFunction.readFile("E:\\work\\������˾\\txt\\����\\listSubNet.txt");
		if(types[0].equals(M.Type_EquityOwnerShipSubNet_Symbol))
			listSubNetSymbol = FileFunction.readFile("E:\\work\\������˾\\txt\\����\\listSubNetSymbol.txt");
		
		for(int i = 2015; i < 2016; i++){
			Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//��¼ÿ����˾����Ӧ��id
			Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//��¼ÿ��id����Ӧ�Ĺ�˾
			int index = 0;//�±��0��ʼ
			byte[][] matrix = new byte[20000][20000];
			//�����Ȩֵ���½�һ��matrix�洢Ȩֵ���ڴ�ռ䲻����ֻ�ܷ�10000��
			int[][] matrixWeight = new int[20000][20000];
			
			//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
			String fileName = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			U.print("��ʼ��ȡ:" + fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet_HSSF(fileName, j);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//�ж��Ƿ�����ĳ�ֽ�������
					List<Integer> yesPPG = new ArrayList<>();//��¼�Ƿ�����type�ı�׼
					String typeValue = "";
					for(String type : types){
						if(type.contains("��ҵ����")){
							typeValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_EquityOwnership));
							if(U.checkTypeValue(typeValue, type)) {
								yesPPG.add(10);
							}
						}
						if(type.contains("����")){
							typeValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol));
							if(U.checkTypeValue(typeValue, type, listYangQi)){
								yesPPG.add(10);
							}
						}
						if(type.equals(M.Type_EquityOwnershipSubNet)){
							typeValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName));
							if(U.checkTypeValue(typeValue, type, listYangQi, listSubNet)){
								yesPPG.add(10);
							}
						}
						if(type.equals(M.Type_EquityOwnerShipSubNet_Symbol)){
							typeValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol));
							if(U.checkTypeValue(typeValue, type, listYangQi, listSubNet, listSubNetSymbol)){
								yesPPG.add(10);
							}
						}
						if(type.contains("��������")){
							typeValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_TransactoinType));
							if(U.checkTypeValue(typeValue, type))
								yesPPG.add(1);
						}
						if(type.contains("��ҵ����")){
							typeValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_Industry));
							if(U.checkTypeValue(typeValue, type)){
								yesPPG.add(1);
							}
						}
					}
					
					//�ڡ���Ӫ-�����������У��Ȱѷǹ�Ӫ��ȥ��
					//tpyes[0]��ʾ��Ӫ������Ӫ
					//���ܻ���������ܲ���Ӱ��
					String tempValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_EquityOwnership)).trim();
					if(types[0].equals(M.Type_EquityOwnershipYangQi)) tempValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol));
					if(types[0].equals(M.Type_EquityOwnershipSubNet)) tempValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName));
					if(types[0].equals(M.Type_EquityOwnerShipSubNet_Symbol)) tempValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol));
					if(!U.checkTypeValue(tempValue, types[0], listYangQi, listSubNet, listSubNetSymbol)) continue;
					
					//�����Ͷ�λ���������ͣ��ɻ�ȡ���׷���
					typeValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_TransactoinType));
					
					//��ȡ���׽���λ����Ԫ
					String sAmount = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_Amount));
					String currency = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_Currency));
					int amount = 0;
					if(sAmount.length() > 4 && !sAmount.contains("-"))//ȡ����Ԫ
						amount = Integer.parseInt(sAmount.substring(0, sAmount.length()-4));
					amount = U.getRMB(amount, currency);
					if(amount < lineThreshold) continue;
					
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
						if(mode == M.MODETYPE_ONLYSELECTED && yesPPG.size() == types.length){//��������������ͨ�����硰��Ӫ-������
							if(!direct){
								matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] += 1;
								matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] += 1;
							}
							else{
								//�������磬�硰����-�������ĵ�������
								//types[0]�ǹ��л�����Ӫ��types[1]�ǹ�����������
								if(U.directFromListCompany(typeValue))
									matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] += 1;
								else
									matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] += 1;
							}
						}
						else if((mode == M.MODETYPE_ONLYSELECTED_WEIGHT || mode == M.MODETYPE_ONLYSELECTED_WEIGHTCSV)
								&& yesPPG.size() == types.length){//��������������ͨ�����硰��Ӫ-������
							if(!direct){
								matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] += 1;
								matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] += 1;
								matrixWeight[mapCompanyId.get(name)][mapCompanyId.get(n)] += amount/names.length;
								matrixWeight[mapCompanyId.get(n)][mapCompanyId.get(name)] += amount/names.length;
							}
							else{
								//�������磬�硰����-�������ĵ�������
								//types[0]�ǹ��л�����Ӫ��types[0]�ǹ�����������
								if(U.directFromListCompany(typeValue)){
									matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] += 1;
									matrixWeight[mapCompanyId.get(name)][mapCompanyId.get(n)] += amount/names.length;
								}
								else{
									matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] += 1;
									matrixWeight[mapCompanyId.get(n)][mapCompanyId.get(name)] += amount/names.length;
								}
							}
						}
						else if(mode == M.MODETYPE_ALLSELECTED && U.getSumList(yesPPG) >= (10 + types.length-2)){//���ڡ���Ӫ-��Ӫ�����ͣ�ֻҪ��һ��10���У����ڡ���Ӫ-��Ӫ-����������Ҫ��11
							matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] += 1;
							matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] += 1;
						}
					}
				}
				
			}
			U.print("idSize:" + mapIdCompany.size());
			U.print("�ļ���ȡ��������ʼд��txt");
			
			//��ȡmatrix
			//����ǵ���ͼ�����һ��������Ҫ��true
			List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapCompanyId.size(), threshold, direct);
			U.print(idList.size());
			
			//д��.net
			String outputTypes = "";
			for(String type : types)
				outputTypes += type + "&";
			String sDirect = "";
			if(direct) sDirect = "����";
			String isWeight = "";
			if(mode == M.MODETYPE_ONLYSELECTED_WEIGHT) isWeight = "#Ȩֵ";
			//CSV��ʽ
			if(mode == M.MODETYPE_ONLYSELECTED_WEIGHTCSV){
				String pathNode = "E:/work/������˾/txt/����/csvnode_" + outputTypes.substring(0, outputTypes.length()-1) + "_" + i + "_��ֵ" + threshold + "_" + lineThreshold + ".csv";
				String pathLine = "E:/work/������˾/txt/����/csvline_" + outputTypes.substring(0, outputTypes.length()-1) + "_" + i + "_��ֵ" + threshold + "_" + lineThreshold + ".csv";
				FileFunction.writeCSV_Node(idList, mapIdCompany, matrixWeight, pathNode);
				FileFunction.writeCSV_Line(idList, matrixWeight, pathLine);
			}
			//NET��ʽ
			String address = "E:/work/������˾/txt/����/" + outputTypes.substring(0, outputTypes.length()-1) + "_" + i + "_��ֵ" + threshold + sDirect + isWeight + "_" + lineThreshold + ".net";
			if(mode == M.MODETYPE_ONLYSELECTED_WEIGHT)
				FileFunction.writeNet_AmountWeight(direct, idList, mapIdCompany, matrix, matrixWeight, address);//Ŀǰʹ����Ȩֵ������
			else
				FileFunction.writeNet_Simple(direct, idList, mapIdCompany, matrix, address);//Ŀǰʹ����Ȩֵ������
		}
		U.print("done");
	}
	
	
	//��������������ױ���һ��������ʾ��˾�����ͣ��ڶ���������ʾ���׵�����
	public static void outputDistrictNet(int mode, int thresholdNode, int thresholdLine, String typeLine, String... typeNodes) throws IOException{
		//��ȡ������-���С��б���д��map<���У�����>
		Map<String, String> mapCityDistrict = new HashMap<>();
		List<String> listDistrict = new ArrayList<>();
		double[][] matrix = new double[200][200];
		//����һ��ͨ����ȡ�ļ�д��
//		List<String> listDistrictCity = FileFunction.readFile("E:\\work\\������˾\\txt\\����\\����_����.txt");
//		for(String line : listDistrictCity){
//			String[] ss = line.split(" ");
//			listDistrict.add(ss[0]);
//			for(int i = 1; i < ss.length; i++){
//				mapCityDistrict.put(ss[i], ss[0]);
//			}
//		}
		//����������ȡ���ý�ĳ����б�
		HSSFSheet sheetZYY = ExcelFunction.getSheet_HSSF("E:\\work\\���ý����ݷ�������\\�Դ�Ϊ׼.xls", 0);
		for(int i = 1; i < sheetZYY.getLastRowNum(); i++){
			String district = U.cleanDistrict(U.getCellStringValue(sheetZYY.getRow(i).getCell(1)));
			String city = U.cleanCity(U.getCellStringValue(sheetZYY.getRow(i).getCell(2)));
			if(!listDistrict.contains(district)) listDistrict.add(district);
			mapCityDistrict.put(city, district);
		}
	
		for(int year = 2015; year < 2016; year++){
			//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
			String fileName = "E:/work/������˾/ԭʼ����/" + year + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			U.print("��ʼ��ȡ:" + fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet_HSSF(fileName, j);
				for(int k = 1 ; k < sheet.getLastRowNum(); k++){
					//���ݹ�˾����ɸѡ��
					int countMatch = 0;//�������������Ĵ�������Ҫ��������typenodes��ͨ��
					for(String typeNode : typeNodes){
						if(typeNode.contains("��ҵ����")){
							String typeValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_EquityOwnership));
							if(U.checkTypeValue(typeValue, typeNode)) {
								countMatch ++;
							}
						}
						if(typeNode.contains("����")){
							List<String> listYangQi = U.getYangQiStockSymbol(WordFunction.getRowList("E:\\work\\������˾\\����\\1103\\�������й�˾����.docx"));
							String typeValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol));
							if(U.checkTypeValue(typeValue, typeNode, listYangQi)){
								countMatch ++;
							}
						}
						if(typeNode.contains("������")){
							List<String> listYangQi = U.getYangQiStockSymbol(WordFunction.getRowList("E:\\work\\������˾\\����\\1103\\�������й�˾����.docx"));
							List<String> listSubNet = FileFunction.readFile("E:\\work\\������˾\\txt\\����\\listSubNet.txt");
							String typeValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName));
							if(U.checkTypeValue(typeValue, typeNode, listYangQi, listSubNet)){
								countMatch ++;
							}
						}
						if(typeNode.contains("��ҵ����")){
							String typeValue = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_Industry));
							if(U.checkTypeValue(typeValue, typeNode)){
								countMatch ++;
							}
						}
					}
					if(countMatch != typeNodes.length) continue;
					
					//���ݽ�������ɸѡ��
					String typeTransaction = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_TransactoinType));
					if(!U.checkTypeValue(typeTransaction, typeLine)) continue;
					
					//���ݳ�����Ϣ��д�������ߺ���������¼
					//��ȡ���׽���λ����Ԫ
					String sAmount = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_Amount));
					String currency = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_Currency));
					int amount = 0;
					if(sAmount.length() > 4 && !sAmount.contains("-"))//ȡ����Ԫ
						amount = Integer.parseInt(sAmount.substring(0, sAmount.length()-4));
					amount = U.getRMB(amount, currency);
					
					
					//���ʹ�˾��
					String name = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName)).trim().replaceAll(" ", "");
					if(U.needContinue(name)) continue;//ȥ�����ؼ�������Ա���������¡����ո������
					String address = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_Address));
					String districtListCompany = U.getDistrict(address, listDistrict, mapCityDistrict);
					if(districtListCompany.equals("")) continue;//�޷���ȡ�����ģ�����
					//���ʹ�����˾
					String asName = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany)).trim().replaceAll(" ", "");
					asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
					String[] names = asName.split("��");
					for(String n : names){
						if(U.needContinue(n)) continue;//ȥ�����ؼ�������Ա���������¡����ո������
						String districtAssociatedCompany = U.getDistrict(n, listDistrict, mapCityDistrict);
						if(districtAssociatedCompany.equals("")) continue;//�޷���ȡ�����ģ�����
						//���ģʽ��ֻ���ǵ����佻�ף������������ڲ��Ĺ�������
						if(mode == M.MODEDISTRICT_NOINNER && districtListCompany.equals(districtAssociatedCompany)) continue;
						//д������ԡ��ڡ���Ϊ��λ
						if(U.directFromListCompany(typeTransaction)){
							matrix[listDistrict.indexOf(districtListCompany)][listDistrict.indexOf(districtAssociatedCompany)] += (double)amount/names.length/10000;
						}
						else{
							matrix[listDistrict.indexOf(districtAssociatedCompany)][listDistrict.indexOf(districtListCompany)] += (double)amount/names.length/10000;
						}
					}
				}
			}
			
			//������ֵɸѡ��
			for(int i = 0; i < matrix.length; i++){
				for(int j = 0; j < matrix.length; j++){
					if(matrix[i][j] < thresholdLine)
						matrix[i][j] = 0;
				}
			}
			
			//������ֵɸѡ�㣬�õ����list
			List<Integer> idList = new ArrayList<>();//��Ÿ�����ֵ��id
			for(int i = 0; i < matrix.length; i++){
				int frequency = 0;
				for(int j = 0; j < matrix.length; j++){
					//ͳ�Ƹù�˾���ֵ�Ƶ��
					if(matrix[i][j] > 0)
						frequency += 1;
				}
				if(frequency >= thresholdNode)
					idList.add(i);
			}
			
			//д��.net�ļ�
			U.print("�ļ���ȡ��������ʼд��txt");
			String sMode = "";
			if(mode == M.MODEDISTRICT_INNER) 
				sMode = "�����ڲ�#";
			else 
				sMode = "�������ڲ�#";
			String typeNode = "";
			for(String type : typeNodes)
				typeNode += type;
			//CSV��ʽ
			String pathNode = "E:\\work\\������˾\\txt\\����\\csvnode_" + sMode + typeNode + "_" + year + "_��ֵ" + thresholdNode + "_" + thresholdLine + ".csv";
			String pathLine = "E:\\work\\������˾\\txt\\����\\csvline_" + sMode + typeNode + "_" + year + "_��ֵ" + thresholdNode + "_" + thresholdLine + ".csv";
			FileFunction.writeCSV_Node(idList, listDistrict, matrix, pathNode);
			FileFunction.writeCSV_Line(idList, matrix, pathLine);
//			else {
//				String path = "E:\\work\\������˾\\txt\\����\\" + year + "_" + sMode + typeNode + "_" + typeLine + "#����ֵ" + thresholdNode + "_����ֵ" + thresholdLine + ".net";
//				FileFunction.writeNet_AmountWeight(listDistrict, idList, matrix, path);
//			}
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
	public static void outputPartition(String classify) throws NumberFormatException, IOException{
		if(classify.equals(M.Classify_EquityOwnership)){
			List<String> cpList = FileFunction.readCompanyNameFromNet("E:\\work\\������˾\\txt\\nettxt_asCompany2011_false_1_10.net");
			String address = "E:\\work\\������˾\\txt\\partition_" + classify + "_2011.txt";
			Map<String, String> mapCompanyClassify = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyType_" + M.Classify_EquityOwnership + ".txt");
			Map<String, Integer> mapClassifyType = new HashMap<>();
			mapClassifyType.put("����", 0);
			mapClassifyType.put("��Ӫ", 1);
			mapClassifyType.put("����", 2);
			mapClassifyType.put("��������", -1);
			FileFunction.writePartition(cpList, mapCompanyClassify, mapClassifyType, address);
		}
		else if(classify.equals(M.Classify_Industry)){
			List<String> cpList = FileFunction.readCompanyNameFromNet("E:\\work\\������˾\\txt\\nettxt_asCompany2011_false_1_10.net");
			String address = "E:\\work\\������˾\\txt\\partition_" + classify + "_2011.txt";
			Map<String, String> mapCompanyClassify = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyType_" + M.Classify_Industry + ".txt");
			Map<String, Integer> mapClassifyType = new HashMap<>();
			mapClassifyType.put("�����뷿�ز�ҵ��������", 0);
			mapClassifyType.put("����ҵ��������", 1);
			mapClassifyType.put("�������۹�������", 2);
			FileFunction.writePartition(cpList, mapCompanyClassify, mapClassifyType, address);
		}
		else if(classify.equals(M.Classify_TransactionType)){
			List<String> cpList = FileFunction.readCompanyNameFromNet("E:\\work\\������˾\\txt\\nettxt_asCompany2011_false_1_10.net");
			String address = "E:\\work\\������˾\\txt\\partition_" + classify + "_2011.txt";
			Map<String, String> mapCompanyClassify = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyType_" + M.Classify_TransactionType + ".txt");
			Map<String, Integer> mapClassifyType = new HashMap<>();
			mapClassifyType.put("����", 0);
			mapClassifyType.put("����", 1);
			mapClassifyType.put("�ʽ�����", 2);
			mapClassifyType.put("������������", -1);
			FileFunction.writePartition(cpList, mapCompanyClassify, mapClassifyType, address);
		}
		else if(classify.equals(M.CLassify_Ownership_Ownership)){
			List<String> cpList = FileFunction.readCompanyNameFromNet("E:\\work\\������˾\\txt\\����\\��ѡ���ڵ�#��ҵ����_����&��ҵ����_��Ӫ_2015.net");
			String address = "E:\\work\\������˾\\txt\\����\\partition_" + classify + "_2015.txt";
			Map<String, String> mapCompanyClassify = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyType_" + M.Classify_EquityOwnership + ".txt");
			Map<String, Integer> mapClassifyType = new HashMap<>();
			mapClassifyType.put("����", 0);
			mapClassifyType.put("��Ӫ", 1);
			mapClassifyType.put("����", 2);
			mapClassifyType.put("��������", 3);
			FileFunction.writePartition(cpList, mapCompanyClassify, mapClassifyType, address);
		}
		else if(classify.equals(M.Classify_ListedCompany)){
			List<String> cpList = FileFunction.readCompanyNameFromNet("E:\\work\\������˾\\txt\\����\\��ҵ����_����&��������_����_2015_��ֵ1����_1.net");
			String address = "E:\\work\\������˾\\txt\\����\\partition_" + classify + "_2015.clu";//clu��pajek�ɶ��ķ����ļ�
			Map<String, String> mapCompanyClassify = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyType.txt");
			Map<String, Integer> mapClassifyType = new HashMap<>();
			mapClassifyType.put("100", 0);
			mapClassifyType.put("101", 1);
			mapClassifyType.put("102", 2);
			FileFunction.writePajekPartition(cpList, mapCompanyClassify, mapClassifyType, address);
		}
	}
	
	
	
	
	
	
	//ɾ����������Ŀ����������¶����ֵ�excel
	public static void onlyVague() throws FileNotFoundException, IOException{
		for(int year = 2015; year < 2016; year++){
			//��ȡexcel���õ���Ҫɾ�����е�index
			String fileName = "E:\\work\\������˾\\txt\\��¶�����\\" + year + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			U.print("��ʼ��ȡ:" + fileName);
			try{
				for(int j = 0; j < sheetNumber; j++){
					HSSFSheet sheet = ExcelFunction.getSheet_HSSF(fileName, j);
					List<Integer> listIndex = new ArrayList<>();
					for(int k = 1 ; k < sheet.getLastRowNum(); k++){
						String asName = U.getCellStringValue(sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany))
								.trim().replaceAll(" ", "").replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
						U.print("������˾:" + k + "/" + asName);
						String[] names = asName.split("��");
						boolean needDelete = true;
						for(String n : names){
							//ֻҪ����һ��ģ���㣬�Ͳ���Ҫɾ��
							if(U.needContinue(n)) needDelete = false;
						}
						if(needDelete) listIndex.add(k);
					}
					ExcelFunction.removeRow(fileName, j, listIndex);
				}
				//����indexɾ����
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}
		U.print("done");
	}
	
}
