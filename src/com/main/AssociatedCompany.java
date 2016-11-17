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

//		ProProcess.outputCompanyName();//统计4个excel中出现的公司（或人名），输出到txt
//		ProProcess.outputCompanyType();//输出公司类型到txts
//		ProProcess.outputCompanyAddress();//输出公司地址到txt
//		ProProcess.outputCompanyClassfiedType(M.Classify_EquityOwnership);//输出按行业、性质等公司类型，并输出同时属于多个类型的公司，以便于人工进行归类
//		ProProcess.outputCompanyClassfiedType_Year();//按年份输出按行业、性质等公司类型
		
		//输出公司关系表
		//第一个参数表示输出格式，第二个参数表示所有公司还是只有A股上市公司，第三个参数表示公司出现的阈值(>=)
//		ProProcess.outputCompanyAssociate(M.OUTPUTFORMAT_COMPANYTYPE, M.MODE_ALLCOMPANY, 2);
		//输出按类型分的公司关系表，第一个参数表示输出类型，第二个参数为阈值
//		ProProcess.outputByClassification(M.OUTPUTFORMAT_NETWeight, 1);
		//输出按系族分的公司关系表，第一个参数为阈值，第二个参数为单向还是双向箭头,第三个参数表示输出类型
//		ProProcess.outputByStrain(M.OUTPUTFORMAT_COMPANYTYPE, 1);
		//输出每一年根据某个类型（如“性质-国企”）的网络
		//第一个参数表示类型，第二个参数表示阈值，第三个参数表示线的权值，
		//第四个参数表示是否有向图，第五个参数表示网络性质，第六个参数表示网络是购销还是担保等网络
//		ProProcess.outputByType(M.MODETYPE_ONLYSELECTED_WEIGHT, 1, 1000, true, M.Type_EquityOwnershipAll, M.Type_IndustryRealty);
		
		//输出结构化的中心性分析的txt
//		ProProcess.outputCentrality("2011_建筑与房地产");
		//输出结构化的结构洞分析的txt(需在函数里人工调整来源和输出地址)
//		ProProcess.outputStructualHoles("2011_全数据");
		//输出作为partition（或者actor attribute）的公司类型2-模矩阵
		ProProcess.outputPartition(M.Classify_ListedCompany);
		
//		WordFunction.test();
		
		//张悦悦学姐的数据分析需求
//		ZYY.ZYY();
		//绘制科学网博客的网络
//		SCBloger.SCBlorger();
		
	}
}
