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
		//第一个参数表示类型，第二个参数表示点的阈值，第三个参数表示线的权值，
		//第四个参数表示是否有向图，第五个参数表示网络性质，第六个参数表示网络是购销还是担保等网络
//		ProProcess.outputByType(M.MODETYPE_ONLYSELECTED_WEIGHTCSV, 1, 1
//				, true, M.Type_EquityOwnershipPrivate, M.Type_TransactionAll);
		//输出地区关联交易表
		//第一个参数表示点的阈值，第二个参数表示线的阈值（以亿为单位）
		//第三个参数表示交易的类型，第四个参数表示公司的类型
//		ProProcess.outputDistrictNet(M.MODEDISTRICT_NOINNER, 1, 1, M.Type_TransactionAll, M.Type_EquityOwnershipAll);
		
		//输出结构化的中心性分析的txt
//		ProProcess.outputCentrality("2011_建筑与房地产");
		//输出结构化的结构洞分析的txt(需在函数里人工调整来源和输出地址)
//		ProProcess.outputStructualHoles("2011_全数据");
		//输出作为partition（或者actor attribute）的公司类型2-模矩阵
//		ProProcess.outputPartition(M.Classify_ListedCompany);
		
		//删除正常的条目，留在有披露不充分的excel
//		ProProcess.onlyVague();
		
		//计算上市公司的平均中心度
//		ProProcess.calculate_ListCompany_TOP5_AverageDegree();//国有、民营TOP5子网络
//		ProProcess.calculate_ListCompany_ALL_AverageDegree();//2015年国有民营全部
		
		//计算国有、民营TOP5子网络涉及的产业数量
//		ProProcess.calculate_industryType();
		
		
		
		
//		WordFunction.test();
		
		//张悦悦学姐的数据分析需求
//		ZYY.ZYY();
		//绘制科学网博客的网络
//		SCBloger.SCBlorger();
		//商业分析课-专利分析
//		Patent.writeNet2(M.MODETYPE_ONLYSELECTED_WEIGHTCSV);
		
		//刘震-聚类
//		LZ.cluster();
		
		//smda的竞品网络
		//第一个参数是点的阈值，第二个参数是线的阈值
//		SMDANet.SMDANet2(10, 10);
//		SMDANet.SMDANet_targetShop("德克士", 10, 10);//目标商户竞争格局
		//插入数据库
//		SMDANet.SMDANet_toDB(10, 10);
		//输出食物列表
//		SMDAFood.getFoodList();
//		SMDAFood.getFoodWordsList();
//		SMDAFood.getFoodWordsNet(2000, 1000);
//		NLPIR.addUserDicFromTxt();//添加用户词表
		//食材-天气关系，插入数据库
//		SMDAFood.getFoodWordsWeatherBase();//各食材在各天气的销量销量分布
//		SMDAFood.getShopnameWeatherBase();//各商家在各天气的销量分布
//		SMDAFood.getUserWeatherBase2();//获取各用户在不同天气的分布
		//销量上升
//		SMDAFood.getWeatherIncreasefoods(20);//计算各种天气下销量top30上升的食材，并录入数据库
//		SMDAFood.getWeatherIncreaseUsers(5);//计算各种天气下各地区购买量上升的用户，并录入数据库(参数表示用户总的购买数量的阈值)
		//用户相关
//		SMDAFood.getUserLoyalty();//计算一个用户的忠诚度(目前只计算信息熵，信息熵越高，越不忠诚)
//		SMDAFood.getUserAvgComentropy_ALL(3);//计算整体每月的平均忠诚度(参数表示用户购买过商店的阈值)
//		SMDAFood.getUserAvgComentropy_ShopName("食其家", 3);//计算某家店每月的平均忠诚度(参数表示用户购买过商店的阈值)
		SMDAFood.getUserAvgComentropy_ByShopid(100);//计算每家店铺的平均忠诚度,参数表示这家商店的用户数量至少应该是多少
		
		//输出采集驴妈妈所要用的网页地址
//		TravelStream.outputLvMaMa();
//		TravelStream.calculate_users();
//		TravelStream.outputNet("beforeSame");//all,before,after,beforeSame
//		TravelStream.getDegree("beforeSame");//all,before,after,beforeSame
		//旅游流长度研究
//		TravelStream.outputUserStream("beforeSame");
//		TravelStream.getDisneyRank();//计算上海迪士尼在第几个景点
//		TravelStream.singDay("all");//计算单日高频旅游流（A-B）
//		TravelStream.topStream("all");//所有旅游流研究，求最大的旅游流出现在哪两个景点之间
		//旅游流其它计算
//		TravelStream.calculateHowManOne();//计算有多少个1（在核心边缘分析中，需要计算密度时使用）
//		TravelStream.printSpotName("36,24");//根据id列表，打印相应的景点名
		
		//SIE比赛
//		SIE.toDB();//将所需字段插入数据库
//		SIE.outputKeywordsNet(1, 10000, 5, 5);//输出网络,参数是开始时间、结束时间，然后是点的阈值，线的阈值
//		SIE.outputTopCitedArticle(200);//输出topN被引文献的篇名、被引次数、关键词
//		SIE.outputKeywordsByYears();//输出每年的关键词
//		SIE.calculateCitingCount();//统计参考文献数量
		
		//新关联交易研究，数据处理部分
//		QualifiedOption.outputConnectedTransaction("关联交易");
		
	}
}
