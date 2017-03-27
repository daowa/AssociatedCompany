package com.main;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import com.Others.LZ;
import com.Others.Patent;
import com.Others.QualifiedOption;
import com.Others.SCBloger;
import com.Others.SIE;
import com.Others.SMDAFood;
import com.Others.SMDANet;
import com.Others.TravelStream;
import com.Others.ZYY;
import com.data.ProProcess;
import com.db.FileFunction;
import com.db.WordFunction;
import com.myClass.M;
import com.myClass.NLPIR;
import com.myClass.U;

public class AssociatedCompany{
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, ParserConfigurationException, SAXException, DocumentException{

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
		
		//�������й�˾��ƽ�����Ķ�
//		ProProcess.calculate_ListCompany_TOP5_AverageDegree();//���С���ӪTOP5������
//		ProProcess.calculate_ListCompany_ALL_AverageDegree();//2015�������Ӫȫ��
		
		//������С���ӪTOP5�������漰�Ĳ�ҵ����
//		ProProcess.calculate_industryType();
		
		
		
		
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
//		SMDANet.SMDANet_targetShop("�¿�ʿ", 10, 10);//Ŀ���̻��������
		//�������ݿ�
//		SMDANet.SMDANet_toDB(10, 10);
		//���ʳ���б�
//		SMDAFood.getFoodList();
//		SMDAFood.getFoodWordsList();
//		SMDAFood.getFoodWordsNet(2000, 1000);
//		NLPIR.addUserDicFromTxt();//����û��ʱ�
		//ʳ��-������ϵ���������ݿ�
//		SMDAFood.getFoodWordsWeatherBase();//��ʳ���ڸ����������������ֲ�
//		SMDAFood.getShopnameWeatherBase();//���̼��ڸ������������ֲ�
//		SMDAFood.getUserWeatherBase2();//��ȡ���û��ڲ�ͬ�����ķֲ�
		//��������
//		SMDAFood.getWeatherIncreasefoods(20);//�����������������top30������ʳ�ģ���¼�����ݿ�
//		SMDAFood.getWeatherIncreaseUsers(5);//������������¸������������������û�����¼�����ݿ�(������ʾ�û��ܵĹ�����������ֵ)
		//�û����
//		SMDAFood.getUserLoyalty();//����һ���û����ҳ϶�(Ŀǰֻ������Ϣ�أ���Ϣ��Խ�ߣ�Խ���ҳ�)
//		SMDAFood.getUserAvgComentropy_ALL(3);//��������ÿ�µ�ƽ���ҳ϶�(������ʾ�û�������̵����ֵ)
//		SMDAFood.getUserAvgComentropy_ShopName("ʳ���", 3);//����ĳ�ҵ�ÿ�µ�ƽ���ҳ϶�(������ʾ�û�������̵����ֵ)
		SMDAFood.getUserAvgComentropy_ByShopid(100);//����ÿ�ҵ��̵�ƽ���ҳ϶�,������ʾ����̵���û���������Ӧ���Ƕ���
		
		//����ɼ�¿������Ҫ�õ���ҳ��ַ
//		TravelStream.outputLvMaMa();
//		TravelStream.calculate_users();
//		TravelStream.outputNet("beforeSame");//all,before,after,beforeSame
//		TravelStream.getDegree("beforeSame");//all,before,after,beforeSame
		//�����������о�
//		TravelStream.outputUserStream("beforeSame");
//		TravelStream.getDisneyRank();//�����Ϻ���ʿ���ڵڼ�������
//		TravelStream.singDay("all");//���㵥�ո�Ƶ��������A-B��
//		TravelStream.topStream("all");//�����������о�������������������������������֮��
		//��������������
//		TravelStream.calculateHowManOne();//�����ж��ٸ�1���ں��ı�Ե�����У���Ҫ�����ܶ�ʱʹ�ã�
//		TravelStream.printSpotName("36,24");//����id�б���ӡ��Ӧ�ľ�����
		
		//SIE����
//		SIE.toDB();//�������ֶβ������ݿ�
//		SIE.outputKeywordsNet(1, 10000, 5, 5);//�������,�����ǿ�ʼʱ�䡢����ʱ�䣬Ȼ���ǵ����ֵ���ߵ���ֵ
//		SIE.outputTopCitedArticle(200);//���topN�������׵�ƪ���������������ؼ���
//		SIE.outputKeywordsByYears();//���ÿ��Ĺؼ���
//		SIE.calculateCitingCount();//ͳ�Ʋο���������
		
		//�¹��������о������ݴ�����
//		QualifiedOption.outputConnectedTransaction("��������");
		
	}
}
