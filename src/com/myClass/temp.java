package com.myClass;

import java.io.FileWriter;

public class temp {

//	�����ʽ
//	if(outputFormat == M.OUTPUTFORMAT_DL){
//		FileWriter fw = new FileWriter("E:/work/������˾/txt/dl_asCompany"
//					+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".txt");
//		fw.write("dl" + "\r\n");
//		fw.write("n = " + idList.size() + "\r\n");
//		fw.write("labels embedded" + "\r\n");
//		fw.write("format = fullmatrix" + "\r\n");
//		fw.write("data:" + "\r\n");
//		String line = null;
//		//д��һ�У��ֶ��У�
//		line = "";//���line
//		for(String key : mapCompanyId.keySet()){
//			if(idList.contains(mapCompanyId.get(key)))//��������ֵ�ļ����ӡ����
//				line += key + " ";
//		}
//		line = line.substring(0, line.length()-1);//ɾ�����һ���ո�
//		fw.write(line + "\r\n");
//		//����д��¼
//		for(String key : mapCompanyId.keySet()){
//			if(idList.contains(mapCompanyId.get(key))){//��������ֵ�ļ����ӡ����
//				U.print("����д�빫˾:" + key + ",idΪ:" + mapCompanyId.get(key));
//				line = "";//���line
//				line += key + " ";
//				for(int fwi = 0; fwi < mapCompanyId.size(); fwi ++){
//					if(idList.contains(fwi)){//��������ֵ���м����ӡ����
//						line += matrix[mapCompanyId.get(key)][fwi] + " ";
//					}
//				}
//				line = line.substring(0, line.length()-1);//ɾ�����һ���ո�
//				fw.write(line + "\r\n");
//			}
//		}
//		fw.close();
//	}
//	else if(outputFormat == M.OUTPUTFORMAT_NET){
//		FileWriter fw = new FileWriter("E:/work/������˾/txt/net_asCompany"
//				+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".txt");
//		fw.write("From\tTo\tWeight\r\n");
//		for(int fwi = 0; fwi < idList.size(); fwi++){
//			U.print("����д�빫˾:" + mapIdCompany.get(idList.get(fwi)) + ",idΪ:" + idList.get(fwi));
//			for(int fwj = 0; fwj < idList.size(); fwj++){
//				if(matrix[idList.get(fwi)][idList.get(fwj)] == 0) continue;//����޹�����������
//				fw.write(idList.get(fwi) + "\t"
//						+ idList.get(fwj) + "\t"
//						+ matrix[idList.get(fwi)][idList.get(fwj)]/2 + "\r\n");
//			}
//		}
//		fw.close();
//	}
	
//	//��ȡһ��excel�������й�˾�����Ĺ�ϵд��
//	XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\������˾\\ԭʼ����\\�����������ݿ�--���ദ��\\" + year + "\\" + fileName + "\\" + excelName, 0);
//	int rowCount = sheet.getLastRowNum();
//	int id = 0;//�±��0��ʼ
//	for(int k = 1 ; k < rowCount ; k++){
//		//���ʹ�˾��
//		XSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
//		//��Щexcel�����п���
//		if(cellCompanyName == null) break;
//		String name = U.getCellStringValue(cellCompanyName).trim().replace(" ", "").replaceAll(" ", "");
//		if(U.needContinue(name)) continue;//ȥ�������յĹ�˾��(��Ӣ�Ŀո�)
//		if(mapCompanyId.get(name) == null){//����ù�˾������map�У���Ϊ�����һ��id
//			mapCompanyId.put(name, id);
//			mapIdCompany.put(id, name);//ͬʱΪ��id��Ӧ��company
//			id++;
//		}
//		//���ʹ�����˾
//		XSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
//		String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
//		
//		asName = asName.replaceAll(",", "��");//2014��excel���и��ʾ�õ���','
//		String[] names = asName.split("��");
//		for(String n : names){
//			if(U.needContinue(n)) continue;//ȥ�������յĹ�˾��(��Ӣ�Ŀո�)
//			if(mapCompanyId.get(n) == null){//����ù�˾������map�У���Ϊ�����һ���±�
//				mapCompanyId.put(n, id);
//				mapIdCompany.put(id, n);//ͬʱΪ��id��Ӧ��company
//				id++;
//			}
//			//���Ƶ��������幫˾ָ�������˾
//			matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] = 1;//����û�и��߸�Ȩ
//			if(direction == 2)//˫���ͷ�������������Ҫ+1
//				matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] = 1;
//		}
//	}
}
