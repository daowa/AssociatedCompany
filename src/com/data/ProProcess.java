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
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					count = mapCompany.get(name) == null ? 1 : mapCompany.get(name)+1;
					mapCompany.put(name, count);
					//添加关联公司名
					cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
					asName = asName.replaceAll(",", "、");//2014的excel中切割标示用的是','
					String[] names = asName.split("、");
					for(String n : names){
						count = mapCompany.get(n) == null ? 1 : mapCompany.get(n)+1;
						mapCompany.put(n, count);
					}
				}
			}
		}
		
		//将map按照value从大到小排序
        TreeMap<String, Integer> sorted_map = U.sortMap(mapCompany);
        
        FileFunction.writeMap_KV(sorted_map, "E:/work/关联公司/txt/companyAndFrequency.txt");//将公司名和出现频次输出
        FileFunction.writeMap_K(sorted_map, "E:/work/关联公司/txt/companyName.txt");//仅输出公司名
	}
	
	//统计4个excel中出现的公司（或人名）的类型，输出到txt并返回各个公司的类型
	public static Map<String, Integer> outputCompanyType() throws IOException{
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
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					//获取股票号码
					HSSFCell tempCell = sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol);
					String stockSymbol = U.getCellStringValue(tempCell).trim().replaceAll(" ", "");
					if(U.isA(stockSymbol))
						map.put(name, M.COMPANYTYPE_A);
					else
						map.put(name, M.COMPANYTYPE_B);
				}
			}
		}

		FileFunction.writeMap_KV(map, "E:\\work\\关联公司\\txt\\companyType.txt");//输出公司类型
		return map;
	}
	
	//将各公司地址输出到txt
	public static Map<String, String> outputCompanyAddress() throws IOException{
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
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					//获取地址
					HSSFCell tempCell = sheet.getRow(k).getCell(M.EXCELINDEX_Address);
					String address = U.getCellStringValue(tempCell).trim().replaceAll(" ", "");
					map.put(name, U.getCompanyAddress(address));
				}
			}
		}
		
		FileFunction.writeMap_KV(map, "E:\\work\\关联公司\\txt\\companyAddress.txt");//将 公司名-地址 输出到txt中
		return map;
	}
	
	public static void outputCompanyClassfiedType() throws IOException{
//		String typeDescribe = "按行业";
//		String[] types = {"建筑与房地产业关联交易", "批发零售关联交易", "制造业关联交易"};
		String typeDescribe = "按企业性质";
		String[] types = {"国企企业关联交易", "民营企业关联交易", "外资控股关联交易"};
//		String typeDescribe = "按交易类型";
//		String[] types = {"担保类关联交易--国企", "担保类关联交易--民营", "担保类关联交易--总库"};
//		String[] types = {"购销关联交易--国企", "购销关联交易--民营", "购销关联交易--总库"};
//		String[] types = {"资金往来关联交易--国企", "资金往来关联交易--民营", "资金往来关联交易--总库"};
		//记录公司名列表
		Map<String, String> mapCompanyType = new HashMap<String, String>();
		//记录重复的公司列表
		Map<String, String> mapRepeat = new HashMap<String, String>();
		for(int year = 2011; year <= 2014; year++){
			for(String type : types){
				U.print("开始读取" + year + type);
				//读取一份excel，将其中公司两两的关系写入
				String fileName = "E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + year + "\\" + typeDescribe + "\\" + year + type + ".xlsx";
				File file = new File(fileName);
				if(!file.exists()){
					U.print(fileName + "不存在");
					continue;
				}
				XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + year + "\\" + typeDescribe + "\\" + year + type + ".xlsx", 0);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//访问主公司
					XSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					//有些excel后面有空行
					if(cellCompanyName == null) break;
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					if(U.needContinue(name)) continue;//去掉“关键管理人员”、“董事”、空格等样本
					if(mapCompanyType.get(name) == null){//如果该公司并不在map中，则为其添加一个type
						mapCompanyType.put(name, type);
					}
					else if(!mapCompanyType.get(name).equals(type)){
						if(mapRepeat.get(name) == null)//之前没记录过，则添加
							mapRepeat.put(name, mapCompanyType.get(name) + "/" + type);
						//之前已经记录过了，就再不添加
						else if(!mapRepeat.get(name).contains(type))
							mapRepeat.put(name, mapRepeat.get(name) + "/" + type);
					}
					
					//访问关联公司
					XSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
					asName = asName.replaceAll(",", "、");//2014的excel中切割标示用的是','
					String[] names = asName.split("、");
					for(String n : names){
						if(U.needContinue(n)) continue;//去掉两个空的公司名(中英文空格)
						if(mapCompanyType.get(n) == null){//如果该公司并不在map中，则为其添加一个type
							mapCompanyType.put(n, type);
						}
						else if(!mapCompanyType.get(n).equals(type)){
							if(mapRepeat.get(n) == null)//之前没记录过，则添加
								mapRepeat.put(n, mapCompanyType.get(n) + "/" + type);
							//之前已经记录过了，就再不添加
							else if(!mapRepeat.get(n).contains(type))
								mapRepeat.put(n, mapRepeat.get(n) + "/" + type);
						}
					}
				}
			}
		}
		FileFunction.writeMap_KV(mapCompanyType, "E:\\work\\关联公司\\txt\\companyType_" + typeDescribe + ".txt");//将公司type写入txt
		FileFunction.writeMap_KV(mapRepeat, "E:\\work\\关联公司\\txt\\repeat_" + typeDescribe + ".txt");//将重复type写入txt
	}
	
	public static void outputCompanyClassfiedType_Year() throws IOException{
		String typeDescribe = "按行业";
		String[] types = {"建筑与房地产业关联交易", "批发零售关联交易", "制造业关联交易"};
//		String typeDescribe = "按企业性质";
//		String[] types = {"国企企业关联交易", "民营企业关联交易", "外资控股关联交易"};
//		String typeDescribe = "按交易类型";
//		String[] types = {"担保类关联交易--国企", "担保类关联交易--民营", "担保类关联交易--总库"};
//		String[] types = {"购销关联交易--国企", "购销关联交易--民营", "购销关联交易--总库"};
//		String[] types = {"资金往来关联交易--国企", "资金往来关联交易--民营", "资金往来关联交易--总库"};
		for(int year = 2011; year <= 2014; year++){
			Map<String, String> mapCompanyType = new HashMap<String, String>();//记录公司名列表
			Map<String, String> mapRepeat = new HashMap<String, String>();//记录重复的公司列表
			for(String type : types){
				U.print("开始读取" + year + type);
				//读取一份excel，将其中公司两两的关系写入
				String fileName = "E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + year + "\\" + typeDescribe + "\\" + year + type + ".xlsx";
				File file = new File(fileName);
				if(!file.exists()){
					U.print(fileName + "不存在");
					continue;
				}
				XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + year + "\\" + typeDescribe + "\\" + year + type + ".xlsx", 0);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//访问主公司
					XSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					//有些excel后面有空行
					if(cellCompanyName == null) break;
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					if(U.needContinue(name)) continue;//去掉“关键管理人员”、“董事”、空格等样本
					if(mapCompanyType.get(name) == null){//如果该公司并不在map中，则为其添加一个type
						mapCompanyType.put(name, type);
					}
					else if(!mapCompanyType.get(name).equals(type)){
						if(mapRepeat.get(name) == null)//之前没记录过，则添加
							mapRepeat.put(name, mapCompanyType.get(name) + "/" + type);
						//之前已经记录过了，就再不添加
						else if(!mapRepeat.get(name).contains(type))
							mapRepeat.put(name, mapRepeat.get(name) + "/" + type);
					}
					
					//访问关联公司
					XSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
					asName = asName.replaceAll(",", "、");//2014的excel中切割标示用的是','
					String[] names = asName.split("、");
					for(String n : names){
						if(U.needContinue(n)) continue;//去掉两个空的公司名(中英文空格)
						if(mapCompanyType.get(n) == null){//如果该公司并不在map中，则为其添加一个type
							mapCompanyType.put(n, type);
						}
						else if(!mapCompanyType.get(n).equals(type)){
							if(mapRepeat.get(n) == null)//之前没记录过，则添加
								mapRepeat.put(n, mapCompanyType.get(n) + "/" + type);
							//之前已经记录过了，就再不添加
							else if(!mapRepeat.get(n).contains(type))
								mapRepeat.put(n, mapRepeat.get(n) + "/" + type);
						}
					}
				}
			}
			FileFunction.writeMap_KV(mapCompanyType, "E:\\work\\关联公司\\txt\\companyType_" + typeDescribe + year + ".txt");//将公司type写入txt
		}
	}
	
	//从net中读取公司名列表
	private static List<String> readCompanyName(String path) throws NumberFormatException, IOException{
		List<String> list = new ArrayList<String>();
		File file = new File(path);
		InputStreamReader stream = new InputStreamReader(new FileInputStream(file));
		BufferedReader reader = new BufferedReader(stream);
		//读取共有多少个公司
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
	
	//将关联公司写入txt
	public static void outputCompanyAssociate(int outputFormat,  int mode, boolean isOneWay, int threshold) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2011; i <= 2014; i++){
			Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//记录每个公司所对应的id
			Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//记录每个id所对应的公司
			int index = 0;//下标从0开始
			byte[][] matrix = new byte[32767][32767];//UCINET最多支持那么多，超过那么多需要换个方法
			
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
						String stockSymbol = U.getCellStringValue(tempCell).trim().replaceAll(" ", "");
						if(!U.isA(stockSymbol))
							continue;
					}
					//访问公司名
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					if(U.needContinue(name)) continue;//去掉“关键管理人员”、“董事”、空格等样本
					if(mapCompanyId.get(name) == null){//如果该公司并不在map中，则为其添加一个id
						mapCompanyId.put(name, index);
						mapIdCompany.put(index, name);//同时为该id对应到company
						index++;
					}
					//访问关联公司
					cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
					
					asName = asName.replaceAll(",", "、");//2014的excel中切割标示用的是','
					String[] names = asName.split("、");
					for(String n : names){
						if(U.needContinue(n)) continue;//去掉“关键管理人员”、“董事”、空格等样本
						if(mapCompanyId.get(n) == null){//如果该公司并不在map中，则为其添加一个下标
							mapCompanyId.put(n, index);
							mapIdCompany.put(index, n);//同时为该id对应到company
							index++;
						}
						//绘制单向，由主体公司指向关联公司
						matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] = 1;
						if(!isOneWay)//如果要求双向箭头，则双向+1
							matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] = 1;
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
				}
				fw.write("\r\n");
				fw.write("*Edges");
				for(int fwi = 0; fwi < idList.size(); fwi++){
					for(int fwj = 0; fwj < idList.size(); fwj++){
						int weight = matrix[idList.get(fwi)][idList.get(fwj)];
						for(int weightI = 0; weightI < weight; weightI++){
							fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
							fw.write((fwi+1) + " " + (fwj+1));
						}
					}
				}
				fw.close();
			}
			else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){
				Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\关联公司\\txt\\companyType.txt");
				FileWriter fw = new FileWriter("E:/work/关联公司/txt/cpType_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net");
				fw.write("*Vertices " + idList.size());
				for(int fwi = 0; fwi < idList.size(); fwi++){
					fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
					String cpName = mapIdCompany.get(idList.get(fwi));
					fw.write((fwi+1) + " \"" + cpName + "\""
							+ " ic " + U.getCompanyTypeColor(map.get(cpName)!=null ? map.get(cpName) : M.COMPANYTYPE_NOIPO));
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
				Map<String, String> map = FileFunction.readMap_SS("E:\\work\\关联公司\\txt\\companyAddress.txt");
				FileWriter fw = new FileWriter("E:/work/关联公司/txt/cpAddress_asCompany"
						+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net");
				fw.write("*Vertices " + idList.size());
				for(int fwi = 0; fwi < idList.size(); fwi++){
					String cpName = mapIdCompany.get(idList.get(fwi));
					fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
					fw.write((fwi+1) + " \"" + cpName + "\""
							+ " ic " + U.getAddressColor(map.get(cpName)));
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
			else if(outputFormat == M.OUTPUTFORMAT_STARCOMPANY){
				String star = "中外运空运发展股份有限公司";
				FileWriter fw = new FileWriter("E:/work/关联公司/txt/StarCompany" + star + i + ".net");
				fw.write("*Vertices " + idList.size());
				for(int fwi = 0; fwi < idList.size(); fwi++){
					fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
					String cpName = mapIdCompany.get(idList.get(fwi));
					if(cpName.equals(star))
						fw.write((fwi+1) + " \"" + cpName + "\"" + " ic " + "Red");
					else
						fw.write((fwi+1) + " \"" + cpName + "\"" + " ic " + "Gray");
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
	
	//将按类型分的关联公司写入txt
	public static void outputByClassification(int threshold,int direction, int outputFormat) throws IOException{
		for(int year = 2011; year <= 2014; year++){
			File file0 = new File("E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + year);
			String[] fileList0 = file0.list();
			for(String fileName : fileList0){
				File file1 = new File("E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + year + "\\" + fileName);
				String[] fileList1 = file1.list();
				for(String excelName :fileList1){//终于读取到excel文件啦..
					//数据存储准备
					U.print("开始读取" + excelName);
					Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//记录每个公司所对应的id
					Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//记录每个id所对应的公司
					int id = 0;//下标从0开始
					byte[][] matrix = new byte[25265][25265];//最大25265个公司（2014年），开这么大的矩阵空间足够了
					
					//读取一份excel，将其中公司两两的关系写入
					XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + year + "\\" + fileName + "\\" + excelName, 0);
					int rowCount = sheet.getLastRowNum();
					for(int k = 1 ; k < rowCount ; k++){
						//访问公司名
						XSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
						//有些excel后面有空行
						if(cellCompanyName == null) break;
						String name = U.getCellStringValue(cellCompanyName).trim().replace(" ", "").replaceAll(" ", "");
						if(U.needContinue(name)) continue;//去掉两个空的公司名(中英文空格)
						if(mapCompanyId.get(name) == null){//如果该公司并不在map中，则为其添加一个id
							mapCompanyId.put(name, id);
							mapIdCompany.put(id, name);//同时为该id对应到company
							id++;
						}
						//访问关联公司
						XSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
						String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
						
						asName = asName.replaceAll(",", "、");//2014的excel中切割标示用的是','
						String[] names = asName.split("、");
						for(String n : names){
							if(U.needContinue(n)) continue;//去掉两个空的公司名(中英文空格)
							if(mapCompanyId.get(n) == null){//如果该公司并不在map中，则为其添加一个下标
								mapCompanyId.put(n, id);
								mapIdCompany.put(id, n);//同时为该id对应到company
								id++;
							}
							//绘制单向，由主体公司指向关联公司
							matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] = 1;//这里没有给线赋权
							if(direction == 2)//双向箭头有两个矩阵格都需要+1
								matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] = 1;
						}
					}
					
					//读取matrix，只选取高于阈值的公司id
					List<Integer> idList = new ArrayList<>();//存放高于阈值的id
					for(int idi = 0; idi < mapCompanyId.size(); idi++){
						int frequency = 0;
						for(int idj = 0; idj < mapCompanyId.size(); idj++){
							//统计该公司出现的频率
							if(matrix[idi][idj] != 0)
								frequency += matrix[idi][idj];
							//单向箭头阈值计算要看行和列
							if(direction == 1 && matrix[idj][idi] != 0)
								frequency += matrix[idj][idi];
						}
						if(frequency >= threshold)
							idList.add(idi);
					}
					
					//输出.net文件
					String temp = "";
					if(direction == 1)
						temp = "E:\\work\\关联公司\\txt\\单向图_无阈值\\" + year + "\\" + fileName + "\\" + excelName;
					else 
						temp = "E:\\work\\关联公司\\txt\\双向图_无阈值\\" + year + "\\" + fileName + "\\" + excelName;
					if(outputFormat == M.OUTPUTFORMAT_NETTXT){//输出网络
						FileWriter fw = new FileWriter(temp.substring(0, temp.length()-4) + "net");
						fw.write("*Vertices " + idList.size());
						for(int fwi = 0; fwi < idList.size(); fwi++){
							fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
							fw.write((fwi+1) + " \"" + mapIdCompany.get(idList.get(fwi)) + "\"");
						}
						fw.write("\r\n");
						fw.write("*Edges");
						for(int fwi = 0; fwi < idList.size(); fwi++){
							for(int fwj = 0; fwj < idList.size(); fwj++){
								int weight = matrix[idList.get(fwi)][idList.get(fwj)];
								for(int weightI = 0; weightI < weight; weightI++){
									fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
									fw.write((fwi+1) + " " + (fwj+1));
								}
							}
						}
						fw.close();
						U.print("已输出到" + temp.substring(0, temp.length()-4) + "net");
					}
					else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){//输出A股颜色
					Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\关联公司\\txt\\companyType.txt");
					FileWriter fw = new FileWriter(temp.substring(0, temp.length()-5) + "colorA.net");
					fw.write("*Vertices " + idList.size());
					for(int fwi = 0; fwi < idList.size(); fwi++){
						fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
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
								fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
								fw.write((fwi+1) + " " + (fwj+1));
							}
						}
					}
					fw.close();
					U.print("已输出到" + temp.substring(0, temp.length()-5) + "colorA.net");
					}
				}
			}
		}
		U.print("done");
	}
	
	//输出按系族分的公司关系表
	public static void outputByStrain(int threshold,int direction, int outputFormat) throws IOException{
			File file = new File("E:\\work\\关联公司\\原始数据\\系族分");
			String[] fileList = file.list();
			for(String fileName : fileList){
				for(String excelName :fileList){
					//数据存储准备
					U.print("开始读取" + excelName);
					Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//记录每个公司所对应的id
					Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//记录每个id所对应的公司
					int id = 0;//下标从0开始
					byte[][] matrix = new byte[1000][1000];
					
					//读取一份excel，将其中公司两两的关系写入
					XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\关联公司\\原始数据\\系族分\\" + excelName, 0);
					int rowCount = sheet.getLastRowNum();
					for(int k = 1 ; k < rowCount ; k++){
						//访问公司名
						XSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
						//有些excel后面有空行
						if(cellCompanyName == null) break;
						String name = U.getCellStringValue(cellCompanyName).trim().replace(" ", "").replaceAll(" ", "");
						if(U.needContinue(name)) continue;//去掉两个空的公司名(中英文空格)
						if(mapCompanyId.get(name) == null){//如果该公司并不在map中，则为其添加一个id
							mapCompanyId.put(name, id);
							mapIdCompany.put(id, name);//同时为该id对应到company
							id++;
						}
						//访问关联公司
						XSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
						String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
						
						asName = asName.replaceAll(",", "、");//2014的excel中切割标示用的是','
						String[] names = asName.split("、");
						for(String n : names){
							if(U.needContinue(n)) continue;//去掉两个空的公司名(中英文空格)
							if(mapCompanyId.get(n) == null){//如果该公司并不在map中，则为其添加一个下标
								mapCompanyId.put(n, id);
								mapIdCompany.put(id, n);//同时为该id对应到company
								id++;
							}
							//绘制单向，由主体公司指向关联公司
							matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] = 1;//这里没有给线赋权
							if(direction == 2)//双向箭头有两个矩阵格都需要+1
								matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] = 1;
						}
					}
					
					//读取matrix，只选取高于阈值的公司id
					List<Integer> idList = new ArrayList<>();//存放高于阈值的id
					for(int idi = 0; idi < mapCompanyId.size(); idi++){
						int frequency = 0;
						for(int idj = 0; idj < mapCompanyId.size(); idj++){
							//统计该公司出现的频率
							if(matrix[idi][idj] != 0)
								frequency += matrix[idi][idj];
							//单向箭头阈值计算要看行和列
							if(direction == 1 && matrix[idj][idi] != 0)
								frequency += matrix[idj][idi];
						}
						if(frequency >= threshold)
							idList.add(idi);
					}
					
					//输出.net文件
					String temp = "";
					if(direction == 1)
						temp = "E:\\work\\关联公司\\txt\\系族\\单向图\\" + excelName;
					else 
						temp = "E:\\work\\关联公司\\txt\\系族\\双向图\\" + excelName;
					if(outputFormat == M.OUTPUTFORMAT_NETTXT){//输出网络
						FileWriter fw = new FileWriter(temp.substring(0, temp.length()-4) + "net");
						fw.write("*Vertices " + idList.size());
						for(int fwi = 0; fwi < idList.size(); fwi++){
							fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
							fw.write((fwi+1) + " \"" + mapIdCompany.get(idList.get(fwi)) + "\"");
						}
						fw.write("\r\n");
						fw.write("*Edges");
						for(int fwi = 0; fwi < idList.size(); fwi++){
							for(int fwj = 0; fwj < idList.size(); fwj++){
								int weight = matrix[idList.get(fwi)][idList.get(fwj)];
								for(int weightI = 0; weightI < weight; weightI++){
									fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
									fw.write((fwi+1) + " " + (fwj+1));
								}
							}
						}
						fw.close();
						U.print("已输出到" + temp.substring(0, temp.length()-4) + "net");
					}
					else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){//输出A股颜色
						Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\关联公司\\txt\\companyType.txt");
					FileWriter fw = new FileWriter(temp.substring(0, temp.length()-5) + "colorA.net");
					fw.write("*Vertices " + idList.size());
					for(int fwi = 0; fwi < idList.size(); fwi++){
						fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
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
								fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
								fw.write((fwi+1) + " " + (fwj+1));
							}
						}
					}
					fw.close();
					U.print("已输出到" + temp.substring(0, temp.length()-5) + "colorA.net");
					}
				}
			}
		U.print("done");
	}
	
	//输出每一年的三个关系表（担保、购销、资金往来）
	public static void outputTransactionType(String type) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2011; i <= 2014; i++){
			Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//记录每个公司所对应的id
			Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//记录每个id所对应的公司
			int index = 0;//下标从0开始
			byte[][] matrix = new byte[32767][32767];//UCINET最多支持那么多，超过那么多需要换个方法
			
			//读取一份excel，将其中公司两两的关系写入
			String fileName = "E:/work/关联公司/原始数据/" + i + ".xls";
			int sheetNumber = ExcelFunction.getSheetNumber(fileName);
			U.print("开始读取:" + fileName);
			for(int j = 0; j < sheetNumber; j++){
				HSSFSheet sheet = ExcelFunction.getSheet(fileName, j);
				int rowCount = sheet.getLastRowNum();
				for(int k = 1 ; k < rowCount ; k++){
					//判断是否属于某种交易类型
					boolean yesPPG = false;//如果yes，则表示是该类型关系，可以写入
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
					//访问公司名
					cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
					String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
					if(U.needContinue(name)) continue;//去掉“关键管理人员”、“董事”、空格等样本
					if(mapCompanyId.get(name) == null){//如果该公司并不在map中，则为其添加一个id
						mapCompanyId.put(name, index);
						mapIdCompany.put(index, name);//同时为该id对应到company
						index++;
					}
					//访问关联公司
					cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
					String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
					
					asName = asName.replaceAll(",", "、");//2014的excel中切割标示用的是','
					String[] names = asName.split("、");
					for(String n : names){
						if(U.needContinue(n)) continue;//去掉“关键管理人员”、“董事”、空格等样本
						if(mapCompanyId.get(n) == null){//如果该公司并不在map中，则为其添加一个下标
							mapCompanyId.put(n, index);
							mapIdCompany.put(index, n);//同时为该id对应到company
							index++;
						}
						if(yesPPG){
							matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] = 1;
							matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] = 1;
						}
					}
				}
			}
			U.print("文件读取结束，开始写入txt");
			
			//读取matrix
			List<Integer> idList = new ArrayList<>();
			for(int idi = 0; idi < mapCompanyId.size(); idi++){
				idList.add(idi);
			}
			//写入.net
			FileWriter fw = new FileWriter("E:/work/关联公司/txt/TransactionType_" + type + "_" + i + ".net");
			fw.write("*Vertices " + idList.size());
			for(int fwi = 0; fwi < idList.size(); fwi++){
				fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
				fw.write((fwi+1) + " \"" + mapIdCompany.get(idList.get(fwi)) + "\"");
			}
			fw.write("\r\n");
			fw.write("*Edges");
			for(int fwi = 0; fwi < idList.size(); fwi++){
				for(int fwj = 0; fwj < idList.size(); fwj++){
					int weight = matrix[idList.get(fwi)][idList.get(fwj)];
					for(int weightI = 0; weightI < weight; weightI++){
						fw.write("\r\n");//为上一行补充换行，避免最后一行也换行了
						fw.write((fwi+1) + " " + (fwj+1));
					}
				}
			}
			fw.close();
		}
		U.print("done");
	}
	
	//输出结构化的中心性分析的txt
	public static void outputCentrality(int year) throws IOException{
		List<String> list = FileFunction.readFile("E:\\work\\关联公司\\txt\\中心度研究\\" + year + "_建筑与房地产.txt");
		List<String> output = new ArrayList<String>();
		for(int i = 15; i < list.size(); i++){
			String line = list.get(i);
			if(line.equals("")) break;
			output.add(line.replaceAll(" {2,}", ","));
		}
		FileWriter fw = new FileWriter("E:\\work\\关联公司\\txt\\中心度研究\\output\\result_" + year + "_建筑与房地产.txt");
		for(int i = 0; i < output.size(); i++){
			fw.write(output.get(i) + "\r\n");
		}
		fw.close();
		U.print("done");
	}
	//输出结构化的结构洞分析的txt
	public static void outputStructualHoles() throws IOException{
		String txtName = "2011_全数据";
		List<String> list = FileFunction.readFile("E:\\work\\关联公司\\txt\\结构洞研究\\" + txtName + ".txt");
		List<String> output = new ArrayList<String>();
		for(int i = 14; i < list.size(); i++){
			String line = list.get(i);
			if(line.equals("")) break;
			output.add(line.trim().replaceAll(" {2,}", ","));
		}
		FileWriter fw = new FileWriter("E:\\work\\关联公司\\txt\\结构洞研究\\output\\result_" + txtName + ".txt");
		for(int i = 0; i < output.size(); i++){
			fw.write(output.get(i) + "\r\n");
		}
		fw.close();
		U.print("done");
	}
	
	public static void outputPartition(String classify, int year) throws NumberFormatException, IOException{
		List<String> cpList = readCompanyName("E:\\work\\关联公司\\txt\\nettxt_asCompany" + year + "_false_1_10.net");
		FileWriter fw = new FileWriter("E:\\work\\关联公司\\txt\\partition_" + classify + "_" + year + ".txt");
		fw.write("dl nr = " + cpList.size() + ", nc = 1 format = edgelist2" + "\r\n");
		fw.write("row labels embedded" + "\r\n");
		fw.write("col labels embedded" + "\r\n");
		fw.write("data:" + "\r\n");
		if(classify.equals(M.Classify_EquityOwnership)){
			Map<String, String> map = FileFunction.readMap_SS("E:\\work\\关联公司\\txt\\companyType_按企业性质.txt");
			for(String cpName : cpList){
				cpName = cpName.trim().replaceAll(" ", "");
				int type = 0;
				if(map.get(cpName) == null)
					type = 3;
				else if(map.get(cpName).equals("国企企业关联交易"))
					type = 0;
				else if(map.get(cpName).equals("民营企业关联交易"))
					type = 1;
				else if(map.get(cpName).equals("外资控股关联交易"))
					type = 2;
				
				if(type == 3)
					U.print(cpName);
				
				fw.write(cpName + " type " + type + "\r\n");
			}
		}
		else if(classify.equals(M.Classify_Industry)){
			Map<String, String> map = FileFunction.readMap_SS("E:\\work\\关联公司\\txt\\companyType_按行业.txt");
			for(String cpName : cpList){
				cpName = cpName.trim().replaceAll(" ", "");
				int type = 0;
				if(map.get(cpName) == null)
					type = 3;
				else if(map.get(cpName).equals("建筑与房地产业关联交易"))
					type = 0;
				else if(map.get(cpName).equals("制造业关联交易"))
					type = 1;
				else if(map.get(cpName).equals("批发零售关联交易"))
					type = 2;
				
				if(type == 3)
					U.print(cpName);
				
				fw.write(cpName + " type " + type + "\r\n");
			}
		}
//		else if(classify.equals(M.Classify_TransactionType)){
//			Map<String, String> map = FileFunction.readMap_SS("E:\\work\\关联公司\\txt\\companyType_按交易类型.txt");
//			for(String cpName : cpList){
//				cpName = cpName.trim().replaceAll(" ", "");
//				int type = 0;
//				if(map.get(cpName) == null)
//					type = 3;
//				else if(map.get(cpName).equals("担保类关联交易--国企"))
//					type = 0;
//				else if(map.get(cpName).equals("民营企业关联交易"))
//					type = 1;
//				else if(map.get(cpName).equals("外资控股关联交易"))
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
