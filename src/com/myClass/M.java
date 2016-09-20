package com.myClass;

//存放key等常用的全局静态变量
public class M {

	//excel的下标
	public static int EXCELINDEX_StockSymbol = 1;
	public static int EXCELINDEX_CompanyName = 3;
	public static int EXCELINDEX_Address = 7;
	public static int EXCELINDEX_AssociatedCompany = 13;
	public static int EXCELINDEX_TransactoinType = 20;
	
	//输出关联公司时的输出格式
	public static int OUTPUTFORMAT_DL = 0;
	public static int OUTPUTFORMAT_NET = 1;//pajek所需的.net格式的文件，只包含点，以及点之间是否有连线
	public static int OUTPUTFORMAT_NETWeight = 2;//pajek所需的.net格式的文件，包含权重
	public static int OUTPUTFORMAT_COMPANYTYPE = 3;
	public static int OUTPUTFORMAT_ADDRESS = 4;
	public static int OUTPUTFORMAT_STARCOMPANY = 5;//只有一家公司标示为红色
	
	//模式
	public static int MODE_ALLCOMPANY = 10;
	public static int MODE_ONLYA = 11;
	
	//不同公司的类型
	public static int COMPANYTYPE_A = 100;
	public static int COMPANYTYPE_B = 101;//这里指的是上市的非A股公司
	public static int COMPANYTYPE_NOIPO = 102;//非上市公司
	
	//.net格式下每个节点所对应的颜色 的规则
	public static int COLOR_ADDRESS = 200;
	public static int COLOR_COMPANYTYPE = 201;
	public static int COLOR_STARCOMPANY = 202;
	
	//不同分类类型
	public static String Classify_EquityOwnership = "性质";
	public static String Classify_Industry = "行业";
	public static String Classify_TransactionType = "交易类型";
	
	//公司类型
	public static String TransactionType_Secured = "担保";
	public static String TransactionType_Purchase = "购销";
	public static String TransactionType_Capital = "资金往来";
}
