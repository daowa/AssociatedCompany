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
	
	public static float THRESHOLD = (float) 0.865113;//���ƶ���ֵ

	public static void cluster() throws IOException{
		//��ʼ������
		List<String> listCity = new ArrayList<>();//�����б�
		List<Integer> listSelected = new ArrayList<>();//�Ѽ���group�ĳ���
		float[][] matrix = new float[35][35];//���ƶȶ�ά����
		
		//��ȡexcel����������б�����ƶȶ�ά����
		XSSFSheet sheet = ExcelFunction.getSheet_XSSF("E:\\work\\done\\����-����������ݴ���\\������1.xlsx", 0);
		int rowCount = sheet.getLastRowNum();
		//��ȡ�����б�
		for(int i = 1; i < rowCount; i++){
			listCity.add(U.getCellStringValue(sheet.getRow(i).getCell(0)));
		}
		//��ȡ��ά����
		for(int i = 1; i < rowCount; i++){
			for(int j = 1; j < rowCount; j++){
				matrix[i-1][j-1] = Float.parseFloat(U.getCellStringValue(sheet.getRow(i).getCell(j)));
			}
		}
		
		while(true){
			//���ѡ��һ���³���
			Random random = new Random();
			int index = -1;
			while(true){
				index = random.nextInt(35);
				if(!listSelected.contains(index))//�����δѡ�й��ĳ��У���ôͨ��
					break;
			}
			//�½�group
			List<Integer> listGroup = new ArrayList<>();
			listGroup.add(index);
			listSelected.add(index);
			//�ȱ�����ֱ���ھӣ��ж��Ƿ��ܼ���group
			for(int i = 0; i < 35; i++){
				if(i == index) continue;//��������
				if(matrix[index][i] <= THRESHOLD) continue;//����ھӣ�����
				if(listSelected.contains(i)) continue;//�Ѿ�����ĳ��group�ĳ��У�����
				//����ͶƱ���ж��Ƿ��ܼ���
				if(vote(listGroup, matrix, i)){
					listGroup.add(i);
					listSelected.add(i);
				}
			}
			//�ٱ��������ھӣ��ж��Ƿ��ܼ���group
			for(int i = 0; i < 35; i++){
				if(i == index) continue;//��������
				if(matrix[index][i] > THRESHOLD) continue;//ֱ���ھӣ�����
				if(listSelected.contains(i)) continue;//�Ѿ�����ĳ��group�ĳ��У�����
				//����ͶƱ���ж��Ƿ��ܼ���
				if(vote(listGroup, matrix, i)){
					listGroup.add(i);
					listSelected.add(i);
				}
			}
			//���group
			String s = "";
			for(int cityIndex : listGroup){
				s += listCity.get(cityIndex) + ",";
			}
			U.print(s.substring(0, s.length()-1));
			
			//�ж��Ƿ��ܽ�������
			if(listSelected.size() == 35)
				break;
		}
	}
	
	private static boolean vote(List<Integer> listGroup, float[][] matrix, int newCity){
		int count = 0;
		for(int cityIndex : listGroup){
			if(matrix[cityIndex][newCity] > THRESHOLD) count++;//ͶƱ
		}
		if((float)count/(float)listGroup.size() > 0.5)
			return true;
		return false;
	}
	
}
