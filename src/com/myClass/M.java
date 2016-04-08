package com.myClass;

//存放key等常用的全局静态变量
public class M {

	//excel的下标
	public static int EXCELINDEX_StockSymbol = 1;
	public static int EXCELINDEX_CompanyName = 3;
	public static int EXCELINDEX_Address = 7;
	public static int EXCELINDEX_AssociatedCompany = 13;
	
	//输出关联公司时的输出格式
	public static int OUTPUTFORMAT_DL = 0;
	public static int OUTPUTFORMAT_NET = 1;
	public static int OUTPUTFORMAT_NETTXT = 2;
	public static int OUTPUTFORMAT_COMPANYTYPE = 3;
	public static int OUTPUTFORMAT_ADDRESS = 4;
	
	//模式
	public static int MODE_ALLCOMPANY = 10;
	public static int MODE_ONLYA = 11;
	
	//不同公司的类型
	public static int COMPANYTYPE_A = 100;
	public static int COMPANYTYPE_B = 101;//这里指的是上市的非A股公司
	public static int COMPANYTYPE_NOIPO = 102;//非上市公司
}
