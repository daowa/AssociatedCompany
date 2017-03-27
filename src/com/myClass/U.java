package com.myClass;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public static void print(double d){
		System.out.println(d + "");
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
        	DecimalFormat df = new DecimalFormat("0");  
            cellValue = df.format(cell.getNumericCellValue()); 
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
	//四舍五入
	public static int MATH_getRounding(double d){
		int result = d-(int)d-0.5 > 0 ? (int)d+1 : (int)d;
		return result;
	}
	
	
	
	//将Map按值从高到低排序，返回TreeMap
	public static TreeMap<String, Integer> sortMap(Map<String, Integer> map){
		ValueComparator bvc =  new ValueComparator(map);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
        sorted_map.putAll(map);
        return sorted_map;
	}
	public static TreeMap<String, Double> sortMap2(Map<String, Double> map){
		ValueComparator2 bvc =  new ValueComparator2(map);
        TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
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
			|| name.equals("Italy") || name.equals("同母系公司") || name.equals("商品房购承人")
			|| name.contains("特保") || name.contains("报酬") || name.contains("配偶") || name.contains("董事") || name.contains("薪酬")
			|| name.contains("关键") || name.contains("董事") || name.contains("本公司") || name.contains("本集团") || name.contains("人员")
			|| name.contains("股东") || name.contains("关联方") || name.contains("监事") || name.contains("夫妇") || name.contains("亲属")
			|| name.contains("控制人") || name.contains("自然人") || name.contains("板块") || name.contains("妻子") || name.contains("高管")
			|| name.contains("总经理") || name.contains("夫妻") || name.contains("经济利润奖金") || name.contains("最终母公司") || name.contains("赵洁红"))
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
		return false;
	}
	//判断是否是B股公司
	public static boolean isB(String stockSymbol){
		String firstThree = stockSymbol.substring(0,3);
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
	//第一个参数是网络图矩阵，第二个参数是实际网络的节点数，第三个参数是阈值，第四个参数表示是否是有向图
	public static List<Integer> getIdList_ModeHowManyCompany(byte[][] matrix, int nodeCount, int threshold, boolean direct){
		List<Integer> idList = new ArrayList<>();//存放高于阈值的id
		if(!direct){
			for(int idi = 0; idi < nodeCount; idi++){
				int frequency = 0;
				for(int idj = 0; idj < nodeCount; idj++){
					//统计该公司出现的频率（目前仅适用于双向箭头）
					if(matrix[idi][idj] > 0)
						frequency += 1;
				}
				if(frequency >= threshold)
					idList.add(idi);
			}
		}
		else{
			for(int idi = 0; idi < nodeCount; idi++){
				int frequency = 0;
				for(int idj = 0; idj < nodeCount; idj++){
					//统计该公司出现的频率
					//同时统计一个id所在的行和列
					if(matrix[idi][idj] > 0)
						frequency += 1;
					if(matrix[idj][idi] > 0)
						frequency += 1;
				}
				if(frequency >= threshold)
					idList.add(idi);
			}
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
	
	//获取excel每一个n个字段的值，以list<list<String>>的方式返回
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
	public static boolean checkTypeValue(String typeValue, String type, List<String>... lists){
		boolean b = false;
		//交易类型
		if(type.equals(M.Type_TransactionAll))
			b = true;
		else if(type.equals(M.Type_TransactionPurchase) && (typeValue.equals("1011") || typeValue.equals("1012")))
			b = true;
		else if(type.equals(M.Type_TransactionGoodsPurchase) && (typeValue.equals("1011") || typeValue.equals("1012") || typeValue.equals("1041") || typeValue.equals("1042")))
			b = true;
		else if(type.equals(M.Type_TransactionSecured) && (typeValue.equals("1071") || typeValue.equals("1072")))
			b = true;
		else if(type.equals(M.Type_TransactionCapital) && (typeValue.equals("1061") || typeValue.equals("1062")))
			b = true;
		//企业性质
		else if(type.equals(M.Type_EquityOwnershipAll))
			b = true;
		else if(type.equals(M.Type_EquityOwnershipNation) && typeValue.equals("0"))
			b = true;
		else if(type.equals(M.Type_EquityOwnershipPrivate) && typeValue.equals("1"))
			b = true;
		else if(type.equals(M.Type_EquityOwnershipForeign) && typeValue.equals("2"))
			b = true;
		//央企
		else if(type.equals(M.Type_EquityOwnershipYangQi) && lists[0].contains(typeValue))
			b = true;
		//指定子网络（按公司名）
		else if(type.equals(M.Type_EquityOwnershipSubNet) && lists[1].contains(typeValue))
			b = true;
		//指定子网络（按公司股票代码）
		else if(type.equals(M.Type_EquityOwnerShipSubNet_Symbol) && lists[2].contains(typeValue))
			b = true;
		//所处行业
		else if(type.equals(M.Type_IndustryRealty) && (typeValue.contains("J") || typeValue.contains("K")))//K是社会服务业，万科和招商地产以及一些物业公司都在这里
			b = true;
		return b;
	}
	
	
	
	//获得央企的股票编号
    public static List<String> getYangQiStockSymbol(List<String> rawList){
    	List<String> result = new ArrayList<>();
    	for(int i = 0; i < rawList.size(); i++){
    		Pattern pattern = Pattern.compile("[0-9]{6}");
    		Matcher matcher = pattern.matcher(rawList.get(i));
    		if(matcher.find())
    			result.add(matcher.group(0));
    	}
    	return result;
    }
    
    //判断交易方向
    public static boolean directFromListCompany(String transcationType){
    	if(Integer.parseInt(transcationType) % 2 == 0)
    		return false;
    	return true;
    }
    
    //将币值都转换为元
    //2016年11月18日汇率
    public static int getRMB(int money, String currency){
    	double result = 0;
    	if(currency.equals("0"))
    		result = money;
    	else if(currency.equals("1"))//美元
    		result = money * 6.8856;
    	else if(currency.equals("2"))//港币
    		result = money * 0.8877;
    	else if(currency.equals("3"))//日元
    		result = money * 0.0622;
    	else if(currency.equals("4"))//欧元
    		result = money * 7.2925;
//    	else if(currency.equals("5"))//法郎
//    		result = money * 6.8249;
    	else if(currency.equals("6"))//马克
    		result = money * 3.8691;
    	else if(currency.equals("7"))//卢布
    		result = money * 0.1056;
    	else if(currency.equals("8"))//瑞士法郎
    		result = money *6.8249;
    	else if(currency.equals("9"))//澳大利亚元
    		result = money * 5.0875;
    	else if(currency.equals("10"))//新加坡元
    		result = money * 4.8344;
//    	else if(currency.equals("11"))//苏里南盾
//    		result = money * 6.8856;
    	else if(currency.equals("12"))//英镑
    		result = money * 8.5535;
    	else if(currency.equals("13"))//印度卢比
    		result = money * 0.1011;
    	else if(currency.equals("14"))//泰国铢
    		result = money * 0.1936;
    	else if(currency.equals("15"))//塞浦路斯镑
    		result = money * 13.2728;
    	else if(currency.equals("16"))//捷克克朗
    		result = money * 0.2705;
    	else if(currency.equals("17"))//挪威克朗
    		result = money * 0.8028;
    	else if(currency.equals("18"))//瑞典克朗
    		result = money * 0.7440;
    	else if(currency.equals("19"))//澳门元
    		result = money * 0.8629;
    	else if(currency.equals("20"))//巴西雷亚尔
    		result = money * 2.0070;
    	else if(currency.equals("21"))//匈牙利福林
    		result = money * 0.0236;
    	else if(currency.equals("22"))//兰特
    		result = money * 0.4722;
    	else if(currency.equals("23"))//基那
    		result = money * 2.1843;
    	else if(currency.equals("24"))//加拿大元
    		result = money * 5.0909;
    	else if(currency.equals("25"))//马拉西亚林吉特
    		result = money * 1.5605;
    	else if(currency.equals("26"))//荷兰盾
    		result = money * 3.4294;
    	else if(currency.equals("27"))//图格里克
    		result = money * 0.0029;
    	else if(currency.equals("28"))//哈萨克斯坦
    		result = money * 0.01996;
    	else if(currency.equals("29"))//韩元
    		result = money * 0.0058;
    	else if(currency.equals("30"))//菲律宾比索
    		result = money * 0.1386;
    	return (int)result;
    }
    
    
    //从公司名中提取地区
    public static String getDistrict(String name, List<String> listDistrict, Map<String, String> mapCityDistrict){
    	for(String district : listDistrict){//先用省进行判断
    		if(name.contains(district))
    			return district;
    	}
    	for(String key : mapCityDistrict.keySet()){//如果没有省份，那就用城市去判断
    		if(name.contains(key)){
    			return mapCityDistrict.get(key);
    		}
    	}
    	return "";
    }
    
    //去除地区名里的“省、市、自治区”等
    public static String cleanDistrict(String str){
    	str = str.trim().replaceAll(" ", "").replaceAll("省", "").replaceAll("自治区", "").replaceAll("市", "").replaceAll("壮族", "")
    			.replaceAll("回族", "").replaceAll("维吾尔", "");
    		return str;
    }
    //地区名称统一
	public static String cleanCity(String str){
	str = str.trim().replaceAll("汨罗", "岳阳").replaceAll("胶州", "青岛").replaceAll("黄岩", "台州")
		.replaceAll("新泰", "泰安").replaceAll("通县", "北京").replaceAll("张家港", "苏州").replaceAll("龙口", "烟台").replaceAll("綦江县", "重庆")
		.replaceAll("石景山", "北京").replaceAll("汉沽", "天津").replaceAll("老河口", "襄阳").replaceAll("株州", "株洲").replaceAll("南汇", "浦东新")
		.replaceAll("丰台", "北京").replaceAll("顺义", "北京").replaceAll("巴南区", "重庆").replaceAll("城口县", "重庆").replaceAll("蛟河", "吉林")
		.replaceAll("常熟", "苏州").replaceAll("浑江", "白山").replaceAll("南岸区", "重庆").replaceAll("河西", "天津").replaceAll("黔江", "重庆")
		.replaceAll("青州", "潍坊").replaceAll("潼南", "重庆").replaceAll("凭祥", "崇左").replaceAll("诸暨", "绍兴").replaceAll("海淀", "北京")
		.replaceAll("义马", "三门峡").replaceAll("马尾", "福州").replaceAll("巴音", "阿拉善左旗").replaceAll("六盘山", "六盘水").replaceAll("慈溪", "宁波")
		.replaceAll("永安", "三明").replaceAll("沁阳", "焦作").replaceAll("大庸", "张家界").replaceAll("诸城", "潍坊").replaceAll("宜兴", "无锡")
		.replaceAll("阿拉善盟", "阿拉善左旗").replaceAll("梅河口", "通化").replaceAll("垫江", "重庆").replaceAll("昆山", "苏州").replaceAll("开原", "铁岭")
		.replaceAll("石河子", "石河子").replaceAll("金桥", "浦东新").replaceAll("淮阴", "淮安").replaceAll("瑞安", "温州").replaceAll("枣阳", "襄阳")
		.replaceAll("静海", "天津").replaceAll("江山", "衢州").replaceAll("滕州", "枣庄").replaceAll("曲阜", "济宁").replaceAll("巢湖", "合肥")
		.replaceAll("石狮", "泉州").replaceAll("东郊", "天津").replaceAll("川沙" , "浦东新").replaceAll("襄樊", "襄阳").replaceAll("丰都县", "重庆")
		.replaceAll("瓦房店", "大连").replaceAll("玉林", "南宁").replaceAll("忠县", "重庆").replaceAll("江北区", "重庆").replaceAll("顺德", "佛山")
		.replaceAll("门头沟", "北京").replaceAll("昌平", "北京").replaceAll("满州里", "呼伦贝尔").replaceAll("达县", "达州").replaceAll("迪庆", "香格里拉")
		.replaceAll("富锦", "佳木斯").replaceAll("即墨", "青岛").replaceAll("卢湾", "黄浦").replaceAll("宣武区", "北京").replaceAll("广汉", "德阳")
		.replaceAll("酉阳土家族苗族自治县", "重庆").replaceAll("阿城", "哈尔滨").replaceAll("松花江", "哈尔滨").replaceAll("张家港", "苏州").replaceAll("西城区", "北京")
		.replaceAll("宿县", "宿州").replaceAll("滁县", "滁州").replaceAll("莱阳", "烟台").replaceAll("郧阳", "十堰").replaceAll("辉县", "新乡")
		.replaceAll("崇文区", "北京").replaceAll("南川", "重庆").replaceAll("鄂西", "襄阳").replaceAll("沉阳", "沈阳").replaceAll("湘乡", "湘潭")
		.replaceAll("哲里木盟", "通辽").replaceAll("塘沽", "天津").replaceAll("天竺出口加工区", "北京").replaceAll("丹阳", "镇江").replaceAll("醴陵", "株洲")
		.replaceAll("九龙坡区", "重庆").replaceAll("二连", "锡林郭勒盟").replaceAll("仪征", "扬州").replaceAll("漕河泾", "徐汇区").replaceAll("思茅", "普洱")
		.replaceAll("铁力", "伊春").replaceAll("铁法", "铁岭").replaceAll("余姚", "宁波").replaceAll("零陵", "永州").replaceAll("密山", "鸡西")
		.replaceAll("海宁", "嘉兴").replaceAll("集安", "通化").replaceAll("江油", "绵阳").replaceAll("平度", "青岛").replaceAll("都江堰", "成都")
		.replaceAll("兴化", "泰州").replaceAll("甘南", "齐齐哈尔").replaceAll("北票", "朝阳").replaceAll("东台", "盐城").replaceAll("来阳", "衡阳")
		.replaceAll("番禹", "广州").replaceAll("惠民", "滨州").replaceAll("文登", "威海").replaceAll("外高桥保税区", "浦东新").replaceAll("卫辉", "新乡")
		.replaceAll("九台", "长春").replaceAll("东兴", "防城港").replaceAll("太仓", "苏州").replaceAll("浦东", "浦东新").replaceAll("瑞昌", "九江")
		.replaceAll("荣城", "威海").replaceAll("武安", "邯郸").replaceAll("义乌", "金华").replaceAll("图们", "延边").replaceAll("海城", "鞍山")
		.replaceAll("锦西", "葫芦岛").replaceAll("兰溪", "金华").replaceAll("东阳", "金华").replaceAll("奉化", "宁波").replaceAll("漕河泾出口加工区", "徐汇区")
		.replaceAll("伊克昭盟", "鄂尔多斯").replaceAll("延边", "延吉").replaceAll("萧山", "杭州").replaceAll("黔西南", "兴义").replaceAll("黔东南", "凯里")
		.replaceAll("黔南", "都匀").replaceAll("南海", "佛山").replaceAll("银南", "固原").replaceAll("同江", "佳木斯市").replaceAll("黔东南", "凯里")
		.replaceAll("吴江", "苏州").replaceAll("兴城", "葫芦岛").replaceAll("启东", "南通").replaceAll("禹州", "许昌").replaceAll("梧州", "贺州")
		.replaceAll("绥芬河", "牡丹江").replaceAll("渮泽", "菏泽").replaceAll("启东", "南通").replaceAll("禹州", "许昌").replaceAll("梧州", "贺州");
	return str;
}
	
	
	
	
	
	//将两个字符串按照排序返回
	public static String getCompareString(String s1, String s2){
		if(s1.compareTo(s2) < 0)
			return s1 + "," + s2;
		return s2 + "," + s1;
	}
	
	
	
	
	
	
	//根据新加入的记录，向map中添加计数
	public static void mapAddCount(Map<String, Integer> map, String s){
		map.put(s, map.get(s) == null ? 1 : map.get(s)+1);
	}
	
	
	
	
	
	
	
	
	//获取map中topN所占比例的记录
	public static List<String> getMapTopPercentage(Map<String, Integer> map, int limit){
		List<String> result = new ArrayList<>();
		//先计算总数
		double all = 0;
		for(Entry<String, Integer> entry : map.entrySet())
			all += entry.getValue();
		//取topN(注意，这里不一定能取到N个)
		TreeMap<String, Integer> sort = U.sortMap(map);
		for(Entry<String, Integer> entry : sort.entrySet()){
			if(limit-- == 0) break;
			result.add(entry.getKey() + ":" + (entry.getValue()/all));
		}
		return result;
	}
	
	//计算信息熵
	public static double getComentropy(Map<String, Integer> map){
		//先计算总数
		double all = 0;
		for(Entry<String, Integer> entry : map.entrySet())
			all += entry.getValue();
		//计算信息熵
		double comentropy = 0;
		for(Entry<String, Integer> entry : map.entrySet()){
			double p = (double)entry.getValue()/all;
			comentropy -= p * (Math.log(p)/Math.log(2));
		}
		return comentropy;
	}
    
}
