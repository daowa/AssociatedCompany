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
	
	//merget表
//	private static int EXCELINDEX_PATENTID = 0;
//	private static int EXCELINDEX_PARTITION = 1;
//	private static int EXCELINDEX_HOLDER = 5;
	private static int EXCELINDEX_TIME = 4;
	private static int EXCELINDEX_SEQUENCE = 16;
	
	//蔚老师的表
	private static int EXCELINDEX_PATENTID = 0;
	private static int EXCELINDEX_HOLDER = 4;
	private static int EXCELINDEX_PARTITION = 6;//5是单位，6是国别

	public static void writeNet(int mode) throws IOException{
		List<String> listHolder = new ArrayList<>();//可删除，有空删
		Map<Integer, String> mapIdHolder = new HashMap<>();
		Map<String, Integer> mapHolderId = new HashMap<>();
		Map<String, Integer> mapHolderPartition = new HashMap<>();
		Map<String, Double> mapHolderWeight = new HashMap<String, Double>();
		Map<String, Integer> mapHolderTimeF = new HashMap<>();//存最早出现时间
		Map<String, Integer> mapHolderTimeL = new HashMap<>();//存最晚出现时间
		byte[][] matrix = new byte[3000][3000];
		int id = 0;
		
		//读取一份excel，将其中公司两两的关系写入
		String fileName = "E:\\work\\课内\\研一上\\商业分析\\作业-专利\\merge.xls";
		U.print("开始读取:" + fileName);
		HSSFSheet sheet = ExcelFunction.getSheet_HSSF(fileName, 0);
		int rowCount = sheet.getLastRowNum();
		List<String> listGroup = new ArrayList<>();//保存同一个专利的团队
		String lastPatentID = "";
		for(int k = 1 ; k < rowCount ; k++){
			//读取发明人
			String holder = U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_HOLDER)).replace(",", ".");
			if(!listHolder.contains(holder)){
				listHolder.add(holder);
				mapIdHolder.put(id, holder);
				mapHolderId.put(holder, id++);
			}
			//该发明人最晚出现时间
			int time = Integer.parseInt(U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_TIME)));
			if(mapHolderTimeF.get(holder) == null) mapHolderTimeF.put(holder, time);
			mapHolderTimeL.put(holder, time);
			//发明人-类型
			int partition = Integer.parseInt(U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_PARTITION))) + 1;
			if(time > 38490) partition = 1;//38490是excel中2005年5月18日，之前的根据是否ibm分0和1，之后新出现的均归为2
			if(mapHolderPartition.get(holder) == null) mapHolderPartition.put(holder, partition);
			//发明人-权重
			double sequence = Double.parseDouble(U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_SEQUENCE)));
			mapHolderWeight.put(holder, (mapHolderWeight.get(holder) == null) ? 1/sequence : mapHolderWeight.get(holder) + 1/sequence);
			//发明人之间的关系
			String patentID = U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_PATENTID));
			if(patentID.equals(lastPatentID)){//如果该行的专利号和上一行一样，则将这一行的专利持有人加入group
				listGroup.add(holder);
			}
			else{//如果是新的专利号，将之前的group写入网络，清空listGroup，并更新lastPatentId
				//写入网络
				for(int i = 0; i < listGroup.size(); i++)
					for(int j = i+1; j < listGroup.size(); j++){
						matrix[mapHolderId.get(listGroup.get(i))][mapHolderId.get(listGroup.get(j))] += 1;
						matrix[mapHolderId.get(listGroup.get(j))][mapHolderId.get(listGroup.get(i))] += 1;
					}
				listGroup.clear();//清空listGroup
				lastPatentID = patentID;//更新lastPatentId
			}
		}
		
		//根据阈值筛选idlist
		List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapHolderId.size(), 0, false);
		//根据时间筛选idList(只针对之后的有效)
		U.print(idList.size());
		for(int i = 0; i < idList.size(); i++){
//			//只收录收购前
//			if(mapHolderTimeF.get(mapIdHolder.get(idList.get(i))) >= 38490){//最早时间都在收购以后，则一定是收购后的人了，删去
//				idList.remove(i);
//				i--;//因为删除，所以前移了，要抵消之后的i++
//			}
			//只收录收购后
			if(mapHolderTimeL.get(mapIdHolder.get(idList.get(i))) < 38490){//最晚时间都在收购以前，则一定是收购前的人了，删去
				idList.remove(i);
				i--;//因为删除，所以前移了，要抵消之后的i++
			}
		}
		U.print(idList.size());
		
		//写入文件
		if(mode == M.MODETYPE_ONLYSELECTED_WEIGHTCSV){
			String pathNode = "E:\\work\\课内\\研一上\\商业分析\\作业-专利\\node.csv";
			String pathLine = "E:\\work\\课内\\研一上\\商业分析\\作业-专利\\line.csv";
			writeCSV_Node(idList, mapIdHolder, mapHolderWeight, mapHolderPartition, pathNode);
			writeCSV_Line(idList, matrix, pathLine);
		}
		else if(mode == M.MODETYPE_ONLYSELECTED){
			String path = "E:\\work\\课内\\研一上\\商业分析\\作业-专利\\Net.net";
			FileFunction.writeNet_Simple(false, idList, mapIdHolder, matrix, path);//目前使用无权值的网络
			//输出partition
			FileWriter fw = new FileWriter("E:\\work\\课内\\研一上\\商业分析\\作业-专利\\partition.net");
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
		List<String> listHolder = new ArrayList<>();//可删除，有空删
		Map<Integer, String> mapIdHolder = new HashMap<>();
		Map<String, Integer> mapHolderId = new HashMap<>();
		Map<String, Integer> mapHolderPartition = new HashMap<>();
		Map<String, Double> mapHolderWeight = new HashMap<String, Double>();
		Map<String, Integer> mapHolderTimeF = new HashMap<>();//存最早出现时间
		Map<String, Integer> mapHolderTimeL = new HashMap<>();//存最晚出现时间
		byte[][] matrix = new byte[3000][3000];
		int id = 0;
		
		//读取一份excel，将其中公司两两的关系写入
		String fileName = "E:\\work\\课内\\研一上\\商业分析\\作业-专利\\collaboration4.xls";
		U.print("开始读取:" + fileName);
		HSSFSheet sheet = ExcelFunction.getSheet_HSSF(fileName, 0);
		int rowCount = sheet.getLastRowNum();
		List<String> listGroup = new ArrayList<>();//保存同一个专利的团队
		String lastPatentID = "";
		for(int k = 1 ; k < rowCount ; k++){
			//读取发明人
			String holder = U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_HOLDER)).replace(",", ".");
			if(!listHolder.contains(holder)){
				listHolder.add(holder);
				mapIdHolder.put(id, holder);
				mapHolderId.put(holder, id++);
			}
			//发明人-类型
			String sPartition = U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_PARTITION));
			int partition = -1;
			//以单位区分
