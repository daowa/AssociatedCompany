package com.Others;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.db.ExcelFunction;
import com.db.FileFunction;
import com.myClass.M;
import com.myClass.U;

public class Patent {
	
	//merget��
//	private static int EXCELINDEX_PATENTID = 0;
//	private static int EXCELINDEX_PARTITION = 1;
//	private static int EXCELINDEX_HOLDER = 5;
	private static int EXCELINDEX_TIME = 4;
	private static int EXCELINDEX_SEQUENCE = 16;
	
	//ε��ʦ�ı�
	private static int EXCELINDEX_PATENTID = 0;
	private static int EXCELINDEX_HOLDER = 4;
	private static int EXCELINDEX_PARTITION = 6;//5�ǵ�λ��6�ǹ���

	public static void writeNet(int mode) throws IOException{
		List<String> listHolder = new ArrayList<>();//��ɾ�����п�ɾ
		Map<Integer, String> mapIdHolder = new HashMap<>();
		Map<String, Integer> mapHolderId = new HashMap<>();
		Map<String, Integer> mapHolderPartition = new HashMap<>();
		Map<String, Double> mapHolderWeight = new HashMap<String, Double>();
		Map<String, Integer> mapHolderTimeF = new HashMap<>();//���������ʱ��
		Map<String, Integer> mapHolderTimeL = new HashMap<>();//���������ʱ��
		byte[][] matrix = new byte[3000][3000];
		int id = 0;
		
		//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
		String fileName = "E:\\work\\����\\��һ��\\��ҵ����\\��ҵ-ר��\\merge.xls";
		U.print("��ʼ��ȡ:" + fileName);
		HSSFSheet sheet = ExcelFunction.getSheet_HSSF(fileName, 0);
		int rowCount = sheet.getLastRowNum();
		List<String> listGroup = new ArrayList<>();//����ͬһ��ר�����Ŷ�
		String lastPatentID = "";
		for(int k = 1 ; k < rowCount ; k++){
			//��ȡ������
			String holder = U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_HOLDER)).replace(",", ".");
			if(!listHolder.contains(holder)){
				listHolder.add(holder);
				mapIdHolder.put(id, holder);
				mapHolderId.put(holder, id++);
			}
			//�÷������������ʱ��
			int time = Integer.parseInt(U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_TIME)));
			if(mapHolderTimeF.get(holder) == null) mapHolderTimeF.put(holder, time);
			mapHolderTimeL.put(holder, time);
			//������-����
			int partition = Integer.parseInt(U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_PARTITION))) + 1;
			if(time > 38490) partition = 1;//38490��excel��2005��5��18�գ�֮ǰ�ĸ����Ƿ�ibm��0��1��֮���³��ֵľ���Ϊ2
			if(mapHolderPartition.get(holder) == null) mapHolderPartition.put(holder, partition);
			//������-Ȩ��
			double sequence = Double.parseDouble(U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_SEQUENCE)));
			mapHolderWeight.put(holder, (mapHolderWeight.get(holder) == null) ? 1/sequence : mapHolderWeight.get(holder) + 1/sequence);
			//������֮��Ĺ�ϵ
			String patentID = U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_PATENTID));
			if(patentID.equals(lastPatentID)){//������е�ר���ź���һ��һ��������һ�е�ר�������˼���group
				listGroup.add(holder);
			}
			else{//������µ�ר���ţ���֮ǰ��groupд�����磬���listGroup��������lastPatentId
				//д������
				for(int i = 0; i < listGroup.size(); i++)
					for(int j = i+1; j < listGroup.size(); j++){
						matrix[mapHolderId.get(listGroup.get(i))][mapHolderId.get(listGroup.get(j))] += 1;
						matrix[mapHolderId.get(listGroup.get(j))][mapHolderId.get(listGroup.get(i))] += 1;
					}
				listGroup.clear();//���listGroup
				lastPatentID = patentID;//����lastPatentId
			}
		}
		
		//������ֵɸѡidlist
		List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapHolderId.size(), 0, false);
		//����ʱ��ɸѡidList(ֻ���֮�����Ч)
		U.print(idList.size());
		for(int i = 0; i < idList.size(); i++){
//			//ֻ��¼�չ�ǰ
//			if(mapHolderTimeF.get(mapIdHolder.get(idList.get(i))) >= 38490){//����ʱ�䶼���չ��Ժ���һ�����չ�������ˣ�ɾȥ
//				idList.remove(i);
//				i--;//��Ϊɾ��������ǰ���ˣ�Ҫ����֮���i++
//			}
			//ֻ��¼�չ���
			if(mapHolderTimeL.get(mapIdHolder.get(idList.get(i))) < 38490){//����ʱ�䶼���չ���ǰ����һ�����չ�ǰ�����ˣ�ɾȥ
				idList.remove(i);
				i--;//��Ϊɾ��������ǰ���ˣ�Ҫ����֮���i++
			}
		}
		U.print(idList.size());
		
		//д���ļ�
		if(mode == M.MODETYPE_ONLYSELECTED_WEIGHTCSV){
			String pathNode = "E:\\work\\����\\��һ��\\��ҵ����\\��ҵ-ר��\\node.csv";
			String pathLine = "E:\\work\\����\\��һ��\\��ҵ����\\��ҵ-ר��\\line.csv";
			writeCSV_Node(idList, mapIdHolder, mapHolderWeight, mapHolderPartition, pathNode);
			writeCSV_Line(idList, matrix, pathLine);
		}
		else if(mode == M.MODETYPE_ONLYSELECTED){
			String path = "E:\\work\\����\\��һ��\\��ҵ����\\��ҵ-ר��\\Net.net";
			FileFunction.writeNet_Simple(false, idList, mapIdHolder, matrix, path);//Ŀǰʹ����Ȩֵ������
			//���partition
			FileWriter fw = new FileWriter("E:\\work\\����\\��һ��\\��ҵ����\\��ҵ-ר��\\partition.net");
			fw.write("*Vertices " + idList.size() + "\r\n");
			for(int i = 0; i < idList.size(); i++){
				String holder = mapIdHolder.get(idList.get(i));
				fw.write(mapHolderPartition.get(holder) + "\r\n");
			}
			fw.close();
		}
		U.print("done");
	}
	
	public static void writeNet2(int mode) throws IOException{
		List<String> listHolder = new ArrayList<>();//��ɾ�����п�ɾ
		Map<Integer, String> mapIdHolder = new HashMap<>();
		Map<String, Integer> mapHolderId = new HashMap<>();
		Map<String, Integer> mapHolderPartition = new HashMap<>();
		Map<String, Double> mapHolderWeight = new HashMap<String, Double>();
		Map<String, Integer> mapHolderTimeF = new HashMap<>();//���������ʱ��
		Map<String, Integer> mapHolderTimeL = new HashMap<>();//���������ʱ��
		byte[][] matrix = new byte[3000][3000];
		int id = 0;
		
		//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
		String fileName = "E:\\work\\����\\��һ��\\��ҵ����\\��ҵ-ר��\\collaboration4.xls";
		U.print("��ʼ��ȡ:" + fileName);
		HSSFSheet sheet = ExcelFunction.getSheet_HSSF(fileName, 0);
		int rowCount = sheet.getLastRowNum();
		List<String> listGroup = new ArrayList<>();//����ͬһ��ר�����Ŷ�
		String lastPatentID = "";
		for(int k = 1 ; k < rowCount ; k++){
			//��ȡ������
			String holder = U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_HOLDER)).replace(",", ".");
			if(!listHolder.contains(holder)){
				listHolder.add(holder);
				mapIdHolder.put(id, holder);
				mapHolderId.put(holder, id++);
			}
			//������-����
			String sPartition = U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_PARTITION));
			int partition = -1;
			//�Ե�λ����
