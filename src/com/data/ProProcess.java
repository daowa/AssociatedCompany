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
		//从excel中获取数据
		List<List<String>> lists = new ArrayList<List<String>>();
		for(int i = 2011; i < 2015; i++){
			String fileName = "E:/work/关联公司/原始数据/" + i + ".xls";
			lists.addAll(U.getRowsList(fileName, M.EXCELINDEX_CompanyName, M.EXCELINDEX_AssociatedCompany));
		}
		
		//将获取到的数据进一步处理
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(int i = 0; i < lists.size(); i++){
			String company = lists.get(i).get(0);
			String associateCompanys = lists.get(i).get(1);
			//统计主体公司频数
			int countCompanys = map.get(company) == null ? 1 : map.get(company)+1;
			map.put(company, countCompanys);
			//统计关联公司频数
			associateCompanys = associateCompanys.replaceAll(",", "、");//2014的excel中切割标示用的是','
			String[] names = associateCompanys.split("、");
			for(String n : names){
				int countAssociateCompnay = map.get(n) == null ? 1 : map.get(n)+1;
				map.put(n, countAssociateCompnay);
			}
		}
		
		//将map按照value从大到小排序
        TreeMap<String, Integer> sorted_map = U.sortMap(map);
        
        FileFunction.writeMap_KV(sorted_map, "E:/work/关联公司/txt/companyAndFrequency.txt");//将公司名和出现频次输出
        FileFunction.writeMap_K(sorted_map, "E:/work/关联公司/txt/companyName.txt");//仅输出公司名
	}
	
	//统计4个excel中出现的公司（或人名）的类型，输出到txt并返回各个公司的类型
	public static Map<String, Integer> outputCompanyType() throws IOException{
		//从excel中获取数据
		List<List<String>> lists = new ArrayList<List<String>>();
		for(int i = 2011; i < 2015; i++){
			String fileName = "E:/work/关联公司/原始数据/" + i + ".xls";
			lists.addAll(U.getRowsList(fileName, M.EXCELINDEX_CompanyName, M.EXCELINDEX_StockSymbol));
		}
		
		//将获取到的数据进一步处理
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(int i = 0; i < lists.size(); i++){
			if(U.isA(lists.get(i).get(1)))
				map.put(lists.get(i).get(0), M.COMPANYTYPE_A);
			else
				map.put(lists.get(i).get(0), M.COMPANYTYPE_B);
		}

		FileFunction.writeMap_KV(map, "E:\\work\\关联公司\\txt\\companyType.txt");//输出公司类型
		return map;
	}
	
	//将各公司地址输出到txt
	public static Map<String, String> outputCompanyAddress() throws IOException{
		//从excel中获取数据
		List<List<String>> lists = new ArrayList<List<String>>();
		for(int i = 2011; i < 2015; i++){
			String fileName = "E:/work/关联公司/原始数据/" + i + ".xls";
			lists.addAll(U.getRowsList(fileName, M.EXCELINDEX_CompanyName, M.EXCELINDEX_Address));
		}
		
		//将获取到的数据进一步处理
		Map<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < lists.size(); i++){
			map.put(lists.get(i).get(0), U.getCompanyAddress(lists.get(i).get(1)));
		}
		
		//输出
		FileFunction.writeMap_KV(map, "E:\\work\\关联公司\\txt\\companyAddress.txt");//将 公司名-地址 输出到txt中
		return map;
	}
	
	public static void outputCompanyClassfiedType() throws IOException{
		String typeDescribe = "按行业";
		String[] types = {"建筑与房地产业关联交易", "批发零售关联交易", "制造业关联交易"};
//		String typeDescribe = "按企业性质";
//		String[] types = {"国企企业关联交易", "民营企业关联交易", "外资控股关联交易"};
//		String typeDescribe = "按交易类型";
//		String[] types = {"担保类关联交易--国企", "担保类关联交易--民营", "担保类关联交易--总库"};
//		String[] types = {"购销关联交易--国企", "购销关联交易--民营", "购销关联交易--总库"};
//		String[] types = {"资金往来关联交易--国企", "资金往来关联交易--民营", "资金往来关联交易--总库"};
		
		//从excel中获取数据
		List<List<String>> lists = new ArrayList<List<String>>();
		for(int i = 2011; i < 2015; i++){
			for(String type : types){
				String fileName = "E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + i + "\\" + typeDescribe + "\\" + i + type + ".xlsx";
				File file = new File(fileName);
				if(!file.exists()){
					U.print(fileName + "不存在");
					continue;
				}
				//增加type字段
				List<List<String>> tempLists = U.getRowsList(fileName, M.EXCELINDEX_CompanyName, M.EXCELINDEX_AssociatedCompany);
				for(int j = 0; j < tempLists.size(); j++){
					tempLists.get(j).add(type);
				}
				lists.addAll(tempLists);
			}
		}
		
		//处理数据
		Map<String, String> mapCompanyType = new HashMap<String, String>(); //记录公司名列表
		Map<String, String> mapRepeat = new HashMap<String, String>(); //记录重复的公司列表
		for(int i = 0; i < lists.size(); i++){
			String company = lists.get(i).get(0);
			String asCompnays = lists.get(i).get(1).replaceAll(",", "、"); //2014的excel中切割标示用的是',';
			String type = lists.get(i).get(2);
			//获取所有名字列表
			String tempCompanys = asCompnays + "、" + company;
			String[] names = tempCompanys.split("、");
			
			//遍历所有公司名进行处理
			for(String name : names){
				if(mapCompanyType.get(name) == null){ //如果该公司并不在map中，则为其添加一个type
					mapCompanyType.put(name, type);
				}
				else if(!mapCompanyType.get(name).equals(type)){
					if(mapRepeat.get(name) == null) //之前没记录过，则添加
						mapRepeat.put(name, mapCompanyType.get(name) + "/" + type);
					else if(!mapRepeat.get(name).contains(type)) //之前已经记录过了，就再不添加
						mapRepeat.put(name, mapRepeat.get(name) + "/" + type);
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
		for(int year = 2011; year < 2012; year++){
			//从excel中获取数据
			List<List<String>> lists = new ArrayList<List<String>>();
			for(String type : types){
				String fileName = "E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + year + "\\" + typeDescribe + "\\" + year + type + ".xlsx";
				File file = new File(fileName);
				if(!file.exists()){
					U.print(fileName + "不存在");
					continue;
				}
				//增加type字段
				List<List<String>> tempLists = U.getRowsList(fileName, M.EXCELINDEX_CompanyName, M.EXCELINDEX_AssociatedCompany);
				for(int j = 0; j < tempLists.size(); j++){
					tempLists.get(j).add(type);
				}
				lists.addAll(tempLists);
			}
			
			//处理数据
			Map<String, String> mapCompanyType = new HashMap<String, String>(); //记录公司名列表
			Map<String, String> mapRepeat = new HashMap<String, String>(); //记录重复的公司列表
			for(int i = 0; i < lists.size(); i++){
				String company = lists.get(i).get(0);
				String asCompnays = lists.get(i).get(1).replaceAll(",", "、"); //2014的excel中切割标示用的是',';
				String type = lists.get(i).get(2);
				//获取所有名字列表
				String tempCompanys = asCompnays + "、" + company;
				String[] names = tempCompanys.split("、");
				
				//遍历所有公司名进行处理
				for(String name : names){
					if(mapCompanyType.get(name) == null){ //如果该公司并不在map中，则为其添加一个type
						mapCompanyType.put(name, type);
					}
					else if(!mapCompanyType.get(name).equals(type)){
						if(mapRepeat.get(name) == null) //之前没记录过，则添加
							mapRepeat.put(name, mapCompanyType.get(name) + "/" + type);
						else if(!mapRepeat.get(name).contains(type)) //之前已经记录过了，就再不添加
							mapRepeat.put(name, mapRepeat.get(name) + "/" + type);
					}
				}
			}
			
			FileFunction.writeMap_KV(mapCompanyType, "E:\\work\\关联公司\\txt\\companyType_" + typeDescribe + year + ".txt");//将公司type写入txt
		}
	}
	
	
	//将关联公司写入txt
	public static void outputCompanyAssociate(int outputFormat,  int mode, boolean isOneWay, int threshold) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2011; i < 2012; i++){
			Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//记录每个公司所对应的id
			Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//记录每个id所对应的公司
			int index = 0;//下标从0开始
			byte[][] matrix = new byte[40000][40000];
			
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
			List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapCompanyId.size(), threshold);
			
			//将关联公司写入txt(不敢放在别处了，再复制一个matrix内存就满了)
			if(outputFormat == M.OUTPUTFORMAT_NETWeight){
				String address = "E:/work/关联公司/txt/nettxt_asCompany" + i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net";
				FileFunction.writeNet_Weight(idList, mapIdCompany, matrix, address);
			}
			else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){
				Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\关联公司\\txt\\companyType.txt");
				String address = "E:/work/关联公司/txt/cpType_asCompany" + i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net";
				FileFunction.writeNet_Color(idList, mapIdCompany, matrix, address, M.COLOR_COMPANYTYPE, map);
			}
			else if(outputFormat == M.OUTPUTFORMAT_ADDRESS){
				Map<String, String> map = FileFunction.readMap_SS("E:\\work\\关联公司\\txt\\companyAddress.txt");
				String address = "E:/work/关联公司/txt/cpAddress_asCompany" + i + "_" + isOneWay + "_" + threshold + "_" + mode + ".net";
				FileFunction.writeNet_Color(idList, mapIdCompany, matrix, address, M.COLOR_ADDRESS, map);
			}
			else if(outputFormat == M.OUTPUTFORMAT_STARCOMPANY){
				String star = "中外运空运发展股份有限公司";
				String address = "E:/work/关联公司/txt/StarCompany" + star + i + ".net";
				Map<String, String> map = new HashMap<>();
				map.put("star", star);
				FileFunction.writeNet_Color(idList, mapIdCompany, matrix, address, M.COLOR_STARCOMPANY, map);
			}
			U.print(i + "年写入完毕");
		}
		U.print("done");
	}
	
	//将按类型分的关联公司写入txt
	public static void outputByClassification(int outputFormat, int threshold) throws IOException{
		for(int year = 2011; year < 2012; year++){
			File file0 = new File("E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + year);
			String[] fileList0 = file0.list();
			for(String fileName : fileList0){
				File file1 = new File("E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + year + "\\" + fileName);
				String[] fileList1 = file1.list();
				for(String excelName :fileList1){//终于读取到excel文件啦..
					//数据存储准备
					U.print("开始读取" + excelName);
					Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//记录每个id所对应的公司
					Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//记录每个公司所对应的id
					byte[][] matrix = new byte[25265][25265];//最大25265个公司（2014年），开这么大的矩阵空间足够了
					String excelAddress = "E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + year + "\\" + fileName + "\\" + excelName;
					U.getMatrix(matrix, mapIdCompany, mapCompanyId, excelAddress);//获取网络矩阵
					
					//读取matrix，只选取高于阈值的公司id
					List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapCompanyId.size(), threshold);
					
					//输出.net文件
					String temp = "E:\\work\\关联公司\\txt\\双向图_无阈值\\" + year + "\\" + fileName + "\\" + excelName;
					if(outputFormat == M.OUTPUTFORMAT_NETWeight){//输出网络
						String address = temp.substring(0, temp.length()-4) + "net";
						FileFunction.writeNet_Weight(idList, mapIdCompany, matrix, address);
					}
					else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){//输出A股颜色
						Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\关联公司\\txt\\companyType.txt");
						String address = temp.substring(0, temp.length()-5) + "colorA.net";
						FileFunction.writeNet_Color(idList, mapIdCompany, matrix, address, M.COLOR_COMPANYTYPE, map);
					}
				}
			}
		}
		U.print("done");
	}
	
	//输出按系族分的公司关系表
	public static void outputByStrain(int outputFormat, int threshold) throws IOException{
			File file = new File("E:\\work\\关联公司\\原始数据\\系族分");
			String[] fileList = file.list();
			for(String excelName :fileList){
				//数据存储准备
				U.print("开始读取" + excelName);
				Map<String, Integer> mapCompanyId = new LinkedHashMap<String, Integer>();//记录每个公司所对应的id
				Map<Integer, String> mapIdCompany = new HashMap<Integer, String>();//记录每个id所对应的公司
				byte[][] matrix = new byte[1000][1000];
				String excelAddress = "E:\\work\\关联公司\\原始数据\\系族分\\" + excelName;
				U.getMatrix(matrix, mapIdCompany, mapCompanyId, excelAddress);//获取网络矩阵
					
				//读取matrix，只选取高于阈值的公司id
				List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapCompanyId.size(), threshold);
					
				//输出.net文件
				String temp = "E:\\work\\关联公司\\txt\\系族\\双向图\\" + excelName;
				if(outputFormat == M.OUTPUTFORMAT_NETWeight){//输出网络
					String address = temp.substring(0, temp.length()-4) + "net";
					FileFunction.writeNet_Weight(idList, mapIdCompany, matrix, address);
				}
				else if(outputFormat == M.OUTPUTFORMAT_COMPANYTYPE){//输出A股颜色
					Map<String, Integer> map = FileFunction.readMap_SI("E:\\work\\关联公司\\txt\\companyType.txt");
					String address = temp.substring(0, temp.length()-5) + "colorA.net";
					FileFunction.writeNet_Color(idList, mapIdCompany, matrix, address, M.COLOR_COMPANYTYPE, map);
				}
			}
		U.print("done");
	}
	
	//输出每一年的三个关系表（担保、购销、资金往来）
	public static void outputTransactionType(String type) throws IOException{
		HSSFCell cellCompanyName = null;
		HSSFCell cellAssociatedCompany = null;
		
		for(int i = 2011; i < 2015; i++){
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
			List<Integer> idList = U.getIdList_ModeHowManyCompany(matrix, mapCompanyId.size(), 0);
			
			//写入.net
			String address = "E:/work/关联公司/txt/TransactionType_" + type + "_" + i + ".net";
			FileFunction.writeNet_Weight(idList, mapIdCompany, matrix, address);
		}
		U.print("done");
	}
	
	
	
	
	
	
	
	//输出结构化的中心性分析的txt
	public static void outputCentrality(String txtName) throws IOException{
		FileFunction.writeCentrality(txtName);
	}
	//输出结构化的结构洞分析的txt
	public static void outputStructualHoles(String txtName) throws IOException{
		FileFunction.writeStructualHoles(txtName);
	}
	
	//输出公司分类
	public static void outputPartition(String classify, int year) throws NumberFormatException, IOException{
		List<String> cpList = FileFunction.readCompanyName("E:\\work\\关联公司\\txt\\nettxt_asCompany" + year + "_false_1_10.net");
		String address = "E:\\work\\关联公司\\txt\\partition_" + classify + "_" + year + ".txt";
		if(classify.equals(M.Classify_EquityOwnership)){
			Map<String, String> mapCompanyClassify = FileFunction.readMap_SS("E:\\work\\关联公司\\txt\\companyType_按企业性质.txt");
			Map<String, Integer> mapClassifyType = new HashMap<>();
			mapClassifyType.put("国企企业关联交易", 0);
			mapClassifyType.put("民营企业关联交易", 1);
			mapClassifyType.put("外资控股关联交易", 2);
			FileFunction.writePartition(cpList, mapCompanyClassify, mapClassifyType, address);
		}
		else if(classify.equals(M.Classify_Industry)){
			Map<String, String> mapCompanyClassify = FileFunction.readMap_SS("E:\\work\\关联公司\\txt\\companyType_按行业.txt");
			Map<String, Integer> mapClassifyType = new HashMap<>();
			mapClassifyType.put("建筑与房地产业关联交易", 0);
			mapClassifyType.put("制造业关联交易", 1);
			mapClassifyType.put("批发零售关联交易", 2);
			FileFunction.writePartition(cpList, mapCompanyClassify, mapClassifyType, address);
		}
		else if(classify.equals(M.Classify_TransactionType)){
			Map<String, String> mapCompanyClassify = FileFunction.readMap_SS("E:\\work\\关联公司\\txt\\companyType_按交易类型.txt");
			Map<String, Integer> mapClassifyType = new HashMap<>();
			mapClassifyType.put("担保类关联交易--国企", 0);
			mapClassifyType.put("民营企业关联交易", 1);
			mapClassifyType.put("外资控股关联交易", 2);
			FileFunction.writePartition(cpList, mapCompanyClassify, mapClassifyType, address);
		}
	}
	
}