//			if(sPartition.contains("Remained"))
//				partition = 0;
//			else if(sPartition.contains("IBM"))
//				partition = 1;
//			else if(sPartition.contains("New"))
//				partition = 2;
			//以国别区分
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
			//发明人-权重
			double sequence = 1;
			mapHolderWeight.put(holder, (mapHolderWeight.get(holder) == null) ? 1/sequence : mapHolderWeight.get(holder) + 1/sequence);
			//发明人之间的关系
			String patentID = U.getCellStringValue(sheet.getRow(k).getCell(EXCELINDEX_PATENTID));
			if(patentID.equals(lastPatentID)){//如果该行的专利号和上一行一样，则将这一行的专利持有人加入group
				listGroup.add(holder);
			}
			else{//如果是新的专利号，将之前的group写入网络，清空listGroup，并更新lastPatentId
				//写入网络
				for(int i = 0; i < listGroup.size(); i++)
					for(int j = i+1; j < listGroup.size(); j++){
						matrix[mapHolderId.get(listGroup.get(i))][mapHolderId.get(listGroup.get(j))] += 1;
						matrix[mapHolderId.get(listGroup.get(j))][mapHolderId.get(listGroup.get(i))] += 1;
					}
				listGroup.clear();//清空listGroup
				lastPatentID = patentID;//更新lastPatentId
			}
		}
		
		//根据阈值筛选idlist
		List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapHolderId.size(), 0, false);
		U.print(idList.size());
		
		//写入文件
		if(mode == M.MODETYPE_ONLYSELECTED_WEIGHTCSV){
			String pathNode = "E:\\work\\课内\\研一上\\商业分析\\作业-专利\\node.csv";
			String pathLine = "E:\\work\\课内\\研一上\\商业分析\\作业-专利\\line.csv";
			writeCSV_Node(idList, mapIdHolder, mapHolderWeight, mapHolderPartition, pathNode);
			writeCSV_Line(idList, matrix, pathLine);
		}
		else if(mode == M.MODETYPE_ONLYSELECTED){
			String path = "E:\\work\\课内\\研一上\\商业分析\\作业-专利\\Net.net";
			FileFunction.writeNet_Simple(false, idList, mapIdHolder, matrix, path);//目前使用无权值的网络
			//输出partition
			FileWriter fw = new FileWriter("E:\\work\\课内\\研一上\\商业分析\\作业-专利\\partition.net");
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
