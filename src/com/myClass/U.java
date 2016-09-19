package com.myClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;

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
	
	
	
	//判断是否是模糊词，如“关键管理人员”
	public static boolean needContinue(String name){
		if(name.equals(" ")
			|| name.contains("关键") || name.contains("董事") || name.contains("本公司") || name.contains("本集团") || name.contains("人员") || name.contains("薪酬")
			|| name.equals("子公司") || name.equals("控股子公司") || name.equals("关键关联人员") || name.equals("主要领导和关键岗位人员") || name.equals("子公司关键人员控制或影响的公司")
			|| name.equals("少数股东及其子公司") || name.equals("公司控股子公司") || name.equals("本公司的子公司")|| name.equals("各子公司")
			|| name.equals("公司的控股子公司") || name.equals("子公司关键人员控制或影响的公司") || name.equals("受同一母公司控制的公司")
			|| name.equals("受同一母公司控制") || name.equals("子公司少数股东") || name.equals("母公司之子公司")|| name.equals("经理")
			|| name.equals("其他子公司") || name.equals("本公司子公司") || name.equals("海外子公司")|| name.equals("财务总监")
			|| name.equals("其他关联方") || name.equals("其他") || name.equals("其他高级管理人员")|| name.equals("其他子公司")
			|| name.equals("其他受同一控股股东及最终控制方控制的其他企业") || name.equals("其他关联关系方") || name.equals("管理人")|| name.equals("关联自然人")
			|| name.contains("董事") || name.equals("联营企业") || name.equals("关键关联人员") || name.equals("合营企业")
			|| name.contains("股东") || name.contains("关联方") || name.contains("监事"))
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
				if(matrix[idi][idj] != 0)
					frequency += matrix[idi][idj];
			}
			if(frequency >= threshold)
				idList.add(idi);
		}
		return idList;
	}

}
