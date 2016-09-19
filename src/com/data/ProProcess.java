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
		Map<String, Integer> mapCompany = new HashMap<String, Integer>();
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2011; i < 2015; i++){
			String fileName = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet(fileName, j);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//��¼�ù�˾���ּ���
					int count = 0;
					//������幫˾��
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					count = mapCompany.get(name) == null ? 1 : mapCompany.get(name)+1;
					mapCompany.put(name, count);
					//��ӹ�����˾��
					cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
					asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
					String[] names = asName.split("��");
					for(String n : names){
						count = mapCompany.get(n) == null ? 1 : mapCompany.get(n)+1;
						mapCompany.put(n, count);
					}
				}
			}
		}
		
		//��map����value�Ӵ�С����
        TreeMap<String, Integer> sorted_map = U.sortMap(mapCompany);
        
        FileFunction.writeMap_KV(sorted_map, "E:/work/������˾/txt/companyAndFrequency.txt");//����˾���ͳ���Ƶ�����
        FileFunction.writeMap_K(sorted_map, "E:/work/������˾/txt/companyName.txt");//�������˾��
	}
	
	//ͳ��4��excel�г��ֵĹ�˾���������������ͣ������txt�����ظ�����˾������
	public static Map<String, Integer> outputCompanyType() throws IOException{
		Map<String, Integer> map = new HashMap<String, Integer>();
		HSSFCell cellCompanyName = null;
		
		for(int i = 2011; i < 2015; i++){
			String fileName = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet(fileName, j);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//��ͳ�����幫˾��û�г��������幫˾�еĹ�����˾���Ƿ����й�˾
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					//��ȡ��Ʊ����
					HSSFCell tempCell = sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol);
					String stockSymbol = U.getCellStringValue(tempCell).trim().replaceAll(" ", "");
					if(U.isA(stockSymbol))
						map.put(name, M.COMPANYTYPE_A);
					else
						map.put(name, M.COMPANYTYPE_B);
				}
			}
		}

		FileFunction.writeMap_KV(map, "E:\\work\\������˾\\txt\\companyType.txt");//�����˾����
		return map;
	}
	
	//������˾��ַ�����txt
	public static Map<String, String> outputCompanyAddress() throws IOException{
		Map<String, String> map = new HashMap<String, String>();
		HSSFCell cellCompanyName = null;
		
		for(int i = 2011; i < 2015; i++){
			String fileName = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet(fileName, j);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//��ͳ�����幫˾����Ϊ�����幫˾�е�ַ��Ϣ
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					//��ȡ��ַ
					HSSFCell tempCell = sheet.getRow(k).getCell(M.EXCELINDEX_Address);
					String address = U.getCellStringValue(tempCell).trim().replaceAll(" ", "");
					map.put(name, U.getCompanyAddress(address));
				}
			}
		}
		
		FileFunction.writeMap_KV(map, "E:\\work\\������˾\\txt\\companyAddress.txt");//�� ��˾��-��ַ �����txt��
		return map;
	}
	
	public static void outputCompanyClassfiedType() throws IOException{
//		String typeDescribe = "����ҵ";
//		String[] types = {"�����뷿�ز�ҵ��������", "�������۹�������", "����ҵ��������"};
		String typeDescribe = "����ҵ����";
		String[] types = {"������ҵ��������", "��Ӫ��ҵ��������", "���ʿعɹ�������"};
//		String typeDescribe = "����������";
//		String[] types = {"�������������--����", "�������������--��Ӫ", "�������������--�ܿ�"};
//		String[] types = {"������������--����", "������������--��Ӫ", "������������--�ܿ�"};
//		String[] types = {"�ʽ�������������--����", "�ʽ�������������--��Ӫ", "�ʽ�������������--�ܿ�"};
		//��¼��˾���б�
		Map<String, String> mapCompanyType = new HashMap<String, String>();
		//��¼�ظ��Ĺ�˾�б�
		Map<String, String> mapRepeat = new HashMap<String, String>();
		for(int year = 2011; year <= 2014; year++){
			for(String type : types){
				U.print("��ʼ��ȡ" + year + type);
				//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
				String fileName = "E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + year + "\\" + typeDescribe + "\\" + year + type + ".xlsx";
				File file = new File(fileName);
				if(!file.exists()){
					U.print(fileName + "������");
					continue;
				}
				XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + year + "\\" + typeDescribe + "\\" + year + type + ".xlsx", 0);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//��������˾
					XSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					//��Щexcel�����п���
					if(cellCompanyName == null) break;
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					if(U.needContinue(name)) continue;//ȥ�����ؼ�������Ա���������¡����ո������
					if(mapCompanyType.get(name) == null){//����ù�˾������map�У���Ϊ�����һ��type
						mapCompanyType.put(name, type);
					}
					else if(!mapCompanyType.get(name).equals(type)){
						if(mapRepeat.get(name) == null)//֮ǰû��¼���������
							mapRepeat.put(name, mapCompanyType.get(name) + "/" + type);
						//֮ǰ�Ѿ���¼���ˣ����ٲ����
						else if(!mapRepeat.get(name).contains(type))
							mapRepeat.put(name, mapRepeat.get(name) + "/" + type);
					}
					
					//���ʹ�����˾
					XSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
					asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
					String[] names = asName.split("��");
					for(String n : names){
						if(U.needContinue(n)) continue;//ȥ�������յĹ�˾��(��Ӣ�Ŀո�)
						if(mapCompanyType.get(n) == null){//����ù�˾������map�У���Ϊ�����һ��type
							mapCompanyType.put(n, type);
						}
						else if(!mapCompanyType.get(n).equals(type)){
							if(mapRepeat.get(n) == null)//֮ǰû��¼���������
								mapRepeat.put(n, mapCompanyType.get(n) + "/" + type);
							//֮ǰ�Ѿ���¼���ˣ����ٲ����
							else if(!mapRepeat.get(n).contains(type))
								mapRepeat.put(n, mapRepeat.get(n) + "/" + type);
						}
					}
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
		for(int year = 2011; year <= 2014; year++){
			Map<String, String> mapCompanyType = new HashMap<String, String>();//��¼��˾���б�
			Map<String, String> mapRepeat = new HashMap<String, String>();//��¼�ظ��Ĺ�˾�б�
			for(String type : types){
				U.print("��ʼ��ȡ" + year + type);
				//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
				String fileName = "E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + year + "\\" + typeDescribe + "\\" + year + type + ".xlsx";
				File file = new File(fileName);
				if(!file.exists()){
					U.print(fileName + "������");
					continue;
				}
				XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + year + "\\" + typeDescribe + "\\" + year + type + ".xlsx", 0);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//��������˾
					XSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					//��Щexcel�����п���
					if(cellCompanyName == null) break;
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					if(U.needContinue(name)) continue;//ȥ�����ؼ�������Ա���������¡����ո������
					if(mapCompanyType.get(name) == null){//����ù�˾������map�У���Ϊ�����һ��type
						mapCompanyType.put(name, type);
					}
					else if(!mapCompanyType.get(name).equals(type)){
						if(mapRepeat.get(name) == null)//֮ǰû��¼���������
							mapRepeat.put(name, mapCompanyType.get(name) + "/" + type);
						//֮ǰ�Ѿ���¼���ˣ����ٲ����
						else if(!mapRepeat.get(name).contains(type))
							mapRepeat.put(name, mapRepeat.get(name) + "/" + type);
					}
					
					//���ʹ�����˾
					XSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
					asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
					String[] names = asName.split("��");
					for(String n : names){
						if(U.needContinue(n)) continue;//ȥ�������յĹ�˾��(��Ӣ�Ŀո�)
						if(mapCompanyType.get(n) == null){//����ù�˾������map�У���Ϊ�����һ��type
							mapCompanyType.put(n, type);
						}
						else if(!mapCompanyType.get(n).equals(type)){
							if(mapRepeat.get(n) == null)//֮ǰû��¼���������
								mapRepeat.put(n, mapCompanyType.get(n) + "/" + type);
							//֮ǰ�Ѿ���¼���ˣ����ٲ����
							else if(!mapRepeat.get(n).contains(type))
								mapRepeat.put(n, mapRepeat.get(n) + "/" + type);
						}
					}
				}
			}
			FileFunction.writeMap_KV(mapCompanyType, "E:\\work\\������˾\\txt\\companyType_" + typeDescribe + year + ".txt");//����˾typeд��txt
		}
	}
	
	//��net�ж�ȡ��˾���б�
	private static List<String> readCompanyName(String path) throws NumberFormatException, IOException{
		List<String> list = new ArrayList<String>();
		File file = new File(path);
		InputStreamReader stream = new InputStreamReader(new FileInputStream(file));
		BufferedReader reader = new BufferedReader(stream);
		//��ȡ���ж��ٸ���˾
		String line = reader.readLine();
		int count = Integer.parseInt(line.substring(10, line.length()));
		U.print(count);
		for(int i = 0; i < count; i++){
			line = reader.readLine();
			Pattern p = Pattern.compile("\".*\"");
			Matcher m=p.matcher(line);
			if(m.find())
				list.add(m.group(0).substring(1, m.group(0).length()-1));
		}
		return list;
	}
	
	//��������˾д��txt
	public static void outputCompanyAssociate(int outputFormat,  int mode, boolean isOneWay, int threshold) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2011; i <= 2014; i++){
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
			List<Integer> idList = new ArrayList<>();//��Ÿ�����ֵ��id
			for(int idi = 0; idi < mapCompanyId.size(); idi++){
				int frequency = 0;
				for(int idj = 0; idj < mapCompanyId.size(); idj++){
					//ͳ�Ƹù�˾���ֵ�Ƶ�ʣ�Ŀǰ��������˫���ͷ��
					if(matrix[idi][idj] != 0)
						frequency += matrix[idi][idj];
				}
				if(frequency >= threshold)
					idList.add(idi);
			}
			
			//��������˾д��txt(���ҷ��ڱ��ˣ��ٸ���һ��matrix�ڴ������)
			if(outputFormat == M.OUTPUTFORMAT_DL){
				FileWriter fw = new FileWriter("E:/work/������˾/txt/dl_asCompany"
							+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".txt");
				fw.write("dl" + "\r\n");
				fw.write("n = " + idList.size() + "\r\n");
				fw.write("labels embedded" + "\r\n");
				fw.write("format = fullmatrix" + "\r\n");
				fw.write("data:" + "\r\n");
				String line = null;
				//д��һ�У��ֶ��У�
				line = "";//���line
				for(String key : mapCompanyId.keySet()){
					if(idList.contains(mapCompanyId.get(key)))//��������ֵ�ļ����ӡ����
						line += key + " ";
				}
				line = line.substring(0, line.length()-1);//ɾ�����һ���ո�
				fw.write(line + "\r\n");
				//����д��¼
				for(String key : mapCompanyId.keySet()){
					if(idList.contains(mapCompanyId.get(key))){//��������ֵ�ļ����ӡ����
						U.print("����д�빫˾:" + key + ",idΪ:" + mapCompanyId.get(key));
						line = "";//���line
						line += key + " ";
						for(int fwi = 0; fwi < mapCompanyId.size(); fwi ++){
							if(idList.contains(fwi)){//��������ֵ���м����ӡ����
								line += matrix[mapCompanyId.get(key)][fwi] + " ";
							}
						}
						line = line.substring(0, line.length()-1);//ɾ�����һ���ո�
						fw.write(line + "\r\n");
					}
				}
				fw.close();
			}
			else if(outputFormat == M.OUTPUTFORMAT_NET){
				FileWriter fw = new FileWriter("E:/work/������˾/txt/net_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".txt");
				fw.write("From\tTo\tWeight\r\n");
				for(int fwi = 0; fwi < idList.size(); fwi++){
					U.print("����д�빫˾:" + mapIdCompany.get(idList.get(fwi)) + ",idΪ:" + idList.get(fwi));
					for(int fwj = 0; fwj < idList.size(); fwj++){
						if(matrix[idList.get(fwi)][idList.get(fwj)] == 0) continue;//����޹�����������
						fw.write(idList.get(fwi) + "\t"
								+ idList.get(fwj) + "\t"
								+ matrix[idList.get(fwi)][idList.get(fwj)]/2 + "\r\n");
					}
				}
				fw.close();
			}
			else if(outputFormat == M.OUTPUTFORMAT_NETTXT){
				FileWriter fw = new FileWriter("E:/work/������˾/txt/nettxt_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net");
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
			}
			else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){
				Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\������˾\\txt\\companyType.txt");
				FileWriter fw = new FileWriter("E:/work/������˾/txt/cpType_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net");
				fw.write("*Vertices " + idList.size());
				for(int fwi = 0; fwi < idList.size(); fwi++){
					fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
					String cpName = mapIdCompany.get(idList.get(fwi));
					fw.write((fwi+1) + " \"" + cpName + "\""
							+ " ic " + U.getCompanyTypeColor(map.get(cpName)!=null ? map.get(cpName) : M.COMPANYTYPE_NOIPO));
					U.print("д�빫˾:" + mapIdCompany.get(idList.get(fwi)));
				}
				fw.write("\r\n");
				fw.write("*Edges");
				for(int fwi = 0; fwi < idList.size(); fwi++){
					for(int fwj = 0; fwj < idList.size(); fwj++){
						int weight = matrix[idList.get(fwi)][idList.get(fwj)];
						for(int weightI = 0; weightI < weight; weightI++){
							fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
							fw.write((fwi+1) + " " + (fwj+1));
							U.print((fwi+1) + " " + (fwj+1));
						}
					}
				}
				fw.close();
			}
			else if(outputFormat == M.OUTPUTFORMAT_ADDRESS){
				Map<String, String> map = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyAddress.txt");
				FileWriter fw = new FileWriter("E:/work/������˾/txt/cpAddress_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net");
				fw.write("*Vertices " + idList.size());
				for(int fwi = 0; fwi < idList.size(); fwi++){
					String cpName = mapIdCompany.get(idList.get(fwi));
					fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
					fw.write((fwi+1) + " \"" + cpName + "\""
							+ " ic " + U.getAddressColor(map.get(cpName)));
					U.print("д�빫˾:" + mapIdCompany.get(idList.get(fwi)));
				}
				fw.write("\r\n");
				fw.write("*Edges");
				for(int fwi = 0; fwi < idList.size(); fwi++){
					for(int fwj = 0; fwj < idList.size(); fwj++){
						int weight = matrix[idList.get(fwi)][idList.get(fwj)];
						for(int weightI = 0; weightI < weight; weightI++){
							fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
							fw.write((fwi+1) + " " + (fwj+1));
							U.print((fwi+1) + " " + (fwj+1));
						}
					}
				}
				fw.close();
			}
			else if(outputFormat == M.OUTPUTFORMAT_STARCOMPANY){
				String star = "�����˿��˷�չ�ɷ����޹�˾";
				FileWriter fw = new FileWriter("E:/work/������˾/txt/StarCompany" + star + i + ".net");
				fw.write("*Vertices " + idList.size());
				for(int fwi = 0; fwi < idList.size(); fwi++){
					fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
					String cpName = mapIdCompany.get(idList.get(fwi));
					if(cpName.equals(star))
						fw.write((fwi+1) + " \"" + cpName + "\"" + " ic " + "Red");
					else
						fw.write((fwi+1) + " \"" + cpName + "\"" + " ic " + "Gray");
					U.print("д�빫˾:" + mapIdCompany.get(idList.get(fwi)));
				}
				fw.write("\r\n");
				fw.write("*Edges");
				for(int fwi = 0; fwi < idList.size(); fwi++){
					for(int fwj = 0; fwj < idList.size(); fwj++){
						int weight = matrix[idList.get(fwi)][idList.get(fwj)];
						for(int weightI = 0; weightI < weight; weightI++){
							fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
							fw.write((fwi+1) + " " + (fwj+1));
							U.print((fwi+1) + " " + (fwj+1));
						}
					}
				}
				fw.close();
			}
			U.print(i + "��д�����");
		}
		U.print("done");
	}
	
	//�������ͷֵĹ�����˾д��txt
	public static void outputByClassification(int threshold,int direction, int outputFormat) throws IOException{
		for(int year = 2011; year <= 2014; year++){
			File file0 = new File("E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + year);
			String[] fileList0 = file0.list();
			for(String fileName : fileList0){
				File file1 = new File("E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + year + "\\" + fileName);
				String[] fileList1 = file1.list();
				for(String excelName :fileList1){//���ڶ�ȡ��excel�ļ���..
					//���ݴ洢׼��
					U.print("��ʼ��ȡ" + excelName);
					Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//��¼ÿ����˾����Ӧ��id
					Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//��¼ÿ��id����Ӧ�Ĺ�˾
					int id = 0;//�±��0��ʼ
					byte[][] matrix = new byte[25265][25265];//���25265����˾��2014�꣩������ô��ľ���ռ��㹻��
					
					//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
					XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + year + "\\" + fileName + "\\" + excelName, 0);
					int rowCount = sheet.getLastRowNum();
					for(int k = 1 ; k < rowCount ; k++){
						//���ʹ�˾��
						XSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
						//��Щexcel�����п���
						if(cellCompanyName == null) break;
						String name = U.getCellStringValue(cellCompanyName).trim().replace(" ", "").replaceAll(" ", "");
						if(U.needContinue(name)) continue;//ȥ�������յĹ�˾��(��Ӣ�Ŀո�)
						if(mapCompanyId.get(name) == null){//����ù�˾������map�У���Ϊ�����һ��id
							mapCompanyId.put(name, id);
							mapIdCompany.put(id, name);//ͬʱΪ��id��Ӧ��company
							id++;
						}
						//���ʹ�����˾
						XSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
						String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
						
						asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
						String[] names = asName.split("��");
						for(String n : names){
							if(U.needContinue(n)) continue;//ȥ�������յĹ�˾��(��Ӣ�Ŀո�)
							if(mapCompanyId.get(n) == null){//����ù�˾������map�У���Ϊ�����һ���±�
								mapCompanyId.put(n, id);
								mapIdCompany.put(id, n);//ͬʱΪ��id��Ӧ��company
								id++;
							}
							//���Ƶ��������幫˾ָ�������˾
							matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] = 1;//����û�и��߸�Ȩ
							if(direction == 2)//˫���ͷ�������������Ҫ+1
								matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] = 1;
						}
					}
					
					//��ȡmatrix��ֻѡȡ������ֵ�Ĺ�˾id
					List<Integer> idList = new ArrayList<>();//��Ÿ�����ֵ��id
					for(int idi = 0; idi < mapCompanyId.size(); idi++){
						int frequency = 0;
						for(int idj = 0; idj < mapCompanyId.size(); idj++){
							//ͳ�Ƹù�˾���ֵ�Ƶ��
							if(matrix[idi][idj] != 0)
								frequency += matrix[idi][idj];
							//�����ͷ��ֵ����Ҫ���к���
							if(direction == 1 && matrix[idj][idi] != 0)
								frequency += matrix[idj][idi];
						}
						if(frequency >= threshold)
							idList.add(idi);
					}
					
					//���.net�ļ�
					String temp = "";
					if(direction == 1)
						temp = "E:\\work\\������˾\\txt\\����ͼ_����ֵ\\" + year + "\\" + fileName + "\\" + excelName;
					else 
						temp = "E:\\work\\������˾\\txt\\˫��ͼ_����ֵ\\" + year + "\\" + fileName + "\\" + excelName;
					if(outputFormat == M.OUTPUTFORMAT_NETTXT){//�������
						FileWriter fw = new FileWriter(temp.substring(0, temp.length()-4) + "net");
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
						U.print("�������" + temp.substring(0, temp.length()-4) + "net");
					}
					else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){//���A����ɫ
					Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\������˾\\txt\\companyType.txt");
					FileWriter fw = new FileWriter(temp.substring(0, temp.length()-5) + "colorA.net");
					fw.write("*Vertices " + idList.size());
					for(int fwi = 0; fwi < idList.size(); fwi++){
						fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
						String cpName = mapIdCompany.get(idList.get(fwi));
						fw.write((fwi+1) + " \"" + cpName + "\""
								+ " ic " + U.getCompanyTypeColor(map.get(cpName)!=null ? map.get(cpName) : M.COMPANYTYPE_NOIPO));
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
					U.print("�������" + temp.substring(0, temp.length()-5) + "colorA.net");
					}
				}
			}
		}
		U.print("done");
	}
	
	//�����ϵ��ֵĹ�˾��ϵ��
	public static void outputByStrain(int threshold,int direction, int outputFormat) throws IOException{
			File file = new File("E:\\work\\������˾\\ԭʼ����\\ϵ���");
			String[] fileList = file.list();
			for(String fileName : fileList){
				for(String excelName :fileList){
					//���ݴ洢׼��
					U.print("��ʼ��ȡ" + excelName);
					Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//��¼ÿ����˾����Ӧ��id
					Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//��¼ÿ��id����Ӧ�Ĺ�˾
					int id = 0;//�±��0��ʼ
					byte[][] matrix = new byte[1000][1000];
					
					//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
					XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\������˾\\ԭʼ����\\ϵ���\\" + excelName, 0);
					int rowCount = sheet.getLastRowNum();
					for(int k = 1 ; k < rowCount ; k++){
						//���ʹ�˾��
						XSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
						//��Щexcel�����п���
						if(cellCompanyName == null) break;
						String name = U.getCellStringValue(cellCompanyName).trim().replace(" ", "").replaceAll(" ", "");
						if(U.needContinue(name)) continue;//ȥ�������յĹ�˾��(��Ӣ�Ŀո�)
						if(mapCompanyId.get(name) == null){//����ù�˾������map�У���Ϊ�����һ��id
							mapCompanyId.put(name, id);
							mapIdCompany.put(id, name);//ͬʱΪ��id��Ӧ��company
							id++;
						}
						//���ʹ�����˾
						XSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
						String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
						
						asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
						String[] names = asName.split("��");
						for(String n : names){
							if(U.needContinue(n)) continue;//ȥ�������յĹ�˾��(��Ӣ�Ŀո�)
							if(mapCompanyId.get(n) == null){//����ù�˾������map�У���Ϊ�����һ���±�
								mapCompanyId.put(n, id);
								mapIdCompany.put(id, n);//ͬʱΪ��id��Ӧ��company
								id++;
							}
							//���Ƶ��������幫˾ָ�������˾
							matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] = 1;//����û�и��߸�Ȩ
							if(direction == 2)//˫���ͷ�������������Ҫ+1
								matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] = 1;
						}
					}
					
					//��ȡmatrix��ֻѡȡ������ֵ�Ĺ�˾id
					List<Integer> idList = new ArrayList<>();//��Ÿ�����ֵ��id
					for(int idi = 0; idi < mapCompanyId.size(); idi++){
						int frequency = 0;
						for(int idj = 0; idj < mapCompanyId.size(); idj++){
							//ͳ�Ƹù�˾���ֵ�Ƶ��
							if(matrix[idi][idj] != 0)
								frequency += matrix[idi][idj];
							//�����ͷ��ֵ����Ҫ���к���
							if(direction == 1 && matrix[idj][idi] != 0)
								frequency += matrix[idj][idi];
						}
						if(frequency >= threshold)
							idList.add(idi);
					}
					
					//���.net�ļ�
					String temp = "";
					if(direction == 1)
						temp = "E:\\work\\������˾\\txt\\ϵ��\\����ͼ\\" + excelName;
					else 
						temp = "E:\\work\\������˾\\txt\\ϵ��\\˫��ͼ\\" + excelName;
					if(outputFormat == M.OUTPUTFORMAT_NETTXT){//�������
						FileWriter fw = new FileWriter(temp.substring(0, temp.length()-4) + "net");
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
						U.print("�������" + temp.substring(0, temp.length()-4) + "net");
					}
					else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){//���A����ɫ
						Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\������˾\\txt\\companyType.txt");
					FileWriter fw = new FileWriter(temp.substring(0, temp.length()-5) + "colorA.net");
					fw.write("*Vertices " + idList.size());
					for(int fwi = 0; fwi < idList.size(); fwi++){
						fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
						String cpName = mapIdCompany.get(idList.get(fwi));
						fw.write((fwi+1) + " \"" + cpName + "\""
								+ " ic " + U.getCompanyTypeColor(map.get(cpName)!=null ? map.get(cpName) : M.COMPANYTYPE_NOIPO));
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
					U.print("�������" + temp.substring(0, temp.length()-5) + "colorA.net");
					}
				}
			}
		U.print("done");
	}
	
	//���ÿһ���������ϵ���������������ʽ�������
	public static void outputTransactionType(String type) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2011; i <= 2014; i++){
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
			List<Integer> idList = new ArrayList<>();
			for(int idi = 0; idi < mapCompanyId.size(); idi++){
				idList.add(idi);
			}
			//д��.net
			FileWriter fw = new FileWriter("E:/work/������˾/txt/TransactionType_" + type + "_" + i + ".net");
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
		}
		U.print("done");
	}
	
	//����ṹ���������Է�����txt
	public static void outputCentrality(int year) throws IOException{
		List<String> list = FileFunction.readFile("E:\\work\\������˾\\txt\\���Ķ��о�\\" + year + "_�����뷿�ز�.txt");
		List<String> output = new ArrayList<String>();
		for(int i = 15; i < list.size(); i++){
			String line = list.get(i);
			if(line.equals("")) break;
			output.add(line.replaceAll(" {2,}", ","));
		}
		FileWriter fw = new FileWriter("E:\\work\\������˾\\txt\\���Ķ��о�\\output\\result_" + year + "_�����뷿�ز�.txt");
		for(int i = 0; i < output.size(); i++){
			fw.write(output.get(i) + "\r\n");
		}
		fw.close();
		U.print("done");
	}
	//����ṹ���Ľṹ��������txt
	public static void outputStructualHoles() throws IOException{
		String txtName = "2011_ȫ����";
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
	
	public static void outputPartition(String classify, int year) throws NumberFormatException, IOException{
		List<String> cpList = readCompanyName("E:\\work\\������˾\\txt\\nettxt_asCompany" + year + "_false_1_10.net");
		FileWriter fw = new FileWriter("E:\\work\\������˾\\txt\\partition_" + classify + "_" + year + ".txt");
		fw.write("dl nr = " + cpList.size() + ", nc = 1 format = edgelist2" + "\r\n");
		fw.write("row labels embedded" + "\r\n");
		fw.write("col labels embedded" + "\r\n");
		fw.write("data:" + "\r\n");
		if(classify.equals(M.Classify_EquityOwnership)){
			Map<String, String> map = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyType_����ҵ����.txt");
			for(String cpName : cpList){
				cpName = cpName.trim().replaceAll(" ", "");
				int type = 0;
				if(map.get(cpName) == null)
					type = 3;
				else if(map.get(cpName).equals("������ҵ��������"))
					type = 0;
				else if(map.get(cpName).equals("��Ӫ��ҵ��������"))
					type = 1;
				else if(map.get(cpName).equals("���ʿعɹ�������"))
					type = 2;
				
				if(type == 3)
					U.print(cpName);
				
				fw.write(cpName + " type " + type + "\r\n");
			}
		}
		else if(classify.equals(M.Classify_Industry)){
			Map<String, String> map = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyType_����ҵ.txt");
			for(String cpName : cpList){
				cpName = cpName.trim().replaceAll(" ", "");
				int type = 0;
				if(map.get(cpName) == null)
					type = 3;
				else if(map.get(cpName).equals("�����뷿�ز�ҵ��������"))
					type = 0;
				else if(map.get(cpName).equals("����ҵ��������"))
					type = 1;
				else if(map.get(cpName).equals("�������۹�������"))
					type = 2;
				
				if(type == 3)
					U.print(cpName);
				
				fw.write(cpName + " type " + type + "\r\n");
			}
		}
//		else if(classify.equals(M.Classify_TransactionType)){
//			Map<String, String> map = FileFunction.readMap_SS("E:\\work\\������˾\\txt\\companyType_����������.txt");
//			for(String cpName : cpList){
//				cpName = cpName.trim().replaceAll(" ", "");
//				int type = 0;
//				if(map.get(cpName) == null)
//					type = 3;
//				else if(map.get(cpName).equals("�������������--����"))
//					type = 0;
//				else if(map.get(cpName).equals("��Ӫ��ҵ��������"))
//					type = 1;
//				else if(map.get(cpName).equals("���ʿعɹ�������"))
//					type = 2;
//				
//				if(type == 3)
//					U.print(cpName);
//				
//				fw.write(cpName + " type " + type + "\r\n");
//			}
//		}
		fw.close();
		U.print("done");
	}
	
}
