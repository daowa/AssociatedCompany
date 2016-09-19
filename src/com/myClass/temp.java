package com.myClass;

import java.io.FileWriter;

public class temp {

//	输出格式
//	if(outputFormat == M.OUTPUTFORMAT_DL){
//		FileWriter fw = new FileWriter("E:/work/关联公司/txt/dl_asCompany"
//					+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".txt");
//		fw.write("dl" + "\r\n");
//		fw.write("n = " + idList.size() + "\r\n");
//		fw.write("labels embedded" + "\r\n");
//		fw.write("format = fullmatrix" + "\r\n");
//		fw.write("data:" + "\r\n");
//		String line = null;
//		//写第一行（字段行）
//		line = "";//清空line
//		for(String key : mapCompanyId.keySet()){
//			if(idList.contains(mapCompanyId.get(key)))//仅高于阈值的加入打印出来
//				line += key + " ";
//		}
//		line = line.substring(0, line.length()-1);//删除最后一个空格
//		fw.write(line + "\r\n");
//		//逐行写记录
//		for(String key : mapCompanyId.keySet()){
//			if(idList.contains(mapCompanyId.get(key))){//仅高于阈值的加入打印出来
//				U.print("正在写入公司:" + key + ",id为:" + mapCompanyId.get(key));
//				line = "";//清空line
//				line += key + " ";
//				for(int fwi = 0; fwi < mapCompanyId.size(); fwi ++){
//					if(idList.contains(fwi)){//仅高于阈值的列加入打印出来
//						line += matrix[mapCompanyId.get(key)][fwi] + " ";
//					}
//				}
//				line = line.substring(0, line.length()-1);//删除最后一个空格
//				fw.write(line + "\r\n");
//			}
//		}
//		fw.close();
//	}
//	else if(outputFormat == M.OUTPUTFORMAT_NET){
//		FileWriter fw = new FileWriter("E:/work/关联公司/txt/net_asCompany"
//				+ i + "_" + isOneWay + "_" + threshold + "_" + mode + ".txt");
//		fw.write("From\tTo\tWeight\r\n");
//		for(int fwi = 0; fwi < idList.size(); fwi++){
//			U.print("正在写入公司:" + mapIdCompany.get(idList.get(fwi)) + ",id为:" + idList.get(fwi));
//			for(int fwj = 0; fwj < idList.size(); fwj++){
//				if(matrix[idList.get(fwi)][idList.get(fwj)] == 0) continue;//如果无关联，则跳过
//				fw.write(idList.get(fwi) + "\t"
//						+ idList.get(fwj) + "\t"
//						+ matrix[idList.get(fwi)][idList.get(fwj)]/2 + "\r\n");
//			}
//		}
//		fw.close();
//	}
}
