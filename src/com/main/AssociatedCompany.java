package com.main;

import java.io.IOException;

import com.data.ProProcess;
import com.myClass.M;
import com.myClass.U;

public class AssociatedCompany{
	
	public static void main(String[] args) throws IOException{

//		ProProcess.outputCompanyName();//统计4个excel中出现的公司（或人名），输出到txt
//		ProProcess.outputCompanyType();//输出公司类型到txt
//		ProProcess.outputCompanyAddress();//输出公司地址到txt
		
		//输出公司关系表
		//第一个参数表示输出格式，第二个参数表示是否是单向箭头，第三个参数表示公司出现的阈值
		ProProcess.outputCompanyAssociate(M.OUTPUTFORMAT_ADDRESS, M.MODE_ALLCOMPANY, false, 50);
		
	}
}
