package com.main;

import java.io.IOException;

import com.data.ProProcess;
import com.myClass.M;
import com.myClass.U;

public class AssociatedCompany {
	
	public static void main(String[] args) throws IOException{

//		ProProcess.outputCompanyName();//ͳ��4��excel�г��ֵĹ�˾�����������������txt
		
		//�����˾��ϵ��
		//��һ��������ʾ�����ʽ���ڶ���������ʾ�Ƿ��ǵ����ͷ��������������ʾ��˾���ֵ���ֵ
		ProProcess.outputCompanyAssociate(M.OUTPUTFORMAT_NETTXT, false, 40);

		
		
	}
}
