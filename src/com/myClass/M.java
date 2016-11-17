package com.myClass;

//存放key等常用的全局静态变量
public class M {

	//excel的下标
	public static int EXCELINDEX_StockSymbol = 1;
	public static int EXCELINDEX_CompanyName = 3;
	public static int EXCELINDEX_Industry = 5;//所处行业
	public static int EXCELINDEX_Address = 7;
	public static int EXCELINDEX_EquityOwnership = 10;
	public static int EXCELINDEX_AssociatedCompany = 13;
	public static int EXCELINDEX_Amount = 19;//交易金额
	public static int EXCELINDEX_TransactoinType = 20;
	
	//输出关联公司时的输出格式
	public static int OUTPUTFORMAT_DL = 0;
	public static int OUTPUTFORMAT_NETSimple = 1;//pajek所需的.net格式的文件，只包含点，以及点之间是否有连线
	public static int OUTPUTFORMAT_NETWeight = 2;//pajek所需的.net格式的文件，包含权重
	public static int OUTPUTFORMAT_COMPANYTYPE = 3;
	public static int OUTPUTFORMAT_ADDRESS = 4;
	public static int OUTPUTFORMAT_STARCOMPANY = 5;//只有一家公司标示为红色
	
	//关联交易网络的模式
	public static int MODE_ALLCOMPANY = 10;
	public static int MODE_ONLYA = 11;
	//根据类型输出网络的模式
	public static int MODETYPE_ALL = 12;//输出所有节点
	public static int MODETYPE_ONLYSELECTED = 13;//满足所有条件才通过，如“国营-担保”
	public static int MODETYPE_ALLSELECTED = 14;//只要满足一个条件就通过，如“国营-民营”
	public static int MODETYPE_ONLYSELECTED_WEIGHT = 15;//“国营-担保”类下，同时还赋予权值
	
	//Excel类型
	public static int EXCEL_XLS = 0;
	public static int EXCEL_XLSX = 1;
	
	//不同公司的类型
	public static int COMPANYTYPE_A = 100;
	public static int COMPANYTYPE_B = 101;//这里指的是上市的非A股公司
	public static int COMPANYTYPE_NOIPO = 102;//非上市公司
	
	//.net格式下每个节点所对应的颜色 的规则
	public static int COLOR_ADDRESS = 200;
	public static int COLOR_COMPANYTYPE = 201;
	public static int COLOR_STARCOMPANY = 202;
	
	//不同分类类型
	public static String Classify_EquityOwnership = "企业性质";
	public static String Classify_Industry = "所处行业";
	public static String Classify_TransactionType = "交易类型";
	public static String CLassify_Ownership_Ownership = "性质_性质";//0924Q1问题中，“国有-民营”共同网络的partition
	public static String Classify_ListedCompany = "上市公司";
	
	//公司性质
	public static String Type_EquityOwnershipAll = "企业性质_所有";
	public static String Type_EquityOwnershipNation = "企业性质_国有";
	public static String Type_EquityOwnershipPrivate = "企业性质_民营";
	public static String Type_EquityOwnershipForeign = "企业性质_外资";
	public static String Type_EquityOwnershipYangQi = "央企";
	//公司类型
	public static String Type_TransactionPurchase = "交易类型_购销";
	public static String Type_TransactionGoodsPurchase = "交易类型_商品购销";
	public static String Type_TransactionSecured = "交易类型_担保";
	public static String Type_TransactionCapital = "交易类型_资金往来";
	//所处行业
	public static String Type_IndustryRealty = "行业类型_房地产";
}
