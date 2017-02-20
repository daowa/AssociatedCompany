package com.Others;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.db.ExcelFunction;
import com.myClass.U;

public class LZ {
	
	public static float THRESHOLD = (float) 0.865113;//相似度阈值

	public static void cluster() throws IOException{
		//初始化变量
		List<String> listCity = new ArrayList<>();//城市列表
		List<Integer> listSelected = new ArrayList<>();//已加入group的城市
		float[][] matrix = new float[35][35];//相似度二维矩阵
		
		//读取excel，读入城市列表和相似度二维矩阵
		XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\done\\刘震-聚类分析数据处理\\工作簿1.xlsx", 0);
		int rowCount = sheet.getLastRowNum();
		//读取城市列表
		for(int i = 1; i < rowCount; i++){
			listCity.add(U.getCellStringValue(sheet.getRow(i).getCell(0)));
		}
		//读取二维矩阵
		for(int i = 1; i < rowCount; i++){
			for(int j = 1; j < rowCount; j++){
				matrix[i-1][j-1] = Float.parseFloat(U.getCellStringValue(sheet.getRow(i).getCell(j)));
			}
		}
		
		while(true){
			//随机选择一个新城市
			Random random = new Random();
			int index = -1;
			while(true){
				index = random.nextInt(35);
				if(!listSelected.contains(index))//如果是未选中过的城市，那么通过
					break;
			}
			//新建group
			List<Integer> listGroup = new ArrayList<>();
			listGroup.add(index);
			listSelected.add(index);
			//先遍历其直接邻居，判断是否能加入group
			for(int i = 0; i < 35; i++){
				if(i == index) continue;//自身，跳过
				if(matrix[index][i] <= THRESHOLD) continue;//间接邻居，跳过
				if(listSelected.contains(i)) continue;//已经进入某个group的城市，跳过
				//组内投票，判断是否能加入
				if(vote(listGroup, matrix, i)){
					listGroup.add(i);
					listSelected.add(i);
				}
			}
			//再遍历其间接邻居，判断是否能加入group
			for(int i = 0; i < 35; i++){
				if(i == index) continue;//自身，跳过
				if(matrix[index][i] > THRESHOLD) continue;//直接邻居，跳过
				if(listSelected.contains(i)) continue;//已经进入某个group的城市，跳过
				//组内投票，判断是否能加入
				if(vote(listGroup, matrix, i)){
					listGroup.add(i);
					listSelected.add(i);
				}
			}
			//输出group
			String s = "";
			for(int cityIndex : listGroup){
				s += listCity.get(cityIndex) + ",";
			}
			U.print(s.substring(0, s.length()-1));
			
			//判断是否能结束运算
			if(listSelected.size() == 35)
				break;
		}
	}
	
	private static boolean vote(List<Integer> listGroup, float[][] matrix, int newCity){
		int count = 0;
		for(int cityIndex : listGroup){
			if(matrix[cityIndex][newCity] > THRESHOLD) count++;//投票
		}
		if((float)count/(float)listGroup.size() > 0.5)
			return true;
		return false;
	}
	
}
