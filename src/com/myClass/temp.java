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
	
//	//读取一份excel，将其中公司两两的关系写入
//	XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\关联公司\\原始数据\\关联交易数据库--分类处理\\" + year + "\\" + fileName + "\\" + excelName, 0);
//	int rowCount = sheet.getLastRowNum();
//	int id = 0;//下标从0开始
//	for(int k = 1 ; k < rowCount ; k++){
//		//访问公司名
//		XSSFCell cellCompanyName = sheet.getRow(k).getCell(M.EXCELINDEX_CompanyName);
//		//有些excel后面有空行
//		if(cellCompanyName == null) break;
//		String name = U.getCellStringValue(cellCompanyName).trim().replace(" ", "").replaceAll(" ", "");
//		if(U.needContinue(name)) continue;//去掉两个空的公司名(中英文空格)
//		if(mapCompanyId.get(name) == null){//如果该公司并不在map中，则为其添加一个id
//			mapCompanyId.put(name, id);
//			mapIdCompany.put(id, name);//同时为该id对应到company
//			id++;
//		}
//		//访问关联公司
//		XSSFCell cellAssociatedCompany = sheet.getRow(k).getCell(M.EXCELINDEX_AssociatedCompany);
//		String asName = U.getCellStringValue(cellAssociatedCompany).trim().replaceAll(" ", "");
//		
//		asName = asName.replaceAll(",", "、");//2014的excel中切割标示用的是','
//		String[] names = asName.split("、");
//		for(String n : names){
//			if(U.needContinue(n)) continue;//去掉两个空的公司名(中英文空格)
//			if(mapCompanyId.get(n) == null){//如果该公司并不在map中，则为其添加一个下标
//				mapCompanyId.put(n, id);
//				mapIdCompany.put(id, n);//同时为该id对应到company
//				id++;
//			}
//			//绘制单向，由主体公司指向关联公司
//			matrix[mapCompanyId.get(name)][mapCompanyId.get(n)] = 1;//这里没有给线赋权
//			if(direction == 2)//双向箭头有两个矩阵格都需要+1
//				matrix[mapCompanyId.get(n)][mapCompanyId.get(name)] = 1;
//		}
//	}
}
