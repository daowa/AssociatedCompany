package com.main;

import java.io.IOException;
import java.sql.SQLException;

import com.Others.LZ;
import com.Others.Patent;
import com.Others.SCBloger;
import com.Others.SMDAFood;
import com.Others.SMDANet;
import com.Others.ZYY;
import com.data.ProProcess;
import com.db.FileFunction;
import com.db.WordFunction;
import com.myClass.M;
import com.myClass.NLPIR;
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
		//��һ��������ʾ���ͣ��ڶ���������ʾ�����ֵ��������������ʾ�ߵ�Ȩֵ��
		//���ĸ�������ʾ�Ƿ�����ͼ�������������ʾ�������ʣ�������������ʾ�����ǹ������ǵ���������
//		ProProcess.outputByType(M.MODETYPE_ONLYSELECTED_WEIGHTCSV, 1, 1
//				, true, M.Type_EquityOwnershipPrivate, M.Type_TransactionAll);
		//��������������ױ�
		//��һ��������ʾ�����ֵ���ڶ���������ʾ�ߵ���ֵ������Ϊ��λ��
		//������������ʾ���׵����ͣ����ĸ�������ʾ��˾������
//		ProProcess.outputDistrictNet(M.MODEDISTRICT_NOINNER, 1, 1, M.Type_TransactionAll, M.Type_EquityOwnershipAll);
		
		//����ṹ���������Է�����txt
//		ProProcess.outputCentrality("2011_�����뷿�ز�");
		//����ṹ���Ľṹ��������txt(���ں������˹�������Դ�������ַ)
//		ProProcess.outputStructualHoles("2011_ȫ����");
		//�����Ϊpartition������actor attribute���Ĺ�˾����2-ģ����
//		ProProcess.outputPartition(M.Classify_ListedCompany);
		
		//ɾ����������Ŀ����������¶����ֵ�excel
//		ProProcess.onlyVague();
		
//		WordFunction.test();
		
		//������ѧ������ݷ�������
//		ZYY.ZYY();
		//���ƿ�ѧ�����͵�����
//		SCBloger.SCBlorger();
		//��ҵ������-ר������
//		Patent.writeNet2(M.MODETYPE_ONLYSELECTED_WEIGHTCSV);
		
		//����-����
//		LZ.cluster();
		
		//smda�ľ�Ʒ����
		//��һ�������ǵ����ֵ���ڶ����������ߵ���ֵ
//		SMDANet.SMDANet2(10, 10);
		//�������ݿ�
//		SMDANet.SMDANet_toDB(10, 10);
		//���ʳ���б�
//		SMDAFood.getFoodList();
//		SMDAFood.getFoodWordsList();
//		SMDAFood.getFoodWordsNet(2000, 1000);
//		NLPIR.addUserDicFromTxt();//����û��ʱ�
		//ʳ��-������ϵ���������ݿ�
		SMDAFood.getFoodWordsWeatherBase();//��ʳ���ڸ�����������
	}
}
