package com.main;

import java.io.IOException;
import java.sql.SQLException;

import com.data.ProProcess;
import com.data.SCBloger;
import com.db.WordFunction;
import com.myClass.M;
import com.myClass.U;

public class AssociatedCompany{
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException{

//		ProProcess.outputCompanyName();//ͳ��4��excel�г��ֵĹ�˾�����������������txt
//		ProProcess.outputCompanyType();//�����˾���͵�txts
//		ProProcess.outputCompanyAddress();//�����˾��ַ��txt
//		ProProcess.outputCompanyClassfiedType(M.Classify_EquityOwnership);//�������ҵ�����ʵȹ�˾���ͣ������ͬʱ���ڶ�����͵Ĺ�˾���Ա����˹����й���
//		ProProcess.outputCompanyClassfiedType_Year();//������������ҵ�����ʵȹ�˾����
		
		//�����˾��ϵ��
		//��һ��������ʾ�����ʽ���ڶ���������ʾ���й�˾����ֻ��A�����й�˾��������������ʾ��˾���ֵ���ֵ(>=)
//		ProProcess.outputCompanyAssociate(M.OUTPUTFORMAT_COMPANYTYPE, M.MODE_ALLCOMPANY, 2);
		//��������ͷֵĹ�˾��ϵ����һ��������ʾ������ͣ��ڶ�������Ϊ��ֵ
//		ProProcess.outputByClassification(M.OUTPUTFORMAT_NETWeight, 1);
		//�����ϵ��ֵĹ�˾��ϵ����һ������Ϊ��ֵ���ڶ�������Ϊ������˫���ͷ,������������ʾ�������
//		ProProcess.outputByStrain(M.OUTPUTFORMAT_COMPANYTYPE, 1);
		//���ÿһ�����ĳ�����ͣ��硰����-���󡱣�������
		//��һ��������ʾ���ͣ��ڶ���������ʾ��ֵ��������������ʾ�ߵ�Ȩֵ��
		//���ĸ�������ʾ�Ƿ�����ͼ�������������ʾ�������ʣ�������������ʾ�����ǹ������ǵ���������
//		ProProcess.outputByType(M.MODETYPE_ONLYSELECTED_WEIGHT, 1, 1000, true, M.Type_EquityOwnershipAll, M.Type_IndustryRealty);
		
		//����ṹ���������Է�����txt
//		ProProcess.outputCentrality("2011_�����뷿�ز�");
		//����ṹ���Ľṹ��������txt(���ں������˹�������Դ�������ַ)
//		ProProcess.outputStructualHoles("2011_ȫ����");
		//�����Ϊpartition������actor attribute���Ĺ�˾����2-ģ����
		ProProcess.outputPartition(M.Classify_ListedCompany);
		
//		WordFunction.test();
		
		//������ѧ������ݷ�������
//		ZYY.ZYY();
		//���ƿ�ѧ�����͵�����
//		SCBloger.SCBlorger();
		
	}
}
