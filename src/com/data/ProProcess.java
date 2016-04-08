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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hwpf.model.types.HRESIAbstractType;

import com.db.ExcelFunction;
import com.db.FileFunction;
import com.myClass.M;
import com.myClass.U;
import com.myClass.ValueComparator;

/**
 * @author Administrator
 *
 */
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
					String name = U.getCellStringValue(cellCompanyName);
					count = mapCompany.get(name) == null ? 1 : mapCompany.get(name)+1;
					mapCompany.put(name, count);
					//��ӹ�����˾��
					cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany);
					asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
					String[] names = asName.split("��");
					for(String n : names){
						count = mapCompany.get(n) == null ? 1 : mapCompany.get(n)+1;
						mapCompany.put(n, count);
					}
				}
			}
		}
		U.print("���ݶ�ȡ��ɣ���ʼ����˾�б�д��txt");
		
		//��map����value�Ӵ�С����
		ValueComparator bvc =  new ValueComparator(mapCompany);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
        sorted_map.putAll(mapCompany);
        
        U.print(sorted_map.size());
        
        FileFunction.writeCompanyAndFrequency(sorted_map);
        FileFunction.writeCompanyName(sorted_map);
        U.print("done");
	}
	
	//ͳ��4��excel�г��ֵĹ�˾���������������ͣ������txt�����ظ�����˾������
	public static Map<String, Integer> outputCompanyType() throws IOException{
		U.print("��ʼ��ȡ��˾����");
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
					String name = U.getCellStringValue(cellCompanyName);
					//��ȡ��Ʊ����
					HSSFCell tempCell = sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol);
					String stockSymbol = U.getCellStringValue(tempCell);
					if(isA(stockSymbol))
						map.put(name, M.COMPANYTYPE_A);
					else
						map.put(name, M.COMPANYTYPE_B);
				}
			}
		}
		U.print("��ȡ��˾�������,��ʼд��TXT");

		FileWriter fw = new FileWriter("E:\\work\\������˾\\txt\\companyType.txt");
		for(String key : map.keySet()){
			fw.write(key + "\t" + map.get(key) + "\r\n");
		}
		fw.close();
		U.print("д��txt���");
		return map;
	}
	
	//������˾��ַ�����txt
	public static Map<String, String> outputCompanyAddress() throws IOException{
		U.print("��ʼ��ȡ��˾��ַ");
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
					String name = U.getCellStringValue(cellCompanyName);
					//��ȡ��ַ
					HSSFCell tempCell = sheet.getRow(k).getCell(M.EXCELINDEX_Address);
					String address = U.getCellStringValue(tempCell);
					map.put(name, getCompanyAddress(address));
				}
			}
		}
		U.print("��ȡ��˾��ַ���,��ʼд��TXT");

		FileWriter fw = new FileWriter("E:\\work\\������˾\\txt\\companyAddress.txt");
		for(String key : map.keySet()){
			fw.write(key + "\t" + map.get(key) + "\r\n");
		}
		fw.close();
		U.print("д��txt���");
		return map;
	}
	
	//��txt�ж�ȡ��˾����
	private static Map<String, Integer> readCompanyType() throws NumberFormatException, IOException{
		Map<String, Integer> map = new HashMap<>();
		File file = new File("E:\\work\\������˾\\txt\\companyType.txt");
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
	
	//��txt�ж�ȡ��˾��ַ
	private static Map<String, String> readCompanyAddress() throws NumberFormatException, IOException{
		Map<String, String> map = new HashMap<>();
		File file = new File("E:\\work\\������˾\\txt\\companyAddress.txt");
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
	
	//��������˾д��txt
	public static void outputCompanyAssociate(int outputFormat,  int mode, boolean isOneWay, int threshold) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2014; i < 2015; i++){
			Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//��¼ÿ����˾����Ӧ��id
			Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//��¼ÿ��id����Ӧ�Ĺ�˾
			int index = 0;//�±��0��ʼ
			byte[][] matrix = new byte[25265][25265];//���25265����˾��2015�꣩������ô��ľ���ռ��㹻��
			
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
						String stockSymbol = U.getCellStringValue(tempCell);
						if(!isA(stockSymbol))
							continue;
					}
					//���ʹ�˾��
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName);
					if(name == " " || name == " ") continue;//ȥ�������յĹ�˾��(��Ӣ�Ŀո�)
					if(mapCompanyId.get(name) == null){//����ù�˾������map�У���Ϊ�����һ��id
						mapCompanyId.put(name, index);
						mapIdCompany.put(index, name);//ͬʱΪ��id��Ӧ��company
						index++;
					}
					//���ʹ�����˾
					cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany);
					
					asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
					String[] names = asName.split("��");
					for(String n : names){
						if(mapCompanyId.get(n) == null){//����ù�˾������map�У���Ϊ�����һ���±�
							mapCompanyId.put(n, index);
							mapIdCompany.put(index, n);//ͬʱΪ��id��Ӧ��company
							index++;
						}
						//���Ƶ��������幫˾ָ�������˾
						matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] += 1;
						if(!isOneWay)//���Ҫ��˫���ͷ����˫��+1
							matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] += 1;
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
					U.print("д�빫˾:" + mapIdCompany.get(idList.get(fwi)));
				}
				fw.write("\r\n");
				fw.write("*Edges");
				for(int fwi = 0; fwi < idList.size(); fwi++){
					for(int fwj = 0; fwj < idList.size(); fwj++){
						int weight = matrix[idList.get(fwi)][idList.get(fwj)];
//						if(weight == 0) continue;//����޹�����������
						for(int weightI = 0; weightI < weight; weightI++){
							fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
							fw.write((fwi+1) + " " + (fwj+1));
							U.print((fwi+1) + " " + (fwj+1));
						}
					}
				}
				fw.close();
			}
			else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){
				Map<String, Integer> map = readCompanyType();
				FileWriter fw = new FileWriter("E:/work/������˾/txt/cpType_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net");
				fw.write("*Vertices " + idList.size());
				for(int fwi = 0; fwi < idList.size(); fwi++){
					fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
					String cpName = mapIdCompany.get(idList.get(fwi));
					fw.write((fwi+1) + " \"" + cpName + "\""
							+ " ic " + getCompanyTypeColor(map.get(cpName)!=null ? map.get(cpName) : M.COMPANYTYPE_NOIPO));
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
				Map<String, String> map = readCompanyAddress();
				FileWriter fw = new FileWriter("E:/work/������˾/txt/cpAddress_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net");
				fw.write("*Vertices " + idList.size());
				for(int fwi = 0; fwi < idList.size(); fwi++){
					String cpName = mapIdCompany.get(idList.get(fwi));
					fw.write("\r\n");//Ϊ��һ�в��任�У��������һ��Ҳ������
					fw.write((fwi+1) + " \"" + cpName + "\""
							+ " ic " + getAddressColor(map.get(cpName)));
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
	
	//�ж��Ƿ���A�ɹ�˾
	private static boolean isA(String stockSymbol){
		String firstThree = stockSymbol.substring(0,3);
		if(firstThree.contains("600"))
			return true;
		if(firstThree.contains("601"))
			return true;
		if(firstThree.contains("603"))
			return true;
		if(firstThree.contains("000"))
			return true;
		return false;
	}
	
	//��ͬ���͹�˾���ص���ɫ
	private static String getCompanyTypeColor(int companyType){
		if(companyType == M.COMPANYTYPE_B)
			return "Blue";
		else if(companyType == M.COMPANYTYPE_A)
			return "Red";
		return "Gray";
	}
	//��ͬ��ַ��˾���ص���ɫ
	private static String getAddressColor(String address){
		if(address == null)
			return "Black";
		else if(address.equals("�Ϻ�"))
			return "Blue";
		else if(address.equals("����"))
			return "Orange";
		else if(address.equals("����"))
			return "Gray";
		return "Black";
	}
	
	//���ع�˾����
	private static String getCompanyAddress(String address){
		if(address.contains("�Ϻ�"))
			return "�Ϻ�";
		if(address.contains("����"))
			return "����";
		if(address.contains("����"))
			return "����";
		return "����";
	}
}
