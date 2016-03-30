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

	//统计4个excel中出现的公司（或人名），输出到txt
	public static void outputCompanyName() throws IOException{
		Map<String, Integer> mapCompany = new HashMap<String, Integer>();
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;

		
		for(int i = 2011; i < 2015; i++){
			String fileName = "E:/work/关联公司/原始数据/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet(fileName, j);
				int rowCount = sheet.getLastRowNum();
				U.print("开始读取" + fileName + ",sheet为" + j + ",共计" + rowCount + "条记录");
				for(int k = 1 ; k < rowCount ; k++){
					//记录该公司出现几次
					int count = 0;
					//添加主体公司名
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName);
					count = mapCompany.get(name) == null ? 1 : mapCompany.get(name)+1;
					mapCompany.put(name, count);
					//添加关联公司名
					cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany);
					asName = asName.replaceAll(",", "、");//2014的excel中切割标示用的是','
					String[] names = asName.split("、");
					for(String n : names){
						count = mapCompany.get(n) == null ? 1 : mapCompany.get(n)+1;
						mapCompany.put(n, count);
					}
				}
			}
		}
		U.print("数据读取完成，开始将公司列表写入txt");
		
		//将map按照value从大到小排序
		ValueComparator bvc =  new ValueComparator(mapCompany);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
        sorted_map.putAll(mapCompany);
        
        U.print(sorted_map.size());
        
        FileFunction.writeCompanyAndFrequency(sorted_map);
        FileFunction.writeCompanyName(sorted_map);
        U.print("done");
	}
	
	
	//将关联公司写入txt
	public static void outputCompanyAssociate(int outputFormat, boolean isOneWay, int threshold) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2011; i < 2015; i++){
			Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//记录每个公司所对应的id
			Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//记录每个id所对应的公司
			int index = 0;//下标从0开始
			byte[][] matrix = new byte[43896][43896];//总共43896个实体，开这么大的矩阵空间足够了
			
			//读取一份excel，将其中公司两两的关系写入
			String fileName = "E:/work/关联公司/原始数据/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			U.print("开始读取:" + fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet(fileName, j);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//访问公司名
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName);
					if(name == " ") continue;//去掉两个空的公司名
					if(mapCompanyId.get(name) == null){//如果该公司并不在map中，则为其添加一个id
						mapCompanyId.put(name, index);
						mapIdCompany.put(index, name);//同时为该id对应到company
						index++;
					}
					//访问关联公司
					cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany);
					
					asName = asName.replaceAll(",", "、");//2014的excel中切割标示用的是','
					String[] names = asName.split("、");
					for(String n : names){
						if(mapCompanyId.get(n) == null){//如果该公司并不在map中，则为其添加一个下标
							mapCompanyId.put(n, index);
							mapIdCompany.put(index, name);//同时为该id对应到company
							index++;
						}
						//绘制单向，由主体公司指向关联公司
						matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] += 1;
						if(!isOneWay)//如果要求双向箭头，则双向+1
							matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] += 1;
					}
				}
			}
			U.print("文件读取结束，开始写入txt");
			
			//读取matrix，只选取高于阈值的公司id（目前仅适用于双向箭头）
			List<Integer> idList = new ArrayList<>();//存放高于阈值的id
			for(int idi = 0; idi < mapCompanyId.size(); idi++){
				int frequency = 0;
				for(int idj = 0; idj < mapCompanyId.size(); idj++){
					//统计该公司出现的频率（目前仅适用于双向箭头）
					if(matrix[idi][idj] != 0)
						frequency += matrix[idi][idj];
				}
				if(frequency >= threshold)
					idList.add(idi);
			}
			
			//将关联公司写入txt(不敢放在别处了，再复制一个matrix内存就满了)
			if(outputFormat == M.OUTPUTFORMAT_DL){
				FileWriter fw = new FileWriter("E:/work/关联公司/txt/dl_asCompany"
							+ i + "_" + isOneWay + "_" + threshold + ".txt");
				fw.write("dl" + "\r\n");
				fw.write("n = " + idList.size() + "\r\n");
				fw.write("labels embedded" + "\r\n");
				fw.write("format = fullmatrix" + "\r\n");
				fw.write("data:" + "\r\n");
				String line = null;
				//写第一行（字段行）
				line = "";//清空line
				for(String key : mapCompanyId.keySet()){
					if(idList.contains(mapCompanyId.get(key)))//仅高于阈值的加入打印出来
						line += key + " ";
				}
				line = line.substring(0, line.length()-1);//删除最后一个空格
				fw.write(line + "\r\n");
				//逐行写记录
				for(String key : mapCompanyId.keySet()){
					if(idList.contains(mapCompanyId.get(key))){//仅高于阈值的加入打印出来
						U.print("正在写入公司:" + key + ",id为:" + mapCompanyId.get(key));
						line = "";//清空line
						line += key + " ";
						for(int fwi = 0; fwi < mapCompanyId.size(); fwi ++){
							if(idList.contains(fwi)){//仅高于阈值的列加入打印出来
								line += matrix[mapCompanyId.get(key)][fwi] + " ";
							}
						}
						line = line.substring(0, line.length()-1);//删除最后一个空格
						fw.write(line + "\r\n");
					}
				}
				fw.close();
			}
			else if(outputFormat == M.OUTPUTFORMAT_NET){
				FileWriter fw = new FileWriter("E:/work/关联公司/txt/net_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + ".txt");
				fw.write("From\tTo\tWeight\r\n");
				for(int fwi = 0; fwi < idList.size(); fwi++){
					U.print("正在写入公司:" + mapIdCompany.get(idList.get(fwi)) + ",id为:" + idList.get(fwi));
					for(int fwj = 0; fwj < idList.size(); fwj++){
						if(matrix[idList.get(fwi)][idList.get(fwj)] == 0) continue;//如果无关联，则跳过
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
				//输出1-mode网络
				FileWriter fw = new FileWriter("E:/work/关联公司/txt/nettxt_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_1mode.net");
				fw.write("*Vertices " + idList.size());
				for(int fwi = 0; fwi < idList.size(); fwi++){
					fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
					fw.write((fwi+1) + " \"" + mapIdCompany.get(idList.get(fwi)) + "\"");
					U.print("写入公司:" + mapIdCompany.get(idList.get(fwi)));
				}
				fw.write("\r\n");
				fw.write("*Edges");
				for(int fwi = 0; fwi < idList.size(); fwi++){
					for(int fwj = 0; fwj < idList.size(); fwj++){
						int weight = matrix[idList.get(fwi)][idList.get(fwj)];
//						if(weight == 0) continue;//如果无关联，则跳过
						for(int weightI = 0; weightI < weight; weightI++){
							fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
							fw.write((fwi+1) + " " + (fwj+1));
							U.print((fwi+1) + " " + (fwj+1));
						}
					}
				}
				fw.close();
			}
			else if(outputFormat == M.OUTPUTFORMAT_NETTXT_2MODE){
				//输出2-mode网络
				FileWriter fw = new FileWriter("E:/work/关联公司/txt/nettxt_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "1mode.net");
				fw.close();
			}
			U.print("写入完毕");
			
		}
		U.print("done");
	}
}
