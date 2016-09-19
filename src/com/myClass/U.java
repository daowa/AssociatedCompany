package com.myClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;

import com.spreada.utils.chinese.ZHConverter;

public class U {
	
	//��ӡ
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
	
	
	
	//���ݵ�Ԫ��ͬ���Է����ַ�����ֵ
	public static String getCellStringValue(HSSFCell cell) {      
        String cellValue = "";      
        switch (cell.getCellType()) {      
        case HSSFCell.CELL_TYPE_STRING://�ַ�������   
            cellValue = cell.getStringCellValue();      
            if(cellValue.trim().equals("")||cellValue.trim().length()<=0)      
                cellValue=" ";      
            break;      
        case HSSFCell.CELL_TYPE_NUMERIC: //��ֵ����   
            cellValue = String.valueOf(cell.getNumericCellValue());      
            break;      
        case HSSFCell.CELL_TYPE_FORMULA: //��ʽ   
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
        case XSSFCell.CELL_TYPE_STRING://�ַ�������   
            cellValue = cell.getStringCellValue();      
            if(cellValue.trim().equals("")||cellValue.trim().length()<=0)      
                cellValue=" ";      
            break;      
        case XSSFCell.CELL_TYPE_NUMERIC: //��ֵ����   
            cellValue = String.valueOf(cell.getNumericCellValue());      
            break;      
        case XSSFCell.CELL_TYPE_FORMULA: //��ʽ   
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
	
	
	
	//������ת������
	public static String ZHConverter_TraToSim(String tradStr) {
		ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
		String simplifiedStr = converter.convert(tradStr);
		return simplifiedStr;
	}
	//������ת������
	public static String ZHConverter_SimToTra(String simpStr) {
		ZHConverter converter = ZHConverter
				.getInstance(ZHConverter.TRADITIONAL);
		String traditionalStr = converter.convert(simpStr);
		return traditionalStr;
	}
	
	
	
	//��ѧ����
	//���
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
	//��ƽ����
	public static double MATH_getAverage(List<Double> inputData) {
		if (inputData == null || inputData.size() == 0)
			return -1;
		int len = inputData.size();
		double result = MATH_getSum(inputData) / len;;
		return result;
	}
	//��ƽ����
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
	//�󷽲�
	public static double MATH_getVariance(List<Double> inputData) {
		int count = inputData.size();
		double sqrsum = MATH_getSquareSum(inputData);
		double average = MATH_getAverage(inputData);
		double result = (sqrsum - count * average * average) / count;
		return result; 
	}
	//���׼��
	public static double MATH_getStandardDiviation(List<Double> inputData) {
		double result;
		//����ֵ������Ҫ
		result = Math.sqrt(Math.abs(MATH_getVariance(inputData)));
		return result;
	}
	
	
	
	//��Map��ֵ�Ӹߵ������򣬷���TreeMap
	public static TreeMap<String, Integer> sortMap(Map<String, Integer> map){
		ValueComparator bvc =  new ValueComparator(map);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
        sorted_map.putAll(map);
        return sorted_map;
	}
	
	
	
	//�ж��Ƿ���ģ���ʣ��硰�ؼ�������Ա��
	public static boolean needContinue(String name){
		if(name.equals(" ")
			|| name.contains("�ؼ�") || name.contains("����") || name.contains("����˾") || name.contains("������") || name.contains("��Ա") || name.contains("н��")
			|| name.equals("�ӹ�˾") || name.equals("�ع��ӹ�˾") || name.equals("�ؼ�������Ա") || name.equals("��Ҫ�쵼�͹ؼ���λ��Ա") || name.equals("�ӹ�˾�ؼ���Ա���ƻ�Ӱ��Ĺ�˾")
			|| name.equals("�����ɶ������ӹ�˾") || name.equals("��˾�ع��ӹ�˾") || name.equals("����˾���ӹ�˾")|| name.equals("���ӹ�˾")
			|| name.equals("��˾�Ŀع��ӹ�˾") || name.equals("�ӹ�˾�ؼ���Ա���ƻ�Ӱ��Ĺ�˾") || name.equals("��ͬһĸ��˾���ƵĹ�˾")
			|| name.equals("��ͬһĸ��˾����") || name.equals("�ӹ�˾�����ɶ�") || name.equals("ĸ��˾֮�ӹ�˾")|| name.equals("����")
			|| name.equals("�����ӹ�˾") || name.equals("����˾�ӹ�˾") || name.equals("�����ӹ�˾")|| name.equals("�����ܼ�")
			|| name.equals("����������") || name.equals("����") || name.equals("�����߼�������Ա")|| name.equals("�����ӹ�˾")
			|| name.equals("������ͬһ�عɹɶ������տ��Ʒ����Ƶ�������ҵ") || name.equals("����������ϵ��") || name.equals("������")|| name.equals("������Ȼ��")
			|| name.contains("����") || name.equals("��Ӫ��ҵ") || name.equals("�ؼ�������Ա") || name.equals("��Ӫ��ҵ")
			|| name.contains("�ɶ�") || name.contains("������") || name.contains("����"))
			return true;
		return false;
	}
	//�ж��Ƿ���A�ɹ�˾
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
	//��ͬ���͹�˾���ص���ɫ
	public static String getCompanyTypeColor(int companyType){
		if(companyType == M.COMPANYTYPE_B)
			return "Blue";
		else if(companyType == M.COMPANYTYPE_A)
			return "Red";
		return "Gray";
	}
	//��ͬ��ַ��˾���ص���ɫ
	public static String getAddressColor(String address){
		if(address == null)
			return "Black";
		else if(address.equals("�Ϻ�"))
			return "Blue";
		else if(address.equals("����"))
			return "Orange";
		else if(address.equals("����"))
			return "Gray";
		return "Black";
	}
	//���ع�˾����
	public static String getCompanyAddress(String address){
		if(address.contains("�Ϻ�"))
			return "�Ϻ�";
		if(address.contains("����"))
			return "����";
		if(address.contains("����"))
			return "����";
		return "����";
	}
	
	
	//������ֵ�����������и�����ֵ��idList
	//������жϷ�ʽ���ù�˾��n�ҹ�˾�����������ף���n>=threshold����ͨ����������õ���˫��ͼ
	//��һ������������ͼ���󣬵ڶ���������ʵ������Ľڵ�������������������ֵ
	public static List<Integer> getIdList_ModeHowManyCompany(byte[][] matrix, int nodeCount, int threshold){
		List<Integer> idList = new ArrayList<>();//��Ÿ�����ֵ��id
		for(int idi = 0; idi < nodeCount; idi++){
			int frequency = 0;
			for(int idj = 0; idj < nodeCount; idj++){
				//ͳ�Ƹù�˾���ֵ�Ƶ�ʣ�Ŀǰ��������˫���ͷ��
				if(matrix[idi][idj] != 0)
					frequency += matrix[idi][idj];
			}
			if(frequency >= threshold)
				idList.add(idi);
		}
		return idList;
	}

}
