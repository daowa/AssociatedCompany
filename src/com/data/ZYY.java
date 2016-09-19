package com.data;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.db.ExcelFunction;
import com.myClass.M;
import com.myClass.U;

public class ZYY {

	public static void ZYY() throws IOException, ClassNotFoundException, SQLException{
		HSSFCell cell = null;
		List<String> listProvince = new ArrayList<>();
		List<String> listCity = new ArrayList<>();
		//����ʡ��
		String fileName = "E:\\work\\���ý����ݷ�������\\�Դ�Ϊ׼.xls";
		HSSFSheet sheet = ExcelFunction.getSheet(fileName, 0);
		int rowCount = sheet.getLastRowNum();
		for(int k = 1; k < rowCount; k++){
			//����ʡ��
			cell = sheet.getRow(k).getCell(1);
			String province = replace(U.getCellStringValue(cell));
//			if(!listProvince.contains(province))
				listProvince.add(province);//���ų�����ʡ�ˣ�Ϊ���ܰ�ʡ���ж���
		}
		for(int k = 1; k < rowCount; k++){
			//��������
			cell = sheet.getRow(k).getCell(2);
			String city = replace(U.getCellStringValue(cell));
			listCity.add(city);
		}
//		listCity.add("����");
		
		//��ȡaccess�е�����
		Map<String, Map<Integer, List<String>>> map = new HashMap<>();
		
        String sDriver="sun.jdbc.odbc.JdbcOdbcDriver";  
        try{  
            Class.forName(sDriver);  
        }  
        catch(Exception e){  
            System.out.println("�޷�������������");  
            return;  
        }  
        String sCon="jdbc:odbc:2006";
		Connection c = DriverManager.getConnection(sCon);
        Statement s = c.createStatement();     
        ResultSet  r = s.executeQuery("SELECT ����,��Ʒ����,���,���� FROM 200602");
        String address = "";
        int id = -1;
        String priceAndQuantity = "";
        String province;
        String city;
        int indexCity = -1;//��¼�ǵڼ�������ƥ����ȷ�ģ��Դ���ȷ��ʡ��
        String pc;//ʡ��
        int k = 0;//�����������һ����ỻ�����쳣����Ȼ��֪��Ϊʲô..
        while(r.next()){
        	k++;
        	U.print(k);
//        	if(k > 10) break;
//        	List<String> listPriceAndQuantity = new ArrayList<>();
        	address = r.getString("����"); if(address == null) continue;
        	id = Integer.parseInt(r.getString("��Ʒ����"));
        	priceAndQuantity = (int)Double.parseDouble(r.getString("���")) + "," + r.getString("����");
        	//��ȡʡ���е�����
        	province = "?";
            city = "?";
            indexCity = -1;
        	for(int i = 0; i < listProvince.size(); i++){
        		if(address.contains(listProvince.get(i))){
        			province = listProvince.get(i);
        			address = address.replaceAll(listProvince.get(i), "");
        			break;
        		}
        	}
        	address = replace(address);
        	for(int i = 0; i < listCity.size(); i++){
        		if(address.contains(listCity.get(i))){
        			city = listCity.get(i);
        			indexCity = i;
        		}
        	}
        	if(province.equals("����")) city = "����";
        	if(province.equals("����")) city = "����";
        	if(province.equals("���")) city = "���";
        	if(province.equals("����")) city = "����";
        	pc = (province.equals("?") ? (indexCity != -1 ? listProvince.get(indexCity) : "δ֪") : province) + "," + (city.equals("?") ? address+"?" : city);
        	//�������
        	if(map.get(pc) == null){//��û�и�ʡ����ϵ����
        		Map<Integer, List<String>> tempMap = new HashMap<>();
        		List<String> tempList = new ArrayList<>();
        		tempList.add(priceAndQuantity);
        		tempMap.put(id, tempList);
        		map.put(pc, tempMap);
        	}
        	else{
        		if(map.get(pc).get(id) == null){//ʡ������£���û�и���Ʒid�����
        			List<String> tempList = new ArrayList<>();
            		tempList.add(priceAndQuantity);
            		map.get(pc).put(id, tempList);
        		}
        		else{//ȫ�У���ֱ������list<"���,����">����
        			map.get(pc).get(id).add(priceAndQuantity);
        		}
        	}
        }
        
        //�����txt
        FileWriter fw = new FileWriter("E:\\work\\���ý����ݷ�������\\middle.txt");
        Set<String> setPC = map.keySet();
        for(String keyPC : setPC){
        	String ssPC[] = keyPC.split(",");
        	Set<Integer> setID = map.get(keyPC).keySet();
        	for(int keyID : setID){
        		fw.write(ssPC[0] + "\t" + ssPC[1] + "\t");
        		fw.write(keyID + "\t");
        		List<String> tempList = map.get(keyPC).get(keyID);
        		List<Double> listPrice = new ArrayList<>();//���
    			List<Double> listQuantity = new ArrayList<>();//����
    			List<Double> listPer = new ArrayList<>();//����
        		for(int i = 0; i < tempList.size(); i++){
        			String ss[] = tempList.get(i).split(",");
        			listPrice.add(Double.parseDouble(ss[0]));
        			listQuantity.add(Double.parseDouble(ss[1]));
        			listPer.add(Double.parseDouble(ss[0]) / Double.parseDouble(ss[1]));
        		}
        		fw.write(U.MATH_getSum(listPrice) + "\t");
        		fw.write(U.MATH_getSum(listQuantity) + "\t");
        		fw.write(U.MATH_getAverage(listPer) + "\t");
        		fw.write(U.MATH_getVariance(listPer) + "\t");
        		fw.write("\r\n");
        	}
        }
        fw.close();
        U.print(map.size());
        U.print("�������middle.txt");
        
        s.close();  
	}
	
	private static String replace(String str){
		str = str.trim().replaceAll(" ", "").replaceAll("ʡ", "").replaceAll("������", "").replaceAll("��", "").replaceAll("׳��", "")
			.replaceAll("����", "").replaceAll("ά���", "").replaceAll("����", "����").replaceAll("����", "�ൺ").replaceAll("����", "̨��")
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
}
