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
}
