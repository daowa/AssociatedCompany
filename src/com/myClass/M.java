package com.myClass;

//���key�ȳ��õ�ȫ�־�̬����
public class M {

	//excel���±�
	public static int EXCELINDEX_StockSymbol = 1;
	public static int EXCELINDEX_CompanyName = 3;
	public static int EXCELINDEX_Address = 7;
	public static int EXCELINDEX_AssociatedCompany = 13;
	public static int EXCELINDEX_TransactoinType = 20;
	
	//���������˾ʱ�������ʽ
	public static int OUTPUTFORMAT_DL = 0;
	public static int OUTPUTFORMAT_NET = 1;
	public static int OUTPUTFORMAT_NETTXT = 2;
	public static int OUTPUTFORMAT_COMPANYTYPE = 3;
	public static int OUTPUTFORMAT_ADDRESS = 4;
	public static int OUTPUTFORMAT_STARCOMPANY = 5;//ֻ��һ�ҹ�˾��ʾΪ��ɫ
	
	//ģʽ
	public static int MODE_ALLCOMPANY = 10;
	public static int MODE_ONLYA = 11;
	
	//��ͬ��˾������
	public static int COMPANYTYPE_A = 100;
	public static int COMPANYTYPE_B = 101;//����ָ�������еķ�A�ɹ�˾
	public static int COMPANYTYPE_NOIPO = 102;//�����й�˾
	
	//��ͬ��������
	public static String Classify_EquityOwnership = "����";
	public static String Classify_Industry = "��ҵ";
	public static String Classify_TransactionType = "��������";
	
	//��˾����
	public static String TransactionType_Secured = "����";
	public static String TransactionType_Purchase = "����";
	public static String TransactionType_Capital = "�ʽ�����";
}