//			if(sPartition.contains("Remained"))
//				partition = 0;
//			else if(sPartition.contains("IBM"))
//				partition = 1;
//			else if(sPartition.contains("New"))
//				partition = 2;
			//�Թ�������
			if(sPartition.contains("us"))
				partition = 0;
			else if(sPartition.contains("jp"))
				partition = 1;
			else if(sPartition.contains("cn"))
				partition = 2;
			else if(sPartition.contains("d7"))
				partition = 3;
			else if(sPartition.contains("gb"))
				partition = 4;
			else if(sPartition.contains("hk"))
				partition = 5;
			else if(sPartition.contains("it"))
				partition = 6;
			U.print(partition);
			if(mapHolderPartition.get(holder) == null) mapHolderPartition.put(holder, partition);
			//������-Ȩ��
			double sequence = 1;
			mapHolderWeight.put(holder, (mapHolderWeight.get(holder) == null) ? 1/sequence : mapHolderWeight.get(holder) + 1/sequence);
			//������֮��Ĺ�ϵ
			String patentID = U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_PATENTID));
			if(patentID.equals(lastPatentID)){//������е�ר���ź���һ��һ��������һ�е�ר�������˼���group
				listGroup.add(holder);
			}
			else{//������µ�ר���ţ���֮ǰ��groupд�����磬���listGroup��������lastPatentId
				//д������
				for(int i = 0; i < listGroup.size(); i++)
					for(int j = i+1; j < listGroup.size(); j++){
						matrix[mapHolderId.get(listGroup.get(i))][mapHolderId.get(listGroup.get(j))] += 1;
						matrix[mapHolderId.get(listGroup.get(j))][mapHolderId.get(listGroup.get(i))] += 1;
					}
				listGroup.clear();//���listGroup
				lastPatentID = patentID;//����lastPatentId
			}
		}
		
		//������ֵɸѡidlist
		List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapHolderId.size(), 0, false);
		U.print(idList.size());
		
		//д���ļ�
		if(mode == M.MODETYPE_ONLYSELECTED_WEIGHTCSV){
			String pathNode = "E:\\work\\����\\��һ��\\��ҵ����\\��ҵ-ר��\\node.csv";
			String pathLine = "E:\\work\\����\\��һ��\\��ҵ����\\��ҵ-ר��\\line.csv";
			writeCSV_Node(idList, mapIdHolder, mapHolderWeight, mapHolderPartition, pathNode);
			writeCSV_Line(idList, matrix, pathLine);
		}
		else if(mode == M.MODETYPE_ONLYSELECTED){
			String path = "E:\\work\\����\\��һ��\\��ҵ����\\��ҵ-ר��\\Net.net";
			FileFunction.writeNet_Simple(false, idList, mapIdHolder, matrix, path);//Ŀǰʹ����Ȩֵ������
			//���partition
			FileWriter fw = new FileWriter("E:\\work\\����\\��һ��\\��ҵ����\\��ҵ-ר��\\partition.net");
			fw.write("*Vertices " + idList.size() + "\r\n");
			for(int i = 0; i < idList.size(); i++){
				String holder = mapIdHolder.get(idList.get(i));
				fw.write(mapHolderPartition.get(holder) + "\r\n");
			}
			fw.close();
		}
		U.print("done");
	}
	
	
	
	
	
	
	
	private static void writeCSV_Node(List<Integer> idList, Map<Integer, String> mapIdCompany, Map<String, Double> mapHolderWeight, Map<String, Integer> mapHolderPartition, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("Id,Label,weighted degree,partition\r\n");
		for(int i = 0; i < idList.size(); i++){
			fw.write((i+1) + "," + mapIdCompany.get(idList.get(i)) + "," + mapHolderWeight.get(mapIdCompany.get(idList.get(i))) + "," + mapHolderPartition.get(mapIdCompany.get(idList.get(i))) + "\r\n");
		}
		fw.close();
	}
	
	private static void writeCSV_Line(List<Integer> idList, byte[][] matrixWeight, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.write("Source,Target,Type,id,label,timeset,weight\r\n");
		int lineId = 0;
		for(int i = 0; i < idList.size(); i++){
			for(int j = 0; j < idList.size(); j++){
				if(matrixWeight[idList.get(i)][idList.get(j)] > 0){
					int weight = matrixWeight[idList.get(i)][idList.get(j)];
					fw.write((i+1) + "," + (j+1) + "," + "unDirected" + "," + lineId++ + "," + "," + "," + weight + "\r\n");
				}
			}
		}
		fw.close();
	}
	
}
