package com.data;

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
				U.print("��ʼ��ȡ" + fileName + ",sheetΪ" + j + ",����" + rowCount + "����¼");
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
	
	
	//��������˾д��txt
	public static void outputCompanyAssociate(int outputFormat, boolean isOneWay, int threshold) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2011; i < 2015; i++){
			Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//��¼ÿ����˾����Ӧ��id
			Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//��¼ÿ��id����Ӧ�Ĺ�˾
			int index = 0;//�±��0��ʼ
			byte[][] matrix = new byte[43896][43896];//�ܹ�43896��ʵ�壬����ô��ľ���ռ��㹻��
			
			//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
			String fileName = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			U.print("��ʼ��ȡ:" + fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet(fileName, j);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//���ʹ�˾��
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName);
					if(name == " ") continue;//ȥ�������յĹ�˾��
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
							mapIdCompany.put(index, name);//ͬʱΪ��id��Ӧ��company
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
							+ i + "_" + isOneWay + "_" + threshold + ".txt");
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
						+ i + "_" + isOneWay + "_" + threshold + ".txt");
				fw.write("From\tTo\tWeight\r\n");
				for(int fwi = 0; fwi < idList.size(); fwi++){
					U.print("����д�빫˾:" + mapIdCompany.get(idList.get(fwi)) + ",idΪ:" + idList.get(fwi));
					for(int fwj = 0; fwj < idList.size(); fwj++){
						if(matrix[idList.get(fwi)][idList.get(fwj)] == 0) continue;//����޹�����������
//						fw.write(mapIdCompany.get(idList.get(fwi)) + "\t"
//								+ mapIdCompany.get(idList.get(fwj)) + "\t"
//								+ matrix[idList.get(fwi)][idList.get(fwj)] + "\r\n");
						fw.write(idList.get(fwi) + "\t"
								+ idList.get(fwj) + "\t"
								+ matrix[idList.get(fwi)][idList.get(fwj)] + "\r\n");
					}
				}
				fw.close();
			}
			else if(outputFormat == M.OUTPUTFORMAT_NETTXT){
				//���1-mode����
				FileWriter fw = new FileWriter("E:/work/������˾/txt/nettxt_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_1mode.net");
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
			else if(outputFormat == M.OUTPUTFORMAT_NETTXT_2MODE){
				//���2-mode����
				FileWriter fw = new FileWriter("E:/work/������˾/txt/nettxt_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "1mode.net");
				fw.close();
			}
			U.print("д�����");
			
		}
		U.print("done");
	}
}
