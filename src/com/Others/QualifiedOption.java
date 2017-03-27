package com.Others;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.db.FileFunction;
import com.myClass.M;
import com.myClass.U;
import com.myClass.POI.PoiExcel2k3Helper;
import com.myClass.POI.PoiExcelHelper;

public class QualifiedOption {

	//将保留意见、包含关联交易的公司数据输出
	public static void outputConnectedTransaction(String type) throws IOException{
		//读入年度-证券代号数据
		List<List<String>> listsYearStocksymble = new ArrayList<>();
		String pathTarget = "E:\\work\\关联公司\\输出结果_按类型分\\20170307保留意见\\" + type + ".xls";
		listsYearStocksymble.addAll(U.getRowsList(pathTarget, 2, 0));
		List<String> listTarget = new ArrayList<>();
		for(int i = 2; i < listsYearStocksymble.size(); i++){
			List<String> list = listsYearStocksymble.get(i);
			String line = list.get(0).substring(0, 4) + "," + list.get(1);
			listTarget.add(line);
		}
		//先写入表头
		List<String> listResult = new ArrayList<>();
		listResult.add("年度,股票代码,股票简称,公司中文全称,公司英文全称,CSRC行业分类,GICS行业分类,公司注册地,证券交易所,企业板块标示,最终控制人类型,交易状态,公告日期," +
				"关联方企业名称,关联方控制关系,关联方与上市公司关系,信息来源,币种,货币单位,交易涉及金额,交易类型");
		//从原有数据中进行查找，并写入所需要的数据
		for(int i = 2009; i < 2016; i++){//年份循环
			String fileName = "E:/work/关联公司/原始数据/" + i + ".xls";
			PoiExcelHelper exHelper = new PoiExcel2k3Helper();
	        int sheetNumbuer = exHelper.getSheetList(fileName).size();
	        for(int j = 0; j < sheetNumbuer; j++){//sheet循环
				List<ArrayList<String>> tempLists = exHelper.readExcel(fileName, j);
				for(List<String> tempList : tempLists){
					//判断是否需要
					String line = tempList.get(0) + "," + tempList.get(1);
					if(!listTarget.contains(line)) continue;
					//写入数据
					String result = "";
					for(int k = 0; k < 21; k++)//字段循环
						result += tempList.get(k).replaceAll(",", "，") + ",";
					listResult.add(result.substring(0, result.length()-1));//去掉最后的逗号
					U.print(result);
				}
	        }
		}
		
		//输出数据
		String pathResult = "E:\\work\\关联公司\\输出结果_按类型分\\20170307保留意见\\" + type + "处理结果.csv";
		FileFunction.writeList(listResult, pathResult);
	}
	
}
