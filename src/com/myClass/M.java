package com.myClass;

//���key�ȳ��õ�ȫ�־�̬����
public class M {

	//excel���±�
	public static int EXCELINDEX_StockSymbol = 1;
	public static int EXCELINDEX_CompanyName = 3;
	public static int EXCELINDEX_Industry = 5;//������ҵ
	public static int EXCELINDEX_Address = 7;
	public static int EXCELINDEX_EquityOwnership = 10;
	public static int EXCELINDEX_AssociatedCompany = 13;
	public static int EXCELINDEX_Amount = 19;//���׽��
	public static int EXCELINDEX_TransactoinType = 20;
	
	//���������˾ʱ�������ʽ
	public static int OUTPUTFORMAT_DL = 0;
	public static int OUTPUTFORMAT_NETSimple = 1;//pajek�����.net��ʽ���ļ���ֻ�����㣬�Լ���֮���Ƿ�������
	public static int OUTPUTFORMAT_NETWeight = 2;//pajek�����.net��ʽ���ļ�������Ȩ��
	public static int OUTPUTFORMAT_COMPANYTYPE = 3;
	public static int OUTPUTFORMAT_ADDRESS = 4;
	public static int OUTPUTFORMAT_STARCOMPANY = 5;//ֻ��һ�ҹ�˾��ʾΪ��ɫ
	
	//�������������ģʽ
	public static int MODE_ALLCOMPANY = 10;
	public static int MODE_ONLYA = 11;
	//����������������ģʽ
	public static int MODETYPE_ALL = 12;//������нڵ�
	public static int MODETYPE_ONLYSELECTED = 13;//��������������ͨ�����硰��Ӫ-������
	public static int MODETYPE_ALLSELECTED = 14;//ֻҪ����һ��������ͨ�����硰��Ӫ-��Ӫ��
	public static int MODETYPE_ONLYSELECTED_WEIGHT = 15;//����Ӫ-���������£�ͬʱ������Ȩֵ
	
	//Excel����
	public static int EXCEL_XLS = 0;
	public static int EXCEL_XLSX = 1;
	
	//��ͬ��˾������
	public static int COMPANYTYPE_A = 100;
	public static int COMPANYTYPE_B = 101;//����ָ�������еķ�A�ɹ�˾
	public static int COMPANYTYPE_NOIPO = 102;//�����й�˾
	
	//.net��ʽ��ÿ���ڵ�����Ӧ����ɫ �Ĺ���
	public static int COLOR_ADDRESS = 200;
	public static int COLOR_COMPANYTYPE = 201;
	public static int COLOR_STARCOMPANY = 202;
	
	//��ͬ��������
	public static String Classify_EquityOwnership = "��ҵ����";
	public static String Classify_Industry = "������ҵ";
	public static String Classify_TransactionType = "��������";
	public static String CLassify_Ownership_Ownership = "����_����";//0924Q1�����У�������-��Ӫ����ͬ�����partition
	public static String Classify_ListedCompany = "���й�˾";
	
	//��˾����
	public static String Type_EquityOwnershipAll = "��ҵ����_����";
	public static String Type_EquityOwnershipNation = "��ҵ����_����";
	public static String Type_EquityOwnershipPrivate = "��ҵ����_��Ӫ";
	public static String Type_EquityOwnershipForeign = "��ҵ����_����";
	public static String Type_EquityOwnershipYangQi = "����";
	//��˾����
	public static String Type_TransactionPurchase = "��������_����";
	public static String Type_TransactionGoodsPurchase = "��������_��Ʒ����";
	public static String Type_TransactionSecured = "��������_����";
	public static String Type_TransactionCapital = "��������_�ʽ�����";
	//������ҵ
	public static String Type_IndustryRealty = "��ҵ����_���ز�";
}
