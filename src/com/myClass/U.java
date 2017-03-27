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
	
	//��ӡ
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
        	DecimalFormat df = new DecimalFormat("0");  
            cellValue = df.format(cell.getNumericCellValue()); 
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
	//��������
	public static int MATH_getRounding(double d){
		int result = d-(int)d-0.5 > 0 ? (int)d+1 : (int)d;
		return result;
	}
	
	
	
	//��Map��ֵ�Ӹߵ������򣬷���TreeMap
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
	
	//�������listֵ�ĺ�
	public static int getSumList(List<Integer> list){
		int result = 0;
		for(int i = 0; i < list.size(); i++){
			result += list.get(i);
		}
		return result;
	}

	
	
	//�ж��Ƿ���ģ����(��¶�����)s���硰�ؼ�������Ա��
	public static boolean needContinue(String name){
		if(name.length() < 4)//ɾ������
			return true;
		if(name.equals(" ") || name.equals("")
			|| name.equals("�ӹ�˾") || name.equals("�ع��ӹ�˾") || name.equals("�ؼ�������Ա") || name.equals("��Ҫ�쵼�͹ؼ���λ��Ա") || name.equals("�ӹ�˾�ؼ���Ա���ƻ�Ӱ��Ĺ�˾")
			|| name.equals("�����ɶ������ӹ�˾") || name.equals("��˾�ع��ӹ�˾") || name.equals("����˾���ӹ�˾")|| name.equals("���ӹ�˾")
			|| name.equals("��˾�Ŀع��ӹ�˾") || name.equals("�ӹ�˾�ؼ���Ա���ƻ�Ӱ��Ĺ�˾") || name.equals("��ͬһĸ��˾���ƵĹ�˾")
			|| name.equals("��ͬһĸ��˾����") || name.equals("�ӹ�˾�����ɶ�") || name.equals("ĸ��˾֮�ӹ�˾")|| name.equals("����")
			|| name.equals("�����ӹ�˾") || name.equals("����˾�ӹ�˾") || name.equals("�����ӹ�˾")|| name.equals("�����ܼ�")
			|| name.equals("����������") || name.equals("����") || name.equals("�����߼�������Ա")|| name.equals("�����ӹ�˾")
			|| name.equals("Inc.") || name.equals("INC��") || name.equals("lnc.") || name.equals("INC.") || name.equals("Ltd.") || name.equals("LLC.") || name.equals("LTD.") || name.equals("Llc.") || name.equals("Llc.")
			|| name.equals("���Ź�˾") || name.equals("��ҵ���") || name.equals("����") || name.equals("�����οͻ�") || name.equals("ʯ�͹�˾")
			|| name.equals("������ͬһ�عɹɶ������տ��Ʒ����Ƶ�������ҵ") || name.equals("����������ϵ��") || name.equals("������")|| name.equals("������Ȼ��")
			|| name.equals("��������") || name.equals("��Ӫ��ҵ") || name.equals("�ؼ�������Ա") || name.equals("��Ӫ��ҵ") || name.equals("��Ӫ��˾")
			|| name.equals("���ű���") || name.equals("��˾�ӹ�˾") || name.equals("����Ӫ��") || name.equals("��Ӧ��˾") || name.equals("�����ӹ�˾")
			|| name.equals("��������") || name.equals("��˹����") || name.equals("��ҵ��˾") || name.equals("ŷ�޹�˾") || name.equals("��������ҵ")
			|| name.equals("�ιɹ�˾") || name.equals("��ҵ��˾") || name.equals("ȫ���ӹ�˾") || name.equals("����С��") || name.equals("ͬϵ�ӹ�˾")
			|| name.equals("��ҵ�ɴ�") || name.equals("���˽ɴ�") || name.equals("��Ӫ��˾") || name.equals("�����ӹ�˾") || name.equals("���ݽ𹩿�")
			|| name.equals("��ҹ�Ӧ��") || name.equals("����������") || name.equals("�����籣") || name.equals("Kobe") || name.equals("Schio")
			|| name.equals("Italy") || name.equals("ͬĸϵ��˾") || name.equals("��Ʒ��������")
			|| name.contains("�ر�") || name.contains("����") || name.contains("��ż") || name.contains("����") || name.contains("н��")
			|| name.contains("�ؼ�") || name.contains("����") || name.contains("����˾") || name.contains("������") || name.contains("��Ա")
			|| name.contains("�ɶ�") || name.contains("������") || name.contains("����") || name.contains("��") || name.contains("����")
			|| name.contains("������") || name.contains("��Ȼ��") || name.contains("���") || name.contains("����") || name.contains("�߹�")
			|| name.contains("�ܾ���") || name.contains("����") || name.contains("�������󽱽�") || name.contains("����ĸ��˾") || name.contains("�Խ��"))
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
		return false;
	}
	//�ж��Ƿ���B�ɹ�˾
	public static boolean isB(String stockSymbol){
		String firstThree = stockSymbol.substring(0,3);
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
	//��һ������������ͼ���󣬵ڶ���������ʵ������Ľڵ�������������������ֵ�����ĸ�������ʾ�Ƿ�������ͼ
	public static List<Integer> getIdList_ModeHowManyCompany(byte[][] matrix, int nodeCount, int threshold, boolean direct){
		List<Integer> idList = new ArrayList<>();//��Ÿ�����ֵ��id
		if(!direct){
			for(int idi = 0; idi < nodeCount; idi++){
				int frequency = 0;
				for(int idj = 0; idj < nodeCount; idj++){
					//ͳ�Ƹù�˾���ֵ�Ƶ�ʣ�Ŀǰ��������˫���ͷ��
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
					//ͳ�Ƹù�˾���ֵ�Ƶ��
					//ͬʱͳ��һ��id���ڵ��к���
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
	
	
	//�õ���˾���磬���õ�һ��matrix�������˹�˾֮�������Ĺ�ϵ
	//���βεķ�ʽ����matrix��mapIdCompany��mapCompanyId
	//������ǹ�˾���͹�����˾����
	//��һ��������������󣬵ڶ��������ǡ�id-��˾����map�������������ǡ���˾��-id��map�����ĸ�������excel��ַ
	public static void getMatrix(byte[][] matrix, Map<Integer, String> mapIdCompany, Map<String, Integer> mapCompanyId, String path) throws IOException{
		int sheetNumbuer = ExcelFunction.getSheetNumber(path);
		int id = 0;//�±��0��ʼ
		for(int i = 0; i < sheetNumbuer; i++){
			//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
			XSSFSheet sheet = ExcelFunction.getSheet_XSSF(path, i);
			int rowCount = sheet.getLastRowNum();
			for(int k = 1 ; k < rowCount ; k++){
				//���ʹ�˾��
				XSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
				//��Щexcel�����п���
				if(cellCompanyName == null) break;
				String name = getCellStringValue(cellCompanyName).trim().replace(" ", "").replaceAll(" ", "");
				if(needContinue(name)) continue;//ȥ�������յĹ�˾��(��Ӣ�Ŀո�)
				if(mapCompanyId.get(name) == null){//����ù�˾������map�У���Ϊ�����һ��id
					mapCompanyId.put(name, id);
					mapIdCompany.put(id, name);//ͬʱΪ��id��Ӧ��company
					id++;
				}
				//���ʹ�����˾
				XSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
				String asName = getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
				
				asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
				String[] names = asName.split("��");
				for(String n : names){
					if(needContinue(n)) continue;//ȥ�������յĹ�˾��(��Ӣ�Ŀո�)
					if(mapCompanyId.get(n) == null){//����ù�˾������map�У���Ϊ�����һ���±�
						mapCompanyId.put(n, id);
						mapIdCompany.put(id, n);//ͬʱΪ��id��Ӧ��company
						id++;
					}
					//���Ƶ��������幫˾ָ�������˾
					matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] += 1;//����û�и��߸�Ȩ
					matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] += 1;//˫���ͷ�������������Ҫ+1
				}
			}
		}
	}
	public static void getMatrixHSSF(byte[][] matrix, Map<Integer, String> mapIdCompany, Map<String, Integer> mapCompanyId, String address, int mode) throws IOException{
		int id = 0;//�±��0��ʼ
		int sheetNumbuer = ExcelFunction.getSheetNumber(address);
		for(int i = 0; i < sheetNumbuer; i++){
			//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
			HSSFSheet sheet = ExcelFunction.getSheet_HSSF(address, i);
			int rowCount = sheet.getLastRowNum();
			for(int k = 1 ; k < rowCount ; k++){
				if(mode == M.MODE_ONLYA){//��A��ģʽ��
					HSSFCell tempCell = sheet.getRow(k).getCell(M.EXCELINDEX_StockSymbol);
					String stockSymbol = U.getCellStringValue(tempCell).trim().replaceAll(" ", "");
					if(!U.isA(stockSymbol))
						continue;
				}
				//���ʹ�˾��
				HSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
				String name = U.getCellStringValue(cellCompanyName).trim().replaceAll(" ", "");
				if(U.needContinue(name)) continue;//ȥ�����ؼ�������Ա���������¡����ո������
				if(mapCompanyId.get(name) == null){//����ù�˾������map�У���Ϊ�����һ��id
					mapCompanyId.put(name, id);
					mapIdCompany.put(id, name);//ͬʱΪ��id��Ӧ��company
					id++;
				}
				//���ʹ�����˾
				HSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
				String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
				
				asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
				String[] names = asName.split("��");
				for(String n : names){
					if(U.needContinue(n)) continue;//ȥ�����ؼ�������Ա���������¡����ո������
					if(mapCompanyId.get(n) == null){//����ù�˾������map�У���Ϊ�����һ���±�
						mapCompanyId.put(n, id);
						mapIdCompany.put(id, n);//ͬʱΪ��id��Ӧ��company
						id++;
					}
					//��������
					matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] += 1;
					matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] += 1;
				}
			}
		}
	}
	
	//��ȡexcelÿһ��n���ֶε�ֵ����list<list<String>>�ķ�ʽ����
	public static List<List<String>> getRowsList(String fileName, int... fields) throws IOException{
		List<List<String>> lists = new ArrayList<List<String>>();
		PoiExcelHelper exHelper;  
        if(fileName.indexOf(".xlsx") != -1) {  
            exHelper = new PoiExcel2k7Helper();  
        }else {  
            exHelper = new PoiExcel2k3Helper();  
        } 
        int sheetNumbuer = exHelper.getSheetList(fileName).size();
        for(int i = 0; i < sheetNumbuer; i++){//����Ҫ��̬���ڣ��ж��sheet��Ҫ i<sheetNumber
			List<ArrayList<String>> tempLists = exHelper.readExcel(fileName, i);
			for(List<String> tempList : tempLists){
				List<String> list = new ArrayList<String>();
				for(int field : fields){
					String value = tempList.get(field);
					//˼���£�����Ҫ��Ҫ���ﴦ�����ݣ�
					if(field == M.EXCELINDEX_CompanyName && needContinue(value)) continue;//ȥ�����ؼ�������Ա����
					list.add(value);
				}
				if(list.size() < fields.length) continue;//С�ڣ�˵���ֶβ�����
			lists.add(list);
			}
        }
		return lists;
	}
	
	//ͨ������������ֶΣ�����Ƿ���ĳ�����ʣ����Ƿ��ǹ�Ӫ��ҵ��
	//�紫�롰1011���롰M.Type_TransactionPurchase��������true����Ϊ1011�ǡ�����-����������s
	//��һ��������typeValue���ڶ�����������Ҫ�ж��Ƿ����ڵ�type
	public static boolean checkTypeValue(String typeValue, String type, List<String>... lists){
		boolean b = false;
		//��������
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
		//��ҵ����
		else if(type.equals(M.Type_EquityOwnershipAll))
			b = true;
		else if(type.equals(M.Type_EquityOwnershipNation) && typeValue.equals("0"))
			b = true;
		else if(type.equals(M.Type_EquityOwnershipPrivate) && typeValue.equals("1"))
			b = true;
		else if(type.equals(M.Type_EquityOwnershipForeign) && typeValue.equals("2"))
			b = true;
		//����
		else if(type.equals(M.Type_EquityOwnershipYangQi) && lists[0].contains(typeValue))
			b = true;
		//ָ�������磨����˾����
		else if(type.equals(M.Type_EquityOwnershipSubNet) && lists[1].contains(typeValue))
			b = true;
		//ָ�������磨����˾��Ʊ���룩
		else if(type.equals(M.Type_EquityOwnerShipSubNet_Symbol) && lists[2].contains(typeValue))
			b = true;
		//������ҵ
		else if(type.equals(M.Type_IndustryRealty) && (typeValue.contains("J") || typeValue.contains("K")))//K��������ҵ����ƺ����̵ز��Լ�һЩ��ҵ��˾��������
			b = true;
		return b;
	}
	
	
	
	//�������Ĺ�Ʊ���
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
    
    //�жϽ��׷���
    public static boolean directFromListCompany(String transcationType){
    	if(Integer.parseInt(transcationType) % 2 == 0)
    		return false;
    	return true;
    }
    
    //����ֵ��ת��ΪԪ
    //2016��11��18�ջ���
    public static int getRMB(int money, String currency){
    	double result = 0;
    	if(currency.equals("0"))
    		result = money;
    	else if(currency.equals("1"))//��Ԫ
    		result = money * 6.8856;
    	else if(currency.equals("2"))//�۱�
    		result = money * 0.8877;
    	else if(currency.equals("3"))//��Ԫ
    		result = money * 0.0622;
    	else if(currency.equals("4"))//ŷԪ
    		result = money * 7.2925;
//    	else if(currency.equals("5"))//����
//    		result = money * 6.8249;
    	else if(currency.equals("6"))//���
    		result = money * 3.8691;
    	else if(currency.equals("7"))//¬��
    		result = money * 0.1056;
    	else if(currency.equals("8"))//��ʿ����
    		result = money *6.8249;
    	else if(currency.equals("9"))//�Ĵ�����Ԫ
    		result = money * 5.0875;
    	else if(currency.equals("10"))//�¼���Ԫ
    		result = money * 4.8344;
//    	else if(currency.equals("11"))//�����϶�
//    		result = money * 6.8856;
    	else if(currency.equals("12"))//Ӣ��
    		result = money * 8.5535;
    	else if(currency.equals("13"))//ӡ��¬��
    		result = money * 0.1011;
    	else if(currency.equals("14"))//̩����
    		result = money * 0.1936;
    	else if(currency.equals("15"))//����·˹��
    		result = money * 13.2728;
    	else if(currency.equals("16"))//�ݿ˿���
    		result = money * 0.2705;
    	else if(currency.equals("17"))//Ų������
    		result = money * 0.8028;
    	else if(currency.equals("18"))//������
    		result = money * 0.7440;
    	else if(currency.equals("19"))//����Ԫ
    		result = money * 0.8629;
    	else if(currency.equals("20"))//�������Ƕ�
    		result = money * 2.0070;
    	else if(currency.equals("21"))//����������
    		result = money * 0.0236;
    	else if(currency.equals("22"))//����
    		result = money * 0.4722;
    	else if(currency.equals("23"))//����
    		result = money * 2.1843;
    	else if(currency.equals("24"))//���ô�Ԫ
    		result = money * 5.0909;
    	else if(currency.equals("25"))//���������ּ���
    		result = money * 1.5605;
    	else if(currency.equals("26"))//������
    		result = money * 3.4294;
    	else if(currency.equals("27"))//ͼ�����
    		result = money * 0.0029;
    	else if(currency.equals("28"))//������˹̹
    		result = money * 0.01996;
    	else if(currency.equals("29"))//��Ԫ
    		result = money * 0.0058;
    	else if(currency.equals("30"))//���ɱ�����
    		result = money * 0.1386;
    	return (int)result;
    }
    
    
    //�ӹ�˾������ȡ����
    public static String getDistrict(String name, List<String> listDistrict, Map<String, String> mapCityDistrict){
    	for(String district : listDistrict){//����ʡ�����ж�
    		if(name.contains(district))
    			return district;
    	}
    	for(String key : mapCityDistrict.keySet()){//���û��ʡ�ݣ��Ǿ��ó���ȥ�ж�
    		if(name.contains(key)){
    			return mapCityDistrict.get(key);
    		}
    	}
    	return "";
    }
    
    //ȥ����������ġ�ʡ���С�����������
    public static String cleanDistrict(String str){
    	str = str.trim().replaceAll(" ", "").replaceAll("ʡ", "").replaceAll("������", "").replaceAll("��", "").replaceAll("׳��", "")
    			.replaceAll("����", "").replaceAll("ά���", "");
    		return str;
    }
    //��������ͳһ
	public static String cleanCity(String str){
	str = str.trim().replaceAll("����", "����").replaceAll("����", "�ൺ").replaceAll("����", "̨��")
		.replaceAll("��̩", "̩��").replaceAll("ͨ��", "����").replaceAll("�żҸ�", "����").replaceAll("����", "��̨").replaceAll("�뽭��", "����")
		.replaceAll("ʯ��ɽ", "����").replaceAll("����", "���").replaceAll("�Ϻӿ�", "����").replaceAll("����", "����").replaceAll("�ϻ�", "�ֶ���")
		.replaceAll("��̨", "����").replaceAll("˳��", "����").replaceAll("������", "����").replaceAll("�ǿ���", "����").replaceAll("�Ժ�", "����")
		.replaceAll("����", "����").replaceAll("�뽭", "��ɽ").replaceAll("�ϰ���", "����").replaceAll("����", "���").replaceAll("ǭ��", "����")
		.replaceAll("����", "Ϋ��").replaceAll("����", "����").replaceAll("ƾ��", "����").replaceAll("����", "����").replaceAll("����", "����")
		.replaceAll("����", "����Ͽ").replaceAll("��β", "����").replaceAll("����", "����������").replaceAll("����ɽ", "����ˮ").replaceAll("��Ϫ", "����")
		.replaceAll("����", "����").replaceAll("����", "����").replaceAll("��ӹ", "�żҽ�").replaceAll("���", "Ϋ��").replaceAll("����", "����")
		.replaceAll("��������", "����������").replaceAll("÷�ӿ�", "ͨ��").replaceAll("�潭", "����").replaceAll("��ɽ", "����").replaceAll("��ԭ", "����")
		.replaceAll("ʯ����", "ʯ����").replaceAll("����", "�ֶ���").replaceAll("����", "����").replaceAll("��", "����").replaceAll("����", "����")
		.replaceAll("����", "���").replaceAll("��ɽ", "����").replaceAll("����", "��ׯ").replaceAll("����", "����").replaceAll("����", "�Ϸ�")
		.replaceAll("ʯʨ", "Ȫ��").replaceAll("����", "���").replaceAll("��ɳ" , "�ֶ���").replaceAll("�差", "����").replaceAll("�ᶼ��", "����")
		.replaceAll("�߷���", "����").replaceAll("����", "����").replaceAll("����", "����").replaceAll("������", "����").replaceAll("˳��", "��ɽ")
		.replaceAll("��ͷ��", "����").replaceAll("��ƽ", "����").replaceAll("������", "���ױ���").replaceAll("����", "����").replaceAll("����", "�������")
		.replaceAll("����", "��ľ˹").replaceAll("��ī", "�ൺ").replaceAll("¬��", "����").replaceAll("������", "����").replaceAll("�㺺", "����")
		.replaceAll("��������������������", "����").replaceAll("����", "������").replaceAll("�ɻ���", "������").replaceAll("�żҸ�", "����").replaceAll("������", "����")
		.replaceAll("����", "����").replaceAll("����", "����").replaceAll("����", "��̨").replaceAll("����", "ʮ��").replaceAll("����", "����")
		.replaceAll("������", "����").replaceAll("�ϴ�", "����").replaceAll("����", "����").replaceAll("����", "����").replaceAll("����", "��̶")
		.replaceAll("����ľ��", "ͨ��").replaceAll("����", "���").replaceAll("���ó��ڼӹ���", "����").replaceAll("����", "��").replaceAll("����", "����")
		.replaceAll("��������", "����").replaceAll("����", "���ֹ�����").replaceAll("����", "����").replaceAll("�����", "�����").replaceAll("˼é", "�ն�")
		.replaceAll("����", "����").replaceAll("����", "����").replaceAll("��Ҧ", "����").replaceAll("����", "����").replaceAll("��ɽ", "����")
		.replaceAll("����", "����").replaceAll("����", "ͨ��").replaceAll("����", "����").replaceAll("ƽ��", "�ൺ").replaceAll("������", "�ɶ�")
		.replaceAll("�˻�", "̩��").replaceAll("����", "�������").replaceAll("��Ʊ", "����").replaceAll("��̨", "�γ�").replaceAll("����", "����")
		.replaceAll("����", "����").replaceAll("����", "����").replaceAll("�ĵ�", "����").replaceAll("����ű�˰��", "�ֶ���").replaceAll("����", "����")
		.replaceAll("��̨", "����").replaceAll("����", "���Ǹ�").replaceAll("̫��", "����").replaceAll("�ֶ�", "�ֶ���").replaceAll("���", "�Ž�")
		.replaceAll("�ٳ�", "����").replaceAll("�䰲", "����").replaceAll("����", "��").replaceAll("ͼ��", "�ӱ�").replaceAll("����", "��ɽ")
		.replaceAll("����", "��«��").replaceAll("��Ϫ", "��").replaceAll("����", "��").replaceAll("�", "����").replaceAll("��������ڼӹ���", "�����")
		.replaceAll("��������", "������˹").replaceAll("�ӱ�", "�Ӽ�").replaceAll("��ɽ", "����").replaceAll("ǭ����", "����").replaceAll("ǭ����", "����")
		.replaceAll("ǭ��", "����").replaceAll("�Ϻ�", "��ɽ").replaceAll("����", "��ԭ").replaceAll("ͬ��", "��ľ˹��").replaceAll("ǭ����", "����")
		.replaceAll("�⽭", "����").replaceAll("�˳�", "��«��").replaceAll("����", "��ͨ").replaceAll("����", "���").replaceAll("����", "����")
		.replaceAll("��Һ�", "ĵ����").replaceAll("�z��", "����").replaceAll("����", "��ͨ").replaceAll("����", "���").replaceAll("����", "����");
	return str;
}
	
	
	
	
	
	//�������ַ����������򷵻�
	public static String getCompareString(String s1, String s2){
		if(s1.compareTo(s2) < 0)
			return s1 + "," + s2;
		return s2 + "," + s1;
	}
	
	
	
	
	
	
	//�����¼���ļ�¼����map����Ӽ���
	public static void mapAddCount(Map<String, Integer> map, String s){
		map.put(s, map.get(s) == null ? 1 : map.get(s)+1);
	}
	
	
	
	
	
	
	
	
	//��ȡmap��topN��ռ�����ļ�¼
	public static List<String> getMapTopPercentage(Map<String, Integer> map, int limit){
		List<String> result = new ArrayList<>();
		//�ȼ�������
		double all = 0;
		for(Entry<String, Integer> entry : map.entrySet())
			all += entry.getValue();
		//ȡtopN(ע�⣬���ﲻһ����ȡ��N��)
		TreeMap<String, Integer> sort = U.sortMap(map);
		for(Entry<String, Integer> entry : sort.entrySet()){
			if(limit-- == 0) break;
			result.add(entry.getKey() + ":" + (entry.getValue()/all));
		}
		return result;
	}
	
	//������Ϣ��
	public static double getComentropy(Map<String, Integer> map){
		//�ȼ�������
		double all = 0;
		for(Entry<String, Integer> entry : map.entrySet())
			all += entry.getValue();
		//������Ϣ��
		double comentropy = 0;
		for(Entry<String, Integer> entry : map.entrySet()){
			double p = (double)entry.getValue()/all;
			comentropy -= p * (Math.log(p)/Math.log(2));
		}
		return comentropy;
	}
    
}
