package com.main;

import java.io.IOException;
import java.sql.SQLException;

import com.data.ProProcess;
import com.data.ZYY;
import com.myClass.M;
import com.myClass.U;

public class AssociatedCompany{
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException{

//		ProProcess.outputCompanyName();//ͳ��4��excel�г��ֵĹ�˾�����������������txt
//		ProProcess.outputCompanyType();//�����˾���͵�txt
//		ProProcess.outputCompanyAddress();//�����˾��ַ��txt
//		ProProcess.outputCompanyClassfiedType();//�������ҵ�����ʵȹ�˾���ͣ������ͬʱ���ڶ�����͵Ĺ�˾���Ա����˹����й���
//		ProProcess.outputCompanyClassfiedType_Year();//������������ҵ�����ʵȹ�˾����
		
		//�����˾��ϵ��
		//��һ��������ʾ�����ʽ���ڶ���������ʾ���й�˾����ֻ��A�����й�˾��������������ʾ�Ƿ��ǵ����ͷ�����ĸ�������ʾ��˾���ֵ���ֵ
//		ProProcess.outputCompanyAssociate(M.OUTPUTFORMAT_STARCOMPANY, M.MODE_ALLCOMPANY, false, 1);
		//��������ͷֵĹ�˾��ϵ����һ������Ϊ��ֵ���ڶ�������Ϊ������˫���ͷ,������������ʾ�������
//		ProProcess.outputByClassification(1, 2, M.OUTPUTFORMAT_COMPANYTYPE);
		//�����ϵ��ֵĹ�˾��ϵ����һ������Ϊ��ֵ���ڶ�������Ϊ������˫���ͷ,������������ʾ�������
//		ProProcess.outputByStrain(1, 2, M.OUTPUTFORMAT_COMPANYTYPE);
		//���ÿһ���������ϵ���������������ʽ�������
//		ProProcess.outputTransactionType(M.TransactionType_Capital);
		
		//����ṹ���������Է�����txt
//		ProProcess.outputCentrality(2014);
		//����ṹ���Ľṹ��������txt(���ں������˹�������Դ�������ַ)
//		ProProcess.outputStructualHoles();
		//�����Ϊpartition������actor attribute���Ĺ�˾����2-ģ����
//		ProProcess.outputPartition(M.Classify_Industry, 2012);
		
		//������ѧ������ݷ�������
//		ZYY.ZYY();
		
	}
}
