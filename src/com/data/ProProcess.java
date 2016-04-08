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
	
	//统计4个excel中出现的公司（或人名）的类型，输出到txt并返回各个公司的类型
	public static Map<String, Integer> outputCompanyType() throws IOException{
		U.print("开始获取公司类型");
		Map<String, Integer> map = new HashMap<String, Integer>();
		HSSFCell cellCompanyName = null;
		
		for(int i = 2011; i < 2015; i++){
			String fileName = "E:/work/关联公司/原始数据/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet(fileName, j);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//仅统计主体公司，没有出现在主体公司中的关联公司都是非上市公司
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName);
					//获取股票号码
					HSSFCell tempCell = sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol);
					String stockSymbol = U.getCellStringValue(tempCell);
					if(isA(stockSymbol))
						map.put(name, M.COMPANYTYPE_A);
					else
						map.put(name, M.COMPANYTYPE_B);
				}
			}
		}
		U.print("获取公司类型完毕,开始写入TXT");

		FileWriter fw = new FileWriter("E:\\work\\关联公司\\txt\\companyType.txt");
		for(String key : map.keySet()){
			fw.write(key + "\t" + map.get(key) + "\r\n");
		}
		fw.close();
		U.print("写入txt完成");
		return map;
	}
	
	//将各公司地址输出到txt
	public static Map<String, String> outputCompanyAddress() throws IOException{
		U.print("开始获取公司地址");
		Map<String, String> map = new HashMap<String, String>();
		HSSFCell cellCompanyName = null;
		
		for(int i = 2011; i < 2015; i++){
			String fileName = "E:/work/关联公司/原始数据/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet(fileName, j);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//仅统计主体公司，因为仅主体公司有地址信息
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName);
					//获取地址
					HSSFCell tempCell = sheet.getRow(k).getCell(M.EXCELINDEX_Address);
					String address = U.getCellStringValue(tempCell);
					map.put(name, getCompanyAddress(address));
				}
			}
		}
		U.print("获取公司地址完毕,开始写入TXT");

		FileWriter fw = new FileWriter("E:\\work\\关联公司\\txt\\companyAddress.txt");
		for(String key : map.keySet()){
			fw.write(key + "\t" + map.get(key) + "\r\n");
		}
		fw.close();
		U.print("写入txt完成");
		return map;
	}
	
	//从txt中读取公司类型
	private static Map<String, Integer> readCompanyType() throws NumberFormatException, IOException{
		Map<String, Integer> map = new HashMap<>();
		File file = new File("E:\\work\\关联公司\\txt\\companyType.txt");
		InputStreamReader stream = new InputStreamReader(new FileInputStream(file));
		BufferedReader reader = new BufferedReader(stream);
		String line = "";
		while((line = reader.readLine()) != null){
			if(line == "") break;//说明读到最最后一行了
			String[] cpType = line.split("\t");
			map.put(cpType[0], Integer.parseInt(cpType[1]));
		}
		return map;
	}
	
	//从txt中读取公司地址
	private static Map<String, String> readCompanyAddress() throws NumberFormatException, IOException{
		Map<String, String> map = new HashMap<>();
		File file = new File("E:\\work\\关联公司\\txt\\companyAddress.txt");
		InputStreamReader stream = new InputStreamReader(new FileInputStream(file));
		BufferedReader reader = new BufferedReader(stream);
		String line = "";
		while((line = reader.readLine()) != null){
			if(line == "") break;//说明读到最最后一行了
			String[] cpType = line.split("\t");
			map.put(cpType[0], cpType[1]);
		}
		return map;
	}
	
	//将关联公司写入txt
	public static void outputCompanyAssociate(int outputFormat,  int mode, boolean isOneWay, int threshold) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2014; i < 2015; i++){
			Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//记录每个公司所对应的id
			Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//记录每个id所对应的公司
			int index = 0;//下标从0开始
			byte[][] matrix = new byte[25265][25265];//最大25265个公司（2015年），开这么大的矩阵空间足够了
			
			//读取一份excel，将其中公司两两的关系写入
			String fileName = "E:/work/关联公司/原始数据/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			U.print("开始读取:" + fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet(fileName, j);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					if(mode == M.MODE_ONLYA){//仅A股模式下
						HSSFCell tempCell = sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol);
						String stockSymbol = U.getCellStringValue(tempCell);
						if(!isA(stockSymbol))
							continue;
					}
					//访问公司名
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName);
					if(name == " " || name == " ") continue;//去掉两个空的公司名(中英文空格)
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
							mapIdCompany.put(index, n);//同时为该id对应到company
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
							+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".txt");
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
						+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".txt");
				fw.write("From\tTo\tWeight\r\n");
				for(int fwi = 0; fwi < idList.size(); fwi++){
					U.print("正在写入公司:" + mapIdCompany.get(idList.get(fwi)) + ",id为:" + idList.get(fwi));
					for(int fwj = 0; fwj < idList.size(); fwj++){
						if(matrix[idList.get(fwi)][idList.get(fwj)] == 0) continue;//如果无关联，则跳过
						fw.write(idList.get(fwi) + "\t"
								+ idList.get(fwj) + "\t"
								+ matrix[idList.get(fwi)][idList.get(fwj)]/2 + "\r\n");
					}
				}
				fw.close();
			}
			else if(outputFormat == M.OUTPUTFORMAT_NETTXT){
				FileWriter fw = new FileWriter("E:/work/关联公司/txt/nettxt_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net");
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
			else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){
				Map<String, Integer> map = readCompanyType();
				FileWriter fw = new FileWriter("E:/work/关联公司/txt/cpType_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net");
				fw.write("*Vertices " + idList.size());
				for(int fwi = 0; fwi < idList.size(); fwi++){
					fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
					String cpName = mapIdCompany.get(idList.get(fwi));
					fw.write((fwi+1) + " \"" + cpName + "\""
							+ " ic " + getCompanyTypeColor(map.get(cpName)!=null ? map.get(cpName) : M.COMPANYTYPE_NOIPO));
					U.print("写入公司:" + mapIdCompany.get(idList.get(fwi)));
				}
				fw.write("\r\n");
				fw.write("*Edges");
				for(int fwi = 0; fwi < idList.size(); fwi++){
					for(int fwj = 0; fwj < idList.size(); fwj++){
						int weight = matrix[idList.get(fwi)][idList.get(fwj)];
						for(int weightI = 0; weightI < weight; weightI++){
							fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
							fw.write((fwi+1) + " " + (fwj+1));
							U.print((fwi+1) + " " + (fwj+1));
						}
					}
				}
				fw.close();
			}
			else if(outputFormat == M.OUTPUTFORMAT_ADDRESS){
				Map<String, String> map = readCompanyAddress();
				FileWriter fw = new FileWriter("E:/work/关联公司/txt/cpAddress_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net");
				fw.write("*Vertices " + idList.size());
				for(int fwi = 0; fwi < idList.size(); fwi++){
					String cpName = mapIdCompany.get(idList.get(fwi));
					fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
					fw.write((fwi+1) + " \"" + cpName + "\""
							+ " ic " + getAddressColor(map.get(cpName)));
					U.print("写入公司:" + mapIdCompany.get(idList.get(fwi)));
				}
				fw.write("\r\n");
				fw.write("*Edges");
				for(int fwi = 0; fwi < idList.size(); fwi++){
					for(int fwj = 0; fwj < idList.size(); fwj++){
						int weight = matrix[idList.get(fwi)][idList.get(fwj)];
						for(int weightI = 0; weightI < weight; weightI++){
							fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
							fw.write((fwi+1) + " " + (fwj+1));
							U.print((fwi+1) + " " + (fwj+1));
						}
					}
				}
				fw.close();
			}
			U.print(i + "年写入完毕");
			
		}
		U.print("done");
	}
	
	//判断是否是A股公司
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
	
	//不同类型公司返回的颜色
	private static String getCompanyTypeColor(int companyType){
		if(companyType == M.COMPANYTYPE_B)
			return "Blue";
		else if(companyType == M.COMPANYTYPE_A)
			return "Red";
		return "Gray";
	}
	//不同地址公司返回的颜色
	private static String getAddressColor(String address){
		if(address == null)
			return "Black";
		else if(address.equals("上海"))
			return "Blue";
		else if(address.equals("深圳"))
			return "Orange";
		else if(address.equals("广州"))
			return "Gray";
		return "Black";
	}
	
	//返回公司城市
	private static String getCompanyAddress(String address){
		if(address.contains("上海"))
			return "上海";
		if(address.contains("深圳"))
			return "深圳";
		if(address.contains("广州"))
			return "广州";
		return "其它";
	}
}
