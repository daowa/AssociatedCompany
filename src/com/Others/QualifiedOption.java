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

	//����������������������׵Ĺ�˾�������
	public static void outputConnectedTransaction(String type) throws IOException{
		//�������-֤ȯ��������
		List<List<String>> listsYearStocksymble = new ArrayList<>();
		String pathTarget = "E:\\work\\������˾\\������_�����ͷ�\\20170307�������\\" + type + ".xls";
		listsYearStocksymble.addAll(U.getRowsList(pathTarget, 2, 0));
		List<String> listTarget = new ArrayList<>();
		for(int i = 2; i < listsYearStocksymble.size(); i++){
			List<String> list = listsYearStocksymble.get(i);
			String line = list.get(0).substring(0, 4) + "," + list.get(1);
			listTarget.add(line);
		}
		//��д���ͷ
		List<String> listResult = new ArrayList<>();
		listResult.add("���,��Ʊ����,��Ʊ���,��˾����ȫ��,��˾Ӣ��ȫ��,CSRC��ҵ����,GICS��ҵ����,��˾ע���,֤ȯ������,��ҵ����ʾ,���տ���������,����״̬,��������," +
				"��������ҵ����,���������ƹ�ϵ,�����������й�˾��ϵ,��Ϣ��Դ,����,���ҵ�λ,�����漰���,��������");
		//��ԭ�������н��в��ң���д������Ҫ������
		for(int i = 2009; i < 2016; i++){//���ѭ��
			String fileName = "E:/work/������˾/ԭʼ����/" + i + ".xls";
			PoiExcelHelper exHelper = new PoiExcel2k3Helper();
	        int sheetNumbuer = exHelper.getSheetList(fileName).size();
	        for(int j = 0; j < sheetNumbuer; j++){//sheetѭ��
				List<ArrayList<String>> tempLists = exHelper.readExcel(fileName, j);
				for(List<String> tempList : tempLists){
					//�ж��Ƿ���Ҫ
					String line = tempList.get(0) + "," + tempList.get(1);
					if(!listTarget.contains(line)) continue;
					//д������
					String result = "";
					for(int k = 0; k < 21; k++)//�ֶ�ѭ��
						result += tempList.get(k).replaceAll(",", "��") + ",";
					listResult.add(result.substring(0, result.length()-1));//ȥ�����Ķ���
					U.print(result);
				}
	        }
		}
		
		//�������
		String pathResult = "E:\\work\\������˾\\������_�����ͷ�\\20170307�������\\" + type + "������.csv";
		FileFunction.writeList(listResult, pathResult);
	}
	
}
