package com.myClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.db.ExcelFunction;
import com.myClass.POI.PoiExcel2k3Helper;
import com.myClass.POI.PoiExcel2k7Helper;
import com.myClass.POI.PoiExcelHelper;
import com.spreada.utils.chinese.ZHConverter;

public class U {
	
	//打印
	public static void print(String s){
		System.out.println(s);
	}
	public static void print(int i){
		System.out.println(i + "");
	}
	public static void print(String[] ss){
		String result = "";
		for(String s : ss){
			result += s;
			result += ",";
		}
		result = result.substring(0, result.length()-1);
		System.out.println(result);
	}
	
	
	
	//根据单元格不同属性返回字符串数值
	public static String getCellStringValue(HSSFCell cell) {      
        String cellValue = "";      
        switch (cell.getCellType()) {      
        case HSSFCell.CELL_TYPE_STRING://字符串类型   
            cellValue = cell.getStringCellValue();      
            if(cellValue.trim().equals("")||cellValue.trim().length()<=0)      
                cellValue=" ";      
            break;      
        case HSSFCell.CELL_TYPE_NUMERIC: //数值类型   
            cellValue = String.valueOf(cell.getNumericCellValue());      
            break;      
        case HSSFCell.CELL_TYPE_FORMULA: //公式   
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);      
            cellValue = String.valueOf(cell.getNumericCellValue());      
            break;      
        case HSSFCell.CELL_TYPE_BLANK:      
            cellValue=" ";      
            break;      
        case HSSFCell.CELL_TYPE_BOOLEAN:      
            break;      
        case HSSFCell.CELL_TYPE_ERROR:      
            break;      
        default:      
            break;      
        }      
        return cellValue;      
    }   
	public static String getCellStringValue(XSSFCell cell) {      
        String cellValue = "";      
        switch (cell.getCellType()) {      
        case XSSFCell.CELL_TYPE_STRING://字符串类型   
            cellValue = cell.getStringCellValue();      
            if(cellValue.trim().equals("")||cellValue.trim().length()<=0)      
                cellValue=" ";      
            break;      
        case XSSFCell.CELL_TYPE_NUMERIC: //数值类型   
            cellValue = String.valueOf(cell.getNumericCellValue());      
            break;      
        case XSSFCell.CELL_TYPE_FORMULA: //公式   
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);      
            cellValue = String.valueOf(cell.getNumericCellValue());      
            break;      
        case XSSFCell.CELL_TYPE_BLANK:      
            cellValue=" ";      
            break;      
        case XSSFCell.CELL_TYPE_BOOLEAN:      
            break;      
        case XSSFCell.CELL_TYPE_ERROR:      
            break;      
        default:      
            break;      
        }      
        return cellValue;      
    }
	
	
	
	//繁体字转简体字
	public static String ZHConverter_TraToSim(String tradStr) {
		ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
		String simplifiedStr = converter.convert(tradStr);
		return simplifiedStr;
	}
	//简体字转繁体字
	public static String ZHConverter_SimToTra(String simpStr) {
		ZHConverter converter = ZHConverter
				.getInstance(ZHConverter.TRADITIONAL);
		String traditionalStr = converter.convert(simpStr);
		return traditionalStr;
	}
	
	
	
	//数学计算
	//求和
	public static double MATH_getSum(List<Double> inputData) {
		if (inputData == null || inputData.size() == 0)
			return -1;
		int len = inputData.size();
		double sum = 0;
		for (int i = 0; i < len; i++) {
			sum = sum + inputData.get(i);
		}
		return sum;
	}
	//求平均数
	public static double MATH_getAverage(List<Double> inputData) {
		if (inputData == null || inputData.size() == 0)
			return -1;
		int len = inputData.size();
		double result = MATH_getSum(inputData) / len;;
		return result;
	}
	//求平方和
	public static double MATH_getSquareSum(List<Double> inputData) {
		if(inputData==null||inputData.size()==0)
		    return -1;
		int len=inputData.size();
		double sqrsum = 0.0;
		for (int i = 0; i <len; i++) {
			sqrsum = sqrsum + inputData.get(i) * inputData.get(i);
		}
		return sqrsum;
	}
	//求方差
	public static double MATH_getVariance(List<Double> inputData) {
		int count = inputData.size();
		double sqrsum = MATH_getSquareSum(inputData);
		double average = MATH_getAverage(inputData);
		double result = (sqrsum - count * average * average) / count;
		return result; 
	}
	//求标准差
	public static double MATH_getStandardDiviation(List<Double> inputData) {
		double result;
		//绝对值化很重要
		result = Math.sqrt(Math.abs(MATH_getVariance(inputData)));
		return result;
	}
	
	
	
	//将Map按值从高到低排序，返回TreeMap
	public static TreeMap<String, Integer> sortMap(Map<String, Integer> map){
		ValueComparator bvc =  new ValueComparator(map);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
        sorted_map.putAll(map);
        return sorted_map;
	}
	
	
	//求出保护list值的和
	public static int getSumList(List<Integer> list){
		int result = 0;
		for(int i = 0; i < list.size(); i++){
			result += list.get(i);
		}
		return result;
	}

	
	
	//判断是否是模糊词(披露不充分)s，如“关键管理人员”
	public static boolean needContinue(String name){
		if(name.length() < 4)//删除人名
			return true;
		if(name.equals(" ") || name.equals("")
			|| name.equals("子公司") || name.equals("控股子公司") || name.equals("关键关联人员") || name.equals("主要领导和关键岗位人员") || name.equals("子公司关键人员控制或影响的公司")
			|| name.equals("少数股东及其子公司") || name.equals("公司控股子公司") || name.equals("本公司的子公司")|| name.equals("各子公司")
			|| name.equals("公司的控股子公司") || name.equals("子公司关键人员控制或影响的公司") || name.equals("受同一母公司控制的公司")
			|| name.equals("受同一母公司控制") || name.equals("子公司少数股东") || name.equals("母公司之子公司")|| name.equals("经理")
			|| name.equals("其他子公司") || name.equals("本公司子公司") || name.equals("海外子公司")|| name.equals("财务总监")
			|| name.equals("其他关联方") || name.equals("其他") || name.equals("其他高级管理人员")|| name.equals("其他子公司")
			|| name.equals("Inc.") || name.equals("INC．") || name.equals("lnc.") || name.equals("INC.") || name.equals("Ltd.") || name.equals("LLC.") || name.equals("LTD.") || name.equals("Llc.") || name.equals("Llc.")
			|| name.equals("集团公司") || name.equals("企业年金") || name.equals("董监") || name.equals("上下游客户") || name.equals("石油公司")
			|| name.equals("其他受同一控股股东及最终控制方控制的其他企业") || name.equals("其他关联关系方") || name.equals("管理人")|| name.equals("关联自然人")
			|| name.equals("赫连剑茹") || name.equals("联营企业") || name.equals("关键关联人员") || name.equals("合营企业") || name.equals("合营公司")
			|| name.equals("新闻报社") || name.equals("公司子公司") || name.equals("电器营销") || name.equals("供应公司") || name.equals("所属子公司")
			|| name.equals("物流集团") || name.equals("瓦斯治理") || name.equals("物业公司") || name.equals("欧洲公司") || name.equals("集团内企业")
			|| name.equals("参股公司") || name.equals("矿业公司") || name.equals("全资子公司") || name.equals("其他小额") || name.equals("同系子公司")
			|| name.equals("企业缴存") || name.equals("个人缴存") || name.equals("联营公司") || name.equals("下属子公司") || name.equals("退休金供款")
			|| name.equals("五家供应商") || name.equals("李世江先生") || name.equals("工资社保") || name.equals("Kobe") || name.equals("Schio")
			|| name.equals("Italy") || name.equals("同母系公司")
			|| name.contains("特保") || name.contains("报酬") || name.contains("配偶") || name.contains("董事") || name.contains("薪酬")
			|| name.contains("关键") || name.contains("董事") || name.contains("本公司") || name.contains("本集团") || name.contains("人员")
			|| name.contains("股东") || name.contains("关联方") || name.contains("监事") || name.contains("夫妇") || name.contains("亲属")
			|| name.contains("控制人") || name.contains("自然人") || name.contains("板块") || name.contains("妻子") || name.contains("高管")
			|| name.contains("总经理") || name.contains("夫妻"))
			return true;
		return false;
	}
	//判断是否是A股公司
	public static boolean isA(String stockSymbol){
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
	public static String getCompanyTypeColor(int companyType){
		if(companyType == M.COMPANYTYPE_B)
			return "Blue";
		else if(companyType == M.COMPANYTYPE_A)
			return "Red";
		return "Gray";
	}
	//不同地址公司返回的颜色
	public static String getAddressColor(String address){
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
	public static String getCompanyAddress(String address){
		if(address.contains("上海"))
			return "上海";
		if(address.contains("深圳"))
			return "深圳";
		if(address.contains("广州"))
			return "广州";
		return "其它";
	}
	
	
	//根据阈值，返回网络中高于阈值的idList
	//这里的判断方式：该公司与n家公司发生关联交易，且n>=threshold，则通过。这里采用的是双向图
	//第一个参数是网络图矩阵，第二个参数是实际网络的节点数，第三个参数是阈值
	public static List<Integer> getIdList_ModeHowManyCompany(byte[][] matrix, int nodeCount, int threshold){
		List<Integer> idList = new ArrayList<>();//存放高于阈值的id
		for(int idi = 0; idi < nodeCount; idi++){
			int frequency = 0;
			for(int idj = 0; idj < nodeCount; idj++){
				//统计该公司出现的频率（目前仅适用于双向箭头）
				if(matrix[idi][idj] > 0)
					frequency += matrix[idi][idj];
			}
			if(frequency >= threshold)
				idList.add(idi);
		}
		return idList;
	}
	
	
	//得到公司网络，即得到一个matrix，包含了公司之间两两的关系
	//以形参的方式处理matrix，mapIdCompany，mapCompanyId
	//处理的是公司名和关联公司两格
	//第一个参数是网络矩阵，第二个参数是“id-公司名”map，第三个参数是“公司名-id”map，第四个参数是excel地址
	public static void getMatrix(byte[][] matrix, Map<Integer, String> mapIdCompany, Map<String, Integer> mapCompanyId, String path) throws IOException{
		int sheetNumbuer = ExcelFunction.getSheetNumber(path);
		int id = 0;//下标从0开始
		for(int i = 0; i < sheetNumbuer; i++){
			//读取一份excel，将其中公司两两的关系写入
			XSSFSheet sheet = ExcelFunction.getSheet_XSSF(path, i);
			int rowCount = sheet.getLastRowNum();
			for(int k = 1 ; k < rowCount ; k++){
				//访问公司名
				XSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
				//有些excel后面有空行
				if(cellCompanyName == null) break;
				String name = getCellStringValue(cellCompanyName).trim().replace(" ", "").replaceAll(" ", "");
				if(needContinue(name)) continue;//去掉两个空的公司名(中英文空格)
				if(mapCompanyId.get(name) == null){//如果该公司并不在map中，则为其添加一个id
					mapCompanyId.put(name, id);
					mapIdCompany.put(id, name);//同时为该id对应到company
					id++;
				}
				//访问关联公司
				XSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
				String asName = getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
				
				asName = asName.replaceAll(",", "、");//2014的excel中切割标示用的是','
				String[] names = asName.split("、");
				for(String n : names){
					if(needContinue(n)) continue;//去掉两个空的公司名(中英文空格)
					if(mapCompanyId.get(n) == null){//如果该公司并不在map中，则为其添加一个下标
						mapCompanyId.put(n, id);
						mapIdCompany.put(id, n);//同时为该id对应到company
						id++;
					}
					//绘制单向，由主体公司指向关联公司
					matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] += 1;//这里没有给线赋权
					matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] += 1;//双向箭头有两个矩阵格都需要+1
				}
			}
		}
	}
	public static void getMatrixHSSF(byte[][] matrix, Map<Integer, String> mapIdCompany, Map<String, Integer> mapCompanyId, String address, int mode) throws IOException{
		int id = 0;//下标从0开始
		int sheetNumbuer = ExcelFunction.getSheetNumber(address);
		for(int i = 0; i < sheetNumbuer; i++){
			//读取一份excel，将其中公司两两的关系写入
			HSSFSheet sheet = ExcelFunction.getSheet_HSSF(address, i);
			int rowCount = sheet.getLastRowNum();
			for(int k = 1 ; k < rowCount ; k++){
				if(mode == M.MODE_ONLYA){//仅A股模式下
					HSSFCell tempCell = sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol);
					String stockSymbol = U.getCellStringValue(tempCell).trim().replaceAll(" ", "");
					if(!U.isA(stockSymbol))
						continue;
				}
				//访问公司名
				HSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
				String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
				if(U.needContinue(name)) continue;//去掉“关键管理人员”、“董事”、空格等样本
				if(mapCompanyId.get(name) == null){//如果该公司并不在map中，则为其添加一个id
					mapCompanyId.put(name, id);
					mapIdCompany.put(id, name);//同时为该id对应到company
					id++;
				}
				//访问关联公司
				HSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
				String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
				
				asName = asName.replaceAll(",", "、");//2014的excel中切割标示用的是','
				String[] names = asName.split("、");
				for(String n : names){
					if(U.needContinue(n)) continue;//去掉“关键管理人员”、“董事”、空格等样本
					if(mapCompanyId.get(n) == null){//如果该公司并不在map中，则为其添加一个下标
						mapCompanyId.put(n, id);
						mapIdCompany.put(id, n);//同时为该id对应到company
						id++;
					}
					//绘制网络
					matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] += 1;
					matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] += 1;
				}
			}
		}
	}
	
	//获取excel每一个n个字段的值，以list<String>的方式返回
	public static List<List<String>> getRowsList(String fileName, int... fields) throws IOException{
		List<List<String>> lists = new ArrayList<List<String>>();
		PoiExcelHelper exHelper;  
        if(fileName.indexOf(".xlsx") != -1) {  
            exHelper = new PoiExcel2k7Helper();  
        }else {  
            exHelper = new PoiExcel2k3Helper();  
        } 
        int sheetNumbuer = exHelper.getSheetList(fileName).size();
        for(int i = 0; i < sheetNumbuer; i++){//这里要动态调节，有多个sheet才要 i<sheetNumber
			List<ArrayList<String>> tempLists = exHelper.readExcel(fileName, i);
			for(List<String> tempList : tempLists){
				List<String> list = new ArrayList<String>();
				for(int field : fields){
					String value = tempList.get(field);
					//思考下，到底要不要这里处理数据？
					if(field == M.EXCELINDEX_CompanyName && needContinue(value)) continue;//去掉“关键管理人员”等
					list.add(value);
				}
				if(list.size() < fields.length) continue;//小于，说明字段不完整
			lists.add(list);
			}
        }
		return lists;
	}
	
	//通过传入的数据字段，检查是否是某个性质（如是否是国营企业）
	//如传入“1011”与“M.Type_TransactionPurchase”，返回true，因为1011是“交易-购销”类型s
	//第一个参数是typeValue，第二个参数是需要判断是否属于的type
	public static boolean checkTypeValue(String typeValue, String type){
		boolean b = false;
		//交易类型
		if(type.equals(M.Type_TransactionPurchase) && (typeValue.equals("1011") || typeValue.equals("1012")))
			b = true;
		else if(type.equals(M.Type_TransactionGoodsPurchase) && (typeValue.equals("1011") || typeValue.equals("1012") || typeValue.equals("1041") || typeValue.equals("1042")))
			b = true;
		else if(type.equals(M.Type_TransactionSecured) && (typeValue.equals("1071") || typeValue.equals("1072")))
			b = true;
		else if(type.equals(M.Type_TransactionCapital) && (typeValue.equals("1061") || typeValue.equals("1062")))
			b = true;
		//企业性质
		else if(type.equals(M.Type_EquityOwnershipNation) && (typeValue.equals("0")))
			b = true;
		else if(type.equals(M.Type_EquityOwnershipPrivate) && typeValue.equals("1"))
			b = true;
		else if(type.equals(M.Type_EquityOwnershipForeign) && typeValue.equals("2"))
			b = true;
		return b;
	}
}
